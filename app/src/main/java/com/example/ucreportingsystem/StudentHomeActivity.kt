package com.example.ucreportingsystem

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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

class StudentHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    lateinit var scanBtn : Button

    companion object {
        const val EXTRA_USER_EMAIL = "extra_user_email"
    }

    private var loggedInEmail: String = ""
    private var loggedInPassword: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_home)

        drawerLayout = findViewById(R.id.drawer_layout)

        loggedInEmail = intent.getStringExtra(StudentProfileActivity.EXTRA_LOGIN_EMAIL) ?: ""
        loggedInPassword = intent.getStringExtra(StudentProfileActivity.EXTRA_LOGIN_PASSWORD) ?: ""

        setupDrawerOpener()
        setupReportButtons()
        setupBottomNavigation()
        setupDrawerNavigation()
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

    private fun setupDrawerOpener() {
        findViewById<ImageView>(R.id.iv_menu_icon).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setupReportButtons() {
        findViewById<LinearLayout>(R.id.btn_incident_report).setOnClickListener {
            Toast.makeText(this, "Starting Incident Report Flow...", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, IncidentReportActivity::class.java).apply {
                putExtra(EXTRA_USER_EMAIL, loggedInEmail)
            }
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btn_medical_emergency).setOnClickListener {
            Toast.makeText(this, "Starting Medical Emergency Report Flow...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MedicalEmergencyActivity::class.java).apply {
                putExtra(EXTRA_USER_EMAIL, loggedInEmail)
            }
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btn_missing_broken).setOnClickListener {
            Toast.makeText(this, "Starting Missing/Broken Report Flow...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MissingBrokenItemActivity::class.java).apply {
                putExtra(EXTRA_USER_EMAIL, loggedInEmail)
            }
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btn_other_reports).setOnClickListener {
            Toast.makeText(this, "Starting Other Reports Flow...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, OtherReportActivity::class.java).apply {
                putExtra(EXTRA_USER_EMAIL, loggedInEmail)
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
                        putExtra(EXTRA_USER_EMAIL, loggedInEmail)
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

    private fun setupDrawerNavigation() {
        val navView: NavigationView = findViewById(R.id.nav_view)

        navView.setNavigationItemSelectedListener(this::onNavigationItemSelected)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_home -> Toast.makeText(this, "You are on the Home screen.", Toast.LENGTH_SHORT).show()

            R.id.nav_profile -> {
                val intent = Intent(this, StudentProfileActivity::class.java).apply {
                    putExtra(StudentProfileActivity.EXTRA_LOGIN_EMAIL, loggedInEmail)
                    putExtra(StudentProfileActivity.EXTRA_LOGIN_PASSWORD, loggedInPassword)
                }
                startActivity(intent)
            }

            R.id.nav_about_us -> {
                val intent = Intent(this, AboutUsActivity::class.java).apply {
                    putExtra(EXTRA_USER_EMAIL, loggedInEmail)
                }
                startActivity(intent)
            }

            R.id.nav_settings -> {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
            }


            R.id.nav_logout -> {
                Toast.makeText(this, "Logging out...", Toast.LENGTH_LONG).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
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