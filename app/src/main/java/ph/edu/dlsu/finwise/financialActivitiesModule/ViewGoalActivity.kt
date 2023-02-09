package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.GoalDecisionMakingActivitiesAdapter
import ph.edu.dlsu.finwise.adapter.GoalTransactionsAdapater
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class ViewGoalActivity : AppCompatActivity() {

    private lateinit var binding : ActivityViewGoalBinding
    private var firestore = Firebase.firestore

    private lateinit var decisionMakingActivitiesAdapater: GoalDecisionMakingActivitiesAdapter

    private lateinit var goalID:String

    private var savedAmount = 0.00F

    private lateinit var goalTransactionsAdapter:GoalTransactionsAdapater
    var transactionsArrayList = ArrayList<Transactions>()

    private lateinit var savingActivityID:String
    private lateinit var budgetingActivityID:String
    private lateinit var spendingActivityID:String


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        var bundle: Bundle = intent.extras!!
        goalID = bundle.getString("goalID").toString()

        setFinancialActivityID()


        var sendBundle = Bundle()
        sendBundle.putString("goalID", goalID)

        binding.btnEditGoal.setOnClickListener {
            var goToEditGoal = Intent(this, ChildEditGoal::class.java)
            goToEditGoal.putExtras(sendBundle)
            this.startActivity(goToEditGoal)
        }

        binding.btnWithdraw.setOnClickListener {
//            bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
//            bundle.putInt("progress", progress.toInt())
//            bundle.putFloat("currentAmount", currentAmount)
//            bundle.putFloat("targetAmount", targetAmount)
            var goalWithdraw = Intent(this, WithdrawActivity::class.java)
            sendBundle.putString("savingActivityID", savingActivityID)
            goalWithdraw.putExtras(sendBundle)
            this.startActivity(goalWithdraw)
        }

        binding.btnDeposit.setOnClickListener {
//            bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
//            bundle.putInt("progress", progress.toInt())
//            bundle.putFloat("currentAmount", currentAmount)
//            bundle.putFloat("targetAmount", targetAmount)
            var goalDeposit = Intent(this, FinancialActivityGoalDeposit::class.java)
            sendBundle.putString("savingActivityID", savingActivityID)
            goalDeposit.putExtras(sendBundle)
            this.startActivity(goalDeposit)
        }

        binding.layoutGoalDetails.setOnClickListener {
            var goalDetails = Intent(this, ViewGoalDetails::class.java)
            goalDetails.putExtras(sendBundle)
            this.startActivity(goalDetails)
        }

        binding.layoutActivityName.setOnClickListener {
            var budgeting  = Intent (this, BudgetActivity::class.java)
            sendBundle.putString("budgetActivityID", budgetingActivityID)
            sendBundle.putString("savingActivityID", savingActivityID)
            budgeting.putExtras(sendBundle)
            this.startActivity(budgeting)
        }
    }

    private fun setFinancialActivityID() {
        firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", goalID).get().addOnSuccessListener { results ->
            for (finActivity in results) {
                var activityObject = finActivity.toObject<FinancialActivities>()
                if (activityObject.financialActivityName == "Saving")
                    savingActivityID = finActivity.id
                if (activityObject.financialActivityName == "Budgeting")
                    budgetingActivityID = finActivity.id
                if (activityObject.financialActivityName == "Spending")
                    spendingActivityID = finActivity.id
            }
        }.continueWith { getSavedAmount() }
    }

    private fun getSavedAmount() {
        firestore.collection("Transactions").whereEqualTo("financialActivityID", savingActivityID).get().addOnSuccessListener { results ->
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()
                transactionsArrayList.add(transactionObject)
                if (transactionObject.transactionType == "Deposit")
                    savedAmount += transactionObject.amount!!
                else if (transactionObject.transactionType == "Withdrawal")
                    savedAmount-= transactionObject.amount!!
            }
        }.continueWith {setFields() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setFields() {
        if (goalID != null) {
            firestore.collection("FinancialGoals").document(goalID!!).get().addOnSuccessListener { document ->
                if (document != null) {
                    //TODO: compute remaining days
                    var goal = document.toObject(FinancialGoals::class.java)
                    binding.tvGoalName.text = goal?.goalName.toString()
                    binding.tvActivityName.text = goal?.financialActivity.toString()

                    var formatSaved = DecimalFormat("#,###.00").format(savedAmount)
                    var formatTarget = DecimalFormat("#,###.00").format(goal?.targetAmount)
                    binding.tvGoalProgress.text = "₱$formatSaved / " + "₱ $formatTarget"
                    binding.progressBar.progress = (savedAmount/ goal?.targetAmount!! * 100).toInt()

                    goalTransactionsAdapter = GoalTransactionsAdapater(this, transactionsArrayList)
                    binding.rvSavingsDeposit.adapter = goalTransactionsAdapter
                    binding.rvSavingsDeposit.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

                    //compute remaining days
                    val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
                    val from = LocalDate.now()
                    val date =  SimpleDateFormat("MM/dd/yyyy").format(goal?.targetDate?.toDate())
                    val to = LocalDate.parse(date.toString(), dateFormatter)

                    var difference = Period.between(from, to)
                    var string  = ""
                    if (difference.years!=0)    string += "${difference.years} year"
                    if (difference.months !=0)  string += "${difference.months} month"
                    if (difference.days !=0)    string += "${difference.days} days remaining"
                    binding.tvRemaining.text = string
                }
            }
        }
    }

}