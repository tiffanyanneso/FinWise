package ph.edu.dlsu.finwise.childDashboardModule

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.childDashboardModule.fragments.DashboardFinancialActivitiesFragment
import ph.edu.dlsu.finwise.childDashboardModule.fragments.DashboardFinancialAssessmentsFragment
import ph.edu.dlsu.finwise.childDashboardModule.fragments.DashboardPersonalFinanceFragment
import ph.edu.dlsu.finwise.databinding.ActivityChildDashboardBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.*
import ph.edu.dlsu.finwise.model.*
import java.lang.Math.abs
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

class ChildDashboardActivity : AppCompatActivity(){

    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var userType: String

    private lateinit var binding: ActivityChildDashboardBinding
    private var bundle = Bundle()

    private var age = 0
    private var personalFinancePerformance = 0.00F

    private var goalSettingPercentage = 0.00F
    private var savingPercentage = 0.00F
    private var budgetingPercentage = 0.00F
        private var nParent = 0
        private var purchasedBudgetItemCount = 0.00F
        private var totalBudgetAccuracy = 0.00F
        //number of budget items, including those from ongoing budgeting activities
        private var budgetItemCount = 0.00F
    private var spendingPercentage = 0.00F
        //number of budget items of completed budgting activities
        private var nBudgetItems = 0.00F
        private var overSpending = 0.00F
        private var overspendingPercentage = 0.00F
        private var nPlanned = 0.00F
        private var nTotalPurchased = 0.00F

    private var financialActivitiesPerformance = 0.00F

    private var financialAssessmentPerformance = 0.00F

    private var nGoalSettingCompleted = 0
    private var nSavingCompleted = 0
    private var nBudgetingCompleted = 0
    private var nSpendingCompleted = 0
    //if there are no assessments taken yet, do not include in fin health computation
    private var nAssessmentsTaken = 0

    private var overallFinancialHealth = 0.00F

