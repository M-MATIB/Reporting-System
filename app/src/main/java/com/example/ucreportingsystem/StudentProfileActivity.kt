package com.example.ucreportingsystem

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ucreportingsystem.databinding.ActivityStudentProfileBinding
import com.google.android.material.button.MaterialButton
import android.widget.RadioGroup
import android.widget.RadioButton
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView

class StudentProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentProfileBinding

    companion object {
        private const val DEFAULT_PHONE_TEXT = "Add phone number"
        private const val DEFAULT_ADDRESS_TEXT = "Add address"
        private const val DATE_FORMAT_MMDDYYYY = "MM/DD/YYYY"
        private const val DATE_FORMAT_DDMMYYYY = "DD/MM/YYYY"
        private const val DATE_FORMAT_YYYYMMDD = "YYYY/MM/DD"
    }

    private var actualUserPassword: String = ""
    private var currentPhone: String = ""
    private var currentAddress: String = ""
    private var currentDateFormat: String = DATE_FORMAT_MMDDYYYY
    private val editActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val field = data?.getStringExtra(EditInputActivity.EXTRA_FIELD_TYPE)
            val newValue = data?.getStringExtra(EditInputActivity.RESULT_NEW_VALUE)

            if (field != null && newValue != null) {
                updateProfileField(field, newValue)
                Toast.makeText(this, "$field updated successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_profile)

        binding = ActivityStudentProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: NavigationView = binding.navView

        // Get the user data directly from the single source of truth: the repository
        val user = UserRepository.currentUser

        currentPhone = DEFAULT_PHONE_TEXT
        currentAddress = DEFAULT_ADDRESS_TEXT

        if (user != null){
            binding.tvUserEmail?.text = user?.email
            binding.tvStudentName?.text = user?.email
            actualUserPassword = user?.password ?: " "
            binding.tvUserPhone?.text = currentPhone
            binding.tvUserAddress?.text = currentAddress
            binding.tvDateFormatValue?.text = currentDateFormat
        }
        else {
            // This is a safety net. If no user is logged in, redirect to the Login screen.
            Toast.makeText(this, "Error: User data not found. Please log in again.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginStudent::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish() // Close this activity
            return // Stop executing the rest of onCreate
        }

        binding.tvUserPassword?.text = "********"


        setupUI()
        setupEditListeners()
        setupPreferenceListeners()
        setupDrawerOpener()
        setupDrawerNavigationHeader(navView)
        setupDrawerNavigation(navView)
    }

    /**
     * Sets up generic UI actions like the back button and menu icon.
     */
    private fun setupUI() {
        binding.btnBackContainer.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.ivMenuIcon.setOnClickListener {
            binding.drawerLayout.openDrawer(binding.navView)
        }
    }

    private fun setupDrawerOpener() {
        findViewById<ImageView>(R.id.iv_menu_icon).setOnClickListener {
            binding.ivMenuIcon.setOnClickListener {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun setupDrawerNavigation(navView: NavigationView) {
        HamburgerNavigationDrawerManager.setupNavigationDrawer(this, binding.drawerLayout, navView)
    }

    private fun setupDrawerNavigationHeader(navView: NavigationView) {
        HamburgerNavigationDrawerManager.NavigationHeader(navView)
    }

    private fun setupPreferenceListeners() {
        binding.llDeleteHistory?.setOnClickListener {
            showDeleteHistoryConfirmation()
        }

        binding.llDateFormat?.setOnClickListener {
            showDateFormatSelectionDialog()
        }
    }

    /**
     * NEW: Displays a custom dialog for the user to select a date format.
     */
    private fun showDateFormatSelectionDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_select_date_format, null)

        val alertDialog = AlertDialog.Builder(this, R.style.Theme_UCRS_NoTitleDialog)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        val btnBack = dialogView.findViewById<LinearLayout>(R.id.btn_dialog_back)
        val btnSetFormat = dialogView.findViewById<MaterialButton>(R.id.btn_set_format)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.rg_date_formats)
        val rbMmddyyyy = dialogView.findViewById<RadioButton>(R.id.rb_mmddyyyy)
        val rbDdmmyyyy = dialogView.findViewById<RadioButton>(R.id.rb_ddmmyyyy)
        val rbYyyymmdd = dialogView.findViewById<RadioButton>(R.id.rb_yyyymmdd)

        val formatMap = mapOf(
            R.id.rb_mmddyyyy to DATE_FORMAT_MMDDYYYY,
            R.id.rb_ddmmyyyy to DATE_FORMAT_DDMMYYYY,
            R.id.rb_yyyymmdd to DATE_FORMAT_YYYYMMDD
        )

        when (currentDateFormat) {
            DATE_FORMAT_MMDDYYYY -> rbMmddyyyy.isChecked = true
            DATE_FORMAT_DDMMYYYY -> rbDdmmyyyy.isChecked = true
            DATE_FORMAT_YYYYMMDD -> rbYyyymmdd.isChecked = true
        }

        btnBack.setOnClickListener {
            alertDialog.dismiss()
        }

        btnSetFormat.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId

            if (selectedId != -1) {
                val newFormat = formatMap[selectedId] ?: currentDateFormat

                currentDateFormat = newFormat
                binding.tvDateFormatValue?.text = currentDateFormat
                Toast.makeText(this, "Date format set to $currentDateFormat", Toast.LENGTH_SHORT).show()
            }
            alertDialog.dismiss()
        }

        alertDialog.show()
    }


    private fun showDeleteHistoryConfirmation() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_delete, null)
        val alertDialog = AlertDialog.Builder(this, R.style.Theme_UCRS_NoTitleDialog)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        val btnBack = dialogView.findViewById<LinearLayout>(R.id.btn_dialog_back)
        val btnConfirmDelete = dialogView.findViewById<MaterialButton>(R.id.btn_confirm_delete)

        btnBack.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirmDelete.setOnClickListener {
            alertDialog.dismiss()
            Toast.makeText(this, "All history records deleted.", Toast.LENGTH_SHORT).show()
        }

        alertDialog.show()
    }

    /**
     * Helper function to start the EditConfirmationActivity. (Omitted for brevity)
     */
    private fun startEditConfirmationActivity(fieldKey: String, currentValue: String) {
        val valueToPass = when (currentValue) {
            DEFAULT_PHONE_TEXT -> ""
            DEFAULT_ADDRESS_TEXT -> ""
            else -> currentValue
        }

        val intent = Intent(this, EditConfirmationActivity::class.java).apply {
            putExtra(EditConfirmationActivity.EXTRA_FIELD_TYPE, fieldKey)
            putExtra(EditConfirmationActivity.EXTRA_CURRENT_VALUE, valueToPass)
        }
        editActivityResult.launch(intent)
    }

    /**
     * Sets up click listeners to launch the dedicated Activity. (Omitted for brevity)
     */
    private fun setupEditListeners() {
        binding.llEditEmail?.setOnClickListener {
            binding.tvUserEmail?.let { emailTextView ->
                startEditConfirmationActivity(
                    fieldKey = "Email",
                    currentValue = emailTextView.text.toString()
                )
            }
        }

        binding.llEditPhone?.setOnClickListener {
            startEditConfirmationActivity(
                fieldKey = "Phone",
                currentValue = currentPhone
            )
        }

        binding.llEditAddress?.setOnClickListener {
            startEditConfirmationActivity(
                fieldKey = "Address",
                currentValue = currentAddress
            )
        }

        binding.llEditPassword?.setOnClickListener {
            startEditConfirmationActivity(
                fieldKey = "Password",
                currentValue = actualUserPassword
            )
        }
    }

    /**
     * Updates the correct data source and TextView based on the received result. (Omitted for brevity)
     */
    private fun updateProfileField(field: String, newValue: String) {
        when (field) {
            "Email" -> binding.tvUserEmail?.text = newValue
            "Phone" -> {
                currentPhone = if (newValue.isEmpty()) DEFAULT_PHONE_TEXT else newValue
                binding.tvUserPhone?.text = currentPhone
            }
            "Address" -> {
                currentAddress = if (newValue.isEmpty()) DEFAULT_ADDRESS_TEXT else newValue
                binding.tvUserAddress?.text = currentAddress
            }
            "Password" -> {
                actualUserPassword = newValue
                binding.tvUserPassword?.text = "********"
            }
        }
    }
}