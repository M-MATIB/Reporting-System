package com.example.ucreportingsystem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import java.util.UUID
import com.example.ucreportingsystem.StudentProfileActivity.Companion.EXTRA_LOGIN_EMAIL
import com.example.ucreportingsystem.StudentProfileActivity.Companion.EXTRA_LOGIN_PASSWORD

class ReportConfirmationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var loggedInEmail: String = "error@nodata.com"
    private var loggedInPassword: String = ""

    companion object {
        const val EXTRA_USER_EMAIL = "extra_user_email"
        const val EXTRA_LOGIN_PASSWORD = "extra_login_password"
    }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var tvReportId: TextView
    private lateinit var tvReportType: TextView
    private lateinit var tvTitleSummary: TextView
    private lateinit var tvDescriptionContent: TextView
    private lateinit var tvLocationArea: TextView
    private lateinit var tvTargetOffice: TextView
    private lateinit var llAttachmentContainer: LinearLayout
    private lateinit var vAttachmentSeparator: View
    private lateinit var tvSubmittedOnDateTime: TextView
    private var receivedAttachmentUris: ArrayList<Uri> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_confirmation)

        loggedInEmail = intent.getStringExtra(EXTRA_USER_EMAIL) ?: "error@nodata.com"
        loggedInPassword = intent.getStringExtra(EXTRA_LOGIN_PASSWORD) ?: ""

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val menuIcon: ImageView = findViewById(R.id.iv_menu_icon)
        val btnBackToHomepage: MaterialButton = findViewById(R.id.btn_back_to_homepage)

        tvReportId = findViewById(R.id.tv_report_id)
        tvReportType = findViewById(R.id.tv_report_type)
        tvTitleSummary = findViewById(R.id.tv_title_summary)
        tvDescriptionContent = findViewById(R.id.tv_description_content)
        tvLocationArea = findViewById(R.id.tv_location_area)
        tvTargetOffice = findViewById(R.id.tv_target_office)
        llAttachmentContainer = findViewById(R.id.ll_attachment_container)
        vAttachmentSeparator = findViewById(R.id.v_attachment_separator)
        tvSubmittedOnDateTime = findViewById(R.id.tv_submitted_on_datetime)


        navView.setNavigationItemSelectedListener(this)
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        populateReportDetails(intent)

        btnBackToHomepage.setOnClickListener {
            val intent = Intent(this, StudentHomeActivity::class.java).apply {
                putExtra(EXTRA_USER_EMAIL, loggedInEmail)
                putExtra(EXTRA_LOGIN_PASSWORD, loggedInPassword)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
        }

        setupOnBackPressed()
    }

    private fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                finish()
            }
        }
    }

    private fun populateReportDetails(intent: Intent) {
        tvReportId.text = generateUniqueReportId()

        val reportType = intent.getStringExtra("EXTRA_REPORT_TYPE") ?: "Incident Report"
        tvReportType.text = reportType

        val reportTitle = intent.getStringExtra("EXTRA_TITLE")
        val reportDescription = intent.getStringExtra("EXTRA_DESCRIPTION")
        val reportLocation = intent.getStringExtra("EXTRA_LOCATION")
        val targetOffice = intent.getStringExtra("EXTRA_OFFICE")
        val submissionDate = intent.getStringExtra("EXTRA_SUBMISSION_DATE")
        val submissionTime = intent.getStringExtra("EXTRA_SUBMISSION_TIME")

        @Suppress("DEPRECATION")
        receivedAttachmentUris = intent.getParcelableArrayListExtra("EXTRA_ATTACHMENT_URIS") ?: ArrayList()

        tvTitleSummary.text = reportTitle
        tvDescriptionContent.text = reportDescription
        tvLocationArea.text = reportLocation
        tvTargetOffice.text = targetOffice

        if (!submissionDate.isNullOrEmpty() && !submissionTime.isNullOrEmpty()) {
            tvSubmittedOnDateTime.text = "$submissionDate\n$submissionTime"
        } else {
            tvSubmittedOnDateTime.text = "Error\nRetrieving Time"
        }

        if (receivedAttachmentUris.isEmpty()) {
            llAttachmentContainer.visibility = View.GONE
            vAttachmentSeparator.visibility = View.GONE
        } else {
            llAttachmentContainer.visibility = View.VISIBLE
            vAttachmentSeparator.visibility = View.VISIBLE
            llAttachmentContainer.removeAllViews()

            receivedAttachmentUris.forEach { uri ->
                val fileName = getFileName(uri) ?: "Unknown File"
                val attachmentView = createAttachmentView(fileName, uri)
                llAttachmentContainer.addView(attachmentView)
            }
        }
    }

    private fun createAttachmentView(fileName: String, uri: Uri): LinearLayout {
        val context = this
        val marginPx = (15 * resources.displayMetrics.density).toInt()

        val container = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = marginPx }
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER

            background = ContextCompat.getDrawable(context, R.drawable.rounded_yellow)
            setPadding(15, 15, 15, 15)

            setOnClickListener { viewAttachment(uri) }
        }

        val icon = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                (35 * resources.displayMetrics.density).toInt(),
                (35 * resources.displayMetrics.density).toInt()
            )
            setImageResource(R.drawable.file_image_icon)
            contentDescription = "Attachment Preview"
            setColorFilter(ContextCompat.getColor(context, R.color.black))
        }
        container.addView(icon)

        val previewTextView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = "Attachment Preview"
            setTextColor(ContextCompat.getColor(context, R.color.black))
            textSize = 16f
            setPadding(0, 10, 0, 0)
        }
        container.addView(previewTextView)

        val nameTextView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = fileName
            setTextColor(ContextCompat.getColor(context, R.color.black))
            textSize = 12f
        }
        container.addView(nameTextView)

        return container
    }

    /**
     * Robust file viewing logic with a fallback to a generic MIME type.
     */

    private fun viewAttachment(uri: Uri) {
        if (uri.scheme == "placeholder") {
            Toast.makeText(this, "Cannot open placeholder image: ${getFileName(uri)}", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW)

            val specificMimeType = contentResolver.getType(uri) ?: "*/*"

            intent.setDataAndType(uri, specificMimeType)

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {

                intent.setDataAndType(uri, "*/*")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {

                    Toast.makeText(this, "No application found to view files.", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Log.e("ViewAttachment", "Error opening file: ${e.message}", e)
            Toast.makeText(this, "Error opening file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun generateUniqueReportId(): String {
        val uuid = UUID.randomUUID().toString()
        val shortId = uuid.substring(0, 8).uppercase()
        return "RPT-$shortId"
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)

        val emailToPass = loggedInEmail
        val passwordToPass = loggedInPassword

        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, StudentHomeActivity::class.java).apply {
                    putExtra(EXTRA_USER_EMAIL, emailToPass)
                    putExtra(EXTRA_LOGIN_PASSWORD, passwordToPass)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
                return true
            }
            R.id.nav_profile -> {
                val intent = Intent(this, StudentProfileActivity::class.java).apply {
                    putExtra(EXTRA_LOGIN_EMAIL, emailToPass)
                    putExtra(EXTRA_LOGIN_PASSWORD, passwordToPass)
                }
                startActivity(intent)
                return true
            }
            R.id.nav_about_us -> {
                val intent = Intent(this, AboutUsActivity::class.java).apply {
                    putExtra(EXTRA_USER_EMAIL, emailToPass)
                    putExtra(EXTRA_LOGIN_PASSWORD, passwordToPass)
                }
                startActivity(intent)
                return true
            }
            R.id.nav_logout -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                return true
            }
        }

        return false
    }
}