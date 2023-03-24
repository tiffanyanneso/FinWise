package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningMenuActivity
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.GoalTransactionsAdapater
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalBinding
import ph.edu.dlsu.finwise.databinding.DialogActivityLockedBinding
import ph.edu.dlsu.finwise.databinding.DialogFinishSavingBinding
import ph.edu.dlsu.finwise.databinding.DialogTakeAssessmentBinding
import ph.edu.dlsu.finwise.databinding.DialogWarningCannotWtithdrawBinding
import ph.edu.dlsu.finwise.financialAssessmentModule.FinancialAssessmentActivity
import ph.edu.dlsu.finwise.model.*
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningActivity
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

    private lateinit var savingActivityStatus:String
    private lateinit var budgetingActivityStatus:String

    private var savings = 0.00F

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle: Bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        childID = bundle.getString("childID").toString()

        checkUser()
        setFinancialActivityID()
        loadButtons()
        setNavigationBar()

        var sendBundle = Bundle()
        sendBundle.putString("financialGoalID", financialGoalID)
        sendBundle.putString("childID", childID)
        sendBundle.putString("source", "viewGoal")

        binding.btnEditGoal.setOnClickListener {
            var goToEditGoal = Intent(this, EditGoal::class.java)
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

        binding.btnDeposit.setOnClickListener {
            var goalDeposit = Intent(this, SavingsDepositActivity::class.java)
            sendBundle.putFloat("savedAmount", savedAmount)
            sendBundle.putString("savingActivityID", savingActivityID)
            sendBundle.putString("budgetingActivityID", budgetingActivityID)
            sendBundle.putString("spendingActivityID", spendingActivityID)
            sendBundle.putInt("progress", binding.progressBar.progress)
            goalDeposit.putExtras(sendBundle)
            this.startActivity(goalDeposit)
        }

        binding.btnWithdraw.setOnClickListener {
            if (currentBalance == 0.00F) {
                cannotWithdrawDialog()
            } else {
                var goalWithdraw = Intent(this, SavingsWithdrawActivity::class.java)
                sendBundle.putString("savingActivityID", savingActivityID)
                sendBundle.putString("budgetingActivityID", budgetingActivityID)
                sendBundle.putString("spendingActivityID", spendingActivityID)
                goalWithdraw.putExtras(sendBundle)
                this.startActivity(goalWithdraw)
            }
        }


        binding.btnChores.setOnClickListener {
            val goToEarningMenu = Intent(this, EarningMenuActivity::class.java)
            sendBundle.putString("financialGoalID", financialGoalID)
            sendBundle.putString("module", "finact")
            sendBundle.putString("childID", childID)
            goToEarningMenu.putExtras(sendBundle)
            startActivity(goToEarningMenu)
        }


        binding.btnGoalDetails.setOnClickListener {
            var goalDetails = Intent(this, ViewGoalDetailsTabbedActivity::class.java)
            goalDetails.putExtras(sendBundle)
            this.startActivity(goalDetails)
        }

        binding.layoutActivityName.setOnClickListener {
            if (savingActivityStatus == "In Progress") {
                var dialogBinding= DialogFinishSavingBinding.inflate(getLayoutInflater())
                var dialog= Dialog(this);
                dialog.setContentView(dialogBinding.getRoot())
                dialog.window!!.setLayout(900, 800)

                dialog.show()
                dialogBinding.btnOk.setOnClickListener{
                    dialog.dismiss()
                }
            } else if (savingActivityStatus == "Completed" && budgetingActivityStatus == "Locked") {
                var dialogBinding= DialogActivityLockedBinding.inflate(getLayoutInflater())
                var dialog= Dialog(this);
                dialog.setContentView(dialogBinding.getRoot())
                dialog.window!!.setLayout(900, 900)

                dialog.show()
                dialogBinding.btnOk.setOnClickListener{
                    dialog.dismiss()
                }
            } else {
                var budgeting  = Intent (this, BudgetActivity::class.java)
                sendBundle.putString("budgetingActivityID", budgetingActivityID)
                sendBundle.putString("savingActivityID", savingActivityID)
                sendBundle.putString("spendingActivityID", spendingActivityID)
                budgeting.putExtras(sendBundle)
                this.startActivity(budgeting)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setFinancialActivityID() {
        var allComplete = true
        firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", financialGoalID).get().addOnSuccessListener { results ->
            for (finActivity in results) {
                val activityObject = finActivity.toObject<FinancialActivities>()
                if (activityObject.financialActivityName == "Saving") {
                    savingActivityStatus = activityObject.status!!
                    savingActivityID = finActivity.id
                    if (activityObject.status == "Completed") {
//                        binding.btnEditGoal.visibility = View.GONE
                        binding.btnEditGoal.isEnabled = false
                        binding.btnEditGoal.isClickable = false
                        binding.btnEditGoal.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.light_grey)))
                    } else {
                        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
                            if (it.toObject<Users>()!!.userType == "Child") {
                                binding.btnEditGoal.isEnabled = true
                                binding.btnEditGoal.isClickable = true
                                binding.btnEditGoal.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.light_green)))
                                allComplete = false
                            }
                        }
                    }
                }
                if (activityObject.financialActivityName == "Budgeting") {
                    budgetingActivityStatus = activityObject.status!!
                    budgetingActivityID = finActivity.id
                }

                if (activityObject.financialActivityName == "Spending")
                    spendingActivityID = finActivity.id

                if (activityObject.status != "Completed")
                    allComplete = false
            }
        }.continueWith {
            //if all activities are complete, no more updates are allowed
            if (allComplete || (savingActivityStatus == "Completed" && budgetingActivityStatus =="Locked")) {
                binding.layoutUpperButtons.visibility = View.GONE
                binding.layoutEdit.visibility = View.GONE
            } else {
                binding.layoutUpperButtons.visibility = View.VISIBLE
                binding.layoutEdit.visibility = View.VISIBLE
            }
            getTransactions()
        }
    }

    private class TransactionFilter(var transactionID:String?=null, var date: Date?=null)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTransactions() {
        var cashBalance = 0.00F
        var mayaBalance = 0.00F
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

                if (transactionObject.paymentType == "Cash") {
                    if (transactionObject?.transactionType == "Deposit")
                        cashBalance += transactionObject?.amount!!
                    else if (transactionObject.transactionType == "Withdrawal")
                        cashBalance -= transactionObject?.amount!!
                }

                else if (transactionObject.paymentType == "Maya") {
                    if (transactionObject?.transactionType == "Deposit")
                        mayaBalance += transactionObject?.amount!!
                    else if (transactionObject.transactionType == "Withdrawal")
                        mayaBalance -= transactionObject?.amount!!
                }

            }

            transactionFilterArrayList.sortByDescending { it.date }
            for (transaction in transactionFilterArrayList)
                transactionsArrayList.add(transaction.transactionID.toString())
        }.continueWith {
            binding.tvCurrentBalance.text = "Available balance: ₱ " + DecimalFormat("#,##0.00").format(currentBalance)
            binding.tvCashSavings.text = "₱ " + DecimalFormat("#,##0.00").format(cashBalance)
            binding.tvMayaSavings.text = "₱ " + DecimalFormat("#,##0.00").format(mayaBalance)
            setFields() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setFields() {
        if (financialGoalID != null) {
            firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener { document ->
                if (document != null) {
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

                    if (goal?.status != "Completed") {
                        var difference = Period.between(from, to)
                        var differenceDays = ((difference.years * 365) + (difference.months * 30) + difference.days)
                        binding.tvRemaining.text = differenceDays.toString() + " days remaining"
                    } else
                        binding.tvRemaining.visibility = View.GONE
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun goToFinancialAssessmentActivity() {
        firestore.collection("Assessments").whereEqualTo("assessmentType", "Pre-Activity").whereEqualTo("assessmentCategory", "Saving").get().addOnSuccessListener {
            if (it.size()!= 0) {
                var assessmentID = it.documents[0].id
                firestore.collection("AssessmentAttempts").whereEqualTo("assessmentID", assessmentID).whereEqualTo("childID", currentUser).get().addOnSuccessListener { results ->
                    if (results.size() != 0) {
                        var assessmentAttemptsObjects = results.toObjects<FinancialAssessmentAttempts>()
                        assessmentAttemptsObjects.sortedByDescending { it.dateTaken }
                        var latestAssessmentAttempt = assessmentAttemptsObjects.get(0).dateTaken
                        val dateFormatter: DateTimeFormatter =
                            DateTimeFormatter.ofPattern("MM/dd/yyyy")
                        val lastTakenFormat =
                            SimpleDateFormat("MM/dd/yyyy").format(latestAssessmentAttempt!!.toDate())
                        val from = LocalDate.parse(lastTakenFormat.toString(), dateFormatter)
                        val today = SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate())
                        val to = LocalDate.parse(today.toString(), dateFormatter)
                        var difference = Period.between(from, to)

                        if (difference.days >= 7)
                            buildAssessmentDialog()
                    } else
                        buildAssessmentDialog()
                }
            }
        }
    }

    private fun buildAssessmentDialog() {
        var dialogBinding= DialogTakeAssessmentBinding.inflate(layoutInflater)
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.root)
        dialog.window!!.setLayout(950, 900)
        dialog.setCancelable(false)
        dialogBinding.btnOk.setOnClickListener { goToAssessment() }
        dialog.show()
    }

    private fun goToAssessment() {
        val bundle = Bundle()
        val assessmentType = "Pre-Activity" // Change: Pre-Activity, Post-Activity
        val assessmentCategory = "Saving" // Change: Budgeting, Saving, Spending, Goal Setting
        bundle.putString("assessmentType", assessmentType)
        bundle.putString("assessmentCategory", assessmentCategory)

        val assessmentQuiz = Intent(this, FinancialAssessmentActivity::class.java)
        assessmentQuiz.putExtras(bundle)
        startActivity(assessmentQuiz)
    }

    private fun loadButtons(){
        loadBackButton()
    }

    private fun setNavigationBar() {

        var navUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(navUser).get().addOnSuccessListener {

        val bottomNavigationViewChild = binding.bottomNav
        val bottomNavigationViewParent = binding.bottomNavParent

        if (it.toObject<Users>()!!.userType == "Parent") {
            bottomNavigationViewChild.visibility = View.GONE
            bottomNavigationViewParent.visibility = View.VISIBLE
            //sends the ChildID to the parent navbar
            val bundle = intent.extras!!
            val childID = bundle.getString("childID").toString()
            val bundleNavBar = Bundle()
            bundleNavBar.putString("childID", childID)
            NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)
        } else if (it.toObject<Users>()!!.userType == "Child") {
            bottomNavigationViewChild.visibility = View.VISIBLE
            bottomNavigationViewParent.visibility = View.GONE
            Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
        }
    }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun cannotWithdrawDialog() {
        var dialogBinding = DialogWarningCannotWtithdrawBinding.inflate(getLayoutInflater())
        var dialog = Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())
        dialog.window!!.setLayout(900, 800)

        dialogBinding.btnOk.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun checkUser() {
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            //current user is parent
            if (it.toObject<Users>()!!.userType == "Parent") {
                binding.btnWithdraw.isEnabled = false
                binding.btnWithdraw.isClickable = false
                binding.btnWithdraw.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_grey))

                binding.btnDeposit.isEnabled = false
                binding.btnDeposit.isClickable = false
                binding.btnDeposit.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_grey))

                binding.btnEditGoal.isEnabled = false
                binding.btnEditGoal.isClickable = false
                binding.btnEditGoal.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_grey))
            }
            else if (it.toObject<Users>()!!.userType == "Child"){
                goToFinancialAssessmentActivity()

                binding.btnWithdraw.isEnabled = true
                binding.btnWithdraw.isClickable = true
                binding.btnWithdraw.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_green))

                binding.btnDeposit.isEnabled = true
                binding.btnDeposit.isClickable = true
                binding.btnDeposit.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_green))

                binding.btnEditGoal.isEnabled = true
                binding.btnEditGoal.isClickable = true
                binding.btnEditGoal.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_green))
            }
        }
    }
}