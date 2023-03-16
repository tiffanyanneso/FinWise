package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialConfirmExpenseBinding
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.exp

class FinancialActivityConfirmExpense : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialConfirmExpenseBinding
    private var firestore = Firebase.firestore

    private lateinit var bundle:Bundle

    private lateinit var budgetItemID:String
    private lateinit var budgetingActivityID:String

    private var expenseAmount = 0.00F
    private var cashBalance = 0.00F
    private var mayaBalance = 0.00F

    private lateinit var paymentType:String

    private lateinit var categoryName:String
    private lateinit var goalName:String
    private lateinit var expenseName:String

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialConfirmExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bundle = intent.extras!!

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        setFields()

        binding.btnConfirm.setOnClickListener {
            if (paymentType == "Cash") {
                //need to split transaction
                if (expenseAmount > cashBalance)
                    splitCashTransaction()
                else
                    normalTransaction()
            } else if (paymentType == "Maya") {
                //need to split transaction
                if (expenseAmount > mayaBalance)
                    splitMayaTransaction()
                else
                    normalTransaction()
            }

            //check if bundle contains shoppingListItemID, meaning that the expense was from a shopping list and need to update the status
            if (bundle.containsKey("shoppingListItemID")) {
                firestore.collection("ShoppingListItems").document(bundle.getString("shoppingListItemID").toString()).update("itemName", bundle.getString("expenseName"))
                firestore.collection("ShoppingListItems").document(bundle.getString("shoppingListItemID").toString()).update("status", "Purchased")
            }

            var spending = Intent(this, SpendingActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("budgetingActivityID", budgetingActivityID)
            sendBundle.putString("budgetItemID", budgetItemID)
            spending.putExtras(sendBundle)
            this.startActivity(spending)
            finish()
        }
        //TODO: BTN CANCEL
    }

    //primary payment method selected is cash, use up all cash savings first
    private fun splitCashTransaction() {
        var remainingAmount = expenseAmount - cashBalance
        var withdrawal = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Withdrawal",
            "transactionName" to "$goalName Withdrawal for $expenseName",
            "amount" to cashBalance,
            "category" to "Goal",
            "financialActivityID" to bundle.getString("savingActivityID"),
            "date" to bundle.getSerializable("date"),
            "paymentType" to "Cash")
        firestore.collection("Transactions").add(withdrawal)

        //from wallet balance, record expense
        var expense = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Expense",
            "transactionName" to bundle.getString("expenseName"),
            "amount" to cashBalance,
            "category" to categoryName,
            "budgetItemID" to bundle.getString("budgetItemID"),
            "financialActivityID" to bundle.getString("spendingActivityID"),
            "date" to bundle.getSerializable("date"),
            "paymentType" to "Cash")
        firestore.collection("Transactions").add(expense)

        var remainingWithdrawal = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Withdrawal",
            "transactionName" to "$goalName Withdrawal for $expenseName",
            "amount" to remainingAmount,
            "category" to "Goal",
            "financialActivityID" to bundle.getString("savingActivityID"),
            "date" to bundle.getSerializable("date"),
            "paymentType" to "Maya")
        firestore.collection("Transactions").add(remainingWithdrawal)

        //from wallet balance, record expense
        var remainingExpense = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Expense",
            "transactionName" to bundle.getString("expenseName"),
            "amount" to remainingAmount,
            "category" to categoryName,
            "budgetItemID" to bundle.getString("budgetItemID"),
            "financialActivityID" to bundle.getString("spendingActivityID"),
            "date" to bundle.getSerializable("date"),
            "paymentType" to "Maya")
        firestore.collection("Transactions").add(remainingExpense)

    }

    //primary payment method selected is maya, use up all maya savings first
    private fun splitMayaTransaction() {
        var remainingAmount = expenseAmount - mayaBalance
        var withdrawal = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Withdrawal",
            "transactionName" to "$goalName Withdrawal for $expenseName",
            "amount" to mayaBalance,
            "category" to "Goal",
            "financialActivityID" to bundle.getString("savingActivityID"),
            "date" to bundle.getSerializable("date"),
            "paymentType" to "Maya")
        firestore.collection("Transactions").add(withdrawal)

        //from wallet balance, record expense
        var expense = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Expense",
            "transactionName" to bundle.getString("expenseName"),
            "amount" to mayaBalance,
            "category" to categoryName,
            "budgetItemID" to bundle.getString("budgetItemID"),
            "financialActivityID" to bundle.getString("spendingActivityID"),
            "date" to bundle.getSerializable("date"),
            "paymentType" to "Maya"
        )
        firestore.collection("Transactions").add(expense)

        var remainingWithdrawal = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Withdrawal",
            "transactionName" to "$goalName Withdrawal for $expenseName",
            "amount" to remainingAmount,
            "category" to "Goal",
            "financialActivityID" to bundle.getString("savingActivityID"),
            "date" to bundle.getSerializable("date"),
            "paymentType" to "Cash")
        firestore.collection("Transactions").add(remainingWithdrawal)

        //from wallet balance, record expense
        var remainingExpense = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Expense",
            "transactionName" to bundle.getString("expenseName"),
            "amount" to remainingAmount,
            "category" to categoryName,
            "budgetItemID" to bundle.getString("budgetItemID"),
            "financialActivityID" to bundle.getString("spendingActivityID"),
            "date" to bundle.getSerializable("date"),
            "paymentType" to "Cash"
        )
        firestore.collection("Transactions").add(remainingExpense)
    }

    private fun normalTransaction() {
        //withdraw money from savings to wallet
        var withdrawal = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Withdrawal",
            "transactionName" to "$goalName Withdrawal for $expenseName",
            "amount" to expenseAmount,
            "category" to "Goal",
            "financialActivityID" to bundle.getString("savingActivityID"),
            "date" to bundle.getSerializable("date"),
            "paymentType" to paymentType
        )

        firestore.collection("Transactions").add(withdrawal)

        //from wallet balance, record expense
        var expense = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Expense",
            "transactionName" to bundle.getString("expenseName"),
            "amount" to expenseAmount,
            "category" to categoryName,
            "budgetItemID" to bundle.getString("budgetItemID"),
            "financialActivityID" to bundle.getString("spendingActivityID"),
            "date" to bundle.getSerializable("date"),
            "paymentType" to paymentType
        )

        firestore.collection("Transactions").add(expense)
    }

    private fun setFields() {
        budgetingActivityID = bundle.getString("budgetingActivityID").toString()
        budgetItemID = bundle.getString("budgetItemID").toString()
        expenseAmount = bundle.getFloat("amount")
        cashBalance = bundle.getFloat("cashBalance")
        mayaBalance = bundle.getFloat("mayaBalance")
        paymentType = bundle.getString("paymentType").toString()
        expenseName = bundle.getString("expenseName").toString()

        binding.tvAmount.text = "₱ " + DecimalFormat("#,###.00").format(bundle.getFloat("amount"))
        binding.tvName.text = bundle.getString("expenseName")
        var date = bundle.getSerializable("date")
        binding.tvDate.text = SimpleDateFormat("MM/dd/yyyy").format(date)

        var totalBalance = cashBalance + mayaBalance - expenseAmount
        binding.tvUpdatedTotalSavings.text = "₱ " + DecimalFormat("#,###.00").format(totalBalance)

        if (paymentType == "Cash") {
            if (cashBalance > expenseAmount) {
                binding.tvUpdatedCashSavings.text = "₱ " + DecimalFormat("#,###.00").format(cashBalance - expenseAmount)
                binding.tvUpdatedMayaSavings.text = "₱ " + DecimalFormat("#,###.00").format(mayaBalance)
            }
            //split between cash and maya, finish cash first
            else if (expenseAmount > cashBalance) {
                var remaining  = expenseAmount - cashBalance
                binding.tvUpdatedCashSavings.text = "₱ " + DecimalFormat("#,##0.00").format(0)
                binding.tvUpdatedMayaSavings.text = "₱ " + DecimalFormat("#,##0.00").format( mayaBalance - remaining)
            }
        } else if (paymentType == "Maya") {
            if (mayaBalance > expenseAmount) {
                binding.tvUpdatedMayaSavings.text = "₱ " + DecimalFormat("#,##0.00").format( mayaBalance - expenseAmount)
                binding.tvUpdatedCashSavings.text = "₱ " + DecimalFormat("#,##0.00").format(cashBalance)
            }
            //split between maya and cash, finish maya first
            else if (expenseAmount > mayaBalance) {
                var remaining  = expenseAmount - mayaBalance
                binding.tvUpdatedMayaSavings.text = "₱ " + DecimalFormat("#,##0.00").format( 0)
                binding.tvUpdatedCashSavings.text = "₱ " + DecimalFormat("#,##0.00").format(cashBalance - remaining)
            }
        }

        firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
            categoryName = it.toObject<BudgetItem>()!!.budgetItemName!!
            if (categoryName != "Others")
                binding.tvCategory.text = categoryName
            else
                binding.tvCategory.text = it.toObject<BudgetItem>()!!.budgetItemNameOther
        }

        firestore.collection("FinancialActivities").document(budgetingActivityID).get().addOnSuccessListener { activity ->
            var financialGoalID = activity.toObject<FinancialActivities>()!!.financialGoalID!!
            println("print " +  financialGoalID)
            firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener { goal ->
                goalName = goal.toObject<FinancialGoals>()!!.goalName!!
            }
        }
    }

}