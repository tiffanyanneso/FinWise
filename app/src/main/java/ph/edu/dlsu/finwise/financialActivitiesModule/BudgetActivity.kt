package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.BudgetCategoryAdapter
import ph.edu.dlsu.finwise.adapter.BudgetCategoryTemplateAdapter
import ph.edu.dlsu.finwise.adapter.SpendingExpenseAdapter
import ph.edu.dlsu.finwise.databinding.*
import ph.edu.dlsu.finwise.financialAssessmentModule.FinancialAssessmentActivity
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class BudgetActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBudgetBinding
    private lateinit var dialogBinding:DialogNewBudgetItemBinding
    private var firestore = Firebase.firestore
    private lateinit var budgetCategoryAdapter: BudgetCategoryAdapter
    private lateinit var budgetCategorySelectAdapter: BudgetCategoryTemplateAdapter
    private lateinit var expenseAdapter:SpendingExpenseAdapter
    lateinit var context:Context
    lateinit var bundle: Bundle

    private lateinit var budgetingActivityID:String
    private lateinit var savingActivityID:String
    private lateinit var spendingActivityID:String

    private var budgetCategoryIDArrayList = ArrayList<String>()

    private var balance = 0.00F
    private var cashBalance = 0.00F
    private var mayaBalance = 0.00F
    private var availableToBudget = 0.00F

    private var isBudgetingCompleted = false

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var currentUserType:String
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

        CoroutineScope(Dispatchers.Main).launch {
            checkUser()
            getBudgetItems()
            loadBackButton()
            getExpenses()
        }

        var allCompleted = true
        firestore.collection("FinancialActivities").document(budgetingActivityID).get().addOnSuccessListener {
            var budgetingActivity = it.toObject<FinancialActivities>()
            if (budgetingActivity?.status == "Completed") {
                isBudgetingCompleted = true
                binding.tvAvailable.text = "Savings available to spend"
                binding.layoutExpenses.visibility = View.VISIBLE
                firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
                    if (it.toObject<Users>()!!.userType == "Child") {
                        binding.btnDoneSettingBudget.visibility = View.GONE
                        binding.btnDoneSpending.visibility = View.VISIBLE
                    }
                }
                binding.topAppBar.title = "Spending"

            }
            else if (budgetingActivity?.status != "Completed"){
                binding.topAppBar.title = "Budgeting"
                binding.tvAvailable.text = "Savings available to budget"
                allCompleted = false
                binding.layoutExpenses.visibility = View.GONE
                firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
                    if (it.toObject<Users>()!!.userType == "Child") {
                        binding.btnDoneSettingBudget.visibility = View.VISIBLE
                        binding.btnDoneSpending.visibility = View.GONE
                        goToFinancialAssessmentActivity("Pre-Activity", "Budgeting")
                    }
                }
            }

            getBalance()

            firestore.collection("FinancialGoals").document(budgetingActivity?.financialGoalID!!).get().addOnSuccessListener {
                binding.tvGoalName.text = it.toObject<FinancialGoals>()!!.goalName
            }
        }.continueWith {
            firestore.collection("FinancialActivities").document(spendingActivityID).get().addOnSuccessListener {
                var financialActivity = it.toObject<FinancialActivities>()
                if (financialActivity?.status != "Completed") {
                    allCompleted = false
                    firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
                        if (it.toObject<Users>()!!.userType == "Child" && financialActivity?.status == "In Progress")
                            goToFinancialAssessmentActivity("Pre-Activity", "Spending")

                    }
                }
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

        binding.btnDeposit.setOnClickListener {
            var deposit = Intent (this, SavingsDepositActivity::class.java)
            var sendBundle = Bundle()
            firestore.collection("FinancialActivities").document(budgetingActivityID).get().addOnSuccessListener {
                var activity = it.toObject<FinancialActivities>()
                sendBundle.putString("source", "budgeting")
                sendBundle.putString("financialGoalID", activity?.financialGoalID)
                sendBundle.putString("savingActivityID", savingActivityID)
                sendBundle.putString("budgetingActivityID", budgetingActivityID)
                sendBundle.putString("spendingActivityID", spendingActivityID)
                deposit.putExtras(sendBundle)
                startActivity(deposit)
            }
        }

        binding.btnWithdraw.setOnClickListener {
            if (balance == 0.00F) {
                cannotWithdrawDialog()
            } else {
                var withdraw = Intent(this, SavingsWithdrawActivity::class.java)
                var sendBundle = Bundle()
                firestore.collection("FinancialActivities").document(budgetingActivityID).get().addOnSuccessListener {
                    var activity = it.toObject<FinancialActivities>()
                    sendBundle.putString("source", "budgeting")
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
        firestore.collection("Transactions").whereEqualTo("financialActivityID", savingActivityID).whereIn("transactionType", Arrays.asList("Deposit", "Withdrawal")).get().addOnSuccessListener { results ->
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()

                if (transactionObject.transactionType == "Deposit") {
                    balance += transactionObject.amount!!

                    if (transactionObject.paymentType == "Cash")
                        cashBalance += transactionObject?.amount!!
                    else if (transactionObject.paymentType == "Maya")
                        mayaBalance += transactionObject?.amount!!
                }

                else if (transactionObject.transactionType == "Withdrawal") {
                    balance -= transactionObject.amount!!

                    if (transactionObject.paymentType == "Cash")
                        cashBalance -= transactionObject?.amount!!
                    else if (transactionObject.paymentType == "Maya")
                        mayaBalance -= transactionObject?.amount!!
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
        firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetingActivityID).whereEqualTo("status", "Active").get().addOnSuccessListener { results ->
            for (item in results) {
                var budgetItem = item.toObject<BudgetItem>()
                availableToBudget -= budgetItem.amount!!
            }
            binding.tvSavingsAvailable.text = "₱ " +  DecimalFormat("#,##0.00").format(availableToBudget)
        }
    }

    private fun getBudgetItems() {
        firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetingActivityID).whereEqualTo("status", "Active").get().addOnSuccessListener { budgetItems ->
            if (budgetItems.isEmpty && currentUserType == "Child")
                createBudgetTemplate()
            else {
                for (item in budgetItems)
                    budgetCategoryIDArrayList.add(item.id)

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
                binding.loadingItems.stopShimmer()
                binding.loadingItems.visibility = View.GONE
                binding.rvViewCategories.visibility = View.VISIBLE
            }
        }
    }

    private fun createBudgetTemplate() {
       firestore.collection("FinancialActivities").document(budgetingActivityID).get().addOnSuccessListener {
           firestore.collection("FinancialGoals").document(it.toObject<FinancialActivities>()?.financialGoalID!!).get().addOnSuccessListener { goalResult ->
               var budgetItemsList = ArrayList<String>()

               var goal = goalResult.toObject<FinancialGoals>()
               if (goal?.financialActivity == "Buying Items") {
                   budgetItemsList.add("Food & Drinks")
                   budgetItemsList.add("Toys & Games")
                   budgetItemsList.add("Gift")
               } else if (goal?.financialActivity == "Planning An Event") {
                   budgetItemsList.add("Food & Drinks")
                   budgetItemsList.add("Gift")
                   budgetItemsList.add("Decorations")
                   budgetItemsList.add("Rental")
                   budgetItemsList.add("Transportation")
                   budgetItemsList.add("Party Favors")
               } else if (goal?.financialActivity == "Situational Shopping") {
                   budgetItemsList.add("Food & Drinks")
                   budgetItemsList.add("Clothing")
               }

               var budgetTemplateDialogBinding = DialogBudgetItemTemplateBinding.inflate(getLayoutInflater())
               var budgetTemplateDialog= Dialog(this);
               budgetTemplateDialog.setContentView(budgetTemplateDialogBinding.getRoot())
               budgetTemplateDialog.window!!.setLayout(1000, 1300)

               budgetCategorySelectAdapter = BudgetCategoryTemplateAdapter(this, budgetItemsList, budgetTemplateDialogBinding.rvBudgetItemTemplate)
               budgetTemplateDialogBinding.rvBudgetItemTemplate.adapter = budgetCategorySelectAdapter
               budgetTemplateDialogBinding.rvBudgetItemTemplate.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
               budgetCategorySelectAdapter.notifyDataSetChanged()

               budgetTemplateDialogBinding.btnSaveBudgetItems.setOnClickListener {
                   saveBudgetItems()
                   budgetTemplateDialog.hide()
               }

               budgetTemplateDialog.setCancelable(false)
               budgetTemplateDialog.show()

           }
       }
    }

    private fun saveBudgetItems() {
        var checkedBudgetItems : ArrayList<BudgetCategoryTemplateAdapter.CheckedItem> = budgetCategorySelectAdapter.returnCheckedBudgetItems()
        println("print save budget item " + checkedBudgetItems.size)
        for (budgetItem in checkedBudgetItems) {
            var budgetItem = BudgetItem(budgetItem.itemName, null,budgetingActivityID, budgetItem.amount,  "Active", "Before", childID)
            firestore.collection("BudgetItems").add(budgetItem)
        }

        getBudgetItems()
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
        var dialogBinding = DialogNewBudgetItemBinding.inflate(getLayoutInflater())
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
                        budgetCategoryAdapter.notifyDataSetChanged()
                        firestore.collection("BudgetItems").add(budgetItem)
                            .addOnSuccessListener { newBudgetItem ->
                                budgetCategoryIDArrayList.add(position, newBudgetItem.id)
                                dialog.dismiss()
                                updateTransactions(budgetItemID, newBudgetItem.id)
                                budgetCategoryAdapter.notifyDataSetChanged()
                            }
                    }
                    //budgeting is not yet done, so just update directly
                } else {
                    firestore.collection("BudgetItems").document(budgetItemID).update("amount", itemAmount)
                    dialog.dismiss()
                    budgetCategoryAdapter.notifyDataSetChanged()
                    getAvailableToBudget()

                }
            }
        }

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun updateTransactions(oldBudgetItemID:String, newBudgetItemID:String) {
        //update transactions
        firestore.collection("Transactions").whereEqualTo("budgetItemID", oldBudgetItemID).get().addOnSuccessListener { results ->
            for (transaction in results)
                firestore.collection("Transactions").document(transaction.id).update("budgetItemID", newBudgetItemID)
        }

        //update shopping list items
        firestore.collection("ShoppingListItems").whereEqualTo("budgetCategory", oldBudgetItemID).get().addOnSuccessListener { results  ->
            for (shoppingListItem in results)
                firestore.collection("ShoppingListItems").document(shoppingListItem.id).update("budgetCategory", newBudgetItemID)
        }
    }

    private fun deleteBudgetItem(position:Int, budgetItemID:String) {
        budgetCategoryIDArrayList.removeAt(position)
        budgetCategoryAdapter.notifyDataSetChanged()

        //deleting budget item after declaring that done setting budget counts as an edit
        if (isBudgetingCompleted)
            firestore.collection("BudgetItems").document(budgetItemID).update("status", "Edited")
        else
            firestore.collection("BudgetItems").document(budgetItemID).update("status", "Deleted")

        getAvailableToBudget()
    }

    private fun showNewBudgetItemDialog() {
        dialogBinding= DialogNewBudgetItemBinding.inflate(getLayoutInflater())
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
            if (availableToBudget <= 0.0F)
                adjustBudgetDialog()
            else {
                dialog.window!!.setLayout(900, 1100)

                dialogBinding.btnSave.setOnClickListener {
                    if (filledUpAndValid()) {
                        var itemName = dialogBinding.dialogDropdownCategoryName.text.toString()
                        var itemAmount =
                            dialogBinding.dialogEtCategoryAmount.text.toString().toFloat()

                        lateinit var budgetItem: BudgetItem

                        var whenAdded = "Before"
                        if (isBudgetingCompleted)
                            whenAdded = "After"

                        if (itemName != "Others")
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

                dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
                dialog.show()
            }
        }
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
            firestore.collection("FinancialActivities").document(budgetingActivityID).update("status", "Completed", "dateCompleted", Timestamp.now()).addOnSuccessListener {
                binding.btnDoneSettingBudget.visibility = View.GONE
                //binding.linearLayoutText.visibility = View.GONE
                isBudgetingCompleted = true
                dialog.dismiss()
                firestore.collection("FinancialActivities").document(spendingActivityID).update("status", "In Progress", "dateStarted", Timestamp.now())
                goToFinancialAssessmentActivity("Post-Activity", "Budgeting")
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
            firestore.collection("FinancialActivities").document(spendingActivityID).update("status", "Completed", "dateCompleted", Timestamp.now())
            returnRemainingSavings()
            dialog.dismiss()
            goToFinancialAssessmentActivity("Post-Activity", "Spending")
//            var finact = Intent(this, FinancialActivity::class.java)
//            startActivity(finact)
        }

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun returnRemainingSavings() {
        if (cashBalance > 0.00F) {
            var cashWithdrawal = hashMapOf(
                "transactionName" to "Excess savings from " + binding.tvGoalName.text.toString(),
                "transactionType" to "Withdrawal",
                "category" to "Goal",
                "date" to Timestamp.now(),
                "userID" to currentUser,
                "amount" to cashBalance,
                "financialActivityID" to savingActivityID,
                "paymentType" to "Cash"
            )
            firestore.collection("Transactions").add(cashWithdrawal)
            firestore.collection("ChildWallet").whereEqualTo("childID", currentUser).whereEqualTo("type", "Cash").get().addOnSuccessListener {
                var walletID = it.documents[0].id
                firestore.collection("ChildWallet").document(walletID).get().addOnSuccessListener {
                    var updatedBalance = it.toObject<ChildWallet>()?.currentBalance!! + cashBalance
                    firestore.collection("ChildWallet").document(walletID).update("currentBalance", updatedBalance)
                }
            }
        }
        if (mayaBalance > 0.00F) {
            var mayaWithdrawal = hashMapOf(
                "transactionName" to "Excess savings from " + binding.tvGoalName.text.toString(),
                "transactionType" to "Withdrawal",
                "category" to "Goal",
                "date" to Timestamp.now(),
                "userID" to currentUser,
                "amount" to mayaBalance,
                "financialActivityID" to savingActivityID,
                "paymentType" to "Maya"
            )
            firestore.collection("Transactions").add(mayaWithdrawal)
            firestore.collection("ChildWallet").whereEqualTo("childID", currentUser).whereEqualTo("type", "Maya").get().addOnSuccessListener {
                var walletID = it.documents[0].id
                firestore.collection("ChildWallet").document(walletID).get().addOnSuccessListener {
                    var updatedBalance = it.toObject<ChildWallet>()?.currentBalance!! + mayaBalance
                    firestore.collection("ChildWallet").document(walletID).update("currentBalance", updatedBalance)
                }
            }
        }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun goToFinancialAssessmentActivity (assessmentType: String, assessmentCategory:String) {
        firestore.collection("Assessments").whereEqualTo("assessmentType", assessmentType).whereEqualTo("assessmentCategory", assessmentCategory).get().addOnSuccessListener {

            if (it.size()!= 0) {
                var assessmentID = it.documents[0].id
                firestore.collection("AssessmentAttempts").whereEqualTo("assessmentID", assessmentID).whereEqualTo("childID", currentUser).orderBy("dateTaken", Query.Direction.DESCENDING).get().addOnSuccessListener { results ->
                    if (results.size() != 0) {
                        var assessmentAttemptsObjects = results.toObjects<FinancialAssessmentAttempts>()
                        var latestAssessmentAttempt = assessmentAttemptsObjects[0].dateTaken

                        println("print assessment attempt" + SimpleDateFormat("MM/dd/yyyy").format(assessmentAttemptsObjects[0].dateTaken!!.toDate()))

                        val dateFormatter: DateTimeFormatter =
                            DateTimeFormatter.ofPattern("MM/dd/yyyy")
                        val lastTakenFormat =
                            SimpleDateFormat("MM/dd/yyyy").format(latestAssessmentAttempt!!.toDate())
                        val from = LocalDate.parse(lastTakenFormat.toString(), dateFormatter)
                        val today = SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate())
                        val to = LocalDate.parse(today.toString(), dateFormatter)
                        var difference = Period.between(from, to)


                        println("print from " + from)
                        println("print difference " + difference)
                        if (difference.days >= 7)
                            buildAssessmentDialog(assessmentType, assessmentCategory)
                        else {
                            if (assessmentType == "Post-Activity" && assessmentCategory == "Budgeting") {
                                var refresh = Intent(this, BudgetActivity::class.java)
                                var sendBundle = Bundle()
                                sendBundle.putString("savingActivityID", savingActivityID)
                                sendBundle.putString("budgetingActivityID", budgetingActivityID)
                                sendBundle.putString("spendingActivityID", spendingActivityID)
                                refresh.putExtras(sendBundle)
                                startActivity(refresh)
                            } else if (assessmentType == "Post-Activity" && assessmentCategory == "Spending") {
                                var finact = Intent(this, FinancialActivity::class.java)
                                startActivity(finact)
                            }
                        }
                    } else
                        buildAssessmentDialog(assessmentType, assessmentCategory)
                }
            } else {
                if (assessmentType == "Post-Activity" && assessmentCategory == "Budgeting") {
                    var refresh = Intent(this, BudgetActivity::class.java)
                    var sendBundle = Bundle()
                    sendBundle.putString("savingActivityID", savingActivityID)
                    sendBundle.putString("budgetingActivityID", budgetingActivityID)
                    sendBundle.putString("spendingActivityID", spendingActivityID)
                    refresh.putExtras(sendBundle)
                    startActivity(refresh)
                } else if (assessmentType == "Post-Activity" && assessmentCategory == "Spending") {
                    var finact = Intent(this, FinancialActivity::class.java)
                    startActivity(finact)
                }
            }
        }
    }

    private fun buildAssessmentDialog(assessmentType: String, assessmentCategory:String) {
        println("print type " + assessmentType +   " category " + assessmentCategory)
        var dialogBinding= DialogTakeAssessmentBinding.inflate(layoutInflater)
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.root)
        dialog.window!!.setLayout(950, 900)
        dialog.setCancelable(false)
        dialogBinding.btnOk.setOnClickListener {
            goToAssessment(assessmentType, assessmentCategory)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun goToAssessment(assessmentType:String, assessmentCategory:String) {
        val bundle = Bundle()
        bundle.putString("assessmentType", assessmentType)
        bundle.putString("assessmentCategory", assessmentCategory)

        val assessmentQuiz = Intent(this, FinancialAssessmentActivity::class.java)
        assessmentQuiz.putExtras(bundle)
        startActivity(assessmentQuiz)
    }


    private suspend fun checkUser() {
        currentUserType = firestore.collection("Users").document(currentUser).get().await().toObject<Users>()!!.userType!!
        //current user is parent
        if (currentUserType == "Parent") {
            binding.layoutWithdraw.visibility = View.GONE
            binding.btnDoneSettingBudget.visibility = View.GONE
            binding.btnDoneSpending.visibility = View.GONE
        } else if (currentUserType == "Child") {
            binding.layoutWithdraw.visibility = View.VISIBLE
            binding.btnDoneSettingBudget.visibility = View.VISIBLE
            binding.btnDoneSpending.visibility = View.VISIBLE
            childID = currentUser
        }
    }
    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}