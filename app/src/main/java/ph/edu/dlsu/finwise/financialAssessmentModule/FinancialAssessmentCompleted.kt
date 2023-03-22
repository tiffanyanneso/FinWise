package ph.edu.dlsu.finwise.financialAssessmentModule

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentCompletedBinding
import ph.edu.dlsu.finwise.databinding.DialogBadgeBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.model.FinancialAssessmentDetails
import ph.edu.dlsu.finwise.model.FinancialAssessmentQuestions

class FinancialAssessmentCompleted : AppCompatActivity() {

    private lateinit var binding:ActivityFinancialAssessmentCompletedBinding

    private var firestore = Firebase.firestore
    private var childID = FirebaseAuth.getInstance().currentUser!!.uid

    private lateinit var assessmentID:String
    private lateinit var assessmentAttemptID:String
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

        updateAnswerCorrectness()

    }

    private fun getBundles() {
        val bundle = intent.extras!!
        assessmentID = bundle.getString("assessmentID").toString()
        assessmentAttemptID = bundle.getString("assessmentAttemptID").toString()
        assessmentName = bundle.getString("assessmentName").toString()
        answerHistoryArrayList = bundle.getSerializable("answerHistory") as ArrayList<FinancialAssessmentQuiz.AnswerHistory>
        nQuestions = answerHistoryArrayList.size
    }

    private fun updateAnswerCorrectness() {
        //TODO: gawing async tapos hiwalay yung mga code sa continue with
        CoroutineScope(Dispatchers.Main).launch {
            for (answerHistory in answerHistoryArrayList) {
                val document = firestore.collection("AssessmentQuestions")
                    .document(answerHistory.questionID).get().await()
                val assessmentQuestion = document.toObject<FinancialAssessmentQuestions>()
                val updatedNAssessments = assessmentQuestion?.nAssessments!! + 1
                var updatedNAnsweredCorrectly = assessmentQuestion.nAnsweredCorrectly
                if (answerHistory.answeredCorrectly) {
                    answeredCorrectly++
                    updatedNAnsweredCorrectly = updatedNAnsweredCorrectly!! + 1
                }

                val assessments = firestore.collection("AssessmentQuestions")
                    .document(answerHistory.questionID)
                    .update("nAssessments", updatedNAssessments,
                    "nAnsweredCorrectly", updatedNAnsweredCorrectly).await()
            }
            setScores()
            badges()
            updateDB()
        }

    }

    private fun badges() {
        // Will wait until the firestore function is complete before moving on to the next line of
        // code
        //TODO: check if pumapasok dito yung functions kasi parang inaasume na emptty yung query
        // snapshot so check mga weherqequalto fields ddin at yung results
        CoroutineScope(Dispatchers.Main).launch {
            if (percentage == 100.0) {
                val querySnapshot = getBadge()

                if (querySnapshot?.isEmpty!!) {
                    setBadgeName()
                    addBadge()
                    showBadgeDialog()
                    // There is at least one document in the snapshot
                    //checkIfUpdateBadge(querySnapshot)
                }
            }
        }
    }

    private suspend fun addBadge() {
        val badge = hashMapOf(
            "badgeName" to badgeName,
            "badgeDescription" to "$assessmentName Assessment",
            "badgeScore" to "$percentage",
            "childID"   to childID,
            "dateEarned" to Timestamp.now()
        )
        firestore.collection("Badges").add(badge).await()
    }

    private fun setBadgeName() {
        /*if (percentage >= 50 && percentage < 75)
            badgeName = "$assessmentName Bronze Brainiac"
        else if (percentage >= 75 && percentage < 100)
            badgeName = "$assessmentName Silver Scholar"
        else if (percentage == 100.0)*/
        badgeName = "$assessmentName Genius"
    }

    /*   private suspend fun checkIfUpdateBadge(querySnapshot: QuerySnapshot?) {
          //TODO: Change badges to userbadges
          val badge = querySnapshot?.documents?.get(0)?.toObject<Badges>()
          if (percentage > badge?.badgeScore!!) {
              if (isUpdateBadge()) {
                  updateBadge(querySnapshot)
                  showBadgeDialog()
              }
          }
      }

      private suspend fun updateBadge(querySnapshot: QuerySnapshot) {
          //TODO: Change badges to userbadges
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
          //TODO: Hindi na kailangan ng mga silver scholar
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

    private suspend fun getBadge(): QuerySnapshot? {
        val usersRef = firestore.collection("Badges")
        val query = usersRef.whereEqualTo("childID", childID)
            .whereEqualTo("badgeName", "$assessmentName Genius")
        return query.get().await()
    }

    private fun showBadgeDialog() {
        val dialogBinding= DialogBadgeBinding.inflate(layoutInflater)
        val dialog= Dialog(this);
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
        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.excellent)

        val badgeTitle = "$badgeName Badge Unlocked!"

        imageView.setImageBitmap(bitmap)
        dialogBinding.tvBadgeTitle.text = badgeTitle

        val badgeMessage = " You are a Great! That's fantastic! You have shown an excellent " +
                "understanding of $assessmentName, and your hard work has paid off. Keep it up!"
        dialogBinding.tvMessage.text = badgeMessage

//TODO: change bitmap
        //TODO: Hindi na kailangan ng mga silver scholar
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


    private fun updateDB() {
        // compute for percentage
        firestore.collection("AssessmentAttempts").document(assessmentAttemptID).update("nAnsweredCorrectly", answeredCorrectly)
        firestore.collection("AssessmentAttempts").document(assessmentAttemptID).update("nQuestions", nQuestions)
        firestore.collection("Assessments").document(assessmentID).get().addOnSuccessListener {
            val assessment = it.toObject<FinancialAssessmentDetails>()
            val updatedNTimesAssessmentTaken = assessment?.nTakes!! + 1
            firestore.collection("Assessments").document(assessmentID).update("nTakes", updatedNTimesAssessmentTaken)
            goToFinancialActivity()
        }

        //binding.progressBar.max = nQuestions
    }

    private fun goToFinancialActivity() {
        binding.btnFinish.setOnClickListener {
            val assessmentTop  = Intent (this, FinancialActivity::class.java)
            this.startActivity(assessmentTop)
        }
    }

    private fun setScores() {
        binding.tvScore.text = "Your score is $answeredCorrectly out of $nQuestions"
        percentage = (answeredCorrectly.toDouble() / nQuestions.toDouble()) * 100
        binding.progressBar.progress = percentage.round(1).toInt()
        binding.textViewProgress.text = "$percentage%"
    }

    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
}