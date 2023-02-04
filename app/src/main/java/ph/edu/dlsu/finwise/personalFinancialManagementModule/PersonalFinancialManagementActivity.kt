package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.PFMBreakdownAdapter
import ph.edu.dlsu.finwise.adapter.TransactionsAdapter
import ph.edu.dlsu.finwise.databinding.ActivityPersonalFinancialManagementBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.personalFinancialManagementModule.breakdownFragments.ExpenseFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.breakdownFragments.IncomeFragment
import java.text.DecimalFormat
import java.util.*


class PersonalFinancialManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalFinancialManagementBinding
    private var firestore = Firebase.firestore
    private lateinit var savingsLineGraphView: GraphView
    private lateinit var balanceLineGraphView: GraphView
    private var transactionsArrayList = ArrayList<Transactions>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalFinancialManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)

        setUpBreakdownTabs()
        loadBalance()
        //getTransactions()
        initializeSavingsLineGraphData()
        goToDepositGoalActivity()
        goToIncomeActivity()
        goToExpenseActivity()
        goToTransactionHistory()

    }

    private fun initializeSavingsLineGraphData() {
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


    }



    private fun setUpBreakdownTabs() {
        val adapter = PFMBreakdownAdapter(supportFragmentManager)
        adapter.addFragment(IncomeFragment(), "Income")
        adapter.addFragment(ExpenseFragment(), "Expense")
        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)

        binding.tabs.getTabAt(0)?.text = "Income"
        binding.tabs.getTabAt(1)?.text = "Expense"
    }

    private fun loadBalance() {
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        firestore.collection("ChildWallet").whereEqualTo("childID", "eWZNOIb9qEf8kVNdvdRzKt4AYrA2")
            .get().addOnSuccessListener { documents ->
                lateinit var id: String
                for (document in documents) {
                    id = document.id
                }
                firestore.collection("ChildWallet").document(id).get()
                    .addOnSuccessListener { document ->
                    val balance = document.toObject<ChildWallet>()
                    val dec = DecimalFormat("#,###.00")
                    binding.tvBalance.text = "₱"+dec.format(balance?.currentBalance)
                }
            }

    }

    private fun goToTransactionHistory() {
        binding.tvViewAll.setOnClickListener {
            val goToDepositGoalActivity = Intent(applicationContext, TransactionHistoryActivity::class.java)
            startActivity(goToDepositGoalActivity)
        }
    }

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