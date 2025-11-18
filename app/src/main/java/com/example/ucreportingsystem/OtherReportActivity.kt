package com.example.ucreportingsystem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class OtherReportActivity : AppCompatActivity() {

    //View Declaration
    private lateinit var Title: EditText
    private lateinit var ReportDescription: EditText
    private lateinit var SubmitReportButton: Button
    private var SelectedUserLocation: String? = null
    private var SelectedReportDestination: String? = null
    //To access cloud Firestore
    val db = Firebase.firestore
    private lateinit var drawerLayout: DrawerLayout
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

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        //View Initialization
        Title = findViewById(R.id.et_report_title)
        ReportDescription = findViewById(R.id.et_report_description)
        SubmitReportButton = findViewById(R.id.btn_submit_report)
        llAttachmentsList = findViewById(R.id.ll_attachments_list)
        llUploadButtonsContainer = findViewById(R.id.ll_upload_buttons_container)
        tvOfficePlaceholder = findViewById(R.id.tv_office_placeholder)
        tvLocationPlaceholder = findViewById(R.id.tv_location_placeholder)

        setupActivityResults()
        setupDrawerOpener()
        setupDrawerNavigationHeader(navView)
        setupDrawerNavigation(navView)
        setupFormInteractions()
        refreshAttachmentsList()
        handleSubmitReport()
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

    private fun setupDrawerOpener() {
        findViewById<ImageView>(R.id.iv_menu_icon).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setupDrawerNavigation(navView: NavigationView) {
        HamburgerNavigationDrawerManager.setupNavigationDrawer(this, drawerLayout, navView)
    }

    private fun setupDrawerNavigationHeader(navView: NavigationView) {
        HamburgerNavigationDrawerManager.NavigationHeader(navView)
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
    }

    private fun handleSubmitReport() {
        SubmitReportButton.setOnClickListener {
            val reportTitle = Title.text.toString().trim()
            val reportDescription = ReportDescription.text.toString().trim()
            val selectedUserLocation = SelectedUserLocation
            val selectedReportDestination = SelectedReportDestination

            if (reportTitle.isEmpty() || reportDescription.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please complete all required fields (Title and Description).",
                    Toast.LENGTH_LONG
                ).show()
            } else if (selectedUserLocation.isNullOrEmpty() || selectedReportDestination.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a location and office.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                AddOtherReportToFirestore(
                    reportTitle,
                    reportDescription,
                    selectedUserLocation,
                    selectedReportDestination
                )
                Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, ReportConfirmationActivity::class.java).apply {
                    putExtra("reportType", "MedicalEmergencyReport")
                    putExtra("title", reportTitle)
                    putExtra("description", reportDescription)
                    putExtra("location", selectedUserLocation)
                    putExtra("office", selectedReportDestination)
                }
                startActivity(intent)
            }
        }
    }

    private fun AddOtherReportToFirestore(
        Title: String,
        IssueDescription: String,
        UserLocation: String,
        ReportDestination: String
    ){
        val OtherReportContents = hashMapOf(
            "Title" to Title,
            "IssueDescription" to IssueDescription,
            "UserLocation" to UserLocation,
            "ReportDestination" to ReportDestination
        )
        db.collection("OtherReports")
            .add(OtherReportContents)
    }

    private fun showLocationSelectionDialog() {
        val rooms = listOf("M301", "M302", "M303", "M304", "M305", "M306", "M307")

        val dialog = RoomSelectionDialogFragment(rooms) { selectedItem ->
            SelectedUserLocation = selectedItem
            tvLocationPlaceholder.text = selectedItem
            tvLocationPlaceholder.setTextColor(ContextCompat.getColor(this, R.color.black))
            Toast.makeText(this, "Selected Location: $selectedItem", Toast.LENGTH_SHORT).show()
        }
        dialog.show(supportFragmentManager, "LOCATION_SELECTOR_TAG")
    }

    private fun showOfficeSelectionDialog() {
        val offices = listOf("CITCS", "COA", "CON", "CEA", "CTE", "CAS", "CHTM", "COL", "CBA")

        val dialog = RoomSelectionDialogFragment(offices) { selectedItem ->
            SelectedReportDestination = selectedItem
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