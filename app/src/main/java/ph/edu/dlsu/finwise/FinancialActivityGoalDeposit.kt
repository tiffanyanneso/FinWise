package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityChildNewGoalBinding
import ph.edu.dlsu.finwise.databinding.ActivityFinancialGoalDepositBinding

class FinancialActivityGoalDeposit : AppCompatActivity() {

    private lateinit var binding : ActivityFinancialGoalDepositBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialGoalDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        binding.btnNext.setOnClickListener {
            var goalName = binding.tvGoalName.text.toString()
            var amount = binding.etAmount.text.toString().toFloat()

            var bundle = Bundle()
            bundle.putString("goalName", goalName)
            bundle.putFloat("amount", amount)

            var goToDepositConfirmation = Intent(context, PFMConfirmDepositActivity::class.java)
            goToDepositConfirmation.putExtras(bundle)
            goToDepositConfirmation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(goToDepositConfirmation)

        }

    }
}