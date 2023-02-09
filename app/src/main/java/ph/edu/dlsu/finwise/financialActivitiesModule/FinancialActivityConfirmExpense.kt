package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    val time = Calendar.getInstance().time
    val formatter = SimpleDateFormat("MM/dd/yyyy")
    val current = formatter.format(time)

    var amount : String? =null

    private lateinit var budgetItemID:String
    private lateinit var budgetActivityID:String


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
        sendBundle.putString("budgetActivityID", budgetActivityID)
        sendBundle.putString("budgetItemID", budgetItemID)

        binding.btnConfirm.setOnClickListener {
            //TODO: CHANGE WHERE THE BUDGET ITEM ID IS PLACED
           var expense = hashMapOf(
               "childID" to "",
               "transactionType" to "Expense",
               "transactionName" to bundle.getString("expenseName"),
               "amount" to bundle.getFloat("amount"),
               "category" to budgetItemID,
               "financialActivityID" to budgetActivityID,
               "date" to bundle.getSerializable("date")
           )

            //adjustUserBalance()

            firestore.collection("Transactions").add(expense).addOnSuccessListener {
                var spending = Intent(this, BudgetExpenseActivity::class.java)
                sendBundle.putString("budgetActivityID", budgetActivityID)
                sendBundle.putString("budgetItemID", budgetItemID)
                spending.putExtras(sendBundle)
                this.startActivity(spending)
                finish()
            }
        }

        //TODO: BTN CANCEL
    }

    private fun setFields() {
        budgetActivityID = bundle.getString("budgetActivityID").toString()
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