package ph.edu.dlsu.finwise.parentDashboardModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R

class ParentDashboardActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_dashboard)


        //Initializes the navbar
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_dashboard)
    }
}