package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityGoalConfirmationBinding
import ph.edu.dlsu.finwise.model.DecisionMakingActivities
import ph.edu.dlsu.finwise.model.GoalSettings
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*


class GoalConfirmationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGoalConfirmationBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context

    private var goalStatus=""

    private lateinit var goalLength:String

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
        binding.tvAmount.text = DecimalFormat("#,##0.00").format(bundle.getFloat("amount"))
        var targetDate = bundle.getSerializable("targetDate")


        var formattedDate = SimpleDateFormat("MM/dd/yyyy").format(targetDate)
        binding.tvTargetDate.text = formattedDate

        binding.tvIsForChild.text = bundle.getBoolean("goalIsForSelf").toString()

        var decisionActivities = bundle.getStringArrayList("decisionActivities")
        var decisionActivitiesString = ""
        if (decisionActivities!=null) {
            for (i in decisionActivities.indices)
                //TODO: how to fix extra space
                decisionActivitiesString += decisionActivities[i].toString() + "\n"

            binding.textDecisionActivities.text = decisionActivitiesString
        }


        binding.btnConfirm.setOnClickListener{
            //check settings of current user
            /*var currentChildUser = FirebaseAuth.getInstance().currentUser!!.uid
            firestore.collection("GoalSettings").whereEqualTo("childID", currentChildUser).get().addOnSuccessListener {
                var goalSettings = it.documents[0].toObject<GoalSettings>()
                //goal has to be reviewed by parent
                if (goalSettings?.autoApproved == false)
                    goalStatus = "For Review"
                else if (goalSettings?.autoApproved == true)*/
                    goalStatus = "In Progress"

                computeDateDifference(formattedDate)

                var goal = hashMapOf(
                    //TODO: add childID, createdBy
                    "childID" to "",
                    "goalName" to bundle.getString("goalName"),
                    "dateCreated" to Timestamp.now(),
                    "createdBy" to "",
                    "targetDate" to targetDate,
                    "goalLength" to goalLength,
                    "targetAmount" to bundle.getFloat("amount"),
                    "currentAmount" to 0,
                    "financialActivity" to bundle.getString("activity"),
                    "lastUpdated" to Timestamp.now(),
                    "status" to goalStatus,
                    "goalIsForSelf" to bundle.getBoolean("goalIsForSelf")
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
                        finish()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to add goal", Toast.LENGTH_SHORT).show()
                }
            }
        //}

        binding.btnBack.setOnClickListener {
            var goToNewGoal = Intent(context, FinancialActivity::class.java)
            goToNewGoal.putExtras(bundle)
            goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(goToNewGoal)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun computeDateDifference(targetDate:String) {
        val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")

        val from = LocalDate.now()
        val to = LocalDate.parse(targetDate, dateFormatter)

        var difference = Period.between(from, to)
        println("difference day  " + difference.days)
        println("difference month  " + difference.months)

        if (difference.days <= 14 && difference.months < 1 )
            goalLength = "Short"
        else if (difference.months < 1)
            goalLength = "Medium"
        else
            goalLength = "Long"
    }
}