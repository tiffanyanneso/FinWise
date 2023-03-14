package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.personalFinancialManagementModule.mayaAPI.MayaPayment
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.PFMAdapter
import ph.edu.dlsu.finwise.databinding.ActivityPersonalFinancialManagementBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningMenuActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.SavingsFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.BalanceFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.ExplanationFragment
import java.text.DecimalFormat
import java.util.ArrayList



class PersonalFinancialManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalFinancialManagementBinding
    private var firestore = Firebase.firestore
    private var bundle = Bundle()
    private lateinit var context: Context
    private var childID  = FirebaseAuth.getInstance().currentUser!!.uid

    var balance = 0.00f
    var income = 0.00f
    var expense = 0.00f

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
            }
    }

    private fun loadExplanation() {
        binding.btnExplanation.setOnClickListener {
            val activity = context as FragmentActivity
            val fm: FragmentManager = activity.supportFragmentManager
            val dialogFragment = ExplanationFragment()
            dialogFragment.show(fm, "fragment_alert")
        }
    }

    private fun computeIncomeExpenseRatio() {
        val ratio = (income / expense * 100).toInt() // convert to percentage and round down to nearest integer
        val imageView = binding.ivScore
        val grade: String
        val performance: String
        val bitmap: Bitmap

        Log.d("asdfdsa", "computeIncomeExpenseRatio: "+income)
        Log.d("asdfdsa", "computeIncomeExpenseRatio: "+expense)

        if (ratio >= 180) {
            performance = "Excellent!"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.dark_green))
            grade = "Your income is much more than your expenses, and you're saving a large amount of money. You're doing an amazing job!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.excellent)
        } else if (ratio in 160..180) {
            performance = "Amazing!"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.green))
            grade = "Your income is significantly more than your expenses, and you're saving a substantial amount of money. Keep it up and look for ways to invest your money wisely."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.great)
        } else if (ratio in 140..159) {
            performance = "Great!"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.light_green))
            grade = "Your income is much more than your expenses, and you're saving a good amount of money. You're on the right track!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.good)
        } else if (ratio in 120..139) {
            binding.tvPerformance.setTextColor(resources.getColor(R.color.yellow))
            performance = "Good!"
            grade = "Your income is more than your expenses, and you're saving a decent amount of money. Keep it up!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.average)
        } else if (ratio in 100..119) {
            performance = "Average"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.red))
            grade = "Your income is slightly more than your expenses, and you're saving a small amount of money. Keep it up and look for ways to increase your income and savings."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        } else if (ratio in 80..99) {
            performance = "Nearly There"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.red))
            grade = "Your income and expenses are about the same, and you're not saving much money. You need to look for ways to increase your income and reduce your expenses."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        } else if (ratio in 60..79) {
            performance = "Almost There"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.red))
            grade = "Your expenses are slightly more than your income, and you're saving a little bit of money. Try to reduce your expenses further to save more money."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        } else if (ratio in 40..59) {
            performance = "Getting There"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.red))
            grade = "Your expenses are more than your income, and you're barely saving any money. You need to cut down on your expenses to start saving money."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        } else if (ratio in 20..39) {
            performance = "Not Quite There"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.red))
            grade = "Your expenses are much more than your income, and you're not saving any money. You need to make some changes to your spending habits."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        } else if (ratio in 1..19) {
            performance = "Need Improvement"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.red))
            grade = "Your expenses are more than your income. This means that you're in trouble and need to take action right away to cut down your expenses."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        }
        else {
            performance = "Bad...!"
            binding.tvPerformance.setTextColor(resources.getColor(R.color.red))
            grade = "You have 0 balance. Click the income button above to add your money "
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.average)
        }

        imageView.setImageBitmap(bitmap)
        binding.tvScore.text = grade
        binding.tvPerformance.text = performance

    }

    private fun getIncomeAndExpense(transactionsArrayList: ArrayList<Transactions>) {
        for (transaction in transactionsArrayList) {
            if (transaction.transactionType == "Income")
                income += transaction.amount!!
            else if (transaction.transactionType == "Income")
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
        goToPayMaya()
        goToEarningActivity()
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
                val childWallet = document.documents[0].toObject<ChildWallet>()
                var amount = DecimalFormat("#,##0.00").format(childWallet?.currentBalance!!)
                if (childWallet.currentBalance!! < 0.00F)
                    amount = "0.00"
                balance = childWallet.currentBalance!!
                binding.tvBalance.text = "₱$amount"
                initializeButtons()
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
    private fun goToPayMaya() {
        binding.btnPayWithMaya.setOnClickListener {
            val goToTransactions = Intent(applicationContext, MayaPayment::class.java)
            getBundles()
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
            bundle.putString("user", "child")
            goToEarningActivity.putExtras(bundle)
            startActivity(goToEarningActivity)
        }
    }

    private fun getBundles() {
        bundle.putFloat("balance", balance)
        bundle.putString("childID", childID)
    }

    /* private fun initializeBalanceBarGraph() {
      firestore.collection("Transactions")
          .get().addOnSuccessListener { documents ->

              val dates = ArrayList<Date>()

              for (transactionSnapshot in documents) {
                  //creating the object from list retrieved in db
                  val transaction = transactionSnapshot.toObject<Transactions>()
                  transactionsArrayList.add(transaction)
              }
              transactionsArrayList.sortByDescending { it.date }

              //get unique dates in transaction arraylist
              for (transaction in transactionsArrayList) {
                  //if array of dates doesn't contain date of the transaction, add the date to the arraylist
                  if (!dates.contains(transaction.date?.toDate()))
                      dates.add(transaction.date?.toDate()!!)
              }

              val sortedDate = dates.sortedBy { it }

              //get deposit for a specific date
              var xAxisValue = 1.00f
              for (date in sortedDate) {
                  var incomeTotal = 0.00F
                  var expenseTotal = 0.00F
                  for (transaction in transactionsArrayList) {
                      //comparing the dates if they are equal
                      if (date.compareTo(transaction.date?.toDate()) == 0) {
                          if (transaction.transactionType == "Income")
                              incomeTotal += transaction.amount!!
                          else
                              expenseTotal += transaction.amount!!
                      }
                  }
                  barEntriesIncome.add(BarEntry(xAxisValue, incomeTotal))
                  barEntriesExpense.add(BarEntry(xAxisValue, expenseTotal))
                  xAxisValue++
              }

              // Convert date object to string array
              var x = 1
              for (d in sortedDate) {
                  val formatter = SimpleDateFormat("MM/dd/yyyy")
                  val date = formatter.format(d).toString()
                  datesBar.add(date)
                  Log.d("DATESS", date+x)
                  x++
              }

              // initializing variable for bar chart.
              barChart = findViewById(ph.edu.dlsu.finwise.R.id.bar_chart_balance)

              // creating a new bar data set.
              barDataSet1 = BarDataSet(barEntriesIncome, "Income")
              barDataSet1!!.color = applicationContext.resources.getColor(ph.edu.dlsu.finwise.R.color.dark_green)
              barDataSet2 = BarDataSet(barEntriesExpense, "Expense")
              barDataSet2!!.color = applicationContext.resources.getColor(ph.edu.dlsu.finwise.R.color.red)

              // below line is to add bar data set to our bar data.
              val data = BarData(barDataSet1, barDataSet2)

              // after adding data to our bar data we
              // are setting that data to our bar chart.
              barChart?.data = data


              // below line is to remove description
              // label of our bar chart.
              barChart?.description?.isEnabled = false


              // below line is to get x axis
              // of our bar chart.
              val xAxis = barChart?.xAxis

              // below line is to set value formatter to our x-axis and
              // we are adding our days to our x axis.
              xAxis?.valueFormatter = IndexAxisValueFormatter(datesBar)


              // below line is to set center axis
              // labels to our bar chart.
              xAxis?.setCenterAxisLabels(true)


              // below line is to set position
              // to our x-axis to bottom.
              xAxis?.position = XAxis.XAxisPosition.BOTTOM


              // below line is to set granularity
              // to our x axis labels.
              xAxis?.granularity = 1f


              // below line is to enable
              // granularity to our x axis.
              xAxis?.isGranularityEnabled = true


              // below line is to make our
              // bar chart as draggable.
              barChart?.isDragEnabled = true


              // below line is to make visible
              // range for our bar chart.
              barChart?.setVisibleXRangeMaximum(3f)

              // below line is to add bar
              // space to our chart.
              val barSpace = 0.1f

              // below line is use to add group
              // spacing to our bar chart.
              val groupSpace = 0.5f

              // we are setting width of
              // bar in below line.
              data.barWidth = 0.15f

              // below line is to set minimum
              // axis to our chart.
              barChart?.xAxis?.axisMinimum = 0f

              // below line is to
              // animate our chart.
              barChart?.animate()

              // below line is to group bars
              // and add spacing to it.
              barChart?.groupBars(0f, groupSpace, barSpace)

              // below line is to invalidate
              // our bar chart.
              barChart?.invalidate()
          }
  }*/

    /*   private fun initializeSavingsLineGraphData() {
            // on below line we are initializing
            // our variable with their ids.
            firestore.collection("Transactions")
                .whereEqualTo("category", "Goal")
                .get().addOnSuccessListener { documents ->
                savingsLineGraphView = findViewById(R.id.savings_line_graph)

                val dates = ArrayList<Date>()
                val dataPoints = ArrayList<DataPoint>()

                for (transactionSnapshot in documents) {
                    //creating the object from list retrieved in db
                    val transaction = transactionSnapshot.toObject<Transactions>()
                    transactionsArrayList.add(transaction)
                }
                transactionsArrayList.sortByDescending { it.date }

                //get unique dates in transaction arraylist
                for (transaction in transactionsArrayList) {
                    //if array of dates doesn't contain date of the transaction, add the date to the arraylist
                    if (!dates.contains(transaction.date?.toDate()))
                        dates.add(transaction.date?.toDate()!!)
                }

                val sortedDate = dates.sortedBy { it }
                //get deposit for a specific date
                var xAxis =0.00
                for (date in sortedDate) {
                    var depositTotal = 0.00F
                    for (transaction in transactionsArrayList) {
                        //comparing the dates if they are equal
                        if (transaction != null ) {
                            if (date.compareTo(transaction.date?.toDate()) == 0) {
                                if (transaction.transactionType == "Deposit")
                                    depositTotal += transaction.amount!!
                                else
                                    depositTotal -= transaction.amount!!
                            }
                        }
                    }
                    dataPoints.add(DataPoint(xAxis, depositTotal.toDouble()))
                    xAxis++
                }

                //plot data to
                // on below line we are adding data to our graph view.
                val series: LineGraphSeries<DataPoint> = LineGraphSeries(dataPoints.toTypedArray())

                // on below line adding animation
                savingsLineGraphView.animate()

                // on below line we are setting scrollable
                // for point graph view
                savingsLineGraphView.viewport.isScrollable = true

                // on below line we are setting scalable.
                savingsLineGraphView.viewport.isScalable = true

                // on below line we are setting scalable y
                savingsLineGraphView.viewport.setScalableY(true)

                // on below line we are setting scrollable y
                savingsLineGraphView.viewport.setScrollableY(true)

                // on below line we are setting color for series.
                series.color = R.color.teal_200

                // on below line we are adding
                // data series to our graph view.
                savingsLineGraphView.addSeries(series)
            }


        }*/
    /*private fun goToTransactionHistory() {
           binding.tvViewAll.setOnClickListener {
               val goToDepositGoalActivity = Intent(applicationContext, TransactionHistoryActivity::class.java)
               startActivity(goToDepositGoalActivity)
           }
       }*/

    /*private fun getTransactions() {
        var transactionIDArrayList = ArrayList<String>()
        //TODO:change to get transactions of current user
        // Dito may collection na transaction at hindi nakanest yung collection sa user
        //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        //firestore.collection("Transactions").whereEqualTo("transactionID", currentUser).get()
        // .addOnSuccessListener{ documents ->

        firestore.collection("Transactions").get().addOnSuccessListener { documents ->
            for (transactionSnapshot in documents) {
                //creating the object from list retrieved in db
                val transactionID = transactionSnapshot.id
                transactionIDArrayList.add(transactionID)
            }
            //transactionsArrayList.sortByDescending { it.date }
            transactionAdapter = TransactionsAdapter(applicationContext, transactionIDArrayList)
            binding.rvViewTransactions.adapter = transactionAdapter
            binding.rvViewTransactions.layoutManager = LinearLayoutManager(applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
        }
    }*/

}