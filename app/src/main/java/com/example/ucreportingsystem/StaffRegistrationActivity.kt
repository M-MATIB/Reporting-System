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
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.ucreportingsystem.R
import com.google.firebase.Firebase


data class Staff(
    val uid: String,
    val name: String,
    val email: String?,
    val office: String,
    val role: String = "Staff")

class StaffRegistrationActivity : AppCompatActivity() {


    private lateinit var StaffName: EditText
    private lateinit var SchoolEmail: EditText
    private lateinit var CreatePassword: EditText
    private lateinit var ConfirmPassword: EditText
    private lateinit var Register: Button
    private var selectedOffice: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_registration)

        //View Initialization
        StaffName = findViewById(R.id.et_full_name)
        SchoolEmail = findViewById(R.id.et_school_email)
        CreatePassword = findViewById(R.id.et_create_password)
        ConfirmPassword = findViewById(R.id.et_confirm_password)
        Register = findViewById(R.id.btn_register)



        setupOfficeDropdown()
        setupRoleToggle()
        setupFinalButtons()
        RegisterUser()

    }

    private fun setupOfficeDropdown() {
        val offices = resources.getStringArray(R.array.office_departments_array)
        val selectionTextView = findViewById<TextView>(R.id.tv_office_selection_text)
        val dropdownBox = findViewById<ConstraintLayout>(R.id.cl_dropdown_box)

        dropdownBox.setOnClickListener {

            MaterialAlertDialogBuilder(
                this,
                com.google.android.material.R.style.Theme_MaterialComponents_Light_Dialog_Alert
            )
                .setTitle("Select Office or Department")
                .setItems(offices) { dialog, which ->
                    val selection = offices[which]
                    selectionTextView.text = selection
                    this.selectedOffice = selection
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun setupRoleToggle() {
        val toggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.toggle_role_group)

        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_role_student -> {
                        val intent = Intent(this, StudentRegistrationActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                    R.id.btn_role_staff -> {
                    }
                }
            }
        }
    }

    private fun setupFinalButtons() {
        findViewById<Button>(R.id.btn_create_account).setOnClickListener {
            navigateToLogin()
        }

    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun RegisterUser() {
        Register.setOnClickListener {
            val Staff_Name = StaffName.text.toString().trim()
            val School_Email = SchoolEmail.text.toString().trim()
            val Create_Password = CreatePassword.text.toString().trim()
            val Confirm_Password = ConfirmPassword.text.toString().trim()
            val Staff_Office = selectedOffice
            Staff_Office.toString()

            if (Staff_Name.isEmpty() || School_Email.isEmpty() || Create_Password.isEmpty() || Confirm_Password.isEmpty())
            {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
            else if (Staff_Office.isNullOrEmpty()) {
                Toast.makeText(this, "Please select an office", Toast.LENGTH_SHORT).show()
            }
            else if (Create_Password != Confirm_Password) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
            else {
                //
            }
        }
    }

}