package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityGoalConfirmationBinding
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*


class GoalConfirmationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGoalConfirmationBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context

    private var goalStatus=""

    private lateinit var goalLength:String

    private lateinit var currentUserType:String

    private var currentUser = "eWZNOIb9qEf8kVNdvdRzKt4AYrA2"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
        binding = ActivityGoalConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this
        //getCurrentUserType()

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        var bundle: Bundle = intent.extras!!
        binding.tvGoalName.text = bundle.getString("goalName")
        binding.tvActivity.text = bundle.getString("activity")
        binding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(bundle.getFloat("amount"))
        if (bundle.getBoolean("goalIsForSelf") == true)
            binding.tvIsForChild.text = "Yes"
        else
            binding.tvIsForChild.text = "No"


        var targetDate = bundle.getSerializable("targetDate")
        var formattedDate = SimpleDateFormat("MM/dd/yyyy").format(targetDate)

        binding.tvTargetDate.text = formattedDate


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

                 var childID:String = " "
                 var createdBy:String =" "
//                if (currentUserType == "Parent") {
//                    var childIDBundle = intent.extras!!
//                    childID = childIDBundle.getString("childID").toString()
//                    createdBy = FirebaseAuth.getInstance().currentUser!!.uid
//                } else if (currentUserType == "Child"){
//                    childID = FirebaseAuth.getInstance().currentUser!!.uid
//                    createdBy = FirebaseAuth.getInstance().currentUser!!.uid
//                }

                var goal = hashMapOf(
                    //TODO: add childID, createdBy
                    "childID" to childID,
                    "goalName" to bundle.getString("goalName"),
                    "dateCreated" to Timestamp.now(),
                    "createdBy" to createdBy,
                    "targetDate" to bundle.getSerializable("targetDate"),
                    "goalLength" to goalLength,
                    "targetAmount" to bundle.getFloat("amount"),
                    "currentSavings" to 0,
                    "financialActivity" to bundle.getString("activity"),
                    "lastUpdated" to Timestamp.now(),
                    "status" to goalStatus,
                    "goalIsForSelf" to bundle.getBoolean("goalIsForSelf")
                )

                //add goal to DB
                firestore.collection("FinancialGoals").add(goal).addOnSuccessListener {
                    createBudgetTemplates(bundle.getString("activity").toString(), it.id)
                    saveFinancialActivities(bundle.getString("activity").toString(), it.id)

                        //if (currentUserType == "Child") {
                            var goToStartGoal = Intent(context, StartGoalActivity::class.java)
                            var bundle1: Bundle = intent.extras!!
                            bundle1.putString("financialGoalID", it.id)
                            goToStartGoal.putExtras(bundle1)
                            goToStartGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(goToStartGoal)
                            finish()
//                        } else {
//                            var parentGoal = Intent(this, ParentGoalActivity::class.java)
//                            var bundle = Bundle()
//                            bundle.putString("childID", childID)
//                            parentGoal.putExtras(bundle)
//                            startActivity(parentGoal)
//                            finish()
//                        }

                    }.addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to add goal", Toast.LENGTH_SHORT).show()
                }
            }
        //}

//        binding.btnBack.setOnClickListener {
//            var goToNewGoal = Intent(context, FinancialActivity::class.java)
//            goToNewGoal.putExtras(bundle)
//            goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            context.startActivity(goToNewGoal)
//        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToNewGoal = Intent(applicationContext, ChildNewGoal::class.java)
            goToNewGoal.putExtras(bundle)
            goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(goToNewGoal)
        }
    }

    private fun createBudgetTemplates(activity:String, financialGoalID:String) {
        if (activity == "Buying Items") {
            var food = BudgetItem("Food", financialGoalID, 0.00F, 0, "Active")
            var toys = BudgetItem("Toys", financialGoalID, 0.00F, 0, "Active")
            var games = BudgetItem("Games", financialGoalID, 0.00F, 0, "Active")
            var gift = BudgetItem("Gift", financialGoalID, 0.00F, 0, "Active")
            firestore.collection("BudgetItems").add(food)
            firestore.collection("BudgetItems").add(toys)
            firestore.collection("BudgetItems").add(games)
            firestore.collection("BudgetItems").add(gift)
        }

        if (activity == "Planning An Event") {
            var food = BudgetItem("Food", financialGoalID, 0.00F, 0, "Active")
            var decoration = BudgetItem("Decoration", financialGoalID, 0.00F, 0, "Active")
            var gift = BudgetItem("Gift", financialGoalID, 0.00F, 0, "Active")
            firestore.collection("BudgetItems").add(food)
            firestore.collection("BudgetItems").add(decoration)
            firestore.collection("BudgetItems").add(gift)}
    }

    private fun saveFinancialActivities(financialActivity:String, goalID:String) {
        var savingActivity = FinancialActivities (goalID, currentUser, "Saving", "In Progress")
        firestore.collection("FinancialActivities").add(savingActivity)
        if (financialActivity == "Buying Items" ||financialActivity == "Planning An Event" || financialActivity == "Situational Shopping") {
            var budgetingActivity = FinancialActivities(goalID, currentUser, "Budgeting", "Locked")
            var spendingActivity = FinancialActivities(goalID, currentUser, "Spending", "Locked")
            firestore.collection("FinancialActivities").add(budgetingActivity)
            firestore.collection("FinancialActivities").add(spendingActivity)
        }
    }

    private fun getCurrentUserType() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("ParentUser").document(currentUser).get().addOnSuccessListener {
            if (it.exists())
                currentUserType = "Parent"
            else
                currentUserType ="Child"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun computeDateDifference(targetDate:String) {
        val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")

        val from = LocalDate.now()
        val to = LocalDate.parse(targetDate, dateFormatter)

        var difference = Period.between(from, to)

        if (difference.days <= 14 && difference.months < 1 )
            goalLength = "Short"
        else if (difference.months < 1)
            goalLength = "Medium"
        else
            goalLength = "Long"
    }
}