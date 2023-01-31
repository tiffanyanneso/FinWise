package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityGoalAccomplishedBinding
import ph.edu.dlsu.finwise.databinding.ActivitySpendingBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.SimpleDateFormat

class GoalAccomplishedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalAccomplishedBinding
    private var firestore = Firebase.firestore

    private lateinit var  financialGoalID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalAccomplishedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var bundle: Bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()

        setText()

        binding.btnFinish.setOnClickListener {
            var goalList = Intent(this, FinancialActivity::class.java)
            this.startActivity(goalList)
        }
    }

    private fun setText () {
        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var financialGoal = it.toObject<FinancialGoals>()
            binding.tvGoal.text = financialGoal?.goalName
            binding.tvActivity.text = financialGoal?.financialActivity
            binding.tvAmount.text = "₱ " + financialGoal?.targetAmount.toString()

            // convert timestamp to date string
            val formatter = SimpleDateFormat("MM/dd/yyyy")
            val date = formatter.format(financialGoal?.targetDate?.toDate())
            binding.tvTargetDate.text = date.toString()
        }
    }
}