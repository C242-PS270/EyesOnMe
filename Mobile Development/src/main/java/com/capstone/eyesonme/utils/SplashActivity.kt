package com.capstone.eyesonme.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.eyesonme.R
import com.capstone.eyesonme.ui.activity.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // Set window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find views for animation
        val logo = findViewById<View>(R.id.textHello)
        val logoContainer = findViewById<View>(R.id.logo_container)
        val divider = findViewById<View>(R.id.divider)
        val titleNavHome = findViewById<View>(R.id.title_navigation_home)
        val appName = findViewById<View>(R.id.app_name)

        // Initial setup for animation
        setupInitialState(logo, logoContainer, divider, titleNavHome, appName)
    }

    private fun setupInitialState(
        logo: View,
        logoContainer: View,
        divider: View,
        titleNavHome: View,
        appName: View
    ) {
        // Set initial visibility and transform
        logo.alpha = 0f
        logoContainer.translationY = 100f
        divider.alpha = 0f
        titleNavHome.alpha = 0f
        titleNavHome.translationX = 100f
        appName.alpha = 0f
        appName.translationY = 50f

        // Trigger animations after layout is ready
        logo.post {
            animateSplashScreen(logo, logoContainer, divider, titleNavHome, appName)
        }
    }

    private fun animateSplashScreen(
        logo: View,
        logoContainer: View,
        divider: View,
        titleNavHome: View,
        appName: View
    ) {
        // Logo container animation (scale and translate)
        val logoContainerScaleX = ObjectAnimator.ofFloat(logoContainer, View.SCALE_X, 0.5f, 1f)
        val logoContainerScaleY = ObjectAnimator.ofFloat(logoContainer, View.SCALE_Y, 0.5f, 1f)
        val logoContainerTranslateY = ObjectAnimator.ofFloat(logoContainer, View.TRANSLATION_Y, 100f, 0f)

        // Logo appearance animation
        val logoAlpha = ObjectAnimator.ofFloat(logo, View.ALPHA, 0f, 1f)

        // App name appearance
        val appNameAlpha = ObjectAnimator.ofFloat(appName, View.ALPHA, 0f, 1f)
        val appNameTranslateY = ObjectAnimator.ofFloat(appName, View.TRANSLATION_Y, 50f, 0f)

        // Divider animation
        val dividerAlpha = ObjectAnimator.ofFloat(divider, View.ALPHA, 0f, 1f)

        // Title animation
        val titleAlpha = ObjectAnimator.ofFloat(titleNavHome, View.ALPHA, 0f, 1f)
        val titleTranslateX = ObjectAnimator.ofFloat(titleNavHome, View.TRANSLATION_X, 100f, 0f)

        // Animator Set
        AnimatorSet().apply {
            playTogether(
                logoContainerScaleX,
                logoContainerScaleY,
                logoContainerTranslateY,
                logoAlpha,
                appNameAlpha,
                appNameTranslateY,
                dividerAlpha,
                titleAlpha,
                titleTranslateX
            )
            duration = 1200 // Increased from 1000 to 1200 milliseconds (1.2 seconds)
            interpolator = AnticipateOvershootInterpolator(1.2f)
            startDelay = 500 // Increased from 300 to 500 milliseconds

            // Navigate to main activity after animation
            addListener(onEnd = {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            })
            start()
        }
    }
}