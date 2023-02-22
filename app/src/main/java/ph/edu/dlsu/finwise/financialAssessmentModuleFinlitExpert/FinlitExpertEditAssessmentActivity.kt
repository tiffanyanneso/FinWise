package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.EditAssessmentQuestionsAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertEditAssessmentBinding


class FinlitExpertEditAssessmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialAssessmentFinlitExpertEditAssessmentBinding
    private var firestore = Firebase.firestore

    private lateinit var editAssessmentQuestionAdapter:EditAssessmentQuestionsAdapter

    private var questionsIDArrayList = ArrayList<String>()

    private lateinit var assessmentID:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentFinlitExpertEditAssessmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        assessmentID = bundle.getString("assessmentID").toString()

        loadQuestions()
        goToFinlitExpertAddNewQuestions()
        goToFinlitExpertSpecificAssessment()

        binding.btnSave.setOnClickListener {
            var specificAssessment = Intent(this, FinlitExpertSpecificAssessmentActivity::class.java)
            var bundle = Bundle()
            bundle.putString("assessmentID", assessmentID)
            specificAssessment.putExtras(bundle)
            this.startActivity(specificAssessment)
        }
    }

    private fun loadQuestions() {
        firestore.collection("AssessmentQuestions").whereEqualTo("assessmentID", assessmentID).get().addOnSuccessListener {results ->
            for (question in results)
                questionsIDArrayList.add(question.id)

            editAssessmentQuestionAdapter = EditAssessmentQuestionsAdapter(this, questionsIDArrayList, assessmentID)
            binding.rvQuestions.adapter = editAssessmentQuestionAdapter
            binding.rvQuestions.layoutManager = LinearLayoutManager(applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
            editAssessmentQuestionAdapter.notifyDataSetChanged()
        }

    }


    private fun goToFinlitExpertAddNewQuestions(){
        binding.btnAddNewQuestions.setOnClickListener(){
            val addNewQuestion = Intent(applicationContext, FinlitExpertAddNewQuestionsActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("assessmentID", assessmentID)
            addNewQuestion.putExtras(sendBundle)
            startActivity(addNewQuestion)
        }
    }

    private fun goToFinlitExpertSpecificAssessment() {
        binding.btnCancel.setOnClickListener() {
            val specificAssessment = Intent(applicationContext, FinlitExpertSpecificAssessmentActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("assessmentID", assessmentID)
            specificAssessment.putExtras(sendBundle)
            startActivity(specificAssessment)
        }
    }

}

