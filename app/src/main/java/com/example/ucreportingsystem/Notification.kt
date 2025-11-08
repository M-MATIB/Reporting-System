package com.example.ucreportingsystem

enum class NotificationStatus {
    PUBLIC,
    PENDING,
    IN_PROGRESS,
    RESOLVED
}

data class Notification(
    val id: String,
    val status: NotificationStatus,
    val category: String,
    val description: String,
    val timestamp: String
)