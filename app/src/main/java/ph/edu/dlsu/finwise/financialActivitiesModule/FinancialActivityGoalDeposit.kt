package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
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

        var dataBundle: Bundle = intent.extras!!
        var goalID = dataBundle.getString("goalID").toString()
        var decisionActivityID = dataBundle.getString("decisionMakingActivityID").toString()

        firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener {
            var financialGoal = it.toObject<FinancialGoals>()
            binding.tvGoalName.text = financialGoal?.goalName
        }


        binding.btnNext.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("goalName", binding.tvGoalName.text.toString())
            bundle.putString("decisionMakingActivityID", decisionActivityID)
            bundle.putFloat("amount", binding.etAmount.text.toString().toFloat())
            bundle.putString("source", "DirectGoalDeposit")

            var goToDepositConfirmation = Intent(context, FinancialActivityConfirmDeposit::class.java)
            goToDepositConfirmation.putExtras(bundle)
            goToDepositConfirmation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(goToDepositConfirmation)

        }

    }
}