/*
☆
☆ Author: ☆ Alexis J. Benejan ☆
☆ Language: Kotlin
☆ File Name: AboutActivity.kt
☆ Date: October 6, 2025
☆ Description: Displays developer information (Alexis J. Benejan) and links (Ko-fi/GitHub).
☆ It retrieves the app version from the package manager.
☆
*/

//Final Version: 1.1.0
//Date: October 6, 2025

package com.alexis_benejan.bmical

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alexis_benejan.bmical.databinding.ActivityAboutBinding
import android.util.Log // <--- ADDED LOG IMPORT for debugging

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // CRASH POINT 1: If ActivityAboutBinding isn't generated/found, the inflation fails.
        // It's vital that activity_about.xml is correct and present in res/layout/
        try {
            binding = ActivityAboutBinding.inflate(layoutInflater)
            setContentView(binding.root)
        } catch (e: Exception) {
            // Safely log the specific cause of the crash (likely a missing XML resource/binding issue)
            Log.e("AboutActivity", "Error initializing View Binding: ${e.message}")
            Toast.makeText(this, "Internal Error: Cannot load layout.", Toast.LENGTH_LONG).show()
            finish() // Safely close the activity if layout fails
            return
        }

        // Set the activity title
        title = getString(R.string.menu_about)

        // ☆ 1. DISPLAY VERSION NUMBER ☆
        displayAppVersion()

        // ☆ 2. SETUP LINK BUTTONS ☆
        setupLinkButtons()
    }

    // Function to retrieve and display the app's version name
    private fun displayAppVersion() {
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0)
            }

            // Get the versionName (e.g., "1.1.0")
            val versionName = packageInfo.versionName

            // Format the string resource (e.g., "Version 1.1.0")
            val versionText = getString(R.string.about_version, versionName)
            binding.versionTextView.text = versionText

        } catch (e: PackageManager.NameNotFoundException) {
            // Fallback if version cannot be found
            binding.versionTextView.text = getString(R.string.about_version, "N/A")
            e.printStackTrace()
        }
    }

    // Function to set up click listeners for external links
    private fun setupLinkButtons() {
        // ☆ Ko-fi Link Listener ☆
        binding.koFiButton.setOnClickListener {
            // Get URL from resources
            val url = getString(R.string.url_ko_fi)
            openWebPage(url)
        }

        // ☆ GitHub Link Listener ☆
        binding.githubButton.setOnClickListener {
            // Get URL from resources
            val url = getString(R.string.url_github)
            openWebPage(url)
        }
    }

    // Helper function to launch an external web browser
    private fun openWebPage(url: String) {
        try {
            val webPage = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, webPage)
            startActivity(intent)
        } catch (e: Exception) {
            // Handle case where no browser app is available
            Toast.makeText(this, "Cannot open link: No browser found.", Toast.LENGTH_SHORT).show()
        }
    }
}
