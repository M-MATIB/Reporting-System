package com.example.ucreportingsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButtonToggleGroup

class StudentRegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_registration)

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
            navigateToLogin()
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
}