package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentBalanceBarChartBinding
import ph.edu.dlsu.finwise.model.Transactions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class BalanceFragment : Fragment(R.layout.fragment_balance_bar_chart) {
    private lateinit var binding: FragmentBalanceBarChartBinding
    private var firestore = Firebase.firestore
    private var transactionsArrayList = ArrayList<Transactions>()

    // Balance bar chart
    // variable for our bar chart
    private var barChart: BarChart? = null

    // variable for bar data set.
    private var barDataSet1: BarDataSet? = null
    // variable for bar data set.
    private var barDataSet2: BarDataSet? = null

    // array list for storing entries.
    private var barEntriesIncome = ArrayList<BarEntry>()
    private var barEntriesExpense = ArrayList<BarEntry>()

    // creating a string array for displaying days.
    private var datesBar = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_balance_bar_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBalanceBarChartBinding.bind(view)
        initializeBalanceBarGraph()
    }

    private fun initializeBalanceBarGraph() {
        firestore.collection("Transactions")
            .get().addOnSuccessListener { documents ->
                loadTransactions(documents)
                val sortedDate = getDates()

                getTransactionData(sortedDate)

                convertDateToString(sortedDate)

                val data = createBarDataSet()

                loadBarChart(data)
            }
    }

    private fun loadBarChart(data: BarData) {
        // initializing variable for bar chart.
        barChart = view?.findViewById(R.id.bar_chart_balance)

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

    private fun createBarDataSet(): BarData {
        // creating a new bar data set.
        barDataSet1 = BarDataSet(barEntriesIncome, "Income")
        barDataSet1!!.color = resources.getColor(ph.edu.dlsu.finwise.R.color.dark_green)
        barDataSet2 = BarDataSet(barEntriesExpense, "Expense")
        barDataSet2!!.color = resources.getColor(ph.edu.dlsu.finwise.R.color.red)

        // below line is to add bar data set to our bar data.
        return BarData(barDataSet1, barDataSet2)
    }

    private fun convertDateToString(sortedDate: List<Date>) {
        // Convert date object to string array
        for (d in sortedDate) {
            val formatter = SimpleDateFormat("MM/dd/yyyy")
            val date = formatter.format(d).toString()
            datesBar.add(date)
        }
    }

    private fun getTransactionData(sortedDate: List<Date>) {
        //get data for loaded dates
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
    }

    private fun loadTransactions(documents: QuerySnapshot) {
        for (transactionSnapshot in documents) {
            //creating the object from list retrieved in db
            val transaction = transactionSnapshot.toObject<Transactions>()
            transactionsArrayList.add(transaction)
        }
        transactionsArrayList.sortByDescending { it.date }
    }

    private fun getDates(): List<Date> {
        val dates = ArrayList<Date>()

        for (transaction in transactionsArrayList) {
            //if array of dates doesn't contain date of the transaction, add the date to the arraylist
            if (!dates.contains(transaction.date?.toDate()))
                dates.add(transaction.date?.toDate()!!)
        }
         return dates.sortedBy { it }
    }


}