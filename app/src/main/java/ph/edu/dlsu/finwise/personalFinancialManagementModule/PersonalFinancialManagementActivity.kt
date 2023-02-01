package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityPersonalFinancialManagementBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import java.text.DecimalFormat


class PersonalFinancialManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalFinancialManagementBinding
    private var firestore = Firebase.firestore
    lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalFinancialManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)


        loadBalance()
        loadIncomePieChart()
        loadExpensePieChart()
        //getTransactions()
        goToDepositGoalActivity()
        goToIncomeActivity()
        goToExpenseActivity()
        goToTransactionHistory()
    }

    private fun loadIncomePieChart() {
        loadPieChart(R.id.pc_income, "income")
    }
    private fun loadExpensePieChart() {
        loadPieChart(R.id.pc_expense, "expense")
    }
    private fun loadPieChart(pieChartLayout: Int, category: String) {

        pieChart = findViewById(pieChartLayout)
        //  setting user percent value, setting description as enabled, and offset for pie chart
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        // disable hole in pie
        pieChart.isDrawHoleEnabled = false

        // setting drag for the pie chart
        //pieChart.dragDecelerationFrictionCoef = 0.95f

        // setting circle color and alpha
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        // setting center text
        pieChart.setDrawCenterText(true)

        // setting rotation for our pie chart
        //pieChart.rotationAngle = 0f

        // enable rotation of the pieChart by touch
        pieChart.isRotationEnabled = false
        pieChart.isHighlightPerTapEnabled = false

        // setting animation for our pie chart
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        // set legend
        /*var legend = pieChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.textSize = 15f
        legend.setDrawInside(false)
        legend.isEnabled = true*/

        //  creating array list and
        // adding data to it to display in pie chart
        // TODO: expense or income
        val entries: ArrayList<PieEntry> = ArrayList()
        if (category == "income") {
            entries.add(PieEntry(25f, "Allowance"))
            entries.add(PieEntry(10f, "Gift"))
            entries.add(PieEntry(2f, "Birthday"))
            entries.add(PieEntry(15f, "Reward"))
            entries.add(PieEntry(3f, "Other"))
        } else {
            entries.add(PieEntry(25f, "Food"))
            entries.add(PieEntry(10f, "Game"))
            entries.add(PieEntry(2f, "Toy"))
            entries.add(PieEntry(15f, "Transportation"))
            entries.add(PieEntry(3f, "Other"))
        }


        // setting pie data set
        val dataSet = PieDataSet(entries, "")

        // on below line we are setting icons.
        dataSet.setDrawIcons(true)

        // setting slice for pie
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors to list
        val colors: ArrayList<Int> = ArrayList()
        colors.add(ContextCompat.getColor(this, R.color.purple_200))
        colors.add(ContextCompat.getColor(this, R.color.yellow))
        colors.add(ContextCompat.getColor(this, R.color.red))
        colors.add(ContextCompat.getColor(this, R.color.dark_green))
        colors.add(ContextCompat.getColor(this, R.color.teal_200))

        // setting colors.
        dataSet.colors = colors

        // setting pie data set
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(12f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data

        // undo all highlights
        pieChart.highlightValues(null)

        // loading chart
        pieChart.invalidate()
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