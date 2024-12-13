import re
import pandas as pd
import tensorflow as tf

# Text preprocessing
def text_preprocessing(data):
    """Preprocess captions text."""
    data['caption'] = data['caption'].astype(str)
    data['caption'] = data['caption'].apply(lambda x: x.lower())
    data['caption'] = data['caption'].apply(lambda x: re.sub(r"[^A-Za-z\s]", "", x))
    data['caption'] = data['caption'].apply(lambda x: re.sub(r"\s+", " ", x))
    data['caption'] = data['caption'].apply(lambda x: " ".join([word for word in x.split() if len(word) > 1]))
    data['caption'] = "<start> " + data['caption'] + " <end>"
    return data

# Initialize and configure vectorizer
def initialize_vectorizer(captions, max_length):
    """Create and adapt the TextVectorization layer."""
    vectorizer = tf.keras.layers.TextVectorization(
        standardize=None,
        max_tokens=None,
        output_mode='int',
        output_sequence_length=None
    )
    vectorizer.adapt(captions)
    return vectorizer