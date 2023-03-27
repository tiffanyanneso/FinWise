package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityGoalSavingDetailsBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentFinancialActivity
import java.text.DecimalFormat
import java.util.*

class GoalSavingDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoalSavingDetailsBinding
    private var firestore = Firebase.firestore
    private var transactionsArrayList = ArrayList<Transactions>()
    private var childID = FirebaseAuth.getInstance().currentUser!!.uid
    private var user = "child"
    var total = 0.00f
    var depositTotalAmount = 0.00f
    private var selectedDatesSort = "weekly"
    var withdrawalTotalAmount = 0.00f
    var savingsPercentage = 0.00f
    var withdrawalPercentage = 0.00f
    private lateinit var sortedDate: List<Date>
    private lateinit var selectedDates: List<Date>


    lateinit var chart: PieChart

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
        user = getBundle?.getString("user").toString()
        if (user == "parent")
            childID = getBundle?.getString("childID").toString()

        transactionsArrayList.clear()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadPieChart() {
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        firestore.collection("Transactions").whereEqualTo("userID", childID)
            .get().addOnSuccessListener { transactionsSnapshot ->
                for (document in transactionsSnapshot) {
                    val transaction = document.toObject<Transactions>()
                    if (transaction.transactionType == "Deposit" ||
                        transaction.transactionType == "Withdrawal" )
                        transactionsArrayList.add(transaction)
                }
                sortedDate = getDatesOfTransactions()
                getDataBasedOnDate()
                calculatePercentages()
                /*setTopThreeCategories()
                topExpense()*/
                loadChart()
            }
    }


    private fun calculatePercentages() {
        total = depositTotalAmount + withdrawalTotalAmount
        savingsPercentage = (depositTotalAmount - withdrawalTotalAmount) / total * 100
        withdrawalPercentage = withdrawalTotalAmount / total * 100
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
        binding.tvBalanceTitle.text = "This $dateRange's Goal Savings"
        val dec = DecimalFormat("#,###.00")
        val deposit = dec.format(depositTotalAmount)
        val withdrawal = dec.format(withdrawalTotalAmount)
        val savingsAmount = depositTotalAmount - withdrawalTotalAmount
        val savings = dec.format(savingsAmount)

        binding.tvDepositTotal.text = "₱$deposit"
        binding.tvWithdrawalTotal.text = "₱$withdrawal"
        setSummary(savings)
    }

    private fun setSummary(savings: String?) {
        var dateRange = "week"
        when (selectedDatesSort) {
            "monthly" -> dateRange = "month"
            "yearly" -> dateRange = "quarter"
        }
        if (user == "child") {
            binding.tvSummary.text = "You've saved ₱$savings for this $dateRange!"
            binding.tvTips.text = "Go to the \"Financial Activities\" to develop your Financial Literacy using your money. Scroll down for more details"
            loadChildFinancialActivitiesButton()
        } else if (user == "parent") {
            binding.tvSummary.text = "Your child saved ₱$savings for this $dateRange!"
            binding.tvTips.text = "Go to the \"Financial Activities\" to develop your child's Financial Literacy using their money. Scroll down for more details"
            loadParentFinancialActivitiesButton()
        }

        /*if (user == "child" && total < 500) {
            binding.tvSummary.text = "You've spent ₱$totalText for this $dateRange!"
            binding.tvTips.text = "Consider reviewing your Top Expenses below or your previous transactions and see which you could lessen"
        } else if (user == "child" && total > 500) {
            binding.tvSummary.text = "You've earned ₱$totalText for this $dateRange"
            binding.tvTips.text = "Consider reviewing your previous transactions and see which you could lessen"
        } else if (user == "parent" && total > 500) {
            binding.tvSummary.text = "Your child spent ₱$totalText for this $dateRange!"
            binding.tvTips.text = "Consider reviewing your child's Top Expenses below or their previous transactions and see which they could lessen"
        } else if (user == "parent" && total < 500) {
            binding.tvSummary.text = "You've child earned ₱$totalText for this $dateRange"
            binding.tvTips.text = "Consider reviewing your child's previous transactions and see which they could lessen"
        }*/

        /*if (savingsAmount > 0) {*/
        /*} TODO: check if negeatives lahat
        else {
            binding.tvSummary.text = "You've saved ₱$savings for this $dateRange!"
            binding.tvTips.text = "Go to the \"Financial Activities\" to use your money that you saved to develop your Financial Literacy"
        }*/

    }

    private fun loadChildFinancialActivitiesButton() {
        //TODO: double chekc kung tama link
        binding.btnAction.setOnClickListener {
            val goToFinancialActivity = Intent(this, FinancialActivity::class.java)
            startActivity(goToFinancialActivity)
        }
    }

    private fun loadParentFinancialActivitiesButton() {
        //TODO: double chekc kung tama link
        binding.btnAction.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("childID",  childID)
            val parentGoal = Intent(this, ParentFinancialActivity::class.java)
            parentGoal.putExtras(bundle)
            startActivity(parentGoal)
        }
    }


    private fun initializeEntries(): List<PieEntry> {
        val entries: ArrayList<PieEntry> = ArrayList()

        if (depositTotalAmount > 0.00f)
            entries.add(PieEntry(savingsPercentage, "Savings"))

        if (withdrawalTotalAmount > 0.00f)
            entries.add(PieEntry(withdrawalPercentage, "Withdrawal"))

        return entries
    }

    private fun resetAmounts() {
        depositTotalAmount = 0.00f
        withdrawalTotalAmount = 0.00f
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDaysOfWeek(dates: List<Date>): List<Date> {
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
        chart = findViewById(R.id.savings_chart)!!

        //  setting user percent value, setting description as enabled, and offset for pie chart
        val entries = initializeEntries()
        val dataSet = PieDataSet(entries, "Values")
        dataSet.colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.parseColor("#FFA500"))
        dataSet.valueTextSize = 14f

        // on below line we are setting icons.
        dataSet.setDrawIcons(true)

        // setting slice for pie
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors to list
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.dark_green))
        colors.add(resources.getColor( R.color.light_green))

        // setting colors.
        dataSet.colors = colors

        // setting pie data set
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(12f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        chart.data = data
        //chart.legend.textSize = 14f

        // undo all highlights
        chart.highlightValues(null)

        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%.1f%%", value) // add the ₱ character to the data point values
            }
        }

        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.setExtraOffsets(5f, 10f, 5f, 5f)

        // disable hole in pie
        chart.isDrawHoleEnabled = false

        // setting drag for the pie chart
        chart.dragDecelerationFrictionCoef = 0.95f

        // setting circle color and alpha
        chart.setTransparentCircleColor(Color.WHITE)
        chart.setTransparentCircleAlpha(110)

        // setting center text
        chart.setDrawCenterText(true)

        // setting rotation for our pie chart
        chart.rotationAngle = 0f

        // enable rotation of the pieChart by touch
        chart.isRotationEnabled = true
        chart.isHighlightPerTapEnabled = true

        // setting animation for our pie chart
        chart.animateY(1400, Easing.EaseInOutQuad)

        // configure legend
        /*val legend = chart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.xEntrySpace = 10f
        legend.yEntrySpace = 0f
        legend.yOffset = 10f
        legend.textSize = 12f*/
        /* val yAxis = chart.axisLeft
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("₱%.0f", value)// Add "₱" symbol to value
            }
        }

        chart.axisRight.isEnabled = false
        val data = BarData(dataSet)
        chart.data = data
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.animateY(1000)

        val xAxis = chart.xAxis
        xAxis.isEnabled = false
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)

        chart.setTouchEnabled(false)
        chart.isHighlightPerTapEnabled = false*/

        // loading chart
        chart.invalidate()


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

        loadBackButton()
        setNavigationBar()
    }

    private fun setNavigationBar() {
        val bottomNavigationViewChild = binding.bottomNav
        val bottomNavigationViewParent = binding.bottomNavParent

        if (user == "child") {
            bottomNavigationViewChild.visibility = View.VISIBLE
            bottomNavigationViewParent.visibility = View.GONE
            Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)
        } else {
            bottomNavigationViewChild.visibility = View.GONE
            bottomNavigationViewParent.visibility = View.VISIBLE
            //sends the ChildID to the parent navbar
            val bundle = Bundle()
            val childID = bundle.getString("childID").toString()
            val bundleNavBar = Bundle()
            bundleNavBar.putString("childID", childID)
            NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance, bundleNavBar)
        }
    }



    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }



}