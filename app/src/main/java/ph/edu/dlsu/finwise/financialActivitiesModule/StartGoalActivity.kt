package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityStartGoalBinding
import ph.edu.dlsu.finwise.model.FinancialGoals

class StartGoalActivity : AppCompatActivity() {

    private lateinit var binding : ActivityStartGoalBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this

        var bundle: Bundle = intent.extras!!
        var goalID = bundle.getString("goalID")
        if (goalID != null) {
            firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener { document ->
                if (document != null) {
                    var goal = document.toObject(FinancialGoals::class.java)
                    binding.tvGoalName.text = goal?.goalName.toString()
                    binding.tvActivity.text = goal?.financialActivity.toString()
                    binding.tvAmount.text = goal?.targetAmount.toString()
                    binding.tvTargetDate.text = goal?.targetDate.toString()
                } else {
                    Toast.makeText(this, "No data", Toast.LENGTH_SHORT)
                }
            }
        }


        computeDays(bundle.getString("targetDate")!!)

        binding.btnGetStarted.setOnClickListener {
            var goToViewGoal = Intent(context, ViewGoalActivity::class.java)
            goToViewGoal.putExtra("goalID", goalID)
            context.startActivity(goToViewGoal)
        }
    }

    private fun computeDays(date:String) {
        //TODO: difference of current date and target date
    }
}