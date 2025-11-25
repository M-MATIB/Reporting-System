package com.example.ucreportingsystem

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

object UserRepository {
    // Data class to represent a user. It's good practice to define the data structure.
    data class User(
        val email: String,
        val fullName: String? = null,
        val password: String? = null,
    )

    // This will hold the currently logged-in user's data.
    // It's private to prevent other classes from changing it directly.
    var currentUser: User? = null
        private set

    // This will hold the type of user (e.g., "Student" or "Staff")
    var userType: String? = null
        private set

    /**
     * Searches for a user in the "Student" collection.
     * If found, it populates 'currentUser' and 'userType'.
     * The callback returns "Student" or null if not found.
     */
    fun fetchStudent(email: String, password: String, context: Context, onComplete: (userRole: String?) -> Unit) {
        val db = Firebase.firestore
        db.collection("Student")
            .whereEqualTo("Email", email)
            .whereEqualTo("Password", password)
            .get()
            .addOnSuccessListener { studentResult ->
                if (!studentResult.isEmpty) {
                    // User found in Student collection
                    val document = studentResult.documents.first() // Get the first document
                    currentUser = User(
                        email = document.getString("Email") ?: "",
                        password = document.getString("Password")
                        // Add other student-specific fields if necessary
                    )
                    userType = "Student"
                    onComplete("Student")
                } else {
                    Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show() // Use the passed context
                    onComplete(null) // Make sure to call onComplete
                }
            }
            .addOnFailureListener { exception ->
                Log.w("UserRepository", "Error getting documents from Student collection.", exception)
                onComplete(null) // Indicate failure
            }
    }

    /**
     * Searches for a user in the "Staff" collection.
     * If found, it populates 'currentUser' and 'userType'.
     * The callback returns "Staff" or null if not found.
     */
    fun fetchStaff(email: String, password: String, context: Context, onComplete: (userRole: String?) -> Unit) {
        val db = Firebase.firestore
        db.collection("Staff")
            .whereEqualTo("Email", email)
            .whereEqualTo("Password", password)
            .get()
            .addOnSuccessListener { staffResult ->
                if (!staffResult.isEmpty) {
                    // User found in Staff collection
                    val document = staffResult.documents.first()
                    currentUser = User(
                        email = document.getString("Email") ?: "",
                        password = document.getString("Password")
                        // Add other staff-specific fields if necessary
                    )
                    userType = "Staff"
                    onComplete("Staff")
                } else {
                    Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show() // Use the passed context
                    onComplete(null) // Make sure to call onComplete
                }
            }
            .addOnFailureListener { exception ->
                Log.w("UserRepository", "Error getting documents from Staff collection.", exception)
                onComplete(null) // Indicate failure
            }
    }

    /**
     * Clears the current user data upon logout.
     */
    fun clearUser() {
        currentUser = null
        userType = null
    }
}
