package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
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
import ph.edu.dlsu.finwise.databinding.FragmentSavingsChartBinding
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.personalFinancialManagementModule.GoalSavingDetailsActivity
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.util.*


class SavingsFragment : Fragment(R.layout.fragment_savings_chart) {
    private lateinit var binding: FragmentSavingsChartBinding
    private var bundle = Bundle()
    private var firestore = Firebase.firestore
    private var childID = FirebaseAuth.getInstance().currentUser!!.uid
    private var transactionsArrayList = ArrayList<Transactions>()
    private lateinit var sortedDate: List<Date>
    private lateinit var selectedDates: List<Date>
    private var weeks: Map<Int, List<Date>>? = null
    private var months: Map<Int, List<Date>>? = null
    private var selectedDatesSort = "weekly"
    private var user = "child"
    var graphData = mutableListOf<Entry>()



    /*// Balance bar chart
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
    private var datesBar = arrayListOf<String>()*/


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_savings_chart, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSavingsChartBinding.bind(view)

        getArgumentsFromPFM()
        initializeBalanceLineGraph()
        initializeDetails()
    }

    private fun getArgumentsFromPFM() {
        val args = arguments
        val child = args?.getString("childID")

        val date = args?.getString("date")

        val currUser = args?.getString("user")


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
            val goToDetails = Intent(context, GoalSavingDetailsActivity::class.java)
            bundle.putString("date", selectedDatesSort)
            bundle.putString("user", user)

            bundle.putString("childID", childID)
            goToDetails.putExtras(bundle)
            startActivity(goToDetails)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeBalanceLineGraph() {
        firestore.collection("Transactions").whereEqualTo("userID", childID)
            .get().addOnSuccessListener { documents ->
                initializeTransactions(documents)
                sortedDate = getDatesOfTransactions()
                setData()
                initializeGraph()
            }
    }

    private fun initializeTransactions(documents: QuerySnapshot) {
        for (transactionSnapshot in documents) {
            //creating the object from list retrieved in db
            val transaction = transactionSnapshot.toObject<Transactions>()
            transactionsArrayList.add(transaction)
        }
        transactionsArrayList.sortByDescending { it.date }
    }

    private fun getDatesOfTransactions(): List<Date> {
        // Step 1: Map the objects to a list of Date objects
        val dates = transactionsArrayList.map { it.date!!.toDate() }

        // Step 2: Create a new list to store the unique dates
        val uniqueDates = mutableListOf<Date>()

        // Step 3-4: Loop through the dates and add unique dates to the list
        for (date in dates) {
            val uniqueDate = Date(date.year, date.month, date.date, 0, 0, 0)
            if (!uniqueDates.contains(uniqueDate)) {
                uniqueDates.add(uniqueDate)
            }
        }

        return uniqueDates.sorted()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData() {


        when (selectedDatesSort) {
            "weekly" -> {
                selectedDates = getDaysOfWeek(sortedDate)
                graphData = addWeeklyData(selectedDates)
                binding.tvBalanceTitle.text = "This Week's Savings Balance Trend"
            }
            "monthly" -> {
                weeks = getWeeksOfCurrentMonth(sortedDate)
                graphData = iterateWeeksOfCurrentMonth(weeks!!)
                binding.tvBalanceTitle.text = "This Month's Savings Balance Trend"
            }
            "quarterly" -> {
                months = getMonthsOfQuarter(sortedDate)
                Log.d("zzzzz", "setData: "+months)
                graphData =  forEachDateInMonths(months!!)
                binding.tvBalanceTitle.text = "This Quarter's Savings Balance Trend"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDaysOfWeek(dates: List<Date>): List<Date> {
        val weekStart = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.SUNDAY
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val weekEnd = weekStart.clone() as Calendar
        weekEnd.add(Calendar.DAY_OF_MONTH, 6)

        val currentWeekDates = mutableListOf<Date>()
        dates.forEach { date ->
            val calendar = Calendar.getInstance().apply { time = date }
            if (!calendar.before(weekStart) && !calendar.after(weekEnd)) {
                currentWeekDates.add(calendar.time)
            }
        }

        if (!currentWeekDates.contains(weekStart.time)) {
            currentWeekDates.add(weekStart.time)
        }
        if (!currentWeekDates.contains(weekEnd.time)) {
            currentWeekDates.add(weekEnd.time)
        }

        return currentWeekDates.sorted()
    }

    private fun addWeeklyData(selectedDates: List<Date>): MutableList<Entry> {
        //get deposit for a specific date
        val data = mutableListOf<Entry>()

        var totalDeposit = 0.00f
        var totalWithdraw= 0.00f
        var xAxisPoint =0.00f
        for (date in selectedDates) {
            var totalAmount = 0.00F
            for (transaction in transactionsArrayList) {
                //comparing the dates if they are equal
                if (date.compareTo(transaction.date?.toDate()) == 0) {
                    if (transaction.transactionType == "Deposit"){
                        totalAmount += transaction.amount!!
                        totalDeposit += transaction.amount!!
                    }
                    else {
                        totalAmount -= transaction.amount!!
                        totalWithdraw += transaction.amount!!
                    }
                }
            }
            data.add(Entry(xAxisPoint, totalAmount))
            //dataPoints.add(DataPoint(xAxisPoint, totalAmount.toDouble()))
            xAxisPoint++
        }
        setTotals(totalDeposit, totalWithdraw)
        return data
    }

    private fun setTotals(totalDeposit: Float, totalWithdraw: Float) {
        /*val depositText = dec.format(totalDeposit)
        val withdrawalText = dec.format(totalWithdraw)*/
        var savings = if (totalDeposit != 0.0f) {
            (totalDeposit - totalWithdraw) / totalDeposit * 100
        } else {
            0.0F // set savings rate to 0 if income is 0
        }
        savings = round(savings / 10.0f) * 10.0f

        if (savings > 0 && user == "child")
            binding.tvSummary.text = "Good job! You are saving $savings% of your income through depositing money to your goals. Keep it up!"
        else if (savings > 0 && user == "parent") {
            binding.tvSummary.text = "Great! Your child is saving $savings% of their income by depositing to their goals. Encourage them to keep it up!"
        } else if (savings < 0 && user == "child") {
            binding.tvSummary.text = "Uh oh! You are saving only $savings% of your income by depositing to your goals. Try depositing more!"
        } else if (savings < 0 && user == "parent") {
            binding.tvSummary.text = "Uh oh! Your child is saving $savings% of their income by depsoting to their goals. Encourage them to deposit more!"
        }
        /*binding.tvDepositTotal.text = "₱$depositText"
        binding.tvWithdrawalTotal.text = "₱$withdrawalText"
        binding.tvRate.text = "$savings%"*/
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
                        if (transaction.transactionType == "Deposit"){
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
                        if (transaction.transactionType == "Deposit"){
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


    private fun initializeGraph() {
        val balanceLineGraphView = view?.findViewById<LineChart>(R.id.savings_chart)!!

        val xAxis = balanceLineGraphView.xAxis
        xAxis.granularity = 1f // set the X-axis label granularity to 1 unit
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        when (selectedDatesSort) {
            "weekly" -> updateXAxisWeekly(xAxis)
            "monthly" -> updateXAxisMonthly(xAxis)
            "quarterly" -> updateXAxisQuarterly(xAxis)
        }


        val yAxis = balanceLineGraphView.axisLeft
        yAxis.setDrawLabels(true) // Show X axis labels
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("₱%.0f", value)// Add "₱" symbol to value
            }
        }

        // Create a dataset from the data
        val dataSet = LineDataSet(graphData, "Goal Savings")
        dataSet.color = R.color.red
        dataSet.setCircleColor(R.color.teal_200)

        // Set the data on the chart and customize it
        val lineData = LineData(dataSet)
        balanceLineGraphView.data = lineData
        dataSet.valueTextSize = 12f

        // on below line adding animation
        balanceLineGraphView.animate()

        balanceLineGraphView.setPinchZoom(true)
        balanceLineGraphView.setScaleEnabled(false)

        balanceLineGraphView.axisRight.isEnabled = false

        balanceLineGraphView.description.isEnabled = false

        // Add a Peso sign in the data points in the graph
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "₱$value" // add the ₱ character to the data point values
            }
        }
    }

    private fun updateXAxisWeekly(xAxis: XAxis?) {
        val dateFormatter = SimpleDateFormat("EEE")
        val dates = selectedDates.distinct()

        if (dates.size < graphData.size) {
            // There are fewer dates than xAxis entries, reduce the number of xAxis entries
            graphData = graphData.take(dates.size) as MutableList<Entry>
        }
        xAxis?.valueFormatter = IndexAxisValueFormatter(dates.map { dateFormatter.format(it) }.toTypedArray())
    }


    private fun updateXAxisMonthly(xAxis: XAxis) {
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "Week ${value.toInt() + 1}"
            }
        }
    }

    private fun updateXAxisQuarterly(xAxis: XAxis?) {

// Get the current quarter's months as an array
        val currentQuarterMonthLabels = months?.values?.flatten()?.distinct()?.sorted()?.map { date ->
            val calendar = Calendar.getInstance()
            calendar.time = date
            val monthIndex = calendar.get(Calendar.MONTH)
            // Convert the month index to a quarter label (e.g. Q1, Q2, Q3, Q4)
            val quarterLabel = "Q${monthIndex / 3 + 1}"
            // Get the month name (e.g. January, February, March, etc.)
            val monthName = SimpleDateFormat("MMMM").format(date)
            "$quarterLabel $monthName"
        }?.toTypedArray()

// Set the X Axis value formatter
        xAxis?.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // Get the month label for the corresponding value
                val index = value.toInt()
                return if (index >= 0 && index < currentQuarterMonthLabels?.size!!) {
                    currentQuarterMonthLabels[index]
                } else {
                    ""
                }
            }
        }
    }



    /*private fun initializeSavingsLineGraph() {
        // on below line we are initializing
        // our variable with their ids.
        firestore.collection("Transactions")
            .whereEqualTo("category", "Goal")
            .get().addOnSuccessListener { documents ->
            savingsLineGraphView = view?.findViewById(R.id.savings_chart)!!

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
                    if (date.compareTo(transaction.date?.toDate()) == 0) {
                        if (transaction.transactionType == "Deposit")
                            depositTotal += transaction.amount!!
                        else
                            depositTotal -= transaction.amount!!
                    }
                }
                dataPoints.add(DataPoint(xAxis, depositTotal.toDouble()))
                xAxis++
            }

            //plot data to
            // on below line we are adding data to our graph view.
            val series = LineGraphSeries(dataPoints.toTypedArray())

            // on below line adding animation
            savingsLineGraphView.animate()

            // on below line we are setting scrollable
            // for point graph view
            savingsLineGraphView.viewport.isScrollable = true

            // on below line we are setting scalable.
            savingsLineGraphView.viewport.isScalable = false

            // on below line we are setting scalable y
            savingsLineGraphView.viewport.setScalableY(false)

            // on below line we are setting scrollable y
            savingsLineGraphView.viewport.setScrollableY(true)
                // customize Y-axis label formatter
                val nf = NumberFormat.getNumberInstance()
                nf.maximumFractionDigits = 0
                savingsLineGraphView.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter(nf, nf) {
                    override fun formatLabel(value: Double, isValueX: Boolean): String {
                        return if (!isValueX) "₱$value" else super.formatLabel(value, isValueX)
                    }
                }

            // on below line we are setting color for series.
            series.color = R.color.teal_200

            series.isDrawDataPoints = true
            series.dataPointsRadius = 10f

            // on below line we are adding
            // data series to our graph view.
            savingsLineGraphView.addSeries(series)
        }


    }

    private fun initializeTotals() {
        firestore.collection("Transactions").whereEqualTo("category", "Goal")
            .get().addOnSuccessListener { documents ->
                var totalDeposit = 0.00f
                var totalWithdrawal= 0.00f
                for (transaction in documents) {
                    val transactionObject = transaction.toObject<Transactions>()
                    if (transactionObject.transactionType == "Deposit")
                        totalDeposit += transactionObject.amount!!
                    else totalWithdrawal += transactionObject.amount!!
                }
                binding.tvDepositTotal.text = "₱"+totalDeposit
                binding.tvWithdrawalTotal.text = "₱"+totalWithdrawal

            }
    }*/

    /*private fun initializeSavingsBarGraph() {
        firestore.collection("Transactions").whereEqualTo("category", "Goal")
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
        barChart = view?.findViewById(R.id.bar_chart_savings)

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
        barDataSet1 = BarDataSet(barEntriesIncome, "Deposit")
        barDataSet1!!.color = resources.getColor(R.color.dark_green)
        barDataSet2 = BarDataSet(barEntriesExpense, "Withdrawal")
        barDataSet2!!.color = resources.getColor(R.color.red)

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
            var depositTotal = 0.00F
            var withdrawTotal = 0.00F
            for (transaction in transactionsArrayList) {
                //comparing the dates if they are equal
                if (date.compareTo(transaction.date?.toDate()) == 0) {
                    if (transaction.transactionType == "Deposit")
                        depositTotal += transaction.amount!!
                    else
                        withdrawTotal += transaction.amount!!
                }
            }
            barEntriesIncome.add(BarEntry(xAxisValue, depositTotal))
            barEntriesExpense.add(BarEntry(xAxisValue, withdrawTotal))
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
    }*/


}