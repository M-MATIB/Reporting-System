package com.example.ucreportingsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class StudentHomeActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    lateinit var scanBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_home)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        // Display User Name in Student Home Page
        val studentNameTextView = findViewById<TextView>(R.id.tv_student_name)
        studentNameTextView.text = UserRepository.currentUser?.email ?: "Student"

        //get the header view from the NavigationView
        val headerView = navView.getHeaderView(0) // Get the first (and only) header
        val navHeaderName: TextView = headerView.findViewById(R.id.tv_nav_header_name)
        val navHeaderId: TextView = headerView.findViewById(R.id.tv_nav_header_id)

        // Display User Name in Navigation Header
        navHeaderName.text = UserRepository.currentUser?.email ?: "Student Name"
        navHeaderId.text = UserRepository.currentUser?.password ?: "student.email@example.com"

        setupDrawerOpener()
        setupReportButtons()
        setupBottomNavigation()
        setupDrawerNavigation(navView)
        setupOnBackPressed()

        scanBtn = findViewById(R.id.btn_quick_report_fab_like)
        scanBtn.setOnClickListener {
            val options = ScanOptions()
            options.setPrompt("Scan a Any QR Code")
            options.setBeepEnabled(true)
            options.setOrientationLocked(true)
            options.setCaptureActivity(CaptureActivity::class.java)
            barcodeLauncher.launch(options)
        }
    }

    // For Drawer Navigation to work
    private fun setupDrawerOpener() {
        findViewById<ImageView>(R.id.iv_menu_icon).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setupReportButtons() {
        findViewById<LinearLayout>(R.id.btn_incident_report).setOnClickListener {
            Toast.makeText(this, "Starting Incident Report Flow...", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, IncidentReportActivity::class.java).apply {
            }
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btn_medical_emergency).setOnClickListener {
            Toast.makeText(this, "Starting Medical Emergency Report Flow...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MedicalEmergencyActivity::class.java).apply {
            }
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btn_missing_broken).setOnClickListener {
            Toast.makeText(this, "Starting Missing/Broken Report Flow...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MissingBrokenItemActivity::class.java).apply {
            }
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btn_other_reports).setOnClickListener {
            Toast.makeText(this, "Starting Other Reports Flow...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, OtherReportActivity::class.java).apply {
            }
            startActivity(intent)
        }
    }

    private fun setupBottomNavigation() {
        findViewById<MaterialButton>(R.id.btn_quick_report_fab_like).setOnClickListener {
            Toast.makeText(this, "Launching Quick Report (QR Scan)!", Toast.LENGTH_LONG).show()
        }

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_report_history -> {
                    val intent = Intent(this, ReportHistoryActivity::class.java).apply {
                    }
                    startActivity(intent)
                    true
                }
                R.id.nav_notifications -> {
                    val intent = Intent(this, NotificationActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_placeholder -> {
                    false
                }
                else -> false
            }
        }
    }

    private fun setupDrawerNavigation(navView: NavigationView) {
        HamburgerNavigationDrawerManager.setupNavigationDrawer(this, drawerLayout, navView)
    }

    private fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        })
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()){ result ->
        if (result.contents != null){
            Toast.makeText(this, "Scanned Result: ${result.contents}", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

}