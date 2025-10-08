/*
☆
☆ Author: ☆ Alexis J. Benejan ☆
☆ Language: Kotlin
☆ Student ID: 843-12-0525
☆ File Name: TdeeCalculatorActivity.kt
☆ Date: October 6, 2025
☆ Description: Logic for the Total Daily Energy Expenditure (TDEE) Calculator.
☆ Implements the Mifflin-St Jeor formula for BMR calculation and applies an activity factor.
☆
*/

package com.alexis_benejan.bmical

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alexis_benejan.bmical.databinding.ActivityTdeeBinding
import kotlin.math.roundToInt

class TdeeCalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTdeeBinding

    // ☆ UNIT STATE: false = Metric (default), true = English ☆
    private var isEnglishUnit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityTdeeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the activity title
        title = getString(R.string.menu_tdee_calculator)

        // ☆ INITIAL SETUP ☆
        // Order is critical: Spinners must be set up before unit system updates labels
        setupSpinners()
        updateUnitSystem(isEnglishUnit)

        // ☆ BUTTON LISTENER ☆
        binding.tdeeCalculateButton.setOnClickListener {
            calculateTdee()
        }

        // ☆ UNIT SWITCH LISTENER ☆
        binding.unitSwitch.setOnCheckedChangeListener { _, isChecked ->
            isEnglishUnit = isChecked
            updateUnitSystem(isEnglishUnit)
        }
    }

    // ☆ SETUP GENDER AND ACTIVITY LEVEL SPINNERS ☆
    private fun setupSpinners() {
        // 1. Gender Spinner (Uses same strings as BMI calculator)
        val genders = resources.getStringArray(R.array.gender_options)
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genders)
        binding.genderInputText.setAdapter(genderAdapter)
        binding.genderInputText.setText(genders[0], false) // Default to Male

        // 2. Activity Level Spinner
        val activityLevels = resources.getStringArray(R.array.activity_levels)
        val activityAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, activityLevels)
        binding.activityInputText.setAdapter(activityAdapter)
        binding.activityInputText.setText(activityLevels[0], false) // Default to Sedentary
    }

    // ☆ UPDATE UI BASED ON UNIT SYSTEM (15 ptos.) ☆
    private fun updateUnitSystem(isEnglish: Boolean) {
        // Clear inputs and hide results card on unit change
        binding.ageInputText.setText("")
        binding.heightInputText.setText("")
        binding.inchesInputText.setText("")
        binding.weightInputText.setText("")
        binding.tdeeResultCard.visibility = View.GONE

        if (isEnglish) {
            // ☆ ENGLISH SYSTEM (lbs, feet/in) ☆
            binding.inchesInputLayout.visibility = View.VISIBLE
            binding.heightInputLayout.hint = getString(R.string.label_height_english)
            binding.heightInputLayout.suffixText = getString(R.string.unit_height_english_feet)
            binding.inchesInputLayout.hint = getString(R.string.label_inches)
            binding.inchesInputLayout.suffixText = getString(R.string.unit_height_english_inches)
            binding.weightInputLayout.hint = getString(R.string.label_weight_english)
            binding.weightInputLayout.suffixText = getString(R.string.unit_weight_english) // 'lbs'
            binding.unitLabel.text = getString(R.string.label_unit_english)
        } else {
            // ☆ METRIC SYSTEM (kg, cm) ☆
            binding.inchesInputLayout.visibility = View.GONE
            binding.heightInputLayout.hint = getString(R.string.label_height_metric)
            binding.heightInputLayout.suffixText = getString(R.string.unit_height_metric) // 'cm'
            binding.weightInputLayout.hint = getString(R.string.label_weight_metric)
            binding.weightInputLayout.suffixText = getString(R.string.unit_weight_metric) // 'kg'
            binding.unitLabel.text = getString(R.string.label_unit_metric)
        }
    }

    // ☆ CORE TDEE CALCULATION LOGIC ☆
    private fun calculateTdee() {
        // Hide keyboard
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(binding.tdeeCalculateButton.windowToken, 0)

        // 1. INPUT VALIDATION AND EXTRACTION
        val ageStr = binding.ageInputText.text.toString()
        val heightStr = binding.heightInputText.text.toString()
        val weightStr = binding.weightInputText.text.toString()
        val inchesStr = binding.inchesInputText.text.toString()
        val genderStr = binding.genderInputText.text.toString()
        val activityLevelStr = binding.activityInputText.text.toString()

        if (ageStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty() || genderStr.isEmpty() || activityLevelStr.isEmpty() || (isEnglishUnit && inchesStr.isEmpty())) {
            Toast.makeText(this, getString(R.string.error_input_required), Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val age = ageStr.toDouble()
            val weightInput = weightStr.toDouble()
            val heightInput = heightStr.toDouble()

            // 2. CONVERT ALL INPUTS TO METRIC (KG and CM) for BMR Formula
            val weightKg: Double
            val heightCm: Double
            val activityFactor = getActivityFactor(activityLevelStr)

            if (isEnglishUnit) {
                // Convert Lbs to Kg
                weightKg = weightInput * 0.453592

                // Convert Feet + Inches to total cm
                val inches = inchesStr.toDouble()
                val totalInches = (heightInput * 12) + inches
                heightCm = totalInches * 2.54
            } else {
                // Metric inputs are already in Kg and Cm
                weightKg = weightInput
                heightCm = heightInput
            }

            // 3. CALCULATE BMR (Basal Metabolic Rate) - using Mifflin-St Jeor Equation
            // BMR = 10*weight(kg) + 6.25*height(cm) - 5*age(y) + S

            val genderAdjustment = if (genderStr == resources.getStringArray(R.array.gender_options)[0]) {
                // Male: +5
                5.0
            } else {
                // Female: -161
                -161.0
            }

            val bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * age) + genderAdjustment

            // 4. CALCULATE TDEE
            val tdee = bmr * activityFactor

            // 5. UPDATE UI
            updateResultDisplay(bmr.roundToInt(), tdee.roundToInt())

        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_invalid_value), Toast.LENGTH_SHORT).show()
        }
    }

    // ☆ GET ACTIVITY FACTOR FOR TDEE CALCULATION ☆
    private fun getActivityFactor(activityLevelStr: String): Double {
        // Extracts the number prefix (e.g., '1.' from '1. Sedentary...')
        val levelIndex = activityLevelStr.substringBefore(".").toIntOrNull() ?: 1

        return when (levelIndex) {
            1 -> 1.2 // Sedentary
            2 -> 1.375 // Lightly Active
            3 -> 1.55 // Moderately Active
            4 -> 1.725 // Very Active
            5 -> 1.9 // Extremely Active
            else -> 1.2
        }
    }

    // ☆ DISPLAY BMR/TDEE RESULTS ☆
    private fun updateResultDisplay(bmr: Int, tdee: Int) {
        // Format BMR result
        val bmrMsg = getString(R.string.tdee_result_bmr, bmr.toString())
        binding.bmrResultText.text = bmrMsg

        // Format TDEE result
        val tdeeMsg = getString(R.string.tdee_result_tdee, tdee.toString())
        binding.tdeeResultText.text = tdeeMsg

        // Show the result card
        binding.tdeeResultCard.visibility = View.VISIBLE
    }
}