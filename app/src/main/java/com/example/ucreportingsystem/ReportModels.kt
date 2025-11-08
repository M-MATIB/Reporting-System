package com.example.ucreportingsystem

data class Report(
    val id: String,
    val type: String,
    val location: String,
    val date: String,
    val status: ReportStatus,
    val isPrivate: Boolean
)

enum class ReportStatus { PENDING, IN_PROGRESS, RESOLVED }