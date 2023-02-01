package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialConfirmSpendingBinding
import ph.edu.dlsu.finwise.databinding.ActivityFinancialRecordExpenseBinding
import ph.edu.dlsu.finwise.model.BudgetCategory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class FinancialActivityConfirmSpending : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialConfirmSpendingBinding
    private var firestore = Firebase.firestore

    private lateinit var bundle:Bundle

    val time = Calendar.getInstance().time
    val formatter = SimpleDateFormat("MM/dd/yyyy")
    val current = formatter.format(time)

    var amount : String? =null

    private lateinit var financialGoalID:String
    private lateinit var spendingDecisionMakingActivityID:String
    private lateinit var budgetCategoryID:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialConfirmSpendingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bundle = intent.extras!!

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        setFields()
        getBudgetCategoryID(bundle.getString("expenseCategory").toString())

        binding.btnConfirm.setOnClickListener {
           var expense = hashMapOf(
               "childID" to "",
               "transactionType" to "Expense",
               "transactionName" to bundle.getString("expenseName"),
               "amount" to bundle.getFloat("amount"),
               "category" to budgetCategoryID,
               "financialGoalID" to financialGoalID,
               "decisionMakingActivityID" to spendingDecisionMakingActivityID,
               "date" to bundle.getSerializable("date")
           )

            //adjustUserBalance()

            firestore.collection("Transactions").add(expense).addOnSuccessListener {
                var spending = Intent(this, SpendingActivity::class.java)
                spending.putExtras(bundle)
                this.startActivity(spending)
                finish()
            }
        }

        //TODO: BTN CANCEL
    }

    private fun setFields() {
        financialGoalID = bundle.getString("financialGoalID").toString()
        spendingDecisionMakingActivityID  = bundle.getString("decisionMakingActivityID").toString()
        amount = bundle.getFloat("amount").toString()
        binding.tvAmount.text = "₱ " + bundle.getFloat("amount").toString()
        binding.tvName.text = bundle.getString("expenseName")
        binding.tvCategory.text = bundle.getString("expenseCategory")
        binding.tvDate.text = bundle.getSerializable("date").toString()
    }

    private fun getBudgetCategoryID(expenseCategoryName:String) {
        firestore.collection("DecisionMakingActivities").whereEqualTo("financialGoalID", financialGoalID).whereEqualTo("decisionMakingActivity", "Setting a Budget").get().addOnSuccessListener {
            var budgetingDecisionMakingActivityID = it.documents[0].id
            firestore.collection("BudgetCategories").whereEqualTo("decisionMakingActivityID", budgetingDecisionMakingActivityID).whereEqualTo("budgetCategory", bundle.getString("expenseCategory")).get().addOnSuccessListener { result ->
                budgetCategoryID = result.documents[0].id
            }
        }
    }

    private fun adjustUserBalance() {
        //TODO: Change user based on who is logged in
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        firestore.collection("ChildWallet").whereEqualTo("childID", "eWZNOIb9qEf8kVNdvdRzKt4AYrA2")
            .get().addOnSuccessListener { documents ->
                lateinit var id: String
                for (document in documents) {
                    id = document.id
                }
                var adjustedBalance = amount?.toDouble()
                    adjustedBalance = -abs(adjustedBalance!!)

                firestore.collection("ChildWallet").document(id)
                    .update("currentBalance", FieldValue.increment(adjustedBalance))
            }
    }

}