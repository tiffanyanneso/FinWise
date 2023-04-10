package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialConfirmDepositBinding
import ph.edu.dlsu.finwise.model.ChildWallet
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

    var amount : Float? =null

    private lateinit var financialGoalID:String
    private lateinit var savingActivityID:String
    private lateinit var budgetingActivityID:String
    private lateinit var spendingActivityID:String

    private lateinit var paymentType:String


    private lateinit var bundle:Bundle

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialConfirmDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        setFields()

        binding.btnConfirm.setOnClickListener {
            val transaction = hashMapOf(
                "transactionName" to bundle.getString("goalName").toString() + " Deposit",
                "transactionType" to "Deposit",
                "category" to "Goal",
                "date" to bundle.getSerializable("date"),
                "userID" to currentUser,
                "amount" to bundle.getFloat("amount"),
                "financialActivityID" to bundle.getString("savingActivityID"),
                "paymentType" to paymentType
            )

            adjustUserBalance()

            firestore.collection("Transactions").add(transaction).addOnSuccessListener {
                firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
                    var goal = it.toObject<FinancialGoals>()
                    var updatedSavings = goal?.currentSavings!! + bundle.getFloat("amount")
                    firestore.collection("FinancialGoals").document(financialGoalID).update("currentSavings", updatedSavings).addOnSuccessListener {
                        //check if the goal was completed with this deposit, if it is, show accomplished screen
                        checkGoalCompletion()
                    }
                }
            }
        }
    }

    private fun setFields() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        bundle = intent.extras!!
        //decisionMakingActivityID = bundle.getString("decisionMakingActivityID").toString()
        binding.tvDate.text = SimpleDateFormat("MM/dd/yyyy").format(bundle.getSerializable("date"))
        binding.tvAmount.text = "₱ " +  DecimalFormat("#,##0.00").format(bundle.getFloat("amount"))
        amount = bundle.getFloat("amount")
        binding.tvGoal.text= bundle.getString("goalName")
        financialGoalID = bundle.getString("financialGoalID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()
        budgetingActivityID = bundle.getString("budgetingActivityID").toString()
        spendingActivityID = bundle.getString("spendingActivityID").toString()

        paymentType = bundle.getString("paymentType").toString()
        binding.tvWalletType.text = paymentType
        binding.tvUpdatedGoalSavings.text = "₱ " +  DecimalFormat("#,##0.00").format((bundle.getFloat("savedAmount") + bundle.getFloat("amount")))
        firestore.collection("ChildWallet").whereEqualTo("childID", currentUser).whereEqualTo("type", paymentType).get().addOnSuccessListener {
            var wallet = it.documents[0].toObject<ChildWallet>()
            binding.tvWalletBalance.text = "₱ " +  DecimalFormat("#,##0.00").format((wallet?.currentBalance!!.toFloat() - bundle.getFloat("amount")))
        }


    }

    private fun adjustUserBalance() {
        firestore.collection("ChildWallet").whereEqualTo("childID", currentUser).whereEqualTo("type", paymentType).get().addOnSuccessListener { result ->
            var walletID = result.documents[0].id

            var adjustedBalance = -abs(amount?.toDouble()!!)
            firestore.collection("ChildWallet").document(walletID).update("currentBalance", FieldValue.increment(adjustedBalance))
        }
    }

    private fun checkGoalCompletion() {
        val sendBundle = Bundle()
        sendBundle.putString("financialGoalID", financialGoalID)
        sendBundle.putString("savingActivityID", savingActivityID)
        sendBundle.putString("budgetingActivityID", budgetingActivityID)
        sendBundle.putString("spendingActivityID", spendingActivityID)
        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            val goal = it.toObject<FinancialGoals>()
            //target amount has already been met, update status of goal and fin activity to completed
            if (goal?.targetAmount!! <= goal.currentSavings!!) {
                firestore.collection("FinancialGoals").document(financialGoalID).update("status", "Completed")
                firestore.collection("FinancialGoals").document(financialGoalID).update("dateCompleted", Timestamp.now())
                firestore.collection("FinancialActivities").document(savingActivityID).update("status", "Completed")
                firestore.collection("FinancialActivities").document(savingActivityID).update("dateCompleted", Timestamp.now())
                val goalAccomplished = Intent(this, GoalAccomplishedActivity::class.java)
                goalAccomplished.putExtras(sendBundle)
                goalAccomplished.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(goalAccomplished)
                finish()
            } else {
                val savingActivity = Intent(this, ViewGoalActivity::class.java)
                savingActivity.putExtras(sendBundle)
                savingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(savingActivity)
                finish()
            }
        }
    }
}