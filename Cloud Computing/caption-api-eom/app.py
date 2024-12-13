from flask import Flask, request, jsonify
import os
from google.cloud import storage, firestore
from datetime import datetime
from model import load_caption_model, load_base_model, extract_feature, predict_caption
from utils import text_preprocessing, initialize_vectorizer
import pandas as pd

app = Flask(__name__)

# GCP Configuration
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "eyes-on-me-firebase-adminsdk-cbq7d-63af46081c.json"  # Path ke service account key
BUCKET_NAME = "eyes-on-me.firebasestorage.app"  # Ganti dengan nama bucket Anda

# Initialize Firestore and Storage Clients
storage_client = storage.Client()
firestore_client = firestore.Client()

# Load models and preprocessors
CAPTION_MODEL_PATH = "model/eom_model.keras"
BASE_MODEL = load_base_model()
CAPTION_MODEL = load_caption_model(CAPTION_MODEL_PATH)

# Preprocess captions to initialize vectorizer
captions = pd.read_csv("captions.txt")
captions = text_preprocessing(captions)
MAX_LENGTH = max(len(caption.split()) for caption in captions['caption'].tolist())
VECTORIZER = initialize_vectorizer(captions['caption'].tolist(), MAX_LENGTH)

def upload_to_bucket(file_path, bucket_name):
    """Upload file to Google Cloud Storage bucket."""
    bucket = storage_client.bucket(bucket_name)
    blob = bucket.blob(f"uploads/{os.path.basename(file_path)}")
    blob.upload_from_filename(file_path)
    blob.make_public()  # Make file publicly accessible
    return blob.public_url

@app.route('/generate-caption', methods=['POST'])
def generate_caption():
    """API endpoint to generate a caption for an image."""
    if 'image' not in request.files:
        return jsonify({"error": "No image provided"}), 400

    image = request.files['image']
    image_path = os.path.join("uploads", image.filename)
    image.save(image_path)

    try:
        # Extract features and predict caption
        features = extract_feature(image_path, BASE_MODEL)
        caption = predict_caption(CAPTION_MODEL, VECTORIZER, features, MAX_LENGTH)

        # Upload image to Cloud Storage
        public_url = upload_to_bucket(image_path, BUCKET_NAME)

        # Save metadata to Firestore with timestamp
        doc_ref = firestore_client.collection("images").document()
        doc_ref.set({
            "filename": image.filename,
            "caption": caption,
            "url": public_url,
            "timestamp": datetime.utcnow().isoformat()  # Menyimpan waktu dalam format ISO 8601
        })

        return jsonify({
            "image": image.filename,
            "caption": caption,
            "url": public_url,
            "timestamp": datetime.utcnow().isoformat()
        })
    finally:
        os.remove(image_path)  # Clean up the uploaded image

if __name__ == '__main__':
    app.run()
