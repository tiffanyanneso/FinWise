package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.BudgetCategoryAdapter
import ph.edu.dlsu.finwise.databinding.ActivityBudgetBinding
import ph.edu.dlsu.finwise.databinding.DialogDoneSettingBudgetBinding
import ph.edu.dlsu.finwise.databinding.DialogFinishBudgetingBinding
import ph.edu.dlsu.finwise.databinding.DialogNewBudgetCategoryBinding
import ph.edu.dlsu.finwise.databinding.DialogProceedNextActivityBinding
import ph.edu.dlsu.finwise.model.BudgetExpense
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat

class BudgetActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBudgetBinding
    private lateinit var dialogBinding:DialogNewBudgetCategoryBinding
    private var firestore = Firebase.firestore
    private lateinit var budgetCategoryAdapter: BudgetCategoryAdapter
    lateinit var context:Context
    lateinit var bundle: Bundle


    private lateinit var budgetActivityID:String
    private lateinit var savingActivityID:String
    private lateinit var spendingActivityID:String

    private lateinit var financialGoalID:String
    private var budgetCategoryIDArrayList = ArrayList<String>()

    private var allocated = 0.00F
    private var totalBudget = 0.00F

    private var nextPriority = 0

    private var balance = 0.00F

    private var isCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context =this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        budgetActivityID = bundle.getString("budgetActivityID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()
        spendingActivityID = bundle.getString("spendingActivityID").toString()

        //checks if child has already finished setting budget
        //if they are done setting budget, any changes would count as an update that would affect their overall score
        firestore.collection("FinancialActivities").document(budgetActivityID).get().addOnSuccessListener {
            var financialActivity = it.toObject<FinancialActivities>()
            if (financialActivity?.status == "Completed") {
                isCompleted = true
                binding.btnDoneSettingBudget.visibility = View.GONE
            }
        }
        //checkUser()
        getBalance()


        binding.btnNewCategory.setOnClickListener { showNewBudgetItemDialog() }
        binding.btnDoneSettingBudget.setOnClickListener {
            var dialogBinding = DialogDoneSettingBudgetBinding.inflate(getLayoutInflater())
            var dialog = Dialog(this);
            dialog.setContentView(dialogBinding.getRoot())
            dialog.window!!.setLayout(900, 800)

            dialogBinding.btnOk.setOnClickListener {
                firestore.collection("FinancialActivities").document(budgetActivityID).update("status", "Completed").addOnSuccessListener {
                    binding.btnDoneSettingBudget.visibility = View.GONE
                    isCompleted = true
                    dialog.dismiss()
                    firestore.collection("FinancialActivities").document(spendingActivityID).update("status", "In Progress")
                }
            }

            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    private fun getBalance() {
        firestore.collection("Transactions").whereEqualTo("financialActivityID", savingActivityID).get().addOnSuccessListener { results ->
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()
                if (transactionObject.transactionType == "Deposit")
                    balance += transactionObject.amount!!
                else if (transactionObject.transactionType == "Withdrawal")
                    balance-= transactionObject.amount!!
            }
        }.continueWith { getBudgetItems() }
    }

    private fun getBudgetItems() {
        firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetActivityID).get().addOnSuccessListener { budgetItems ->
            for (item in budgetItems) {
                var budgetItemObject = item.toObject<BudgetItem>()
                if (budgetItemObject.status == "Active")
                    budgetCategoryIDArrayList.add(item.id)
            }

            //set on click listener for menu item
            budgetCategoryAdapter = BudgetCategoryAdapter(this, budgetCategoryIDArrayList, object:BudgetCategoryAdapter.MenuClick{
                override fun clickMenuItem(position: Int, menuOption: String, budgetItemID: String) {
                    if (menuOption == "Edit")
                        editBudgetItem(budgetItemID)
                    else
                        deleteBudgetItem(position, budgetItemID)
                }}, object:BudgetCategoryAdapter.ItemClick {

                override fun clickItem(budgetItemID: String, budgetActivityID: String) {
                    //"done setting budget" already clicked, they can access expense
                    if (isCompleted) {
                        var budgetCategory = Intent(context, BudgetExpenseActivity::class.java)
                        var bundle = Bundle()

                        bundle.putString ("budgetItemID", budgetItemID)
                        //change to spending
                        bundle.putString ("spendingActivityID", spendingActivityID)
                        budgetCategory.putExtras(bundle)
                        budgetCategory.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(budgetCategory)
                    } else { finishBudgeting() }
                }
            })
            binding.rvViewCategories.adapter = budgetCategoryAdapter
            binding.rvViewCategories.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }.continueWith { deductExpenses() }
    }

    private fun deductExpenses() {
        if (budgetCategoryIDArrayList.size == 0)
            binding.tvSavingsAvailable.text = "₱ " +  DecimalFormat("#,###.00").format(balance)
        else {
            for (budgetCategoryID in budgetCategoryIDArrayList) {
                firestore.collection("BudgetExpenses").whereEqualTo("budgetCategoryID", budgetCategoryID).get().addOnSuccessListener { budgetExpenses ->
                    for (expense in budgetExpenses) {
                        var expenseObject = expense.toObject<BudgetExpense>()
                        balance -= expenseObject.amount!!
                    }
                }.continueWith { binding.tvSavingsAvailable.text = "₱ " +  DecimalFormat("#,###.00").format(balance) }
            }
        }
    }

    fun editBudgetItem(budgetItemID:String) {
        var dialogBinding= DialogNewBudgetCategoryBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(900, 1000)

        firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
            var budgetItem = it.toObject<BudgetItem>()
            dialogBinding.dialogEtCategoryName.setText(budgetItem?.budgetItemName.toString())
            dialogBinding.dialogEtCategoryAmount.setText(budgetItem?.amount!!.toInt().toString())

        }

        dialogBinding.btnSave.setOnClickListener {
            var itemName = dialogBinding.dialogEtCategoryName.text.toString()
            var itemAmount = dialogBinding.dialogEtCategoryAmount.text.toString().toFloat()

            firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
                var budgetItem = it.toObject<BudgetItem>()
                var nUpdate = budgetItem?.nUpdate!!.toInt()
                if (isCompleted)
                    nUpdate += 1

                firestore.collection("BudgetItems").document(budgetItemID).update("budgetItemName", itemName,
                    "amount", itemAmount, "nupdate", nUpdate).addOnSuccessListener {
                    budgetCategoryAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
            }
            //TODO: VALIDATION
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun deleteBudgetItem(position:Int, budgetItemID:String) {
        budgetCategoryIDArrayList.removeAt(position)
        budgetCategoryAdapter.notifyDataSetChanged()

        firestore.collection("BudgetItems").document(budgetItemID).update("status", "Deleted")

    }

    private fun showNewBudgetItemDialog() {

        dialogBinding= DialogNewBudgetCategoryBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(900, 1000)

        dialogBinding.btnSave.setOnClickListener {
            if (filledUpAndValid()) {
                var itemName = dialogBinding.dialogEtCategoryName.text.toString()
                var itemAmount = dialogBinding.dialogEtCategoryAmount.text.toString().toFloat()
                var nUpdate = 0
                if (isCompleted) //they made a change after they declared that they're done setting a budget
                    nUpdate = 1
                var budgetCategory = BudgetItem(itemName, budgetActivityID, itemAmount, nUpdate, "Active")

                firestore.collection("BudgetItems").add(budgetCategory).addOnSuccessListener {
                    budgetCategoryIDArrayList.add(it.id)
                    budgetCategoryAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun finishBudgeting() {
        var dialogBinding= DialogFinishBudgetingBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(900, 800)
        dialog.show()

        dialogBinding.btnOk.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun filledUpAndValid() : Boolean {
        var valid = true
        if (dialogBinding.dialogEtCategoryName.text.toString().trim().isEmpty()){
            dialogBinding.containerCategory.helperText = "Input budget name."
            valid = false
        } else
            dialogBinding.containerCategory.helperText = ""

        //check if text is empty
        if (dialogBinding.dialogEtCategoryAmount.text.toString().trim().isEmpty()){
            dialogBinding.containerAmount.helperText = "Input budget amount."
            valid = false
        } else {
            //text is not empty
            dialogBinding.containerAmount.helperText = ""
            //check if amount is greater than 0
            //TODO: VALIDATION TO CHECK IF BUDGET AMOUNT IS LESS THAN THEIR AVAIALBLE SAVINGS TO BUDGET
            if (dialogBinding.dialogEtCategoryAmount.text.toString().toFloat() <=0 ) {
                dialogBinding.containerAmount.helperText = "Input a valid amount."
                valid = false
            } else
                dialogBinding.containerAmount.helperText = ""
        }

        return valid
    }

    private fun checkUser() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("ParentUser").document(currentUser).get().addOnSuccessListener {
            //current user is parent
            if (it.exists()) {
                binding.btnNewCategory.visibility = View.GONE
                binding.btnDoneSettingBudget.visibility = View.GONE
            }
        }
    }
}