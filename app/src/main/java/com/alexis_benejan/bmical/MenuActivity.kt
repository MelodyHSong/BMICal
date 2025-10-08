/*
☆
☆ Author: ☆ Alexis J. Benejan ☆
☆ Language: Kotlin
☆ File Name: MenuActivity.kt
☆ Date: October 6, 2025
☆ Description: The main launcher activity for Version 1.1.0, providing a menu for application features.
☆
*/

// Final Version: 1.1.0
// Date: October 6, 2025

package com.alexis_benejan.bmical

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alexis_benejan.bmical.databinding.ActivityMenuBinding
import com.airbnb.lottie.LottieAnimationView

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Launch BMI Calculator
        binding.bmiCalculatorButton.setOnClickListener {
            val intent = Intent(this, BmiCalculatorActivity::class.java)
            startActivity(intent)
        }

        // 2. Launch TDEE Calculator
        binding.tdeeCalculatorButton.setOnClickListener {
            val intent = Intent(this, TdeeCalculatorActivity::class.java)
            startActivity(intent)
        }

        // 3. Launch About Activity (New for v1.1.0)
        binding.aboutButton.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        // 4. Start Lottie Animation (Developer Request for polish)
        startBackgroundAnimation()
    }

    // Handles playing the Lottie background animation
    private fun startBackgroundAnimation() {
        val animationView: LottieAnimationView = binding.animationView
        // Note: Lottie file must exist in app/src/main/assets/ and be named background_logo_animation.json
        animationView.playAnimation()
    }
}
