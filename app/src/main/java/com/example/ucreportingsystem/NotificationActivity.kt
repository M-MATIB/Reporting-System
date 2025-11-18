package com.example.ucreportingsystem

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView

class NotificationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: NotificationAdapter
    private lateinit var allNotifications: List<Notification>
    private lateinit var searchEditText: EditText
    private var activeFilterButton: MaterialButton? = null
    private var activeFilterButtonId: Int = R.id.btn_status_all_filter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_notifications)
        allNotifications = createSampleNotifications()
        adapter = NotificationAdapter(this, allNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        setupNavigationAndFiltering()
        setupDrawerOpener()
        setupDrawerNavigationHeader(navView)
        setupDrawerNavigation(navView)

    }

    private fun setupNavigationAndFiltering() {
        findViewById<LinearLayout>(R.id.btn_back_container).setOnClickListener {
            finish()
        }

        searchEditText = findViewById(R.id.et_search_notifications)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterNotifications(s.toString(), activeFilterButtonId)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val allFilterButton: MaterialButton = findViewById(R.id.btn_status_all_filter)
        val publicButton: MaterialButton = findViewById(R.id.btn_status_public)
        val pendingButton: MaterialButton = findViewById(R.id.btn_status_pending)
        val inProgressButton: MaterialButton = findViewById(R.id.btn_status_in_progress)
        val resolvedButton: MaterialButton = findViewById(R.id.btn_status_resolved)

        allFilterButton.setOnClickListener { handleStatusFilterClick(allFilterButton, null) }
        publicButton.setOnClickListener { handleStatusFilterClick(publicButton, NotificationStatus.PUBLIC) }
        pendingButton.setOnClickListener { handleStatusFilterClick(pendingButton, NotificationStatus.PENDING) }
        inProgressButton.setOnClickListener { handleStatusFilterClick(inProgressButton, NotificationStatus.IN_PROGRESS) }
        resolvedButton.setOnClickListener { handleStatusFilterClick(resolvedButton, NotificationStatus.RESOLVED) }

        activeFilterButton = allFilterButton
        updateButtonState(activeFilterButton!!)
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

    private fun handleStatusFilterClick(selectedButton: MaterialButton, status: NotificationStatus?) {
        activeFilterButtonId = selectedButton.id
        activeFilterButton = selectedButton
        updateButtonState(selectedButton)

        filterNotifications(searchEditText.text.toString(), activeFilterButtonId, status)
    }

    private fun filterNotifications(query: String, filterButtonId: Int, status: NotificationStatus? = null) {
        val filteredList = allNotifications.filter { notification ->

            val matchesStatus = when (filterButtonId) {
                R.id.btn_status_all_filter -> true
                R.id.btn_status_public -> notification.status == NotificationStatus.PUBLIC
                else -> notification.status == status
            }

            val normalizedQuery = query.trim().lowercase()
            val matchesQuery = if (normalizedQuery.isBlank()) {
                true
            } else {
                notification.category.lowercase().contains(normalizedQuery) ||
                        notification.description.lowercase().contains(normalizedQuery) ||
                        notification.id.lowercase().contains(normalizedQuery)
            }

            matchesStatus && matchesQuery
        }

        adapter.updateList(filteredList)
    }

    private fun updateButtonState(selectedButton: MaterialButton) {

        val buttons = listOf<MaterialButton>(
            findViewById(R.id.btn_status_all_filter),
            findViewById(R.id.btn_status_public),
            findViewById(R.id.btn_status_pending),
            findViewById(R.id.btn_status_in_progress),
            findViewById(R.id.btn_status_resolved)
        )

        val ucGreen = ContextCompat.getColor(this, R.color.uc_green)
        val white = ContextCompat.getColor(this, R.color.white)
        val black = ContextCompat.getColor(this, R.color.black)

        val allBgColor = white
        val allTextColor = black
        val allStrokeColor = ContextCompat.getColorStateList(this, R.color.uc_green)

        val publicBgColor = ContextCompat.getColor(this, R.color.public_bg)
        val publicTextColor = ContextCompat.getColor(this, R.color.public_text)
        val publicStrokeColor = ContextCompat.getColorStateList(this, R.color.public_text)

        val pendingBgColor = ContextCompat.getColor(this, R.color.pending_bg)
        val pendingTextColor = ContextCompat.getColor(this, R.color.pending)
        val pendingStrokeColor = ContextCompat.getColorStateList(this, R.color.pending)

        val inProgressBgColor = ContextCompat.getColor(this, R.color.in_progress_bg)
        val inProgressTextColor = ContextCompat.getColor(this, R.color.in_progress)
        val inProgressStrokeColor = ContextCompat.getColorStateList(this, R.color.in_progress)

        val resolvedBgColor = ContextCompat.getColor(this, R.color.resolved_bg)
        val resolvedTextColor = ContextCompat.getColor(this, R.color.resolved)
        val resolvedStrokeColor = ContextCompat.getColorStateList(this, R.color.resolved)

        for (button in buttons) {
            button.strokeWidth = 4

            when (button.id) {
                R.id.btn_status_public -> {
                    button.setBackgroundColor(publicBgColor)
                    button.setTextColor(publicTextColor)
                    button.strokeColor = publicStrokeColor
                }
                R.id.btn_status_pending -> {
                    button.setBackgroundColor(pendingBgColor)
                    button.setTextColor(pendingTextColor)
                    button.strokeColor = pendingStrokeColor
                }
                R.id.btn_status_in_progress -> {
                    button.setBackgroundColor(inProgressBgColor)
                    button.setTextColor(inProgressTextColor)
                    button.strokeColor = inProgressStrokeColor
                }
                R.id.btn_status_resolved -> {
                    button.setBackgroundColor(resolvedBgColor)
                    button.setTextColor(resolvedTextColor)
                    button.strokeColor = resolvedStrokeColor
                }
                R.id.btn_status_all_filter -> {
                    button.setBackgroundColor(allBgColor)
                    button.setTextColor(allTextColor)
                    button.strokeColor = allStrokeColor
                }
            }
        }

        selectedButton.setBackgroundColor(ucGreen)
        selectedButton.setTextColor(white)
        selectedButton.strokeColor = allStrokeColor
    }


    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val intent: Intent

        when (menuItem.itemId) {
            R.id.nav_home -> {
                intent = Intent(this, StudentHomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }

            R.id.nav_profile -> {
                intent = Intent(this, StudentProfileActivity::class.java).apply {
                    //putExtra(StudentProfileActivity.EXTRA_LOGIN_EMAIL, loggedInEmail)
                    //putExtra(StudentProfileActivity.EXTRA_LOGIN_PASSWORD, loggedInPassword)
                }
                startActivity(intent)
            }

            R.id.nav_about_us -> {
                intent = Intent(this, AboutUsActivity::class.java).apply {
                    //putExtra(StudentHomeActivity.EXTRA_USER_EMAIL, loggedInEmail)
                }
                startActivity(intent)
            }

            R.id.nav_logout -> {
                Toast.makeText(this, "Logging out...", Toast.LENGTH_LONG).show()
                intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun createSampleNotifications(): List<Notification> {
        return listOf(
            Notification(id = "1", status = NotificationStatus.PUBLIC, category = "New Public Report", description = "Jane Doe Posted: “Fallen tree along camp 7 road”", timestamp = "just now"),
            Notification(id = "2", status = NotificationStatus.PENDING, category = "Report Successfully Filed: RPT-12345", description = "Status: Pending. Thank you for submitting 'Missing Mouse and Keyboard'.", timestamp = "5 minutes ago"),
            Notification(id = "3", status = NotificationStatus.IN_PROGRESS, category = "Status: In Progress", description = "Your report 'RPT-9876' is currently being processed by the facilities department.", timestamp = "1 hour ago"),
            Notification(id = "4", status = NotificationStatus.RESOLVED, category = "Status: Resolved", description = "Your report 'RPT-5432' concerning the broken water fountain has been closed and is complete.", timestamp = "Yesterday"),
            Notification(id = "5", status = NotificationStatus.PUBLIC, category = "Campus Alert", description = "The main lobby is temporarily closed due to maintenance.", timestamp = "1 day ago")
        )
    }
}