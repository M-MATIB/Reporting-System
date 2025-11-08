package com.example.ucreportingsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView

class StaffHomepage : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_staff_homepage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        setupDrawerOpener()

        val New_Report = findViewById<Button>(R.id. btn_NewReports)
        New_Report.setOnClickListener {
            Toast.makeText(this, "Navigate to New Reports", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Admin_NewReports ::class.java)
            startActivity(intent)
        }

        val Pending_Report = findViewById<Button>(R.id.btn_Pending)
        Pending_Report.setOnClickListener {
            Toast.makeText(this, "Navigate to Pending Reports", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Admin_PendingReports ::class.java)
            startActivity(intent)
        }

        val InProgress_Report = findViewById<Button>(R.id.btn_InProgress)
        InProgress_Report.setOnClickListener {
            Toast.makeText(this, "Navigate to In Progress Reports", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Admin_InProgressReports ::class.java)
            startActivity(intent)
        }

        val Resolved_Report = findViewById<Button>(R.id.btn_Resolved)
        Resolved_Report.setOnClickListener {
            Toast.makeText(this, "Navigate to Resolved Reports", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Admin_ResolvedReports ::class.java)
            startActivity(intent)
        }
    }

    private fun setupDrawerOpener() {
        findViewById<ImageView>(R.id.iv_menu_icon).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

}