    private val tabIcons = intArrayOf(
        R.drawable.personalfinance,
        R.drawable.goal,
        R.drawable.baseline_assessment_24
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChildDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_dashboard)
        getPerformance()
        initializeFragments()
        initializeDateButtons()
    }

    private fun getPerformance() {
        //get the age of the child
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            val child = it.toObject<Users>()
            //compute age
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date = SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            val difference = Period.between(to, from)

            age = difference.years
            Log.d("kandor", "age: "+age)
            getPersonalFinancePerformance()
        }
    }

    private fun initializeDateButtons() {
        //initializeWeeklyButton()
        initializeMonthlyButton()
        initializeQuarterlyButton()
    }

    /*private fun initializeWeeklyButton() {
        val weeklyButton = binding.btnWeekly
        val monthlyButton = binding.btnMonthly
        val yearlyButton = binding.btnQuarterly
        weeklyButton.setOnClickListener {
            weeklyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green))
            monthlyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            yearlyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            bundle.putString("date", "weekly")
            initializeFragments()
            //setUpBreakdownTabs()
        }
    }*/

    private fun initializeMonthlyButton() {
        //val weeklyButton = binding.btnWeekly
        val monthlyButton = binding.btnMonthly
        val yearlyButton = binding.btnQuarterly
        monthlyButton.setOnClickListener {
            //weeklyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            monthlyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green))
            yearlyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            bundle.putString("date", "monthly")
            initializeFragments()
            //setUpBreakdownTabs()
        }
    }

    private fun initializeQuarterlyButton() {
        //val weeklyButton = binding.btnWeekly
        val monthlyButton = binding.btnMonthly
        val quarterlyButton = binding.btnQuarterly
        quarterlyButton.setOnClickListener {
            //weeklyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            monthlyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            quarterlyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green))
            bundle.putString("date", "quarterly")
            initializeFragments()
            //setUpBreakdownTabs()
        }
    }

    private fun initializeFragments() {
        setBundle()
        val adapter = FinancialActivity.ViewPagerAdapter(supportFragmentManager)
        val dashboardPersonalFinanceFragment = DashboardPersonalFinanceFragment()
        val dashboardFinancialActivitiesFragment = DashboardFinancialActivitiesFragment()
        val dashboardFinancialAssessmentsFragment = DashboardFinancialAssessmentsFragment()
        dashboardPersonalFinanceFragment.arguments = bundle
        dashboardFinancialActivitiesFragment.arguments = bundle
        dashboardFinancialAssessmentsFragment.arguments = bundle
        Log.d("xcvxzcv", "initializeFragments: "+ bundle)
        adapter.addFragment(dashboardPersonalFinanceFragment,"Personal Finance")
        adapter.addFragment(dashboardFinancialActivitiesFragment,"Financial Activities")
        adapter.addFragment(dashboardFinancialAssessmentsFragment,"Financial Assessments")

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        setupTabIcons()
    }

    private fun setBundle() {
        // sending of friendchildID
        bundle.putString("childID", currentUser)
        //TODO: change
        userType = "child"
        bundle.putString("user", userType)
    }


    private fun setupTabIcons() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
        binding.tabLayout.getTabAt(2)?.setIcon(tabIcons[2])
    }

    private fun getPersonalFinancePerformance() {
        var income = 0.00F
        var expense = 0.00F
        firestore.collection("Transactions").whereEqualTo("userID", currentUser).whereIn("transactionType", listOf("Income", "Expense")).get().addOnSuccessListener { results ->
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()
                if (transactionObject.transactionType == "Income")
                    income += transactionObject.amount!!
                else if (transactionObject.transactionType == "Expense")
                    expense += transactionObject.amount!!}
        }.continueWith {
            var personalFinancePerformancePercent = income/expense * 100
            if (personalFinancePerformancePercent > 200)
                personalFinancePerformancePercent = 200F
            personalFinancePerformance = personalFinancePerformancePercent / 2
            /*binding.progressBarPersonalFinance.progress = personalFinancePerformance.toInt()
            binding.tvPersonalFinancePercent.text = DecimalFormat("##0.00").format(personalFinancePerformance) + "%"*/
            if (age == 10 || age == 11)
                getGoalSettingPerformance()
            else
                getSavingPerformanceScore()
        }
    }

    private fun getGoalSettingPerformance() {
        var overallRating = 0.00F
        var nRatings = 0
        firestore.collection("GoalRating").whereEqualTo("childID", currentUser).get().addOnSuccessListener { results ->
            nGoalSettingCompleted = results.size()
            nRatings = results.size()
            for (rating in results) {
                val ratingObject = rating.toObject<GoalRating>()
                overallRating += ratingObject.overallRating!!
            }
            if (nRatings != 0)
                goalSettingPercentage = ((overallRating / nRatings)/5)* 100

            getSavingPerformanceScore()
        }
    }

    private fun getSavingPerformanceScore() {
        var nGoals = 0.00F
        var nOnTime =0.00F
        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
           nSavingCompleted = results.size()
            if (results.size() != 0) {
                for (goal in results) {
                    nGoals++
                    val goalObject = goal.toObject<FinancialGoals>()
                    if (goalObject.dateCompleted != null) {
                        val targetDate = goalObject?.targetDate!!.toDate()
                        val completedDate = goalObject?.dateCompleted!!.toDate()

                        //goal was completed before the target date, meaning it was completed on time
                        if (completedDate.before(targetDate) || completedDate.equals(targetDate))
                            nOnTime++
                    }
                }
                savingPercentage = (nOnTime/nGoals)*100
            }
        }.continueWith {
            getBudgetingPerformanceScore()
        }
    }

    @SuppressLint("NewApi")
    private fun getBudgetingPerformanceScore() {
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
            nBudgetingCompleted = results.size()
            if (results.size()!=0) {
                for (activity in results) {
                    firestore.collection("BudgetItems").whereEqualTo("financialActivityID", activity.id).whereEqualTo("status", "Active").get().addOnSuccessListener { budgetItems ->
                        for (budgetItem in budgetItems) {
                            budgetItemCount++
                            val budgetItemObject = budgetItem.toObject<BudgetItem>()
                            //parental involvement
                            firestore.collection("Users").document(budgetItemObject.createdBy.toString()).get().addOnSuccessListener { user ->
                                //parent is the one who added the budget item
                                if (user.toObject<Users>()!!.userType == "Parent")
                                    nParent++
                            }.continueWith {
                                getBudgetAccuracy(activity.id, budgetItem.id, budgetItemObject)
                            }
                        }
                    }
                }
            } else
                //no budgeting completed yet, so naturally no spending also, proceed to computing of financial assessment score
                getFinancialAssessmentScore()
        }.continueWith {
            getSpendingPerformance()
        }
    }

    private fun getBudgetAccuracy(budgetingActivityID:String, budgetItemID:String, budgetItemObject:BudgetItem) {
        firestore.collection("FinancialActivities").document(budgetingActivityID).get().addOnSuccessListener {
            firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", it.toObject<FinancialActivities>()!!.financialGoalID!!).whereEqualTo("financialActivityName", "Spending").get().addOnSuccessListener { spending ->
                val spendingActivity = spending.documents[0].toObject<FinancialActivities>()
                if (spendingActivity?.status == "Completed") {
                    //budget accuracy
                    purchasedBudgetItemCount++
                    firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID).get().addOnSuccessListener { transactions ->
                        var spent = 0.00F
                        for (transaction in transactions)
                            spent += transaction.toObject<Transactions>()!!.amount!!
                        totalBudgetAccuracy += (100 - (abs(budgetItemObject.amount!! - spent) / budgetItemObject.amount!!) * 100)
                    }.continueWith {
                        if (purchasedBudgetItemCount != 0.00F)
                            budgetingPercentage = ((totalBudgetAccuracy/purchasedBudgetItemCount) + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2
                        else
                            budgetingPercentage = ((1 - (nParent.toFloat()/budgetItemCount)) * 100)
                    }
                } else {
                    if (purchasedBudgetItemCount != 0.00F)
                        budgetingPercentage = ((totalBudgetAccuracy/purchasedBudgetItemCount) + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2
                    else
                        budgetingPercentage = ((1 - (nParent.toFloat()/budgetItemCount)) * 100)
                }
                //println("print budgeting " + budgetingPercentage )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getSpendingPerformance() {
        //get budgeting items to see if they overspent for a specific budget item
        //get completed spending activities
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
            if (results.size() != 0) {
                nSpendingCompleted = results.size()
                for (spending in results) {
                    val spendingActivity = spending.toObject<FinancialActivities>()
                    println("print " + spendingActivity.financialGoalID)
                    firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", spendingActivity.financialGoalID).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Completed").get().addOnSuccessListener { budgeting ->
                        val budgetingID = budgeting.documents[0].id
                        firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetingID).whereEqualTo("status", "Active").get().addOnSuccessListener { results ->
                            nBudgetItems += results.size()
                            for (budgetItem in results) {

                                val budgetItemObject = budgetItem.toObject<BudgetItem>()
                                checkOverSpending(budgetItem.id, budgetItemObject.amount!!)
                            }
                        }
                    }
                }
            } else
                getFinancialAssessmentScore()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkOverSpending(budgetItemID:String, budgetItemAmount:Float){
        firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID).whereEqualTo("transactionType", "Expense").get().addOnSuccessListener { spendingTransactions ->
            var amountSpent = 0.00F
            for (expense in spendingTransactions) {
                val expenseObject = expense.toObject<Transactions>()
                amountSpent+= expenseObject.amount!!
            }
            //they spent more than their allocated budget
            if (amountSpent > budgetItemAmount)
                overSpending++

        }.continueWith {

            overspendingPercentage = (overSpending/nBudgetItems)
            if (age > 9 ) {
                purchasePlanning()
            }
            else {
                spendingPercentage = (1-overspendingPercentage)*100
//                println("print goal  setting " + goalSettingPercentage)
//                println("print savings " +  savingPercentage)
//                println("print budgeting " +  budgetingPercentage)
//                println("print spending " + spendingPercentage)
                financialActivitiesPerformance = ((savingPercentage + budgetingPercentage + spendingPercentage) / 3)
                //binding.tvFinactPercent.text = DecimalFormat("##0.00").format(financialActivitiesPerformance) + "%"
                println("print finact performance " + financialActivitiesPerformance)
                getFinancialAssessmentScore()
            }
        }
    }

    private fun purchasePlanning() {
        //items planned / all the items they bought * 100
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "Completed").get().addOnSuccessListener { allSpendingActivities ->
            for (spendingActivityID in allSpendingActivities) {
                firestore.collection("ShoppingListItems").whereEqualTo("spendingActivityID", spendingActivityID.id).get().addOnSuccessListener { shoppingListItems ->
                    for (shoppingListItem in shoppingListItems) {
                        val shoppingListItemObject = shoppingListItem.toObject<ShoppingListItem>()
                        if (shoppingListItemObject.status == "Purchased")
                            nPlanned++
                    }
                }.continueWith {
                    firestore.collection("Transactions").whereEqualTo("financialActivityID", spendingActivityID.id).whereEqualTo("transactionType", "Expense").get().addOnSuccessListener { expenseTransactions ->
                        nTotalPurchased += expenseTransactions.size().toFloat()
                    }.continueWith {
                        spendingPercentage = ((1-overspendingPercentage)*100 + ((nPlanned/nTotalPurchased)*100)) /2

                        println("print goal setting " + goalSettingPercentage)
                        println("print saving " + savingPercentage)
                        println("print budgeting " + budgetingPercentage)
                        println("print spending " + spendingPercentage)
                        if (age == 9 || age == 12)
                            financialActivitiesPerformance = ((savingPercentage + budgetingPercentage + spendingPercentage) / 3)
                        else if (age == 10 || age == 11)
                            financialActivitiesPerformance = ((goalSettingPercentage + savingPercentage + budgetingPercentage + spendingPercentage) / 4)

                        /*binding.progressBarFinancialActivities.progress = financialActivitiesPerformance.toInt()
                        binding.tvFinactPercent.text = DecimalFormat("##0.00").format(financialActivitiesPerformance) + "%"*/
                        println("print finact performance " + financialActivitiesPerformance)
                        getFinancialAssessmentScore()
                    }
                }
            }
        }
    }

    private fun getFinancialAssessmentScore() {
        var nCorrect = 0
        var nQuestions = 0
        firestore.collection("AssessmentAttempts").whereEqualTo("childID", currentUser).get().addOnSuccessListener { results ->
            if (results.size()!=0) {
                for (attempt in results) {
                    val assessmentAttempt = attempt.toObject<FinancialAssessmentAttempts>()
                    if (assessmentAttempt.nAnsweredCorrectly != null && assessmentAttempt.nQuestions != null) {
                        nCorrect += assessmentAttempt.nAnsweredCorrectly!!
                        nQuestions += assessmentAttempt.nQuestions!!
                    }
                }
                println("print correct " + nCorrect)
                println("print nquestions"  + nQuestions)
                financialAssessmentPerformance = (nCorrect.toFloat() / nQuestions.toFloat()) * 100
                /*binding.progressBarFinancialAssessments.progress = financialAssessmentPerformance.toInt()
                binding.tvFinancialAssessmentPercentage.text = DecimalFormat("##0.00").format(financialAssessmentPerformance) + "%"*/
                getOverallFinancialHealth()
            } else
                getOverallFinancialHealth()
        }
    }

    private fun getOverallFinancialHealth(){
//        println("print in overall financial health")
//        println("print personal finance " + personalFinancePerformance)
//        println("print goal setting " + goalSettingPercentage)
//        println("print saving " + savingPercentage)
//        println("print nsaving " + nSavingCompleted)
//        println("print budgeting " + budgetingPercentage)
//        println("print nBudgeting " + nBudgetingCompleted)
//        println("print spending " + spendingPercentage)
//        println("print nspending " + nSpendingCompleted)
//        println("print assessment " + financialAssessmentPerformance)


        //no goal setting performance for age 9 and 12 (age 9 - parent makes the goal, age 12 - child's goals doesn't need to be reviewed)
        if (age == 9 || age == 12) {
            //no saving completed yet, score will be personal finance and financial assessment (prelim)
            overallFinancialHealth = if(nSavingCompleted == 0)
                (((personalFinancePerformance * .5) + financialAssessmentPerformance*.5)*100).toFloat()

            //no budgeting completed yet, score will be personal finance, saving performance,  financial assessment (prelim)
            else if (nBudgetingCompleted == 0)
                ((personalFinancePerformance * .35) + (savingPercentage  * .35) + (financialAssessmentPerformance * .30)).toFloat()

            //no spending completed yet, score will be personal finance, saving performance, budgeting performance,  financial assessment (prelim)
            else if (nSpendingCompleted == 0)
                ((personalFinancePerformance * .35) + (((savingPercentage + budgetingPercentage) / 2) * .35) + (financialAssessmentPerformance * .30)).toFloat()
            else
                ((personalFinancePerformance * .35) + (((savingPercentage + budgetingPercentage + spendingPercentage) / 3) * .35) + (financialAssessmentPerformance * .30)).toFloat()

        } else if (age == 10 || age == 11) {
            //no goal setting completed yet, score will be personal finance, and  financial assessment (prelim)
            if (nGoalSettingCompleted == 0)
                overallFinancialHealth = (((personalFinancePerformance * .5) + financialAssessmentPerformance*.5)*100).toFloat()

            //no saving completed yet, score will be personal finance, goal setting, and financial assessment (prelim)
            else if(nSavingCompleted == 0)
                overallFinancialHealth = ((personalFinancePerformance * .35) + (goalSettingPercentage  * .35) + (financialAssessmentPerformance * .30)).toFloat()

            //no budgeting completed yet, score will be personal finance, saving performance,  financial assessment (prelim)
            else if (nBudgetingCompleted == 0)
                overallFinancialHealth = ((personalFinancePerformance * .35) + (((goalSettingPercentage + savingPercentage)/2)  * .35) + (financialAssessmentPerformance * .30)).toFloat()

            //no spending completed yet, score will be personal finance, saving performance, budgeting performance,  financial assessment (prelim)
            else if (nSpendingCompleted == 0)
                overallFinancialHealth = ((personalFinancePerformance * .35) + (((goalSettingPercentage + savingPercentage + budgetingPercentage) / 3) * .35) + (financialAssessmentPerformance * .30)).toFloat()
            
            else
            overallFinancialHealth = ((personalFinancePerformance * .35) + (((goalSettingPercentage + savingPercentage + budgetingPercentage + spendingPercentage) / 4) * .35) + (financialAssessmentPerformance * .30)).toFloat()
        }
        Log.d("kandor", "personalFinancePerformance: "+personalFinancePerformance)
        Log.d("kandor", "financialAssessmentPerformance: "+financialAssessmentPerformance)
        Log.d("kandor", "overallFinancialHealth: "+overallFinancialHealth)
        binding.tvPerformancePercentage.text =  DecimalFormat("##0.00").format(overallFinancialHealth) + "%"
        //TODO: 1. grade the child's overall score (percentage)
    //          2. give description
    //          3. Give action

    }

    class ViewPagerAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm){

        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()
        override fun getCount() = mFragmentList.size
        override fun getItem(position: Int) = mFragmentList[position]
        override fun getPageTitle(position: Int) = mFragmentTitleList[position]

        fun addFragment(fragment: Fragment, title:String){
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }
    }


}