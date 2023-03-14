package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityWaitGoalReviewBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat

class WaitGoalReview : AppCompatActivity() {

    private lateinit var binding :ActivityWaitGoalReviewBinding

    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaitGoalReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        var financialGoalID = bundle.getString("financialGoalID").toString()
        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var goal = it.toObject<FinancialGoals>()
            binding.tvGoalName.text = goal?.goalName
            binding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(goal?.targetAmount)
            binding.tvActivity.text = goal?.financialActivity
        }

        binding.btnActivities.setOnClickListener{
            var finact = Intent(this, FinancialActivity::class.java)
            startActivity(finact)
        }
    }
}