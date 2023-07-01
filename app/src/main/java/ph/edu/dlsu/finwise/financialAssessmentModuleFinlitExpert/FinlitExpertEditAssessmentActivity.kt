package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.NavbarFinlitExpert
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.EditAssessmentQuestionsAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertEditAssessmentBinding
import ph.edu.dlsu.finwise.model.FinancialAssessmentDetails
import ph.edu.dlsu.finwise.model.FinancialAssessmentQuestions


class FinlitExpertEditAssessmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialAssessmentFinlitExpertEditAssessmentBinding
    private var firestore = Firebase.firestore

    private lateinit var editAssessmentQuestionAdapter:EditAssessmentQuestionsAdapter

    private var questionsIDArrayList = ArrayList<String>()
    private var questionStatusArrayList = ArrayList<QuestionStatus>()

    private lateinit var assessmentID:String
    private lateinit var assessmentCategory: String // Add this line
    private lateinit var assessmentType: String // Add this line
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentFinlitExpertEditAssessmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        assessmentID = bundle.getString("assessmentID").toString()
       assessmentCategory = bundle.getString("assessmentCategory").toString() // Initialize assessmentCategory
        assessmentType = bundle.getString("assessmentType").toString() // Initialize assessmentType

        loadAssessmentDetails()
        loadQuestions()
        goToFinlitExpertAddNewQuestions()
        goToFinlitExpertSpecificAssessment()
        loadBackButton()

        NavbarFinlitExpert(findViewById(R.id.bottom_nav_finlit_expert), this, R.id.nav_finlit_assessment)


        binding.btnSave.setOnClickListener {
            updateAssessmentDetails()
            var specificAssessment = Intent(this, FinlitExpertSpecificAssessmentActivity::class.java)
            var bundle = Bundle()
            bundle.putString("assessmentID", assessmentID)
            specificAssessment.putExtras(bundle)
            this.startActivity(specificAssessment)
        }
    }

    private fun loadAssessmentDetails() {
        firestore.collection("Assessments").document(assessmentID).get().addOnSuccessListener {
            var assessment = it.toObject<FinancialAssessmentDetails>()
            binding.etNumberOfQuestions.setText(assessment?.nQuestionsInAssessment!!.toString())
            binding.etDescription.setText(assessment?.description)
        }
    }

    private fun updateAssessmentDetails() {
        firestore.collection("Assessments").document(assessmentID).update("description", binding.etDescription.text.toString())
        //TODO: Need to make the number of questions either automatic
        // or move it edit text below and make it required the adding of question
        // so the user doesn't forget to input it to avoid bugs
        firestore.collection("Assessments").document(assessmentID).update("nQuestionsInAssessment", binding.etNumberOfQuestions.text.toString().toInt())

        for (questionStatus in questionStatusArrayList)
            firestore.collection("AssessmentQuestions").document(questionStatus.questionID).update("isUsed", questionStatus.isActive)
    }

    private fun loadQuestions() {
        firestore.collection("AssessmentQuestions").whereEqualTo("assessmentID", assessmentID).get().addOnSuccessListener {results ->
            for (question in results) {
                var questionObject = question.toObject<FinancialAssessmentQuestions>()
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
            val goToFinlitExpertSpecificAssessmentActivity = Intent(applicationContext, FinlitExpertSpecificAssessmentActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("assessmentID", assessmentID)
            sendBundle.putString("assessmentCategory", assessmentCategory)
            sendBundle.putString("assessmentType", assessmentType)
            goToFinlitExpertSpecificAssessmentActivity.putExtras(sendBundle)
            startActivity(goToFinlitExpertSpecificAssessmentActivity)
        }
    }
    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(
            resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24,
            null)
        binding.topAppBar.setNavigationOnClickListener {
            FinlitExpertSpecificAssessmentActivity() // Navigate back to FinlitExpertAssessmentTypeActivity
        }
    }

    private fun FinlitExpertSpecificAssessmentActivity(){
        val specificAssessment = Intent(applicationContext, FinlitExpertSpecificAssessmentActivity::class.java)
        var sendBundle = Bundle()
        sendBundle.putString("assessmentID", assessmentID)
        sendBundle.putString("assessmentCategory", assessmentCategory)
        sendBundle.putString("assessmentType", assessmentType)
        specificAssessment.putExtras(sendBundle)
        startActivity(specificAssessment)
        finish() // Add this line to finish the current activity
    }

 /*   override fun onBackPressed() {
        val specificAssessment = Intent(applicationContext, FinlitExpertSpecificAssessmentActivity::class.java)
        var sendBundle = Bundle()
        sendBundle.putString("assessmentID", assessmentID)
        specificAssessment.putExtras(sendBundle)
        startActivity(specificAssessment)
    }*/


    class QuestionStatus (var questionID:String, var question:String, var difficulty:String, var isActive:Boolean) {}
}

