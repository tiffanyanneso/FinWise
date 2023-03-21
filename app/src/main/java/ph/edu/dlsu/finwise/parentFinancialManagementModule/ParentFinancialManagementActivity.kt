package ph.edu.dlsu.finwise.parentFinancialManagementModule

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.MainActivity
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.PFMAdapter
import ph.edu.dlsu.finwise.databinding.ActivityParentFinancialManagementBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningActivity
import ph.edu.dlsu.finwise.parentFinancialManagementModule.pFMFragments.ExplanationParentFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.CashMayaBalanceBreakdownActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.TransactionHistoryActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.BalanceFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.SavingsFragment
import java.text.DecimalFormat
import java.util.ArrayList


class ParentFinancialManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentFinancialManagementBinding
    private var firestore = Firebase.firestore
    private var bundle = Bundle()
    private lateinit var context: Context
    private var balance = 0.00f
    private var income = 0.00f
    private var expense = 0.00f
    private lateinit var childID: String

    private val tabIcons2 = intArrayOf(
        R.drawable.baseline_account_balance_24,
        R.drawable.baseline_wallet_24
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentFinancialManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        //Initializes the navbar
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance)
        loadBalance()

        binding.topAppBar.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent (this, MainActivity::class.java)
                    startActivity (intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun initializeFragments() {
        setUpChartTabs()
        goToSendMoney()
        loadFinancialHealth()
        initializeDateButtons()
        goToParentTransactions()
        goToEarningActivity()
        goToCashMayaBalanceBreakdownParent()
    }

    private fun goToCashMayaBalanceBreakdownParent(){
        binding.btnSeeMore.setOnClickListener{
            val goToCashMayaBalanceBreakdown = Intent(applicationContext, CashMayaBalanceBreakdownActivity::class.java)
            bundle.putString("childID", childID)
            bundle.putFloat("balance", balance)
            goToCashMayaBalanceBreakdown.putExtras(bundle)
            startActivity(goToCashMayaBalanceBreakdown)
        }
    }

    private fun goToEarningActivity() {
        binding.btnEarning.setOnClickListener {
            val goToHomeRewardsActivity = Intent(this, EarningActivity::class.java)
            bundle.putString("childID", childID)
            bundle.putString("module", "pfm")
            goToHomeRewardsActivity.putExtras(bundle)
            startActivity(goToHomeRewardsActivity)
        }
    }


    private fun goToParentTransactions() {
        binding.btnViewTransactions.setOnClickListener {
            val goToTransactions = Intent(applicationContext, TransactionHistoryActivity::class.java)
            bundle.putString("user", "parent")
            bundle.putString("childID", childID)
            goToTransactions.putExtras(bundle)
            startActivity(goToTransactions)
        }
    }


    private fun initializeDateButtons() {
        initializeWeeklyButton()
        initializeMonthlyButton()
        initializeQuarterlyButton()
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


    private fun setUpChartTabs() {
        val adapter = PFMAdapter(supportFragmentManager)
        val balanceFragment = BalanceFragment()
        val savingsFragment = SavingsFragment()
        bundle.putString("user", "parent")
        bundle.putString("childID", childID)
        balanceFragment.arguments = bundle
        savingsFragment.arguments = bundle
        adapter.addFragment(balanceFragment, "Balance")
        adapter.addFragment(savingsFragment, "Goal Savings")
        binding.viewPagerBarCharts.adapter = adapter
        binding.tabsBarCharts.setupWithViewPager(binding.viewPagerBarCharts)

        binding.tabsBarCharts.getTabAt(0)?.text = "Balance"
        binding.tabsBarCharts.getTabAt(1)?.text = "Goal Savings"
        setupTabIcons2()
    }

    private fun setupTabIcons2() {
        binding.tabsBarCharts.getTabAt(0)?.setIcon(tabIcons2[0])
        binding.tabsBarCharts.getTabAt(1)?.setIcon(tabIcons2[1])
    }


    private fun loadFinancialHealth() {
        //TODO: change to currentUser
        firestore.collection("Transactions").whereEqualTo("userID", childID)
            .get().addOnSuccessListener { documents ->
                val transactionsArrayList = initializeTransactions(documents)
                getIncomeAndExpense(transactionsArrayList)
                computeIncomeExpenseRatio()
                loadExplanation()
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

    private fun getIncomeAndExpense(transactionsArrayList: ArrayList<Transactions>) {
        for (transaction in transactionsArrayList) {
            if (transaction.transactionType == "Income")
                income += transaction.amount!!
            else if (transaction.transactionType == "Expense")
                expense += transaction.amount!!
        }
    }

    private fun goToCashMayaBalanceBreakdown(){
        binding.btnSeeMore.setOnClickListener{
            val goToCashMayaBalanceBreakdown = Intent(applicationContext, CashMayaBalanceBreakdownActivity::class.java)
            goToCashMayaBalanceBreakdown.putExtras(bundle)
            startActivity(goToCashMayaBalanceBreakdown)
        }
    }

    private fun computeIncomeExpenseRatio() {
        val ratio = (income / expense * 100).toInt() // convert to percentage and round down to nearest integer
        val imageView = binding.ivScore
        val grade: String
        val performance: String
        val bitmap: Bitmap

        if (ratio >= 180) {
            performance = "Excellent!"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.dark_green))
            grade = "Your child's income is much more than their expenses, and they're saving a large amount of money. They're doing an amazing job!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.excellent)
        } else if (ratio in 160..180) {
            performance = "Amazing!"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.green))
            grade = "Your child's income is significantly more than their expenses, and they're saving a substantial amount of money. They can look for ways to invest their money wisely."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.great)
        } else if (ratio in 140..159) {
            performance = "Great!"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.light_green))
            grade = "Your child's income is much more than their expenses, and they're saving a good amount of money. They're on the right track!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.good)
        } else if (ratio in 120..139) {
            binding.tvPerformance.setTextColor(resources.getColor(R.color.yellow))
            performance = "Good!"
            grade = "Your child's income is more than their expenses, and they're saving a decent amount of money. They should Keep it up!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.average)
        } else if (ratio in 100..119) {
            performance = "Average"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.red))
            grade = "Your child's income is slightly more than their expenses, and they're saving a small amount of money. They should keep it up and look for ways to increase their income and savings."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        } else if (ratio in 80..99) {
            performance = "Nearly There"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.red))
            grade = "Your child's income and expenses are about the same, and they're not saving much money. They need to look for ways to increase their income and reduce their expenses."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        } else if (ratio in 60..79) {
            performance = "Almost There"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.red))
            grade = "Your child's expenses are slightly more than their income, and they're saving a little bit of money. They should try to reduce their expenses further to save more money."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        } else if (ratio in 40..59) {
            performance = "Getting There"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.red))
            grade = "Your child's expenses are more than their income, and they're barely saving any money. They need to cut down on their expenses to start saving money."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        } else if (ratio in 20..39) {
            performance = "Not Quite There"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.red))
            grade = "Your child's expenses are much more than their income, and they're not saving any money. They need to make some changes to their spending habits."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        } else if (ratio in 1..19) {
            performance = "Need Improvement"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.red))
            grade = "Your child's expenses are more than their income. This means that they're in trouble and need to take action right away to cut down their expenses."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        }
        else {
            performance = "Bad...!"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.red))
            grade = "Your child has 0 balance. You can give them money through Maya by clicking the button above"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.average)
        }

        imageView.setImageBitmap(bitmap)
        binding.tvScore.text = grade
        binding.tvPerformance.text = performance

    }

    private fun loadExplanation() {
        //TODO: Change Fragment to parent
        binding.btnExplanation.setOnClickListener {
            val activity = context as FragmentActivity
            val fm: FragmentManager = activity.supportFragmentManager
            val dialogFragment = ExplanationParentFragment()
            dialogFragment.show(fm, "fragment_alert")
        }
    }


    private fun loadBalance() {
        val parentID = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").whereEqualTo("parentID", parentID)
            .get().addOnSuccessListener { document ->
                //check which child
                childID = document.documents[0].id
                loadBalanceView()
                initializeFragments()
            }
    }

    private fun loadBalanceView() {
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
                binding.tvCurrentBalanceOfChild.text = "₱$amount"
            }.continueWith { goToCashMayaBalanceBreakdown() }
    }


    private fun goToSendMoney() {
        binding.btnSendMoney.setOnClickListener {
            val goToSendMoney = Intent(applicationContext, ParentSendMoneyActivity::class.java)
            bundle.putFloat("balance", balance)
            goToSendMoney.putExtras(bundle)
            startActivity(goToSendMoney)
        }
    }
}