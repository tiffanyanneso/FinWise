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
    private var score =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentCompletedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        assessmentID = bundle.getString("assessmentID").toString()
        assessmentAttemptID = bundle.getString("assessmentAttemptID").toString()
        score = bundle.getInt("score")

        firestore.collection("AssessmentQuestions").whereEqualTo("assessmentID", assessmentID).get().addOnSuccessListener { results ->
            var nQuestions = 0
            for (question in results) {
                var questionObject = question.toObject<AssessmentQuestions>()
                if (questionObject.isUsed == true)
                    nQuestions++
            }
            binding.tvScore.text = "Your score is $score out of $nQuestions"
            binding.progressBar.max = nQuestions
            binding.progressBar.progress = score
        }.continueWith {
            println("print score " + score)
            println("print assessment attempt " + assessmentAttemptID)
            firestore.collection("AssessmentAttempts").document(assessmentAttemptID).update("score", score)
        }


        binding.btnFinish.setOnClickListener {
            var assessmentTop  = Intent (this, FinancialAssessmentActivity::class.java)
            this.startActivity(assessmentTop)
        }
    }
}