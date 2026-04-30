package com.university.studentauth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        if (user == null) {
            navigateToLogin()
            return
        }

        populateUserInfo(user)

        // Reload user to ensure metadata and details are fully synced
        user.reload().addOnCompleteListener {
            val updatedUser = auth.currentUser
            if (updatedUser != null) {
                populateUserInfo(updatedUser)
            }
        }

        setupLogout()
        setupChangePassword()
        setupDeleteAccount()
    }

    private fun populateUserInfo(user: com.google.firebase.auth.FirebaseUser) {
        val email = if (user.email.isNullOrEmpty()) "No Email Provided" else user.email
        val uid = if (user.uid.isEmpty()) "No UID" else user.uid
        val creationTimestamp = user.metadata?.creationTimestamp ?: 0L
        val dateStr = if (creationTimestamp > 0) {
            SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
                .format(Date(creationTimestamp))
        } else {
            "Just now"
        }

        val initial = email?.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
        findViewById<TextView>(R.id.tv_avatar).text = initial
        findViewById<TextView>(R.id.tv_email_value).text = email
        findViewById<TextView>(R.id.tv_uid_value).text = uid
        findViewById<TextView>(R.id.tv_date_value).text = dateStr
    }

    private fun setupLogout() {
        findViewById<MaterialButton>(R.id.btn_logout).setOnClickListener {
            auth.signOut()
            navigateToLogin()
        }
    }

    private fun setupChangePassword() {
        val etNewPassword = findViewById<TextInputEditText>(R.id.et_new_password)
        val etConfirmNewPassword = findViewById<TextInputEditText>(R.id.et_confirm_new_password)
        val tilNewPassword = findViewById<TextInputLayout>(R.id.til_new_password)
        val tilConfirmNewPassword = findViewById<TextInputLayout>(R.id.til_confirm_new_password)
        val btnUpdate = findViewById<MaterialButton>(R.id.btn_update_password)

        btnUpdate.setOnClickListener {
            val newPassword = etNewPassword.text.toString()
            val confirmPassword = etConfirmNewPassword.text.toString()

            tilNewPassword.error = null
            tilConfirmNewPassword.error = null

            if (newPassword.isEmpty()) {
                tilNewPassword.error = "New password is required"
                return@setOnClickListener
            }
            if (newPassword.length < 8) {
                tilNewPassword.error = "Password must be at least 8 characters"
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                tilConfirmNewPassword.error = "Please confirm your new password"
                return@setOnClickListener
            }
            if (newPassword != confirmPassword) {
                tilConfirmNewPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            btnUpdate.isEnabled = false

            auth.currentUser?.updatePassword(newPassword)
                ?.addOnSuccessListener {
                    btnUpdate.isEnabled = true
                    etNewPassword.text?.clear()
                    etConfirmNewPassword.text?.clear()
                    Snackbar.make(btnUpdate, getString(R.string.password_updated), Snackbar.LENGTH_LONG).show()
                }
                ?.addOnFailureListener { e ->
                    btnUpdate.isEnabled = true
                    Snackbar.make(
                        btnUpdate,
                        e.message ?: "Failed to update password.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
        }
    }

    private fun setupDeleteAccount() {
        val btnDelete = findViewById<MaterialButton>(R.id.btn_delete_account)

        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_confirm_title))
                .setMessage(getString(R.string.delete_confirm_message))
                .setPositiveButton(getString(R.string.btn_delete)) { _, _ ->
                    btnDelete.isEnabled = false
                    auth.currentUser?.delete()
                        ?.addOnSuccessListener {
                            navigateToLogin()
                        }
                        ?.addOnFailureListener { e ->
                            btnDelete.isEnabled = true
                            Snackbar.make(
                                btnDelete,
                                e.message ?: "Failed to delete account.",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                }
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
