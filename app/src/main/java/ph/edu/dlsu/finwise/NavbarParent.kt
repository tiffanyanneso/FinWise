package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import ph.edu.dlsu.finwise.databinding.ActivityParentAssessmentLandingPageBinding
import ph.edu.dlsu.finwise.parentAssessmentModule.parentAssessmentLandingPage
import ph.edu.dlsu.finwise.parentDashboardModule.ParentDashboardActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentGoalActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentLandingPageActivity
import ph.edu.dlsu.finwise.parentFinancialManagementModule.ParentFinancialManagementActivity


class NavbarParent (bottomNavigationView: BottomNavigationView, appCon: Context, navItem: Int) {
    init {

        // Initializes the navigation in the bottom navbar
        bottomNavigationView.selectedItemId = navItem
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {


                R.id.nav_parent_finance -> {
                    val intent = Intent(appCon, ParentFinancialManagementActivity::class.java)
                    appCon.startActivity(intent)
                }


                //TODO: can't navigate back to this activity because it doesn't pass a childID
                // that is needed in this activity in line 61
                // make childID global variable?
                R.id.nav_parent_goal -> {
                    val intent = Intent(appCon, ParentGoalActivity::class.java)
                    appCon.startActivity(intent)
                }

                R.id.nav_parent_dashboard -> {
                    val intent = Intent(appCon, ParentDashboardActivity::class.java)
                    appCon.startActivity(intent)
                }

                R.id.nav_parent_assessment-> {
                    val intent = Intent(appCon, parentAssessmentLandingPage::class.java)
                    appCon.startActivity(intent)
                }
            }
            true
        }
    }
}