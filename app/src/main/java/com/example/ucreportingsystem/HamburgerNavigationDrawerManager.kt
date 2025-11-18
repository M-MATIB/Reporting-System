package com.example.ucreportingsystem

import android.app.Activity
import android.content.Intent
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

object HamburgerNavigationDrawerManager {
    fun setupNavigationDrawer(
        activity: Activity,
        drawerLayout: DrawerLayout,
        navView: NavigationView
    ) {
        navView.setNavigationItemSelectedListener { menuItem ->
            // Close the drawer first
            drawerLayout.closeDrawer(GravityCompat.START)

            // Use a handler to delay the navigation slightly, allowing the drawer to close smoothly
            android.os.Handler(activity.mainLooper).postDelayed({
                when (menuItem.itemId) {
                    R.id.nav_home -> {
                        if (activity !is StudentHomeActivity) {
                            val intent = Intent(activity, StudentHomeActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            activity.startActivity(intent)
                        }
                    }
                    R.id.nav_profile -> {
                        if (activity !is StudentProfileActivity) {
                            activity.startActivity(Intent(activity, StudentProfileActivity::class.java))
                        }
                    }
                    R.id.nav_about_us -> {
                        if (activity !is AboutUsActivity) { // Assuming you have an AboutUsActivity
                            activity.startActivity(Intent(activity, AboutUsActivity::class.java))
                        }
                    }
                    R.id.nav_logout -> {
                        // Clear user session
                        UserRepository.clearUser()
                        Toast.makeText(activity, "Logging out...", Toast.LENGTH_LONG).show()

                        // Navigate to LoginActivity and clear the back stack
                        val intent = Intent(activity, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        activity.startActivity(intent)
                        activity.finish()
                    }
                }
            }, 250) // 250ms delay
            true
        }
    }

    fun NavigationHeader(navView: NavigationView){
        val headerView = navView.getHeaderView(0)
        val navHeaderName: TextView = headerView.findViewById(R.id.tv_nav_header_name)
        val navHeaderId: TextView = headerView.findViewById(R.id.tv_nav_header_id)

        // Display User Name in Navigation Header
        navHeaderName.text = UserRepository.currentUser?.email ?: ""
        navHeaderId.text = UserRepository.currentUser?.password ?: ""
    }
}