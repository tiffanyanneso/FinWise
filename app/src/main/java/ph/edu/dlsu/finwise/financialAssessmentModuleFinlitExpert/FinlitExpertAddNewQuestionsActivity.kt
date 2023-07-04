package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.NavbarFinlitExpert
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.NewChoicesAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertAddNewQuestionsBinding
import ph.edu.dlsu.finwise.databinding.DialogAddNewChoiceBinding
import ph.edu.dlsu.finwise.databinding.DialogEditChoiceBinding

class FinlitExpertAddNewQuestionsActivity : AppCompatActivity() {

    class Choice (var choice:String?=null, var correct:Boolean?=null) {}

    private lateinit var binding: ActivityFinancialAssessmentFinlitExpertAddNewQuestionsBinding

    private lateinit var newChoicesAdapter:NewChoicesAdapter
    //create an inner class choice with string and boolean (check if correct answer)
    private var choicesArrayList = ArrayList<Choice>()

    private var firestore = Firebase.firestore

    private lateinit var assessmentID:String

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentFinlitExpertAddNewQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NavbarFinlitExpert(findViewById(R.id.bottom_nav_finlit_expert), this, R.id.nav_finlit_assessment)

        loadBackButton()

        var bundle = intent.extras!!
        assessmentID = bundle.getString("assessmentID").toString()

        newChoicesAdapter = NewChoicesAdapter(this, choicesArrayList, object:NewChoicesAdapter.EditChoice{
            override fun editChoice(position: Int, choice: String, isCorrect: Boolean) {
                editChoiceDialog(position, choice, isCorrect)
            }

        })
        binding.rvViewChoice.adapter = newChoicesAdapter
        binding.rvViewChoice.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        // for the dropdown
        val items = resources.getStringArray(R.array.assessment_question_difficulty)
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        binding.dropdownDifficulty.setAdapter(adapter)

        binding.btnAddNewChoices.setOnClickListener {
            if (choicesArrayList.size < 4) {
                binding.errorChoiceQuantity.visibility = View.GONE
                addNewChoice()
            } else {
                binding.errorChoiceQuantity.text = "Cannot add more than 4 choices"
                binding.errorChoiceQuantity.visibility = View.VISIBLE
            }
        }

        binding.btnSave.setOnClickListener {
            saveQuestionAndChoices()
        }
        goToFinlitExpertEditAssessment()
    }

    private fun saveQuestionAndChoices() {
        if (isValid()) {
            binding.errorChoiceQuantity.visibility = View.GONE
            var questionObject = hashMapOf(
                "assessmentID" to assessmentID,
                "question" to binding.etQuestion.text.toString(),
                "difficulty" to binding.dropdownDifficulty.text.toString(),
                "answerAccuracy" to 0.00F,
                "dateCreated" to Timestamp.now(),
                "dateModified" to Timestamp.now(),
                "createdBy" to currentUser,
                "modifiedBy" to currentUser,
                "isUsed" to true,
                "nAssessments" to 0,
                "nAnsweredCorrectly" to 0
            )
            firestore.collection("AssessmentQuestions").add(questionObject).addOnSuccessListener {
                var questionID = it.id

                for (choiceItem in choicesArrayList) {
                    var choiceObject = hashMapOf(
                        "questionID" to questionID,
                        "choice" to choiceItem.choice,
                        "isCorrect" to choiceItem.correct
                    )
                    firestore.collection("AssessmentChoices").add(choiceObject)
                }
                var editAssessment = Intent(this, FinlitExpertEditAssessmentActivity::class.java)
                var sendBundle = Bundle()
                sendBundle.putString("assessmentID", assessmentID)
                editAssessment.putExtras(sendBundle)
                this.startActivity(editAssessment)
            }
        }
    }

    private fun addNewChoice() {
        var dialogBinding= DialogAddNewChoiceBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(800, 1000)

        dialogBinding.btnSave.setOnClickListener {
            choicesArrayList.add(Choice(dialogBinding.dialogEtNewChoice.text.toString(), dialogBinding.cbCorrect.isChecked))
            newChoicesAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
        }
        dialog.show()
    }

    private fun editChoiceDialog(position:Int, choice:String, isCorrect:Boolean) {
        var dialogBinding= DialogEditChoiceBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())
        dialogBinding.dialogEtNewChoice.setText(choice)
        dialogBinding.cbCorrect.isChecked = isCorrect

        dialog.window!!.setLayout(1000, 1000)

        dialogBinding.btnSave.setOnClickListener {
            choicesArrayList.set(position, Choice(dialogBinding.dialogEtNewChoice.text.toString(), dialogBinding.cbCorrect.isChecked))
            newChoicesAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialogBinding.btnDelete.setOnClickListener {
            choicesArrayList.removeAt(position)
            newChoicesAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun isValid() : Boolean{
        var valid = true

        if (binding.etQuestion.text.toString().isEmpty()) {
            valid = false
            binding.containerQuestion.helperText = "Input question"
        } else
            binding.containerQuestion.helperText = ""

        if (binding.dropdownDifficulty.text.toString().isEmpty()) {
            valid = false
            binding.containerDifficulty.helperText = "Select question difficulty"
        } else
            binding.containerDifficulty.helperText = ""

        var correctAnswerCount = choicesArrayList.count{it.correct!!}
        if (correctAnswerCount != 1) {
            valid = false
            binding.errorCorrectAnswer.visibility = View.VISIBLE
            binding.errorCorrectAnswer.text = "There should be one correct answer"
        } else
            binding.errorCorrectAnswer.visibility = View.GONE

        if (choicesArrayList.size < 2) {
            valid = false
            binding.errorChoiceQuantity.text = "Input at least 2 choices"
            binding.errorChoiceQuantity.visibility = View.VISIBLE
        } else
            binding.errorChoiceQuantity.visibility = View.GONE

        return valid
    }

    private fun goToFinlitExpertEditAssessment() {
        binding.btnCancel.setOnClickListener() {
            val goToFinlitExpertEditAssessmentActivity = Intent(applicationContext, FinlitExpertEditAssessmentActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("assessmentID", assessmentID)
            goToFinlitExpertEditAssessmentActivity.putExtras(sendBundle)
            startActivity(goToFinlitExpertEditAssessmentActivity)
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}