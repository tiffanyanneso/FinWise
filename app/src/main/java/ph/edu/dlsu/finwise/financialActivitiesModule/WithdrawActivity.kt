package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivitySpendingBinding
import ph.edu.dlsu.finwise.databinding.ActivityWithdrawBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.SimpleDateFormat

class WithdrawActivity : AppCompatActivity() {

    private lateinit var binding:ActivityWithdrawBinding
    private var firestore = Firebase.firestore

    private lateinit var financialGoalID:String
    private lateinit var decisionMakingActivityID:String

    private lateinit var bundle:Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bundle = intent.extras!!
        setData()

        binding.btnNext.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("goalName", binding.tvGoalName.text.toString())
            bundle.putString("financialGoalID",financialGoalID)
            bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
            bundle.putFloat("amount", binding.etAmount.text.toString().toFloat())
            bundle.putSerializable("date", SimpleDateFormat("MM-dd-yyyy").parse((binding.etTransactionDate.month+1).toString() + "-" +
                    binding.etTransactionDate.dayOfMonth.toString() + "-" + binding.etTransactionDate.year))



            var confirmWithdraw = Intent (this, ConfirmWithdraw::class.java)
            confirmWithdraw.putExtras(bundle)
            confirmWithdraw.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(confirmWithdraw)
        }
    }

    private fun setData() {
        financialGoalID = bundle.getString("financialGoalID").toString()
        decisionMakingActivityID = bundle.getString("decisionMakingActivityID").toString()

        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var financialGoal = it.toObject<FinancialGoals>()
            binding.tvGoalName.text = financialGoal?.goalName
            binding.tvProgressAmount.text = "₱ " + bundle.getFloat("currentAmount") + " / ₱ " + bundle.getFloat("targetAmount")
            binding.pbProgress.progress = bundle.getInt("progress")
        }
    }
}