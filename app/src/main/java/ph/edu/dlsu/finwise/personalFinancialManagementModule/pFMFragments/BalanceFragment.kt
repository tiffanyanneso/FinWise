package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentBalanceChartBinding
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.personalFinancialManagementModule.TrendDetailsActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


class BalanceFragment : Fragment(R.layout.fragment_balance_chart) {
    private lateinit var binding: FragmentBalanceChartBinding
    private var bundle = Bundle()
    private var firestore = Firebase.firestore
    private var childID  = FirebaseAuth.getInstance().currentUser!!.uid
    private var transactionsArrayList = ArrayList<Transactions>()
    private lateinit var sortedDate: List<Date>
    private lateinit var selectedDates: List<Date>
    private var weeks: Map<Int, List<Date>>? = null
    private var months: Map<Int, List<Date>>? = null
    private var selectedDatesSort = "weekly"
    private var user = "child"
    private lateinit var chart: LineChart
    var graphData = mutableListOf<Entry>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_balance_chart, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBalanceChartBinding.bind(view)
        getArgumentsFromPFM()
        initializeBalanceLineGraph()
        initializeDetails()

    }


    private fun getArgumentsFromPFM() {
        val args = arguments
        val date = args?.getString("date")
        val currUser = args?.getString("user")
        val child = args?.getString("childID")

        if (child != null) {
            childID = child
        }

        if (currUser != null) {
            user = currUser
        }


        if (date != null) {
            selectedDatesSort = date
            transactionsArrayList.clear()
        }
    }

    private fun initializeDetails() {
        binding.btnDetails.setOnClickListener{
            val goToDetails = Intent(context, TrendDetailsActivity::class.java)
            bundle.putString("date", selectedDatesSort)
            bundle.putString("user", user)
            if (user == "child")
                childID  = FirebaseAuth.getInstance().currentUser!!.uid

            bundle.putString("childID", childID)
            goToDetails.putExtras(bundle)
            startActivity(goToDetails)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeBalanceLineGraph() {
        // on below line we are initializing
        // our variable with their ids.
        firestore.collection("Transactions").whereEqualTo("createdBy", childID)
            .get().addOnSuccessListener { documents ->
                initializeTransactions(documents)
                sortedDate = getDatesOfTransactions()
                setData()
                initializeGraph()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData(): MutableList<Entry> {

        when (selectedDatesSort) {
            "weekly" -> {
                selectedDates = getDaysOfWeek(sortedDate)
                graphData = addWeeklyData(selectedDates)
                binding.tvBalanceTitle.text = "This Week's Balance Trend"
            }
            "monthly" -> {
                weeks = getWeeksOfCurrentMonth(sortedDate)
                graphData = iterateWeeksOfCurrentMonth(weeks!!)
                /*val group = groupDates(sortedDate, "month")
                iterateDatesByQuarter(group)*/
                binding.tvBalanceTitle.text = "This Month's Balance Trend"
            }
            "quarterly" -> {
                months = getMonthsOfQuarter(sortedDate)
                graphData =  forEachDateInMonths(months!!)
                binding.tvBalanceTitle.text = "This Quarter's Balance Trend"
            }
        }
        return graphData
    }

    private fun iterateWeeksOfCurrentMonth(weeks: Map<Int, List<Date>>): MutableList<Entry> {
        val data = mutableListOf<Entry>()
        var totalIncome = 0.00f
        var totalExpense= 0.00f
        var xAxisPoint =0.00f

        weeks.forEach { (weekNumber, datesInWeek) ->
            var totalAmount = 0.00F
            datesInWeek.forEach { date ->
                for (transaction in transactionsArrayList) {
                    if (date.compareTo(transaction.date?.toDate()) == 0) {
                        if (transaction.transactionType == "Income"){
                            totalAmount += transaction.amount!!
                            totalIncome += transaction.amount!!
                        }
                        else {
                            totalAmount -= transaction.amount!!
                            totalExpense += transaction.amount!!
                        }
                    }
                }
            }
            data.add(Entry(xAxisPoint, totalAmount))
            xAxisPoint++
        }
        setTotals(totalIncome, totalExpense)
        return data
    }

    private fun getMonthsOfQuarter(dates: List<Date>): Map<Int, List<Date>> {
        // Get the current quarter
        // TODO: double check kung bat hindi kasama april + MAY BUTTON SA TERND TAPO SREDIERECT SA DETAILS LIKE PIE CHART, TOP 3, AT TRANSACTIONS WITHIN GTTHOSE DATES
        val currentQuarter = (Calendar.getInstance().get(Calendar.MONTH) / 3) + 1

        // Group the dates by month for the current quarter
        val groupedDates = dates.groupBy { date ->
            val calendar = Calendar.getInstance()
            calendar.time = date

            // Check if the date falls within the current quarter
            val quarter = (calendar.get(Calendar.MONTH) / 3) + 1
            if (quarter == currentQuarter) {
                // Get the month of the date
                val month = calendar.get(Calendar.MONTH) + 1

                // Return the month number as the key
                month
            } else {
                // If the date does not fall within the current quarter, return null
                null
            }
        }

        return groupedDates.filterKeys { it != null } as Map<Int, List<Date>>
    }

    private fun forEachDateInMonths(months: Map<Int, List<Date>>): MutableList<Entry> {
        val data = mutableListOf<Entry>()
        var totalIncome = 0.00f
        var totalExpense= 0.00f
        var xAxisPoint =0.00f

        for ((month, datesInMonth) in months) {
            var totalAmount = 0.00F
            for (date in datesInMonth) {
                for (transaction in transactionsArrayList) {
                    if (date.compareTo(transaction.date?.toDate()) == 0) {
                        if (transaction.transactionType == "Income"){
                            totalAmount += transaction.amount!!
                            totalIncome += transaction.amount!!
                        }
                        else {
                            totalAmount -= transaction.amount!!
                            totalExpense += transaction.amount!!
                        }
                    }
                }
            }
            data.add(Entry(xAxisPoint, totalAmount))
            xAxisPoint++
        }
        setTotals(totalIncome, totalExpense)
        return data
    }

    private fun getWeeksOfCurrentMonth(dates: List<Date>): Map<Int, List<Date>> {
        val calendar = Calendar.getInstance()

        // Get the current month and year
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        // Filter the dates that belong to the current month and year
        val filteredDates = dates.filter { date ->
            calendar.time = date
            calendar.get(Calendar.MONTH) == currentMonth &&
                    calendar.get(Calendar.YEAR) == currentYear
        }

        // Group the filtered dates by week
        return filteredDates.groupBy { date ->
            calendar.time = date
            calendar.get(Calendar.WEEK_OF_MONTH)
        }
    }

    private fun addWeeklyData(selectedDates: List<Date>): MutableList<Entry> {
        //get deposit for a specific date
        val data = mutableListOf<Entry>()

        var totalIncome = 0.00f
        var totalExpense= 0.00f
        var xAxisPoint =0.00f
        for (date in selectedDates) {
            var totalAmount = 0.00F
            for (transaction in transactionsArrayList) {
                //comparing the dates if they are equal
                if (date.compareTo(transaction.date?.toDate()) == 0) {
                    if (transaction.transactionType == "Income"){
                        totalAmount += transaction.amount!!
                        totalIncome += transaction.amount!!
                    }
                    else {
                        totalAmount -= transaction.amount!!
                        totalExpense += transaction.amount!!
                    }
                }
            }
            data.add(Entry(xAxisPoint, totalAmount))
            //dataPoints.add(DataPoint(xAxisPoint, totalAmount.toDouble()))
            xAxisPoint++
        }

        setTotals(totalIncome, totalExpense)
        return data
    }

    private fun setTotals(totalIncome: Float, totalExpense: Float) {
        val dec = DecimalFormat("#,###.00")
        /*val incomeText = dec.format(totalIncome)
        val expenseText = dec.format(totalExpense)*/
        val netIncome = totalIncome - totalExpense
        var netIncomeText = dec.format(netIncome)

        /*binding.tvIncomeTotal.text = "₱$incomeText"
        binding.tvExpenseTotal.text = "₱$expenseText"*/
        if (netIncome > 0 && user == "child")
            binding.tvSummary.text = "You've earned ₱$netIncomeText more than you spent 😄"
        else if (netIncome > 0 && user == "parent") {
            binding.tvSummary.text = "Your child earned ₱$netIncomeText more than they spent 😄"
        } else if (netIncome < 0 && user == "child") {
            netIncomeText = kotlin.math.abs(netIncome).toString()
            binding.tvSummary.text = "You've spent ₱$netIncomeText more than you earned 😞"
        } else if (netIncome < 0 && user == "parent") {
            netIncomeText = kotlin.math.abs(netIncome).toString()
            binding.tvSummary.text = "Your child spent ₱$netIncomeText more than they earned 😞"
        }

    }

    private fun getDatesOfTransactions(): List<Date> {
//get unique dates in transaction arraylist
        val dates = ArrayList<Date>()

        /*for (transaction in transactionsArrayList) {
            //if array of dates doesn't contain date of the transaction, add the date to the arraylist
            if (!dates.contains(transaction.date?.toDate()))
                dates.add(transaction.date?.toDate()!!)
        }*/
        val sortedList = transactionsArrayList.sortedBy { it.date }
        val uniqueList = sortedList.distinctBy { it.date }

        for (obj in uniqueList) {
            dates.add(obj.date?.toDate()!!)
        }

        return dates
    }

    private fun initializeTransactions(documents: QuerySnapshot) {
        for (transactionSnapshot in documents) {
            //creating the object from list retrieved in db
            val transaction = transactionSnapshot.toObject<Transactions>()
            transactionsArrayList.add(transaction)
        }
        transactionsArrayList.sortByDescending { it.date }
    }

    private fun updateXAxisWeekly(xAxis: XAxis?) {
        val dateFormatter = SimpleDateFormat("EEEE")
        val dates = selectedDates.distinct()

        if (dates.size < graphData.size) {
            // There are fewer dates than xAxis entries, reduce the number of xAxis entries
            graphData = graphData.take(dates.size) as MutableList<Entry>
        }
        xAxis?.valueFormatter = IndexAxisValueFormatter(dates.map { dateFormatter.format(it) }.toTypedArray())

        /*xAxis?.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return if (value.toInt() < dates.size) {
                    dateFormatter.format(dates[value.toInt()])
                } else {
                    ""
                }
            }
        }*/

        /*xAxis?.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value.toInt() < dates.size) {
                    dateFormatter.format(dates[value.toInt()])
                } else {
                    ""
                }
            }
        }*/
    }

    private fun updateXAxisMonthly(xAxis: XAxis) {
        val dateMap = weeks // Your date map here

// Create a list of week labels from the date map
        val weekLabels = mutableListOf<String>()
        for (weekDates in dateMap!!.values) {
            val weekNo = weekLabels.size + 1
            val weekSuffix = if (weekNo % 10 == 1 && weekNo != 11) "st"
            else if (weekNo % 10 == 2 && weekNo != 12) "nd"
            else if (weekNo % 10 == 3 && weekNo != 13) "rd"
            else "th"
            val weekLabel = "${weekNo}${weekSuffix} Week"
            weekLabels.add(weekLabel)
        }

        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value.toInt() < weekLabels.size) weekLabels[value.toInt()] else ""
            }
        }


    }

    private fun updateXAxisQuarterly(xAxis: XAxis?) {
// quarter
        // Create an array of month names
        val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

// Create a list of x-axis values
        val xAxisValues = mutableListOf<Float>()
        var i = 0f
        for (monthList in months!!.values) {
            xAxisValues.add(i + (monthList.size - 1f) / 2f)
            i += monthList.size.toFloat()
        }
        // configure the X-axis
        xAxis?.labelCount = 4
        xAxis?.isGranularityEnabled = true
        xAxis?.axisMinimum = 0f
        xAxis?.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                if (index >= 0 && index < monthNames.size) {
                    return monthNames[index]
                }
                return ""
            }
        }
    }

    private fun initializeGraph() {
        chart = view?.findViewById(R.id.balance_chart)!!


        val xAxis = chart.xAxis
            xAxis.granularity = 1f // Set a smaller granularity if there are fewer data points

        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // TODO: Fix becuase this only applies when dates of the week are empty
        if (selectedDates.isNotEmpty()) {
            when (selectedDatesSort) {
                "weekly" -> updateXAxisWeekly(xAxis)
                "monthly" -> updateXAxisMonthly(xAxis)
                "quarterly" -> updateXAxisQuarterly(xAxis)
            }
        } else Toast.makeText(context, "No data available", Toast.LENGTH_SHORT).show()



        val yAxis = chart.axisLeft
        yAxis.setDrawLabels(true) // Show X axis labels
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("₱%.0f", value)// Add "₱" symbol to value
            }
        }

        // Create a dataset from the data
        val dataSet = LineDataSet(graphData, "Balance")
        dataSet.color = R.color.red
        dataSet.setCircleColor(R.color.teal_200)
        dataSet.valueTextSize = 14f


        //Set the data on the chart and customize it
        val lineData = LineData(dataSet)
        chart.data = lineData

        // on below line adding animation
        chart.animate()

        chart.setPinchZoom(true)
        chart.setScaleEnabled(false)

        chart.axisRight.isEnabled = false

        chart.description.isEnabled = false

        // Add a Peso sign in the data points in the graph
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("₱%.2f", value) // add the ₱ character to the data point values
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDaysOfWeek(dates: List<Date>): List<Date> {
        val startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY)
        val endOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY)
        val filteredDates = dates.filter { date ->
            val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            !localDate.isBefore(startOfWeek) && !localDate.isAfter(endOfWeek)
        }

        return filteredDates
    }

    /*private fun addData(groups: Map<Int, List<Date>>): MutableList<Entry> {
        val data = mutableListOf<Entry>()
        var totalIncome = 0.00f
        var totalExpense= 0.00f
        var xAxisPoint =0.00f

        for ((x, dates) in groups) {
            var totalAmount = 0.00F
            for (date in dates) {
                for (transaction in transactionsArrayList) {
                    if (date.compareTo(transaction.date?.toDate()) == 0) {
                        if (transaction.transactionType == "Income"){
                            totalAmount += transaction.amount!!
                            totalIncome += transaction.amount!!
                        }
                        else {
                            totalAmount -= transaction.amount!!
                            totalExpense += transaction.amount!!
                        }
                    }
                }
            }
            data.add(Entry(xAxisPoint, totalAmount))
            xAxisPoint++
        }
        setTotals(totalIncome, totalExpense)
        return data
    }*/

/*    private fun groupDates(dates: List<Date>, range: String): Map<Int, List<Date>> {
        val calendar = Calendar.getInstance()
        val groups = mutableMapOf<Int, MutableList<Date>>()
        for (date in dates) {
            calendar.time = date
            var dateRange = calendar.get(Calendar.WEEK_OF_YEAR)
            if (range == "quarterly")
                dateRange = (calendar.get(Calendar.MONTH) / 3) + 1


            if (!groups.containsKey(dateRange)) {
                groups[dateRange] = mutableListOf(date)
            } else {
                groups[dateRange]?.add(date)
            }
        }

        return groups
    }*/


    /*  private fun initializeBalanceBarGraph() {
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

  */
}