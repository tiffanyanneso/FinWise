package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertEditAssessmentBinding


class FinlitExpertEditAssessmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialAssessmentFinlitExpertEditAssessmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentFinlitExpertEditAssessmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goToFinlitExpertAddNewQuestions()
        goToFinlitExpertSpecificAssessment()

        }


    private fun goToFinlitExpertAddNewQuestions(){
        binding.btnAddNewQuestions.setOnClickListener(){
            val goToFinlitExpertAddNewQuestion = Intent(applicationContext, FinlitExpertAddNewQuestionsActivity::class.java)
            startActivity(goToFinlitExpertAddNewQuestion)
        }
    }



    private fun goToFinlitExpertSpecificAssessment() {
        binding.btnCancel.setOnClickListener() {
            val goToFinlitExpertSpecificAssessment = Intent(applicationContext, FinlitExpertSpecificAssessmentActivity::class.java)
            startActivity(goToFinlitExpertSpecificAssessment)
        }
    }

}

