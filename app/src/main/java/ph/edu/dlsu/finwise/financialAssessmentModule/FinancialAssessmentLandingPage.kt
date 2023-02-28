package ph.edu.dlsu.finwise.financialAssessmentModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentLandingPageBinding

class FinancialAssessmentLandingPage : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialAssessmentLandingPageBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_assessment)
    }

}