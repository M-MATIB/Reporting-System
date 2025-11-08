package com.example.ucreportingsystem

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase

class LoginActivity : AppCompatActivity() {
    //By Melquin

    private val STUDENT_DOMAIN = "@students.uc-bcf.edu.ph"
    private val STAFF_DOMAIN = "@staff.uc-bcf.edu.ph"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        setupCreateAccountLink()
        setupLoginButton()
        setupEmergencyReportButton()

        val Create_Account = findViewById<Button>(R.id.btn_create_account)
        Create_Account.setOnClickListener {
            val intent = Intent(this, StudentRegistrationActivity::class.java)
            startActivity(intent)

        }

    }

    /*
    By Jowas
    private fun setupCreateAccountLink() {
        findViewById<Button>(R.id.btn_create_account).setOnClickListener {
            val intent = Intent(this, StudentRegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupLoginButton() {
        findViewById<Button>(R.id.btn_login).setOnClickListener {
            val emailInput = findViewById<EditText>(R.id.et_email)
            val passwordInput = findViewById<EditText>(R.id.et_password)

            val enteredEmail = emailInput.text.toString().trim()
            val enteredPassword = passwordInput.text.toString()

            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userRole = when {
                enteredEmail.endsWith(STUDENT_DOMAIN, ignoreCase = true) -> "student"
                enteredEmail.endsWith(STAFF_DOMAIN, ignoreCase = true) -> "staff"
                else -> "unknown"
            }

            if (userRole == "student") {
                val intent = Intent(this, StudentHomeActivity::class.java).apply {
                    putExtra(StudentProfileActivity.EXTRA_LOGIN_EMAIL, enteredEmail)
                    putExtra(StudentProfileActivity.EXTRA_LOGIN_PASSWORD, enteredPassword)
                }
                startActivity(intent)
                finish()
            }

            else if (userRole == "staff") {
                val intent = Intent(this, StaffHomepage::class.java).apply {
                }
                startActivity(intent)
                finish()
                Toast.makeText(this, "Staff login successful. Navigate to Staff Home.", Toast.LENGTH_LONG).show()
            }

            else if (userRole == "staff") {
                val intent = Intent(this, StaffHomepage::class.java).apply {
                }
                startActivity(intent)
                finish()
                Toast.makeText(this, "Staff login successful. Navigate to Staff Home.", Toast.LENGTH_LONG).show()
            }

            else {
                Toast.makeText(this, "Login failed. Invalid email domain or Password.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupEmergencyReportButton() {
        findViewById<Button>(R.id.btn_emergency_report).setOnClickListener {
            Toast.makeText(this, "Starting Emergency Report flow...", Toast.LENGTH_SHORT).show()
        }
    }
     */

    private fun setupCreateAccountLink() {
        findViewById<Button>(R.id.btn_create_account).setOnClickListener {
            val intent = Intent(this, StudentRegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupLoginButton() {
        findViewById<Button>(R.id.btn_login).setOnClickListener {
            val emailInput = findViewById<EditText>(R.id.et_email)
            val passwordInput = findViewById<EditText>(R.id.et_password)

            val enteredEmail = emailInput.text.toString().trim()
            val enteredPassword = passwordInput.text.toString()

            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userRole = when {
                enteredEmail.endsWith(STUDENT_DOMAIN, ignoreCase = true) -> "student"
                enteredEmail.endsWith(STAFF_DOMAIN, ignoreCase = true) -> "staff"
                else -> "unknown"
            }

            if (userRole == "student") {
                val intent = Intent(this, StudentHomeActivity::class.java).apply {
                    putExtra(StudentProfileActivity.EXTRA_LOGIN_EMAIL, enteredEmail)
                    putExtra(StudentProfileActivity.EXTRA_LOGIN_PASSWORD, enteredPassword)
                }
                startActivity(intent)
                finish()
            }

            else if (userRole == "staff") {
                val intent = Intent(this, StaffHomepage::class.java).apply {
                }
                startActivity(intent)
                finish()
                Toast.makeText(this, "Staff login successful. Navigate to Staff Home.", Toast.LENGTH_LONG).show()
            }

            else if (userRole == "staff") {
                val intent = Intent(this, StaffHomepage::class.java).apply {
                }
                startActivity(intent)
                finish()
                Toast.makeText(this, "Staff login successful. Navigate to Staff Home.", Toast.LENGTH_LONG).show()
            }

            else {
                Toast.makeText(this, "Login failed. Invalid email domain or Password.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupEmergencyReportButton() {
        findViewById<Button>(R.id.btn_emergency_report).setOnClickListener {
            Toast.makeText(this, "Starting Emergency Report flow...", Toast.LENGTH_SHORT).show()
        }
    }



}