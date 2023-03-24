package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentExpenseBinding
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.personalFinancialManagementModule.TransactionHistoryActivity
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList


class ExpenseFragment : Fragment(R.layout.fragment_expense) {
    lateinit var pieChart: PieChart
    private lateinit var binding: FragmentExpenseBinding
    private var bundle = Bundle()
    private var firestore = Firebase.firestore
    private var transactionsArrayList = ArrayList<Transactions>()
    private var clothesPercentage = 0.00f
    private var foodPercentage = 0.00f
    private var giftPercentage = 0.00f
    private var toysAndGamesPercentage = 0.00f
    private var transportationPercentage = 0.00f
    private var entertainmentPercentage = 0.00f
    private var personalCarePercentage = 0.00f
    private var decorationsPercentage = 0.00f
    private var rentPercentage = 0.00f
    private var partyFavorsPercentage = 0.00f
    private var miscellaneousPercentage = 0.00f
    private var otherPercentage = 0.00f
    private var clothes = 0.00f
    private var food = 0.00f
    private var gift = 0.00f
    private var toysAndGames = 0.00f
    private var transportation = 0.00f
    private var entertainment = 0.00f
    private var personalCare = 0.00f
    private var decorations = 0.00f
    private var rent = 0.00f
    private var partyFavors = 0.00f
    private var miscellaneous = 0.00f
    private var other = 0.00f
    private var total = 0.00f
    private var selectedDatesSort = "weekly"
    private var user = "child"
    private lateinit var childID: String
    private lateinit var sortedDate: List<Date>
    private lateinit var selectedDates: List<Date>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expense, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentExpenseBinding.bind(view)
        getArgumentsFromPFM()
        loadTransactionHistory()
        loadPieChart()
    }

    private fun loadTransactionHistory() {
        binding.btnAction.setOnClickListener {
            val goToTransactionHistory = Intent(context, TransactionHistoryActivity::class.java)
            bundle.putString("user", user)
            if (user == "parent")
                bundle.putString("childID", childID)
            bundle.putString("isExpense", "yes")
            goToTransactionHistory.putExtras(bundle)
            startActivity(goToTransactionHistory)
        }
    }

    private fun getArgumentsFromPFM() {
        val args = arguments
        val date = args?.getString("date")

        val currUser = args?.getString("user")
        val id = args?.getString("childID")
        if (id != null) {
            childID = id
        }

        if (currUser != null) {
            user = currUser
        }


        if (date != null) {
            selectedDatesSort = date
            transactionsArrayList.clear()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadPieChart() {
        //TODO: Update data based on user
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        Toast.makeText(context, "" + childID, Toast.LENGTH_SHORT).show()
        firestore.collection("Transactions").whereEqualTo("userID", childID)
            .get().addOnSuccessListener { transactionsSnapshot ->
                for (document in transactionsSnapshot) {
                    val transaction = document.toObject<Transactions>()
                    if (transaction.transactionType == "Expense" || transaction.transactionType == "Expense (Maya")
                        transactionsArrayList.add(transaction)
                }
                sortedDate = getDatesOfTransactions()
                getDataBasedOnDate()
                calculatePercentages()
                setTopThreeCategories()
                topExpense()
                setSummary()
                loadPieChartView()
            }
    }

    private fun setSummary() {
        val dec = DecimalFormat("#,###.00")
        var totalText = dec.format(total)
        if (total == 0.0f)
            totalText = "0"
        var dateRange = "week"
        when (selectedDatesSort) {
            "monthly" -> dateRange = "month"
            "yearly" -> dateRange = "quarter"
        }


        if (user == "child") {
            binding.tvSummary.text = "You've spent ₱$totalText for this $dateRange"
            binding.tvTips.text =
                "Consider reviewing your Top Expenses below or your previous transactions and see which you could lessen"
        } else if (user == "parent") {
            binding.tvSummary.text = "Your child spent ₱$totalText for this $dateRange!"
            binding.tvTips.text =
                "Consider reviewing your child's Top Expenses below or their previous transactions and see which they could lessen"
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
                binding.tvExpenseBreakdown.text = "This Week's Expense"
            }
            "monthly" -> {
                /*val group = groupDates(sortedDate, "monthly")
                addData(group)*/
                val weeks = getWeeksOfCurrentMonth(sortedDate)
                computeDataForMonth(weeks)
                setTopThreeCategories()
                binding.tvExpenseBreakdown.text = "This Month's Expense"
                /*val group = groupDates(sortedDate, "month")
                iterateDatesByQuarter(group)*/
            }
            "quarterly" -> {
                /*val group = groupDates(sortedDate, "quarterly")
                addData(group)*/
                val group = getMonthsOfQuarter(sortedDate)
                computeDataForQuarter(group)
                setTopThreeCategories()
                binding.tvExpenseBreakdown.text = "This Quarter's Expense"
            }
        }

    }

    private fun resetAmounts() {
        clothes = 0.00f
        food = 0.00f
        gift = 0.00f
        toysAndGames = 0.00f
        transportation = 0.00f
        other = 0.00f
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
                    when (transaction.category) {
                        "Clothes" -> clothes += transaction.amount!!.toFloat()
                        "Food" -> food += transaction.amount!!.toFloat()
                        "Gift" -> gift += transaction.amount!!.toFloat()
                        "Toys & Games" -> toysAndGames += transaction.amount!!.toFloat()
                        "Transportation" -> transportation += transaction.amount!!.toFloat()
                        "Entertainment" -> entertainment += transaction.amount!!.toFloat()
                        "Personal Care" -> personalCare += transaction.amount!!.toFloat()
                        "Decorations" -> decorations += transaction.amount!!.toFloat()
                        "Rent" -> rent += transaction.amount!!.toFloat()
                        "Party Favors" -> partyFavors += transaction.amount!!.toFloat()
                        "Miscellaneous" -> miscellaneous += transaction.amount!!.toFloat()
                        "Other" -> other += transaction.amount!!.toFloat()
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
                        when (transaction.category) {
                            "Clothes" -> clothes += transaction.amount!!.toFloat()
                            "Food" -> food += transaction.amount!!.toFloat()
                            "Gift" -> gift += transaction.amount!!.toFloat()
                            "Toys & Games" -> toysAndGames += transaction.amount!!.toFloat()
                            "Transportation" -> transportation += transaction.amount!!.toFloat()
                            "Entertainment" -> entertainment += transaction.amount!!.toFloat()
                            "Personal Care" -> personalCare += transaction.amount!!.toFloat()
                            "Decorations" -> decorations += transaction.amount!!.toFloat()
                            "Rent" -> rent += transaction.amount!!.toFloat()
                            "Party Favors" -> partyFavors += transaction.amount!!.toFloat()
                            "Miscellaneous" -> miscellaneous += transaction.amount!!.toFloat()
                            "Other" -> other += transaction.amount!!.toFloat()
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
                        when (transaction.category) {
                            "Clothes" -> clothes += transaction.amount!!.toFloat()
                            "Food" -> food += transaction.amount!!.toFloat()
                            "Gift" -> gift += transaction.amount!!.toFloat()
                            "Toys & Games" -> toysAndGames += transaction.amount!!.toFloat()
                            "Transportation" -> transportation += transaction.amount!!.toFloat()
                            "Entertainment" -> entertainment += transaction.amount!!.toFloat()
                            "Personal Care" -> personalCare += transaction.amount!!.toFloat()
                            "Decorations" -> decorations += transaction.amount!!.toFloat()
                            "Rent" -> rent += transaction.amount!!.toFloat()
                            "Party Favors" -> partyFavors += transaction.amount!!.toFloat()
                            "Miscellaneous" -> miscellaneous += transaction.amount!!.toFloat()
                            "Other" -> other += transaction.amount!!.toFloat()
                        }
                    }
                }
            }
        }
    }


    private fun calculatePercentages() {
        total = clothes + food + gift + toysAndGames + transportation + entertainment +
                personalCare + decorations + rent + partyFavors + miscellaneous + other

        clothesPercentage = clothes / total * 100
        foodPercentage = food / total * 100
        giftPercentage = gift / total * 100
        toysAndGamesPercentage = toysAndGames / total * 100
        transportationPercentage = transportation / total * 100
        entertainmentPercentage = entertainment / total * 100
        personalCarePercentage = personalCare / total * 100
        decorationsPercentage = decorations / total * 100
        rentPercentage = rent / total * 100
        partyFavorsPercentage = partyFavors / total * 100
        miscellaneousPercentage = miscellaneous / total * 100
        otherPercentage = other / total * 100
    }

    private fun setTopThreeCategories() {
        val totals = mapOf(
            "Clothes" to clothes,
            "Food" to food,
            "Gift" to gift,
            "Toys & Games" to toysAndGames,
            "Transportation" to transportation,
            "Entertainment" to entertainment,
            "Personal Care" to personalCare,
            "Decorations" to decorations,
            "Rent" to rent,
            "Party Favors" to partyFavors,
            "Miscellaneous" to miscellaneous,
            "Other" to other
        )

        val top3Categories = totals.entries.sortedByDescending { it.value }.take(3)
        val dec = DecimalFormat("#,###.00")


        binding.tvTopExpense2.text = top3Categories[1].key
        binding.tvTopExpenseTotal2.text = "₱"+dec.format(top3Categories[1].value)
        binding.tvTopExpense3.text = top3Categories[2].key
        binding.tvTopExpenseTotal3.text = "₱"+dec.format(top3Categories[2].value)
    }


    private fun topExpense() {
        val highest =
            maxOf(
                clothes, food, gift, toysAndGames, transportation, entertainment, personalCare,
                decorations, rent, partyFavors, miscellaneous, other
            )

        val dec = DecimalFormat("#,###.00")
        val amount = dec.format(highest)
        binding.tvExpenseTotal.text = "₱$amount"

        when (highest) {
            clothes -> binding.tvTopExpense.text = "Clothes"
            food -> binding.tvTopExpense.text = "Food"
            gift -> binding.tvTopExpense.text = "Food"
            toysAndGames -> binding.tvTopExpense.text = "Toys & Games"
            transportation -> binding.tvTopExpense.text = "Transportation"
            entertainment -> binding.tvTopExpense.text = "Entertainment"
            personalCare -> binding.tvTopExpense.text = "Personal Care"
            decorations -> binding.tvTopExpense.text = "Decorations"
            rent -> binding.tvTopExpense.text = "Rent"
            partyFavors -> binding.tvTopExpense.text = "Party Favors"
            miscellaneous -> binding.tvTopExpense.text = "Miscellaneous"
            other -> binding.tvTopExpense.text = "Other"
        }
    }

    private fun loadPieChartView() {
        pieChart = view?.findViewById(R.id.pc_expense)!!
        //  setting user percent value, setting description as enabled, and offset for pie chart
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        // disable hole in pie
        pieChart.isDrawHoleEnabled = false

        // setting drag for the pie chart
        pieChart.dragDecelerationFrictionCoef = 0.95f

        // setting circle color and alpha
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        // setting center text
        pieChart.setDrawCenterText(true)

        // setting rotation for our pie chart
        pieChart.rotationAngle = 0f

        // enable rotation of the pieChart by touch
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true

        // setting animation for our pie chart
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        // configure legend
        val legend = pieChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.xEntrySpace = 10f
        legend.yEntrySpace = 0f
        legend.yOffset = 10f
        legend.textSize = 12f

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
        val entries = initializeEntriesPieChart()
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
        colors.add(resources.getColor(R.color.purple_200))
        colors.add(resources.getColor( R.color.yellow))
        colors.add(resources.getColor(R.color.red))
        colors.add(resources.getColor( R.color.dark_green))
        colors.add(resources.getColor( R.color.teal_200))

        // setting colors.
        dataSet.colors = colors

        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%.1f%%", value) // add the ₱ character to the data point values
            }
        }
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

    private fun initializeEntriesPieChart(): ArrayList<PieEntry> {
        val entries: ArrayList<PieEntry> = ArrayList()

        if (clothesPercentage > 0.00f)
            entries.add(PieEntry(clothesPercentage, "Clothes"))

        if (foodPercentage > 0.00f)
            entries.add(PieEntry(foodPercentage, "Food"))

        if (giftPercentage > 0.00f)
            entries.add(PieEntry(giftPercentage, "Gift"))

        if (toysAndGamesPercentage > 0.00f)
            entries.add(PieEntry(toysAndGamesPercentage, "Toys & Games"))

        if (transportationPercentage > 0.00f)
            entries.add(PieEntry(transportationPercentage, "Transportation"))

        if (entertainmentPercentage > 0.00f)
            entries.add(PieEntry(entertainmentPercentage, "Entertainment"))

        if (personalCarePercentage > 0.00f)
            entries.add(PieEntry(personalCarePercentage, "Personal Care"))

        if (decorationsPercentage > 0.00f)
            entries.add(PieEntry(decorationsPercentage, "Decoration"))

        if (rentPercentage > 0.00f)
            entries.add(PieEntry(rentPercentage, "Rent"))

        if (partyFavorsPercentage > 0.00f)
            entries.add(PieEntry(partyFavorsPercentage, "Party Favors"))

        if (miscellaneousPercentage > 0.00f)
            entries.add(PieEntry(miscellaneousPercentage, "Miscellaneous"))

        if (otherPercentage > 0.00f)
            entries.add(PieEntry(otherPercentage, "Other"))

        return entries
    }

    /* private fun calculatePercentage() {
     for (transaction in transactionsArrayList) {
         when (transaction.category) {
             "Clothes" -> clothes += transaction.amount!!.toFloat()
             "Food" -> food += transaction.amount!!.toFloat()
             "Gift" -> gift += transaction.amount!!.toFloat()
             "Toys & Games" -> toysAndGames += transaction.amount!!.toFloat()
             "Transportation" -> transportation += transaction.amount!!.toFloat()
             "Other" -> other += transaction.amount!!.toFloat()
         }
     }
     val total = clothes + food + gift + toysAndGames + transportation + other

     clothesPercentage = clothes / total * 100
     foodPercentage = food / total * 100
     giftPercentage = gift / total * 100
     toysAndGamesPercentage = toysAndGames / total * 100
     transportationPercentage = transportation / total * 100
     otherPercentage = other / total * 100

     topExpense()
     loadPieChartView()
 }*/


}