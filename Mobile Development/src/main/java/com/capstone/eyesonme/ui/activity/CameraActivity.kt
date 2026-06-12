package com.capstone.eyesonme.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.capstone.eyesonme.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var captureButton: FloatingActionButton
    private lateinit var flashButton: ImageView
    private lateinit var galleryButton: ImageView
    private lateinit var imageCapture: ImageCapture

    private var currentFlashMode = ImageCapture.FLASH_MODE_AUTO
    private var currentPhotoPath: String? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // Updated gallery launcher using Photo Picker
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            // Take persistent URI permissions
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            // Navigate to ProcessActivity with selected gallery image
            val intent = Intent(this, ProcessActivity::class.java).apply {
                putExtra("IMAGE_URI", uri.toString())
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
            finish()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        previewView = findViewById(R.id.previewView)
        captureButton = findViewById(R.id.captureButton)
        flashButton = findViewById(R.id.flashButton)
        galleryButton = findViewById(R.id.galleryButton)

        // Disable capture button initially
        captureButton.isEnabled = false

        // Check camera permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        // Setup capture button
        captureButton.setOnClickListener {
            captureImage()
        }

        // Setup flash button
        flashButton.setOnClickListener {
            cycleFlashMode()
        }

        // Setup gallery button
        galleryButton.setOnClickListener {
            openGallery()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            // ImageCapture
            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_DEFAULT)
                .setFlashMode(currentFlashMode)
                .build()

            // Select back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before binding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                // Enable capture button after camera is ready
                captureButton.isEnabled = true

            } catch(exc: Exception) {
                Log.e("CameraActivity", "Failed to start camera", exc)
                Toast.makeText(
                    this,
                    "Failed to start camera: ${exc.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureImage() {
        // Create file to save photo
        val photoFile = createImageFile()

        // Configure output
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Take picture
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        baseContext,
                        "Failed to take photo",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // Navigate to ProcessActivity with captured image
                    val intent = Intent(this@CameraActivity, ProcessActivity::class.java).apply {
                        putExtra("IMAGE_PATH", currentPhotoPath)
                    }
                    startActivity(intent)
                    finish()
                }
            }
        )
    }

    private fun cycleFlashMode() {
        currentFlashMode = when (currentFlashMode) {
            ImageCapture.FLASH_MODE_AUTO -> {
                flashButton.setImageResource(R.drawable.ic_flash_on)
                ImageCapture.FLASH_MODE_ON
            }
            ImageCapture.FLASH_MODE_ON -> {
                flashButton.setImageResource(R.drawable.ic_flash_off)
                ImageCapture.FLASH_MODE_OFF
            }
            else -> {
                flashButton.setImageResource(R.drawable.ic_flash_auto)
                ImageCapture.FLASH_MODE_AUTO
            }
        }

        // Restart camera with new flash mode
        startCamera()
    }

    private fun openGallery() {
        // Use the new Photo Picker API
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun createImageFile(): File {
        // Create unique filename
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(null)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Save file path
            currentPhotoPath = absolutePath
        }
    }
}