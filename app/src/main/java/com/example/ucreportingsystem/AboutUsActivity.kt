package com.example.ucreportingsystem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.example.ucreportingsystem.databinding.ActivityAboutUsBinding
import android.content.Intent
import com.example.ucreportingsystem.StudentHomeActivity
import com.example.ucreportingsystem.StudentProfileActivity
import com.example.ucreportingsystem.LoginActivity
import com.example.ucreportingsystem.StudentProfileActivity.Companion.EXTRA_LOGIN_PASSWORD

class AboutUsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutUsBinding
    private var loggedInEmail: String = "error@nodata.com"
    private var loggedInPassword: String = ""

    companion object {
        const val EXTRA_USER_EMAIL = "extra_user_email"
        const val EXTRA_LOGIN_PASSWORD = "extra_login_password"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loggedInEmail = intent.getStringExtra(EXTRA_USER_EMAIL) ?: "error@nodata.com"
        loggedInPassword = intent.getStringExtra(EXTRA_LOGIN_PASSWORD) ?: ""

        setupDrawerAndHeader()
        setupPvmToggleGroup()

        updateToggleStyles(binding.btnPurpose.id)
        displayContent(binding.btnPurpose.id)
    }

    private fun setupDrawerAndHeader() {
        binding.ivMenuIcon.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.btnBackContainer.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupNavigationDrawer()
    }

    private fun setupNavigationDrawer() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->

            val emailToPass = loggedInEmail
            val passwordToPass = loggedInPassword

            when (menuItem.itemId) {

                R.id.nav_home -> {
                    val intent = Intent(this, StudentHomeActivity::class.java).apply {
                        putExtra(EXTRA_USER_EMAIL, emailToPass)
                        putExtra(EXTRA_LOGIN_PASSWORD, passwordToPass)
                    }
                    startActivity(intent)
                }

                R.id.nav_profile -> {
                    val intent = Intent(this, StudentProfileActivity::class.java).apply {
                        putExtra(StudentProfileActivity.EXTRA_LOGIN_EMAIL, emailToPass)
                        putExtra(StudentProfileActivity.EXTRA_LOGIN_PASSWORD, passwordToPass)
                    }
                    startActivity(intent)
                }

                R.id.nav_about_us -> {
                }

                R.id.nav_logout -> {
                    val intent = Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
            }

            binding.drawerLayout.closeDrawers()
            return@setNavigationItemSelectedListener true
        }
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