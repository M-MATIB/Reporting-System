package com.example.ucreportingsystem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.UUID
import java.util.Date
import java.util.Locale


class ReportConfirmationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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
    private lateinit var tvSubmittedBy: TextView
    private var receivedAttachmentUris: ArrayList<Uri> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_confirmation)

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
        tvSubmittedBy = findViewById(R.id.tv_submitted_by)

        navView.setNavigationItemSelectedListener(this)
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        populateReportDetailsFromIntent()

        btnBackToHomepage.setOnClickListener {
            val intent = Intent(this, StudentHomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
        }

        setupOnBackPressed()
    }

    private fun populateReportDetailsFromIntent() {
        tvReportId.text = generateUniqueReportId()

        val reportType = intent.getStringExtra("reportType")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val location = intent.getStringExtra("location")
        val office = intent.getStringExtra("office")

        // 1. Access the UserRepository to get the current user's email
        val currentUserEmail = UserRepository.currentUser?.email

        // 2. Set the text of the tv_submitted_by TextView
        //    It's good practice to provide a fallback text in case the user is null
        tvSubmittedBy.text = currentUserEmail ?: "Unknown User"

        tvReportType.text = when (reportType) {
            "MedicalEmergencyReport" -> "Medical Emergency Report"
            "IncidentReport" -> "Incident Report"
            else -> "Report"
        }

        tvTitleSummary.text = title
        tvDescriptionContent.text = description
        tvLocationArea.text = location
        tvTargetOffice.text = office

        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
        val now = Date()
        tvSubmittedOnDateTime.text = "${dateFormat.format(now)}\n${timeFormat.format(now)}"

        // Hide attachment section as it's not being passed
        llAttachmentContainer.visibility = View.GONE
        vAttachmentSeparator.visibility = View.GONE
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

    private fun generateUniqueReportId(): String {
        val uuid = UUID.randomUUID().toString()
        val shortId = uuid.substring(0, 8).uppercase()
        return "RPT-$shortId"
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)

        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, StudentHomeActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
                return true
            }
            R.id.nav_profile -> {
                val intent = Intent(this, StudentProfileActivity::class.java).apply {
                }
                startActivity(intent)
                return true
            }
            R.id.nav_about_us -> {
                val intent = Intent(this, AboutUsActivity::class.java).apply {
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