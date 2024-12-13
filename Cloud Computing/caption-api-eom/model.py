import tensorflow as tf
import numpy as np
from tensorflow.keras.applications.densenet import preprocess_input

# Load pretrained models
def load_caption_model(model_path="model/eom_model.keras"):
    """Load the pre-trained caption model."""
    model = tf.keras.models.load_model(model_path)
    return model

def load_base_model():
    """Load base model for feature extraction."""
    base_model = tf.keras.applications.DenseNet201(weights="imagenet", include_top=False, pooling="avg")
    return base_model

# Feature extraction
def extract_feature(image_path, base_model, img_size=224):
    """Extract features from an image using the base model."""
    img = tf.keras.preprocessing.image.load_img(image_path, target_size=(img_size, img_size))
    img = tf.keras.preprocessing.image.img_to_array(img)
    img = preprocess_input(img)
    img = np.expand_dims(img, axis=0)
    feature = base_model.predict(img)
    return feature

# Predict caption
def predict_caption(model, vectorizer, feature, max_length, output_length=20):
    """Generate a caption for the image."""
    vocabulary = vectorizer.get_vocabulary()
    feature = tf.convert_to_tensor(feature)
    feature = tf.expand_dims(feature, axis=0)

    in_text = "<start>"
    for _ in range(output_length):
        # Vectorize the input sequence
        sequence = vectorizer([in_text])[0]
        sequence = tf.keras.preprocessing.sequence.pad_sequences([sequence], maxlen=max_length)

        # Predict the next word
        y_pred = model.predict((feature, sequence), verbose=0)
        max_idx = np.argmax(y_pred)

        word = vocabulary[max_idx]
        if word is None:
            break
        in_text += " " + word
        if word == "<end>":
            break

    # Clean up the generated text
    return in_text.replace("<start>", "").replace("<end>", "").strip()