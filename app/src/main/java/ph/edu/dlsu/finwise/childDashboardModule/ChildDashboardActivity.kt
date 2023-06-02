package ph.edu.dlsu.finwise.childDashboardModule

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.childDashboardModule.fragments.DashboardFinancialActivitiesFragment
import ph.edu.dlsu.finwise.childDashboardModule.fragments.DashboardFinancialAssessmentsFragment
import ph.edu.dlsu.finwise.childDashboardModule.fragments.DashboardPersonalFinanceFragment
import ph.edu.dlsu.finwise.databinding.ActivityChildDashboardBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.*
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

class ChildDashboardActivity : AppCompatActivity(){

    private var firestore = Firebase.firestore
    private var childID = FirebaseAuth.getInstance().currentUser!!.uid
    private var userType = "child"

    private var mediaPlayer: MediaPlayer? = null

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
        bundle.putString("childID", childID)
        bundle.putString("user", userType)
    }


    private fun setupTabIcons() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
        binding.tabLayout.getTabAt(2)?.setIcon(tabIcons[2])
    }

    private fun getPerformance() {
        //get the age of the child
        firestore.collection("Users").document(childID).get().addOnSuccessListener {
            val child = it.toObject<Users>()
            //compute age
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date = SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            val difference = Period.between(to, from)

            age = difference.years
            getPersonalFinancePerformance()
        }
    }



    private fun getPersonalFinancePerformance() {
        var income = 0.00F
        var expense = 0.00F
        firestore.collection("Transactions").whereEqualTo("userID", childID).whereIn("transactionType", listOf("Income", "Expense")).get().addOnSuccessListener { results ->
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
            getFinancialActivitiesPerformance()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getFinancialActivitiesPerformance() {
        CoroutineScope(Dispatchers.Main).launch {
            if (age == 10 || age == 11)
                getGoalSettingPerformance()

            getSavingPerformanceScore()
            getBudgetingPerformanceScore()
            getSpendingPerformance()
            getFinancialActivitiesScores()
        }
    }

    private fun getFinancialActivitiesScores() {
        if (age > 9 ) {
            CoroutineScope(Dispatchers.Main).launch {
                purchasePlanningPerformance()
            }
        }
        else spendingPercentage = (1-overspendingPercentage)*100

        calculateFinancialActivitiesScore()

    }

    private suspend fun getSpendingPerformance() {
        //get budgeting items to see if they overspent for a specific budget item
        //get completed spending activities
        val financialActivitiesDocuments = firestore.collection("FinancialActivities")
            .whereEqualTo("childID", childID)
            .whereEqualTo("financialActivityName", "Spending")
            .whereEqualTo("status", "Completed").get().await()
        if (financialActivitiesDocuments.size() != 0) {
            nSpendingCompleted = financialActivitiesDocuments.size()
            for (spending in financialActivitiesDocuments) {
                val spendingActivity = spending.toObject<FinancialActivities>()
                println("print " + spendingActivity.financialGoalID)
                val financialActivityDocuments = firestore.collection("FinancialActivities")
                    .whereEqualTo("financialGoalID", spendingActivity.financialGoalID)
                    .whereEqualTo("financialActivityName", "Budgeting")
                    .whereEqualTo("status", "Completed").get().await()
                val budgetingID = financialActivityDocuments.documents[0].id
                val budgetItemsDocuments = firestore.collection("BudgetItems")
                    .whereEqualTo("financialActivityID", budgetingID)
                    .whereEqualTo("status", "Active").get().await()
                nBudgetItems += budgetItemsDocuments.size()

                for (budgetItem in budgetItemsDocuments) {
                    val budgetItemObject = budgetItem.toObject<BudgetItem>()
                    checkOverSpending(budgetItem.id, budgetItemObject.amount!!)
                }
            }
        }
    }

    private suspend fun checkOverSpending(budgetItemID:String, budgetItemAmount:Float){
        val transactionsDocuments = firestore.collection("Transactions")
            .whereEqualTo("budgetItemID", budgetItemID)
            .whereEqualTo("transactionType", "Expense").get().await()
        var amountSpent = 0.00F
        for (expense in transactionsDocuments) {
            val expenseObject = expense.toObject<Transactions>()
            amountSpent+= expenseObject.amount!!
        }
        //they spent more than their allocated budget
        if (amountSpent > budgetItemAmount)
            overSpending++

        overspendingPercentage = (overSpending/nBudgetItems)
    }

    private fun calculateFinancialActivitiesScore() {
        checkIfNaNFinancialActivitiesScores()

        if (age == 9 || age == 12)
            financialActivitiesPerformance = ((savingPercentage + budgetingPercentage + spendingPercentage) / 3)
        else if (age == 10 || age == 11)
            financialActivitiesPerformance = ((goalSettingPercentage + savingPercentage + budgetingPercentage + spendingPercentage) / 4)

        getFinancialAssessmentScore()
    }



    private suspend fun purchasePlanningPerformance() {
        //items planned / all the items they bought * 100
        val financialActivitiesDocuments = firestore.collection("FinancialActivities")
            .whereEqualTo("childID", childID)
            .whereEqualTo("financialActivityName", "Spending")
            .whereEqualTo("status", "Completed").get().await()
        for (spendingActivityID in financialActivitiesDocuments) {
            val shoppingListItemsDocuments = firestore.collection("ShoppingListItems")
                .whereEqualTo("spendingActivityID", spendingActivityID.id).get().await()
            for (shoppingListItem in shoppingListItemsDocuments) {
                val shoppingListItemObject = shoppingListItem.toObject<ShoppingListItem>()
                if (shoppingListItemObject.status == "Purchased")
                    nPlanned++
            }
            val transactionsDocuments = firestore.collection("Transactions")
                .whereEqualTo("financialActivityID", spendingActivityID.id)
                .whereEqualTo("transactionType", "Expense").get().await()
            nTotalPurchased += transactionsDocuments.size().toFloat()
            spendingPercentage = ((1-overspendingPercentage)*100 + ((nPlanned/nTotalPurchased)*100)) /2

        }

    }

    private suspend fun getBudgetingPerformanceScore() {
        val financialActivitiesDocuments = firestore.collection("FinancialActivities")
            .whereEqualTo("childID", childID)
            .whereEqualTo("financialActivityName", "Budgeting")
            .whereEqualTo("status", "Completed").get().await()
        nBudgetingCompleted = financialActivitiesDocuments.size()

        if (financialActivitiesDocuments.size() != 0) {
            for (activity in financialActivitiesDocuments) {
                processBudgetItems(activity)
            }
        }
    }


    private suspend fun processBudgetItems(activity: QueryDocumentSnapshot?) {
        val budgetItemsDocuments = firestore.collection("BudgetItems")
            .whereEqualTo("financialActivityID", activity?.id)
            .whereEqualTo("status", "Active").get().await()
        for (budgetItem in budgetItemsDocuments) {
            budgetItemCount++
            val budgetItemObject = budgetItem.toObject<BudgetItem>()
            //parental involvement
            getParentalInvolvementBudget(budgetItemObject)
            if (activity != null) {
                getBudgetAccuracy(activity.id, budgetItem.id, budgetItemObject)
            }
        }
    }

    private suspend fun getBudgetAccuracy(budgetingActivityID:String, budgetItemID:String,
                                          budgetItemObject:BudgetItem) {
        val financialActivitiesDocument = firestore.collection("FinancialActivities")
            .document(budgetingActivityID).get().await()
        val financialActivitiesDocuments = firestore.collection("FinancialActivities")
            .whereEqualTo("financialGoalID",
                financialActivitiesDocument.toObject<FinancialActivities>()!!.financialGoalID!!)
            .whereEqualTo("financialActivityName", "Spending").get().await()
        val spendingActivity = financialActivitiesDocuments.documents[0].toObject<FinancialActivities>()
        Log.d("ufc", "spendingActivity: "+spendingActivity?.status)

        if (spendingActivity?.status == "Completed") {
            //budget accuracy
            purchasedBudgetItemCount++
            val transactionsDocuments = firestore.collection("Transactions")
                .whereEqualTo("budgetItemID", budgetItemID).get().await()
            var spent = 0.00F
            for (transaction in transactionsDocuments)
                spent += transaction.toObject<Transactions>().amount!!
            totalBudgetAccuracy +=
                (100 - (kotlin.math.abs(budgetItemObject.amount!! - spent)
                        / budgetItemObject.amount!!) * 100)

            budgetingPercentage = if (purchasedBudgetItemCount != 0.00F)
                ((totalBudgetAccuracy/purchasedBudgetItemCount)
                        + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2
            else
                ((1 - (nParent.toFloat()/budgetItemCount)) * 100)

        } else {
            budgetingPercentage = if (purchasedBudgetItemCount != 0.00F)
                ((totalBudgetAccuracy/purchasedBudgetItemCount) + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2
            else
                ((1 - (nParent.toFloat()/budgetItemCount)) * 100)
        }
        //println("print budgeting " + budgetingPercentage )
    }


    private suspend fun getParentalInvolvementBudget(budgetItemObject: BudgetItem) {
        val userDocument = firestore.collection("Users")
            .document(budgetItemObject.createdBy.toString()).get().await()
        //parent is the one who added the budget item
        if (userDocument.toObject<Users>()?.userType == "Parent")
            nParent++

    }

    private suspend fun getGoalSettingPerformance() {
        var overallRating = 0.00F
        val nRatings: Int
        val goalRatingDocuments = firestore.collection("GoalRating")
            .whereEqualTo("childID", childID).get().await()
        nGoalSettingCompleted = goalRatingDocuments.size()
        nRatings = goalRatingDocuments.size()
        for (rating in goalRatingDocuments) {
            val ratingObject = rating.toObject<GoalRating>()
            overallRating += ratingObject.overallRating!!
        }
        goalSettingPercentage = 0.00F

        if (nRatings != 0)
            goalSettingPercentage = ((overallRating / nRatings)/5)* 100

    }

    private suspend fun getSavingPerformanceScore() {
        var nGoals = 0.00F
        var nOnTime = 0.00F
        val financialGoalsDocuments = firestore.collection("FinancialGoals")
            .whereEqualTo("childID", childID)
            .whereEqualTo("status", "Completed").get().await()
        nSavingCompleted = financialGoalsDocuments.size()

        if (financialGoalsDocuments.size() != 0) {
            for (goal in financialGoalsDocuments) {
                nGoals++
                val goalObject = goal.toObject<FinancialGoals>()
                if (goalObject.dateCompleted != null) {
                    val targetDate = goalObject.targetDate!!.toDate()
                    val completedDate = goalObject.dateCompleted!!.toDate()

                    //goal was completed before the target date, meaning it was completed on time
                    if (completedDate.before(targetDate) || completedDate == targetDate)
                        nOnTime++
                }
            }
            savingPercentage = (nOnTime/nGoals)*100
        }
    }

    private fun checkIfNaNFinancialActivitiesScores() {
        val percentages = mutableListOf(savingPercentage, spendingPercentage, budgetingPercentage,
            goalSettingPercentage)

        for (i in percentages.indices) {
            if (percentages[i].isNaN()) {
                when (i) {
                    0 -> savingPercentage = 0.00f
                    1 -> spendingPercentage = 0.00f
                    2 -> budgetingPercentage = 0.00f
                    3 -> goalSettingPercentage = 0.00f
                }
            }
        }
    }

    private fun getFinancialAssessmentScore() {

        var nCorrect = 0
        var nQuestions = 0
        firestore.collection("AssessmentAttempts").whereEqualTo("childID", childID).get().addOnSuccessListener { results ->
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

    private fun checkIfNaN() {
        val percentages = mutableListOf(personalFinancePerformance, financialAssessmentPerformance,
            financialActivitiesPerformance, savingPercentage, spendingPercentage,
            budgetingPercentage, goalSettingPercentage)

        for (ind in percentages)
            Log.d("mastro", "checkIfNaN: "+ind)

        for (i in percentages.indices) {
            if (percentages[i].isNaN()) {
                when (i) {
                    0 -> personalFinancePerformance = 0.00f
                    1 -> financialAssessmentPerformance = 0.00f
                    2 -> financialActivitiesPerformance = 0.00f
                    3 -> savingPercentage = 0.00f
                    4 -> spendingPercentage = 0.00f
                    5 -> budgetingPercentage = 0.00f
                    6 -> goalSettingPercentage = 0.00f
                }
            }
        }
    }


    private fun getOverallFinancialHealth(){
        checkIfNaN()
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

        Toast.makeText(this, ""+overallFinancialHealth, Toast.LENGTH_SHORT).show()
        setPerformanceView()
    }


    private fun setPerformanceView() {
        if (overallFinancialHealth.isNaN())
            overallFinancialHealth = 0.00F

        val df = DecimalFormat("#.#")
        df.roundingMode = java.math.RoundingMode.UP
        val roundedValue = df.format(overallFinancialHealth)

        binding.tvPerformancePercentage.text = "${roundedValue}%"

        val imageView = binding.ivScore
        val message: String
        val performance: String
        val bitmap: Bitmap

        /*TODO: Change Audio file in mediaPlayer*/
        var audio = 0

        if (overallFinancialHealth == 100.00F) {
            audio = if (userType == "Parent")
                R.raw.child_dashboard_parent_excellent
            else
                R.raw.child_dashboard_excellent

            performance = "Excellent!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.dark_green))
            message = if (userType == "Parent")
                "Your child is a financial guru! Celebrate their accomplishments and encourage them to keep it up!"
            else "You've demonstrated exceptional knowledge and skills in personal finance, financial activities, and financial assessments!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.excellent)
        } else if (overallFinancialHealth > 90) {
            audio = if (userType == "Parent")
                R.raw.child_dashboard_parent_amazing
            else
                R.raw.child_dashboard_amazing

            performance = "Amazing!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.amazing_green))
            message = if (userType == "Parent")
                "Your child has a solid foundation in personal finance, financial activities, and concepts. Keep empowering them!"
            else " You're a true financial whiz! Keep refining your skills, exploring financial concepts, and inspiring others with your expertise!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.amazing)
        } else if (overallFinancialHealth > 80) {
            audio = if (userType == "Parent")
                R.raw.child_dashboard_parent_great
            else
                R.raw.child_dashboard_great

            performance = "Great!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.green))
            message = if (userType == "Parent")
                "Your child has strong financial decision-making skills. Encourage them to keep this up!"
            else "You have a strong grasp of finance concepts and know how to properly manage your money in day to day activities. Keep it up!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.great)
        } else if (overallFinancialHealth > 70) {
            audio = if (userType == "Parent")
                R.raw.child_dashboard_parent_good
            else
                R.raw.child_dashboard_good

            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.light_green))
            performance = "Good!"
            message = if (userType == "Parent")
                "Your child is good at real-life financial decision-making & has a good grasp of financial concepts!"
            else "Keep making great decisions in real-life financial situations and learning about financial concepts!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.good)
        } else if (overallFinancialHealth > 60) {
            audio = if (userType == "Parent")
                R.raw.child_dashboard_parent_average
            else
                R.raw.child_dashboard_average

            performance = "Average"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.yellow))
            message = if (userType == "Parent")
                "Continue supporting your child in their development by having them participate in decision making activities at home!"
            else "You're becoming a confident financial decision-maker. Keep doing financial activities & assessments to grow!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.average)
        } else if (overallFinancialHealth > 50) {
            audio = if (userType == "Parent")
                R.raw.child_dashboard_parent_nearly_there
            else
                R.raw.child_dashboard_nearly_there

            performance = "Nearly There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            message = if (userType == "Parent")
                "Your child is nearly there. Have them participate in decision making activities at home!"
            else "You're making significant strides in your financial literacy journey. Keep making wise financial decisions!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.nearly_there)
        } else if (overallFinancialHealth > 40) {
            audio = if (userType == "Parent")
                R.raw.child_dashboard_parent_almost_there
            else
                R.raw.child_dashboard_almost_there

            performance = "Almost There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.almost_there_yellow))
            message = if (userType == "Parent")
                "Your child is developing their financial decision-making. Have them participate in decision making activities at home!"
            else "You're becoming a savvy money manager. Keep exploring financial activities and assessments to strengthen your skills!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.almost_there)
        } else if (overallFinancialHealth > 30) {
            audio = if (userType == "Parent")
                R.raw.child_dashboard_parent_getting_there
            else
                R.raw.child_dashboard_getting_there

            performance = "Getting There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.getting_there_orange))
            message = if (userType == "Parent")
                "Your child is still developing their financial decision-making. Allow them to practice this skill at home!"
            else "You're beginning to build a solid foundation in financial decision-making. Keep improving!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.getting_there)
        } else if (overallFinancialHealth > 20) {
            audio = if (userType == "Parent")
                R.raw.child_dashboard_parent_not_quite
            else
                R.raw.child_dashboard_not_quite

            performance = "Not Quite There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.not_quite_there_red))
            message = if (userType == "Parent")
                "Your child is still developing their financial decision-making. Allow them to practice this skill at home!"
            else "You're developing your financial decision-making skills. Keep exercising financial decision making to improve!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.not_quite_there_yet)
        } else if (overallFinancialHealth > 10) {
            audio = if (userType == "Parent")
                R.raw.child_dashboard_parent_needs_improement
            else
                R.raw.child_dashboard_needs_improvement

            performance = "Needs Improvement"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.red))
            message = if (userType == "Parent")
                "Your child is starting their financial journey! Encourage them to keep exploring financial decision-making!"
            else "You're just getting started, and that's okay! Keep exercising your financial decision-making. Don't give up!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        }
        else {
            audio = if (userType == "Parent")
                R.raw.child_dashboard_parent_default
            else
                R.raw.child_dashboard_parent_default

            performance = "Get Started!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.black))
            message = "Start using the app and go back to this module to view your performance!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.peso_coin)
            binding.tvPerformancePercentage.visibility = View.GONE
        }

        imageView.setImageBitmap(bitmap)
        binding.tvPerformanceText.text = message
        binding.tvPerformanceStatus.text = performance
        binding.tvPerformancePercentage.text =  DecimalFormat("##0.00").format(overallFinancialHealth) + "%"

        loadAudio(audio)
    }
    private fun loadAudio(audio: Int) {
        /*TODO: Change binding and Audio file in mediaPlayer*/
        binding.btnAudioOverallFinancialLiteracyScore.setOnClickListener {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, audio)
            }

            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                mediaPlayer?.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayer?.start()
        }
    }

    override fun onPause() {
        releaseMediaPlayer()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
    }


    private fun releaseMediaPlayer() {
        mediaPlayer?.apply {
            if (isPlaying) {
                pause()
                seekTo(0)
            }
            stop()
            release()
        }
        mediaPlayer = null
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