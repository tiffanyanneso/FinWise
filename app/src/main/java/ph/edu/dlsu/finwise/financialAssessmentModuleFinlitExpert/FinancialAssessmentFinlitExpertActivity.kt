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

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        NavbarFinlitExpert(
            findViewById(R.id.bottom_nav_finlit_expert),
            this,
            R.id.nav_finlit_assessment
        )
    }

    private fun addQuestions() {
        goalSettingPrelimQuestions()
        goalSettingPreActivityQuestions()
        goalSettingPostActivityQuestions()
        typeOfGoalsPreliminaryQuestions()
        typeOfGoalsPreActivityQuestions()
        typeOfGoalsPostActivityQuestions()
        smartGoalsPreliminaryQuestions()
        smartGoalsPreActivityQuestions()
        smartGoalsPostActivityQuestions()
    }

    private fun goalSettingPrelimQuestions() {
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

    private fun typeOfGoalsPreliminaryQuestions() {
        var assessment = hashMapOf(
            "assessmentCategory" to "Type of Goals",
            "assessmentType" to "Preliminary",
            "description" to "Preliminary assessment for type of goals",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            typeOfGoalsPreliminaryEasy(assessmentID)
            typeOfGoalsPreliminaryMedium(assessmentID)
            typeOfGoalsPreliminaryHard(assessmentID)
        }
    }
    private fun typeOfGoalsPreActivityQuestions() {
        var assessment = hashMapOf(
            "assessmentCategory" to "Type of Goals",
            "assessmentType" to "Pre-Activity",
            "description" to "Pre-Activity assessment for type of goals",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            typeOfGoalsPreActivityEasy(assessmentID)
            typeOfGoalsPreActivityMedium(assessmentID)
            typeOfGoalsPreActivityHard(assessmentID)
        }
    }
    private fun typeOfGoalsPostActivityQuestions() {
        var assessment = hashMapOf(
            "assessmentCategory" to "Type of Goals",
            "assessmentType" to "Post-Activity",
            "description" to "Post-Activity assessment for type of goals",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            typeOfGoalsPostActivityEasy(assessmentID)
            typeOfGoalsPostActivityMedium(assessmentID)
            typeOfGoalsPostActivityHard(assessmentID)
        }
    }

    private fun smartGoalsPreliminaryQuestions() {
        var assessment = hashMapOf(
            "assessmentCategory" to "SMART Goals",
            "assessmentType" to "Preliminary",
            "description" to "Preliminary assessment for SMART goals",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            smartGoalsPreliminaryEasy(assessmentID)
            smartGoalsPreliminaryMedium(assessmentID)
            smartGoalsPreliminaryHard(assessmentID)
        }
    }
    private fun smartGoalsPreActivityQuestions() {
        var assessment = hashMapOf(
            "assessmentCategory" to "Type of Goals",
            "assessmentType" to "Pre-Activity",
            "description" to "Pre-Activity assessment for SMART goals",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            smartGoalsPreActivityEasy(assessmentID)
            smartGoalsPreActivityMedium(assessmentID)
            smartGoalsPreActivityHard(assessmentID)
        }
    }
    private fun smartGoalsPostActivityQuestions() {
        var assessment = hashMapOf(
            "assessmentCategory" to "SMART Goals",
            "assessmentType" to "Post-Activity",
            "description" to "Post-Activity assessment for SMART goals",
            "createdOn" to Timestamp.now(),
            "createdBy" to currentUser,
            "nTakes" to 0,
            "nQuestionsInAssessment" to 3
        )
        firestore.collection("Assessments").add(assessment).addOnSuccessListener {
            var assessmentID = it.id
            smartGoalsPostActivityEasy(assessmentID)
            smartGoalsPostActivityMedium(assessmentID)
            smartGoalsPostActivityHard(assessmentID)
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

            question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "Why is it important to set financial goals?",
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
                    "choice" to "So that you have a direction to follow",
                    "isCorrect" to true
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "They are only for fun",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "It will decrease your motivation",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                    "questionID" to questionID,
                    "choice" to "It is not important, you will always have enough money",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)
            }
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

        }

        private fun addGoalSettingPreliminaryHard(assessmentID: String) {
           var question = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to "What is a financial goal?",
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
                    "choice" to "A way to spend your money",
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choice)

                choice = hashMapOf(
                     "questionID" to questionID,
                    "choice" to "A target you have to improve your financial situation",
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
        }

        private fun addGoalSettingPreActivityHard(assessmentID: String) {
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
        }

    private fun typeOfGoalsPreliminaryEasy(assessmentID: String) {
        var question = hashMapOf(
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
    }
    private fun typeOfGoalsPreActivityEasy(assessmentID: String) {
        var question = hashMapOf(
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
    }

    private fun  typeOfGoalsPostActivityEasy(assessmentID: String) {
        var question = hashMapOf(
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
    }

    private fun typeOfGoalsPreliminaryMedium(assessmentID: String) {
        var question = hashMapOf(
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
    }
    private fun typeOfGoalsPreliminaryHard(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a financial goal?",
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
                "choice" to "A way to spend your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A target you have to improve your financial situation",
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
    }
    private fun  typeOfGoalsPreActivityMedium(assessmentID: String) {
        var question = hashMapOf(
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
    }
    private fun  typeOfGoalsPreActivityHard(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a financial goal?",
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
                "choice" to "A way to spend your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A target you have to improve your financial situation",
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
    }
    private fun  typeOfGoalsPostActivityMedium(assessmentID: String) {
        var question = hashMapOf(
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
    }

    private fun  typeOfGoalsPostActivityHard(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a financial goal?",
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
                "choice" to "A way to spend your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A target you have to improve your financial situation",
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
    }

    private fun smartGoalsPreliminaryEasy(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a financial goal?",
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
                "choice" to "A way to spend your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A target you have to improve your financial situation",
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
    }

    private fun smartGoalsPreliminaryMedium(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a financial goal?",
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
                "choice" to "A way to spend your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A target you have to improve your financial situation",
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
    }

    private fun smartGoalsPreliminaryHard(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a financial goal?",
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
                "choice" to "A way to spend your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A target you have to improve your financial situation",
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
    }

    private fun smartGoalsPreActivityEasy(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a financial goal?",
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
                "choice" to "A way to spend your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A target you have to improve your financial situation",
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
    }

    private fun smartGoalsPreActivityMedium(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a financial goal?",
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
                "choice" to "A way to spend your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A target you have to improve your financial situation",
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
    }

    private fun smartGoalsPreActivityHard(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a financial goal?",
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
                "choice" to "A way to spend your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A target you have to improve your financial situation",
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
    }

    private fun smartGoalsPostActivityEasy(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a financial goal?",
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
                "choice" to "A way to spend your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A target you have to improve your financial situation",
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
    }

    private fun smartGoalsPostActivityMedium(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a financial goal?",
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
                "choice" to "A way to spend your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A target you have to improve your financial situation",
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
    }

    private fun smartGoalsPostActivityHard(assessmentID: String) {
        var question = hashMapOf(
            "assessmentID" to assessmentID,
            "question" to "What is a financial goal?",
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
                "choice" to "A way to spend your money",
                "isCorrect" to false
            )
            firestore.collection("AssessmentChoices").add(choice)

            choice = hashMapOf(
                "questionID" to questionID,
                "choice" to "A target you have to improve your financial situation",
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
    }
}
