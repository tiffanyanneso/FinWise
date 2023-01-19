package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import  androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityGoalConfirmationBinding
import java.text.SimpleDateFormat
import java.util.*

class GoalConfirmationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGoalConfirmationBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this

        var bundle: Bundle = intent.extras!!
        binding.tvGoalName.text = bundle.getString("goalName")
        binding.tvActivity.text = bundle.getString("activity")
        binding.tvAmount.text = bundle.getFloat("amount").toString()
        binding.tvTargetDate.text = bundle.getString("targetDate")


        binding.btnConfirm.setOnClickListener {
            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat("MM/dd/yyyy")
            val current = formatter.format(time)

            var goal = hashMapOf(
                //TODO: add childID, createdBy
                "childID" to "",
                "goalName" to bundle.getString("goalName"),
                "dateCreated" to current ,
                "createdBy" to "",
                "targetDate" to bundle.getString("targetDate"),
                "targetAmount" to bundle.getFloat("amount"),
                "currentAmount" to 0,
                "financialActivity" to bundle.getString("activity"),
                "lastUpdated" to current,
                "status" to "In Progress"
            )
            firestore.collection("FinancialGoals").add(goal).addOnSuccessListener {
                Toast.makeText(this, "Goal added", Toast.LENGTH_SHORT).show()
                var goToStartGoal = Intent(context, StartGoalActivity::class.java)
                var bundle1: Bundle = intent.extras!!
                bundle1.putString("goalID", it.id)
                goToStartGoal.putExtras(bundle1)
                goToStartGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(goToStartGoal)

            }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to add goal", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnBack.setOnClickListener {
            var goToNewGoal = Intent(context, FinancialActivity::class.java)
            goToNewGoal.putExtras(bundle)
            goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(goToNewGoal)
        }

    }
}