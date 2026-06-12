package com.capstone.eyesonme.ui.activity

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.capstone.eyesonme.data.ImageDataSource
import com.capstone.eyesonme.data.ImageDatabase
import com.capstone.eyesonme.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var titleView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var listenButton: ExtendedFloatingActionButton
    private lateinit var favoriteButton: FloatingActionButton
    private lateinit var textToSpeech: TextToSpeech
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var imageDataSource: ImageDataSource

    private var isFavorite = false
    private var currentImages: String? = null
    private var currentImagePath: String? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val database = ImageDatabase.getDatabase(this)
        imageDataSource = ImageDataSource(this, database)

        // Initialize views
        imageView = findViewById(R.id.iv_item_photo_detail)
        titleView = findViewById(R.id.tv_story_title)
        descriptionView = findViewById(R.id.tv_story_description)
        progressBar = findViewById(R.id.progressBar)
        listenButton = findViewById(R.id.fab_listen)
        favoriteButton = findViewById(R.id.fab_favorite)

        // Get image ID and path from intent
        currentImages = intent.getStringExtra("IMAGE_ID")
        currentImagePath = intent.getStringExtra("IMAGE_PATH")

        // Load image details
        loadImageDetails()

        // Setup Text-to-Speech
        setupTextToSpeech()

        // Setup favorite button
        setupFavoriteButton()
    }

    private fun loadImageDetails() {
        progressBar.visibility = View.VISIBLE

        coroutineScope.launch {
            // Prioritaskan data dari intent
            val images = currentImages
            val imageUrl = intent.getStringExtra("IMAGE_URL")
            // Try to load image from path (camera-captured image)
            if (!currentImagePath.isNullOrEmpty()) {
                // Ambil data dari Firestore
                firestore.collection("images")
                    .document(images!!)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val url = document.getString("urlBucket") ?: imageUrl
                // Load image from file path
                Glide.with(this@DetailActivity)
                    .load(File(currentImagePath!!))
                    .placeholder(R.drawable.img_placeholder)
                    .into(imageView)

                            // Cek apakah caption sudah ada
                            val caption = document.getString("caption")
                            if (caption != null) {
                                titleView.text = "Image Caption"
                                descriptionView.text = caption
                            } else {
                                // Jika belum ada caption, trigger proses captioning
                                triggerCaptioning(images, url)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@DetailActivity, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

            }
            // If no path, try to load from database using ID
            else if (!currentImages.isNullOrEmpty()) {
                val image = imageDataSource.getImageById(currentImages!!)
                image?.let {
                    // Load image using Glide
                    Glide.with(this@DetailActivity)
                        .load(it.imageUrl)
                        .placeholder(R.drawable.img_placeholder)
                        .into(imageView)

                    // Set title and description from the image data
                    titleView.text = it.title
                    descriptionView.text = it.description

                    // Update favorite status
                    isFavorite = it.isFavorite
                    updateFavoriteIcon()
                } ?: run {
                    // Handle case where image is not found
                    titleView.text = "Image Not Found"
                    descriptionView.text = "Unable to load image details"
                }
            } else {
                // No image source provided
                titleView.text = "No Image"
                descriptionView.text = "No image to display"
            }

            progressBar.visibility = View.GONE
        }
    }

    private fun triggerCaptioning(images: String, imageUrl: String?) {
        // Contoh: Anda bisa menggunakan Cloud Functions atau layanan ML untuk captioning
        // Di sini adalah contoh sederhana
        descriptionView.text = "Memproses caption..."

        // Misalkan Anda memiliki endpoint untuk captioning
        // Ini hanya contoh, Anda perlu mengimplementasikan logika ML sebenarnya
        val captionData = hashMapOf(
            "images" to images,
            "imageUrl" to imageUrl,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("caption_queue")
            .add(captionData)
            .addOnSuccessListener {
                // Tunggu response dari ML service
                listenForCaption(images)
            }
    }

    private fun listenForCaption(images: String) {
        firestore.collection("images")
            .document(images)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val caption = snapshot.getString("caption")
                    if (!caption.isNullOrEmpty()) {
                        descriptionView.text = caption
                    }
                }
            }
    }

    private fun setupTextToSpeech() {
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.ENGLISH
                listenButton.setOnClickListener {
                    textToSpeech.speak(
                        descriptionView.text.toString(),
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        ""
                    )
                }
            }
        }
    }

    private fun setupFavoriteButton() {
        favoriteButton.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteIcon()

            // Update favorite status in database
            currentImages?.let { id ->
                lifecycleScope.launch {
                    imageDataSource.updateFavoriteStatus(id, isFavorite)
                }
            }
        }
    }

    private fun updateFavoriteIcon() {
        val iconRes = if (isFavorite)
            R.drawable.ic_favorited
        else
            R.drawable.ic_favorite
        favoriteButton.setImageResource(iconRes)
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.shutdown()
        coroutineScope.cancel()
    }
}