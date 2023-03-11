package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityConfirmWithdrawBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.math.abs


class FinancialActivityConfirmWithdraw : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmWithdrawBinding
    private var firestore = Firebase.firestore

    private lateinit var bundle:Bundle

    private lateinit var financialGoalID:String
    private lateinit var savingActivityID:String
    private lateinit var budgetingActivityID:String
    private lateinit var spendingActivityID:String

    private var savedAmount = 0.00F

    private var withdrawAmount = 0.00F

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmWithdrawBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bundle = intent.extras!!

        setFields()

        binding.btnConfirm.setOnClickListener {
            var withdrawal = hashMapOf(
                "transactionName" to bundle.getString("goalName").toString() + " Withdrawal",
                "transactionType" to "Withdrawal",
                "category" to "Goal",
                "date" to bundle.getSerializable("date"),
                "createdBy" to currentUser,
                "amount" to bundle.getFloat("amount"),
                "financialActivityID" to bundle.getString("savingActivityID")
            )
            firestore.collection("Transactions").add(withdrawal).addOnSuccessListener {
                firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
                    // if not goal is not completed, adjust the savings
                    var goal = it.toObject<FinancialGoals>()
                    if (goal?.status != "Completed") {
                        var updatedSavings = goal?.currentSavings!! - bundle.getFloat("amount")
                        firestore.collection("FinancialGoals").document(financialGoalID).update("currentSavings", updatedSavings).addOnSuccessListener {
                            adjustUserBalance()
                            var bundle = Bundle()
                            //bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
                            bundle.putString("financialGoalID", financialGoalID)
                            var saving = Intent(this, ViewGoalActivity::class.java)
                            saving.putExtras(bundle)
                            saving.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            this.startActivity(saving)
                            finish()
                        }
                    }
                    //withdraw done when child is already in budgeting/spending
                    else {
                        var sendBundle = Bundle()
                        sendBundle.putString("savingActivityID", savingActivityID)
                        sendBundle.putString("budgetingActivityID", budgetingActivityID)
                        sendBundle.putString("spendingActivityID", spendingActivityID)
                        var budgeting = Intent(this, BudgetActivity::class.java)
                        budgeting.putExtras(sendBundle)
                        startActivity(budgeting)
                    }
                }
            }
        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToWithdraw = Intent(applicationContext, SavingsWithdrawActivity::class.java)

            var backBundle = Bundle()
            backBundle.putString("financialGoalID", financialGoalID)
            backBundle.putString("savingActivityID", savingActivityID)
            backBundle.putString("budgetingActivityID", budgetingActivityID)
            backBundle.putString("spendingActivityID", spendingActivityID)

            goToWithdraw.putExtras(backBundle)
            goToWithdraw.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToWithdraw)
        }
    }

    private fun setFields() {
        financialGoalID = bundle.getString("financialGoalID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()
        budgetingActivityID = bundle.getString("budgetingActivityID").toString()
        spendingActivityID = bundle.getString("spendingActivityID").toString()
        binding.tvGoal.text = bundle.getString("goalName")
        binding.tvAmount.text = "₱ " + DecimalFormat("#,###.00").format(bundle.getFloat("amount"))
        withdrawAmount = bundle.getFloat("amount")
        binding.tvDate.text = SimpleDateFormat("MM/dd/yyyy").format(bundle.getSerializable("date"))
        binding.tvUpdatedGoalSavings.text = "₱ " +  DecimalFormat("#,##0.00").format((bundle.getFloat("savedAmount") - bundle.getFloat("amount")))
        firestore.collection("ChildWallet").whereEqualTo("childID", "eWZNOIb9qEf8kVNdvdRzKt4AYrA2").get().addOnSuccessListener {
            var wallet = it.documents[0].toObject<ChildWallet>()
            binding.tvWalletBalance.text = "₱ " +  DecimalFormat("#,##0.00").format((wallet?.currentBalance!!.toFloat() + bundle.getFloat("amount")))
        }
    }

    private fun adjustUserBalance() {
        firestore.collection("ChildWallet").whereEqualTo("childID", currentUser).get().addOnSuccessListener { result ->
            var walletID = result.documents[0].id

            var adjustedBalance = abs(withdrawAmount?.toDouble()!!)
            firestore.collection("ChildWallet").document(walletID).update("currentBalance", FieldValue.increment(adjustedBalance))
        }
    }
}