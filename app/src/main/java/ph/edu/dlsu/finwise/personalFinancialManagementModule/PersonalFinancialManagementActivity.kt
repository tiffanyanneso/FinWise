package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.GraphView
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.adapter.PFMAdapter
import ph.edu.dlsu.finwise.databinding.ActivityPersonalFinancialManagementBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.ExpenseFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.SavingsFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.BalanceFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.IncomeFragment
import java.text.DecimalFormat
import kotlin.collections.ArrayList


class PersonalFinancialManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalFinancialManagementBinding
    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalFinancialManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(ph.edu.dlsu.finwise.R.id.bottom_nav), this, ph.edu.dlsu.finwise.R.id.nav_finance)

        setUpBreakdownTabs()
        setUpChartTabs()
        loadBalance()
        goToDepositGoalActivity()
        goToIncomeActivity()
        goToExpenseActivity()
        //getTransactions()
        //initializeBalanceBarGraph()
        //initializeSavingsBarGraph()
        //goToTransactionHistory()
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

    private fun setUpBreakdownTabs() {
        val adapter = PFMAdapter(supportFragmentManager)
        adapter.addFragment(IncomeFragment(), "Income")
        adapter.addFragment(ExpenseFragment(), "Expense")
        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)

        binding.tabs.getTabAt(0)?.text = "Income"
        binding.tabs.getTabAt(1)?.text = "Expense"
    }

    private fun setUpChartTabs() {
        val adapter = PFMAdapter(supportFragmentManager)
        adapter.addFragment(BalanceFragment(), "Balance")
        adapter.addFragment(SavingsFragment(), "Savings")
        binding.viewPagerBarCharts.adapter = adapter
        binding.tabsBarCharts.setupWithViewPager(binding.viewPagerBarCharts)

        binding.tabsBarCharts.getTabAt(0)?.text = "Balance"
        binding.tabsBarCharts.getTabAt(1)?.text = "Savings"
    }

    private fun loadBalance() {
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        firestore.collection("ChildWallet").document("eWZNOIb9qEf8kVNdvdRzKt4AYrA2").get()
            .addOnSuccessListener { document ->
            val balance = document.toObject<ChildWallet>()
            binding.tvBalance.text = "₱abc"+balance?.currentBalance
                Toast.makeText(this, balance?.currentBalance.toString(), Toast.LENGTH_SHORT).show()
        }


    }

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

    private fun goToDepositGoalActivity() {
        binding.btnGoal.setOnClickListener {
            val goToDepositGoalActivity = Intent(applicationContext, RecordDepositActivity::class.java)
            startActivity(goToDepositGoalActivity)
        }
    }

    private fun goToIncomeActivity() {
        binding.btnIncome.setOnClickListener {
            val goToIncomeActivity = Intent(applicationContext, RecordIncomeActivity::class.java)
            startActivity(goToIncomeActivity)
        }
    }

    private fun goToExpenseActivity() {
        binding.btnExpense.setOnClickListener {
            val goToExpenseActivity = Intent(applicationContext, RecordExpenseActivity::class.java)
            startActivity(goToExpenseActivity)
        }
    }

}