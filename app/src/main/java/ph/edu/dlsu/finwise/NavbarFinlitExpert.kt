package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.FinancialAssessmentFinlitExpertActivity
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.FinlitExpertProfileActivity

class NavbarFinlitExpert (bottomNavigationView: BottomNavigationView, appCon: Context, navItem: Int) {
    init {

        // Initializes the navigation in the bottom navbar
        bottomNavigationView.selectedItemId = navItem
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.nav_finlit_assessment -> {
                    val intent = Intent(appCon, FinancialAssessmentFinlitExpertActivity::class.java)
                    appCon.startActivity(intent)
                }

      /*          R.id.nav_finlit_profile -> {
                    val intent = Intent(appCon, FinlitExpertProfileActivity::class.java)
                    appCon.startActivity(intent)
                }*/
            }
            true
        }
    }
}