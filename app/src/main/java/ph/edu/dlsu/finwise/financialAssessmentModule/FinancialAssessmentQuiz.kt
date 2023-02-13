package ph.edu.dlsu.finwise.financialAssessmentModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentQuizBinding
import ph.edu.dlsu.finwise.model.AssessmentChoices
import ph.edu.dlsu.finwise.model.AssessmentQuestions
import kotlin.random.Random

class FinancialAssessmentQuiz : AppCompatActivity() {
    private lateinit var binding :ActivityFinancialAssessmentQuizBinding

    private var firestore = Firebase.firestore

    private lateinit var assessmentID:String
    private lateinit var questionIDArrayList:ArrayList<String>

    private lateinit var questionID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        assessmentID = bundle.getString("assessmentID").toString()
        questionIDArrayList = bundle.getStringArrayList("questionIDArrayList")!!

        getQuestion()
        binding.layoutChoice1.setOnClickListener {
            if (binding.tvIsCorrect1.text == "true")
                Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT).show()
            nextQuestion()
        }
        binding.layoutChoice2.setOnClickListener {
            if (binding.tvIsCorrect2.text == "true")
                Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT).show()
            nextQuestion()
        }
        binding.layoutChoice3.setOnClickListener {
            if (binding.tvIsCorrect3.text == "true")
                Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT).show()
            nextQuestion()
        }
        binding.layoutChoice4.setOnClickListener {
            if (binding.tvIsCorrect4.text == "true")
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT).show()
            nextQuestion()
        }

    }

    private fun getQuestion() {
        val questionIndex = Random.nextInt(questionIDArrayList!!.size)

        firestore.collection("AssessmentQuestions").document(questionIDArrayList[questionIndex]).get().addOnSuccessListener {
            questionID = it.id
            var question = it.toObject<AssessmentQuestions>()
            binding.tvQuestion.text = question?.question
            questionIDArrayList.removeAt(questionIndex)
        }.continueWith { getChoices() }
    }

    private fun getChoices() {
        firestore.collection("AssessmentChoices").whereEqualTo("questionID", questionID).get().addOnSuccessListener { results ->
            var index = 1
            for (choice in results ) {
                var choiceObject = choice.toObject<AssessmentChoices>()

                if (index == 1) {
                    binding.tvChoice1ID.text = choice.id
                    binding.tvChoice1.text = choiceObject.choice
                    binding.tvIsCorrect1.text = choiceObject.isCorrect.toString()
                }
                else if (index == 2) {
                    binding.tvChoice2ID.text = choice.id
                    binding.tvChoice2.text = choiceObject.choice
                    binding.tvIsCorrect2.text = choiceObject.isCorrect.toString()
                }
                else if (index == 3) {
                    binding.tvChoice3ID.text = choice.id
                    binding.tvChoice3.text = choiceObject.choice
                    binding.tvIsCorrect3.text = choiceObject.isCorrect.toString()
                }
                else if (index == 4) {
                    binding.tvChoice4ID.text = choice.id
                    binding.tvChoice4.text = choiceObject.choice
                    binding.tvIsCorrect4.text = choiceObject.isCorrect.toString()
                }
                index++
            }

            //hide layouts if there are less than 4 choices
            if (index == 3) {
                binding.layoutChoice3.visibility = View.GONE
                binding.layoutChoice4.visibility = View.GONE
            }
            if (index ==4 )
                binding.layoutChoice4.visibility = View.GONE
        }
    }

    private fun nextQuestion() {
        //if there are still questions, go to the next
        if (questionIDArrayList.size > 0) {
            var nextQuestion = Intent(this, FinancialAssessmentQuiz::class.java)
            var bundle = Bundle()
            bundle.putString("assessmentID", assessmentID)
            bundle.putStringArrayList("questionIDArrayList", questionIDArrayList)
            nextQuestion.putExtras(bundle)
            this.startActivity(nextQuestion)
        }
        else
            Toast.makeText(this, "End Assessment", Toast.LENGTH_SHORT).show()
        //TODO:show results
    }
}