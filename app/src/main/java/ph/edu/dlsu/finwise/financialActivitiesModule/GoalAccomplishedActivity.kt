package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityGoalAccomplishedBinding
import ph.edu.dlsu.finwise.databinding.DialogProceedNextActivityBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class GoalAccomplishedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalAccomplishedBinding
    private var firestore = Firebase.firestore

    private lateinit var  financialGoalID:String
    private lateinit var savingActivityID:String
    private lateinit var budgetingActivityID:String
    private lateinit var spendingActivityID:String

    private lateinit var goalName:String

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalAccomplishedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var bundle: Bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()
        budgetingActivityID = bundle.getString("budgetingActivityID").toString()
        spendingActivityID = bundle.getString("spendingActivityID").toString()

        setText()

        var sendBundle = Bundle()
        sendBundle.putString("financialGoalID", financialGoalID)
        sendBundle.putString("savingActivityID", savingActivityID)
        sendBundle.putString("budgetingActivityID", budgetingActivityID)
        sendBundle.putString("spendingActivityID", spendingActivityID)


        binding.btnFinish.setOnClickListener {
            var dialogBinding = DialogProceedNextActivityBinding.inflate(getLayoutInflater())
            var dialog = Dialog(this);
            dialog.setContentView(dialogBinding.getRoot())
            dialog.window!!.setLayout(900, 800)

            dialog.show()
            dialogBinding.btnProceed.setOnClickListener {
                firestore.collection("FinancialActivities").document(budgetingActivityID).update("status", "In Progress")
                var budgetActivity = Intent(this, BudgetActivity::class.java)
                budgetActivity.putExtras(sendBundle)
                this.startActivity(budgetActivity)
            }

            dialogBinding.btnNo.setOnClickListener {
                //withdrawal transaction from goal to wallet
                adjustWalletBalances()
            }
        }
    }

    private fun setText () {
        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var financialGoal = it.toObject<FinancialGoals>()
            goalName = financialGoal?.goalName.toString()
            binding.tvGoal.text = goalName
            binding.tvActivity.text = financialGoal?.financialActivity
            binding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(financialGoal?.targetAmount)
            // convert timestamp to date string
            binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(financialGoal?.targetDate?.toDate())
        }
    }

    private fun adjustWalletBalances() {
        var cashBalance = 0.00F
        var mayaBalance = 0.00F
        firestore.collection("Transactions").whereEqualTo("financialActivityID", savingActivityID).whereIn("transactionType", Arrays.asList("Deposit", "Withdrawal")).get().addOnSuccessListener { results ->
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()
                if (transactionObject.paymentType == "Cash") {
                    if (transactionObject?.transactionType == "Deposit")
                        cashBalance += transactionObject?.amount!!
                    else if (transactionObject.transactionType == "Withdrawal")
                        cashBalance -= transactionObject?.amount!!
                }

                else if (transactionObject.paymentType == "Maya") {
                    if (transactionObject?.transactionType == "Deposit")
                        mayaBalance += transactionObject?.amount!!
                    else if (transactionObject.transactionType == "Withdrawal")
                        mayaBalance -= transactionObject?.amount!!
                }
            }
            adjustCashBalance(cashBalance)
            adjustMayaBalance(mayaBalance)
            var finact = Intent(this, FinancialActivities::class.java)
            this.startActivity(finact)
        }
    }

    private fun adjustCashBalance(cashBalance:Float) {
        //withdraw money from savings to wallet
        var withdrawal = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Withdrawal",
            "transactionName" to "$goalName Withdrawal",
            "amount" to cashBalance,
            "category" to "Goal",
            "financialActivityID" to savingActivityID,
            "date" to Timestamp.now(),
            "paymentType" to "Cash"
        )
        firestore.collection("Transactions").add(withdrawal)

        firestore.collection("ChildWallet").whereEqualTo("childID", currentUser).whereEqualTo("type", "Cash").get().addOnSuccessListener { result ->
            var walletID = result.documents[0].id
            firestore.collection("ChildWallet").document(walletID).update("currentBalance", FieldValue.increment(cashBalance.toDouble()))
        }
    }

    private fun adjustMayaBalance(mayaBalance:Float) {
        //withdraw money from savings to wallet
        var withdrawal = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Withdrawal",
            "transactionName" to "$goalName Withdrawal",
            "amount" to mayaBalance,
            "category" to "Goal",
            "financialActivityID" to savingActivityID,
            "date" to Timestamp.now(),
            "paymentType" to "Maya"
        )
        firestore.collection("Transactions").add(withdrawal)

        firestore.collection("ChildWallet").whereEqualTo("childID", currentUser).whereEqualTo("type", "Maya").get().addOnSuccessListener { result ->
            var walletID = result.documents[0].id
            firestore.collection("ChildWallet").document(walletID).update("currentBalance", FieldValue.increment(mayaBalance.toDouble()))
        }
    }
}