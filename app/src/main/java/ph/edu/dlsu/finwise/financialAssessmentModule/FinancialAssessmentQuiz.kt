package ph.edu.dlsu.finwise.financialAssessmentModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentQuizBinding
import ph.edu.dlsu.finwise.model.FinancialAssessmentChoices
import ph.edu.dlsu.finwise.model.FinancialAssessmentQuestions
import kotlin.random.Random

class FinancialAssessmentQuiz : AppCompatActivity() {
    private lateinit var binding :ActivityFinancialAssessmentQuizBinding

    private var firestore = Firebase.firestore

    private var assessmentIDArrayList = ArrayList<String>()
    //private var assessmentAttemptIDArrayList = ArrayList<String>()
    private lateinit var assessmentName:String
    private var nNumberOfQuestions: Int = 0
    private var currentNumber: Int = 0
    private var score =0
    private lateinit var questionIDArrayList:ArrayList<String>

    private lateinit var questionID:String

    private var answerHistoryArrayList = ArrayList<AnswerHistory>()

    //holds which index is the correct answer is to highlight it
    private var correctChoice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras!!
        assessmentIDArrayList = bundle.getStringArrayList("assessmentIDArrayList") as ArrayList<String>
        nNumberOfQuestions = bundle.getInt("nNumberOfQuestions")
        currentNumber = bundle.getInt("currentNumber")
        /*assessmentAttemptIDArrayList = bundle.getStringArrayList("assessmentAttemptIDArrayList")
                as ArrayList<String>*/
        assessmentName = bundle.getString("assessmentName").toString()
        score = bundle.getInt("score")
        questionIDArrayList = bundle.getStringArrayList("questionIDArrayList")!!
        answerHistoryArrayList = bundle.getParcelableArrayList("answerHistory")!!

        initializeTitleAndNumber()
        getQuestion()
        initializeLayoutClicks()

