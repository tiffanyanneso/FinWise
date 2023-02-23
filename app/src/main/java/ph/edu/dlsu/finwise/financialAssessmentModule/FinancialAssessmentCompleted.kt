package ph.edu.dlsu.finwise.financialAssessmentModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentBinding
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentCompletedBinding
import ph.edu.dlsu.finwise.model.AssessmentQuestions

class FinancialAssessmentCompleted : AppCompatActivity() {

    private lateinit var binding:ActivityFinancialAssessmentCompletedBinding

    private var firestore = Firebase.firestore

    private lateinit var assessmentID:String
    private lateinit var assessmentAttemptID:String
    //private var score =0

    private var answerHistoryArrayList = ArrayList<FinancialAssessmentQuiz.AnswerHistory>()
    private var answeredCorrectly = 0
    private var nQuestions = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentCompletedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        assessmentID = bundle.getString("assessmentID").toString()
        assessmentAttemptID = bundle.getString("assessmentAttemptID").toString()
        answerHistoryArrayList = bundle.getSerializable("answerHistory") as ArrayList<FinancialAssessmentQuiz.AnswerHistory>
        nQuestions = answerHistoryArrayList.size

        updateAnswerCorrectness()

        firestore.collection("AssessmentQuestions").whereEqualTo("assessmentID", assessmentID).get().addOnSuccessListener { results ->

            binding.tvScore.text = "Your score is $answeredCorrectly out of $nQuestions"
//            binding.progressBar.max = nQuestions

            // compute for percentage
            var percentage = (answeredCorrectly / nQuestions) * 100

            binding.progressBar.progress = percentage
        }.continueWith {
            firestore.collection("AssessmentAttempts").document(assessmentAttemptID).update("nAnsweredCorrectly", answeredCorrectly)
            firestore.collection("AssessmentAttempts").document(assessmentAttemptID).update("nQuestions", nQuestions)
        }


        binding.btnFinish.setOnClickListener {
            var assessmentTop  = Intent (this, FinancialAssessmentActivity::class.java)
            this.startActivity(assessmentTop)
        }
    }

    private fun updateAnswerCorrectness() {
        for (answerHistory in answerHistoryArrayList) {
            firestore.collection("AssessmentQuestions").document(answerHistory.questionID).get().addOnSuccessListener {
                var assessmentQuestion = it.toObject<AssessmentQuestions>()
                var updatedNAssessments = assessmentQuestion?.nAssessments!! + 1
                var updatedNAnsweredCorrectly = assessmentQuestion?.nAnsweredCorrectly
                if (answerHistory.answeredCorrectly) {
                    answeredCorrectly++
                    updatedNAnsweredCorrectly = updatedNAnsweredCorrectly!! + 1
                }

                firestore.collection("AssessmentQuestions").document(answerHistory.questionID).
                    update("nAssessments", updatedNAssessments, "nAnsweredCorrectly", updatedNAnsweredCorrectly)
            }
        }
    }
}