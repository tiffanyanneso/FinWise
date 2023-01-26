package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.BudgetCategoryAdapter
import ph.edu.dlsu.finwise.adapter.GoalViewDepositAdapater
import ph.edu.dlsu.finwise.databinding.ActivityBudgetBinding
import ph.edu.dlsu.finwise.databinding.DialogNewBudgetCategoryBinding
import ph.edu.dlsu.finwise.model.BudgetCategory
import ph.edu.dlsu.finwise.model.DecisionMakingActivities
import ph.edu.dlsu.finwise.model.FinancialGoals

class BudgetActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBudgetBinding
    private var firestore = Firebase.firestore
    private lateinit var budgetCategoryAdapter: BudgetCategoryAdapter


    private lateinit var decisionMakingActivityID:String
    private var budgetCategoryIDArrayList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle: Bundle = intent.extras!!
        decisionMakingActivityID = bundle.getString("decisionActivityID").toString()

        //get the amount of needed for the decision making activity
        firestore.collection("DecisionMakingActivities").document(decisionMakingActivityID).get().addOnSuccessListener {
            var decisionMakingActvity = it.toObject<DecisionMakingActivities>()
            binding.tvBudgetAmount.text = "₱ "+ decisionMakingActvity?.targetAmount.toString()

            //get the name of financial goal
            firestore.collection("FinancialGoals").document(decisionMakingActvity?.financialGoalID.toString()).get().addOnSuccessListener {
                var financialGoal = it.toObject<FinancialGoals>()
                //binding.tvGoalName.text = financialGoal?.goalName.toString()
            }
        }

        getBudgetCategories()


        binding.btnNewCategory.setOnClickListener {
            showNewBudgetCategoryDialog()
        }
    }
    
    private fun getBudgetCategories() {
        firestore.collection("BudgetCategories").whereEqualTo("decisionMakingActivityID", decisionMakingActivityID).get().addOnSuccessListener { budgetCategories ->
            for (document in budgetCategories) {
                var budgetCategoryID = document.id
                budgetCategoryIDArrayList.add(budgetCategoryID)
            }
            budgetCategoryAdapter = BudgetCategoryAdapter(this, budgetCategoryIDArrayList)
            binding.rvViewCategories.adapter = budgetCategoryAdapter
            binding.rvViewCategories.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }


    private fun showNewBudgetCategoryDialog() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_new_budget_category)

        val btnSave = dialog.findViewById<Button>(R.id.btn_save)
        var btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)


        btnSave.setOnClickListener {
            var categoryName = dialog.findViewById<EditText>(R.id.dialog_et_category_name).text.toString()
            var categoryAmount = dialog.findViewById<EditText>(R.id.dialog_et_category_amount).text.toString()
            var budgetCategory = BudgetCategory(categoryName, decisionMakingActivityID, categoryAmount)
            firestore.collection("BudgetCategories").add(budgetCategory).addOnSuccessListener {
                dialog.dismiss()
                budgetCategoryIDArrayList.add(it.id)
                budgetCategoryAdapter.notifyDataSetChanged()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}