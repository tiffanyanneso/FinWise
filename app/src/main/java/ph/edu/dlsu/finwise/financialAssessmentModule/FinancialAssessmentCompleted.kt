package ph.edu.dlsu.finwise.financialAssessmentModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentCompletedBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.model.FinancialAssessmentDetails
import ph.edu.dlsu.finwise.model.FinancialAssessmentQuestions

class FinancialAssessmentCompleted : AppCompatActivity() {

    private lateinit var binding:ActivityFinancialAssessmentCompletedBinding

    private var firestore = Firebase.firestore

    private lateinit var assessmentID:String
    private lateinit var assessmentAttemptID:String
    private lateinit var assessmentName:String
    //private var score =0

    private var answerHistoryArrayList = ArrayList<FinancialAssessmentQuiz.AnswerHistory>()
    private var answeredCorrectly = 0
    private var nQuestions = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentCompletedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras!!
        assessmentID = bundle.getString("assessmentID").toString()
        assessmentAttemptID = bundle.getString("assessmentAttemptID").toString()
        assessmentName = bundle.getString("assessmentName").toString()
        answerHistoryArrayList = bundle.getSerializable("answerHistory") as ArrayList<FinancialAssessmentQuiz.AnswerHistory>
        nQuestions = answerHistoryArrayList.size

        binding.tvTitle.text = "You finished the $assessmentName quiz!"

        updateAnswerCorrectness()

    }

    private fun updateAnswerCorrectness() {
        for (answerHistory in answerHistoryArrayList) {
            firestore.collection("AssessmentQuestions").document(answerHistory.questionID).get().addOnSuccessListener {
                val assessmentQuestion = it.toObject<FinancialAssessmentQuestions>()
                val updatedNAssessments = assessmentQuestion?.nAssessments!! + 1
                var updatedNAnsweredCorrectly = assessmentQuestion.nAnsweredCorrectly
                if (answerHistory.answeredCorrectly) {
                    answeredCorrectly++
                    updatedNAnsweredCorrectly = updatedNAnsweredCorrectly!! + 1
                }

                firestore.collection("AssessmentQuestions").document(answerHistory.questionID).
                    update("nAssessments", updatedNAssessments, "nAnsweredCorrectly", updatedNAnsweredCorrectly)
            }.continueWith {
                setScores()
                updateDB()
            }
        }
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
        val percentage = (answeredCorrectly.toDouble() / nQuestions.toDouble()) * 100
        binding.progressBar.progress = percentage.round(1).toInt()
        binding.textViewProgress.text = "$percentage%"
    }

    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
}