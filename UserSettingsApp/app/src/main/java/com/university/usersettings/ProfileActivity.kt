package com.university.usersettings

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    lateinit var tvWelcome: TextView
    lateinit var etStudentId: EditText
    lateinit var etFullName: EditText
    lateinit var spinnerDept: Spinner
    lateinit var spinnerYear: Spinner
    lateinit var etEmail: EditText
    lateinit var btnSaveProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profile Setup"

        tvWelcome = findViewById(R.id.tvWelcome)
        etStudentId = findViewById(R.id.etStudentId)
        etFullName = findViewById(R.id.etFullName)
        spinnerDept = findViewById(R.id.spinnerDept)
        spinnerYear = findViewById(R.id.spinnerYear)
        etEmail = findViewById(R.id.etEmail)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)

        val deptAdapter = ArrayAdapter.createFromResource(
            this, R.array.department_options, android.R.layout.simple_spinner_item
        )
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDept.adapter = deptAdapter

        val yearAdapter = ArrayAdapter.createFromResource(
            this, R.array.year_options, android.R.layout.simple_spinner_item
        )
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = yearAdapter

        loadProfile()

        btnSaveProfile.setOnClickListener { saveProfile() }
    }

    fun loadProfile() {
        val profilePrefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)

        val savedName = profilePrefs.getString("KEY_STUDENT_NAME", "")
        tvWelcome.text = if (savedName.isNullOrEmpty()) "Welcome!" else "Welcome back, $savedName!"

        etStudentId.setText(profilePrefs.getString("KEY_STUDENT_ID", ""))
        etFullName.setText(profilePrefs.getString("KEY_STUDENT_NAME", ""))
        etEmail.setText(profilePrefs.getString("KEY_EMAIL", ""))

        val departments = arrayOf("CSE", "EEE", "BBA", "English", "Law")
        val savedDept = profilePrefs.getString("KEY_DEPARTMENT", "")
        val deptIndex = departments.indexOf(savedDept)
        if (deptIndex >= 0) spinnerDept.setSelection(deptIndex)

        val years = arrayOf("1st Year", "2nd Year", "3rd Year", "4th Year")
        val savedYear = profilePrefs.getString("KEY_YEAR", "")
        val yearIndex = years.indexOf(savedYear)
        if (yearIndex >= 0) spinnerYear.setSelection(yearIndex)
    }

    fun saveProfile() {
        val profilePrefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
        with(profilePrefs.edit()) {
            putString("KEY_STUDENT_ID", etStudentId.text.toString())
            putString("KEY_STUDENT_NAME", etFullName.text.toString())
            putString("KEY_DEPARTMENT", spinnerDept.selectedItem.toString())
            putString("KEY_YEAR", spinnerYear.selectedItem.toString())
            putString("KEY_EMAIL", etEmail.text.toString())
            apply()
        }

        val savedName = etFullName.text.toString()
        tvWelcome.text = if (savedName.isEmpty()) "Welcome!" else "Welcome back, $savedName!"

        Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
