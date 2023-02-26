package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityGoalSavingDetailsBinding
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class GoalSavingDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoalSavingDetailsBinding
    private var setBundle = Bundle()
    private var selectedDateRange = "week"
    private var firestore = Firebase.firestore
    private var transactionsArrayList = ArrayList<Transactions>()
    var depositTotalAmount = 0.00f
    var withdrawalTotalAmount = 0.00f
    private lateinit var sortedDate: List<Date>
    private lateinit var selectedDates: List<Date>
    private var selectedDatesSort = "weekly"
    lateinit var barChart: BarChart

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalSavingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getBundle()
        loadButtons()
        loadPieChart()
    }

    private fun getBundle() {
        val getBundle = intent.extras
        selectedDatesSort = getBundle?.getString("date").toString()
        transactionsArrayList.clear()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadPieChart() {
        //TODO: Update data based on user
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        firestore.collection("Transactions").get()
            .addOnSuccessListener { transactionsSnapshot ->
                lateinit var id: String
                for (document in transactionsSnapshot) {
                    val transaction = document.toObject<Transactions>()
                    if (transaction.transactionType == "Deposit" ||
                        transaction.transactionType == "Withdrawal" )
                        transactionsArrayList.add(transaction)
                }
                sortedDate = getDatesOfTransactions()
                getDataBasedOnDate()
                /*calculatePercentages()
                setTopThreeCategories()
                topExpense()*/
                loadChart()
            }
    }

    private fun getDatesOfTransactions(): List<Date> {
        //get unique dates in transaction arraylist
        val dates = java.util.ArrayList<Date>()

        for (transaction in transactionsArrayList) {
            //if array of dates doesn't contain date of the transaction, add the date to the arraylist
            if (!dates.contains(transaction.date?.toDate()))
                dates.add(transaction.date?.toDate()!!)
        }
        return dates.sortedBy { it }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDataBasedOnDate() {
        resetAmounts()
        when (selectedDatesSort) {
            "weekly" -> {
                selectedDates = getDaysOfWeek(sortedDate)
                addWeeklyData(selectedDates)
                setText("Week")
                //setTopThreeCategories()
            }
            "monthly" -> {
                /*val group = groupDates(sortedDate, "monthly")
                addData(group)*/
                val weeks = getWeeksOfCurrentMonth(sortedDate)
                computeDataForMonth(weeks)
                setText("Month")
                //setTopThreeCategories()
                /*val group = groupDates(sortedDate, "month")
                iterateDatesByQuarter(group)*/
            }
            "quarterly" -> {
                /*val group = groupDates(sortedDate, "quarterly")
                addData(group)*/
                val group = getMonthsOfQuarter(sortedDate)
                computeDataForQuarter(group)
                //setTopThreeCategories()
                setText("Quarter")
            }
        }

    }

    private fun setText(dateRange: String) {
        binding.tvTitle.text = "This $dateRange's Goal Savings"
        val dec = DecimalFormat("#,###.00")
        val deposit = dec.format(depositTotalAmount)
        val withdrawal = dec.format(withdrawalTotalAmount)
        binding.tvDepositTotal.text = "₱$deposit"
        binding.tvWithdrawalTotal.text = "₱$withdrawal"
    }

    private fun initializeEntries(): List<BarEntry> {
        val entries = listOf(
                BarEntry(1f, depositTotalAmount),
                BarEntry(2f, withdrawalTotalAmount)
        )
        return entries
    }

    private fun resetAmounts() {
        depositTotalAmount = 0.00f
        withdrawalTotalAmount = 0.00f
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDaysOfWeek(dates: List<Date>): List<Date> {
        val startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY)
        val endOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY)
        val filteredDates = dates.filter { date ->
            val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            !localDate.isBefore(startOfWeek) && !localDate.isAfter(endOfWeek)
        }
        return filteredDates
    }

    private fun addWeeklyData(selectedDates: List<Date>) {
        /*TODO: Might have to seperate this function para magroup yung days sa week, weeks
                sa month, at months sa year*/
        //get deposit for a specific date
        for (date in selectedDates) {
            for (transaction in transactionsArrayList) {
                //comparing the dates if they are equal
                if (date.compareTo(transaction.date?.toDate()) == 0) {
                    when (transaction.transactionType) {
                        "Deposit" -> depositTotalAmount += transaction.amount!!.toFloat()
                        "Withdrawal" -> withdrawalTotalAmount += transaction.amount!!.toFloat()
                    }
                }
            }
        }
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

    private fun computeDataForMonth(weeks: Map<Int, List<Date>>) {
        weeks.forEach { (weekNumber, datesInWeek) ->
            datesInWeek.forEach { date ->
                for (transaction in transactionsArrayList) {
                    //comparing the dates if they are equal
                    if (date.compareTo(transaction.date?.toDate()) == 0) {
                        when (transaction.transactionType) {
                            "Deposit" -> depositTotalAmount += transaction.amount!!.toFloat()
                            "Withdrawal" -> withdrawalTotalAmount += transaction.amount!!.toFloat()
                        }
                    }
                }
            }
        }
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

    private fun computeDataForQuarter(months: Map<Int, List<Date>>) {
        for ((month, datesInMonth) in months) {
            for (date in datesInMonth) {
                for (transaction in transactionsArrayList) {
                    //comparing the dates if they are equal
                    if (date.compareTo(transaction.date?.toDate()) == 0) {
                        when (transaction.transactionType) {
                            "Deposit" -> depositTotalAmount += transaction.amount!!.toFloat()
                            "Withdrawal" -> withdrawalTotalAmount += transaction.amount!!.toFloat()
                        }
                    }
                }
            }
        }
    }


  /*  private fun setTopThreeCategories() {
        val totals = mapOf(
            "Clothes" to clothes,
            "Food" to food,
            "Gift" to gift,
            "Toys & Games" to toysAndGames,
            "Transportation" to transportation,
            "Other" to other
        )
        val top3Categories = totals.entries.sortedByDescending { it.value }.take(3)
        val dec = DecimalFormat("#,###.00")


        binding.tvTopExpense2.text = top3Categories[1].key
        binding.tvTopExpenseTotal2.text = "₱"+dec.format(top3Categories[1].value)
        binding.tvTopExpense3.text = top3Categories[2].key
        binding.tvTopExpenseTotal3.text = "₱"+dec.format(top3Categories[2].value)
    }*/


/*    private fun topExpense() {
        if (clothes >= food && clothes >= gift && clothes >= toysAndGames
            && clothes >= transportation && clothes >= other) {
            binding.tvTopExpense.text = "Clothes"
            val dec = DecimalFormat("#,###.00")
            val amount = dec.format(clothes)
            binding.tvExpenseTotal.text = "₱$amount"
        } else if (food >= clothes && food >= gift && food >= toysAndGames &&
            food >= transportation && food >= other) {
            binding.tvTopExpense.text = "Food"
            val dec = DecimalFormat("#,###.00")
            val amount = dec.format(food)
            binding.tvExpenseTotal.text = "₱$amount"
        } else if (gift >= clothes && gift >= food && gift >= toysAndGames &&
            gift >= transportation && gift >= other) {
            binding.tvTopExpense.text = "Gift"
            val dec = DecimalFormat("#,###.00")
            val amount = dec.format(gift)
            binding.tvTopExpense.text = "₱$amount"
        } else if (toysAndGames >= clothes && toysAndGames >= gift && toysAndGames >= food &&
            toysAndGames >= transportation && toysAndGames >= other) {
            binding.tvTopExpense.text = "Toys & Games"
            val dec = DecimalFormat("#,###.00")
            val amount = dec.format(toysAndGames)
            binding.tvExpenseTotal.text = "₱$amount"
        } else if (transportation >= clothes && transportation >= gift && transportation >= toysAndGames
            && transportation >= food && transportation >= other) {
            binding.tvTopExpense.text = "Transportation"
            val dec = DecimalFormat("#,###.00")
            val amount = dec.format(transportation)
            binding.tvExpenseTotal.text = "₱$amount"
        } else {
            binding.tvTopExpense.text = "Other"
            val dec = DecimalFormat("#,###.00")
            val amount = dec.format(other)
            binding.tvExpenseTotal.text = "₱$amount"
        }
    }*/

    private fun loadChart() {
        barChart = findViewById(R.id.savings_chart)!!

        val yAxis = barChart.axisLeft
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("₱%.0f", value)// Add "₱" symbol to value
            }
        }

        barChart.axisRight.isEnabled = false



        //  setting user percent value, setting description as enabled, and offset for pie chart
        val entries = initializeEntries()
        val dataSet = BarDataSet(entries, "Values")
        dataSet.colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.parseColor("#FFA500"))
        dataSet.valueTextSize = 14f

        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "₱$value" // add the ₱ character to the data point values
            }
        }

        val data = BarData(dataSet)
        barChart.data = data
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.animateY(1000)

        val xAxis = barChart.xAxis
        xAxis.isEnabled = false
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)

        barChart.setTouchEnabled(false)
        barChart.isHighlightPerTapEnabled = false


    }

/*    private fun initializeEntriesPieChart(): ArrayList<PieEntry> {
        val entries: ArrayList<PieEntry> = ArrayList()

        if (clothesPercentage > 0.00f)
            entries.add(PieEntry(clothesPercentage, "Clothes"))

        if (foodPercentage > 0.00f)
            entries.add(PieEntry(foodPercentage, "Food"))

        if (toysAndGamesPercentage > 0.00f)
            entries.add(PieEntry(toysAndGamesPercentage, "Gift"))

        if (transportationPercentage > 0.00f)
            entries.add(PieEntry(transportationPercentage, "Toys & Games"))

        if (transportationPercentage > 0.00f)
            entries.add(PieEntry(transportationPercentage, "Transportation"))

        if (otherPercentage > 0.00f)
            entries.add(PieEntry(otherPercentage, "Other"))

        return entries
    }*/

    private fun loadButtons() {
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)
        loadBackButton()
    }



    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToPFM = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goToPFM)
        }
    }



}