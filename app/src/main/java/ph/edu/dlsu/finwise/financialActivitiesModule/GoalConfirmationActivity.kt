package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import android.os.Build
import  androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.DateTime
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityGoalConfirmationBinding
import ph.edu.dlsu.finwise.model.DecisionMakingActivities
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class GoalConfirmationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGoalConfirmationBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
        binding = ActivityGoalConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        var bundle: Bundle = intent.extras!!
        binding.tvGoalName.text = bundle.getString("goalName")
        binding.tvActivity.text = bundle.getString("activity")
        binding.tvAmount.text = bundle.getFloat("amount").toString()
        var targetDate = bundle.getSerializable("targetDate")
        binding.tvTargetDate.text = targetDate.toString()

        var decisionActivities = bundle.getStringArrayList("decisionActivities")
        var decisionActivitiesString = ""
        if (decisionActivities!=null) {
            for (i in decisionActivities.indices)
                //TODO: how to fix extra space
                decisionActivitiesString += decisionActivities[i].toString() + "\n"

            binding.textDecisionActivities.text = decisionActivitiesString
        }



        binding.btnConfirm.setOnClickListener{
            var goal = hashMapOf(
                //TODO: add childID, createdBy
                "childID" to "",
                "goalName" to bundle.getString("goalName"),
                "dateCreated" to Timestamp.now(),
                "createdBy" to "",
                "targetDate" to targetDate,
                "targetAmount" to bundle.getFloat("amount"),
                "currentAmount" to 0,
                "financialActivity" to bundle.getString("activity"),
                "lastUpdated" to Timestamp.now(),
                "status" to "In Progress",
                //"decisionMakingActivities" to decisionActivitiesObjectArrayList
            )

            //add goal to DB
            firestore.collection("FinancialGoals").add(goal).addOnSuccessListener {

                //add decision making activities in inner collection of FinancialGoals
                var decisionActivities = bundle.getStringArrayList("decisionActivities")
                if (decisionActivities != null) {
                    var priority = 1
                    for (i in decisionActivities.indices) {
                        var decisionMakingActivity = DecisionMakingActivities()
                        decisionMakingActivity.financialGoalID = it.id
                        decisionMakingActivity.targetAmount = bundle.getFloat("amount")
                        decisionMakingActivity.priority = priority
                        if (priority == 1)
                            decisionMakingActivity.status = "In Progress"
                        else
                            decisionMakingActivity.status = "Not Yet Started"

                        priority++

                        if (decisionActivities[i].contentEquals("Setting a Budget"))
                            decisionMakingActivity.decisionMakingActivity = "Setting a Budget"
                        if (decisionActivities[i].contentEquals("Deciding to Save"))
                            decisionMakingActivity.decisionMakingActivity = "Deciding to Save"
                        if (decisionActivities[i].contentEquals("Deciding to Spend"))
                            decisionMakingActivity.decisionMakingActivity = "Deciding to Spend"

                        firestore.collection("DecisionMakingActivities").add(decisionMakingActivity)
                            .addOnSuccessListener {
                            }
                    }
                    var goToStartGoal = Intent(context, StartGoalActivity::class.java)
                    var bundle1: Bundle = intent.extras!!
                    bundle1.putString("goalID", it.id)
                    goToStartGoal.putExtras(bundle1)
                    goToStartGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(goToStartGoal)
                }
            }.addOnFailureListener { e ->
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