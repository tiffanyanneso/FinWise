package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialConfirmDepositBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class FinancialActivityConfirmDeposit : AppCompatActivity() {

    private lateinit var binding : ActivityFinancialConfirmDepositBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context

    //Time
    val formatter = SimpleDateFormat("MM/dd/yyyy")
    val time = Calendar.getInstance().time
    val current = formatter.format(time)

    var amount : String? =null

    private lateinit var decisionMakingActivityID:String
    private lateinit var financialGoalID:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialConfirmDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        val bundle: Bundle = intent.extras!!
        decisionMakingActivityID = bundle.getString("decisionMakingActivityID").toString()
        financialGoalID = bundle.getString("financialGoalID").toString()
        binding.tvDate.text = SimpleDateFormat("MM/dd/yyyy").format(bundle.getSerializable("date"))
        amount = bundle.getFloat("amount").toString()
        binding.tvAmount.text = "₱ " +  DecimalFormat("#,##0.00").format(bundle.getFloat("amount"))
        binding.tvGoal.text= bundle.getString("goalName")

        binding.btnConfirm.setOnClickListener {
            val goalName = bundle.getString("goalName").toString()

            val transaction = hashMapOf(
                "transactionName" to goalName,
                "transactionType" to "Deposit",
                "category" to "Goal",
                "date" to bundle.getSerializable("date"),
                "createdBy" to "",
                "amount" to bundle.getFloat("amount"),
                "decisionMakingActivityID" to decisionMakingActivityID
            )

            //adjustUserBalance()

            firestore.collection("Transactions").add(transaction).addOnSuccessListener {
                val bundle = Bundle()
                bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
                bundle.putString("financialGoalID", financialGoalID)
                val savingActivity = Intent(this, SavingActivity::class.java)
                savingActivity.putExtras(bundle)
                savingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(savingActivity)
                finish()
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
                Toast.makeText(this, adjustedBalance.toString(), Toast.LENGTH_SHORT).show()

                firestore.collection("ChildWallet").document(id)
                    .update("currentBalance", FieldValue.increment(adjustedBalance))
            }
    }

}