package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ph.edu.dlsu.finwise.NavbarFinlitExpert
import ph.edu.dlsu.finwise.R

class FinlitExpertProfileActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_financial_assessment_finlit_expert_profile)

            // Hides actionbar,
            // and initializes the navbar
            supportActionBar?.hide()
            NavbarFinlitExpert(findViewById(R.id.bottom_nav_finlit_expert), this, R.id.nav_finlit_profile)
        }
}