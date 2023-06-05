package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import ph.edu.dlsu.finwise.parentDashboardModule.ParentDashboardActivity



class NavbarParentFirst (bottomNavigationView: BottomNavigationView, appCon: Context, navItem: Int) {
    init {
        // Initializes the navigation in the bottom navbar
        bottomNavigationView.selectedItemId = navItem
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {


                R.id.nav_parent_first_dashboard -> {
                    val intent = Intent(appCon, ParentDashboardActivity::class.java)
                    appCon.startActivity(intent)
                }


                R.id.nav_parent_first_profile-> {
                    val intent = Intent(appCon, ParentProfileActivity::class.java)
                    appCon.startActivity(intent)
                }
            }
            true
        }
    }

}
