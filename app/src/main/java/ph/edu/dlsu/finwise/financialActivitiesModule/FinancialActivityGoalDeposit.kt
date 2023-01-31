package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.personalFinancialManagementModule.ConfirmDepositActivity
import ph.edu.dlsu.finwise.databinding.ActivityFinancialGoalDepositBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals

class FinancialActivityGoalDeposit : AppCompatActivity() {

    private lateinit var binding : ActivityFinancialGoalDepositBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialGoalDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        var dataBundle: Bundle = intent.extras!!
        var financialGoalID = dataBundle.getString("financialGoalID").toString()
        var decisionActivityID = dataBundle.getString("decisionMakingActivityID").toString()

        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var financialGoal = it.toObject<FinancialGoals>()
            binding.tvGoalName.text = financialGoal?.goalName
            binding.tvProgressAmount.text = "₱ " + dataBundle.getFloat("currentAmount") + " / ₱ " + dataBundle.getFloat("targetAmount")
            binding.pbProgress.progress = dataBundle.getInt("progress")
        }


        binding.btnNext.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("goalName", binding.tvGoalName.text.toString())
            bundle.putString("financialGoalID",financialGoalID)
            bundle.putString("decisionMakingActivityID", decisionActivityID)
            bundle.putFloat("amount", binding.etAmount.text.toString().toFloat())
            bundle.putString("source", "DirectGoalDeposit")
            bundle.putString("date", (binding.etTransactionDate.month + 1).toString() + "/" +
                    (binding.etTransactionDate.dayOfMonth).toString() + "/" + (binding.etTransactionDate.year).toString())

            var goToDepositConfirmation = Intent(context, FinancialActivityConfirmDeposit::class.java)
            goToDepositConfirmation.putExtras(bundle)
            goToDepositConfirmation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(goToDepositConfirmation)

        }

    }
}