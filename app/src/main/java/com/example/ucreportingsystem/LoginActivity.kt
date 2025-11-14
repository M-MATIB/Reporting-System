package com.example.ucreportingsystem

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

            searchUserInCollections(enteredEmail, enteredPassword)

        }
    }

    private fun setupEmergencyReportButton() {
        findViewById<Button>(R.id.btn_emergency_report).setOnClickListener {
            Toast.makeText(this, "Starting Emergency Report flow...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchUserInCollections(enteredEmail:String, enteredPassword:String) {
        // First, search in the "Student" collection
        db.collection("Student")
            .whereEqualTo("Email", enteredEmail)
            .whereEqualTo("Password", enteredPassword)
            .get()
            .addOnSuccessListener { studentResult ->
                if (!studentResult.isEmpty) {
                    // User found in Student collection
                    Toast.makeText(this, "Student login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, StudentHomeActivity::class.java)
                    startActivity(intent)
                    finish() // Optional: Finish LoginActivity so user can't go back
                } else {
                    // If not found in Student, search in the "Staff" collection
                    searchInStaffCollection(enteredEmail, enteredPassword)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents from Student collection.", exception)
                Toast.makeText(this, "An error occurred during login.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun searchInStaffCollection(enteredEmail: String, enteredPassword: String) {
        db.collection("Staff")
            .whereEqualTo("Email", enteredEmail)
            .whereEqualTo("Password", enteredPassword)
            .get()
            .addOnSuccessListener { staffResult ->
                if (!staffResult.isEmpty) {
                    // User found in Staff collection
                    Toast.makeText(this, "Staff login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, StaffHomepage::class.java)
                    startActivity(intent)
                    finish() // Optional: Finish LoginActivity
                } else {
                    // User not found in either collection
                    Toast.makeText(this, "Login failed. Invalid credentials.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents from Staff collection.", exception)
                Toast.makeText(this, "An error occurred during login.", Toast.LENGTH_SHORT).show()
            }
    }

}