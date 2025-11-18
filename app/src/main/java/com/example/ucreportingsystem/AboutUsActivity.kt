package com.example.ucreportingsystem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.example.ucreportingsystem.databinding.ActivityAboutUsBinding
import android.widget.ImageView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView


class AboutUsActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)


        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPvmToggleGroup()
        setupDrawerOpener()
        setupDrawerNavigationHeader(navView)
        setupDrawerNavigation(navView)

        updateToggleStyles(binding.btnPurpose.id)
        displayContent(binding.btnPurpose.id)
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

    private fun setupPvmToggleGroup() {
        binding.togglePvm.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                updateToggleStyles(checkedId)
                displayContent(checkedId)
            }
        }
    }

    /**
     * Updates the background and text color of the PVM buttons based on the currently checked button.
     */
    private fun updateToggleStyles(checkedId: Int) {
        val buttons = listOf(binding.btnPurpose, binding.btnVision, binding.btnMission)
        val activeBgColor = ContextCompat.getColorStateList(this, R.color.forest_green)
        val activeTextColor = ContextCompat.getColor(this, R.color.white)
        val inactiveBgColor = ContextCompat.getColorStateList(this, R.color.ashy_cyan)
        val inactiveTextColor = ContextCompat.getColor(this, R.color.uc_green)

        buttons.forEach { button ->
            if (button.id == checkedId) {
                button.setBackgroundTintList(activeBgColor)
                button.setTextColor(activeTextColor)
            } else {
                button.setBackgroundTintList(inactiveBgColor)
                button.setTextColor(inactiveTextColor)
            }
        }
    }

    /**
     * Updates the dynamic title and content TextViews based on the checked button ID.
     */
    private fun displayContent(checkedId: Int) {
        val title: String
        val content: String

        when (checkedId) {
            R.id.btn_purpose -> {
                title = "Our Purpose"
                content = "To enable students to efficiently report their concerns through a reliable digital platform."
            }
            R.id.btn_vision -> {
                title = "Our Vision"
                content = "To foster a secure and responsive academic environment through seamless communication."
            }
            R.id.btn_mission -> {
                title = "Our Mission"
                content = "To provide students with a swift and accessible reporting system that ensures immediate attention and responsive action to their concerns."
            }
            else -> {
                title = ""
                content = ""
            }
        }

        binding.tvDynamicTitle.text = title
        binding.tvDynamicContent.text = content
    }
}