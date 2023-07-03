package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.NavbarFinlitExpert
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.NewChoicesAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertEditQuestionsBinding
import ph.edu.dlsu.finwise.databinding.DialogAddNewChoiceBinding
import ph.edu.dlsu.finwise.model.FinancialAssessmentChoices
import ph.edu.dlsu.finwise.model.FinancialAssessmentQuestions

class FinlitExpertEditQuestionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialAssessmentFinlitExpertEditQuestionsBinding
    private var firestore = Firebase.firestore

    private lateinit var questionID:String
    private lateinit var assessmentID:String
    private lateinit var bundle:Bundle

    private lateinit var choicesAdapter : NewChoicesAdapter
    private var choicesIDArrayList = ArrayList<String>()
    private var choicesArrayList = ArrayList<FinlitExpertAddNewQuestionsActivity.Choice>()

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentFinlitExpertEditQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NavbarFinlitExpert(findViewById(R.id.bottom_nav_finlit_expert), this, R.id.nav_finlit_assessment)

        goToFinlitExpertEditAssessment()
        setFields()
        loadBackButton()
        binding.btnAddNewChoices.setOnClickListener {
            if (choicesArrayList.size < 4) {
                binding.errorChoices.visibility = View.GONE
                addNewChoice()
            }  else {
                binding.errorChoices.text = "Cannot add more than 4 choices"
                binding.errorChoices.visibility = View.VISIBLE
            }
        }

        binding.btnSave.setOnClickListener {
            if (isValid())
                updateQuestion()
        }
    }

    private fun updateQuestion(){
        var isUsed = true
        if (binding.dropdownStatus.text.toString() == "Inactive")
            isUsed = false
        firestore.collection("AssessmentQuestions").document(questionID).update("question", binding.etQuestion.text.toString(),
        "difficulty", binding.dropdownDifficulty.text.toString(), "isUsed", isUsed, "dateModified", Timestamp.now(),
                            "modifiedBy", currentUser)

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
            this.startActivity(editAssessment)
        }
    }

    private fun addNewChoice() {
        var dialogBinding= DialogAddNewChoiceBinding.inflate(getLayoutInflater())
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
        var dialogBinding= DialogAddNewChoiceBinding.inflate(getLayoutInflater())
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
            var question = it.toObject<FinancialAssessmentQuestions>()
            binding.etQuestion.setText(question?.question)
            binding.dropdownDifficulty.setText(question?.difficulty)

            // for the dropdown
            var items = resources.getStringArray(R.array.assessment_question_difficulty)
            var adapter = ArrayAdapter(this, R.layout.list_item, items)
            binding.dropdownDifficulty.setAdapter(adapter)

            if (question?.isUsed == true)
                binding.dropdownStatus.setText("Active")
            else
                binding.dropdownStatus.setText("Inactive")
             items = resources.getStringArray(R.array.assessment_question_status)
             adapter = ArrayAdapter(this, R.layout.list_item, items)
            binding.dropdownStatus.setAdapter(adapter)
        }

        firestore.collection("AssessmentChoices").whereEqualTo("questionID", questionID).get().addOnSuccessListener { results ->
            for (choice in results) {
                choicesIDArrayList.add(choice.id)
                var choiceObject = choice.toObject<FinancialAssessmentChoices>()
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

    private fun goToFinlitExpertEditAssessment() {
        binding.btnCancel.setOnClickListener() {
            val specificAssessment = Intent(applicationContext, FinlitExpertEditAssessmentActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("assessmentID", assessmentID)
            specificAssessment.putExtras(sendBundle)
            startActivity(specificAssessment)
            //finish()
        }
    }

    private fun isValid() : Boolean {
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

        if (choicesArrayList.size < 2) {
            valid = false
            binding.errorChoices.text = "Input at least 2 choices"
            binding.errorChoices.visibility = View.VISIBLE
        } else
            binding.errorChoices.visibility = View.GONE

        return valid
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        val specificAssessment = Intent(applicationContext, FinlitExpertEditAssessmentActivity::class.java)
        val sendBundle = Bundle()
        sendBundle.putString("assessmentID", assessmentID)
        specificAssessment.putExtras(sendBundle)
        startActivity(specificAssessment)
        //finish()
    }
}