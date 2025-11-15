package com.example.ucreportingsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class LoginActivity : AppCompatActivity() {

    private lateinit var EmailInput: EditText
    private lateinit var PasswordInput: EditText

    //To access cloud Firestore
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        EmailInput = findViewById(R.id.et_email)
        PasswordInput = findViewById(R.id.et_password)

        setupCreateAccountLink()
        setupLoginButton()
        setupEmergencyReportButton()

        val Create_Account = findViewById<Button>(R.id.btn_create_account)
        Create_Account.setOnClickListener {
            val intent = Intent(this, StudentRegistrationActivity::class.java)
            startActivity(intent)

        }

    }
    private fun setupCreateAccountLink() {
        findViewById<Button>(R.id.btn_create_account).setOnClickListener {
            val intent = Intent(this, StudentRegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupLoginButton() {
        findViewById<Button>(R.id.btn_login).setOnClickListener {

            val enteredEmail = EmailInput.text.toString().trim()
            val enteredPassword = PasswordInput.text.toString()

            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            UserRepository.fetchUser(enteredEmail, enteredPassword) { userRole ->
                when (userRole) {
                    "Student" -> {
                        Toast.makeText(this, "Student login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, StudentHomeActivity::class.java)
                        startActivity(intent)
                        finish() // Finish LoginActivity so user can't go back
                    }
                    "Staff" -> {
                        Toast.makeText(this, "Staff login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, StaffHomepage::class.java)
                        startActivity(intent)
                        finish() // Finish LoginActivity
                    }
                    else -> {
                        // This block runs if userRole is null (login failed)
                        Toast.makeText(this, "Login failed. Invalid credentials or network error.", Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }

    private fun setupEmergencyReportButton() {
        findViewById<Button>(R.id.btn_emergency_report).setOnClickListener {
            Toast.makeText(this, "Starting Emergency Report flow...", Toast.LENGTH_SHORT).show()
        }
    }

}