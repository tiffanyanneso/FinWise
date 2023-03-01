package ph.edu.dlsu.finwise.financialAssessmentModule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentBinding

class FinancialAssessmentActivity : AppCompatActivity() {

    private lateinit var binding:ActivityFinancialAssessmentBinding

    private var assessmentID = "yRwmBYlXxSkETeaSYN4f"

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

            binding.btnTakeAssessment.setOnClickListener {
                //TODO: UPDATE CURRENT USER
                var assessmentAttempt = hashMapOf(
                     "childID" to "currentUser",
                    "assessmentID" to assessmentID,
                    "dateTaken" to Timestamp.now()
                )
                firestore.collection("AssessmentAttempts").add(assessmentAttempt).addOnSuccessListener {
                    var assessmentAttemptID = it.id
                    firestore.collection("AssessmentQuestions")
                        .whereEqualTo("assessmentID", assessmentID).get()
                        .addOnSuccessListener { results ->
                            for (question in results)
                                questionIDArrayList.add(question.id)

                            var assessmentQuiz = Intent(this, FinancialAssessmentQuiz::class.java)
                            var bundle = Bundle()
                            //bundle.putInt("score", 0)
                            bundle.putString("assessmentAttemptID", assessmentAttemptID)
                            bundle.putString("assessmentID", assessmentID)
                            bundle.putStringArrayList("questionIDArrayList", questionIDArrayList)
                            bundle.putSerializable("answerHistory", answerHistoryArrayList)
                            assessmentQuiz.putExtras(bundle)
                            this.startActivity(assessmentQuiz)
                        }
                }

            }
        }

    }

