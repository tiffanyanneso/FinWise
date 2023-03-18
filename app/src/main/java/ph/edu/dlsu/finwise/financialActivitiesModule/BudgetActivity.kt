package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.BudgetCategoryAdapter
import ph.edu.dlsu.finwise.adapter.SpendingExpenseAdapter
import ph.edu.dlsu.finwise.databinding.ActivityBudgetBinding
import ph.edu.dlsu.finwise.databinding.DialogAdjustBudgetBinding
import ph.edu.dlsu.finwise.databinding.DialogDoneSettingBudgetBinding
import ph.edu.dlsu.finwise.databinding.DialogDoneSpendingBinding
import ph.edu.dlsu.finwise.databinding.DialogFinishBudgetingBinding
import ph.edu.dlsu.finwise.databinding.DialogNewBudgetCategoryBinding
import ph.edu.dlsu.finwise.databinding.DialogWarningCannotWtithdrawBinding
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class BudgetActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBudgetBinding
    private lateinit var dialogBinding:DialogNewBudgetCategoryBinding
    private var firestore = Firebase.firestore
    private lateinit var budgetCategoryAdapter: BudgetCategoryAdapter
    private lateinit var expenseAdapter:SpendingExpenseAdapter
    lateinit var context:Context
    lateinit var bundle: Bundle

    private lateinit var budgetingActivityID:String
    private lateinit var savingActivityID:String
    private lateinit var spendingActivityID:String

    private var budgetCategoryIDArrayList = ArrayList<String>()

    private var balance = 0.00F
    private var availableToBudget = 0.00F

    private var isBudgetingCompleted = false

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit  var currentUserType:String
    private lateinit var childID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context =this

        // initializes the navbar

        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        bundle = intent.extras!!
        savingActivityID = bundle.getString("savingActivityID").toString()
        budgetingActivityID = bundle.getString("budgetingActivityID").toString()
        spendingActivityID = bundle.getString("spendingActivityID").toString()
        childID = bundle.getString("childID").toString()

        checkUser()
        getBudgetItems()
        loadBackButton()
        if (spendingActivityID!=null)
            getExpenses()

        var allCompleted = true
        firestore.collection("FinancialActivities").document(budgetingActivityID).get().addOnSuccessListener {
            var budgetingActivity = it.toObject<FinancialActivities>()
            if (budgetingActivity?.status == "Completed") {
                isBudgetingCompleted = true
                binding.tvAvailable.text = "Savings available to spend"

                if (currentUserType == "Child") {
                    binding.btnDoneSettingBudget.visibility = View.GONE
                    binding.btnDoneSpending.visibility = View.VISIBLE
                }
            }
            else if (budgetingActivity?.status != "Completed"){
                binding.tvAvailable.text = "Savings available to budget"
                allCompleted = false
                if (currentUserType == "Child") {
                    binding.btnDoneSettingBudget.visibility = View.VISIBLE
                    binding.btnDoneSpending.visibility = View.GONE
                }
            }

            getBalance()

            firestore.collection("FinancialGoals").document(budgetingActivity?.financialGoalID!!).get().addOnSuccessListener {
                binding.tvGoalName.text = it.toObject<FinancialGoals>()!!.goalName
            }
        }.continueWith {
            firestore.collection("FinancialActivities").document(spendingActivityID).get().addOnSuccessListener {
                var financialActivity = it.toObject<FinancialActivities>()
                if (financialActivity?.status != "Completed")
                    allCompleted = false
                //spending is done, hide buttons
                else
                    binding.linearLayoutButtons.visibility = View.GONE
            }.continueWith {
                if (allCompleted)
                    binding.btnNewCategory.visibility = View.GONE
            }
        }


        binding.btnTransactions.setOnClickListener{
            var goToTransactions = Intent(this, ReasonExpensesActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("budgetingActivityID", budgetingActivityID)
            goToTransactions.putExtras(sendBundle)
            this.startActivity(goToTransactions)
        }

        binding.btnWithdraw.setOnClickListener {
            if (balance == 0.00F) {
                cannotWithdrawDialog()
            } else {
                var withdraw = Intent(this, SavingsWithdrawActivity::class.java)
                var sendBundle = Bundle()
                firestore.collection("FinancialActivities").document(budgetingActivityID).get().addOnSuccessListener {
                    var activity = it.toObject<FinancialActivities>()
                    sendBundle.putString("savingActivityID", savingActivityID)
                    sendBundle.putString("financialGoalID", activity?.financialGoalID)
                    withdraw.putExtras(sendBundle)
                    startActivity(withdraw)
                }
            }

        }

        binding.btnGoalDetails.setOnClickListener{
            firestore.collection("FinancialActivities").document(savingActivityID).get().addOnSuccessListener {
                var financialGoalID = it.toObject<FinancialActivities>()!!.financialGoalID
                var goalDetails = Intent (this, ViewGoalDetails::class.java)
                var sendBundle = Bundle()
                sendBundle.putString("financialGoalID", financialGoalID)
                goalDetails.putExtras(sendBundle)
                startActivity(goalDetails)
            }
        }
        binding.btnNewCategory.setOnClickListener { showNewBudgetItemDialog() }
        binding.btnDoneSettingBudget.setOnClickListener { doneSettingBudgetDialog() }
        binding.btnDoneSpending.setOnClickListener { doneSpendingDialog()}

        binding.tvViewExpenses.setOnClickListener {
            var spendingTransactions = Intent(this, SpendingTransactionsActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("spendingActivityID", spendingActivityID)
            spendingTransactions.putExtras(sendBundle)
            startActivity(spendingTransactions)
        }
    }

    private fun getBalance() {
        var cashBalance = 0.00F
        var mayaBalance = 0.00F
        firestore.collection("Transactions").whereEqualTo("financialActivityID", savingActivityID).whereIn("transactionType", Arrays.asList("Deposit", "Withdrawal")).get().addOnSuccessListener { results ->
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()
                if (transactionObject.transactionType == "Deposit")
                    balance += transactionObject.amount!!
                else if (transactionObject.transactionType == "Withdrawal")
                    balance-= transactionObject.amount!!

                if (transactionObject.paymentType == "Cash") {
                    if (transactionObject?.transactionType == "Deposit")
                        cashBalance += transactionObject.amount!!
                    else if (transactionObject.transactionType == "Withdrawal")
                        cashBalance -= transactionObject.amount!!
                }

                else if (transactionObject.paymentType == "Maya") {
                    if (transactionObject?.transactionType == "Deposit")
                        mayaBalance += transactionObject.amount!!
                    else if (transactionObject.transactionType == "Withdrawal")
                        mayaBalance -= transactionObject.amount!!
                }
            }
        }.continueWith {
            binding.tvSavingsAvailable.text = "₱ " +  DecimalFormat("#,##0.00").format(balance)
            binding.tvCashSavings.text = "₱ " + DecimalFormat("#,##0.00").format(cashBalance)
            binding.tvMayaSavings.text = "₱ " + DecimalFormat("#,##0.00").format(mayaBalance)
            if (!isBudgetingCompleted)
                getAvailableToBudget()
        }
    }

    private fun getAvailableToBudget () {
        availableToBudget = balance
        firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetingActivityID).get().addOnSuccessListener { results ->
            for (item in results) {
                var budgetItem = item.toObject<BudgetItem>()
                availableToBudget -= budgetItem.amount!!
            }
            binding.tvSavingsAvailable.text = "₱ " +  DecimalFormat("#,##0.00").format(availableToBudget)
        }
    }

    private fun getBudgetItems() {
        firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetingActivityID).get().addOnSuccessListener { budgetItems ->
            for (item in budgetItems) {
                var budgetItemObject = item.toObject<BudgetItem>()
                if (budgetItemObject.status == "Active")
                    budgetCategoryIDArrayList.add(item.id)
            }

            //set on click listener for menu item
            budgetCategoryAdapter = BudgetCategoryAdapter(
                this,
                budgetCategoryIDArrayList,
                spendingActivityID,
                object : BudgetCategoryAdapter.MenuClick {
                    override fun clickMenuItem(position: Int, menuOption: String, budgetItemID: String) {
                        if (menuOption == "Edit")
                            editBudgetItem(position, budgetItemID)
                        else
                            deleteBudgetItem(position, budgetItemID)
                    }},
                    object : BudgetCategoryAdapter.ItemClick {
                        override fun clickItem(budgetItemID: String, budgetingActivityID: String) {
                            //"done setting budget" already clicked, they can access expense
                            if (isBudgetingCompleted) {
                                var budgetExpense = Intent(context, SpendingActivity::class.java)
                                var bundle = Bundle()

                                bundle.putString("budgetingActivityID", budgetingActivityID)
                                bundle.putString("savingActivityID", savingActivityID)
                                bundle.putString("budgetItemID", budgetItemID)
                                bundle.putString("spendingActivityID", spendingActivityID)
                                bundle.putString("childID", childID)
                                budgetExpense.putExtras(bundle)
                                budgetExpense.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(budgetExpense)
                            } else {
                                finishBudgeting()
                            }
                        }
                })
            binding.rvViewCategories.adapter = budgetCategoryAdapter
            binding.rvViewCategories.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun getExpenses() {
        var expenses = ArrayList<Transactions>()
        firestore.collection("Transactions").whereEqualTo("financialActivityID", spendingActivityID).whereEqualTo("transactionType", "Expense").get().addOnSuccessListener { results ->
            for (transaction in results)
                expenses.add(transaction.toObject())

            expenseAdapter = SpendingExpenseAdapter(this, expenses)
            binding.rvExpenses.adapter = expenseAdapter
            binding.rvExpenses.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            expenseAdapter.notifyDataSetChanged()
        }
    }


    fun editBudgetItem(position:Int, budgetItemID:String) {
        var amount = 0
        var dialogBinding = DialogNewBudgetCategoryBinding.inflate(getLayoutInflater())
        var dialog = Dialog(this);

        firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetingActivityID)
            .whereEqualTo("status", "Active").get().addOnSuccessListener { results ->
            for (budgetItem in results) {
                var budgetItemObject = budgetItem.toObject<BudgetItem>()
                availableToBudget -= budgetItemObject.amount!!
            }
        }.continueWith {
            firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
                var budgetItem = it.toObject<BudgetItem>()
                amount = budgetItem?.amount!!.toInt()
                dialogBinding.dialogDropdownCategoryName.setText(budgetItem?.budgetItemName.toString())
                dialogBinding.dialogEtCategoryAmount.setText(budgetItem?.amount!!.toInt().toString())
            }

            if(!isBudgetingCompleted)
                dialogBinding.tvRemainingBalance.text = "You have ₱${DecimalFormat("#,##0.00").format(availableToBudget)} remaining left to budget"
            else
                dialogBinding.tvRemainingBalance.text = "You have ₱${DecimalFormat("#,##0.00").format(balance)} in your savings"
        }
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(900, 1000)

        //disable editing of category
        dialogBinding.dialogDropdownCategoryName.isClickable = false
        dialogBinding.dialogDropdownCategoryName.isEnabled = false
        availableToBudget = balance


        dialogBinding.btnSave.setOnClickListener {
            var itemName = dialogBinding.dialogDropdownCategoryName.text.toString()
            var itemAmount = dialogBinding.dialogEtCategoryAmount.text.toString().toFloat()
            //check that the fields were changed
            if (dialogBinding.dialogEtCategoryAmount.text.toString().toInt() != amount) {
                //budgeting is already completed
                if (isBudgetingCompleted) {
                    firestore.collection("BudgetItems").document(budgetItemID).update("status", "Edited").addOnSuccessListener {
                        lateinit var budgetItem: BudgetItem
                        if (itemName != "Others")
                            budgetItem = BudgetItem(itemName, null, budgetingActivityID, itemAmount, "Active", "After", currentUser)
                        else
                            budgetItem = BudgetItem(itemName, dialogBinding.dialogEtOtherCategoryName.text.toString(), budgetingActivityID, itemAmount, "Active", "After", currentUser)

                        budgetCategoryIDArrayList.removeAt(position)

                        firestore.collection("BudgetItems").add(budgetItem)
                            .addOnSuccessListener { newBudgetItem ->
                                budgetCategoryIDArrayList.add(position, newBudgetItem.id)
                                dialog.dismiss()
                            }
                    }
                    //budgeting is not yet done, so just update directly
                } else {
                    firestore.collection("BudgetItems").document(budgetItemID).update("amount", itemAmount)
                    dialog.dismiss()
                }
                budgetCategoryAdapter.notifyDataSetChanged()
            }
        }

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun deleteBudgetItem(position:Int, budgetItemID:String) {
        budgetCategoryIDArrayList.removeAt(position)
        budgetCategoryAdapter.notifyDataSetChanged()

        //deleting budget item after declaring that done setting budget counts as an edit
        if (isBudgetingCompleted)
            firestore.collection("BudgetItems").document(budgetItemID).update("status", "Edited")
        else
            firestore.collection("BudgetItems").document(budgetItemID).update("status", "Deleted")
    }

    private fun showNewBudgetItemDialog() {
        dialogBinding= DialogNewBudgetCategoryBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        val items = resources.getStringArray(R.array.pfm_expense_category)
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        dialogBinding.dialogDropdownCategoryName.setAdapter(adapter)

        //hiding other category name field
        dialogBinding.dialogDropdownCategoryName.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
           if (dialogBinding.dialogDropdownCategoryName.text.toString() == "Others")
               dialogBinding.containerOtherCategoryName.visibility = View.VISIBLE
            else
               dialogBinding.containerOtherCategoryName.visibility = View.GONE
        }

        availableToBudget = balance
        firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetingActivityID).whereEqualTo("status", "Active").get().addOnSuccessListener { results ->
            for (budgetItem in results) {
                var budgetItemObject = budgetItem.toObject<BudgetItem>()
                availableToBudget -= budgetItemObject.amount!!
            }
        }.continueWith {
            dialogBinding.tvRemainingBalance.text = "You have ₱${DecimalFormat("#,##0.00").format(availableToBudget)} remaining left to budget"

            //budgeting is not yet done
            if (!isBudgetingCompleted) {
                if (availableToBudget <= 0.0F)
                    adjustBudgetDialog()
            }
            else  {
                dialog.window!!.setLayout(900, 1100)

                dialogBinding.btnSave.setOnClickListener {
                    if (filledUpAndValid()) {
                        var itemName = dialogBinding.dialogDropdownCategoryName.text.toString()
                        var itemAmount = dialogBinding.dialogEtCategoryAmount.text.toString().toFloat()

                        lateinit var budgetItem : BudgetItem

                        var whenAdded = "Before"
                        if (isBudgetingCompleted)
                            whenAdded = "After"

                        if (itemName!= "Others")
                            budgetItem = BudgetItem(itemName, null, budgetingActivityID, itemAmount, "Active", whenAdded, currentUser)
                        else
                            budgetItem = BudgetItem(itemName, dialogBinding.dialogEtOtherCategoryName.text.toString(), budgetingActivityID, itemAmount, "Active", whenAdded, currentUser)

                        firestore.collection("BudgetItems").add(budgetItem).addOnSuccessListener { budgetItem ->
                            getAvailableToBudget()
                            budgetCategoryIDArrayList.add(budgetItem.id)
                            budgetCategoryAdapter.notifyDataSetChanged()
                            dialog.dismiss()
                        }
                    }
                }

                dialogBinding.btnCancel.setOnClickListener { dialog.dismiss()}
                dialog.show()
            }}

    }

    private fun adjustBudgetDialog() {
        var adjustBudgetDialogBinding = DialogAdjustBudgetBinding.inflate(getLayoutInflater())
        var adjustBudgetDialog= Dialog(this);
        adjustBudgetDialog.setContentView(adjustBudgetDialogBinding.getRoot())

        adjustBudgetDialog.window!!.setLayout(900, 800)
        adjustBudgetDialogBinding.btnOk.setOnClickListener { adjustBudgetDialog.dismiss() }

        adjustBudgetDialog.show()
    }

    private fun finishBudgeting() {
        var dialogBinding= DialogFinishBudgetingBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(900, 800)
        dialog.show()

        dialogBinding.btnOk.setOnClickListener { dialog.dismiss() }
    }

    private fun filledUpAndValid() : Boolean {
        var valid = true
        if (dialogBinding.dialogDropdownCategoryName.text.toString().trim().isEmpty()){
            dialogBinding.containerCategory.helperText = "Select budget category"
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

    private fun doneSettingBudgetDialog() {
        var dialogBinding = DialogDoneSettingBudgetBinding.inflate(getLayoutInflater())
        var dialog = Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())
        dialog.window!!.setLayout(900, 800)

        dialogBinding.btnOk.setOnClickListener {
            firestore.collection("FinancialActivities").document(budgetingActivityID).update("status", "Completed").addOnSuccessListener {
                binding.btnDoneSettingBudget.visibility = View.GONE
                //binding.linearLayoutText.visibility = View.GONE
                isBudgetingCompleted = true
                dialog.dismiss()
                firestore.collection("FinancialActivities").document(spendingActivityID).update("status", "In Progress")
                var refresh = Intent(this, BudgetActivity::class.java)
                var sendBundle = Bundle()
                sendBundle.putString("savingActivityID", savingActivityID)
                sendBundle.putString("budgetingActivityID", budgetingActivityID)
                sendBundle.putString("spendingActivityID", spendingActivityID)
                refresh.putExtras(sendBundle)
                startActivity(refresh)
            }
        }

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun doneSpendingDialog() {
        var dialogBinding = DialogDoneSpendingBinding.inflate(getLayoutInflater())
        var dialog = Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())
        dialog.window!!.setLayout(900, 800)

        dialogBinding.btnOk.setOnClickListener {
            firestore.collection("FinancialActivities").document(spendingActivityID).update("status", "Completed")
            dialog.dismiss()
            var finact = Intent(this, FinancialActivity::class.java)
            startActivity(finact)
        }

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun cannotWithdrawDialog() {
        var dialogBinding = DialogWarningCannotWtithdrawBinding.inflate(getLayoutInflater())
        var dialog = Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())
        dialog.window!!.setLayout(900, 800)

        dialogBinding.btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun checkUser() {
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            var user  = it.toObject<Users>()!!
            currentUserType = user.userType!!
            //current user is parent
            if (user.userType == "Parent") {
                binding.layoutWithdraw.visibility = View.GONE
                binding.btnDoneSettingBudget.visibility = View.GONE
                binding.btnDoneSpending.visibility = View.GONE
                binding.btnNewCategory.visibility = View.GONE
            } else if (user.userType == "Child") {
                binding.layoutWithdraw.visibility = View.VISIBLE
                binding.btnDoneSettingBudget.visibility = View.VISIBLE
                binding.btnDoneSpending.visibility = View.VISIBLE
                binding.btnNewCategory.visibility = View.VISIBLE
            }
        }
    }
    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}