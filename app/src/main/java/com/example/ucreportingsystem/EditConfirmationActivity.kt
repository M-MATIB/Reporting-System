package com.example.ucreportingsystem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ucreportingsystem.databinding.ActivityEditConfirmationBinding

class EditConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditConfirmationBinding
    private var fieldType: String = ""

    companion object {
        const val EXTRA_FIELD_TYPE = "extra_field_type"
        const val EXTRA_CURRENT_VALUE = "extra_current_value"
        private const val EMPTY_PHONE_PROMPT = "No phone number set."
        private const val EMPTY_ADDRESS_PROMPT = "No address set."
        private const val ADD_PHONE_MESSAGE = "There's no current phone number. Do you wish to add?"
        private const val ADD_PHONE_BUTTON = "Add Phone Number"
        private const val ADD_ADDRESS_MESSAGE = "There's no current address. Do you wish to add?"
        private const val ADD_ADDRESS_BUTTON = "Add Address"
    }

    private val inputActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            setResult(Activity.RESULT_OK, result.data)

            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fieldType = intent.getStringExtra(EXTRA_FIELD_TYPE) ?: "Value"
        val currentValue = intent.getStringExtra(EXTRA_CURRENT_VALUE) ?: ""

        setFieldSpecificUI(fieldType, currentValue)

        binding.btnBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        binding.btnChangeValue.setOnClickListener {
            launchEditInputActivity(fieldType)
        }
    }

    /**
     * Function to launch the EditInputActivity using the launcher.
     */
    private fun launchEditInputActivity(fieldType: String) {
        val intent = Intent(this, EditInputActivity::class.java).apply {
            putExtra(EditInputActivity.EXTRA_FIELD_TYPE, fieldType)
        }
        inputActivityLauncher.launch(intent)
    }

    /**
     * Sets the icon, title, button text, confirmation message, and current value display based on the field.
     */
    private fun setFieldSpecificUI(type: String, currentValue: String) {
        val iconResId: Int
        var message: String
        var buttonText: String
        var displayedValue: String

        val isValueEmpty = currentValue.isEmpty()

        when (type) {
            "Email" -> {
                iconResId = R.drawable.email_icon
                message = "This is your current email.\nDo you wish to change it?"
                buttonText = "Change Email"
                displayedValue = currentValue
            }
            "Phone" -> {
                iconResId = R.drawable.phone_icon

                if (isValueEmpty) {
                    message = ADD_PHONE_MESSAGE
                    buttonText = ADD_PHONE_BUTTON
                    displayedValue = EMPTY_PHONE_PROMPT
                } else {
                    message = "This is your current phone number.\nDo you wish to change it?"
                    buttonText = "Change Phone"
                    displayedValue = currentValue
                }
            }
            "Address" -> {
                iconResId = R.drawable.home_icon

                if (isValueEmpty) {
                    message = ADD_ADDRESS_MESSAGE
                    buttonText = ADD_ADDRESS_BUTTON
                    displayedValue = EMPTY_ADDRESS_PROMPT
                } else {
                    message = "This is your current address.\nDo you wish to change it?"
                    buttonText = "Change Address"
                    displayedValue = currentValue
                }
            }
            "Password" -> {
                iconResId = R.drawable.lock_icon
                message = "Your current password is hidden for security.\nDo you wish to change it?"
                buttonText = "Change Password"
                val passwordLength = currentValue.length
                displayedValue = "•".repeat(passwordLength)
            }
            else -> {
                iconResId = R.drawable.email_icon
                message = "This is your current information.\nDo you wish to change it?"
                buttonText = "Change Value"
                displayedValue = currentValue
            }
        }

        binding.ivFieldIcon.setImageResource(iconResId)
        binding.tvFieldTitle.text = "$type:"
        binding.btnChangeValue.text = buttonText
        binding.tvConfirmationMessage.text = message

        binding.etCurrentValue.text = displayedValue
    }
}