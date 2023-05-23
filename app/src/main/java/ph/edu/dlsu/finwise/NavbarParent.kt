package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import ph.edu.dlsu.finwise.childDashboardModule.ChildDashboardActivity
import ph.edu.dlsu.finwise.financialAssessmentModule.FinancialAssessmentLandingPageActivity
import ph.edu.dlsu.finwise.parentDashboardModule.ParentDashboardActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentFinancialActivity
import ph.edu.dlsu.finwise.parentFinancialManagementModule.ParentFinancialManagementActivity


class NavbarParent (bottomNavigationView: BottomNavigationView, appCon: Context, navItem: Int, bundle: Bundle?) {
    init {
        val sendBundle = getAndSetBundle(bundle)
        // Initializes the navigation in the bottom navbar
        bottomNavigationView.selectedItemId = navItem
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.nav_parent_finance -> {
                    val intent = Intent(appCon, ParentFinancialManagementActivity::class.java)
                    if (bundle != null) {
                        intent.putExtras(sendBundle)
                    }
                    appCon.startActivity(intent)
                }

                R.id.nav_parent_goal -> {
                    val intent = Intent(appCon, ParentFinancialActivity::class.java)
                    if (bundle != null) {
                        intent.putExtras(sendBundle)
                    }
                    appCon.startActivity(intent)
                }

                R.id.nav_parent_dashboard -> {
                    val intent = Intent(appCon, ChildDashboardActivity::class.java)
                    if (bundle != null) {
                        intent.putExtras(sendBundle)
                    }
                    appCon.startActivity(intent)
                }

                R.id.nav_parent_assessment-> {
                    val intent = Intent(appCon, FinancialAssessmentLandingPageActivity::class.java)
                    if (bundle != null) {
                        intent.putExtras(sendBundle)
                    }
                    appCon.startActivity(intent)
                }

                R.id.nav_parent_profile-> {
                    val intent = Intent(appCon, ParentProfileActivity::class.java)
                    if (bundle != null) {
                        intent.putExtras(sendBundle)
                    }
                    appCon.startActivity(intent)
                }
            }
            true
        }
    }

    private fun getAndSetBundle(bundle: Bundle?): Bundle {
        val childID = bundle?.getString("childID")

        val sendBundle = Bundle()
        sendBundle.putString("childID", childID)
        return sendBundle
    }
}
