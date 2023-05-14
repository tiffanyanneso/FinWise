package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityWaitGoalReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogTakeAssessmentBinding
import ph.edu.dlsu.finwise.financialAssessmentModule.FinancialAssessmentActivity
import ph.edu.dlsu.finwise.model.FinancialAssessmentAttempts
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class WaitGoalReview : AppCompatActivity() {

    private lateinit var binding :ActivityWaitGoalReviewBinding

    private var firestore = Firebase.firestore

    private var assessmentTaken = true
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaitGoalReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)


        var bundle = intent.extras!!
        var financialGoalID = bundle.getString("financialGoalID").toString()
        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var goal = it.toObject<FinancialGoals>()
            binding.tvGoalName.text = goal?.goalName
            binding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(goal?.targetAmount)
            binding.tvActivity.text = goal?.financialActivity
            binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(goal?.targetDate!!.toDate())
        }

        binding.btnActivities.setOnClickListener{
            getAssessmentStatus()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAssessmentStatus() {
        firestore.collection("Assessments").whereEqualTo("assessmentType", "Post-Activity").whereEqualTo("assessmentCategory", "Goal Setting").get().addOnSuccessListener {
            if (it.size() != 0) {
                var assessmentID = it.documents[0].id
                firestore.collection("AssessmentAttempts").whereEqualTo("assessmentID", assessmentID).whereEqualTo("childID", currentUser).orderBy("dateTaken", Query.Direction.DESCENDING).get().addOnSuccessListener { results ->
                    if (results.size() != 0) {
                        var assessmentAttemptsObjects =
                            results.toObjects<FinancialAssessmentAttempts>()
                        var latestAssessmentAttempt = assessmentAttemptsObjects.get(0).dateTaken
                        val dateFormatter: DateTimeFormatter =
                            DateTimeFormatter.ofPattern("MM/dd/yyyy")
                        val lastTakenFormat =
                            SimpleDateFormat("MM/dd/yyyy").format(latestAssessmentAttempt!!.toDate())
                        val from = LocalDate.parse(lastTakenFormat.toString(), dateFormatter)
                        val today = SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate())
                        val to = LocalDate.parse(today.toString(), dateFormatter)
                        var difference = Period.between(from, to)

                        if (difference.days >= 7)
                            assessmentTaken = false
                        else
                            assessmentTaken = true
                    } else
                        assessmentTaken = false
                }.continueWith {
                    if (assessmentTaken) {
                        var finact = Intent(this, FinancialActivity::class.java)
                        startActivity(finact)
                    } else
                        buildAssessmentDialog()
                }
            }
        }
    }

    private fun buildAssessmentDialog() {
        var dialogBinding= DialogTakeAssessmentBinding.inflate(layoutInflater)
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.root)
        dialog.window!!.setLayout(950, 900)
        dialog.setCancelable(false)
        dialogBinding.btnOk.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("assessmentType", "Post-Activity")
            bundle.putString("assessmentCategory", "Goal Setting")

            val assessmentQuiz = Intent(this, FinancialAssessmentActivity::class.java)
            assessmentQuiz.putExtras(bundle)
            startActivity(assessmentQuiz)
            dialog.dismiss()
        }
        dialog.show()
    }
}