package com.example.ucreportingsystem

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.view.GravityCompat
import androidx.core.content.ContextCompat
import com.example.ucreportingsystem.databinding.ActivityReportHistoryBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.button.MaterialButton

class ReportHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportHistoryBinding
    private lateinit var reportAdapter: ReportAdapter
    private val allReports = mutableListOf<Report>()
    private var currentFilterStatus: ReportStatus? = null
    private var isPrivateFilterActive: Boolean = true
    private var currentSearchQuery: String = ""

    private val reportTypes = listOf("Incident Report", "Medical Emergency", "Missing/Broken Item", "Other Reports")
    private val roomLocations = listOf("M301", "M302", "M303", "M304", "M305", "M306", "M307")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivMenuIcon.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        setupDrawerNavigation()

        binding.btnBackContainer.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerView()
        fetchReportData()
        setupSearch()
        setupFilters()
        applyAllFilters()
        updateButtonState(binding.btnStatusAll as MaterialButton)
    }

    private fun setupDrawerNavigation() {
        val navView: NavigationView = binding.navView
        navView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawers()
            when (menuItem.itemId) {
                R.id.nav_home -> startActivity(Intent(this, StudentHomeActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                R.id.nav_logout -> {
                    Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    })
                    finish()
                }
                R.id.nav_profile -> Toast.makeText(this, "Navigating to Profile...", Toast.LENGTH_SHORT).show()
                R.id.nav_about_us -> Toast.makeText(this, "Navigating to About Us...", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(this, "Navigating to: ${menuItem.title}", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun setupRecyclerView() {
        reportAdapter = ReportAdapter(emptyList()) { report ->
            onViewDetailsClicked(report)
        }

        binding.recyclerReportHistory.apply {
            layoutManager = LinearLayoutManager(this@ReportHistoryActivity)
            adapter = reportAdapter
            setHasFixedSize(true)
        }
    }

    private fun fetchReportData() {
        val statuses = ReportStatus.entries.toTypedArray()

        for (i in 1..12) {
            val reportId = "RPT-${1000 + i}"
            val reportType = reportTypes[i % reportTypes.size]
            val location = roomLocations[i % roomLocations.size]
            val status = statuses[i % statuses.size]
            val dateTime = "10/${17 - (i / 3)}/2025, ${10 + (i % 8)}:00 AM"
            val isPrivate = i % 2 == 0

            allReports.add(Report(reportId, reportType, location, dateTime, status, isPrivate))
        }
    }

    private fun setupSearch() {
        binding.etSearchReports.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s.toString()
                applyAllFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun setupFilters() {
        binding.togglePrivatePublic.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    binding.btnPrivate.id -> filterReports(isPrivate = true)
                    binding.btnPublic.id -> filterReports(isPrivate = false)
                }
            }
        }

        binding.btnStatusAll.setOnClickListener {
            filterByStatus(null)
            updateButtonState(binding.btnStatusAll as MaterialButton)
        }
        binding.btnStatusPending.setOnClickListener {
            filterByStatus(ReportStatus.PENDING)
            updateButtonState(binding.btnStatusPending as MaterialButton)
        }
        binding.btnStatusInProgress.setOnClickListener {
            filterByStatus(ReportStatus.IN_PROGRESS)
            updateButtonState(binding.btnStatusInProgress as MaterialButton)
        }
        binding.btnStatusResolved.setOnClickListener {
            filterByStatus(ReportStatus.RESOLVED)
            updateButtonState(binding.btnStatusResolved as MaterialButton)
        }
    }

    private fun filterReports(isPrivate: Boolean) {
        isPrivateFilterActive = isPrivate
        Toast.makeText(this, "Toggled: ${if (isPrivate) "Private" else "Public"}", Toast.LENGTH_SHORT).show()
        applyAllFilters()
    }

    private fun filterByStatus(status: ReportStatus?) {
        currentFilterStatus = status
        applyAllFilters()

        val statusText = status?.name?.replace("_", " ") ?: "All"
        Toast.makeText(this, "Status Filter: $statusText", Toast.LENGTH_SHORT).show()
    }

    private fun applyAllFilters() {
        var filteredList = allReports.toList()

        filteredList = filteredList.filter { it.isPrivate == isPrivateFilterActive }

        if (currentFilterStatus != null) {
            filteredList = filteredList.filter { it.status == currentFilterStatus }
        }

        if (currentSearchQuery.isNotBlank()) {
            val query = currentSearchQuery.trim().lowercase()
            filteredList = filteredList.filter { report ->
                report.id.lowercase().contains(query) ||
                        report.type.lowercase().contains(query) ||
                        report.location.lowercase().contains(query)
            }
        }

        reportAdapter.updateList(filteredList)
    }

    private fun updateButtonState(selectedButton: MaterialButton) {
        val buttons = listOf(
            binding.btnStatusAll,
            binding.btnStatusPending,
            binding.btnStatusInProgress,
            binding.btnStatusResolved
        )

        val activeBgColor = ContextCompat.getColor(this, R.color.uc_green)
        val activeTextColor = ContextCompat.getColor(this, R.color.white)

        val pendingBgColor = ContextCompat.getColor(this, R.color.pending_bg)
        val inProgressBgColor = ContextCompat.getColor(this, R.color.in_progress_bg)
        val resolvedBgColor = ContextCompat.getColor(this, R.color.resolved_bg)

        val pendingTextColor = ContextCompat.getColor(this, R.color.pending)
        val inProgressTextColor = ContextCompat.getColor(this, R.color.in_progress)
        val resolvedTextColor = ContextCompat.getColor(this, R.color.resolved)

        val pendingStrokeColor = ContextCompat.getColorStateList(this, R.color.pending)
        val inProgressStrokeColor = ContextCompat.getColorStateList(this, R.color.in_progress)
        val resolvedStrokeColor = ContextCompat.getColorStateList(this, R.color.resolved)
        val allStrokeColor = ContextCompat.getColorStateList(this, R.color.uc_green)

        for (button in buttons) {
            val materialButton = button as MaterialButton

            materialButton.strokeWidth = 4
            materialButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            materialButton.setTextColor(ContextCompat.getColor(this, R.color.uc_green))

            when (materialButton.id) {
                binding.btnStatusPending.id -> {
                    materialButton.setBackgroundColor(pendingBgColor)
                    materialButton.setTextColor(pendingTextColor)
                    materialButton.strokeColor = pendingStrokeColor
                }
                binding.btnStatusInProgress.id -> {
                    materialButton.setBackgroundColor(inProgressBgColor)
                    materialButton.setTextColor(inProgressTextColor)
                    materialButton.strokeColor = inProgressStrokeColor
                }
                binding.btnStatusResolved.id -> {
                    materialButton.setBackgroundColor(resolvedBgColor)
                    materialButton.setTextColor(resolvedTextColor)
                    materialButton.strokeColor = resolvedStrokeColor
                }
                binding.btnStatusAll.id -> {
                    materialButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                    materialButton.setTextColor(ContextCompat.getColor(this, R.color.black))
                    materialButton.strokeColor = allStrokeColor
                }
            }
        }

        selectedButton.setBackgroundColor(activeBgColor)
        selectedButton.setTextColor(activeTextColor)
        selectedButton.strokeColor = allStrokeColor
    }


    private fun onViewDetailsClicked(report: Report) {
        Toast.makeText(this, "Viewing details for: ${report.id} - ${report.type}", Toast.LENGTH_SHORT).show()
    }
}