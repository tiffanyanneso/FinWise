package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.NavbarFinlitExpert
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertBinding


class FinancialAssessmentFinlitExpertActivity : AppCompatActivity() {

    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private lateinit var binding:ActivityFinancialAssessmentFinlitExpertBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentFinlitExpertBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addQuestions()

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

    private fun addQuestions() {
        goalSettingQuestions()
        savingQuestions()
        budgetingQuestions()
        spendingQuestions()
    }

    private fun goalSettingQuestions() {
        var assessment = hashMapOf(
             "assessmentCategory" to "Goal Setting",
             "assessmentType" to "Preliminary",
             "description" to "Preliminary assessment for goal setting",
             "createdOn" to Timestamp.now(),
             "createdBy" to currentUser,
             "nTakes" to 0,
             "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            addGoalSettingPreliminaryEasy(assessmentID)
            addGoalSettingPreliminaryMedium(assessmentID)
            addGoalSettingPreliminaryHard(assessmentID)
        }
    }

    private fun addGoalSettingPreliminaryEasy(assessmentID:String) {
        var question = hashMapOf(
             "assessmentID" to assessmentID,
             "question" to "A financial goal is…",
             "difficulty" to "Easy",
             "dateCreated" to Timestamp.now(),
             "createdBy" to currentUser,
             "isUsed" to true,
             "nAssessments" to 0,
             "nAnsweredCorrectly" to 0
        )
        firestore.collection("AssessmentQuestions").add(question).addOnSuccessListener {
            var questionID = it.id
            var choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Objective for your money",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Destiny for your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Adventure for your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

//        question = hashMapOf(
//            "assessmentID" to assessmentID,
//            "question" to,
//            "difficulty" to "Easy",
//            "dateCreated" to Timestamp.now(),
//            "createdBy" to currentUser,
//            "isUsed" to true,
//            "nAssessments" to 0,
//            "nAnsweredCorrectly" to 0
//        )
//        firestore.collection("AssessmentQuestions").add(question).addOnSuccessListener {
//            var questionID = it.id
//            var choice = hashMapOf(
//                "questionID" to questionID,
//                "choice" to,
//                "isCorrect" to false
//            )
//            firestore.collection("AssessmentChoices").add(choice)
//        }
//
//        question = hashMapOf(
//            "assessmentID" to assessmentID,
//            "question" to,
//            "difficulty" to "Easy",
//            "dateCreated" to Timestamp.now(),
//            "createdBy" to currentUser,
//            "isUsed" to true,
//            "nAssessments" to 0,
//            "nAnsweredCorrectly" to 0
//        )
//        firestore.collection("AssessmentQuestions").add(question).addOnSuccessListener {
//            var questionID = it.id
//            var choice = hashMapOf(
//                "questionID" to questionID,
//                "choice" to,
//                "isCorrect" to false
//            )
//            firestore.collection("AssessmentChoices").add(choice)
//        }
//
//        question = hashMapOf(
//            "assessmentID" to assessmentID,
//            "question" to,
//            "difficulty" to "Easy",
//            "dateCreated" to Timestamp.now(),
//            "createdBy" to currentUser,
//            "isUsed" to true,
//            "nAssessments" to 0,
//            "nAnsweredCorrectly" to 0
//        )
//        firestore.collection("AssessmentQuestions").add(question).addOnSuccessListener {
//            var questionID = it.id
//            var choice = hashMapOf(
//                "questionID" to questionID,
//                "choice" to,
//                "isCorrect" to false
//            )
//            firestore.collection("AssessmentChoices").add(choice)
//        }
//
//        var question = hashMapOf(
//            "assessmentID" to assessmentID,
//            "question" to,
//            "difficulty" to "Easy",
//            "dateCreated" to Timestamp.now(),
//            "createdBy" to currentUser,
//            "isUsed" to true,
//            "nAssessments" to 0,
//            "nAnsweredCorrectly" to 0
//        )
//        firestore.collection("AssessmentQuestions").add(question).addOnSuccessListener {
//            var questionID = it.id
//            var choice = hashMapOf(
//                "questionID" to questionID,
//                "choice" to,
//                "isCorrect" to false
//            )
//            firestore.collection("AssessmentChoices").add(choice)
//        }

    }

    private fun addGoalSettingPreliminaryMedium(assessmentID:String) {

    }

    private fun addGoalSettingPreliminaryHard(assessmentID:String) {

    }

    private fun savingQuestions() {

    }

    private fun budgetingQuestions() {

    }

    private fun spendingQuestions() {

    }
}