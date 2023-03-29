package ph.edu.dlsu.finwise.financialAssessmentModule

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentCompletedBinding
import ph.edu.dlsu.finwise.databinding.DialogBadgeBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.model.FinancialAssessmentQuestions
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class FinancialAssessmentCompleted : AppCompatActivity() {

    private lateinit var binding:ActivityFinancialAssessmentCompletedBinding

    private var firestore = Firebase.firestore
    private var childID = FirebaseAuth.getInstance().currentUser!!.uid

    private var assessmentIDArrayList = ArrayList<String>()
    //private var assessmentAttemptIDArrayList = ArrayList<String>()
    private lateinit var assessmentName:String
    private lateinit var badgeName: String


    //private var score =0

    private var answerHistoryArrayList = ArrayList<FinancialAssessmentQuiz.AnswerHistory>()
    private var answeredCorrectly = 0
    private var nQuestions = 0
    private var percentage = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentCompletedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBundles()
        binding.tvTitle.text = "You finished the $assessmentName quiz!"

        updateCollections()

    }

    private fun getBundles() {
        val bundle = intent.extras
        assessmentIDArrayList = bundle?.getStringArrayList("assessmentIDArrayList")
                as ArrayList<String>
        /*assessmentAttemptIDArrayList = bundle.getStringArrayList("assessmentAttemptIDArrayList")
                as ArrayList<String>*/
        assessmentName = bundle.getString("assessmentName").toString()
        answerHistoryArrayList = bundle.getSerializable("answerHistory")
                as ArrayList<FinancialAssessmentQuiz.AnswerHistory>
        nQuestions = answerHistoryArrayList.size
    }

    private fun updateCollections() {
        CoroutineScope(Dispatchers.Main).launch {
            for (answerHistory in answerHistoryArrayList) {
                updateAnsweredCorrectly(answerHistory)
            }
            setScores()
            badges()
            updateAssessmentAttempts()
            initializeFinishButtonActivity()
        }
    }

    private suspend fun updateAnsweredCorrectly(answerHistory: FinancialAssessmentQuiz.AnswerHistory) {
        val document = getAssessmentQuestions(answerHistory)
        val assessmentQuestion = document?.toObject<FinancialAssessmentQuestions>()
        var updatedNAnsweredCorrectly = assessmentQuestion?.nAnsweredCorrectly
        //ito yung kung kahit sinong nakasagot ng question na to kasi kinukuha sa assesmentquestion
        // gawa bagong var
        var isAnsweredCorrectly = false
        if (answerHistory.answeredCorrectly) {
            answeredCorrectly++
            updatedNAnsweredCorrectly = updatedNAnsweredCorrectly!! + 1
            isAnsweredCorrectly = true
        }

        if (updatedNAnsweredCorrectly != null) {
            updateAssessmentQuestionsDB(answerHistory, updatedNAnsweredCorrectly, isAnsweredCorrectly)
        }
    }

    private suspend fun updateAssessmentQuestionsDB(
        answerHistory: FinancialAssessmentQuiz.AnswerHistory,
        updatedNAnsweredCorrectly: Int?,
        isAnsweredCorrectly: Boolean
    ) {
        val data = hashMapOf(
            "nAssessments" to FieldValue.increment(1),
            "nAnsweredCorrectly" to updatedNAnsweredCorrectly
        )
        val assessmentQuestion = firestore.collection("AssessmentQuestions")
            .document(answerHistory.questionID)
        assessmentQuestion.update(data as Map<String, Any>).await()

        updateAssessmentAttemptsFields(assessmentQuestion, isAnsweredCorrectly)
    }

    private suspend fun updateAssessmentAttemptsFields(
        assessmentQuestion: DocumentReference,
        isAnsweredCorrectly: Boolean
    ) {
        val assessmentQuestionObject = assessmentQuestion.get().await().toObject<FinancialAssessmentQuestions>()
        val assessmentID = assessmentQuestionObject?.assessmentID

        if (assessmentID != null) {
            val data = if (isAnsweredCorrectly) {
                hashMapOf(
                    "nAnsweredCorrectly" to FieldValue.increment(1),
                    "nQuestions" to FieldValue.increment(1)
                )
            } else {
                hashMapOf(
                    "nQuestions" to FieldValue.increment(1)
                )
            }

            val assessmentAttemptDocRef = firestore.collection("AssessmentAttempts")
                .whereEqualTo("assessmentID", assessmentID).whereEqualTo("childID",childID)
            val assessmentAttemptDocument = assessmentAttemptDocRef.get().await()
            if (!assessmentAttemptDocument.isEmpty) {
                assessmentAttemptDocument.documents[0].reference.update(data as Map<String, Any>).await()
            } else {
                createAssessmentAttempt(assessmentID, isAnsweredCorrectly)
            }
        } else Toast.makeText(this, "null assessmentID", Toast.LENGTH_SHORT).show()
    }

    private suspend fun createAssessmentAttempt(
        assessmentID: String,
        isAnsweredCorrectly: Boolean
    ) {
        var nAnsweredCorrectly = 0
        if (isAnsweredCorrectly)
            nAnsweredCorrectly = 1

        val assessmentAttempt = hashMapOf(
            "childID" to childID,
            "assessmentID" to assessmentID,
            "nAnsweredCorrectly" to nAnsweredCorrectly,
            "nQuestions" to 1,
            "dateTaken" to Timestamp.now()
        )
        firestore.collection("AssessmentAttempts").add(assessmentAttempt).await()
    }


    private suspend fun getAssessmentQuestions(answerHistory: FinancialAssessmentQuiz.AnswerHistory)
    : DocumentSnapshot? {
        val db =  firestore.collection("AssessmentQuestions")
            .document(answerHistory.questionID)

        return db.get().await()
    }

    private suspend fun badges() {
        // Will wait until the firestore function is complete before moving on to the next line of
        // code
            if (percentage == 100.0) {
                val querySnapshot = getBadge()

                if (querySnapshot?.isEmpty!!) {
                    addBadge()
                    showBadgeDialog()
                    // There is at least one document in the snapshot
                    //checkIfUpdateBadge(querySnapshot)
                }
            }
    }

    private suspend fun addBadge() {
        badgeName = "$assessmentName Genius"
        val badge = hashMapOf(
            "badgeName" to badgeName,
            "badgeType" to "$assessmentName Assessment",
            "badgeDescription" to "Scored 100% of the $assessmentName Assessment",
            "badgeScore" to percentage,
            "childID"   to childID,
            "dateEarned" to SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate())
        )
        firestore.collection("Badges").add(badge).await()
    }


    private suspend fun getBadge(): QuerySnapshot? {
        val usersRef = firestore.collection("Badges")
        val query = usersRef.whereEqualTo("childID", childID)
            .whereEqualTo("badgeName", "$assessmentName Genius")
        return query.get().await()
    }

    private fun showBadgeDialog() {
        val dialogBinding= DialogBadgeBinding.inflate(layoutInflater)
        val dialog= Dialog(this)
        dialog.setContentView(dialogBinding.root)
        // Initialize dialog

        dialog.window!!.setLayout(1050, 1300)

        setViewBindings(dialogBinding)
        setBtn(dialogBinding, dialog)

        dialog.show()
    }

    private fun setBtn(dialogBinding: DialogBadgeBinding, dialog: Dialog) {
        dialogBinding.btnOk.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun setViewBindings(dialogBinding: DialogBadgeBinding) {
        val imageView = dialogBinding.ivBadge
        var badgeImage = R.drawable.excellent

        when (assessmentName) {
            "Budgeting" -> badgeImage = R.drawable.badge_brainy_budgeting
            "Spending" -> badgeImage = R.drawable.badge_brainy_spending
            "Saving" -> badgeImage = R.drawable.badge_brainy_saving
            "Goal Setting" -> badgeImage = R.drawable.badge_brainy_goal_setting
        }
        val bitmap = BitmapFactory.decodeResource(resources, badgeImage)
        imageView.setImageBitmap(bitmap)

        val badgeTitle = "$badgeName Badge Unlocked!"
        dialogBinding.tvBadgeTitle.text = badgeTitle
        val badgeMessage = " You are a Great! That's fantastic! You have shown an excellent " +
                "understanding of $assessmentName, and your hard work has paid off. Keep it up!"
        dialogBinding.tvMessage.text = badgeMessage

        /*if (percentage >= 50 && percentage < 75) {
            color = resources.getColor(R.color.bronze)
            badgeMessage = "Congratulations on earning the Bronze Brainiac badge! " +
                    "You're on your way to becoming a financial expert! Keep up the great work!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.excellent)
        }
        else if (percentage >= 75 && percentage < 100) {
            color = resources.getColor(R.color.silver)
            badgeMessage = "You've earned the Silver Scholar badge! That's amazing! You're well on " +
                    "your way to mastering all things finance. Keep up the good work!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.excellent)

        }
        else if (percentage == 100.0) {
            color = resources.getColor(R.color.gold)
            badgeMessage = " You are a Great! That's fantastic! You have shown an excellent " +
                    "understanding of $assessmentName, and your hard work has paid off. Keep it up!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.excellent)
        }*/
    }


    private suspend fun updateAssessmentAttempts() {
        // compute for percentage
        /*for (attempt in assessmentAttemptIDArrayList) {
            firestore.collection("AssessmentAttempts").document(attempt)
                .update("nAnsweredCorrectly", answeredCorrectly).await()
            firestore.collection("AssessmentAttempts").document(attempt)
                .update("nQuestions", nQuestions).await()
        }*/

        for (assessmentID in assessmentIDArrayList) {
            firestore.collection("Assessments").document(assessmentID)
                .update("nTakes", FieldValue.increment(1)).await()
        }
        //binding.progressBar.max = nQuestions
    }


    private fun initializeFinishButtonActivity() {
        val btnFinish = binding.btnFinish
        btnFinish.visibility = View.VISIBLE
        btnFinish.setOnClickListener {
            val assessmentTop  = Intent (this, FinancialActivity::class.java)
            startActivity(assessmentTop)
        }
    }

    private fun setScores() {
        binding.tvScore.text = "Your score is $answeredCorrectly out of $nQuestions"
        percentage = (answeredCorrectly.toDouble() / nQuestions.toDouble()) * 100
        binding.progressBar.progress = percentage.round(1).toInt()
        binding.textViewProgress.text = "${DecimalFormat("##0.00").format(percentage)}%"
    }

    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }

    /*   private suspend fun checkIfUpdateBadge(querySnapshot: QuerySnapshot?) {
        val badge = querySnapshot?.documents?.get(0)?.toObject<Badges>()
        if (percentage > badge?.badgeScore!!) {
            if (isUpdateBadge()) {
                updateBadge(querySnapshot)
                showBadgeDialog()
            }
        }
    }

    private suspend fun updateBadge(querySnapshot: QuerySnapshot) {
        val updatedBadge = hashMapOf(
            "badgeName" to badgeName,
            "badgeScore" to "$percentage",
            "dateEarned" to Timestamp.now()
        )
        val badgeID = querySnapshot.documents[0].id
        firestore.collection("Badges").document(badgeID).update(updatedBadge as Map<String, Any>)
            .await()
    }

   private fun isUpdateBadge(): Boolean {
        var updateBadge = false
        if (percentage >= 75 && percentage < 100) {
            badgeName = "$assessmentName Silver Scholar"
            updateBadge = true
        }
        else if (percentage == 100.0) {
            badgeName = "$assessmentName Gold Genius"
            updateBadge = true
        }
        return updateBadge
    }*/
}