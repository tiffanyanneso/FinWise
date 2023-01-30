package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
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
    private lateinit var context:Context


    private lateinit var decisionMakingActivityID:String
    private lateinit var financialGoalID:String
    private var budgetCategoryIDArrayList = ArrayList<String>()

    private var allocated = 0.00F
    private var totalBudget = 0.00F

    private var nextPriority = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context =this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        var bundle: Bundle = intent.extras!!
        decisionMakingActivityID = bundle.getString("decisionMakingActivityID").toString()

        getBudgetCategories()

        binding.btnNewCategory.setOnClickListener {
            showNewBudgetCategoryDialog()
        }

        binding.btnDoneSettingBudget.setOnClickListener {
            firestore.collection("DecisionMakingActivities").document(decisionMakingActivityID).update("status", "Completed").addOnSuccessListener {
                //set the status of the next decision making activity to in progress
                activateNextDecisionMakingActivity()
                val viewGoal = Intent(this, ViewGoalActivity::class.java)
                var bundle = Bundle()
                bundle.putString("goalID", financialGoalID)
                viewGoal.putExtras(bundle)
                viewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(viewGoal)
            }
        }
    }

    private fun activateNextDecisionMakingActivity(){
        firestore.collection("DecisionMakingActivities").whereEqualTo("financialGoalID", financialGoalID).whereEqualTo("priority", nextPriority).get().addOnSuccessListener {
            var documentID = it.documents[0].id
            firestore.collection("DecisionMakingActivities").document(documentID).update("status", "In Progress")
        }
    }

    private fun getBudgetCategories() {
        firestore.collection("BudgetCategories").whereEqualTo("decisionMakingActivityID", decisionMakingActivityID).get().addOnSuccessListener { budgetCategories ->
            for (document in budgetCategories) {
                var budgetCategoryID = document.id
                budgetCategoryIDArrayList.add(budgetCategoryID)

                var budgetCategory = document.toObject<BudgetCategory>()
                allocated += budgetCategory.amount!!.toFloat()
            }
            getBudgetInfo()
            budgetCategoryAdapter = BudgetCategoryAdapter(this, budgetCategoryIDArrayList)
            binding.rvViewCategories.adapter = budgetCategoryAdapter
            binding.rvViewCategories.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun getBudgetInfo() {
        //get the amount of needed for the decision making activity
        firestore.collection("DecisionMakingActivities").document(decisionMakingActivityID).get().addOnSuccessListener {
            var decisionMakingActivity = it.toObject<DecisionMakingActivities>()
            financialGoalID = decisionMakingActivity?.financialGoalID.toString()
            nextPriority = decisionMakingActivity?.priority.toString().toInt() + 1
            totalBudget = decisionMakingActivity!!.targetAmount!!
            binding.tvBudgetAmount.text = "₱ " + allocated + " / ₱ "  + decisionMakingActivity!!.targetAmount!!

            //hide done setting budget budget if the activity is already completed
            if (decisionMakingActivity.status == "Completed") {
                binding.btnNewCategory.visibility = View.GONE
                binding.btnDoneSettingBudget.visibility = View.GONE
            } else {
                binding.btnNewCategory.visibility = View.VISIBLE
                binding.btnDoneSettingBudget.visibility = View.VISIBLE
            }

            //get the name of financial goal
            firestore.collection("FinancialGoals").document(decisionMakingActivity?.financialGoalID.toString()).get().addOnSuccessListener {
                var financialGoal = it.toObject<FinancialGoals>()
                //binding.tvGoalName.text = financialGoal?.goalName.toString()
            }
        }
    }

    private fun updateAllocated(newCategoryAmount:Float) {
        allocated += newCategoryAmount
        binding.tvBudgetAmount.text = "₱ " + allocated + "/ ₱ "  + totalBudget
    }

    private fun showNewBudgetCategoryDialog() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_new_budget_category)
        dialog.window!!.setLayout(900, 1000)

        val btnSave = dialog.findViewById<Button>(R.id.btn_save)
        var btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)


        btnSave.setOnClickListener {
            var categoryName = dialog.findViewById<EditText>(R.id.dialog_et_category_name).text.toString()
            var categoryAmount = dialog.findViewById<EditText>(R.id.dialog_et_category_amount).text.toString()
            var budgetCategory = BudgetCategory(categoryName, decisionMakingActivityID, categoryAmount)
            if (categoryAmount.toFloat()+allocated > totalBudget)
                dialog.findViewById<TextView>(R.id.tv_error).visibility = View.VISIBLE
            else {
                firestore.collection("BudgetCategories").add(budgetCategory).addOnSuccessListener {
                    dialog.findViewById<TextView>(R.id.tv_error).visibility = View.GONE
                    dialog.dismiss()
                    budgetCategoryIDArrayList.add(it.id)
                    budgetCategoryAdapter.notifyDataSetChanged()
                    updateAllocated(categoryAmount.toFloat())
                }
            }

        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}