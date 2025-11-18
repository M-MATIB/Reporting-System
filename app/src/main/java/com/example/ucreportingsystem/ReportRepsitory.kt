package com.example.ucreportingsystem

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


object ReportRepository {
    // A data class to represent an incident report.
    // It's good practice to use val and provide default values.
    data class Report(
        val Title: String = "",
        val IssueDescription: String = "",
        val UserLocation: String = "",
        val ReportDestination: String? = null
    )

    // A callback to handle the results asynchronously
    fun fetchAllReports(onReportsFetched: (List<Report>) -> Unit) {
        val db = Firebase.firestore
        db.collection("IncidentReports")
            .get()
            .addOnSuccessListener { result ->
                val reportList = mutableListOf<Report>()
                for (document in result) {
                    // Automatically convert the document to a Report object
                    val report = document.toObject<Report>()
                    reportList.add(report)
                    Log.d("ReportRepository", "Fetched Report: $report")
                }
                onReportsFetched(reportList)
            }
            .addOnFailureListener { exception ->
                Log.w("ReportRepository", "Error getting documents.", exception)
                onReportsFetched(emptyList()) // Return an empty list on failure
            }
    }

    /**
     * Listens for real-time additions to the "IncidentReports" collection.
     *
     * This function attaches a snapshot listener that triggers only when a new
     * document is added to the collection.
     *
     * @param onNewReport A callback function that will be invoked with the newly
     *                    added Report object.
     */
    fun listenForNewReports(onNewReport: (Report) -> Unit) {
        val db = Firebase.firestore
        db.collection("IncidentReports")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("ReportRepository", "Listen failed.", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        // 1. Convert the new document to a Report object
                        val newReport = dc.document.toObject<Report>()
                        Log.d("ReportRepository", "New report added: $newReport")

                        // 2. Access the fields directly from the newReport object
                        val title = newReport.Title
                        val description = newReport.IssueDescription
                        val location = newReport.UserLocation
                        val destination = newReport.ReportDestination // This can be null

                        // 3. You can now use these variables as needed
                        Log.d("ReportRepository", "Accessed Title: $title")
                        Log.d("ReportRepository", "Accessed Description: $description")
                        Log.d("ReportRepository", "Accessed Location: $location")
                        Log.d("ReportRepository", "Accessed Destination: $destination")

                        // 4. Pass the complete object to the callback
                        onNewReport(newReport)
                    }
                }
            }
    }
}