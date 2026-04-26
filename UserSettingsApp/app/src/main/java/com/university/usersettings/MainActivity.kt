package com.university.usersettings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    lateinit var etStudentName: EditText
    lateinit var rgTheme: RadioGroup
    lateinit var rbLight: RadioButton
    lateinit var rbDark: RadioButton
    lateinit var rbSystem: RadioButton
    lateinit var switchNotif: SwitchCompat
    lateinit var spinnerLang: Spinner
    lateinit var seekBarFont: SeekBar
    lateinit var tvFontSizeLabel: TextView
    lateinit var btnSaveSettings: Button
    lateinit var btnResetSettings: Button
    lateinit var btnViewSettings: Button
    lateinit var fabProfile: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etStudentName = findViewById(R.id.etStudentName)
        rgTheme = findViewById(R.id.rgTheme)
        rbLight = findViewById(R.id.rbLight)
        rbDark = findViewById(R.id.rbDark)
        rbSystem = findViewById(R.id.rbSystem)
        switchNotif = findViewById(R.id.switchNotif)
        spinnerLang = findViewById(R.id.spinnerLang)
        seekBarFont = findViewById(R.id.seekBarFont)
        tvFontSizeLabel = findViewById(R.id.tvFontSizeLabel)
        btnSaveSettings = findViewById(R.id.btnSaveSettings)
        btnResetSettings = findViewById(R.id.btnResetSettings)
        btnViewSettings = findViewById(R.id.btnViewSettings)
        fabProfile = findViewById(R.id.fabProfile)

        val languages = arrayOf("English", "Bangla", "Arabic", "French")
        val langAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLang.adapter = langAdapter

        seekBarFont.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvFontSizeLabel.text = "Font Size: ${progress + 12}sp"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnSaveSettings.setOnClickListener { saveSettings() }
        btnResetSettings.setOnClickListener { resetPreferences() }
        btnViewSettings.setOnClickListener {
            startActivity(Intent(this, SettingsViewerActivity::class.java))
        }
        fabProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val profilePrefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)

        etStudentName.setText(profilePrefs.getString("KEY_STUDENT_NAME", ""))

        when (prefs.getString("KEY_THEME", "light")) {
            "light" -> rbLight.isChecked = true
            "dark" -> rbDark.isChecked = true
            "system" -> rbSystem.isChecked = true
        }

        val fontSize = prefs.getInt("KEY_FONT_SIZE", 16)
        seekBarFont.progress = fontSize - 12
        tvFontSizeLabel.text = "Font Size: ${fontSize}sp"

        switchNotif.isChecked = prefs.getBoolean("KEY_NOTIFICATIONS", true)

        val languages = arrayOf("English", "Bangla", "Arabic", "French")
        val savedLanguage = prefs.getString("KEY_LANGUAGE", "English")
        val langIndex = languages.indexOf(savedLanguage)
        if (langIndex >= 0) spinnerLang.setSelection(langIndex)
    }

    fun saveSettings() {
        val selectedTheme = when (rgTheme.checkedRadioButtonId) {
            R.id.rbLight -> "light"
            R.id.rbDark -> "dark"
            R.id.rbSystem -> "system"
            else -> "light"
        }

        val appPrefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        with(appPrefs.edit()) {
            putString("KEY_THEME", selectedTheme)
            putBoolean("KEY_NOTIFICATIONS", switchNotif.isChecked)
            putString("KEY_LANGUAGE", spinnerLang.selectedItem.toString())
            putInt("KEY_FONT_SIZE", seekBarFont.progress + 12)
            putLong("KEY_LAST_SAVED", System.currentTimeMillis())
            apply()
        }

        val profilePrefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
        with(profilePrefs.edit()) {
            putString("KEY_STUDENT_NAME", etStudentName.text.toString())
            apply()
        }

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
    }

    fun resetPreferences() {
        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        rbLight.isChecked = true
        switchNotif.isChecked = true
        seekBarFont.progress = 4
        tvFontSizeLabel.text = "Font Size: 16sp"
        spinnerLang.setSelection(0)
        etStudentName.setText("")

        Toast.makeText(this, "Settings reset to default", Toast.LENGTH_SHORT).show()
    }
}
