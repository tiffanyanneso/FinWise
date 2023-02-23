package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.EditAssessmentQuestionsAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertEditAssessmentBinding
import ph.edu.dlsu.finwise.model.AssessmentQuestions


class FinlitExpertEditAssessmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialAssessmentFinlitExpertEditAssessmentBinding
    private var firestore = Firebase.firestore

    private lateinit var editAssessmentQuestionAdapter:EditAssessmentQuestionsAdapter

    private var questionsIDArrayList = ArrayList<String>()
    private var questionStatusArrayList = ArrayList<QuestionStatus>()

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
            updateAssessmentDetails()
            var specificAssessment = Intent(this, FinlitExpertSpecificAssessmentActivity::class.java)
            var bundle = Bundle()
            bundle.putString("assessmentID", assessmentID)
            specificAssessment.putExtras(bundle)
            this.startActivity(specificAssessment)
        }
    }

    private fun updateAssessmentDetails() {
        firestore.collection("Assessments").document(assessmentID).update("description", binding.etDescription.text.toString())
        firestore.collection("Assessments").document(assessmentID).update("nQuestionsInAssessment", binding.etNumberOfQuestions.text.toString().toInt())

        for (questionStatus in questionStatusArrayList)
            firestore.collection("AssessmentQuestions").document(questionStatus.questionID).update("isUsed", questionStatus.isActive)
    }

    private fun loadQuestions() {
        firestore.collection("AssessmentQuestions").whereEqualTo("assessmentID", assessmentID).get().addOnSuccessListener {results ->
            for (question in results) {
                var questionObject = question.toObject<AssessmentQuestions>()
                questionsIDArrayList.add(question.id)
                questionStatusArrayList.add(QuestionStatus(question.id,
                    questionObject?.question.toString(), questionObject?.difficulty.toString(), questionObject.isUsed!!))
            }

            editAssessmentQuestionAdapter = EditAssessmentQuestionsAdapter(this, questionStatusArrayList, assessmentID, object:EditAssessmentQuestionsAdapter.QuestionStatusSwitch{
                override fun clickQuestionStatusSwitch(position: Int, questionID: String, isActive: Boolean) {
                    updateQuestionStatusArrayList(position, questionID, isActive)
                }
            })
            binding.rvQuestions.adapter = editAssessmentQuestionAdapter
            binding.rvQuestions.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            editAssessmentQuestionAdapter.notifyDataSetChanged()
        }
    }

    private fun updateQuestionStatusArrayList(position:Int, questionID: String, isActive: Boolean) {
        questionStatusArrayList.set(position, QuestionStatus(questionID, questionStatusArrayList[position].question,
            questionStatusArrayList[position].difficulty,isActive))
        editAssessmentQuestionAdapter.notifyDataSetChanged()
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

    class QuestionStatus (var questionID:String, var question:String, var difficulty:String, var isActive:Boolean) {}
}

