/*
☆
☆ Author: ☆ Alexis J. Benejan ☆
☆ Language: Kotlin
☆ Student ID: 843-12-0525
☆ File Name: BmiCalculatorActivity.kt
☆ Date: October 6, 2025
☆ Description: Contains the core logic for the BMI Calculator (refactored from MainActivity).
☆ It handles unit conversion (Metric/English/Feet/Inches), input validation, BMI classification,
☆ and display of the result, including the normal weight range.
☆
*/

package com.alexis_benejan.bmical

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.alexis_benejan.bmical.databinding.ActivityMainBinding
import kotlin.math.pow
import kotlin.math.roundToInt

// Data class to hold the BMI classification result.
data class BmiClassificationResult(
    val status: String,      // e.g., "Normal Weight"
    val colorResId: Int,     // e.g., R.color.colorNormal
    val minWeight: Double,   // lower bound of normal weight range (BMI)
    val maxWeight: Double    // upper bound of normal weight range (BMI)
)

class BmiCalculatorActivity : AppCompatActivity() {

    // ☆ VIEW BINDING (3 ptos.) ☆
    private lateinit var binding: ActivityMainBinding

    // ☆ UNIT STATE: false = Metric (default), true = English ☆
    private var isEnglishUnit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout using View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the title for this specific activity
        title = getString(R.string.menu_bmi_calculator)

        // ☆ INITIAL SETUP ☆
        setupGenderSpinner()
        updateUnitSystem(isEnglishUnit) // Set initial state to Metric (isEnglishUnit=false)

        // ☆ CALCULATE BUTTON LISTENER ☆
        binding.calculateButton.setOnClickListener {
            calculateBmi()
        }

