package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.*
import ph.edu.dlsu.finwise.databinding.ActivityPersonalFinancialManagementBinding
import ph.edu.dlsu.finwise.databinding.DialogNotifSummaryChildBinding
import ph.edu.dlsu.finwise.model.*
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningMenuActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.BalanceFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.ExplanationFragment
import java.text.DecimalFormat
import java.util.*


class PersonalFinancialManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalFinancialManagementBinding
    private var firestore = Firebase.firestore
    private var bundle = Bundle()
    private var childID  = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var context: Context
    private var mediaPlayer: MediaPlayer? = null

    var balance = 0.00f
    var income = 0.00f
    var expense = 0.00f

    private lateinit var lastLogin: Date

    private var newEarningArrayList = ArrayList<String>()
    private var earningSentArrayList = ArrayList<String>()
    private var goalEditArrayList = ArrayList<String>()
    private var goalReviewedArrayList = ArrayList<String>()

    /*private val tabIcons1 = intArrayOf(
        R.drawable.baseline_wallet_24,
        R.drawable.baseline_shopping_cart_checkout_24
    )*/

    private val tabIcons2 = intArrayOf(
        R.drawable.baseline_account_balance_24,
        R.drawable.baseline_wallet_24
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalFinancialManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)

        checkNotifications()
        initializeFragments()
        //setUpBreakdownTabs()
        //initializeButtons()
        //initializeBalanceBarGraph()
        //initializeSavingsBarGraph()
        //goToTransactionHistory()
    }



    private fun initializeFragments() {
        bundle.putString("user", "child")
        setUpChartTabs()
        loadBalance()
        loadFinancialHealth()
    }

    private fun loadFinancialHealth() {
        firestore.collection("Transactions").whereEqualTo("userID", childID)
            .get().addOnSuccessListener { documents ->
                val transactionsArrayList = initializeTransactions(documents)
                getIncomeAndExpense(transactionsArrayList)
                computeIncomeExpenseRatio()
                loadExplanation()
                loadView()
            }
    }

    private fun loadView() {
        binding.layoutPfm.visibility = View.VISIBLE
        binding.bottomNav.visibility = View.VISIBLE
    }

    private fun loadAudio(audio: Int) {
        binding.btnAudioPersonalFinanceScore.setOnClickListener {
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

    private fun loadExplanation() {
        binding.btnExplanation.setOnClickListener {
            val activity = context as FragmentActivity
            val fm: FragmentManager = activity.supportFragmentManager
            val dialogFragment = ExplanationFragment()
            dialogFragment.show(fm, "fragment_alert")
            releaseMediaPlayer()
        }
    }

    private fun computeIncomeExpenseRatio() {
        val ratio = (income / expense * 100).toInt() // convert to percentage and round down to nearest integer
        val imageView = binding.ivScore
        val grade: String
        val performance: String
        val bitmap: Bitmap
        //TODO: Change audio
        var audio = 0

        if (ratio >= 180) {
            audio = R.raw.financial_management_excellent
            performance = "Excellent!"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.dark_green))
            grade = "Your income is much more than your expenses, and you're saving money. You're doing an excellent job!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.excellent)
        } else if (ratio in 160..180) {
            audio = R.raw.financial_management_amazing
            performance = "Amazing!"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.amazing_green))
            grade = "Your income is significantly more than your expenses, and you're saving money. Keep it up!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.amazing)
        } else if (ratio in 140..159) {
            audio = R.raw.financial_management_great
            performance = "Great!"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.green))
            grade = "Your income is much more than your expenses, and you're saving a good amount of money. You're on the right track!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.great)
        } else if (ratio in 120..139) {
            audio = R.raw.financial_management_good
            binding.tvPerformance.setTextColor(resources.getColor(R.color.light_green))
            performance = "Good!"
            grade = "Your income is more than your expenses, and you're saving a decent amount of money. Keep it up!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.good)
        } else if (ratio in 100..119) {
            audio = R.raw.financial_management_average
            performance = "Average"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.yellow))
            grade = "Your income is slightly more than your expenses, and you're saving money. Keep it up and look for ways to increase your income and savings."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.average)
        } else if (ratio in 80..99) {
            audio = R.raw.financial_management_nearly_there
            performance = "Nearly There"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            grade = "Your income and expenses are about the same, and you're saving some money. Look for ways to increase your income and reduce your expenses."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.nearly_there)
        } else if (ratio in 60..79) {
            audio = R.raw.financial_management_almost_there
            performance = "Almost There"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.almost_there_yellow))
            grade = "Your expenses are slightly more than your income, and you're saving a bit of money. Try to reduce your expenses further to save more."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.almost_there)
        } else if (ratio in 40..59) {
            audio = R.raw.financial_management_getting_there
            performance = "Getting There"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.getting_there_orange))
            grade = "Your expenses are more than your income, and you're barely saving money. Try cutting down on your expenses to save money."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.getting_there)
        } else if (ratio in 20..39) {
            audio = R.raw.financial_management_not_quite
            performance = "Not Quite There"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.not_quite_there_red))
            grade = "Your expenses are much more than your income, and you're not saving money. Try cutting down on your expenses and thinking before you buy!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.not_quite_there_yet)
        } else if (ratio in 1..19) {
            audio = R.raw.financial_management_needs_improvement
            performance = "Needs Improvement"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.red))
            grade = "Your expenses are more than your income. Try cutting down on your expenses and earning money through chores or selling!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        }
        else {
            audio = R.raw.financial_management_default
            performance = "Get Started!"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.dark_green))
            grade = "You have 0 balance. Click the income button above to add your money."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.excellent)
        }

        loadAudio(audio)
        imageView.setImageBitmap(bitmap)
        binding.tvScore.text = grade
        binding.tvPerformance.text = performance

    }

    private fun getIncomeAndExpense(transactionsArrayList: ArrayList<Transactions>) {
        for (transaction in transactionsArrayList) {
            if (transaction.transactionType == "Income")
                income += transaction.amount!!
            else if (transaction.transactionType == "Expense")
                expense += transaction.amount!!
        }
    }

    private fun initializeTransactions(documents: QuerySnapshot): ArrayList<Transactions> {
        val transactionsArrayList = ArrayList<Transactions>()
        for (transactionSnapshot in documents) {
            //creating the object from list retrieved in db
            val transaction = transactionSnapshot.toObject<Transactions>()
            transactionsArrayList.add(transaction)
        }
        return transactionsArrayList
    }

    private fun initializeButtons() {
        goToDepositGoalActivity()
        goToIncomeActivity()
        goToExpenseActivity()
        goToTransactions()
        goToEarningActivity()
        goToCashMayaBalanceBreakdown()
        initializeDateButtons()
    }

    private fun initializeDateButtons() {
        initializeWeeklyButton()
        initializeMonthlyButton()
        initializeQuarterlyButton()
    }

    private fun initializeWeeklyButton() {
        val weeklyButton = binding.btnWeekly
        val monthlyButton = binding.btnMonthly
        val yearlyButton = binding.btnQuarterly
        weeklyButton.setOnClickListener {
            weeklyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green))
            monthlyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            yearlyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            bundle.putString("date", "weekly")
            setUpChartTabs()
            //setUpBreakdownTabs()
        }
    }

    private fun initializeMonthlyButton() {
        val weeklyButton = binding.btnWeekly
        val monthlyButton = binding.btnMonthly
        val yearlyButton = binding.btnQuarterly
        monthlyButton.setOnClickListener {
            weeklyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            monthlyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green))
            yearlyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            bundle.putString("date", "monthly")
            setUpChartTabs()
            //setUpBreakdownTabs()
        }
    }

    private fun initializeQuarterlyButton() {
        val weeklyButton = binding.btnWeekly
        val monthlyButton = binding.btnMonthly
        val quarterlyButton = binding.btnQuarterly
        quarterlyButton.setOnClickListener {
            weeklyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            monthlyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            quarterlyButton.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green))
            bundle.putString("date", "quarterly")
            setUpChartTabs()
            //setUpBreakdownTabs()
        }
    }


    private fun setUpChartTabs() {
        val adapter = PFMAdapter(supportFragmentManager)
        val balanceFragment = BalanceFragment()
        balanceFragment.arguments = bundle
        //val savingsFragment = SavingsFragment()

        //savingsFragment.arguments = bundle
        adapter.addFragment(balanceFragment, "Balance")
        //adapter.addFragment(savingsFragment, "Goal Savings")
        binding.viewPagerBarCharts.adapter = adapter
        binding.tabsBarCharts.setupWithViewPager(binding.viewPagerBarCharts)

        binding.tabsBarCharts.getTabAt(0)?.text = "Balance"
        //binding.tabsBarCharts.getTabAt(1)?.text = "Goal Savings"
        setupTabIcons2()
    }
    private fun setupTabIcons2() {
        binding.tabsBarCharts.getTabAt(0)?.setIcon(tabIcons2[0])
        //binding.tabsBarCharts.getTabAt(1)?.setIcon(tabIcons2[1])
    }

   /* private fun setupTabIcons1() {
        binding.tabs.getTabAt(0)?.setIcon(tabIcons1[0])
        binding.tabs.getTabAt(1)?.setIcon(tabIcons1[1])
    }*/

   /* private fun setUpBreakdownTabs() {
        val adapter = PFMAdapter(supportFragmentManager)
        val incomeFragment = IncomeFragment()
        val expenseFragment = ExpenseFragment()
        incomeFragment.arguments = bundle
        expenseFragment.arguments = bundle
        adapter.addFragment(incomeFragment, "Income")
        adapter.addFragment(expenseFragment, "Expense")
        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)

        binding.tabs.getTabAt(0)?.text = "Income"
        binding.tabs.getTabAt(1)?.text = "Expense"
        setupTabIcons1()
    }*/

    private fun loadBalance() {
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        firestore.collection("ChildWallet").whereEqualTo("childID", childID)
            .get().addOnSuccessListener { document ->
                var walletAmount = 0.00f
                for (wallets in document) {
                    val childWallet = wallets.toObject<ChildWallet>()
                    walletAmount += childWallet.currentBalance!!
                }
                var amount = DecimalFormat("#,##0.00").format(walletAmount)
                if (walletAmount < 0.00F)
                    amount = "0.00"
                balance = walletAmount
                binding.tvBalance.text = "₱$amount"
                initializeButtons()
            }
    }

    private fun goToCashMayaBalanceBreakdown(){
        binding.btnSeeMore.setOnClickListener{
            val goToCashMayaBalanceBreakdown = Intent(applicationContext, CashMayaBalanceBreakdownActivity::class.java)
            getBundles()
            goToCashMayaBalanceBreakdown.putExtras(bundle)
            startActivity(goToCashMayaBalanceBreakdown)
        }
    }


    private fun goToTransactions() {
        binding.btnViewTransactions.setOnClickListener {
            val goToTransactions = Intent(applicationContext, TransactionHistoryActivity::class.java)
            bundle.putString("user", "child")
            goToTransactions.putExtras(bundle)
            startActivity(goToTransactions)
        }
    }


    private fun goToDepositGoalActivity() {
        binding.btnGoal.setOnClickListener {
            val goToDepositGoalActivity = Intent(applicationContext, RecordDepositActivity::class.java)
            getBundles()
            goToDepositGoalActivity.putExtras(bundle)
            startActivity(goToDepositGoalActivity)
        }
    }

    private fun goToIncomeActivity() {
        binding.btnIncome.setOnClickListener {
            val goToIncomeActivity = Intent(applicationContext, RecordIncomeActivity::class.java)
            getBundles()
            goToIncomeActivity.putExtras(bundle)
            startActivity(goToIncomeActivity)
        }
    }

    private fun goToExpenseActivity() {
        binding.btnExpense.setOnClickListener {
            val goToExpenseActivity = Intent(applicationContext, RecordExpenseActivity::class.java)
            getBundles()
            goToExpenseActivity.putExtras(bundle)
            startActivity(goToExpenseActivity)
        }
    }

    private fun goToEarningActivity() {
        binding.btnEarning.setOnClickListener {
            val goToEarningActivity = Intent(applicationContext, EarningMenuActivity::class.java)
            val bundle = Bundle()
            bundle.putString("childID", childID)
            bundle.putString("module", "pfm")
            goToEarningActivity.putExtras(bundle)
            startActivity(goToEarningActivity)
        }
    }

    private fun getBundles() {
        bundle.putFloat("balance", balance)
        bundle.putString("childID", childID)
    }

    private fun checkNotifications() {
        CoroutineScope(Dispatchers.Main).launch {
            lastLogin = firestore.collection("Users").document(childID).get().await().toObject<Users>()!!.lastLogin!!.toDate()
            notificationsForNewEarning()
            notificationForSentEarning()
            notificationsForGoalReviewed()
            notificationsForGoalEdit()
            loadDialogAndRecyclerView()
            updateLastLogin()
        }

    }

    private suspend fun notificationsForNewEarning() {
        var earningActivities = firestore.collection("EarningActivities").whereEqualTo("childID", childID).whereEqualTo("status", "Ongoing").get().await()

        for (earning in earningActivities) {
            var earningDateAdded = earning.toObject<EarningActivityModel>().dateAdded!!.toDate()

            if (earningDateAdded.after(lastLogin))
                newEarningArrayList.add(earning.id)
        }
    }

    private suspend fun notificationForSentEarning() {
        var earningActivities = firestore.collection("EarningActivities").whereEqualTo("childID", childID).whereEqualTo("status", "Completed").get().await()
        for (earning in earningActivities) {
            var earningDateSent = earning.toObject<EarningActivityModel>().dateSent!!.toDate()

            if (earningDateSent.after(lastLogin))
                earningSentArrayList.add(earning.id)
        }

    }

    private suspend fun notificationsForGoalReviewed() {
        var goalsReviewed = firestore.collection("GoalRating").whereEqualTo("childID", childID).whereEqualTo("status", "Approved").get().await()
        for (goalRating in goalsReviewed) {
            var dateGoalReviewed = goalRating.toObject<GoalRating>().lastUpdated!!.toDate()

            if (dateGoalReviewed.after(lastLogin))
                goalReviewedArrayList.add(goalRating.toObject<GoalRating>().financialGoalID!!)
        }
    }

    private suspend fun notificationsForGoalEdit() {
        var goalsEdit = firestore.collection("FinancialGoals").whereEqualTo("status", "For Editing").get().await()
        for (goalEdit in goalsEdit) {
            goalEditArrayList.add(goalEdit.id)
        }
    }

    private fun loadDialogAndRecyclerView() {
        if (!newEarningArrayList.isEmpty() || !earningSentArrayList.isEmpty() || !goalReviewedArrayList.isEmpty() || !goalEditArrayList.isEmpty()) {
            var dialogBinding = DialogNotifSummaryChildBinding.inflate(layoutInflater)
            var dialog = Dialog(this)
            dialog.setContentView(dialogBinding.root)
            dialog.window!!.setLayout(1100, 2000)

            if (!newEarningArrayList.isEmpty()) {
                var newEarningActivityModel = ChildNotificationNewEarningAdapter(this, newEarningArrayList)
                dialogBinding.rvNewEarning.adapter = newEarningActivityModel
                dialogBinding.rvNewEarning.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                newEarningActivityModel.notifyDataSetChanged()
            } else
               dialogBinding.layoutNewEarning.visibility = View.GONE

            if (!earningSentArrayList.isEmpty()) {
                var earningSentAdapter = ChildNotificationRewardSentAdapter(this, earningSentArrayList)
                dialogBinding.rvReceivedEarning.adapter = earningSentAdapter
                dialogBinding.rvReceivedEarning.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                earningSentAdapter.notifyDataSetChanged()
            } else
                dialogBinding.layoutReceivedEarning.visibility = View.GONE

            if (!goalReviewedArrayList.isEmpty()) {
                var goalsApprovedAdapter = ChildNotificationGoalRatedAdapter(this, goalReviewedArrayList)
                dialogBinding.rvApprovedGoals.adapter = goalsApprovedAdapter
                dialogBinding.rvApprovedGoals.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                goalsApprovedAdapter.notifyDataSetChanged()
            } else
                dialogBinding.layoutGoalApproved.visibility = View.GONE

            if (!goalEditArrayList.isEmpty()) {
                var goalEditAdapter = ChildNotificationGoalEditAdapter(this, goalEditArrayList)
                dialogBinding.rvGoalEdit.adapter = goalEditAdapter
                dialogBinding.rvGoalEdit.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                goalEditAdapter.notifyDataSetChanged()
            } else
                dialogBinding.layoutGoalEdit.visibility = View.GONE

            dialogBinding.btnViewAll.setOnClickListener {
                var summary = Intent(this, ChildNotificationSummary::class.java)
                summary.putExtra("lastLogin", lastLogin)
                startActivity(summary)
            }

            dialogBinding.btnClose.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }

    }

    private fun updateLastLogin() {
        firestore.collection("Users").document(childID).update("lastLogin", Timestamp.now())
    }

}