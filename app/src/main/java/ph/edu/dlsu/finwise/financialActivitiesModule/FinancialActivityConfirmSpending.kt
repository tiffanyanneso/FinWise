package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityFinancialConfirmSpendingBinding
import ph.edu.dlsu.finwise.databinding.ActivityFinancialRecordExpenseBinding
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


    private lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialConfirmSpendingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=  this
        bundle = intent.extras!!

        setFields()

        binding.btnConfirm.setOnClickListener {
           var expense = hashMapOf(
               "childID" to "",
               "transactionType" to "Expense",
               "transactionName" to bundle.getString("expenseName"),
               "amount" to bundle.getFloat("amount"),
               "category" to bundle.getString("expenseCategory"),
               "financialGoalID" to bundle.getString("financialGoalID"),
               "decisionMakingActivityID" to bundle.getString("decisionMakingActivityID"),
               "date" to current
           )

            //adjustUserBalance()

            firestore.collection("Transactions").add(expense).addOnSuccessListener {
                var spending = Intent(this, SpendingActivity::class.java)
                spending.putExtras(bundle)
                context.startActivity(spending)
            }
        }

        //TODO: BTN CANCEL
    }

    private fun setFields() {
        amount = bundle.getFloat("amount").toString()
        binding.tvAmount.text = "₱ " + bundle.getFloat("amount").toString()
        binding.tvName.text = bundle.getString("expenseName")
        binding.tvCategory.text = bundle.getString("expenseCategory")
        binding.tvDate.text = current
    }

    private fun adjustUserBalance() {
        //TODO: Change user based on who is logged in
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        firestore.collection("ChildWallet").whereEqualTo("childID", "JoCGIUSVMWTQ2IB7Rf41ropAv3S2")
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