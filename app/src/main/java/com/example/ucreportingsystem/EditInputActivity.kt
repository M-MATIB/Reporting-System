package com.example.ucreportingsystem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ucreportingsystem.databinding.ActivityEditInputBinding

class EditInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditInputBinding
    private var fieldType: String = ""

    companion object {
        const val EXTRA_FIELD_TYPE = "extra_field_type"
        const val RESULT_NEW_VALUE = "result_new_value"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fieldType = intent.getStringExtra(EXTRA_FIELD_TYPE) ?: "Value"

        setFieldSpecificUI(fieldType)

        binding.btnBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        binding.btnSubmitReport.setOnClickListener {
            val newValue = binding.etInputNew.text.toString().trim()
            val confirmValue = binding.etConfirmNew.text.toString().trim()

            binding.tilInputNew.error = null
            binding.tilConfirmNew.error = null

            if (newValue.isEmpty() || confirmValue.isEmpty()) {
                Toast.makeText(this, "Input field cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fieldType == "Phone") {
                if (newValue.length != 11) {
                    Toast.makeText(this, "Phone number must be exactly 11 digits.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            if (newValue != confirmValue) {
                val errorMsg = when (fieldType) {
                    "Email" -> "Email doesn't match."
                    "Phone" -> "Phone numbers don't match."
                    "Address" -> "Address values don't match."
                    "Password" -> "Passwords don't match."
                    else -> "Values do not match."
                }

                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }

            val resultIntent = Intent().apply {
                putExtra(EXTRA_FIELD_TYPE, fieldType)
                putExtra(RESULT_NEW_VALUE, newValue)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun setFieldSpecificUI(type: String) {
        val iconResId: Int
        val inputType: Int
        val hint1: String
        val hint2: String
        val saveText: String

        binding.tilInputNew.isPasswordVisibilityToggleEnabled = false
        binding.tilConfirmNew.isPasswordVisibilityToggleEnabled = false

        when (type) {
            "Email" -> {
                iconResId = R.drawable.email_icon
                inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                hint1 = "Input New Email"
                hint2 = "Confirm New Email"
                binding.etInputNew.hint = "e.g., your.email@gmail.com"
                binding.etConfirmNew.hint = "e.g., confirm.email@gmail.com"
                saveText = "Save Email"
            }
            "Phone" -> {
                iconResId = R.drawable.phone_icon
                inputType = InputType.TYPE_CLASS_PHONE
                hint1 = "Input Mobile Number"
                hint2 = "Confirm Mobile Number"
                binding.etInputNew.hint = "e.g., 09xxxxxxxxx"
                binding.etConfirmNew.hint = "e.g., 09xxxxxxxxx"
                saveText = "Save Phone"
            }
            "Address" -> {
                iconResId = R.drawable.home_icon
                inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_CLASS_TEXT
                hint1 = "Input New Address"
                hint2 = "Confirm New Address"
                binding.etInputNew.hint = "e.g., 123 Main St, Baguio City"
                binding.etConfirmNew.hint = "e.g., 123 Main St, Baguio City"
                saveText = "Save Address"
            }
            "Password" -> {
                iconResId = R.drawable.lock_icon
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                hint1 = "Input New Password"
                hint2 = "Confirm New Password"
                binding.etInputNew.hint = "e.g., P@ssword123"
                binding.etConfirmNew.hint = "e.g., P@ssword123"
                saveText = "Save Password"

                binding.tilInputNew.isPasswordVisibilityToggleEnabled = true
                binding.tilConfirmNew.isPasswordVisibilityToggleEnabled = true
            }
            else -> return
        }

        binding.ivFieldIcon.setImageResource(iconResId)
        binding.tvNewFieldTitle.text = "Edit $type"
        binding.tvInputHint1.text = hint1
        binding.tvInputHint2.text = hint2
        binding.etInputNew.inputType = inputType
        binding.etConfirmNew.inputType = inputType
        binding.btnSubmitReport.text = saveText
    }
}