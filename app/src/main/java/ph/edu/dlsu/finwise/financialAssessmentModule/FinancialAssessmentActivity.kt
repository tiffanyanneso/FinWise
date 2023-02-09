package ph.edu.dlsu.finwise.financialAssessmentModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R

class FinancialAssessmentActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_financial_assessment)

            // Hides actionbar,
            // and initializes the navbar
            supportActionBar?.hide()
            Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_assessment)
        }
    }

