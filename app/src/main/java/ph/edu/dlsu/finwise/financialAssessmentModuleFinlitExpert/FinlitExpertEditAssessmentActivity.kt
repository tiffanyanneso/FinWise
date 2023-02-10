package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertEditAssessmentBinding


class FinlitExpertEditAssessmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialAssessmentFinlitExpertEditAssessmentBinding
    private var firestore = Firebase.firestore

    private lateinit var assessmentID:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentFinlitExpertEditAssessmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        assessmentID = bundle.getString("assessmentID").toString()

        goToFinlitExpertAddNewQuestions()
        goToFinlitExpertSpecificAssessment()
    }


    private fun goToFinlitExpertAddNewQuestions(){
        binding.btnAddNewQuestions.setOnClickListener(){
            val goToFinlitExpertAddNewQuestion = Intent(applicationContext, FinlitExpertAddNewQuestionsActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("assessmentID", assessmentID)
            goToFinlitExpertAddNewQuestion.putExtras(sendBundle)
            startActivity(goToFinlitExpertAddNewQuestion)
        }
    }

    private fun goToFinlitExpertSpecificAssessment() {
        binding.btnCancel.setOnClickListener() {
            val goToFinlitExpertSpecificAssessment = Intent(applicationContext, FinlitExpertSpecificAssessmentActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("assessmentID", assessmentID)
            goToFinlitExpertSpecificAssessment.putExtras(sendBundle)
            startActivity(goToFinlitExpertSpecificAssessment)
        }
    }

}

