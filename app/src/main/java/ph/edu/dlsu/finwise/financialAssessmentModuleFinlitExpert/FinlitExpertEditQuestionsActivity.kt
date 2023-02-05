package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertEditQuestionsBinding

class FinlitExpertEditQuestionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialAssessmentFinlitExpertEditQuestionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentFinlitExpertEditQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
}

    }