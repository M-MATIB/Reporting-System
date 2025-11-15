package com.example.ucreportingsystem

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

object UserRepository {
    //To access cloud Firestore
    val db = Firebase.firestore

    // Data class to represent a user. It's good practice to define the data structure.
    data class User(
        val email: String,
        val FullName: String? = null,
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
     * Searches for a user in the "Student" and "Staff" collections.
     * If found, it populates 'currentUser' and 'userType'.
     * The callback returns the type of user found ("Student", "Staff") or null if not found.
     */

    fun fetchUser(email: String, password: String, onComplete: (userRole: String?) -> Unit) {
        // First, search in the "Student" collection
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
                    // If not found in Student, search in the "Staff" collection
                    fetchStaff(email, password, onComplete)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("UserRepository", "Error getting documents from Student collection.", exception)
                onComplete(null) // Indicate failure
            }
    }

    private fun fetchStaff(email: String, password: String, onComplete: (userRole: String?) -> Unit) {
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
                    // User not found in either collection
                    currentUser = null
                    userType = null
                    onComplete(null)
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