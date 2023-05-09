package ph.edu.dlsu.finwise.financialAssessmentModule

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentBinding
import ph.edu.dlsu.finwise.model.FinancialAssessmentAttempts
import ph.edu.dlsu.finwise.model.FinancialAssessmentDetails
import ph.edu.dlsu.finwise.model.FinancialAssessmentQuestions
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class FinancialAssessmentActivity : AppCompatActivity() {

    private lateinit var binding:ActivityFinancialAssessmentBinding

    private var assessmentIDArrayList = ArrayList<String>()
    //private var assessmentAttemptIDArrayList = ArrayList<String>()
    private lateinit var assessmentType: String
    private lateinit var assessmentCategory: String
    private lateinit var assessmentName: String
    private var nQuestionsInAssessment = 0
    private var childID = FirebaseAuth.getInstance().currentUser!!.uid
    private var age = 0

    private var questionIDArrayList = ArrayList<String>()
    private var answerHistoryArrayList = ArrayList<FinancialAssessmentQuiz.AnswerHistory>()


    private var firestore = Firebase.firestore
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_assessment)

        retrieveBundle()
        loadAssessments()
    }

    private suspend fun getPreliminaryAssessmentDocuments(): QuerySnapshot? {
        val docRef = firestore.collection("Assessments")
            .whereEqualTo("assessmentType", assessmentType)
        return docRef.get().await()
    }

    private suspend fun loadAssessmentAttemptButton() {
        binding.btnTakeAssessment.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                /*for (assessment in assessmentIDArrayList) {
                    val attempt = makeAssessmentAttempt(assessment)
                    val assessmentAttempt = firestore.collection("AssessmentAttempts")
                        .add(attempt).await()
                    assessmentAttemptIDArrayList.add(assessmentAttempt.id)
                }*/
                goToFinancialAssessmentQuiz()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadAssessments() {
        // Makes firestore functions async --> Firestore functions will finish first
        // before going to next line (must implement suspend in function to use .async())
        CoroutineScope(Dispatchers.Main).launch {
            getChildAge()
            val assessmentsDocumentSnapshot: QuerySnapshot? = if (assessmentType == "Preliminary")
                getPreliminaryAssessmentDocuments()
            else getAssessmentDocument()

            loadTextViewsBinding(assessmentsDocumentSnapshot, assessmentType)
            getAssessmentQuestions(assessmentsDocumentSnapshot)
            Log.d("questionsccxcxc", "loadAssessments: "+questionIDArrayList)
            loadAssessmentAttemptButton()
        }
    }

    private suspend fun loadTextViewsBinding(assessmentsDocumentSnapshot: QuerySnapshot?, assessmentType: String) {
        if (assessmentsDocumentSnapshot != null) {
            lateinit var description: String
            if (assessmentType == "Preliminary") {
                addNQuestions(assessmentsDocumentSnapshot)
                addAssessmentIDArrayList(assessmentsDocumentSnapshot)
                assessmentName = "Preliminary Quiz"


                description = "Hey there! Before we start our journey towards financial literacy, " +
                        "let's gauge your current knowledge. Please take a few moments to complete " +
                        "this preliminary quiz. Good luck!"
                binding.tvPreviousScore.visibility = View.GONE
                binding.tvName.visibility = View.GONE
                binding.tvWhat.visibility = View.GONE
                binding.textViewProgress.visibility = View.GONE
                binding.tvBreakdown.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.bottomNav.visibility = View.GONE
            } else {
                val assessment = assessmentsDocumentSnapshot.documents[0]
                    .toObject<FinancialAssessmentDetails>()
                assessmentName = assessment?.assessmentCategory.toString()
                nQuestionsInAssessment = assessment?.nQuestionsInAssessment!!
                description = assessment.description.toString()
                if (description == "")
                    description = "\"Hey there! Before we start our journey towards financial literacy," +
                            "let's gauge your current knowledge. Please take a few moments" +
                            "to complete this preliminary quiz. Good luck!"

                binding.tvName.text = assessmentName
                val assessmentID = assessmentsDocumentSnapshot.documents[0].id
                assessmentIDArrayList.add(assessmentID)
                getScore()
            }
            binding.tvDescription.text = description
        }
    }

    private fun addAssessmentIDArrayList(assessmentsDocumentSnapshot: QuerySnapshot) {
        for (assessments in assessmentsDocumentSnapshot)
            assessmentIDArrayList.add(assessments.id)
    }

    private fun addNQuestions(assessmentsDocumentSnapshot: QuerySnapshot) {
        for (assessment in assessmentsDocumentSnapshot){
            val assessmentObject = assessment.toObject<FinancialAssessmentDetails>()
            nQuestionsInAssessment += assessmentObject.nQuestionsInAssessment!!
        }
    }

    private suspend fun getScore() {
        val assessmentAttempt = firestore.collection("AssessmentAttempts")
            .whereEqualTo("assessmentID" , assessmentIDArrayList[0])
            .whereEqualTo("childID", childID)
            .get().await()
                lateinit var progressPercent: String
                lateinit var scoreBreakdown: String
                val progressInt: Int
                if (assessmentAttempt.documents.isNotEmpty()) {
                    // Documents exist
                    val assessmentAttempt = assessmentAttempt.documents[0].toObject<FinancialAssessmentAttempts>()
                    val nAnsweredCorrectly = assessmentAttempt?.nAnsweredCorrectly
                    val nQuestions = assessmentAttempt?.nQuestions

                    val percentage = (nAnsweredCorrectly?.toDouble()?.div(nQuestions?.toDouble()!!))
                        ?.times(100.0)
                    println("print " +  percentage)
                    val formattedValue = DecimalFormat("##0.#").format(percentage)
                    progressPercent = "$formattedValue%"
                    scoreBreakdown = "You got $nAnsweredCorrectly / $nQuestions"
                    progressInt = if (percentage != null) {
                        percentage.toInt()
                    } else 0
                } else {
                    // No documents exist
                    progressPercent = "0%"
                    scoreBreakdown = "You haven't taken this quiz yet"
                    progressInt = 0
                }
                binding.textViewProgress.text = progressPercent
                binding.tvBreakdown.text = scoreBreakdown
                binding.progressBar.progress = progressInt

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getChildAge() {
        val user = firestore.collection("Users").document(childID).get().await()
            val child = user.toObject<Users>()
            //compute age
            val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date = child?.birthday?.toDate()?.let { SimpleDateFormat("MM/dd/yyyy").format(it) }
            val to = LocalDate.parse(date.toString(), dateFormatter)
            val difference = Period.between(to, from)

            age = difference.years
    }

    private fun makeAssessmentAttempt(assessment: String): Any {
        val assessmentAttempt = hashMapOf(
            "childID" to childID,
            "assessmentID" to assessment,
            "dateTaken" to Timestamp.now()
        )
        return assessmentAttempt
    }

    private suspend fun getAssessmentQuestions(assessmentsIDArray: QuerySnapshot?) {
        if (assessmentsIDArray != null) {
            for (assessmentID in assessmentsIDArray) {
                val questions = getAssessmentQuestionsCollection(assessmentID.id)
                Log.d("tagheur", "questions: "+questions?.count())
                Log.d("tagheur", "assessmentID: "+assessmentID)
                if (questions != null) {
                    for (question in questions) {
                        questionIDArrayList.add(question.id)
                    }
                }
            }
            Log.d("tagheur", "questionIDArrayList: "+questionIDArrayList)
            Log.d("tagheur", "questionIDArrayList.count: "+questionIDArrayList.count())
            Log.d("tagheur", "age: "+age)
        }
    }

    private suspend fun getAssessmentQuestionsCollection(assessmentID: String): QuerySnapshot? {
        var difficultyQuery =  listOf("Easy")
        if (age == 10 || age == 11)
            difficultyQuery = listOf("Easy", "Medium")
        else if (age == 12)
            difficultyQuery = listOf("Easy", "Medium", "Hard")

        val docRef = firestore.collection("AssessmentQuestions")
            .whereEqualTo("assessmentID", assessmentID)
            .whereIn("difficulty", difficultyQuery)
            .limit(5)

        return docRef.get().await()
    }

    private suspend fun getAssessmentDocument(): QuerySnapshot? {
        val docRef = firestore.collection("Assessments")
            .whereEqualTo("assessmentType", assessmentType)
            .whereEqualTo("assessmentCategory", assessmentCategory)
        return docRef.get().await()
    }

    private fun goToFinancialAssessmentQuiz() {
        val assessmentQuiz = Intent(this, FinancialAssessmentQuiz::class.java)
        val bundle = setBundles()
        assessmentQuiz.putExtras(bundle)
        startActivity(assessmentQuiz)
    }

    private fun setBundles(): Bundle {
        val bundle = Bundle()
        //bundle.putStringArrayList("assessmentAttemptIDArrayList", assessmentAttemptIDArrayList)
        bundle.putStringArrayList("assessmentIDArrayList", assessmentIDArrayList)
        bundle.putString("assessmentName", assessmentName)
        bundle.putStringArrayList("questionIDArrayList", questionIDArrayList)
        bundle.putInt("nNumberOfQuestions", nQuestionsInAssessment)
        bundle.putInt("currentNumber", 1)
        bundle.putSerializable("answerHistory", answerHistoryArrayList)
        return bundle
    }

    private fun retrieveBundle() {
        val bundle = intent.extras
        //TODO: fix comments
        /*assessmentType = "Pre-Activity"
        assessmentCategory = "Budgeting"*/
        assessmentType = bundle?.getString("assessmentType").toString()
        assessmentCategory = bundle?.getString("assessmentCategory").toString()
    }


    // Function call that triggers the redirection to the financialAssessmentActivity
    /*goToFinancialAssessmentActivity()

    //Functions that builds the trigger and passes the values
    //Check and change the "assessmentType" and the "assessmentCategory" depending on what activity
    //the child is in
    private fun goToFinancialAssessmentActivity() {
        val assessmentQuiz = Intent(this, FinancialAssessmentActivity::class.java)
        val bundle = setBundles()
        assessmentQuiz.putExtras(bundle)
        startActivity(assessmentQuiz)
    }

    private fun setBundles(): Bundle {
        val bundle = Bundle()
        val assessmentType = "Pre-Activity" // Change: Pre-Activity, Post-Activity
        val assessmentCategory = "Budgeting" // Change: Budgeting, Saving, Spending, Goal Setting
        bundle.putString("assessmentType", assessmentType)
        bundle.putString("assessmentCategory", assessmentCategory)
        return bundle
    }*/

}
