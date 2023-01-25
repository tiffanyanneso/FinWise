package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivitySavingBinding
import ph.edu.dlsu.finwise.model.DecisionMakingActivities

class SavingActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySavingBinding
    private var firestore = Firebase.firestore
    private lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        var bundle: Bundle = intent.extras!!
        var decisionMakingActivityID = bundle.getString("decisionActivityID").toString()
        var goalID = bundle.getString("goalID")

        firestore.collection("DecisionMakingActivities").document(decisionMakingActivityID).get().addOnSuccessListener {
            var decisionMakingActvity = it.toObject<DecisionMakingActivities>()
            var currentAmount:Float = 0.0F
            //TODO: CHANGE HOW TO COMPUTE FOR GOAL PROGRESS (USE DATA FROM TRANSACTIONS COLLECTION
            if (decisionMakingActvity?.currentAmount != null)
                currentAmount = decisionMakingActvity.currentAmount!!
            binding.tvGoalAmount.text = currentAmount.toString() + " / " + decisionMakingActvity!!.targetAmount.toString()
        }

        binding.btnDeposit.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("goalID", goalID)
            bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
            var savingActivity = Intent(this, FinancialActivityGoalDeposit::class.java)
            savingActivity.putExtras(bundle)
            context.startActivity(savingActivity)
        }
    }
}