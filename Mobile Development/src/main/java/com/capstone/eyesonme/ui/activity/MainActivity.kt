package com.capstone.eyesonme.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.capstone.eyesonme.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var cameraFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        setContentView(R.layout.activity_main)

        // Find NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup Bottom Navigation
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.setupWithNavController(navController)

        // Setup Camera FAB
        cameraFab = findViewById(R.id.fab_camera)

        cameraFab.setOnClickListener {
            // Langsung buka CameraActivity
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }
}