        // ☆ UNIT SWITCH LISTENER (15 ptos. for unit support) ☆
        binding.unitSwitch.setOnCheckedChangeListener { _, isChecked ->
            isEnglishUnit = isChecked
            updateUnitSystem(isEnglishUnit)
        }
    }

    // ☆ SETUP GENDER SPINNER ☆
    private fun setupGenderSpinner() {
        // Get the string array defined in resources (localized)
        val genders = resources.getStringArray(R.array.gender_options)

        // Create an adapter for the dropdown
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genders)

        // Set the adapter to the AutoCompleteTextView
        binding.genderInputText.setAdapter(adapter)

        // Set default selection (Male)
        binding.genderInputText.setText(genders[0], false)
    }


    // ☆ UPDATE UI BASED ON UNIT SYSTEM (15 ptos.) ☆
    // Toggles visibility of fields and updates labels/suffix texts
    private fun updateUnitSystem(isEnglish: Boolean) {
        // Clear previous input when switching units
        binding.heightInputText.setText("")
        binding.inchesInputText.setText("")
        binding.weightInputText.setText("")
        binding.resultCard.visibility = View.GONE

        if (isEnglish) {
            // ☆ ENGLISH SYSTEM (lbs, feet/in) ☆
            // Show Inches input for split height entry
            binding.inchesInputLayout.visibility = View.VISIBLE
            binding.heightInputLayout.hint = getString(R.string.label_height_english) // Feet label
            binding.heightInputLayout.suffixText = getString(R.string.unit_height_english_feet) // 'ft'
            binding.inchesInputLayout.hint = getString(R.string.label_inches) // Inches label
            binding.inchesInputLayout.suffixText = getString(R.string.unit_height_english_inches) // 'in'
            binding.weightInputLayout.hint = getString(R.string.label_weight_english)
            binding.weightInputLayout.suffixText = getString(R.string.unit_weight_english) // 'lbs'
            binding.unitLabel.text = getString(R.string.label_unit_english)
        } else {
            // ☆ METRIC SYSTEM (kg, cm) ☆
            // Hide Inches input (only one height field needed)
            binding.inchesInputLayout.visibility = View.GONE
            binding.heightInputLayout.hint = getString(R.string.label_height_metric) // Height (cm)
            binding.heightInputLayout.suffixText = getString(R.string.unit_height_metric) // 'cm'
            binding.weightInputLayout.hint = getString(R.string.label_weight_metric)
            binding.weightInputLayout.suffixText = getString(R.string.unit_weight_metric) // 'kg'
            binding.unitLabel.text = getString(R.string.label_unit_metric)
        }
    }

    // ☆ CORE BMI CALCULATION LOGIC (30 ptos.) ☆
    private fun calculateBmi() {
        // Hide keyboard when calculation starts
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(binding.calculateButton.windowToken, 0)

        // 1. INPUT VALIDATION
        val heightStr = binding.heightInputText.text.toString()
        val weightStr = binding.weightInputText.text.toString()
        val inchesStr = binding.inchesInputText.text.toString()

        // Check for required fields based on unit system
        if (heightStr.isEmpty() || weightStr.isEmpty() || (isEnglishUnit && inchesStr.isEmpty())) {
            Toast.makeText(this, getString(R.string.error_input_required), Toast.LENGTH_SHORT).show()
            return
        }

        // 2. GET GENDER (Read selection for future use/logging)
        val selectedGender = binding.genderInputText.text.toString()

        // 3. CONVERT INPUT TO STANDARD SI UNITS (Meters and Kilograms)
        val heightInMeters: Double
        val weightInKilograms: Double

        try {
            if (isEnglishUnit) {
                // English: Convert feet/inches to total inches, then to meters; lbs to kg
                val feet = heightStr.toDouble()
                val inches = inchesStr.toDouble()
                val weightLbs = weightStr.toDouble()

                // Total Inches = (feet * 12) + inches
                val totalInches = (feet * 12) + inches

                // Convert inches to meters (1 in = 0.0254 m)
                heightInMeters = totalInches * 0.0254

                // Convert pounds to kilograms (1 lb = 0.453592 kg)
                weightInKilograms = weightLbs * 0.453592
            } else {
                // Metric: Convert cm to meters
                val heightCm = heightStr.toDouble()
                val weightKg = weightStr.toDouble()

                // Convert cm to meters (1 m = 100 cm)
                heightInMeters = heightCm / 100.0
                weightInKilograms = weightKg
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, getString(R.string.error_invalid_value), Toast.LENGTH_SHORT).show()
            return
        }

        // 4. CALCULATE BMI
        // Formula: BMI = kg / m^2
        val bmi = weightInKilograms / heightInMeters.pow(2)
        val roundedBmi = (bmi * 10.0).roundToInt() / 10.0 // Round to one decimal place

        // 5. GET CLASSIFICATION (Note: Standard BMI ranges are used, regardless of gender)
        val classification = getBmiClassification(bmi)

        // 6. UPDATE UI WITH RESULTS
        updateResultDisplay(roundedBmi, classification)
    }

    // ☆ CLASSIFICATION AND COLOR LOGIC (2 ptos. for color handling) ☆
    private fun getBmiClassification(bmi: Double): BmiClassificationResult {
        return when {
            bmi < 18.5 -> {
                // Underweight: Range is < 18.5
                BmiClassificationResult(
                    getString(R.string.status_underweight),
                    R.color.colorUnderweight,
                    0.0, // Min BMI for reference (not used in weight calc)
                    18.5
                )
            }
            bmi < 24.9 -> {
                // Normal: Range is 18.5 to 24.9
                BmiClassificationResult(
                    getString(R.string.status_normal),
                    R.color.colorNormal,
                    18.5,
                    24.9
                )
            }
            bmi < 29.9 -> {
                // Overweight: Range is 25.0 to 29.9
                BmiClassificationResult(
                    getString(R.string.status_overweight),
                    R.color.colorOverweight,
                    25.0,
                    29.9
                )
            }
            else -> {
                // Obese: Range is 30.0 and up
                BmiClassificationResult(
                    getString(R.string.status_obese),
                    R.color.colorObese,
                    30.0,
                    Double.MAX_VALUE
                )
            }
        }
    }

    // ☆ DISPLAY RESULTS (Includes Normal Weight Range Calculation) ☆
    private fun updateResultDisplay(bmi: Double, classification: BmiClassificationResult) {
        // 1. Display BMI value and Status
        binding.bmiResultText.text = bmi.toString()
        binding.statusText.text = classification.status

        // 2. Apply color to Status Text and Indicator View (2 ptos.)
        val color = ContextCompat.getColor(this, classification.colorResId)
        binding.statusText.setTextColor(color)
        binding.statusIndicatorView.setBackgroundColor(color)

        // 3. Calculate Normal Weight Range (Range = Min/Max BMI * Height^2)

        // Recalculate height in meters based on current inputs
        val heightInMeters: Double = try {
            if (isEnglishUnit) {
                val feet = binding.heightInputText.text.toString().toDouble()
                val inches = binding.inchesInputText.text.toString().toDouble()
                val totalInches = (feet * 12) + inches
                totalInches * 0.0254 // Convert to meters
            } else {
                binding.heightInputText.text.toString().toDouble() / 100.0 // Convert cm to meters
            }
        } catch (e: Exception) {
            return
        }

        // Calculate Min/Max weight in KG based on standard normal BMI range (18.5 - 24.9)
        val minBmiKg = 18.5 * heightInMeters.pow(2)
        val maxBmiKg = 24.9 * heightInMeters.pow(2)

        val unitLabel: String
        val minWeightStr: String
        val maxWeightStr: String

        // 4. Convert the weight range back to the user's selected unit system
        if (isEnglishUnit) {
            // Convert kg to lbs (1 kg = 2.20462 lbs)
            minWeightStr = (minBmiKg * 2.20462).roundToInt().toString()
            maxWeightStr = (maxBmiKg * 2.20462).roundToInt().toString()
            unitLabel = getString(R.string.unit_weight_english) // 'lbs'
        } else {
            // Keep in kg
            minWeightStr = (minBmiKg).roundToInt().toString()
            maxWeightStr = (maxBmiKg).roundToInt().toString()
            unitLabel = getString(R.string.unit_weight_metric) // 'kg'
        }

        // 5. Set Normal Range text (30 ptos.)
        val normalRangeMsg = getString(
            R.string.result_normal_range,
            minWeightStr,
            maxWeightStr,
            unitLabel
        )
        binding.normalRangeText.text = normalRangeMsg

        // 6. Show the result card
        binding.resultCard.visibility = View.VISIBLE
    }
}
