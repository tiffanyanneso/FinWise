package ph.edu.dlsu.finwise.parentFinancialManagementModule

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.PFMAdapter
import ph.edu.dlsu.finwise.databinding.ActivityParentFinancialManagementBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.personalFinancialManagementModule.TransactionHistoryActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.BalanceFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.ExplanationFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.SavingsFragment
import java.text.DecimalFormat
import java.util.ArrayList


class ParentFinancialManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentFinancialManagementBinding
    private var firestore = Firebase.firestore
    private var bundle = Bundle()
    private lateinit var context: Context
    var balance = 0.00f
    var income = 0.00f
    var expense = 0.00f
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
        setUpChartTabs()
        loadBalance()
        loadFinancialHealth()
        goToParentTransactions()
        initializeDateButtons()
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
        //TODO: check if weekly button is still clicked, else remove color
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
        //TODO: check if weekly button is still clicked, else remove color
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
        //TODO: check if weekly button is still clicked, else remove color
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
        firestore.collection("Transactions")
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
            else if (transaction.transactionType == "Income")
                expense += transaction.amount!!
        }
    }

    private fun computeIncomeExpenseRatio() {
        val ratio = (income / expense * 100).toInt() // convert to percentage and round down to nearest integer
        val imageView = binding.ivScore
        val grade: String
        val bitmap: Bitmap

        if (ratio >= 200) {
            grade = "Excellent \uD83D\uDCB0 \n Your child is doing great! They have a good handle on their finances and are spending less than they earn."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.excellent)
        } else if (ratio >= 150 && ratio < 200) {
             grade = "Great \uD83D\uDC4D \n Your child is doing pretty well, but there is room for improvement. They are spending close to what they earn but could benefit from finding ways to increase their income or reduce their expenses so they can save more money."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.great)
        } else if (ratio >= 100 && ratio < 150) {
             grade = "Good \uD83D\uDE42 \n Your child is spending more than they earn and may be struggling to save money. It's important to help them find ways to cut back on expenses and increase their income, so they can achieve a healthier financial situation."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.good)
        } else if (ratio >= 50 && ratio < 100) {
             grade = "Average \uD83D\uDE10 \n Your child is spending significantly more than they earn and may be in danger of accumulating debt. It's important to take action to help them reduce their expenses and increase their income as soon as possible."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.average)
        } else {
             grade = "Bad \uD83D\uDCB8 \n Your child is spending far more than they earn and is likely accumulating debt. It's crucial to take immediate action to help them get their finances in order, including reducing expenses and finding ways to increase income."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        }

        imageView.setImageBitmap(bitmap)
        binding.tvScore.text = grade
    }

    private fun loadExplanation() {
        binding.btnExplanation.setOnClickListener {
            val activity = context as FragmentActivity
            val fm: FragmentManager = activity.supportFragmentManager
            val dialogFragment = ExplanationFragment()
            dialogFragment.show(fm, "fragment_alert")
        }
    }


    private fun loadBalance() {
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        /*firestore.collection("ChildWallet").whereEqualTo("childID", "eWZNOIb9qEf8kVNdvdRzKt4AYrA2")
            .get().addOnSuccessListener { document ->
                val childWallet = document.documents[0].toObject<ChildWallet>()
                val dec = DecimalFormat("#,###.00")
                balance = childWallet?.currentBalance!!
                val amount = dec.format(balance)
                binding.tvBalance.text = "₱$amount"
                initializeButtons()
            }*/
        val dec = DecimalFormat("#,###.00")
        //TODO: change to real wallet?
        balance = 150.00f
        val amount = dec.format(balance)
        binding.tvCurrentBalanceOfChild.text = "₱$amount"
        goToSendMoney()
    }



    private fun goToParentTransactions() {
        binding.btnViewTransactions.setOnClickListener {
            val goToTransactions = Intent(applicationContext, TransactionHistoryActivity::class.java)
            bundle.putString("user", "parent")
            goToTransactions.putExtras(bundle)
            startActivity(goToTransactions)
        }
    }

    private fun goToSendMoney() {
        binding.btnSendMoney.setOnClickListener {
            val goToSendMoney = Intent(applicationContext, ParentSendMoneyActivity::class.java)
            Toast.makeText(this, "balance"+balance, Toast.LENGTH_SHORT).show()
            bundle.putFloat("balance", balance)
            goToSendMoney.putExtras(bundle)
            startActivity(goToSendMoney)
        }
    }
}