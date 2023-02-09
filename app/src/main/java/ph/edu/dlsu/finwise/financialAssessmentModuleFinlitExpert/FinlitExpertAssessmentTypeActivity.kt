package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ph.edu.dlsu.finwise.NavbarFinlitExpert
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertBinding
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertTypeBinding

class FinlitExpertAssessmentTypeActivity : AppCompatActivity () {

    private lateinit var binding:ActivityFinancialAssessmentFinlitExpertTypeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentFinlitExpertTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        var assessmentCategory = bundle.getString("assessmentCategory")

        var specificAssessment = Intent(this, FinlitExpertSpecificAssessmentActivity::class.java)
        var dataBundle = Bundle()
        dataBundle.putString("assessmentCategory", assessmentCategory)


        binding.btnPreliminary.setOnClickListener {
            dataBundle.putString("assessmentType", binding.tvPreliminary.text.toString())
            specificAssessment.putExtras(dataBundle)
            this.startActivity(specificAssessment)
        }

        binding.btnPost.setOnClickListener {
            dataBundle.putString("assessmentType", binding.tvPost.text.toString())
            specificAssessment.putExtras(dataBundle)
            this.startActivity(specificAssessment)
        }

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        NavbarFinlitExpert(findViewById(R.id.bottom_nav_finlit_expert), this, R.id.nav_finlit_assessment)

    }
}