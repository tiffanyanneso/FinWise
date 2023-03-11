package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialConfirmExpenseBinding
import ph.edu.dlsu.finwise.model.BudgetItem
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class FinancialActivityConfirmExpense : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialConfirmExpenseBinding
    private var firestore = Firebase.firestore

    private lateinit var bundle:Bundle

    var amount : String? =null

    private lateinit var budgetItemID:String
    private lateinit var budgetingActivityID:String

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

        var sendBundle = Bundle()
        sendBundle.putString("budgetingActivityID", budgetingActivityID)
        sendBundle.putString("budgetItemID", budgetItemID)

        binding.btnConfirm.setOnClickListener {
            //withdraw money from savings to wallet
            var withdrawal = hashMapOf(
                "createdBy" to currentUser,
                "transactionType" to "Withdrawal",
                "transactionName" to bundle.getString("expenseName"),
                "amount" to bundle.getFloat("amount"),
                "category" to bundle.getString("budgetItemID"),
                "financialActivityID" to bundle.getString("savingActivityID"),
                "date" to bundle.getSerializable("date")
            )

            firestore.collection("Transactions").add(withdrawal)

            firestore.collection("BudgetItems").document(bundle.getString("budgetItemID").toString()).get().addOnSuccessListener {
                var budgetItem = it.toObject<BudgetItem>()
                var categoryName = budgetItem?.budgetItemName.toString()

                //from wallet balance, record expense
                var expense = hashMapOf(
                    "createdBy" to currentUser,
                    "transactionType" to "Expense",
                    "transactionName" to bundle.getString("expenseName"),
                    "amount" to bundle.getFloat("amount"),
                    "category" to categoryName,
                    "budgetItemID" to bundle.getString("budgetItemID"),
                    "financialActivityID" to bundle.getString("spendingActivityID"),
                    "date" to bundle.getSerializable("date")
                )

                firestore.collection("Transactions").add(expense).addOnSuccessListener {
                    var spending = Intent(this, SpendingActivity::class.java)
                    sendBundle.putString("budgetingActivityID", budgetingActivityID)
                    sendBundle.putString("budgetItemID", budgetItemID)
                    spending.putExtras(sendBundle)
                    this.startActivity(spending)
                    finish()
                }.continueWith {
                    adjustUserBalance()
                }

                //check if bundle contains shoppingListItemID, meaning that the expense was from a shopping list and need to update the status
                if (bundle.containsKey("shoppingListItemID")) {
                    firestore.collection("ShoppingListItems").document(bundle.getString("shoppingListItemID").toString()).update("itemName", bundle.getString("expenseName"))
                    firestore.collection("ShoppingListItems").document(bundle.getString("shoppingListItemID").toString()).update("status", "Purchased")
                }
            }
        }

        //TODO: BTN CANCEL
    }

    private fun setFields() {
        budgetingActivityID = bundle.getString("budgetingActivityID").toString()
        budgetItemID = bundle.getString("budgetItemID").toString()

        amount = bundle.getFloat("amount").toString()
        binding.tvAmount.text = "₱ " + DecimalFormat("#,###.00").format(bundle.getFloat("amount"))
        binding.tvName.text = bundle.getString("expenseName")
        var date = bundle.getSerializable("date")
        binding.tvDate.text = SimpleDateFormat("MM/dd/yyyy").format(date)

        firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
            var budgetItem = it.toObject<BudgetItem>()
            binding.tvCategory.text = budgetItem?.budgetItemName.toString()
        }
    }

    private fun adjustUserBalance() {
        firestore.collection("ChildWallet").whereEqualTo("childID", currentUser).get().addOnSuccessListener { documents -> lateinit var id: String
            for (document in documents) {
                id = document.id
            }
            var adjustedBalance = amount?.toDouble()
                adjustedBalance = -abs(adjustedBalance!!)

            firestore.collection("ChildWallet").document(id).update("currentBalance", FieldValue.increment(adjustedBalance))
        }
    }

}