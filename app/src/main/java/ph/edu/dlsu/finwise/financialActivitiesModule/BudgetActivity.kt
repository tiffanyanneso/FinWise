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
import com.google.firebase.firestore.ktx.toObjects
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

    private var spent = 0.00F
    private var totalBudget = 0.00F


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle: Bundle = intent.extras!!
        decisionMakingActivityID = bundle.getString("decisionMakingActivityID").toString()


        //get amount already allocated
        //getSpentAmount()

        //get the amount of needed for the decision making activity
        firestore.collection("DecisionMakingActivities").document(decisionMakingActivityID).get().addOnSuccessListener {
            var decisionMakingActivity = it.toObject<DecisionMakingActivities>()
            totalBudget = decisionMakingActivity!!.targetAmount!!
            binding.tvBudgetAmount.text = "₱ " + spent + "/ ₱ "  + decisionMakingActivity!!.targetAmount!!

            //get the name of financial goal
            firestore.collection("FinancialGoals").document(decisionMakingActivity?.financialGoalID.toString()).get().addOnSuccessListener {
                var financialGoal = it.toObject<FinancialGoals>()
                //binding.tvGoalName.text = financialGoal?.goalName.toString()
            }
        }

        getBudgetCategories()


        binding.btnNewCategory.setOnClickListener {
            showNewBudgetCategoryDialog()
        }
    }

    private fun getSpentAmount() {
        firestore.collection("BudgetCategories").whereEqualTo("decisionMakingActivityID", decisionMakingActivityID).get().addOnSuccessListener { documentSnapshot ->
            for (budgetCategorySnapshot in documentSnapshot) {
                var budgetCategory = budgetCategorySnapshot.toObject<BudgetCategory>()
                spent += budgetCategory.amount!!.toFloat()
            }
        }
    }
    
    private fun getBudgetCategories() {
        firestore.collection("BudgetCategories").whereEqualTo("decisionMakingActivityID", decisionMakingActivityID).get().addOnSuccessListener { budgetCategories ->
            for (document in budgetCategories) {
                var budgetCategoryID = document.id
                budgetCategoryIDArrayList.add(budgetCategoryID)

                var budgetCategory = document.toObject<BudgetCategory>()
                spent += budgetCategory.amount!!.toFloat()
            }
            budgetCategoryAdapter = BudgetCategoryAdapter(this, budgetCategoryIDArrayList)
            binding.rvViewCategories.adapter = budgetCategoryAdapter
            binding.rvViewCategories.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun updateSpent(newCategoryAmount:Float) {
        spent += newCategoryAmount
        binding.tvBudgetAmount.text = "₱ " + spent + "/ ₱ "  + totalBudget
    }

    private fun showNewBudgetCategoryDialog() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_new_budget_category)
        dialog.window!!.setLayout(800, 900)

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
                updateSpent(categoryAmount.toFloat())
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}