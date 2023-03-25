package ph.edu.dlsu.finwise.financialAssessmentModule

import android.content.Intent
import android.os.Build
import android.os.Bundle
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

    private lateinit var assessmentID: String
    private lateinit var assessmentType: String
    private lateinit var assessmentCategory: String
    private lateinit var assessmentName: String
    private var childID = FirebaseAuth.getInstance().currentUser!!.uid
    private var age = 0

    private var questionIDArrayList = ArrayList<String>()
    private var answerHistoryArrayList = ArrayList<FinancialAssessmentQuiz.AnswerHistory>()


    private var firestore = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_assessment)

        retrieveBundle()
        loadAssessments()
        loadAssessmentAttemptButton()
    }

    private fun loadAssessmentAttemptButton() {
        binding.btnTakeAssessment.setOnClickListener {
            val assessmentAttempt = makeAssessmentAttempt()
            firestore.collection("AssessmentAttempts").add(assessmentAttempt)
                .addOnSuccessListener {
                    val assessmentAttemptID = it.id
                    goToFinancialAssessmentQuiz(assessmentAttemptID)
                }
        }
    }

    private fun loadAssessments() {
        // Makes firestore functions async --> Firestore functions will finish first
        // before going to next line (must implement suspend in function to use .async())
        CoroutineScope(Dispatchers.Main).launch {
            val assessmentsDocumentSnapshot = getAssessmentDocument()
            loadTextViewsBinding(assessmentsDocumentSnapshot)
            getChildAge()
            getAssessmentQuestions(assessmentsDocumentSnapshot)
        }
    }

    private fun loadTextViewsBinding(assessmentsDocumentSnapshot: QuerySnapshot?) {
        if (assessmentsDocumentSnapshot != null) {
            val assessment = assessmentsDocumentSnapshot.documents[0]
                .toObject<FinancialAssessmentDetails>()
            assessmentName = assessment?.assessmentCategory.toString()
            var description = assessment?.description.toString()
            if (description == "")
                description = "This assessment is about testing your $assessmentName knowledge and skills"

            binding.tvName.text = assessmentName
            binding.tvDescription.text = description

            assessmentID = assessmentsDocumentSnapshot.documents[0].id
            getScore(assessmentID)
        }
    }

    private fun getScore(assessmentID: String) {
        firestore.collection("AssessmentAttempts")
            .whereEqualTo("assessmentID" , assessmentID)
            .whereEqualTo("childID", childID)
            .get().addOnSuccessListener { querySnapshot ->
                lateinit var progressPercent: String
                lateinit var scoreBreakdown: String
                val progressInt: Int
                if (querySnapshot.documents.isNotEmpty()) {
                    // Documents exist
                    val assessmentAttempt = querySnapshot.documents[0].toObject<FinancialAssessmentAttempts>()
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getChildAge() {
        firestore.collection("Users").document(childID).get().addOnSuccessListener {
            var child = it.toObject<Users>()
            //compute age
            val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            age = difference.years
        }
    }

    private fun makeAssessmentAttempt(): Any {
        val assessmentAttempt = hashMapOf(
            "childID" to childID,
            "assessmentID" to assessmentID,
            "dateTaken" to Timestamp.now()
        )
        return assessmentAttempt
    }

    private suspend fun getAssessmentQuestions(assessmentsIDArray: QuerySnapshot?) {
        if (assessmentsIDArray != null) {
            for (assessmentID in assessmentsIDArray) {
                val questions = getAssessmentQuestionsCollection(assessmentID.id)
                if (questions != null) {
                    for (question in questions) {
                        questionIDArrayList.add(question.id)
                    }
                }
            }
        }
    }

    private suspend fun getAssessmentQuestionsCollection(assessmentID: String): QuerySnapshot? {
        var difficultyQuery =  Arrays.asList("Easy")
        if (age == 10 || age == 11)
            difficultyQuery = Arrays.asList("Easy", "Medium")
        else if (age == 12)
            difficultyQuery = Arrays.asList("Easy", "Medium", "Hard")

        val docRef = firestore.collection("AssessmentQuestions")
            .whereEqualTo("assessmentID", assessmentID)
            .whereEqualTo("difficulty", difficultyQuery)
        return docRef.get().await()
    }

    private suspend fun getAssessmentDocument(): QuerySnapshot? {
        val docRef = firestore.collection("Assessments")
            .whereEqualTo("assessmentType", assessmentType)
            .whereEqualTo("assessmentCategory", assessmentCategory)
        return docRef.get().await()
    }

    private fun goToFinancialAssessmentQuiz(assessmentAttemptID: String) {
        val assessmentQuiz = Intent(this, FinancialAssessmentQuiz::class.java)
        val bundle = setBundles(assessmentAttemptID)
        assessmentQuiz.putExtras(bundle)
        startActivity(assessmentQuiz)
    }

    private fun setBundles(assessmentAttemptID: String): Bundle {
        val bundle = Bundle()
        bundle.putString("assessmentAttemptID", assessmentAttemptID)
        bundle.putString("assessmentID", assessmentID)
        bundle.putString("assessmentName", assessmentName)
        bundle.putStringArrayList("questionIDArrayList", questionIDArrayList)
        bundle.putInt("nNumberOfQuestions", questionIDArrayList.size)
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
