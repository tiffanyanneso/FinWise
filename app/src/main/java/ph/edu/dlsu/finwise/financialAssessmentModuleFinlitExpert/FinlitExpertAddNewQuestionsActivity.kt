package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.NewChoicesAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertAddNewQuestionsBinding
import ph.edu.dlsu.finwise.databinding.DialogueAddNewChoiceBinding

class FinlitExpertAddNewQuestionsActivity : AppCompatActivity() {

    class Choice (var choice:String?=null, var correct:Boolean?=null) {}

    private lateinit var binding: ActivityFinancialAssessmentFinlitExpertAddNewQuestionsBinding

    private lateinit var newChoicesAdapter:NewChoicesAdapter
    //create an inner class choice with string and boolean (check if correct answer)
    private var choicesArrayList = ArrayList<Choice>()

    private var firestore = Firebase.firestore

    private lateinit var assessmentID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentFinlitExpertAddNewQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            //TODO: ADD ALERT IF THERE ARE ALREADY 4 CHOICES
            addNewChoice()
        }

        binding.btnSave.setOnClickListener {
            saveQuestionAndChoices()
        }
        goToFinlitExpertEditAssessment()
    }

    private fun saveQuestionAndChoices() {
        //TODO: updatefin lit expert user ID
        var questionObject = hashMapOf(
             "assessmentID" to assessmentID,
             "question" to binding.etQuestion.text.toString(),
             "difficulty" to binding.dropdownDifficulty.text.toString(),
             "answerAccuracy" to 0.00F,
             "dateCreated" to Timestamp.now(),
             "dateModified" to Timestamp.now(),
             "createdBy" to "fintlitexpertID",
             "modifiedBy" to "finlitepxertID",
             "isUsed" to true,
            "nAssessments" to 0,
            "nAnsweredCorrectly" to 0
        )
        firestore.collection("AssessmentQuestions").add(questionObject).addOnSuccessListener {
            var questionID = it.id

            for (choiceItem in choicesArrayList)  {
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

    private fun addNewChoice() {
        var dialogBinding= DialogueAddNewChoiceBinding.inflate(getLayoutInflater())
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
        var dialogBinding= DialogueAddNewChoiceBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())
        dialogBinding.dialogEtNewChoice.setText(choice)
        dialogBinding.cbCorrect.isChecked = isCorrect

        dialog.window!!.setLayout(800, 1000)

        dialogBinding.btnSave.setOnClickListener {
            choicesArrayList.set(position, Choice(dialogBinding.dialogEtNewChoice.text.toString(), dialogBinding.cbCorrect.isChecked))
            newChoicesAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun goToFinlitExpertEditAssessment() {
        binding.btnCancel.setOnClickListener() {
            val goToFinlitExpertEditAssessmentActivity = Intent(applicationContext, FinlitExpertEditAssessmentActivity::class.java)
            startActivity(goToFinlitExpertEditAssessmentActivity)
        }


}
}