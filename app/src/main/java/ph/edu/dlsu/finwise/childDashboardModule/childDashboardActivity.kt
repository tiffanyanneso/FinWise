package ph.edu.dlsu.finwise.childDashboardModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R

class childDashboardActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_dashboard)

      // Initializes the navbar
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_dashboard)
    }

}