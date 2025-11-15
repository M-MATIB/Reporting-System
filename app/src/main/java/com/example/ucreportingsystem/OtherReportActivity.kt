package com.example.ucreportingsystem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.TypedValue
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class OtherReportActivity : AppCompatActivity() {

    private var loggedInEmail: String = "error@nodata.com"
    private var loggedInPassword: String = ""

    companion object {
        const val EXTRA_USER_EMAIL = "extra_user_email"
        const val EXTRA_LOGIN_PASSWORD = "extra_login_password"
    }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var etReportTitle: EditText
    private lateinit var etReportDescription: EditText
    private lateinit var tvOfficePlaceholder: TextView
    private lateinit var tvLocationPlaceholder: TextView
    private lateinit var llAttachmentsList: LinearLayout
    private lateinit var llUploadButtonsContainer: LinearLayout
    private var attachedFiles: MutableList<Uri> = mutableListOf()
    private lateinit var capturePhotoLauncher: ActivityResultLauncher<Intent>
    private lateinit var uploadFileLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_report)

        loggedInEmail = intent.getStringExtra(EXTRA_USER_EMAIL) ?: "error@nodata.com"
        loggedInPassword = intent.getStringExtra(EXTRA_LOGIN_PASSWORD) ?: ""
        drawerLayout = findViewById(R.id.drawer_layout)
        tvLocationPlaceholder = findViewById(R.id.tv_location_placeholder)
        llAttachmentsList = findViewById(R.id.ll_attachments_list)
        llUploadButtonsContainer = findViewById(R.id.ll_upload_buttons_container)

        etReportTitle = findViewById(R.id.et_report_title)
        etReportDescription = findViewById(R.id.et_report_description)
        tvOfficePlaceholder = findViewById(R.id.tv_office_placeholder)

        setupActivityResults()
        setupTopBarAndNavigation()
        setupFormInteractions()
        refreshAttachmentsList()
    }

    private fun setupActivityResults() {
        capturePhotoLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val dummyName = "Captured Photo ${attachedFiles.size + 1}.jpg"
                attachedFiles.add(Uri.parse("placeholder:///$dummyName"))
                refreshAttachmentsList()
                Toast.makeText(this, "Photo Captured Successfully! Total: ${attachedFiles.size}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Photo capture cancelled.", Toast.LENGTH_SHORT).show()
            }
        }

        uploadFileLauncher = registerForActivityResult(
            ActivityResultContracts.OpenMultipleDocuments()
        ) { uris: List<Uri>? ->
            if (uris != null && uris.isNotEmpty()) {
                attachedFiles.addAll(uris)
                refreshAttachmentsList()
                Toast.makeText(this, "${uris.size} files added. Total: ${attachedFiles.size}", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "File selection cancelled.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupTopBarAndNavigation() {
        findViewById<ImageView>(R.id.iv_menu_icon).setOnClickListener {
            drawerLayout.openDrawer(findViewById<NavigationView>(R.id.nav_view))
        }

        findViewById<LinearLayout>(R.id.btn_back_container)?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val navView: NavigationView = findViewById(R.id.nav_view)

        val emailToPass = loggedInEmail
        val passwordToPass = loggedInPassword

        navView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawers()

            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, StudentHomeActivity::class.java).apply {
                        putExtra(EXTRA_USER_EMAIL, emailToPass)
                        putExtra(EXTRA_LOGIN_PASSWORD, passwordToPass)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, StudentProfileActivity::class.java).apply {
                        //putExtra(EXTRA_LOGIN_EMAIL, emailToPass)
                        putExtra(EXTRA_LOGIN_PASSWORD, passwordToPass)
                    }
                    startActivity(intent)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_about_us -> {
                    val intent = Intent(this, AboutUsActivity::class.java).apply {
                        putExtra(EXTRA_USER_EMAIL, emailToPass)
                        putExtra(EXTRA_LOGIN_PASSWORD, passwordToPass)
                    }
                    startActivity(intent)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_logout -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    return@setNavigationItemSelectedListener true
                }
            }
            true
        }
    }

    private fun setupFormInteractions() {
        findViewById<LinearLayout>(R.id.btn_select_location)?.setOnClickListener {
            showLocationSelectionDialog()
        }

        findViewById<LinearLayout>(R.id.btn_select_office)?.setOnClickListener {
            showOfficeSelectionDialog()
        }

        findViewById<MaterialButton>(R.id.btn_capture_photo)?.setOnClickListener {
            launchCameraIntent()
        }

        findViewById<MaterialButton>(R.id.btn_upload_file)?.setOnClickListener {
            launchFilePickerIntent()
        }

        findViewById<MaterialButton>(R.id.btn_submit_report)?.setOnClickListener {
            handleSubmitReport()
        }
    }

    private fun handleSubmitReport() {
        val reportTitle = etReportTitle.text?.toString()?.trim() ?: ""
        val reportDescription = etReportDescription.text?.toString()?.trim() ?: ""
        val reportLocation = tvLocationPlaceholder.text.toString()
        val targetOffice = tvOfficePlaceholder.text.toString()
        val defaultLocationText = "Room #"
        val defaultOfficeText = "Select Office/Department"

        if (reportTitle.isBlank() || reportDescription.isBlank() || reportLocation == defaultLocationText || targetOffice == defaultOfficeText) {
            Toast.makeText(this, "Please complete all required fields (Title, Description, Location, and Office).", Toast.LENGTH_LONG).show()
            return
        }

        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
        val submissionDate = dateFormat.format(Date())
        val submissionTime = timeFormat.format(Date())

        val intent = Intent(this, ReportConfirmationActivity::class.java)

        intent.putExtra("EXTRA_REPORT_TYPE", "Other Report")

        intent.putExtra("EXTRA_TITLE", reportTitle)
        intent.putExtra("EXTRA_DESCRIPTION", reportDescription)
        intent.putExtra("EXTRA_LOCATION", reportLocation)
        intent.putExtra("EXTRA_OFFICE", targetOffice)
        intent.putExtra("EXTRA_SUBMISSION_DATE", submissionDate)
        intent.putExtra("EXTRA_SUBMISSION_TIME", submissionTime)
        intent.putExtra(ReportConfirmationActivity.EXTRA_USER_EMAIL, loggedInEmail)
        intent.putExtra(ReportConfirmationActivity.EXTRA_LOGIN_PASSWORD, loggedInPassword)
        intent.putParcelableArrayListExtra("EXTRA_ATTACHMENT_URIS", ArrayList(attachedFiles))
        startActivity(intent)
    }

    private fun showLocationSelectionDialog() {
        val rooms = listOf("M301", "M302", "M303", "M304", "M305", "M306", "M307")

        val dialog = RoomSelectionDialogFragment(rooms) { selectedItem ->
            tvLocationPlaceholder.text = selectedItem
            tvLocationPlaceholder.setTextColor(ContextCompat.getColor(this, R.color.black))
            Toast.makeText(this, "Selected Location: $selectedItem", Toast.LENGTH_SHORT).show()
        }
        dialog.show(supportFragmentManager, "LOCATION_SELECTOR_TAG")
    }

    private fun showOfficeSelectionDialog() {
        val offices = listOf("CITCS", "COA", "CON", "CEA", "CTE", "CAS", "CHTM", "COL", "CBA")

        val dialog = RoomSelectionDialogFragment(offices) { selectedItem ->
            tvOfficePlaceholder.text = selectedItem
            tvOfficePlaceholder.setTextColor(ContextCompat.getColor(this, R.color.black))
            Toast.makeText(this, "Selected Office: $selectedItem", Toast.LENGTH_SHORT).show()
        }
        dialog.show(supportFragmentManager, "OFFICE_SELECTOR_TAG")
    }

    private fun launchCameraIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            capturePhotoLauncher.launch(takePictureIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "No camera app found.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchFilePickerIntent() {
        uploadFileLauncher.launch(arrayOf("image/*", "application/pdf"))
    }

    private fun refreshAttachmentsList() {
        llAttachmentsList.removeAllViews()

        if (attachedFiles.isEmpty()) {
            return
        }

        attachedFiles.forEachIndexed { index, uri ->
            val fileName = getFileName(uri) ?: "Unknown File ($index)"
            val itemView = createAttachmentItemView(fileName, index)
            llAttachmentsList.addView(itemView)
        }
    }

    private fun createAttachmentItemView(fileName: String, index: Int): LinearLayout {
        val context = this
        val marginPx = (8 * resources.displayMetrics.density).toInt()

        val container = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = marginPx }
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            background = ContextCompat.getDrawable(context, R.drawable.rounded_edittext_green_border)
            setPadding(12, 12, 12, 12)
        }

        val fileIcon = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(34, 34).apply { marginEnd = 8 }
            setImageResource(R.drawable.file_image_icon)
            setColorFilter(ContextCompat.getColor(context, R.color.uc_green))
            contentDescription = "File Icon"
        }
        container.addView(fileIcon)

        val nameTextView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
            text = fileName
            setTextColor(ContextCompat.getColor(context, R.color.uc_green))
            textSize = 14f
        }
        container.addView(nameTextView)

        val removeButton = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(38, 38)
            setImageResource(R.drawable.close_icon)
            setColorFilter(ContextCompat.getColor(context, R.color.uc_green))
            contentDescription = "Remove File"
            isClickable = true
            isFocusable = true

            val typedValue = TypedValue()
            if (context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, typedValue, true)) {
                background = ContextCompat.getDrawable(context, typedValue.resourceId)
            } else {
                background = null
            }

            setOnClickListener {
                removeFileAtIndex(index)
            }
        }
        container.addView(removeButton)

        return container
    }

    private fun removeFileAtIndex(index: Int) {
        if (index >= 0 && index < attachedFiles.size) {
            val fileName = getFileName(attachedFiles[index])
            attachedFiles.removeAt(index)
            refreshAttachmentsList()

            val remainingCount = attachedFiles.size
            if (remainingCount > 0) {
                Toast.makeText(this, "Removed: $fileName. $remainingCount remaining.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Removed: $fileName. All attachments cleared.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        if (uri.scheme == "placeholder") {
            return uri.pathSegments.lastOrNull()
        }

        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        result = it.getString(nameIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1 && cut != null) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }
}