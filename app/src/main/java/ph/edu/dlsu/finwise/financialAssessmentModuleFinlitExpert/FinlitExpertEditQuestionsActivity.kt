package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.NewChoicesAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertEditQuestionsBinding
import ph.edu.dlsu.finwise.databinding.DialogueAddNewChoiceBinding
import ph.edu.dlsu.finwise.model.AssessmentChoices
import ph.edu.dlsu.finwise.model.AssessmentQuestions

class FinlitExpertEditQuestionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialAssessmentFinlitExpertEditQuestionsBinding
    private var firestore = Firebase.firestore

    private lateinit var questionID:String
    private lateinit var assessmentID:String
    private lateinit var bundle:Bundle

    private lateinit var choicesAdapter : NewChoicesAdapter
    private var choicesIDArrayList = ArrayList<String>()
    private var choicesArrayList = ArrayList<FinlitExpertAddNewQuestionsActivity.Choice>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentFinlitExpertEditQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFields()

        binding.btnAddNewChoices.setOnClickListener {
            //TODO: ADD ALERT IF THERE ARE ALREADY 4 CHOICES
            addNewChoice()
        }

        binding.btnSave.setOnClickListener {
            updateQuestion()
        }
    }

    private fun updateQuestion(){
        firestore.collection("AssessmentQuestions").document(questionID).update("question", binding.etQuestion.text.toString(),
        "difficulty", binding.dropdownDifficulty.text.toString(), "dateModified", Timestamp.now(),
                            "modifiedBy", "currentFinLitExpert")

        firestore.collection("AssessmentChoices").whereEqualTo("questionID", questionID).get().addOnSuccessListener { results ->
            for (choice in results)
                firestore.collection("AssessmentChoices").document(choice.id).delete()

                for (choiceItem in choicesArrayList)  {
                    var choiceObject = hashMapOf(
                        "questionID" to questionID,
                        "choice" to choiceItem.choice,
                        "isCorrect" to choiceItem.correct
                    )
                    firestore.collection("AssessmentChoices").add(choiceObject)
                }
        }.continueWith {
            Toast.makeText(this, "Question Updated", Toast.LENGTH_SHORT)
            var editAssessment = Intent(this, FinlitExpertEditAssessmentActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("assessmentID", assessmentID)
            editAssessment.putExtras(sendBundle)
            startActivity(editAssessment)
        }
    }

    private fun addNewChoice() {
        var dialogBinding= DialogueAddNewChoiceBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(800, 1000)

        dialogBinding.btnSave.setOnClickListener {
            choicesArrayList.add(FinlitExpertAddNewQuestionsActivity.Choice(dialogBinding.dialogEtNewChoice.text.toString(),
                dialogBinding.cbCorrect.isChecked))
            choicesAdapter.notifyDataSetChanged()
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
            choicesArrayList.set(position, FinlitExpertAddNewQuestionsActivity.Choice(dialogBinding.dialogEtNewChoice.text.toString(),
                    dialogBinding.cbCorrect.isChecked))
            choicesAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setFields() {
        bundle = intent.extras!!
        questionID = bundle.getString("questionID").toString()
        assessmentID = bundle.getString("assessmentID").toString()


        firestore.collection("AssessmentQuestions").document(questionID).get().addOnSuccessListener {
            var question = it.toObject<AssessmentQuestions>()
            binding.etQuestion.setText(question?.question)
            binding.dropdownDifficulty.setText(question?.difficulty)

            // for the dropdown
            val items = resources.getStringArray(R.array.assessment_question_difficulty)
            val adapter = ArrayAdapter(this, R.layout.list_item, items)
            binding.dropdownDifficulty.setAdapter(adapter)
        }

        firestore.collection("AssessmentChoices").whereEqualTo("questionID", questionID).get().addOnSuccessListener { results ->
            for (choice in results) {
                choicesIDArrayList.add(choice.id)
                var choiceObject = choice.toObject<AssessmentChoices>()
                choicesArrayList.add(FinlitExpertAddNewQuestionsActivity.Choice(choiceObject?.choice, choiceObject?.isCorrect))
            }
            choicesAdapter = NewChoicesAdapter(this, choicesArrayList, object:NewChoicesAdapter.EditChoice{
                override fun editChoice(position: Int, choice: String, isCorrect: Boolean) {
                    editChoiceDialog(position, choice, isCorrect)
                }
            })
            binding.rvViewChoice.adapter = choicesAdapter
            binding.rvViewChoice.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }
}