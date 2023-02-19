package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.app.appsearch.AppSearchSchema.BooleanPropertyConfig
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.BudgetCategoryAdapter
import ph.edu.dlsu.finwise.adapter.GoalDecisionMakingActivitiesAdapter
import ph.edu.dlsu.finwise.adapter.GoalTransactionsAdapater
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalBinding
import ph.edu.dlsu.finwise.databinding.DialogFinishSavingBinding
import ph.edu.dlsu.finwise.model.BudgetExpense
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ViewGoalActivity : AppCompatActivity() {

    private lateinit var binding : ActivityViewGoalBinding
    private var firestore = Firebase.firestore

    private lateinit var decisionMakingActivitiesAdapater: GoalDecisionMakingActivitiesAdapter

    private lateinit var financialGoalID:String

    private var savedAmount = 0.00F
    private var targetAmount = 0.00F

    private lateinit var goalTransactionsAdapter:GoalTransactionsAdapater
    var transactionsArrayList = ArrayList<String>()

    private lateinit var savingActivityID:String
    private lateinit var budgetingActivityID:String
    private lateinit var spendingActivityID:String

    private var balance = 0.00F


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
        financialGoalID = bundle.getString("financialGoalID").toString()
        println("print "  + financialGoalID)

        //checkUser()
        setFinancialActivityID()


        var sendBundle = Bundle()
        sendBundle.putString("financialGoalID", financialGoalID)

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
            if (savedAmount>=targetAmount) {
                var budgeting  = Intent (this, BudgetActivity::class.java)
                sendBundle.putString("budgetActivityID", budgetingActivityID)
                sendBundle.putString("savingActivityID", savingActivityID)
                budgeting.putExtras(sendBundle)
                this.startActivity(budgeting)
            } else {
                var dialogBinding= DialogFinishSavingBinding.inflate(getLayoutInflater())
                var dialog= Dialog(this);
                dialog.setContentView(dialogBinding.getRoot())
                dialog.window!!.setLayout(900, 800)

                dialog.show()
                dialogBinding.btnOk.setOnClickListener{
                    dialog.dismiss()
                }
            }

        }
    }

    private fun setFinancialActivityID() {
        firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", financialGoalID).get().addOnSuccessListener { results ->
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

    private class TransactionFilter(var transactionID:String?=null, var date: Date?=null)

    private fun getSavedAmount() {
        var transactionFilterArrayList = ArrayList<TransactionFilter>()
        firestore.collection("Transactions").whereEqualTo("financialActivityID", savingActivityID).get().addOnSuccessListener { results ->
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()
                transactionFilterArrayList.add(TransactionFilter(transaction.id,transactionObject.date!!.toDate()))
                if (transactionObject.transactionType == "Deposit")
                    savedAmount += transactionObject.amount!!
                else if (transactionObject.transactionType == "Withdrawal")
                    savedAmount-= transactionObject.amount!!
            }

            transactionFilterArrayList.sortByDescending { it.date }
            for (transaction in transactionFilterArrayList)
                transactionsArrayList.add(transaction.transactionID.toString())
        }.continueWith {setFields() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setFields() {
        if (financialGoalID != null) {
            firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener { document ->
                if (document != null) {
                    //TODO: compute remaining days
                    var goal = document.toObject(FinancialGoals::class.java)
                    binding.tvGoalName.text = goal?.goalName.toString()
                    binding.tvActivityName.text = goal?.financialActivity.toString()
                    targetAmount = goal?.targetAmount!!.toFloat()

                    var formatSaved = DecimalFormat("#,##0.00").format(savedAmount)
                    var formatTarget = DecimalFormat("#,##0.00").format(goal?.targetAmount)
                    binding.tvGoalProgress.text = "₱$formatSaved / " + "₱ $formatTarget"
                    var progress = (savedAmount/ goal?.targetAmount!! * 100).toInt()
                    if (progress< 100)
                        binding.progressBar.progress = progress
                    else
                        binding.progressBar.progress = 100

                    goalTransactionsAdapter = GoalTransactionsAdapater(this, transactionsArrayList)
                    binding.rvSavingsDeposit.adapter = goalTransactionsAdapter
                    binding.rvSavingsDeposit.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

                    //compute remaining days
                    val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
                    val from = LocalDate.now()
                    val date =  SimpleDateFormat("MM/dd/yyyy").format(goal.targetDate?.toDate())
                    val to = LocalDate.parse(date.toString(), dateFormatter)
                    var difference = Period.between(from, to)
                    binding.tvRemaining.text = difference.days.toString() + " days remaining"
                }
            }.continueWith {
                deductExpenses() }
        }
    }

    private fun deductExpenses() {
        var balance = savedAmount
        firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetingActivityID).get().addOnSuccessListener { budgetItems ->
            if (budgetItems.size() == 0)
                binding.tvCurrentBalance.text = "₱ " +  DecimalFormat("#,##0.00").format(balance)
            else {
                for (item in budgetItems) {
                    firestore.collection("BudgetExpenses").whereEqualTo("budgetCategoryID", item.id).get().addOnSuccessListener { budgetExpenses ->
                        for (expense in budgetExpenses) {
                            var expenseObject = expense.toObject<BudgetExpense>()
                            balance -= expenseObject.amount!!
                        }
                    }.continueWith { binding.tvCurrentBalance.text = "Available balance: ₱ " +  DecimalFormat("#,###.00").format(balance)   }
                }
            }
        }
    }

    private fun checkUser() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("ParentUser").document(currentUser).get().addOnSuccessListener {
            //current user is parent
            if (it.exists()) {
                binding.layoutBtnDepositWithdraw.visibility = View.GONE
            }
        }
    }
}