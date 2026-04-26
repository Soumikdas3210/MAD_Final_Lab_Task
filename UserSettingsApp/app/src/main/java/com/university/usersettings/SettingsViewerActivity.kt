package com.university.usersettings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_viewer)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Saved Settings"

        val tvPlaceholder = findViewById<TextView>(R.id.tvPlaceholder)
        val layoutCards = findViewById<LinearLayout>(R.id.layoutCards)
        val btnEdit = findViewById<Button>(R.id.btnEdit)

        val appPrefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val profilePrefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)

        val lastSaved = appPrefs.getLong("KEY_LAST_SAVED", -1L)

        if (lastSaved == -1L) {
            tvPlaceholder.visibility = View.VISIBLE
            layoutCards.visibility = View.GONE
        } else {
            tvPlaceholder.visibility = View.GONE
            layoutCards.visibility = View.VISIBLE

            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val tvLastSaved = findViewById<TextView>(R.id.tvLastSaved)
            tvLastSaved.text = "Last Saved: ${sdf.format(Date(lastSaved))}"

            val tvNameValue = findViewById<TextView>(R.id.tvNameValue)
            val tvThemeValue = findViewById<TextView>(R.id.tvThemeValue)
            val tvNotifValue = findViewById<TextView>(R.id.tvNotifValue)
            val tvLangValue = findViewById<TextView>(R.id.tvLangValue)
            val tvFontSizeValue = findViewById<TextView>(R.id.tvFontSizeValue)
            val tvStudentIdValue = findViewById<TextView>(R.id.tvStudentIdValue)
            val tvDeptValue = findViewById<TextView>(R.id.tvDeptValue)
            val tvYearValue = findViewById<TextView>(R.id.tvYearValue)
            val tvEmailValue = findViewById<TextView>(R.id.tvEmailValue)

            tvNameValue.text = profilePrefs.getString("KEY_STUDENT_NAME", "Not set") ?: "Not set"
            tvThemeValue.text = appPrefs.getString("KEY_THEME", "light") ?: "light"
            tvNotifValue.text = if (appPrefs.getBoolean("KEY_NOTIFICATIONS", true)) "Enabled" else "Disabled"
            tvLangValue.text = appPrefs.getString("KEY_LANGUAGE", "English") ?: "English"
            tvFontSizeValue.text = "${appPrefs.getInt("KEY_FONT_SIZE", 16)}sp"
            tvStudentIdValue.text = profilePrefs.getString("KEY_STUDENT_ID", "Not set") ?: "Not set"
            tvDeptValue.text = profilePrefs.getString("KEY_DEPARTMENT", "Not set") ?: "Not set"
            tvYearValue.text = profilePrefs.getString("KEY_YEAR", "Not set") ?: "Not set"
            tvEmailValue.text = profilePrefs.getString("KEY_EMAIL", "Not set") ?: "Not set"
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
