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

class LoginStaffActivity : AppCompatActivity() {

    private lateinit var EmailInput: EditText
    private lateinit var PasswordInput: EditText
    private lateinit var toggleGroup: MaterialButtonToggleGroup

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_staff)

        EmailInput = findViewById(R.id.et_email)
        PasswordInput = findViewById(R.id.et_password)
        toggleGroup = findViewById(R.id.toggle_role_group)

        setupToggleLogic()
        setupCreateAccountLink()
        setupLoginButton()
        setupEmergencyReportButton()
    }

    private fun setupToggleLogic() {
        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked && checkedId == R.id.btn_role_student) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
        }
    }

    private fun setupCreateAccountLink() {
        findViewById<Button>(R.id.btn_create_account).setOnClickListener {
            val intent = Intent(this, StaffRegistrationActivity::class.java)
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
                        finish()
                    }
                    "Staff" -> {
                        Toast.makeText(this, "Staff login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, StaffHomepage::class.java)
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