package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.common.models.TotalAmount
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMaya
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMayaResult
import com.paymaya.sdk.android.paywithpaymaya.SinglePaymentResult
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import org.json.JSONObject
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialConfirmExpenseBinding
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.math.BigDecimal
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

    private var expenseAmount = 0.00F
    private var cashBalance = 0.00F
    private var mayaBalance = 0.00F

    private lateinit var paymentType:String

    private lateinit var categoryName:String
    private lateinit var goalName:String
    private lateinit var expenseName:String

    private lateinit var savingActivityID:String
    private lateinit var budgetingActivityID:String
    private lateinit var spendingActivityID:String

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid


    // For Pay With PayMaya API: This is instantiating the Pay With PayMaya API.
    val payWithPayMayaClient = PayWithPayMaya.newBuilder()
        .clientPublicKey("pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(LogLevel.ERROR)
        .build()

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
        }
        //TODO: BTN CANCEL
    }

    private fun buildSinglePaymentRequest(amount:Float): SinglePaymentRequest {
        val amount = BigDecimal(amount.toDouble())
        val currency = "PHP"
        val redirectUrl = RedirectUrl(
            success = "http://success.com",
            failure = "http://failure.com",
            cancel = "http://cancel.com"
        )
        /* val metadata = JSONObject()
         metadata.put("customerName", "John Doe")
         metadata.put("customerEmail", "johndoe@example.com")
 */
        return SinglePaymentRequest(
            TotalAmount(amount, currency),
            "123456789",
            redirectUrl,
            null as JSONObject?
        )
    }

    // For Pay With PayMaya API: Once A Pay With PayMaya Activity finishes, this function receives the results.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        payWithPayMayaClient.onActivityResult(requestCode, resultCode, data)?.let {
            processCheckoutResult(it)
        }
    }

    private fun processCheckoutResult(result: PayWithPayMayaResult) {
        when (result) {
            is SinglePaymentResult.Success -> {
                val resultID: String = result.paymentId
                //Toast.makeText(this, "Payment Successful. Payment ID: $resultID", Toast.LENGTH_LONG).show()
                Log.d("PayMayaa", resultID)
                //adjustUserBalance()
                //lagay sa paywithpayamyaclient to
                goToSpending()
            }
            is SinglePaymentResult.Cancel -> {
                val resultID: String? = result.paymentId

                //Toast.makeText(this, "Payment Cancelled. Payment ID: $resultID", Toast.LENGTH_LONG).show()
                if (resultID != null) {
                    Log.d("PayMayaa", resultID)
                }
            }

            is SinglePaymentResult.Failure -> {
                val resultID: String? = result.paymentId
                val message =
                    "Failure, checkoutId: ${resultID}, exception: ${result.exception}"
                if (result.exception is BadRequestException) {
                    Log.d("PayMayaa", (result.exception as BadRequestException).error.toString())
                }
                //Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                Log.d("PayMayaa", message)

            }
            else -> {}
        }
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
            "financialActivityID" to savingActivityID,
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
            "budgetItemID" to budgetItemID,
            "financialActivityID" to spendingActivityID,
            "date" to bundle.getSerializable("date"),
            "paymentType" to "Cash")
        firestore.collection("Transactions").add(expense)

        var remainingWithdrawal = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Withdrawal",
            "transactionName" to "$goalName Withdrawal for $expenseName",
            "amount" to remainingAmount,
            "category" to "Goal",
            "financialActivityID" to savingActivityID,
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
            "budgetItemID" to budgetItemID,
            "financialActivityID" to spendingActivityID,
            "date" to bundle.getSerializable("date"),
            "paymentType" to "Maya")
        firestore.collection("Transactions").add(remainingExpense)

        payWithPayMayaClient.startSinglePaymentActivityForResult(this, buildSinglePaymentRequest(remainingAmount))
    }

    //primary payment method selected is maya, use up all maya savings first
    private fun splitMayaTransaction() {
        val remainingAmount = expenseAmount - mayaBalance
        //from goal savings to wallet
        val withdrawal = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Withdrawal",
            "transactionName" to "$goalName Withdrawal for $expenseName",
            "amount" to mayaBalance,
            "category" to "Goal",
            "financialActivityID" to savingActivityID,
            "date" to bundle.getSerializable("date"),
            "paymentType" to "Maya")
        firestore.collection("Transactions").add(withdrawal)

        //from wallet balance, record expense
        val expense = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Expense",
            "transactionName" to bundle.getString("expenseName"),
            "amount" to mayaBalance,
            "category" to categoryName,
            "budgetItemID" to budgetItemID,
            "financialActivityID" to spendingActivityID,
            "date" to bundle.getSerializable("date"),
            "paymentType" to "Maya"
        )
        firestore.collection("Transactions").add(expense)

        payWithPayMayaClient.startSinglePaymentActivityForResult(this, buildSinglePaymentRequest(mayaBalance))

        //from goal savings to wallet
        val remainingWithdrawal = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Withdrawal",
            "transactionName" to "$goalName Withdrawal for $expenseName",
            "amount" to remainingAmount,
            "category" to "Goal",
            "financialActivityID" to savingActivityID,
            "date" to bundle.getSerializable("date"),
            "paymentType" to "Cash")
        firestore.collection("Transactions").add(remainingWithdrawal)

        //from wallet balance, record expense
        val remainingExpense = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Expense",
            "transactionName" to bundle.getString("expenseName"),
            "amount" to remainingAmount,
            "category" to categoryName,
            "budgetItemID" to budgetItemID,
            "financialActivityID" to spendingActivityID,
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
            "financialActivityID" to savingActivityID,
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
            "budgetItemID" to budgetItemID,
            "financialActivityID" to spendingActivityID,
            "date" to bundle.getSerializable("date"),
            "paymentType" to paymentType
        )

        firestore.collection("Transactions").add(expense)

        if (paymentType == "Maya")
            payWithPayMayaClient.startSinglePaymentActivityForResult(this, buildSinglePaymentRequest(expenseAmount))
        else
            goToSpending()

    }

    private fun setFields() {
        savingActivityID = bundle.getString("savingActivityID").toString()
        budgetingActivityID = bundle.getString("budgetingActivityID").toString()
        spendingActivityID = bundle.getString("spendingActivityID").toString()
        budgetItemID = bundle.getString("budgetItemID").toString()
        expenseAmount = bundle.getFloat("amount")
        cashBalance = bundle.getFloat("cashBalance")
        mayaBalance = bundle.getFloat("mayaBalance")
        paymentType = bundle.getString("paymentType").toString()
        expenseName = bundle.getString("expenseName").toString()

        binding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(bundle.getFloat("amount"))
        binding.tvName.text = bundle.getString("expenseName")
        var date = bundle.getSerializable("date")
        binding.tvDate.text = SimpleDateFormat("MM/dd/yyyy").format(date)

        var totalBalance = cashBalance + mayaBalance - expenseAmount
        binding.tvUpdatedTotalSavings.text = "₱ " + DecimalFormat("#,##0.00").format(totalBalance)


        if (paymentType == "Cash") {
            if (cashBalance > expenseAmount) {
                binding.tvUpdatedCashSavings.text = "₱ " + DecimalFormat("#,###0.00").format(cashBalance - expenseAmount)
                binding.tvUpdatedMayaSavings.text = "₱ " + DecimalFormat("#,##0.00").format(mayaBalance)
            }
            //split between cash and maya, finish cash first
            else if (expenseAmount >= cashBalance) {
                var remaining  = expenseAmount - cashBalance
                binding.tvUpdatedCashSavings.text = "₱ " + DecimalFormat("#,##0.00").format(0)
                binding.tvUpdatedMayaSavings.text = "₱ " + DecimalFormat("#,##0.00").format( mayaBalance - remaining)
            }
        } else if (paymentType == "Maya") {
            if (mayaBalance >= expenseAmount) {
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

        firestore.collection("BudgetItems").document(bundle.getString("budgetItemID")!!).get().addOnSuccessListener {
            categoryName = it.toObject<BudgetItem>()!!.budgetItemName!!
            if (categoryName != "Others")
                binding.tvCategory.text = categoryName
            else
                binding.tvCategory.text = it.toObject<BudgetItem>()!!.budgetItemNameOther
        }


        firestore.collection("FinancialActivities").document(budgetingActivityID).get().addOnSuccessListener { activity ->
            var financialGoalID = activity.toObject<FinancialActivities>()!!.financialGoalID!!
            firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener { goal ->
                goalName = goal.toObject<FinancialGoals>()!!.goalName!!
            }
        }
    }

    private fun goToSpending() {
        var spending = Intent(this, SpendingActivity::class.java)
        var sendBundle = Bundle()
        sendBundle.putString("savingActivityID", savingActivityID)
        sendBundle.putString("budgetingActivityID", budgetingActivityID)
        sendBundle.putString("spendingActivityID", spendingActivityID)
        sendBundle.putString("budgetItemID", budgetItemID)
        sendBundle.putString("childID", currentUser)
        spending.putExtras(sendBundle)
        startActivity(spending)

    }

}