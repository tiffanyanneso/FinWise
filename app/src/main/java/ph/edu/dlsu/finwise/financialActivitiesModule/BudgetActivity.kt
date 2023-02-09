package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.BudgetCategoryAdapter
import ph.edu.dlsu.finwise.databinding.ActivityBudgetBinding
import ph.edu.dlsu.finwise.databinding.DialogNewBudgetCategoryBinding
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat

class BudgetActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBudgetBinding
    private var firestore = Firebase.firestore
    private lateinit var budgetCategoryAdapter: BudgetCategoryAdapter
    private lateinit var context:Context


    private lateinit var budgetActivityID:String
    private lateinit var savingActivityID:String
    private lateinit var financialGoalID:String
    private var budgetCategoryIDArrayList = ArrayList<String>()

    private var allocated = 0.00F
    private var totalBudget = 0.00F

    private var nextPriority = 0

    private var balance = 0.00F


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
        financialGoalID = bundle.getString("goalID").toString()
        budgetActivityID = bundle.getString("budgetActivityID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()


        getBalance()

        binding.btnNewCategory.setOnClickListener {
            showNewBudgetItemDialog()
        }

        binding.btnDoneSettingBudget.setOnClickListener {

        }
    }

    private fun deductExpenses() {
        firestore.collection("Transactions").whereEqualTo("financialActivityID", budgetActivityID).get().addOnSuccessListener { results ->
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()
                if (transactionObject.transactionType == "Expense")
                    balance -= transactionObject.amount!!
            }
            binding.tvSavingsAvailable.text = "₱ " +  DecimalFormat("#,###.00").format(balance)
        }.continueWith {  getBudgetItems() }
    }

    private fun getBalance() {
        firestore.collection("Transactions").whereEqualTo("financialActivityID", savingActivityID).get().addOnSuccessListener { results ->
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()
                if (transactionObject.transactionType == "Deposit")
                    balance += transactionObject.amount!!
                else if (transactionObject.transactionType == "Withdrawal" ||transactionObject.transactionType == "Expense" )
                    balance-= transactionObject.amount!!
            }
        }.continueWith { deductExpenses() }
    }

    /*private fun activateNextDecisionMakingActivity(){
        firestore.collection("DecisionMakingActivities").whereEqualTo("financialGoalID", financialGoalID).whereEqualTo("priority", nextPriority).get().addOnSuccessListener {
            var documentID = it.documents[0].id
            firestore.collection("DecisionMakingActivities").document(documentID).update("status", "In Progress")
        }
    }*/

    private fun getBudgetItems() {
        firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetActivityID).get().addOnSuccessListener { budgetItems ->
            for (item in budgetItems) {
                budgetCategoryIDArrayList.add(item.id)
                //var budgetCategory = item.toObject<BudgetItem>()
            }
            //getBudgetInfo()
            budgetCategoryAdapter = BudgetCategoryAdapter(this, budgetCategoryIDArrayList)
            binding.rvViewCategories.adapter = budgetCategoryAdapter
            binding.rvViewCategories.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    /*private fun getBudgetInfo() {
        //get the amount of needed for the decision making activity
        firestore.collection("DecisionMakingActivities").document(decisionMakingActivityID).get().addOnSuccessListener {
            var decisionMakingActivity = it.toObject<DecisionMakingActivities>()
            financialGoalID = decisionMakingActivity?.financialGoalID.toString()
            nextPriority = decisionMakingActivity?.priority.toString().toInt() + 1
            totalBudget = decisionMakingActivity?.targetAmount!!
            binding.tvBudgetAmount.text = "₱ " + DecimalFormat("#,###.00").format(allocated) + " / ₱ "  + DecimalFormat("#,###.00").format(decisionMakingActivity?.targetAmount)

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
    }*/

    private fun showNewBudgetItemDialog() {

        var dialogBinding= DialogNewBudgetCategoryBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(900, 1000)

        dialogBinding.btnSave.setOnClickListener {
            var itemName = dialogBinding.dialogEtCategoryName.text.toString()
            var itemAmount = dialogBinding.dialogEtCategoryAmount.text.toString().toFloat()
            var budgetCategory = BudgetItem(itemName, budgetActivityID, itemAmount, Timestamp.now(), 1)

            firestore.collection("BudgetItems").add(budgetCategory).addOnSuccessListener {
                budgetCategoryIDArrayList.add(it.id)
                budgetCategoryAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }


            //TODO: VALIDATION
            /*if (itemAmount.toFloat()+allocated > totalBudget)
                dialog.findViewById<TextView>(R.id.tv_error).visibility = View.VISIBLE
            else {
                firestore.collection("BudgetCategories").add(budgetCategory).addOnSuccessListener {
                    dialog.findViewById<TextView>(R.id.tv_error).visibility = View.GONE
                    dialog.dismiss()
                    budgetCategoryIDArrayList.add(it.id)
                    budgetCategoryAdapter.notifyDataSetChanged()
                }
            }*/
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}