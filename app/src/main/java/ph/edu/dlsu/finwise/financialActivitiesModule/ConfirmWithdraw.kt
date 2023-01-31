package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityConfirmWithdrawBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class ConfirmWithdraw : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmWithdrawBinding
    private var firestore = Firebase.firestore

    private lateinit var bundle:Bundle

    private lateinit var financialGoalID:String
    private lateinit var decisionMakingActivityID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmWithdrawBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bundle = intent.extras!!

        setData()

        binding.btnConfirm.setOnClickListener {
            var withdrawal = hashMapOf(
                "childID" to "",
                "transactionType" to "Withdrawal",
                 "transactionName" to "",
                 "amount" to bundle.getFloat("amount"),
                 "category" to "",
                 "financialGoalID" to bundle.getString("financialGoalID"),
                 "decisionMakingActivityID" to bundle.getString("decisionMakingActivityID"),
                 "date" to bundle.getSerializable("date")
            )
            firestore.collection("Transactions").add(withdrawal).addOnSuccessListener {
                //TODO: ADJUST USER BALANCE (INCREASE WALLET BALANCE)
                var bundle = Bundle()
                bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
                bundle.putString("financialGoalID", financialGoalID)
                var saving = Intent (this, SavingActivity::class.java)
                saving.putExtras(bundle)
                saving.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(saving)
                finish()
            }
        }
    }

    private fun setData() {
        financialGoalID = bundle.getString("financialGoalID").toString()
        decisionMakingActivityID = bundle.getString("decisionMakingActivityID").toString()
        binding.tvGoal.text = bundle.getString("goalName")
        binding.tvAmount.text = "₱ " + DecimalFormat("#,###.00").format(bundle.getFloat("amount")).toString()
        binding.tvDate.text = SimpleDateFormat("MM-dd-yyyy").format(bundle.getSerializable("date") as Date).toString()
    }
}