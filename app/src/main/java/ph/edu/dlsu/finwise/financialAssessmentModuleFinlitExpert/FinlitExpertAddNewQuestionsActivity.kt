package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.NewChoicesAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertAddNewQuestionsBinding
import ph.edu.dlsu.finwise.databinding.DialogueAddNewChoiceBinding

class FinlitExpertAddNewQuestionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialAssessmentFinlitExpertAddNewQuestionsBinding

    private lateinit var newChoicesAdapter:NewChoicesAdapter
    private var choicesArrayList = ArrayList<String>()

    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentFinlitExpertAddNewQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newChoicesAdapter = NewChoicesAdapter(this, choicesArrayList)
        binding.rvViewChoice.adapter = newChoicesAdapter
        binding.rvViewChoice.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        binding.btnAddNewChoices.setOnClickListener {
            addNewChoice()
        }

        binding.btnSave.setOnClickListener {
            saveQuestionAndChoices()
        }
        goToFinlitExpertEditAssessment()
    }

    private fun saveQuestionAndChoices() {
        //TODO: update assessment ID and fin lit expert user ID
        var question = binding.etQuestion.text.toString()
        var questionObject = hashMapOf(
             "assessmentID" to "assessmentID",
             "question" to question,
             "answerAccuracy" to 0.00F,
             "dateCreated" to Timestamp.now(),
             "dateModified" to Timestamp.now(),
             "createdBy" to "fintlitexpertID",
             "modifiedBy" to "finlitepxertID",
             "isUsed" to true
        )
        firestore.collection("AssessmentQuestions").add(questionObject).addOnSuccessListener {
            var questionID = it.id

            //TODO: update how to check if the choice is the correct answer
            for (choice in choicesArrayList)  {
                var choiceObject = hashMapOf(
                    "questionID" to questionID,
                    "choice" to choice,
                    "isCorrect" to false
                )
                firestore.collection("AssessmentChoices").add(choiceObject)
            }
        }
    }

    private fun addNewChoice() {
        var dialogBinding= DialogueAddNewChoiceBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(800, 600)

        dialogBinding.btnSave.setOnClickListener {
            choicesArrayList.add(dialogBinding.dialogEtNewChoice.text.toString())
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