        binding.btnNext.setOnClickListener {
            nextQuestion()
        }
    }

    private fun initializeTitleAndNumber() {
        val assessmentTitle = "$assessmentName Quiz"
        binding.assessmentTitle.text = assessmentTitle

        binding.questionsLeft.text = "$currentNumber / $nNumberOfQuestions"
    }



    private fun getQuestion() {
        val questionIndex = Random.nextInt(questionIDArrayList.size)

        firestore.collection("AssessmentQuestions")
            .document(questionIDArrayList[questionIndex]).get().addOnSuccessListener {
            questionID = it.id
            val question = it.toObject<FinancialAssessmentQuestions>()
            binding.tvQuestion.text = question?.question
            questionIDArrayList.removeAt(questionIndex)
        }.continueWith { getChoices() }
    }

    private fun getChoices() {
        firestore.collection("AssessmentChoices").whereEqualTo("questionID", questionID).get().addOnSuccessListener { results ->
            var index = 1
            for (choice in results ) {
                val choiceObject = choice.toObject<FinancialAssessmentChoices>()
                if (choiceObject.isCorrect == true)
                    correctChoice = index
                when (index) {
                    1 -> {
                        binding.tvChoice1ID.text = choice.id
                        binding.tvChoice1.text = choiceObject.choice
                        binding.tvIsCorrect1.text = choiceObject.isCorrect.toString()
                        binding.layoutChoice1.setBackgroundColor(ContextCompat.getColor(this, R.color.cream))
                    }
                    2 -> {
                        binding.tvChoice2ID.text = choice.id
                        binding.tvChoice2.text = choiceObject.choice
                        binding.tvIsCorrect2.text = choiceObject.isCorrect.toString()
                        binding.layoutChoice2.setBackgroundColor(ContextCompat.getColor(this, R.color.cream))

                    }
                    3 -> {
                        binding.tvChoice3ID.text = choice.id
                        binding.tvChoice3.text = choiceObject.choice
                        binding.tvIsCorrect3.text = choiceObject.isCorrect.toString()
                        binding.layoutChoice3.setBackgroundColor(ContextCompat.getColor(this, R.color.cream))
                    }
                    4 -> {
                        binding.tvChoice4ID.text = choice.id
                        binding.tvChoice4.text = choiceObject.choice
                        binding.tvIsCorrect4.text = choiceObject.isCorrect.toString()
                        binding.layoutChoice4.setBackgroundColor(ContextCompat.getColor(this, R.color.cream))
                    }
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
        val sendBundle = Bundle()
        sendBundle.putStringArrayList("assessmentIDArrayList", assessmentIDArrayList)
        sendBundle.putString("assessmentName", assessmentName)
        sendBundle.putInt("nNumberOfQuestions", nNumberOfQuestions)
        sendBundle.putInt("currentNumber", currentNumber+1)
        //sendBundle.putStringArrayList("assessmentAttemptIDArrayList", assessmentAttemptIDArrayList)
        sendBundle.putStringArrayList("questionIDArrayList", questionIDArrayList)
        sendBundle.putInt("score", score)
        sendBundle.putParcelableArrayList("answerHistory", answerHistoryArrayList)

        //if there are still questions, go to the next
        if (currentNumber+1 <= nNumberOfQuestions) {
            val nextQuestion = Intent(this, FinancialAssessmentQuiz::class.java)
            nextQuestion.putExtras(sendBundle)
            this.startActivity(nextQuestion)
            finish()
        }
        else{
            val assessmentCompleted = Intent(this, FinancialAssessmentCompleted::class.java)
            assessmentCompleted.putExtras(sendBundle)
            this.startActivity(assessmentCompleted)
        }
    }

    private fun highlightCorrect() {
        when (correctChoice) {
            1 -> binding.layoutChoice1.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green))
            2 -> binding.layoutChoice2.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green))
            3 -> binding.layoutChoice3.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green))
            4 -> binding.layoutChoice4.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green))
        }

        binding.layoutChoice1.isClickable = false
        binding.layoutChoice2.isClickable = false
        binding.layoutChoice3.isClickable = false
        binding.layoutChoice4.isClickable = false
    }

    private fun initializeLayoutClicks() {
        binding.layoutChoice1.setOnClickListener {
            highlightCorrect()
            binding.btnNext.visibility = View.VISIBLE
            if (binding.tvIsCorrect1.text == "true") {
                score++
                answerHistoryArrayList.add(AnswerHistory(questionID, true))
            }
            else {
                binding.layoutChoice1.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                answerHistoryArrayList.add(AnswerHistory(questionID, false))
            }
        }
        binding.layoutChoice2.setOnClickListener {
            highlightCorrect()
            binding.btnNext.visibility = View.VISIBLE
            if (binding.tvIsCorrect2.text == "true") {
                score++
                answerHistoryArrayList.add(AnswerHistory(questionID, true))
            }
            else {
                binding.layoutChoice2.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                answerHistoryArrayList.add(AnswerHistory(questionID, false))
            }
        }
        binding.layoutChoice3.setOnClickListener {
            highlightCorrect()
            binding.btnNext.visibility = View.VISIBLE
            if (binding.tvIsCorrect3.text == "true") {
                score++
                answerHistoryArrayList.add(AnswerHistory(questionID, true))
            }
            else {
                binding.layoutChoice3.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                answerHistoryArrayList.add(AnswerHistory(questionID, false))
            }

        }
        binding.layoutChoice4.setOnClickListener {
            highlightCorrect()
            binding.btnNext.visibility = View.VISIBLE
            if (binding.tvIsCorrect4.text == "true") {
                score++
                answerHistoryArrayList.add(AnswerHistory(questionID, true))
            }
            else {
                binding.layoutChoice4.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                answerHistoryArrayList.add(AnswerHistory(questionID, false))
            }
        }
    }

    class AnswerHistory(var questionID:String, var answeredCorrectly:Boolean) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString().toString(),
            parcel.readByte() != 0.toByte()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(questionID)
            parcel.writeByte(if (answeredCorrectly) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<AnswerHistory> {
            override fun createFromParcel(parcel: Parcel): AnswerHistory {
                return AnswerHistory(parcel)
            }

            override fun newArray(size: Int): Array<AnswerHistory?> {
                return arrayOfNulls(size)
            }
        }
    }

}