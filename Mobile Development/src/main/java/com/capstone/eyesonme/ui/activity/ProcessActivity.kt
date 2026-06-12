package com.capstone.eyesonme.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.capstone.eyesonme.R
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.util.concurrent.TimeUnit
import android.Manifest
import android.content.pm.PackageManager
import com.google.firebase.storage.FirebaseStorage

class ProcessActivity : AppCompatActivity() {
    private lateinit var previewImageView: ImageView
    private lateinit var retakeImageButton: Button
    private lateinit var uploadButton: Button
    private lateinit var progressIndicator: LinearProgressIndicator
    private val firestore = FirebaseFirestore.getInstance()

    private var currentPhotoPath: String? = null
    private var currentImageUri: Uri? = null

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            processImage()
        } else {
            Toast.makeText(this, "Izin penyimpanan diperlukan", Toast.LENGTH_SHORT).show()
        }
    }

    // Panggil saat mencoba memproses gambar
    private fun checkStoragePermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                processImage()
            }
            else -> {
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process)

        previewImageView = findViewById(R.id.previewImageView)
        retakeImageButton = findViewById(R.id.retakeImageButton)
        uploadButton = findViewById(R.id.uploadButton)
        progressIndicator = findViewById(R.id.progressIndicator)

        // Cek sumber gambar (kamera atau galeri)
        val imagePath = intent.getStringExtra("IMAGE_PATH")
        val imageUri = intent.getStringExtra("IMAGE_URI")
        if (!imageUri.isNullOrEmpty()) {
            val uri = Uri.parse(imageUri)

            // Take persistent URI permissions
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            currentImageUri = uri
            Glide.with(this)
                .load(currentImageUri)
                .into(previewImageView)
        }

        when {
            // Gambar dari kamera
            !imagePath.isNullOrEmpty() -> {
                currentPhotoPath = imagePath
                val imageFile = File(imagePath)
                Glide.with(this)
                    .load(imageFile)
                    .into(previewImageView)
            }
            // Gambar dari galeri
            !imageUri.isNullOrEmpty() -> {
                currentImageUri = Uri.parse(imageUri)
                Glide.with(this)
                    .load(currentImageUri)
                    .into(previewImageView)
            }
            else -> {
                Toast.makeText(this, "Tidak ada gambar yang dipilih", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
        }

        retakeImageButton.setOnClickListener {
            // Kembali ke CameraActivity untuk mengambil gambar ulang
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            finish()
        }

        uploadButton.setOnClickListener {
            processImage()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun processImage() {
        val imageToProcess = when {
            currentPhotoPath != null -> File(currentPhotoPath!!)
            currentImageUri != null -> {
                try {
                    // Gunakan uriToFile untuk mengkonversi URI
                    uriToFile(currentImageUri!!)
                } catch (e: Exception) {
                    Log.e("ProcessActivity", "Error processing gallery image", e)
                    Toast.makeText(
                        this,
                        "Gagal memproses gambar dari galeri: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
            }
            else -> {
                Toast.makeText(this, "Tidak ada gambar yang dipilih", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Lanjutkan proses sepersebelumnya dengan imageToProcess
        uploadButton.isEnabled = false
        progressIndicator.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val images = generateTempImages()
                withContext(Dispatchers.Main) {
                    sendImageToAPI(images)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressIndicator.visibility = View.GONE
                    uploadButton.isEnabled = true
                    Toast.makeText(
                        this@ProcessActivity,
                        "Gagal memproses gambar: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        uploadToFirebase(imageToProcess)
    }

    private fun sendImageToAPI(images: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://eom-imagecapt-api-349085248843.asia-southeast2.run.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            .build()

        val apiService = retrofit.create(ImageCaptionApiService::class.java)

        // Buat MultipartBody.Part untuk gambar
        val file = if (currentPhotoPath != null) {
            File(currentPhotoPath!!)
        } else {
            // Konversi URI dari galeri menjadi file
            uriToFile(currentImageUri!!)
        }

        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestBody)

        apiService.generateCaption(imagePart).enqueue(object : Callback<ImageCaptionResponse> {
            override fun onResponse(
                call: Call<ImageCaptionResponse>,
                response: Response<ImageCaptionResponse>
            ) {
                progressIndicator.visibility = View.GONE
                uploadButton.isEnabled = true

                if (response.isSuccessful) {
                    val caption = response.body()?.caption ?: "Caption tidak tersedia"

                    // Simpan data ke Firestore dengan minimal informasi
                    val imageData = hashMapOf(
                        "images" to images,
                        "caption" to caption
                    )

                    firestore.collection("images")
                        .document(images)
                        .set(imageData)
                        .addOnSuccessListener {
                            val intent = Intent(this@ProcessActivity, DetailActivity::class.java).apply {
                                putExtra("IMAGE_PATH", currentPhotoPath ?: currentImageUri.toString())
                                putExtra("IMAGE_ID", images)
                            }
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@ProcessActivity,
                                "Gagal menyimpan data: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(
                        this@ProcessActivity,
                        "Gagal mendapatkan caption: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ImageCaptionResponse>, t: Throwable) {
                progressIndicator.visibility = View.GONE
                uploadButton.isEnabled = true

                Toast.makeText(
                    this@ProcessActivity,
                    "Kesalahan jaringan: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // Fungsi tambahan untuk mengonversi URI galeri menjadi File
    private fun uriToFile(uri: Uri): File {
        try {
            // Ambil input stream dari URI
            val inputStream = contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("Cannot open input stream")

            // Buat file sementara di cache directory
            val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir)
            tempFile.deleteOnExit()

            // Salin input stream ke file sementara
            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            Log.d("ProcessActivity", "File created at: ${tempFile.absolutePath}")
            return tempFile
        } catch (e: Exception) {
            Log.e("ProcessActivity", "Error converting URI to File", e)
            throw RuntimeException("Failed to convert URI to file: ${e.message}")
        }
    }
    private fun uploadToFirebase(file: File) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileUri = Uri.fromFile(file)
        val imageRef = storageRef.child("images/${file.name}")

        val uploadTask = imageRef.putFile(fileUri)

        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "Processed Succesfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Log.e("ProcessActivity", "Failed Processed", e)
            Toast.makeText(this, "Failed Processed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



    // Tambahan: Metode untuk mendapatkan path file dari URI
    private fun getPathFromUri(uri: Uri): String? {
        try {
            // Coba metode standar dulu
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                return cursor.getString(columnIndex)
            }

            // Jika gagal, gunakan metode alternatif
            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir)
                tempFile.deleteOnExit()

                inputStream.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                return tempFile.absolutePath
            }
        } catch (e: Exception) {
            Log.e("ProcessActivity", "Error getting path from URI", e)
        }
        return null
    }

    // API Service interface
    interface ImageCaptionApiService {
        @Multipart
        @POST("generate-caption")
        fun generateCaption(
            @Part image: MultipartBody.Part
        ): Call<ImageCaptionResponse>
    }

    // Response data class
    data class ImageCaptionResponse(val caption: String)

    private fun generateTempImages(): String {
        return "temp_${System.currentTimeMillis()}"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}