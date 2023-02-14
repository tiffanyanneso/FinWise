package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialConfirmDepositBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class FinancialActivityConfirmDeposit : AppCompatActivity() {

    private lateinit var binding : ActivityFinancialConfirmDepositBinding
    private var firestore = Firebase.firestore

    //Time
    val formatter = SimpleDateFormat("MM/dd/yyyy")
    val time = Calendar.getInstance().time
    val current = formatter.format(time)

    var amount : String? =null

    private lateinit var goalID:String
    private lateinit var savingActivityID:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialConfirmDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        val bundle: Bundle = intent.extras!!
        //decisionMakingActivityID = bundle.getString("decisionMakingActivityID").toString()
        binding.tvDate.text = SimpleDateFormat("MM/dd/yyyy").format(bundle.getSerializable("date"))
        binding.tvAmount.text = "₱ " +  DecimalFormat("#,##0.00").format(bundle.getFloat("amount"))
        binding.tvGoal.text= bundle.getString("goalName")
        goalID = bundle.getString("goalID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()

        binding.btnConfirm.setOnClickListener {
            val transaction = hashMapOf(
                "transactionName" to bundle.getString("goalName").toString() + " Deposit",
                "transactionType" to "Deposit",
                "category" to "Goal",
                "date" to bundle.getSerializable("date"),
                "createdBy" to "",
                "amount" to bundle.getFloat("amount"),
                "financialActivityID" to bundle.getString("savingActivityID")
                //"decisionMakingActivityID" to decisionMakingActivityID
            )

            //adjustUserBalance()

            firestore.collection("Transactions").add(transaction).addOnSuccessListener {
                //check if the goal was completed with this deposit, if it is, show accomplished screen
                checkGoalCompletion()
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

    private fun checkGoalCompletion() {
        firestore.collection("Transactions").whereEqualTo("financialActivityID", savingActivityID).get().addOnSuccessListener {results ->
            var saved = 0.00F
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()
                    if (transactionObject.transactionType == "Deposit")
                        saved += transactionObject.amount!!.toFloat()
                    else if (transactionObject.transactionType == "Withdrawal")
                        saved -= transactionObject.amount!!.toFloat()
            }

            firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener {
                var goal = it.toObject<FinancialGoals>()
                val bundle = Bundle()
                //bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
                bundle.putString("goalID", goalID)
                //target amount has already been met, update status of goal and fin activity to completed
                if (goal?.targetAmount!! <= saved) {
                    firestore.collection("FinancialGoals").document(goalID).update("status", "Completed")
                    firestore.collection("FinancialActivities").document(savingActivityID).update("status", "Completed")
                    val goalAccomplished = Intent(this, GoalAccomplishedActivity::class.java)
                    goalAccomplished.putExtras(bundle)
                    goalAccomplished.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    this.startActivity(goalAccomplished)
                    finish()
                } else {
                    val savingActivity = Intent(this, ViewGoalActivity::class.java)
                    savingActivity.putExtras(bundle)
                    savingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    this.startActivity(savingActivity)
                    finish()
                }
            }
        }
    }

}