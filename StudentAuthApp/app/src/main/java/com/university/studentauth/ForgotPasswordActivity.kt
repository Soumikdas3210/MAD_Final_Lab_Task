package com.university.studentauth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tilEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnSendReset: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        auth = FirebaseAuth.getInstance()
        tilEmail = findViewById(R.id.til_email)
        etEmail = findViewById(R.id.et_email)
        btnSendReset = findViewById(R.id.btn_send_reset)

        btnSendReset.setOnClickListener { sendResetEmail() }
    }

    private fun sendResetEmail() {
        val email = etEmail.text.toString().trim()

        tilEmail.error = null

        if (email.isEmpty()) {
            tilEmail.error = "Email is required"
            return
        }

        btnSendReset.isEnabled = false

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.reset_success), Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                btnSendReset.isEnabled = true
                Snackbar.make(
                    btnSendReset,
                    e.message ?: "Failed to send reset email. Please try again.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
    }
}
