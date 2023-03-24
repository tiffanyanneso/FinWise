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
            "assessmentCategory" to "Budgeting",
            "assessmentType" to "Preliminary",
            "description" to "Preliminary assessment for budgeting",
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
            "assessmentCategory" to "Budgeting",
            "assessmentType" to "Pre-Activity",
            "description" to "Pre-Activity assessment for budgeting",
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
            "assessmentCategory" to "Budgeting",
            "assessmentType" to "Post-Activity",
            "description" to "Post-Activity assessment for budgeting",
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
            "assessmentCategory" to "Saving",
            "assessmentType" to "Preliminary",
            "description" to "Preliminary assessment for saving",
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
            "assessmentCategory" to "Saving",
            "assessmentType" to "Pre-Activity",
            "description" to "Pre-Activity assessment for saving",
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
            "assessmentCategory" to "Saving",
            "assessmentType" to "Post-Activity",
            "description" to "Post-Activity assessment for saving",
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
            "assessmentCategory" to "Spending",
            "assessmentType" to "Preliminary",
            "description" to "Preliminary assessment for spending",
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
            "assessmentCategory" to "Spending",
            "assessmentType" to "Pre-Activity",
            "description" to "Pre-Activity assessment for spending",
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
            "assessmentCategory" to "Spending",
            "assessmentType" to "Post-Activity",
            "description" to "Post-Activity assessment for spending",
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
                "choice" to "A long-term goal",
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
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "When should you start saving money?",
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
                "choice" to "When you're an adult",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "When you have a job",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "You don't need to save money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "When you’re a kid",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Is it better to save money or spend it all?",
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
                "choice" to "Spend it all right away",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Save some for the future",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It doesn't matter",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Save nothing",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is the best way to save money?",
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
                "choice" to "By setting aside some money frequently",
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
                "choice" to "By setting aside all your money all the time",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By not setting aside your mone",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "If you have a goal to buy fried chicken worth P150, you should:",
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
                "choice" to "Steal money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Wait until the day before to start saving",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Set aside money to your goal savings early on",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your classmate to buy it for you",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How can earning help you achieve your financial goals?",
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
                "choice" to "Can help you reach your financial goals faster",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Earning money isn't important for achieving financial goals",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It's better to rely on someone else, like your parents, to fund your financial goals",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It's too hard to earn money when you're young, so it's not worth trying to earn anything",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You can get money to save from these sources EXCEPT:",
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
                "choice" to "Allowance",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Bills",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Gifts",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Rewards",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is delayed gratification?",
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
                "choice" to "Buying something you want immediately",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Resisting temptation for something better in the future",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Doing everything you want",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Giving in to temptation",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "The following are reasons to practice delayed gratification EXCEPT:",
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
                "choice" to "You can make better decisions",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It will benefit you in the future",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "You will be sad at first",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It will help you achieve your goals",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addSavingPreActivityEasy(assessmentID: String){
       var question = hashMapOf(
           "assessmentID" to assessmentID,
           "question" to "If you received a lot of money gifts for Christmas, it is best to:",
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
                "choice" to "Spend some of the money and save the rest for the future",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Spend all of the money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Not spend any of the money and be sad",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Give and share your money to your friends",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "It was your birthday recently and you received a lot of money gifts. What should you do?",
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
                "choice" to "Spend some of the money and save the rest for the future",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Spend all of the money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Not spend any of the money and be sad",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Give and share your money to your friends",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Your friend, Jacob does not have enough money to buy a toy he likes. He mentioned that he does not save his allowance. What advice can you give him?",
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
                "choice" to "Save some of your allowances each week",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Continue spending your entire allowance",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your mom to buy the toy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your friends for money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How much money should you save from your allowance?",
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
                "choice" to "How much money should you save from your allowance?",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Only save a very little amount of my allowance",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Most of your allowance",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A random amount each time",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You can save money by doing these EXCEPT",
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
                "choice" to "Putting money in a piggy bank",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Setting financial goals",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Spending your whole allowance",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Keeping track of your income and expenses",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "If you get an allowance of P100 per week, what is the best way to save for a financial goal?",
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
                "choice" to "Spend the whole P100 in one day",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Set aside some of your allowances for your financial goal savings",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Wait until your financial goal is due tomorrow before you set aside",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You recently got a piggy bank. What can you do with it?",
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
                "choice" to "Play with it",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Save some of your money by placing coins and bills in it",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Open it immediately",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Give it to your friend",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You received a lot of rewards for doing chores at home. You can save money by:",
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
                "choice" to "Spending all of the money you earned",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Treating your neighbors to merienda",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Setting aside some of the money you earned",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buying a bucket of fried chicken",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How can you earn money to save?",
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
                "choice" to "Do chores for a reward",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Steal money from others",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Beg for money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Steal and sell things that belong to others",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Jasmine plans to earn some money for a special toy that she wants. What should she do?",
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
                "choice" to "Beg her parents to buy it for her",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Steal money from her siblings or friends",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Offer to do chores",
                "isCorrect" to true
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
            "question" to "These are some ways you can earn money EXCEPT:",
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
                "choice" to "Weekly allowances",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Rewards from helping with chores",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buying food from the canteen",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Gifts from Ninangs and Ninongs",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You want to buy a very cool book but do not have enough money. What can you do?",
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
                "choice" to "Ask your parents if there are any chores you can do to get a reward",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your parents to buy you the book",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your friend to buy you the book",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Steal the book",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Your mom asks you to sweep the floor for a reward. What should you do after?",
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
                "choice" to "Spend the money immediately on ice cream",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Save the money for a toy you have been eyeing",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Spend the money on a less cool toy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Treat yourself to some french fries",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Your mom said that she would give you a reward if you help her clean the dishes. What is the best course of action?",
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
                "choice" to "Help her and save the money you earned",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Refuse and say that washing the dishes is boring",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Help her and spend the money you earned on ice cream",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Refuse because you will play with your friend",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How can delaying gratification help you save money?",
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
                "choice" to "You spend more money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "You can control your wants for a better reward",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "You lose your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "You earn money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Your friend bought yummy looking candy but you were planning on eating at KFC for merienda. What should you do?",
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
                "choice" to "Buy the candy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Save your money for KFC later",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your friend to buy you candy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy the candy and ask your mom to buy you KFC",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You pass by an ice cream place on the way home but you’re in the process of  saving up for an awesome toy. What should you do?",
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
                "choice" to "Buy ice cream",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Do not buy the ice cream & save your money for the toy",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy ice cream for you and your friend",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy ice cream for your mom",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Jen is saving up for a Yumburger but she saw a vendor selling french fries. What should she do?",
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
                "choice" to "Avoid the temptation and save her money for the burger",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy the french fries and finish her money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy french fries and ice cream",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask her mom to buy her the burger instead",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You pass by a Mcdonald’s on the way home but you’re in the process of saving up for a cool game. What should you do?",
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
                "choice" to "Buy fried chicken",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Save your money for the game",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy french fries",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Forget about the game",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Mark is saving up for a bucket of chicken but he passed by a vendor selling chips. What should he do?",
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
                "choice" to "Avoid the temptation and save his money for the chips",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Forget about the chicken",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy all the chips",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy chips and ice cream",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addSavingPostActivityEasy(assessmentID: String){
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "When you get money, what's the first thing you should do with it?",
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
                "choice" to "Spend it right away",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Give it to a friend",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Save some of it",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Throw it away",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Why should you save your money?",
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
                "choice" to "So you have money to use in the future",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "So that you will be sad now",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "So that your mom will be proud",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What should you do if you want to buy something that you can’t afford right now?",
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
                "choice" to "Borrow money from a friend",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Steal money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Save up for it",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Give up and not buy it",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How can you increase the amount of money you save?",
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
                "choice" to "Spend more money than you make",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "b. Save money from your allowance and any extra money you receive",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Only buy expensive things",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "d. Give away all your money to other",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How can you increase the amount of money you save?",
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
                "choice" to "Spend more money than you make",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Save money from your allowance and any extra money you receive",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Only buy expensive things",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Give away all your money to other",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "If you save P100 every week, this means that you will",
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
                "choice" to "Have less money in the future",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Have more money in the future",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Have more money in the past",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Have less money in the past",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "At the end of the week, you discover that you have some money left from your allowance. What should you do?",
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
                "choice" to "Spend it all on french fries",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy a small snack and save the rest of your money",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Eat outside and treat all your friends",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy the things you want right away",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Angela bought French Fries, but she still has P50 left. What should she do if she wants to save?",
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
                "choice" to "Buy a chocolate sundae",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Set the remaining money aside to her savings",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy more french fries",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "The following are ways you can save money EXCEPT:",
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
                "choice" to "Place your money in a piggy bank",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Set financial goals",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Eat out everyday",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Set aside money from your allowance",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Can you make money by helping out at home?",
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
                "choice" to "No, you should only focus on making money for yourself",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Yes, by doing chores",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Yes, by stealing from others",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No, helping others won't earn you any money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You have received a lot of money gifts from your Ninangs and Ninongs. What can you do to save?",
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
                "choice" to "Go to the nearest toy store and spend it all",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy one toy and save the rest of the money",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Treat your whole family to dinner",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy the biggest toy and spend all the money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "An example of delayed gratification is:",
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
                "choice" to "Buying the toy you want immediately",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving money for a nice toy instead of spending it on candies",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buying candies",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Asking your mom to buy you a nice toy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "The following are examples of delaying gratification EXCEPT:",
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
                "choice" to "Immediately buying a cool toy",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Opting to wait for lunch time to buy food instead of buying candy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving money for a nice meal instead of spending it on snacks",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Not buying a small now for a bigger toy later",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addSavingPreliminaryMedium(assessmentID: String){
        var   question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Saving is…",
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
                "choice" to "Is the act of putting money aside for future use",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "The act of spending money right away",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "The act of borrowing money from friends",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "The act of doing nothing",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Why is it important to save money?",
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
                "choice" to "So you can buy the most expensive things",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "So you can show off to your friends",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "So you have enough money to buy the things you really want and  need",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "So you can never spend it and just watch it grow",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "One of your cousins, Michelle told you that you should stop saving money and start spending it right away. Should you follow her advice?",
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
                "choice" to "Yes, because she knows best",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No, saving allows you to have money in the future",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Tracking your income and expenses can help you save EXCEPT",
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
                "choice" to "Provides awareness of where your money is going",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No, saving allows you to have money in the future",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Gives you motivation to stay focused on goals",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Makes you spend all your money",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Zoie noticed that your are tracking your income and expenses. How would you explain the reason why?",
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
                "choice" to "To be aware of your financial situation & spend less to save more",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To have your parents pay you back for your expenses",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To brag about how much money you earn",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To make your classmates jealous",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Why is it important to earn money?",
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
                "choice" to "It is not important because you can rely on other",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It is important so that you can have money to save or spend",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It will make you popular and people will like you more",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "You can use it to buy things that you don't really need, but just want for fun",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "These are reasons why practicing delayed gratification is important except…",
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
                "choice" to "Allows you to make bigger & better purchases",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Allows you to think about what you really want",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Allows you to buy everything immediately",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Allows you to save more money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Delayed gratification is important because…",
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
                "choice" to "It allows you to make bigger & better purchases if you wait",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It allows you to buy everything you want",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Allows you to buy everything immediately",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It allows you to spend all your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Why is it important to delay gratification?",
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
                "choice" to "Not important because you should enjoy everything in life immediately",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Not important to delay gratification because waiting for things just makes you sad and bored",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Important because it can help you save for bigger & better things",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "There's no need to delay gratification because you can always find a way to get what you want right now",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addSavingPreActivityMedium(assessmentID: String){
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Which of the following is a good example of saving for something you want?",
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
                "choice" to "Saving for a new video game",
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
                "choice" to "Saving for movie ticket",
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
            "question" to "What is the purpose of saving when you want to buy school supplies?",
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
                "choice" to "To have enough money to pay for pencils, stationary, and uniforms",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To spend all the money on entertainment",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To buy expensive and unnecessary items",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To spend all the money on treats at school",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Jenny is having trouble buying the things she wants and needs. What advice can you give her?",
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
                "choice" to "Set aside small amounts of money consistently",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Continue buying everything she wants",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Stop buying anything",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask her parents for more allowance",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Tanya forgot to save money for her school supplies. Now, she will not have new pencils for school. What should she have done?",
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
                "choice" to "Continue forgetting to save",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Set aside money from her allowance and rewards",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask her mom to buy supplies for her",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Get supplies from her school",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is the purpose of saving when you want to go grocery shopping?",
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
                "choice" to "To have enough money to pay for snacks and food",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To spend all the money on entertainment",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To buy expensive and unnecessary items",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To spend all the money on treats",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Patricia is having a difficult time reaching her financial goals. How can she better save for her goals?",
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
                "choice" to "Evaluate what her income sources are and save money from them",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Stop and give up on her financial goals",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Borrow money from her friends or parents",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Spend money so she would be able to cheer herself up",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Martin wants to start tracking the money he earns and spends so he can better save his money. What can he use to help him?",
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
                "choice" to "Nothing",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Scratch paper",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "His brain to keep track of everything",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Application that tracks income and expenses",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What are some ways you can earn money?",
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
                "choice" to "By doing household chores and selling",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By stealing",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By borrowing money from parents",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By begging for money from my friends",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What are some things you can do to earn money?",
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
                "choice" to "Do nothing",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By stealing",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By borrowing money from parents",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Do household chores",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What are ways for you to earn money by doing chores at home?",
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
                "choice" to "By cleaning my room",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By helping set the table",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By washing the dishes",
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
            "question" to "Jonathan decides to carry out some chores in his house so he can earn money. The following chores are what Jonathan can do in order to earn money",
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
                "choice" to "Wiping the table",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By helping set the table",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By washing the dishes",
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
            "question" to "You want to buy a new school supplies, but you don't have enough money saved. What are some ways you can earn extra money to afford it?",
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
                "choice" to "Do chores or sell items",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your parents for more allowance",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Forget about your school supplies",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your teacher for supplies",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Your mom said that she would give you a reward if you help her go grocery shopping. What is the best course of action?",
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
                "choice" to "Help her and save the money you earned",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Refuse and say that shopping is boring",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Help her and spend the money you earned on ice cream",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Refuse because you will play with your friend",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What should you do if you want to purchase a large toy however, your mom tells you to think about it first?",
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
                "choice" to "You should demand that your mom buy you the toy right away",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It’s mportant to listen to her and consider her advice since it gives you more time to think about it carefully",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "You should tell your mom that you don't care about her opinion and buy the toy anyway",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "You should sneak away from your mom and buy the toy without telling her",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addSavingPostActivityMedium(assessmentID: String){
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "I should save because…",
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
                "choice" to "Everyone else is doing it",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It want to impress my friends",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "I don’t want to spend my money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It can help me buy things I want in the future",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Saving money allows you to do all the following except..",
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
                "choice" to "Allows you to buy things you really want in the future",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Allows you to be prepared for unexpected expenses",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Allows you to make a smart financial decisions",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Allows you to spend money without thinking",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How can you meet your medium-term goals?",
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
                "choice" to "By constantly setting aside money",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By constantly eating out",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By asking your parents for money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "By telling your friends to give you money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Nicole has finally reached her financial goals, which of the following do you think she did to accomplish them?",
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
                "choice" to "She tracked her income and expenses",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "She kept her target date and amount in mind",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "She saved regularly and consistently",
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
            "question" to "Tanya wants to buy school supplies. She can do the following things to do so EXCEPT",
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
                "choice" to "Save a small amount of money regularly",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Set a financial goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Track income and expenses",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Spend money on food and toys",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Ali wants to buy a large toy. She can do the following things to do so EXCEPT",
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
                "choice" to "Save a small amount of money regularly",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Set a financial goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Track income and expenses",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Spend money on food and toys",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Mary wants to buy a new keyboard. How do you think she should save up for it?",
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
                "choice" to "Save all her money in a piggy bank",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Create a goal by and setting a realistic timeline to save for it",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask her parents for the money to buy what she wants",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Spend all her money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You have a financial goal of saving for a school backpack. How can you save enough money for it?",
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
                "choice" to "Limit your expenses and deposit a part of your income",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your parents to buy it for you",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Forget about the school backpack",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your school for a backpack",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
          question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You have a financial goal of saving for a new book. How can you save enough money for it?",
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
                "choice" to "Ask your parents to buy it for you",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Forget about the book",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Limit your expenses and deposit a part of your income",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your library for a book",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You plan to are saving for your goals and your mom asks you to help her prepare dinner for a reward. What should you do?",
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
                "choice" to "Accept the offer for the reward",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Decline and say you would rather play",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Sleep instead",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Tell your mom to do it by herself",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You are struggling to save enough money for your financial goals. What can you do?",
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
                "choice" to "Earn money by doing chores",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Continue spending your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Waiting for allowances or gifts",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Borrowing money from your classmates",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Justin wants to buy a game today but remembers that he is saving for a bigger and better game. What should he do?",
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
                "choice" to "Buy the game he wants now",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Wait and save more money to buy the better game",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Tell his mom to get the game now",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Get neither games",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You have been saving up for a nice keyboard for a while now. However, you see an ok looking toy. What should you do?",
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
                "choice" to "Buy the toy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Forget about the keyboard",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Delay your gratification and continue saving for the toy",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy both",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "For Christmas, you want to buy a nice toy. You see a cool one in the mall but know that if you waited a bit longer, you’d have money to buy an even nicer one. What should you do?",
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
                "choice" to "Buy the toy immediately",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Wait so that you can buy the better toy",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your mom to buy you both toys",
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
            "question" to "You are back to school shopping and want to buy a cool pencil case. You see one now but know you can save money for a better one. What should you do?",
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
                "choice" to "Delay your gratification and wait to buy a pencil case",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy the ugly pencil case now",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your mom to buy both pencil cases for you",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You have been saving up for a nice toy for a while now. However, you see an ok looking toy. What should you do?",
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
                "choice" to "Buy the toy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Forget about the nice toy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Delay your gratification and continue saving for the nicer toy",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

              choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy both",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You are back in the grocery and want to buy candies. You see one brand now but know you can save money for a better one. What should you do?",
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
                "choice" to "Delay your gratification and wait to buy the candy you like",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy the ok tasting candy case now",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your mom to buy both candies for you",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addSavingPreliminaryHard(assessmentID: String){
        var  question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What are the benefits of saving your money?",
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
                "choice" to "It can help you reach your goals, like buying a new toy or going on a special trip",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "No benefit since it is only for adults",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "You will be able to buy the things you want",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It means you can’t buy anything fun",
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
                "choice" to "Yes, because the future is uncertain",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to " No, it's important to think about the future and save for it",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It's better to use credit cards to cover future expenses",
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
            "question" to "What is the best way to save money for a long-term goal?",
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
                "choice" to "Save all of your money in a piggy bank",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Keep all of your money in your pocket",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Deposit money to a financial goal or savings account",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Give all of your money away to charity",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "A savings account is",
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
                "choice" to "A bank account where your money will grow",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A place to spend your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A magical place",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A theme park",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What are ways you can earn money as a kid?",
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
                "choice" to "Ask parents for more allowance",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Sell lemonade or homemade crafts",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Beg for money on the street",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Play video games",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Aside from selling lemonade or homemade crafts, which of the following skill-based jobs can also give you earnings?",
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
                "choice" to "Playing with pets",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Watching TV",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Walking in the street",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Cleaning and organizing rooms at home",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Ken wants to earn money, however, he doesn’t know what to do. What advice can you give him?",
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
                "choice" to "Try selling something–clothes, food, or handmade goods",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask for money from his parents",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "He can ask money from his friends",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Steal money from his parents",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Hans realizes that he needs money for his long term financial goals. What can he do to start earning money now?",
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
                "choice" to "Selling homemade crafts or starting a simple business",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask for money from his parents",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "He can ask money from his friends",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Steal money from his parents",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Your friend Lana enjoys baking crinkles. She mentions that she would like to earn money. What can you advise her to do?",
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
                "choice" to "Start selling her crinkles",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Never sell her crinkles",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Give your crinkles",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Why is it important to practice delayed gratification?",
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
                "choice" to "It is important because you should always get what you want right away",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It can help you achieve your long-term goals by allowing you to save money and work towards bigger, more meaningful accomplishments",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It is important because it's too hard to wait and be patient",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

             choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It is important because it's more fun to have things right now instead of waiting",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How does delayed gratification benefit you in the long run?",
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
                "choice" to "It benefit you because you never get to have fun or enjoy anything",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It benefit you because you might change your mind and not want the thing you were waiting for anymore",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It benefit you because it's too hard to wait and be patient",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Delayed gratification can also help you appreciate things more when you finally get them, and can help you avoid making impulsive decisions that you may regret later on",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Why is delaying gratification important for achieving long term goals?",
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
                "choice" to "In order to achieve your goals, you need to sacrifice now",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "It cannot help you",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "You are able to spend all your money now",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addSavingPreActivityHard(assessmentID: String){
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Which of the following is a correct concept of saving",
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
                "choice" to "Saving money is a waste of time and doesn't help you in any way",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving money means you have to give up all your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving money means you can't buy anything you want",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving money means setting aside a portion of your money for the future",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Luke knows that saving money is important. That is because…",
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
                "choice" to "It can help him be prepared for unexpected expenses",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "He wants to impress his friends",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "He wants to be rich and famous",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "parents",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Mary wants to donate to charity but does not have extra money. What can she do?",
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
                "choice" to "Practice saving so she has money to donate",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask her mom to donate on her behalf",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Forget about donating to charity",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask her friends for extra money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Since Lance needs to purchase sports equipment within 6 months to 1 year, What are some saving strategies that can do to save money for his sports equipment?",
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
                "choice" to "Borrow sports equipment from friends or local stores to save money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Not save and just borrow money from his parents",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask someone to buy the sports equipment for him",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Create a savings goal by determining the cost of the item he wants and setting a realistic timeline to save for it",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What's the best way to save money for a long-term goal, such as saving for a trip? ",
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
                "choice" to "Spend all your money now",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Save the spare change and hope it adds up",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Set money aside from each income or allowance to a goal or savings account",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your parents to buy it",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You want to go on a trip with your friends during the summer. These are things you can you do to save money for this trip EXCEPT?",
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
                "choice" to "Create a savings account and put money aside",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Set a financial goals and set money aside",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Evaluate your sources of income and set aside money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Spend all your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }

        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You are saving money for your birthday lunch. How can you save enough money for it?",
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
                "choice" to "Limit your expenses and deposit a part of your income",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your parents to spend instead",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Forget about your birthday",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your friends to donate money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Since Will has been earning money from selling lemonade what should she do in order to further earn more money?",
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
                "choice" to "He could sell other homemade crafts in order for him to earn more money",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "He could beg money from his parents to have more money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "He can borrow money from his friends to have more money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Which of the following is a good entrepreneurship job that you can do when you want to earn money?",
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
                "choice" to "Playing Video games all day",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Watch TV all day",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Hanging out with friends all day",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Creating and selling your own handmade crafts",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "All of the following are good ways to earn money from entrepreneurship except",
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
                "choice" to "Selling lemonade",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Watch TV all day",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Providing pet-sitting services",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Selling homemade crafts",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Which of the following situations is a good example of delaying gratification?",
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
                "choice" to "Eating all the candy at once instead of saving some for later",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Spending all your allowance money on toys as soon as you get it",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving money from your allowance instead of spending it immediately",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buying a lot of expensive clothes just because they are on sale",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
    }
    private fun addSavingPostActivityHard(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "The following are the importance of saving except",
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
                "choice" to "Saving money can help you reach your goals",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving money means you can't have any fun",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving money can teach you good financial habits",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Saving money can be used to help others in need",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "How does having savings help you for your future?",
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
                "choice" to "Means you can spend all your money on things you want right now",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Coesn't help you at all because you'll always earn more money in the future",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Help you achieve your goals and provide a sense of security for the future",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Means you can be lazy and not work hard for your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "In case of financial emergencies, I should",
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
                "choice" to "Ignore the problem and hope it goes away",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Use some of my saved money to address the problem",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Borrow money from friends",
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
            "question" to "These are reasons why you can save money for EXCEPT",
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
                "choice" to "Donating to charity",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Emergency expenses",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Financial goals",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "To make friends jealous",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "These are examples of emergency expenses you should save for EXCEPT",
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
                "choice" to "Loss of an item",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Sudden need to ride transportation",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Lack of money for needs like food and water",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Brand new toy was released",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "As Jeffrey becomes older, what do you think are the ways he could improve his saving strategy for the things he wants to purchase in the future?",
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
                "choice" to "He should set specific savings goals for the things he wants to purchase in the future",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "He should rely on his parents or other adults to save money for him",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "He should do nothing and hope for the best",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "He should ask his friends to save money for him",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "If Paul wants to make his money grow, what saving strategies should he do?",
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
                "choice" to "He should steal money from his friends in order to save more money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "He should beg money from his parents",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Paul should consider opening a savings account and depositing a portion of his allowance or any money he receives as gifts",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Do nothing and hope for the best",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "Which of the following do you think can be classified as a good saving strategy for a long-term goal?",
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
                "choice" to "Spending all of your money now and hoping to earn more in the future to achieve your long-term goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Opening a savings account and depositing a portion of your income",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Borrowing money from friends or family to achieve your long-term goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Spending all your money on short-term wants and needs",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You earned P2000 from selling homebaked goods. These are things you should do with the money EXCEPT",
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
                "choice" to "Reinvest money your money and sell more homebaked goods",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Deposit the money to a financial goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Deposit the money to your savings account",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Spend the money on toys and food",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You are having trouble reaching your financial goals. What should you NOT do?",
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
                "choice" to "Sell homemade goods",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Do chores at home",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Continue spending your money",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Sell handmade goods",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You notice that you cannot reach your long-term financial goals. What can you do to save more money?",
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
                "choice" to "Ask your parents for more allowance",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy everything you want",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Earn money by selling food, drinks, or handmade items",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Spend more",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You have earned money from selling homemade goods while saving for a long-term goal. What should you do?",
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
                "choice" to "Spend the money on things you want immediately",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy something as a reward",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Set aside the money for your long term goal",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Forget about your goal",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You have enough money to buy a new pair of sneakers, but you also want to start saving for a new keyboard. What is the best decision for your long-term financial goals?",
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
                "choice" to "Delay gratification and save your money instead",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy both things",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your mom to buy you the sneakers",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Tell your friend to buy you the keyboard",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You have enough money to buy a good snack, but you also want to start saving for a gift for your mom. What is the best decision for your medium-term financial goals?",
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
                "choice" to "Delay gratification and save your money instead",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy both things",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Ask your mom to buy you the sneakers",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Tell your friend to buy you the keyboard",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
        question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "You want to buy a new toy, but you know you should save your money for school supplies. What can you do to resist the temptation to make the purchase?",
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
                "choice" to "Keep in mind the importance of your school supplies",
                "isCorrect" to true
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Prioritize your toy",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Buy both things even if you don’t have enough money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "Forget about the school supplies",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)
        }
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
