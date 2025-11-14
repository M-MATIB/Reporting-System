package com.example.ucreportingsystem

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButtonToggleGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class StudentRegistrationActivity : AppCompatActivity() {

    //View Declaration
    private lateinit var StudentName: EditText
    private lateinit var SchoolEmail: EditText
    private lateinit var CreatePassword: EditText
    private lateinit var ConfirmPassword: EditText
    private lateinit var Register: Button

    //To access cloud Firestore
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_registration)

        //View Initialization
        StudentName = findViewById(R.id.et_full_name)
        SchoolEmail = findViewById(R.id.et_school_email)
        CreatePassword = findViewById(R.id.et_create_password)
        ConfirmPassword = findViewById(R.id.et_confirm_password)
        Register = findViewById(R.id.btn_register)

        setupRoleToggle()
        setupFinalButtons()
    }

    private fun setupRoleToggle() {
        val toggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.toggle_role_group)

        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_role_staff -> {
                        val intent = Intent(this, StaffRegistrationActivity::class.java)

                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                    R.id.btn_role_student -> {
                    }
                }
            }
        }
    }

    private fun setupFinalButtons() {

        findViewById<Button>(R.id.btn_register).setOnClickListener {
            RegisterStudents()
        }

        findViewById<Button>(R.id.btn_create_account).setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun RegisterStudents(){
        Register.setOnClickListener {
            val Student_Name = StudentName.text.toString().trim()
            val School_Email = SchoolEmail.text.toString().trim()
            val Create_Password = CreatePassword.text.toString().trim()
            val Confirm_Password = ConfirmPassword.text.toString().trim()

            if (Student_Name.isEmpty() || School_Email.isEmpty() || Create_Password.isEmpty() || Confirm_Password.isEmpty())
            {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
            else if (Create_Password != Confirm_Password) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
            else {
                AddStudentToFirestore(Student_Name, School_Email, Confirm_Password)
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                navigateToLogin()
            }
        }
    }

    private fun AddStudentToFirestore(
        Student_Name: String,
        SchoolEmail: String,
        ConfirmPassword: String,
    ){
        val student = hashMapOf(
            "Name" to Student_Name,
            "Email" to SchoolEmail,
            "Password" to ConfirmPassword,
        )
        db.collection("Student")
            .add(student)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

}