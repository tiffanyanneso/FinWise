package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity


class Navbar(bottomNavigationView: BottomNavigationView, appCon: Context, navItem: Int) {
    init {

        // Initializes the navigation in the bottom navbar
        bottomNavigationView.selectedItemId = navItem
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.nav_finance -> {
                    val intent = Intent(appCon, PersonalFinancialManagementActivity::class.java)
                    appCon.startActivity(intent)
                }

                R.id.nav_goal -> {
                    val intent = Intent(appCon, FinancialActivity::class.java)
                    appCon.startActivity(intent)
                }
/*
                R.id.nav_dashboard -> {
                val intent = Intent(appCon, DashboardActivity::class.java)
                appCon.startActivity(intent)
                }

                R.id.nav_assessment -> {
                    val intent = Intent(appCon, FinancialAssessmentActivity::class.java)
                    appCon.startActivity(intent)
                }

                R.id.nav_profile -> {
                    val intent = Intent(appCon, ProfileActivity::class.java)
                    appCon.startActivity(intent)
                }*/
            }
            true
        }
    }
}