package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ph.edu.dlsu.finwise.NavbarFinlitExpert
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertBinding


class FinancialAssessmentFinlitExpertActivity : AppCompatActivity() {

        private lateinit var binding:ActivityFinancialAssessmentFinlitExpertBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityFinancialAssessmentFinlitExpertBinding.inflate(layoutInflater)
            setContentView(binding.root)

            var bundle = Bundle()
            var assessmentType = Intent(this, FinlitExpertAssessmentTypeActivity::class.java)


            binding.btnFinlitBudgeting.setOnClickListener {
                bundle.putString("assessmentCategory", binding.tvFinlitBudgeting.text.toString())
                assessmentType.putExtras(bundle)
                this.startActivity(assessmentType)
            }

            binding.btnFinlitFinancialGoals.setOnClickListener {
                bundle.putString("assessmentCategory", binding.tvFinlitFinancialGoals.text.toString())
                assessmentType.putExtras(bundle)
                this.startActivity(assessmentType)
            }

            binding.btnFinlitSaving.setOnClickListener {
                bundle.putString("assessmentCategory", binding.tvFinlitSaving.text.toString())
                assessmentType.putExtras(bundle)
                this.startActivity(assessmentType)
            }

            binding.btnFinlitSpending.setOnClickListener {
                bundle.putString("assessmentCategory", binding.tvFinlitSpending.text.toString())
                assessmentType.putExtras(bundle)
                this.startActivity(assessmentType)
            }

            // Hides actionbar,
            // and initializes the navbar
            supportActionBar?.hide()
            NavbarFinlitExpert(findViewById(R.id.bottom_nav_finlit_expert), this, R.id.nav_finlit_assessment)
        }
}