package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityBudgetBinding
import ph.edu.dlsu.finwise.databinding.DialogNewBudgetCategoryBinding
import ph.edu.dlsu.finwise.model.DecisionMakingActivities
import ph.edu.dlsu.finwise.model.FinancialGoals

class BudgetActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBudgetBinding
    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle: Bundle = intent.extras!!
        var decisionMakingActivityID = bundle.getString("decisionActivityID").toString()

        //get the amount of needed for the decision making activity
        firestore.collection("DecisionMakingActivities").document(decisionMakingActivityID).get().addOnSuccessListener {
            var decisionMakingActvity = it.toObject<DecisionMakingActivities>()
            binding.tvBudgetAmount.text = decisionMakingActvity?.targetAmount.toString()

            //get the name of financial goal
            firestore.collection("FinancialGoals").document(decisionMakingActvity?.financialGoalID.toString()).get().addOnSuccessListener {
                var financialGoal = it.toObject<FinancialGoals>()
                binding.tvGoalName.text = financialGoal?.goalName.toString()
            }
        }




        binding.btnNewCategory.setOnClickListener {
            showNewBudgetCategoryDialog()
        }
    }


    private fun showNewBudgetCategoryDialog() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_new_budget_category)

        val btnSave = dialog.findViewById<Button>(R.id.btn_save)
        var btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)


        btnSave.setOnClickListener {
            //TODO: SAVE TO DB
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}