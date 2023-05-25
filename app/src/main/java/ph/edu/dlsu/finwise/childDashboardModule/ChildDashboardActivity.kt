package ph.edu.dlsu.finwise.childDashboardModule

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
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
import ph.edu.dlsu.finwise.NavbarParent
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

        checkUser()
    }

    private fun checkUser() {
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            val user = it.toObject<Users>()!!
            userType = user.userType.toString()
            //current user is a child
            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent
            if (user.userType == "Child") {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_dashboard)
            } else if (user.userType == "Parent") {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                //sends the ChildID to the parent navbar
                val bundle = intent.extras
                currentUser = bundle?.getString("childID").toString()
                val bundleNavBar = Bundle()
                bundleNavBar.putString("childID", currentUser)
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_dashboard, bundleNavBar)
            }
            getPerformance()
            initializeFragments()
            initializeDateButtons()
        }
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
                        val targetDate = goalObject.targetDate!!.toDate()
                        val completedDate = goalObject.dateCompleted!!.toDate()

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
                                if (user.toObject<Users>()?.userType == "Parent")
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
            audio = R.raw.sample
            performance = "Excellent!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.dark_green))
            message = if (userType == "Parent")
                "Your child is a financial guru. Celebrate their accomplishments and encourage them to pursue advanced financial literacy courses or activities!"
            else "You've demonstrated exceptional knowledge and skills in personal finance, financial activities, and financial assessments!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.excellent)
        } else if (overallFinancialHealth > 90) {
            audio = R.raw.sample
            performance = "Amazing!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.amazing_green))
            message = if (userType == "Parent")
                "They have a solid foundation in personal finance. Keep empowering them to pursue their financial goals!"
            else "You're a true financial whiz. Keep refining your skills, exploring complex financial concepts, and inspiring others with your expertise!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.amazing)
        } else if (overallFinancialHealth > 80) {
            audio = R.raw.sample
            performance = "Great!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.green))
            message = if (userType == "Parent")
                "Your child's financial knowledge and skills are strong. Encourage them to explore new ways to grow their savings and make informed financial decisions!"
            else "You have a strong grasp of personal finance concepts. Keep exploring advanced topics and applying your knowledge to real-life situations!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.great)
        } else if (overallFinancialHealth > 70) {
            audio = R.raw.sample
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.light_green))
            performance = "Good!"
            message = if (userType == "Parent")
                "Your child is doing an excellent job with their financial literacy. They understand the value of budgeting and saving. Keep supporting their efforts!"
            else "You're well on your way to becoming a financial expert. Keep setting goals, budgeting effectively, and making informed choices!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.good)
        } else if (overallFinancialHealth > 60) {
            audio = R.raw.sample
            performance = "Average"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.yellow))
            message = if (userType == "Parent")
                "Your child's financial skills are improving. Continue helping them track their expenses and work towards achieving their goals!"
            else "You're becoming a confident financial decision-maker. Keep practicing financial activities and assessments to enhance your knowledge and skills!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.average)
        } else if (overallFinancialHealth > 50) {
            audio = R.raw.sample
            performance = "Nearly There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            message = if (userType == "Parent")
                "Your child has reached the halfway point. They are building solid financial habits. Keep encouraging them to save and set realistic goals!"
            else "You're making significant strides in your financial literacy journey. Keep saving, setting goals, and budgeting wisely. Your financial future looks bright!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.nearly_there)
        } else if (overallFinancialHealth > 40) {
            audio = R.raw.sample
            performance = "Almost There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.almost_there_yellow))
            message = if (userType == "Parent")
                "Your child is developing a good understanding of personal finance. Support them in making thoughtful spending decisions!"
            else "You're becoming a savvy money manager. Keep exploring different financial activities and assessments to strengthen your skills!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.almost_there)
        } else if (overallFinancialHealth > 30) {
            audio = R.raw.sample
            performance = "Getting There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.getting_there_orange))
            message = if (userType == "Parent")
                "They are learning the importance of budgeting and saving. Encourage them to practice these skills regularly!"
            else "You're building a solid foundation in personal finance. Keep setting goals, budgeting wisely, and learning about money. Your financial confidence is growing!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.getting_there)
        } else if (overallFinancialHealth > 20) {
            audio = R.raw.sample
            performance = "Not Quite There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.not_quite_there_red))
            message = if (userType == "Parent")
                "Your child is making progress in understanding personal finance. Continue guiding them in setting financial goals!"
            else "You're making progress in developing your financial skills. Keep practicing different activities and assessments to continue growing your financial knowledge!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.not_quite_there_yet)
        } else if (overallFinancialHealth > 10) {
            audio = R.raw.sample
            performance = "Need Improvement"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.red))
            message = if (userType == "Parent")
                "Your child is starting their financial journey! Encourage them to keep learning and exploring different ways to manage money!"
            else "You're just getting started, and that's fantastic! Keep exploring saving, goal setting, budgeting, and spending. Your financial journey is off to a great start!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        }
        else {
            audio = R.raw.sample
            performance = "Get Started!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.yellow))
            message = "You have no overall finance performance yet. Start using the app and go back to this module"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.nearly_there)
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

    override fun onDestroy() {
        mediaPlayer?.let { releaseMediaPlayer(it) }
        super.onDestroy()
    }

    override fun onPause() {
        mediaPlayer?.let { releaseMediaPlayer(it) }
        super.onPause()
    }

    private fun releaseMediaPlayer(mediaPlayer: MediaPlayer) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
        mediaPlayer.stop()
        mediaPlayer.release()
    }


    private fun checkIfNaN() {
        val percentages = mutableListOf(personalFinancePerformance, financialAssessmentPerformance,
            savingPercentage, spendingPercentage, budgetingPercentage, goalSettingPercentage)

        for (i in percentages.indices) {
            if (percentages[i].isNaN()) {
                when (i) {
                    0 -> personalFinancePerformance = 0.00f
                    1 -> financialAssessmentPerformance = 0.00f
                    2 -> savingPercentage = 0.00f
                    3 -> spendingPercentage = 0.00f
                    4 -> budgetingPercentage = 0.00f
                    5 -> goalSettingPercentage = 0.00f
                }
            }
        }
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