package com.example.ucreportingsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class LoginStudent : AppCompatActivity() {

    private lateinit var EmailInput: EditText
    private lateinit var PasswordInput: EditText
    private lateinit var toggleGroup: MaterialButtonToggleGroup

    //To access cloud Firestore
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        EmailInput = findViewById(R.id.et_email)
        PasswordInput = findViewById(R.id.et_password)
        toggleGroup = findViewById(R.id.toggle_role_group)

        // Initialize Setup Functions
        setupToggleLogic()
        setupCreateAccountLink()
        setupLoginButton()
        setupEmergencyReportButton()
    }

    // 1. NEW: Logic to switch to Staff Activity
    private fun setupToggleLogic() {
        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            }
        findViewById<Button>(R.id.btn_role_staff).setOnClickListener {
            val intent = Intent(this, LoginStaff::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
    }

    // 2. Create Account -> Goes to STUDENT Registration
    private fun setupCreateAccountLink() {
        findViewById<Button>(R.id.btn_create_account).setOnClickListener {
            val intent = Intent(this, StudentRegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    // 3. Login Logic
    private fun setupLoginButton() {
        findViewById<Button>(R.id.btn_login).setOnClickListener {

            val enteredEmail = EmailInput.text.toString().trim()
            val enteredPassword = PasswordInput.text.toString()

            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            UserRepository.fetchStudent(enteredEmail, enteredPassword, this) { userRole ->
                when (userRole) {
                    "Student" -> {
                        Toast.makeText(this, "Student login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, StudentHomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else -> {
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