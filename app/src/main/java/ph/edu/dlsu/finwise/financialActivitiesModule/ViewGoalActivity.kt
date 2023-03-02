package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningActivity
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
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

    private lateinit var financialGoalID:String
    private lateinit var source:String
    private lateinit var childID:String

    //the money the child currently has, this value is affected by spending (expense) transactions
    private var currentBalance = 0.00F
    //the money that the child has already saved for the goal, this value will only be changed by manual withdrawals
    //e.g. the child withdrew money from their goal
    //but not affected by withdrawals for expenses
    private var savedAmount = 0.00F
    private var targetAmount = 0.00F

    private lateinit var goalTransactionsAdapter:GoalTransactionsAdapater
    var transactionsArrayList = ArrayList<String>()

    private lateinit var savingActivityID:String
    private lateinit var budgetingActivityID:String
    private lateinit var spendingActivityID:String

    private var savings = 0.00F


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


        childID = bundle.getString("childID").toString()
        checkUser()
        setFinancialActivityID()

        var sendBundle = Bundle()
        sendBundle.putString("financialGoalID", financialGoalID)
        sendBundle.putString("source", "viewGoal")

        binding.btnEditGoal.setOnClickListener {
            var goToEditGoal = Intent(this, ChildEditGoal::class.java)
            goToEditGoal.putExtras(sendBundle)
            this.startActivity(goToEditGoal)
        }

        binding.tvViewAll.setOnClickListener {
            var goToGoalTransactions = Intent(this, GoalTransactionsActivity::class.java)
            sendBundle.putString("savingActivityID", savingActivityID)
            goToGoalTransactions.putExtras(sendBundle)
            this.startActivity(goToGoalTransactions)
        }

        binding.btnViewTransactions.setOnClickListener {
            var goToGoalTransactions = Intent(this, GoalTransactionsActivity::class.java)
            sendBundle.putString("savingActivityID", savingActivityID)
            goToGoalTransactions.putExtras(sendBundle)
            this.startActivity(goToGoalTransactions)
        }

        binding.btnWithdraw.setOnClickListener {
//            bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
//            bundle.putInt("progress", progress.toInt())
//            bundle.putFloat("currentAmount", currentAmount)
//            bundle.putFloat("targetAmount", targetAmount)
            var goalWithdraw = Intent(this, WithdrawActivity::class.java)
            sendBundle.putFloat("savedAmount", savedAmount)
            sendBundle.putString("savingActivityID", savingActivityID)
            sendBundle.putInt("progress", binding.progressBar.progress)
            goalWithdraw.putExtras(sendBundle)
            this.startActivity(goalWithdraw)
        }


        binding.btnChores.setOnClickListener {
            var goToChores = Intent(this, EarningActivity::class.java)
            sendBundle.putString("savingActivityID", savingActivityID)
            sendBundle.putString("childID", childID)
            goToChores.putExtras(sendBundle)
            startActivity(goToChores)
        }

        binding.tvViewAllEarning.setOnClickListener {
            var goToChores = Intent(this, EarningActivity::class.java)
            this.startActivity(goToChores)
        }


        binding.btnDeposit.setOnClickListener {
//            bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
//            bundle.putInt("progress", progress.toInt())
//            bundle.putFloat("currentAmount", currentAmount)
//            bundle.putFloat("targetAmount", targetAmount)
            var goalDeposit = Intent(this, FinancialActivityGoalDeposit::class.java)
            sendBundle.putFloat("savedAmount", savedAmount)
            sendBundle.putString("savingActivityID", savingActivityID)
            sendBundle.putInt("progress", binding.progressBar.progress)
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
                sendBundle.putString("spendingActivityID", spendingActivityID)
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

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToFinancialActivities = Intent(applicationContext, FinancialActivity::class.java)

            this.startActivity(goToFinancialActivities)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setFinancialActivityID() {
        firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", financialGoalID).get().addOnSuccessListener { results ->
            for (finActivity in results) {
                var activityObject = finActivity.toObject<FinancialActivities>()
                if (activityObject.financialActivityName == "Saving") {
                    savingActivityID = finActivity.id
                    if (activityObject.status == "Completed")
                        binding.btnEditGoal.visibility = View.GONE
                }
                if (activityObject.financialActivityName == "Budgeting")
                    budgetingActivityID = finActivity.id
                if (activityObject.financialActivityName == "Spending")
                    spendingActivityID = finActivity.id
            }
        }.continueWith {
            getTransactions() }
    }

    private class TransactionFilter(var transactionID:String?=null, var date: Date?=null)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTransactions() {
        var transactionFilterArrayList = ArrayList<TransactionFilter>()
        //TODO: INCLUDE IN QUERY SPENDING ACTIVITY ID
        firestore.collection("Transactions").whereEqualTo("financialActivityID", savingActivityID).whereIn("transactionType", Arrays.asList("Deposit", "Withdrawal")).get().addOnSuccessListener { results ->
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()
                transactionFilterArrayList.add(TransactionFilter(transaction.id,transactionObject.date!!.toDate()))

                if (transactionObject.transactionType == "Deposit")
                    currentBalance += transactionObject.amount!!

                else if (transactionObject.transactionType == "Withdrawal")
                    currentBalance -= transactionObject.amount!!

            }

            transactionFilterArrayList.sortByDescending { it.date }
            for (transaction in transactionFilterArrayList)
                transactionsArrayList.add(transaction.transactionID.toString())
        }.continueWith {
            binding.tvCurrentBalance.text = "Available balance: ₱ " + DecimalFormat("#,##0.00").format(currentBalance)
            setFields() }
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
                    savedAmount = goal?.currentSavings!!.toFloat()
                    targetAmount = goal?.targetAmount!!.toFloat()

                    var formatSaved = DecimalFormat("#,##0.00").format(goal?.currentSavings)
                    var formatTarget = DecimalFormat("#,##0.00").format(goal?.targetAmount)
                    binding.tvGoalProgress.text = "₱$formatSaved / " + "₱ $formatTarget"
                    var progress = (goal?.currentSavings!! / goal?.targetAmount!! * 100).toInt()
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
            }
                //deductExpenses() }
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
//                binding.layoutBtnDepositWithdraw.visibility = View.GONE
                binding.btnWithdraw.isEnabled = false
                binding.btnWithdraw.isClickable = false
                binding.btnWithdraw.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.light_grey)))

                binding.btnDeposit.isEnabled = false
                binding.btnDeposit.isClickable = false
                binding.btnDeposit.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.light_grey)))
            }
            else {
                binding.btnWithdraw.isEnabled = true
                binding.btnWithdraw.isClickable = true
                binding.btnWithdraw.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.light_green)))

                binding.btnDeposit.isEnabled = true
                binding.btnDeposit.isClickable = true
                binding.btnDeposit.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.light_green)))
            }
        }
    }
}