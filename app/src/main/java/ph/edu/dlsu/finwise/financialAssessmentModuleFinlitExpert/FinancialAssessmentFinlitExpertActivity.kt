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

    private lateinit var binding: ActivityFinancialAssessmentFinlitExpertBinding

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

        // and initializes the navbar
        NavbarFinlitExpert(findViewById(R.id.bottom_nav_finlit_expert), this, R.id.nav_finlit_assessment)
    }

    private fun addQuestions() {
        goalSettingPreliminaryQuestions()
        goalSettingPreActivityQuestions()
        goalSettingPostActivityQuestions()
        budgetingPreliminaryQuestions()
        budgetingPreActivityQuestions()
        budgetingPostActivityQuestions()
        savingPreliminaryQuestions()
        savingPreActivityQuestions()
        savingPostActivityQuestions()
        spendingPreliminaryQuestions()
        spendingPreActivityQuestions()
        spendingPostActivityQuestions()
    }

    private fun goalSettingPreliminaryQuestions() {
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

    private fun goalSettingPreActivityQuestions() {
        var assessment = hashMapOf(
            "assessmentCategory" to "Goal Setting",
            "assessmentType" to "Pre-Activity",
            "description" to "Pre-Activity assessment for goal setting",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            addGoalSettingPreActivityEasy(assessmentID)
            addGoalSettingPreActivityMedium(assessmentID)
            addGoalSettingPreActivityHard(assessmentID)
        }
    }

    private fun goalSettingPostActivityQuestions() {
        var assessment = hashMapOf(
            "assessmentCategory" to "Goal Setting",
            "assessmentType" to "Post-Activity",
            "description" to "Post-Activity assessment for goal setting",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            addGoalSettingPostActivityEasy(assessmentID)
            addGoalSettingPostActivityMedium(assessmentID)
            addGoalSettingPostActivityHard(assessmentID)
        }
    }

    private fun budgetingPreliminaryQuestions(){
        var assessment = hashMapOf(
            "assessmentCategory" to "Concept of Budgeting",
            "assessmentType" to "Preliminary",
            "description" to "Preliminary assessment for concept of budgeting",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            addBudgetingPreliminaryEasy(assessmentID)
            addBudgetingPreliminaryMedium(assessmentID)
            addBudgetingPreliminaryHard(assessmentID)
        }
    }

    private fun budgetingPreActivityQuestions(){
        var assessment = hashMapOf(
            "assessmentCategory" to "Concept of Budgeting",
            "assessmentType" to "Pre-Activity",
            "description" to "Pre-Activity assessment for concept of budgeting",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            addBudgetingPreActivityEasy(assessmentID)
            addBudgetingPreActivityMedium(assessmentID)
            addBudgetingPreActivityHard(assessmentID)
        }
    }
    private fun budgetingPostActivityQuestions(){
        var assessment = hashMapOf(
            "assessmentCategory" to "Concept of Budgeting",
            "assessmentType" to "Post-Activity",
            "description" to "Post-Activity assessment for concept of budgeting",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            addBudgetingPostActivityEasy(assessmentID)
            addBudgetingPostActivityMedium(assessmentID)
            addBudgetingPostActivityHard(assessmentID)
        }
    }

    private fun savingPreliminaryQuestions(){
        var assessment = hashMapOf(
            "assessmentCategory" to "Concept of Saving",
            "assessmentType" to "Preliminary",
            "description" to "Preliminary assessment for concept of saving",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            addSavingPreliminaryEasy(assessmentID)
            addSavingPreliminaryMedium(assessmentID)
            addSavingPreliminaryHard(assessmentID)
        }
    }
    private fun savingPreActivityQuestions(){
        var assessment = hashMapOf(
            "assessmentCategory" to "Concept of Saving",
            "assessmentType" to "Pre-Activity",
            "description" to "Pre-Activity assessment for concept of saving",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            addSavingPreActivityEasy(assessmentID)
            addSavingPreActivityMedium(assessmentID)
            addSavingPreActivityHard(assessmentID)
        }
    }

    private fun savingPostActivityQuestions(){
        var assessment = hashMapOf(
            "assessmentCategory" to "Concept of Saving",
            "assessmentType" to "Post-Activity",
            "description" to "Post-Activity assessment for concept of saving",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            addSavingPostActivityEasy(assessmentID)
            addSavingPostActivityMedium(assessmentID)
            addSavingPostActivityHard(assessmentID)
        }
    }
    private fun spendingPreliminaryQuestions(){
        var assessment = hashMapOf(
            "assessmentCategory" to "Concept of Spending",
            "assessmentType" to "Preliminary",
            "description" to "Preliminary assessment for concept of spending",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            addSpendingPreliminaryEasy(assessmentID)
            addSpendingPreliminaryMedium(assessmentID)
            addSpendingPreliminaryHard(assessmentID)
        }
    }
    private fun spendingPreActivityQuestions(){
        var assessment = hashMapOf(
            "assessmentCategory" to "Concept of Spending",
            "assessmentType" to "Pre-Activity",
            "description" to "Pre-Activity assessment for concept of spending",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            addSpendingPreActivityEasy(assessmentID)
            addSpendingPreActivityMedium(assessmentID)
            addSpendingPreActivityHard(assessmentID)
        }
    }
    private fun spendingPostActivityQuestions(){
        var assessment = hashMapOf(
            "assessmentCategory" to "Concept of Spending",
            "assessmentType" to "Post-Activity",
            "description" to "Post-Activity assessment for concept of spending",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            addSpendingPostActivityEasy(assessmentID)
            addSpendingPostActivityMedium(assessmentID)
            addSpendingPostActivityHard(assessmentID)
        }
    }

    private fun addGoalSettingPreliminaryEasy(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a Financial Goal?",
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
                "choice" to "A plan you have for your money",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Something you want to buy right now",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Something you are required to buy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Something you want to give away",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a short-term financial goal?",
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
                "choice" to "A goal you want to achieve in 10 years",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A goal you want to achieve in the next few days or weeks",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A goal you want to achieve in 7 months",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "A short-term financial goal is…",
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
                "choice" to "A goal you want to achieve in 10 years",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It usually takes years to achieve",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It takes a short amount of time to achieve, a few days or weeks",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "SMART goal stands for…",
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
                "choice" to "Specific, Measurable, Achievable, Relevant, Time-bound",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Specific, Meaningful, Achievable, Relevant, Time-bound",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Sporty, Measurable, Achievable, Relevant, Time-bound",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a SMART goal?",
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
                "choice" to "A very clear and reasonable goal",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A dumb goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A goal that is not attainable",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "A SMART goal is",
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
                "choice" to "Clear, realistic, and focused",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Impossible to achieve",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Nice to look at",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Why should we set SMART goals?",
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
                "choice" to "It makes achieving goals more difficult",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It increases your chances of achieving the goal",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It makes your goals sound better",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Why should goals be SMART?",
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
                "choice" to "So that your parents will be proud",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "So that you sound smart",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "So that they are clear and would motivate you",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Why should goals be SMART?",
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
                "choice" to "So that your parents will be proud",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "So that you sound smart",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "So that they are clear and would motivate you",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }

    private fun addGoalSettingPreActivityEasy(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Is it important to set financial goals?",
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
                "choice" to "Yes, it's important for everyone",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No, it's not important",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Yes, but only for adults",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It does not matter",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Short-term financial goals are...",
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
                "choice" to "Goals that have smaller target amounts and take days or weeks to achieve",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Goals that take forever to achieve",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Goals that have bigger target amounts and take years to achieve",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to " Which of these goals is a SMART goal?",
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
                "choice" to "I will save P200 to buy a gift",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "I will save 20 pesos each week",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "I will save P200 in 3 months to buy a gift for Lolo by saving 20 pesos each week",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Is the goal “I want to save money” a SMART one?",
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
                "choice" to "Yes, it’s a perfect goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No, because it’s not specific and is not time-bound",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How can you make your goals specific?",
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
                "choice" to "By answering the questions: Who, What, Where, When, & Why",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By not including important details",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By making your goal very long",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Jen is having trouble making a specific goal and asks you for help. Which of the following is a specific goal?",
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
                "choice" to "Saving money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving money for a Jollibee Yumburger",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving money for a snack",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Manny said that he wants to save money. Is this a specific goal?",
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
                "choice" to "Yes, Manny made a great goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No, Manny does not mention what he wants to save for",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Denny wants to save money for a birthday gift for a birthday next month. What is missing from his goal?",
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
                "choice" to "A target amount so that the goal can be measured",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A gift so that he does not need to buy one",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A target date because he did not indicate one",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How can you make your goals achievable?",
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
                "choice" to "By reviewing your target amount and date and reflecting on whether you can really achieve the goal",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By making sure its impossible to achieve",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By competing with your friend and making your target amount very high",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By setting your target date too soon",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }

    private fun addGoalSettingPostActivityEasy(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Financial goals are…",
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
                "choice" to "A plan to save money",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A plan to spend money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A plan to budget money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

            question = hashMapOf(
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
                    "choice" to "Adventure for your money",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Destiny for your money",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)
            }
            question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "I will set a financial goal",
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
                    "choice" to "As early as possible",
                    "isCorrect" to true
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Never",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "The day before I want to buy something",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)
            }
            question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "What should you do if you want to buy something, but don't have enough money for it?",
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
                    "choice" to "Set a goal and save up for it",
                    "isCorrect" to true
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Steal the money from someone else",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Borrow money from a friend",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Ask your parents to buy it for you",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)
            }
            question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "If you currently have P50 but the toy you want costs P100. What can you do to eventually buy that toy?",
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
                    "choice" to "Set a financial goal to save money for the toy",
                    "isCorrect" to true
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Borrow money from a friend",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Ask money from a stranger",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Steal money",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)
            }
            question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "A book you really want costs P200 but you only have P100 right now. You can…",
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
                    "choice" to "Set a financial goal to save money for the book",
                    "isCorrect" to true
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Ask your mom to buy the book",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Steal money",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

            }
            question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "Your lolo’s birthday is coming up and you want to buy him a gift. How can you save enough money to buy him that gift?",
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
                    "choice" to "Take note of how much the gift costs and put money away frequently for it",
                    "isCorrect" to true
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Ask your dad to buy the gift",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Buy the gift on the day of his birthday",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Request money from your friends so you can buy the gift",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)
            }
            question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "What are some examples of short-term financial goals?",
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
                    "choice" to "Saving for a new car",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Starting a business",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for college",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for a sandwich",
                    "isCorrect" to true
                )
                firestore.collection("AssessmentChoices").add(choice)
            }
            question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "An example of short-term financial goal is",
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
                    "choice" to "Saving for a house",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for fried chicken",
                    "isCorrect" to true
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for college",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for a bike",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)
            }
            question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "Which of the following is NOT a short-term financial goal?",
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
                    "choice" to "Saving for a corndog",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for fried chicken",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for a phone",
                    "isCorrect" to true
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for a corndog",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)
            }
            question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "Karly wants to buy a small toy which costs P100. What type of goal should they set?",
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
                    "choice" to "A short-term goal because it is a small amount",
                    "isCorrect" to true
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "A short-term goal because Karly is short",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "A long-term goal because it is a huge amount",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)
            }
            question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "Is saving for college a short-term financial goal?",
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
                    "choice" to "Yes",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "No",
                    "isCorrect" to true
                )
                firestore.collection("AssessmentChoices").add(choice)
            }
            question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "Miguel set a financial goal so that he could save for a burger. Can this be a short-term financial goal?",
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
                    "choice" to "Yes",
                    "isCorrect" to true
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "No",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)
            }
            question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "Your friend Karl is not sure about what type of goal to set for a P200 book that he wants to buy. What should you advise him to do?",
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
                    "choice" to "Set a short-term goal",
                    "isCorrect" to true
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Set a medium-term goal",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Buy the book now",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Ask his mom to buy the book",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)
            }
            question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "What is NOT an example of a short-term goal?",
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
                    "choice" to "Saving for a book",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for a snack",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for retirement",
                    "isCorrect" to true
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for a toy",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)
            }
            question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "What is NOT an example of a short-term goal?",
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
                    "choice" to "Saving for a guitar",
                    "isCorrect" to true
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for a snack",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for burger",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for a toy",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)
            }
            question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "What is NOT an example of a short-term goal?",
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
                    "choice" to "Saving for an out-of-town trip",
                    "isCorrect" to true
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for a snack",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for burger",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "Saving for a toy",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)
            }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Miguel wants to buy a chicken sandwich worth P250 but he only has P10. Is it realistic for him to have saved enough money to buy the sandwich tomorrow?",
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
                "choice" to "Yes",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No, he does not have enough time",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "An item you want to buy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A way to earn more money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Tracy currently receives an allowance of P500 a week. Will she be able to achieve a goal with a target amount of P100,000?",
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
                "choice" to "No, it is not achievable",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Yes, it is achievable",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How can you make your goals relevant?",
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
                "choice" to "By thinking about what is important to you",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By thinking about what is important to your friend",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By thinking about what is important to your mom",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Penny wants to treat herself to an afternoon snack. The only option she has that is near her is fried chicken which she hates. Should she still set a goal for the chicken?",
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
                "choice" to "No, because fried chicken is unhealthy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No, because it is not relevant to her",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Yes, because she should just keep setting goals",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How can you make your goals time-bound?",
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
                "choice" to "By taking your time to accomplish your goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By not setting a target date",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By setting a target date",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Ben wants to save money for a small toy but he has not set a target date. Is this a SMART goal?",
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
                "choice" to "No because it is not time bound",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Yes because he does not need a target date to be time bound",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Manny set a financial goal of P100 to save money for a toy he wants to buy. However, he did not set a target date. Is this a SMART goal?",
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
                "choice" to "Yes",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        }

    private fun addGoalSettingPreliminaryMedium(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Why is it important to set financial goals?",
            "difficulty" to "Medium",
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
                "choice" to "They can help you focus to get what you want ",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "They help you waste your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "They help you spend your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Financial goals are important to set because?",
            "difficulty" to "Medium",
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
                "choice" to "They guide you on what to do with your money ",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "They are a waste of time",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "They are only important for older individuals",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It is not important to set if you have money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a medium-term goal?",
            "difficulty" to "Medium",
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
                "choice" to "A medium-term goal is something that is impossible to achieve",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A medium-term goal is something that you don't really care about",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A medium-term goal is a goal that you want to achieve in the near future, like within a few months or less than a year",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A medium-term goal is something that you want to achieve when you are already in your 40s",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "An example of a medium-term financial goal is",
            "difficulty" to "Medium",
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
                "choice" to "Saving up for a big trip around the world",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To save up for a new toy or game that you really want within the next few months",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To buy a fancy car when you turn 18",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To save up for a college education",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Which of the following is a medium-term goal?",
            "difficulty" to "Medium",
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
                "choice" to "Saving for college",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for travel",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for clothes",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To save up for a college education",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Mark wants to save money for school supplies for the upcoming school year. What should he do?",
            "difficulty" to "Medium",
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
                "choice" to "Set a short-term goal since it’ll be cheap",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Set a medium-term goal since it’ll take a bit longer to save for",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy all the things now cause he’s rich",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Borrow money from friends so he can buy the school supplies right away",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Karly wants to buy a large toy which costs P1500. What type of goal should they set?",
            "difficulty" to "Medium",
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
                "choice" to "A short-term goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A medium-term goal",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A lonmg-term goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Do nothing",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Jessie plans to save up for a ticket to a movie theater. His target date is 2 weeks away. Is this a medium-term goal?",
            "difficulty" to "Medium",
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
                "choice" to "Yes",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A No",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "All of these are benefits of setting SMART goals EXCEPT",
            "difficulty" to "Medium",
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
                "choice" to "Provides clarity and focus by defining what, how, and when to achieve",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Gives a clear target to work towards",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It is not achievable",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Helps you prioritize your time and resources by focusing on what’s important",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What does the S in SMART Goals stand for?",
            "difficulty" to "Medium",
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
                "choice" to "Specific - a goal must be focused",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Simple - a goal must be easy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Sensible - a goal must be reasonable",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Sample - a goat must be typical",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What does the M in SMART Goals stand for?",
            "difficulty" to "Medium",
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
                "choice" to "Measurable- a goal must have an amount for progress tracking",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Memorable - a goal must be easily remembered",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Minimum- a goal must be very easy to accomplish",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Mental - a goal must be hard to understand",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What does the A in SMART Goals stand for?",
            "difficulty" to "Medium",
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
                "choice" to "Achievable- a goal must be achievable",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Arrogant - a goal must be arrogant",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Aimless - a goal must not have direction",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Address - a goal must have address",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What does the R in SMART Goals stand for?",
            "difficulty" to "Medium",
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
                "choice" to "Range",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Realistic",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Redirection",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Revenge",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What does the T in SMART Goals stand for?",
            "difficulty" to "Medium",
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
                "choice" to "Tall",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Time-Bound",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Tenacious",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }

    private fun addGoalSettingPreActivityMedium(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How can setting financial goals help you?",
            "difficulty" to "Medium",
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
                "choice" to "It improves your overall financial well-being ",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It does not do anything",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It will not help you attain your goals in a realistic way",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It might decrease your motivation to achieve your financial goals",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "The following are ways financial goals can help you except",
            "difficulty" to "Medium",
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
                "choice" to "It leads you to waste your money",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Motivates you to save money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Provides you with direction",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Helps you save money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Which of the following is true about setting a financial goal?",
            "difficulty" to "Medium",
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
                "choice" to "Helps you decide what you want to achieve",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Makes it harder to achieve things as it adds pressure",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Makes you feel overwhelmed and discouraged if you don't achieve it quickly",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Pointless because everything is unpredictable",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Your friend Maria plans to set her own financial goals. All of the following are ways that financial goals can help her except..",
            "difficulty" to "Medium",
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
                "choice" to "Helps her to spend more money",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Motivates her to save money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Provides her with direction",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Helps her save money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Financial goals benefit you by…",
            "difficulty" to "Medium",
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
                "choice" to "All of the above",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Helping you save money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Increasing your motivation to save money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Providing you with a clear direction",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "An example of a medium-term financial goal is",
            "difficulty" to "Medium",
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
                "choice" to "Saving up for a big trip around the world",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To save up for a new toy or game that you really want within the next few months",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To buy a fancy car when you turn 18",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To save up for a college education",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Which of the following is a medium-term goal?",
            "difficulty" to "Medium",
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
                "choice" to "Saving for college",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for travel",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for clothes",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To save up for a college education",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Mark wants to save money for school supplies for the upcoming school year. What should he do?",
            "difficulty" to "Medium",
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
                "choice" to "Set a short-term goal since it’ll be cheap",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Set a medium-term goal since it’ll take a bit longer to save for",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy all the things now cause he’s rich",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Borrow money from friends so he can buy the school supplies right away",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Karly wants to buy a large toy which costs P1500. What type of goal should they set?",
            "difficulty" to "Medium",
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
                "choice" to "A short-term goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A medium-term goal",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A lonmg-term goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Do nothing",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Jessie plans to save up for a ticket to a movie theater. His target date is 2 weeks away. Is this a medium-term goal?",
            "difficulty" to "Medium",
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
                "choice" to "Yes",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A No",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Josh earns P500 from his weekly allowance. Is it realistic for him to buy a toy worth P2,000 next week?",
            "difficulty" to "Medium",
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
                "choice" to "Yes",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What part of SMART is missing from this goal? I will save until next week for a P1,000 shirt I don’t like.",
            "difficulty" to "Medium",
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
                "choice" to "Measurable, no target amount",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Time-bound, no duration",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Relevant, not important to you",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Achievable, too difficult",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Wesley will be saving his money to buy a video game within six months. Has he set a good time-bound goal?",
            "difficulty" to "Medium",
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
                "choice" to "Yes",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What part of SMART is missing from this goal? I will save for a P3,000 Buffet meal.",
            "difficulty" to "Medium",
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
                "choice" to "Measurable, no target amount",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Time-bound, no duration",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Achievable, too difficult",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Specific, no details",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }

    private fun addGoalSettingPostActivityMedium(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "The school supplies you need to buy cost P2,000 but you only have P400. What can you do to afford the supplies? ",
            "difficulty" to "Medium",
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
                "choice" to "Set a financial goal with a target amount of P1,600 ",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your mom for P1,600",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Tell your friend to buy it for you",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Decide not to buy the supplies",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "The gift you want to buy cost P1,000 but you only have P200. What can you do to afford the gift?",
            "difficulty" to "Medium",
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
                "choice" to "Set a financial goal with a target amount of P800 ",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Decide not to buy the gift",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Tell your friend to buy it for you",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "If you currently have P70 but the toy you want costs P1000. What can you do to eventually buy that toy?",
            "difficulty" to "Medium",
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
                "choice" to "Set a financial goal to prioritize saving money for the toy",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Borrow money from a friend",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask money from a stranger",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Steal money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "A medium-term goal is",
            "difficulty" to "Medium",
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
                "choice" to "Something you want to achieve in the near future, but not right away",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Something you don't really care about, but you have to do anyway",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Something you achieve in more than 10 years",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Something you achieve in one day or less",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What are some examples of medium-term financial goals?",
            "difficulty" to "Medium",
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
                "choice" to "Saving for a new car",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for college",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for school supplies",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Starting a business",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Is saving for school supplies a medium-term financial goal?",
            "difficulty" to "Medium",
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
                "choice" to "Yes",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Which of the following is NOT a medium-term financial goal?",
            "difficulty" to "Medium",
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
                "choice" to "Saving for a new car",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for clothes",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for school supplies",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for toys",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Josh earns P500 from his weekly allowance. Is it realistic for him to buy a toy worth P2,000 next week?",
            "difficulty" to "Medium",
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
                "choice" to "Yes",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What part of SMART is missing from this goal? I will save until next week for a P1,000 shirt I don’t like.",
            "difficulty" to "Medium",
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
                "choice" to "Measurable, no target amount",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Time-bound, no duration",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Relevant, not important to you",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Achievable, too difficult",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Wesley will be saving his money to buy a video game within six months. Has he set a good time-bound goal?",
            "difficulty" to "Medium",
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
                "choice" to "Yes",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What part of SMART is missing from this goal? I will save for a P3,000 Buffet meal.",
            "difficulty" to "Medium",
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
                "choice" to "Measurable, no target amount",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Time-bound, no duration",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Achievable, too difficult",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Specific, no details",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }

    private fun addGoalSettingPreliminaryHard(assessmentID: String) {
        var question= hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "If you want to save for a daytrip to Tagaytay worth P10,000, is this considered a “Specific” SMART goal?",
            "difficulty" to "Hard",
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
                "choice" to "Yes, the goal is detailed",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No, the goal is not detailed enough",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }

    private fun addGoalSettingPreActivityHard(assessmentID: String) {
       var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "A long-term goal is",
            "difficulty" to "Hard",
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
                "choice" to "Something you don't really care about, but you have to do it anyway",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Something that is impossible to achieve",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Something you can achieve in a day",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Something that takes a long time to save up for",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What are some examples of long-term financial goals?",
            "difficulty" to "Hard",
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
                "choice" to "Saving for a new toy or game",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for a burger",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for an instrument",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for a fruit",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "An example of long-term financial goal is",
            "difficulty" to "Hard",
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
                "choice" to "Saving for a new toy or game",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for a burger",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving money for a future trip with family",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for a fruit",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Is saving money to buy a notebook a long-term financial goal?",
            "difficulty" to "Hard",
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
                "choice" to "Yes",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving money for a future trip with family",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for a fruit",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Which of the following is NOT a long-term financial goal?",
            "difficulty" to "Hard",
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
                "choice" to "Saving money to buy a nice toy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for a burger",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving money to buy uniforms",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "All of the above",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Which of the following is a long-term goal?",
            "difficulty" to "Hard",
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
                "choice" to "Saving for a field trip",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving for a burger",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving money to buy uniforms",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving to buy candy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What part of SMART is missing from this goal? I will save P5,000 in 1 month to buy gifts.",
            "difficulty" to "Hard",
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
                "choice" to "Measurable, no target amount",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Time bound, no duration",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Achievable, to difficult",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Specific, not enough details like whom the gift is for",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How can you check if your goal is achievable?",
            "difficulty" to "Hard",
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
                "choice" to "Ask your parents",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask you friend",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Do not check",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Reflect on your current situation",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "If you want to save for groceries, is this considered a “Measurable” SMART goal?",
            "difficulty" to "Hard",
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
                "choice" to "Yes",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No, there is no target amount",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What part of SMART is missing from this goal? I will save for 2 months to treat my mom for her birthday.",
            "difficulty" to "Hard",
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
                "choice" to "Measurable, no target amount",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Time-bound, no duration",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Achievable, too difficult",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Relevant, not important to you",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What part of SMART is missing from this goal? I will save until next week for a P5,000 toy my friend is telling me to buy.",
            "difficulty" to "Hard",
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
                "choice" to "Measurable, no target amount",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Time-bound, no duration",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Achievable, too difficult",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Relevant, not important to you",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }

    private fun addGoalSettingPostActivityHard(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "A financial goal is",
            "difficulty" to "Hard",
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
                "choice" to "A way to spend all your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A way to earn more money ",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A way to improve your financial situation",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A way to ask your parents for money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Financial goals are…EXCEPT",
            "difficulty" to "Hard",
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
                "choice" to "A target you have for your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A direction you have with your money ",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A way to improve your financial situation",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A way to spend your money",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Why is goal setting important for improving your financial situation?",
            "difficulty" to "Hard",
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
                "choice" to "Allows you to focus on your expenses",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Helps you save all your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Helps you prioritize and focus on what matters to you",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to " Allows you to impress people around you",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Should you spend all your money now and worry about the future later?",
            "difficulty" to "Hard",
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
                "choice" to "It's better to use credit cards to cover future expenses",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to " No, it's important to think about the future and set financial goals",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Yes, because the future is uncertain",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It depends on how much money you have now",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Leslie decides to save money for an emergency fund, which of the following do you think she should do?",
            "difficulty" to "Hard",
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
                "choice" to "Spend her money on toys and games",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Set a goal and contribute regularly to it",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Save only a small amount when she feels like it",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Save a portion of it and spend it right away",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What should Abby do if ever she needs to use the emergency fund?",
            "difficulty" to "Hard",
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
                "choice" to "Replenish the fund as soon as possible",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Celebrate and buy something she has been wanting",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Stop saving money altogether",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Do nothing and just borrow money from her classmates",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Your friend is curious why you have a financial goal to save money for your emergency fund. What would you tell them?",
            "difficulty" to "Hard",
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
                "choice" to "Because you want to impress them",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "So that you can have money to use on toys and games",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Because your mom told you to make one",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "So that you would have money for emergencies like losing your belongings or an unexpected expense",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Why should you consider saving money to donate to a charity?",
            "difficulty" to "Hard",
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
                "choice" to "Because you want to impress them",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Because your parents told you to do it",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To make a positive impact on your community or the world",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To keep the money for yourself and spend it on things you want",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Why should you consider saving money to donate to a charity?",
            "difficulty" to "Hard",
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
                "choice" to "Ask your mom to donate on your behalf",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Set a financial goal to save money to donate",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ignore the charity",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Tell your friends to donate instead",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "If I want to save for a new guitar, I should set a:",
            "difficulty" to "Hard",
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
                "choice" to "Short-term goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Medium-term goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Long-term goal",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Jake decides to start saving money for his college, what should he do?",
            "difficulty" to "Hard",
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
                "choice" to "Set a short-term goal since it’ll be cheap",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Set a long-term goal since it’ll take a long time to save",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Do nothing. He should only start saving when he reaches college",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Request money from his friends so he can start saving",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Gab wants to throw himself a birthday party. What type of goal should he set?",
            "difficulty" to "Hard",
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
                "choice" to "A short-term goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A medium-term goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A long-term goal",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "nothing",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Harry earns P700 from his weekly allowance. Is it realistic for him to buy a toy worth P5,000 next week?",
            "difficulty" to "Hard",
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
                "choice" to "Yes",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Ben wants to save money for his birthday party but he has not set a target date. Is this a SMART goal?",
            "difficulty" to "Hard",
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
                "choice" to "No because it is not time bound",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Yes because he does not need a target date to be time bound",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Mark wants to buy a gaming console worth P50,000. Is it realistic for him to save for it?",
            "difficulty" to "Hard",
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
                "choice" to "Yes, he can save enough money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No, the amount is too big for his age",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What part of SMART is missing from this goal? I will save until next year for a P1,000,000 car.",
            "difficulty" to "Hard",
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
                "choice" to "Measurable, no target amount",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Time-bound, no duration",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Specific, no details",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Achievable, too difficult",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addBudgetingPreliminaryEasy(assessmentID: String){
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a budget?",
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
                "choice" to "A plan for how to manage your money",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A plan for wasting money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A plan for making more money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What should you do after creating your budget?",
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
                "choice" to "Never look at it again",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Review it to see if you missed anything",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Delete your budget",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "After creating your budget, remember to",
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
                "choice" to "Review and adjust your budget items and amounts if needed",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Not look at the budget anymore",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Show your friends your budget",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

    }
    private fun addBudgetingPreActivityEasy(assessmentID: String){
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a budget when it comes to spending money?",
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
                "choice" to "Something you wear",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A plan for how you will spend your money",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Something you use to count your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A toy that helps you learn about money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "I should budget because",
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
                "choice" to "Budgets look cute",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It will help guide my spending",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It will help you make more money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How can you budget your money?",
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
                "choice" to "By listing budget items categories and budget amounts",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By spending all your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By never using your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "When creating a budget, it is important to",
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
                "choice" to "List down some budget items",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "List down ALL possible budget items",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Only list down a few budget items",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "If you want to have 2 budget items, food, and entertainment. How should you divide your money between these 2?",
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
                "choice" to "Put all the money into the food item",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Put all the money into the drinks item",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Divide the money between the two items",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You want to buy a small toy. What budget item should you create to include the toy?",
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
                "choice" to "Food",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Toys & Games",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Transportation",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Entertainment",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You are planning to buy your mom a gift for her birthday. What budget item should you create?",
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
                "choice" to "Gifts",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Toys & Games",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Transportation",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Personal Care",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Tim wants to buy a new shirt. What budget item should he create?",
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
                "choice" to "Clothes",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Food & Drinks",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Transportation",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Personal Care",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addBudgetingPostActivityEasy(assessmentID: String){
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How can you make sure you have enough money to buy the things you need?",
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
                "choice" to "Spend all your money as soon as you get it",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your parents for more money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Budget your money and spend it wisely",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Sell your toys to make extra money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "If you have a budget of P100 for toys, a toy that costs P150 is",
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
                "choice" to "Over your budget",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Equal to your budget",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Under your budget",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Your friend has the habit of overspending. What budgeting advice would you give them?",
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
                "choice" to "I would advise them to list budget items and their amounts to use as a guide",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "I would tell them to spend as much as they want",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "I would advise them to stop spending their money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Your want to buy another snack but you are not sure if you have the budget for it. What should you do?",
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
                "choice" to "Check how much you have spent and see how much you have remaining",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Do not check your budget and buy the snack",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your mom to buy you the snack",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Benny set a budget of P100 for food. He has already spent P90. Does he have the budget to buy candy that costs P10?",
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
                "choice" to "Yes",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Tom set a budget of P120 for toys. He saw a cool racecar that costs P125. It is…",
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
                "choice" to "Over his budget",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Under his budget",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Equal to his budget",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "These are important to do when creating a budget EXCEPT:",
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
                "choice" to "List down budget items like Snacks” and Entertainment",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Allot an amount for each budget item based on your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Review your budget items and their amounts",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Budget more than the money you have",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "When creating a budget keep these in mind EXCEPT:",
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
                "choice" to "Allot an amount for each budget item based on your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Review your budget items and their amounts",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Adjust your budget if needed",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Do not add any budget items to your budget",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "These are important to do when creating a budget EXCEPT:",
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
                "choice" to "List down budget items like Snacks” and Entertainment",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Allot an amount for each budget item based on your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Review your budget items and their amounts",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Budget more than the money you have",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "When creating a budget keep these in mind EXCEPT:",
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
                "choice" to "Allot an amount for each budget item based on your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Review your budget items and their amounts",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Adjust your budget if needed",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Do not add any budget items to your budget",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addBudgetingPreliminaryMedium(assessmentID: String){
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Why is it important to set a budget for your money?",
            "difficulty" to "Medium",
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
                "choice" to "To spend all your money at once",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To show off to your friends",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To have enough money for the things you need",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To forget about your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What are some ways you can manage your money better and make it go further?",
            "difficulty" to "Medium",
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
                "choice" to "Planning by creating a budget",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Spending all your money on video games",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buying expensive toys and gadgets",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Giving your money away to friends",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What do you think about creating a budgeting plan?",
            "difficulty" to "Medium",
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
                "choice" to "It is a waste of time and effort, better to just spend money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It is only for adults, and it's not something that kids need to worry about",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Can help you manage your money better and achieve your financial goals",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Creating a budgeting plan is too complicated and requires too much math, which is boring",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addBudgetingPreActivityMedium(assessmentID: String){
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "A budget is..",
            "difficulty" to "Medium",
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
                "choice" to "A plan for how you’ll spend to ensure you have enough money your needs and wants",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "a guide for you to know how to give away your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A plan to spend all of your money on unnecessary expenses",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A grocery list of items that you plan to purchase at the store",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "I should budget because",
            "difficulty" to "Medium",
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
                "choice" to "My mom told me so",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "I can spend more money on things I like",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "My friends and classmate are also doing it",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It helps guide my spending",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Budgeting allows you to do all the following except..",
            "difficulty" to "Medium",
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
                "choice" to "It helps me control my spending",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It allows me to spend all my money right away",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It helps me prioritize my spending",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It can help me plan for my expenses",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "The ability to plan and control your future expenses is one of the benefits of budgeting",
            "difficulty" to "Medium",
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
                "choice" to "True",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "False",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "It is important for kids like me to create and set budget because..",
            "difficulty" to "Medium",
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
                "choice" to "It helps me control my spending",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It helps me prioritize my spending",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It can help me plan for my expenses",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "All of the above",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Your mom asked you to create a budget for grocery shopping next week. She wants to make a meal containing pork, apples, and pepper. What budget items should you set?",
            "difficulty" to "Medium",
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
                "choice" to "Meat, Fruit, Condiments",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Food",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Meat",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Vegetables",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Your first day of school is coming up. You decided to shop for school supplies. What budget items should you set?",
            "difficulty" to "Medium",
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
                "choice" to "Clothing, stationary",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Food, transportation, clothes",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Drinks, toys & games",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "None of the above",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You are planning to go grocery shopping with your mom. She says that you should prioritize buying food over drinks. This means that…",
            "difficulty" to "Medium",
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
                "choice" to "You should allot a bigger budget item amount for food",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "You should allot a bigger budget item amount for drinks",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "You should allot the same budget item amount for both",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "You should not budget at all",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Summer is ending and it is time to buy back to school materials. Which budget item is NOT appropriate to budget for",
            "difficulty" to "Medium",
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
                "choice" to "Toys & games",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Clothing",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Food & drinks",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Stationery",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Jason is hosting a dinner. What budget items would you advise him to include in his budget?",
            "difficulty" to "Medium",
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
                "choice" to "Food & drinks, Decor, Entertainment",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Clothing",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Toys & games",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Venue",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addBudgetingPostActivityMedium(assessmentID: String){
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Your friends tell you to not budget anymore and instead to just spend your money. Do you think you should follow their advice? Why or why not?",
            "difficulty" to "Medium",
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
                "choice" to "Yes",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No, budgeting and planning is important",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What are some things you might include in a budget?",
            "difficulty" to "Medium",
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
                "choice" to "No budget items",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Your favorite TV shows and movies to buy or rent",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Any money you find on the street",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Budget items like “Food & Drinks” and “Toys",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Dave’s mom told him that it is important to budget so he would have enough money to buy the things he needs. Do you think this is true? Why or why not?",
            "difficulty" to "Medium",
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
                "choice" to "True",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "false",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What do you think about unplanned expenses in budgeting?",
            "difficulty" to "Medium",
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
                "choice" to "They don't really exist, you should spend your money however you want",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "They can be avoided",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "They can happen to anyone, which is why its important to include in your budget",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "You should just borrow money from someone else",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Do you think having an extra budget item for unplanned expenses is a good idea?",
            "difficulty" to "Medium",
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
                "choice" to "Yes, so that I will be prepared",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No, I do not need to prepare",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "When creating a budget, which of the following are part of unplanned expenses?",
            "difficulty" to "Medium",
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
                "choice" to "Emergency expenses, such a gift for your friend",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Unexpected purchases such as a school project that requires extra supplies",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Unforeseen events, such as a natural disaster",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "All of the above",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "The night before her first day of school, Mary realized that she forgot to buy pens. Luckily, she had saved extra money. Why is that?",
            "difficulty" to "Medium",
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
                "choice" to "She budgeted for unplanned expenses",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "She was visited by the tooth fairy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "She found money on the floor",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "She asked her parents for money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addBudgetingPreliminaryHard(assessmentID: String){
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What are the benefits of budgeting for your money?",
            "difficulty" to "Hard",
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
                "choice" to "Being able to buy everything you want",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Knowing where your money is going",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Making sure you always have the latest items",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Showing off to others how much money you have",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Which of the following is a correct concept of budgeting?",
            "difficulty" to "Hard",
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
                "choice" to "Helps you manage your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Allows you to plan for your expenses",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Helps you make informed choices",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "All of the above",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Which of the following is a wrong concept of budgeting?",
            "difficulty" to "Hard",
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
                "choice" to "Helps you manage your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Helps you save money for your planned expensees",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Helps you make informed choices",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "None of the above",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How does budgeting help you for your future?",
            "difficulty" to "Hard",
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
                "choice" to "Helps you manage your money effectively",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Makes you a boring person",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Only for rich people and doesn't help regular kids like me",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It is too complicated and kids can't understand it",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What do you think is the reason why it is necessary to understand the concept of budgeting?",
            "difficulty" to "Hard",
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
                "choice" to "Helps you make sure you have enough money to spend",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Allows you to spend all your money on toys and candy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "So that you can buy the expensive things without worrying",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It is boring and unnecessary",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Why is it important to account for unexpected expenses when creating a budget?",
            "difficulty" to "Hard",
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
                "choice" to "Because unexpected expenses can occur at any time and can significantly impact you financially",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Unexpected expenses are very rare so no need to include them",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "If unexpected expenses arise, you can borrow money from your friends",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Not important since you can ask for money from your parents",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addBudgetingPreActivityHard(assessmentID: String){
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "The following are the importance of budgeting except",
            "difficulty" to "Hard",
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
                "choice" to "Helps manage your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Help save money for the future",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Help you make informed spending choices",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Not important since your parents can do it for you",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is the purpose of budgeting when you want to plan a trip for vacation?",
            "difficulty" to "Hard",
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
                "choice" to "To buy as many souvenirs as possible during the trip",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To make sure you spend all of your money on the trip",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To plan an expensive trip, regardless of the cost",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To make sure you have enough money for everything you need and want",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "The following are good budgeting plans except..",
            "difficulty" to "Hard",
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
                "choice" to "Allows me to spend all my money right away",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Makes me want to give my money to my friends",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Allows me to plan for where my money is going",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "None of the above",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You noticed that you are frequently going over the amounts you set for your budget items. What should you NOT do?",
            "difficulty" to "Hard",
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
                "choice" to "Take note of the budget items you are struggling with and be more mindful when spending",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Budget more money towards those specific budget items",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Do nothing and always overspend",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Review your budget and think about where you will need the money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "These are the steps of budgeting except",
            "difficulty" to "Hard",
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
                "choice" to "Think about budget items you will spend for",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "List all budget items down",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Think about how much money you will be needed for each item",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Spend without making a budget",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "If you are planning to throw a birthday party out of town, which budget items should you anticipate? Select the most complete.",
            "difficulty" to "Hard",
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
                "choice" to "Party favors, food",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Party favors, food, entertainment, transportation",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Food, entertainment",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "If you are planning to have a vacation with your friends, which budget items should you include? Select the most complete.",
            "difficulty" to "Hard",
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
                "choice" to "Clothing, food & drinks, transportation",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Clothing",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Transportation",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "None",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How can you use budgeting to plan for unexpected expenses or emergencies?",
            "difficulty" to "Hard",
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
                "choice" to "Ignore the possibility of unexpected expenses or emergencies",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Rely on your parents or friends",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Do nothing and just wait for the time to come",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Increase your budget item amounts or add a budget item for unexpected expenses",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Paul decides to create a budget for an emergency fund, which of the following is important?",
            "difficulty" to "Hard",
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
                "choice" to "An emergency fund is money that you set aside for unexpected expenses or emergencies",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "When creating a budget for an emergency fund, he should first determine how much money he need to save based on the allowance that your parents give him",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It's important to create a budget for an emergency fund because unexpected expenses can happen at any time",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "All of the above",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "If you are planning to throw a birthday party at home, which budget items should you anticipate? Select the most complete",
            "difficulty" to "Hard",
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
                "choice" to "Party favors, food",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Party favors, food, entertainment",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Food, entertainment",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Your class has a food booth for your school fair. What budget items should you include? Select the most complete",
            "difficulty" to "Hard",
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
                "choice" to "Entertainment",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Decor, food & drinks",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Food & drinks",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Transportation, Food & drinks, entertainment",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Your class has a game booth for your school fair. What budget items should you include? Select the most complete.",
            "difficulty" to "Hard",
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
                "choice" to "Entertainment, decor",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Food & drinks, entertainment",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Transportation, clothing",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Clothing",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "If you are planning to have a vacation with your friends, which budget items should you include? Select the most complete.",
            "difficulty" to "Hard",
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
                "choice" to "Clothes, shoes, snacks, backpack",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Clothes and snacks only",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Backpack only",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "None",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You will go to your school fair. What are budget items you can budget for? Select the most complete.",
            "difficulty" to "Hard",
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
                "choice" to "Food & drinks, toys & games, entertainment",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Entertainment, clothing",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Food & drinks",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Transportation",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addBudgetingPostActivityHard(assessmentID: String){
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What did you learn about the benefits of budgeting from this activity?",
            "difficulty" to "Hard",
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
                "choice" to "Budgeting is a waste of time and doesn't help you save money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Budgeting helps you manage your money, make informed spending choices, and save money for the future",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Budgeting only benefits rich people, not ordinary people",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "The only benefit of budgeting is that it helps you show off to others how much money you have",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How do you plan to apply what you learned about budgeting to your own finances?",
            "difficulty" to "Hard",
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
                "choice" to "I don't plan to apply what I learned because budgeting is too complicated",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Apply what I learned by creating a budget and tracking my expenses to make sure I have enough money for everything I need and want",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "I plan to use what I learned to buy more expensive things",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "I don't need to apply what I learned because my parents manage my finances for me",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "An example of an emergency fund are the following..",
            "difficulty" to "Hard",
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
                "choice" to "Pet care expenses for a pet that becomes sick or injured",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Replacement of a broken phone or tablet",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Unexpected school fees or supplies",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "All of the above",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addSavingPreliminaryEasy(assessmentID: String){
    }
    private fun addSavingPreActivityEasy(assessmentID: String){
    }
    private fun addSavingPostActivityEasy(assessmentID: String){
    }
    private fun addSavingPreliminaryMedium(assessmentID: String){
    }
    private fun addSavingPreActivityMedium(assessmentID: String){
    }
    private fun addSavingPostActivityMedium(assessmentID: String){
    }
    private fun addSavingPreliminaryHard(assessmentID: String){
    }
    private fun addSavingPreActivityHard(assessmentID: String){
    }
    private fun addSavingPostActivityHard(assessmentID: String) {
    }
    private fun addSpendingPreliminaryEasy(assessmentID: String){
    }
    private fun addSpendingPreActivityEasy(assessmentID: String){
    }
    private fun addSpendingPostActivityEasy(assessmentID: String){
    }
    private fun addSpendingPreliminaryMedium(assessmentID: String){
    }
    private fun addSpendingPreActivityMedium(assessmentID: String){
    }
    private fun addSpendingPostActivityMedium(assessmentID: String){
    }
    private fun addSpendingPreliminaryHard(assessmentID: String){
    }
    private fun addSpendingPreActivityHard(assessmentID: String){
    }
    private fun addSpendingPostActivityHard(assessmentID: String) {
    }
}
