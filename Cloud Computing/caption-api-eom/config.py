import os
from google.cloud import firestore

# Initialize Firestore client
def get_firestore_client():
    """Returns a Firestore client."""
    # Ensure GOOGLE_APPLICATION_CREDENTIALS is set to the path of your service account key JSON file
    return firestore.Client()