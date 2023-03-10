package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
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
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityGoalConfirmationBinding
import ph.edu.dlsu.finwise.databinding.DialogSmartGoalInfoBinding
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentGoalActivity
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

    private lateinit var goalLength:String

    private lateinit var currentUserType:String

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    //will be changed depending on who set the goal or what age the kid is
    private var goalStatus: String = "In Progress"

    private lateinit var childID:String
    private lateinit var createdBy:String

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
        binding.tvAmount.text = "??? " + DecimalFormat("#,##0.00").format(bundle.getFloat("amount"))
        if (bundle.getBoolean("goalIsForSelf") == true)
            binding.tvIsForChild.text = "Yes"
        else
            binding.tvIsForChild.text = "No"

        getCurrentUserType()


        var targetDate = bundle.getSerializable("targetDate")
        var formattedDate = SimpleDateFormat("MM/dd/yyyy").format(targetDate)

        binding.tvTargetDate.text = formattedDate

        binding.btnConfirm.setOnClickListener{

            computeDateDifference(formattedDate)

            var goal = hashMapOf(
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

                    if (currentUserType == "Child") {

                        if (goalStatus == "For Review") {
                            var waitGoalReview = Intent(this, WaitGoalReview::class.java)
                            var sendBundle = Bundle()
                            sendBundle.putString("financialGoalID", it.id)
                            waitGoalReview.putExtras(sendBundle)
                            startActivity(waitGoalReview)
                        } else if (goalStatus == "In Progress") {
                            var goToStartGoal = Intent(context, StartGoalActivity::class.java)
                            var bundle1: Bundle = intent.extras!!
                            bundle1.putString("financialGoalID", it.id)
                            goToStartGoal.putExtras(bundle1)
                            goToStartGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(goToStartGoal)
                            finish()
                        }
                    } else {
                        var parentGoal = Intent(this, ParentGoalActivity::class.java)
                        var bundle = Bundle()
                        bundle.putString("childID", childID)
                        parentGoal.putExtras(bundle)
                        startActivity(parentGoal)
                        finish()
                    }

                }.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to add goal", Toast.LENGTH_SHORT).show()
            }
        }

//        binding.btnBack.setOnClickListener {
//            var goToNewGoal = Intent(context, FinancialActivity::class.java)
//            goToNewGoal.putExtras(bundle)
//            goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            context.startActivity(goToNewGoal)
//        }

        binding.btnSMARTGoal.setOnClickListener{
            showGoalDialog()
        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToNewGoal = Intent(applicationContext, NewGoal::class.java)
            goToNewGoal.putExtras(bundle)
            goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(goToNewGoal)
        }
    }

    private fun createBudgetTemplates(activity:String, financialGoalID:String) {
        if (activity == "Buying Items") {
            var food = BudgetItem("Food & Drinks", null, financialGoalID, 0.00F, "Active")
            var toys = BudgetItem("Toys & Games",null, financialGoalID, 0.00F,  "Active")
            var gift = BudgetItem("Gift",null, financialGoalID, 0.00F,  "Active")
            firestore.collection("BudgetItems").add(food)
            firestore.collection("BudgetItems").add(toys)
            firestore.collection("BudgetItems").add(gift)
        }

        if (activity == "Planning An Event") {
            var food = BudgetItem("Food & Drinks",null, financialGoalID, 0.00F, "Active")
            var decoration = BudgetItem("Decorations", null,financialGoalID, 0.00F,  "Active")
            var gift = BudgetItem("Gift", null,financialGoalID, 0.00F,  "Active")
            var rental = BudgetItem("Rental", null,financialGoalID, 0.00F,  "Active")
            var transportation = BudgetItem("Transportation", null,financialGoalID, 0.00F,  "Active")
            var partyFavors = BudgetItem("Party Favors", null,financialGoalID, 0.00F,  "Active")
            firestore.collection("BudgetItems").add(food)
            firestore.collection("BudgetItems").add(decoration)
            firestore.collection("BudgetItems").add(gift)
            firestore.collection("BudgetItems").add(rental)
            firestore.collection("BudgetItems").add(transportation)
            firestore.collection("BudgetItems").add(partyFavors)
        }

        if (activity == "Situational Shopping") {
            var food = BudgetItem("Food & Drinks", null, financialGoalID, 0.00F, "Active")
            var clothes = BudgetItem("Clothing",null, financialGoalID, 0.00F,  "Active")
            firestore.collection("BudgetItems").add(food)
            firestore.collection("BudgetItems").add(clothes)
        }
    }

    private fun saveFinancialActivities(financialActivity:String, goalID:String) {
        lateinit var savingActivity: FinancialActivities
        if (goalStatus == "For Review")
             savingActivity = FinancialActivities (goalID, childID, "Saving", "Locked")
        else
            savingActivity = FinancialActivities (goalID, childID, "Saving", "In Progress")
        firestore.collection("FinancialActivities").add(savingActivity)
        if (financialActivity == "Buying Items" ||financialActivity == "Planning An Event" || financialActivity == "Situational Shopping") {
            var budgetingActivity = FinancialActivities(goalID, childID, "Budgeting", "Locked")
            var spendingActivity = FinancialActivities(goalID, childID, "Spending", "Locked")
            firestore.collection("FinancialActivities").add(budgetingActivity)
            firestore.collection("FinancialActivities").add(spendingActivity)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAge() {
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            var child = it.toObject<Users>()
            //compute age
            val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var age = difference.years
            if (age == 10 || age == 11)
                goalStatus = "For Review"
        }
    }


    private fun getCurrentUserType() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            if (it.toObject<Users>()!!.userType == "Parent") {
                currentUserType = "Parent"
                childID = intent.extras!!.getString("childID").toString()
                createdBy  = FirebaseAuth.getInstance().currentUser!!.uid
            }
            else if (it.toObject<Users>()!!.userType == "Child") {
                currentUserType = "Child"
                childID = FirebaseAuth.getInstance().currentUser!!.uid
                createdBy  = FirebaseAuth.getInstance().currentUser!!.uid
                checkAge()
            }
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
        else if (difference.days > 14 && difference.months < 1)
            goalLength = "Medium"
        else
            goalLength = "Long"
    }

    private fun showGoalDialog() {

        var dialogBinding= DialogSmartGoalInfoBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}