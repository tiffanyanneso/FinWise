package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import  androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityGoalConfirmationBinding
import ph.edu.dlsu.finwise.model.DecisionMakingActivities
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

        var decisionActivities = bundle.getStringArrayList("decisionActivities")
        var decisionActivitiesString = ""
        if (decisionActivities!=null) {
            for (i in decisionActivities.indices)
                //TODO: how to fix extra space
                decisionActivitiesString += decisionActivities[i].toString() + "\n"
            binding.textDecisionActivities.text = decisionActivitiesString
        }



        binding.btnConfirm.setOnClickListener {
            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat("MM/dd/yyyy")
            val current = formatter.format(time)

            var decisionActivities = bundle.getStringArrayList("decisionActivities")
            var decisionActivitiesObjectArrayList = arrayListOf<DecisionMakingActivities>()
            
            if (decisionActivities!=null) {
                for (i in decisionActivities.indices) {
                    var decisionMakingActivitiesObject = DecisionMakingActivities()
                    if (decisionActivities[i].contentEquals("Setting a Budget")) {
                        decisionMakingActivitiesObject.decisonMakingActivity = "Setting a Budget"
                        decisionActivitiesObjectArrayList.add(decisionMakingActivitiesObject)
                    }
                    if (decisionActivities[i].contentEquals("Deciding to Save")) {
                        decisionMakingActivitiesObject.decisonMakingActivity = "Deciding to Save"
                        decisionActivitiesObjectArrayList.add(decisionMakingActivitiesObject)
                    }
                    if (decisionActivities[i].contentEquals("Deciding to Spend")) {
                        decisionMakingActivitiesObject.decisonMakingActivity = "Deciding to Spend"
                        decisionActivitiesObjectArrayList.add(decisionMakingActivitiesObject)
                    }
                }
            }

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
                "status" to "In Progress",
                "decisionMakingActivities" to decisionActivitiesObjectArrayList
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