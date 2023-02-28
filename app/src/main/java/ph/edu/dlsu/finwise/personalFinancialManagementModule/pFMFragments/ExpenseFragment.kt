package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentExpenseBinding
import ph.edu.dlsu.finwise.model.Transactions
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
    private var firestore = Firebase.firestore
    private var transactionsArrayList = ArrayList<Transactions>()
    var clothesPercentage = 0.00f
    var foodPercentage = 0.00f
    var giftPercentage = 0.00f
    var toysAndGamesPercentage = 0.00f
    var transportationPercentage = 0.00f
    var otherPercentage = 0.00f
    var clothes = 0.00f
    var food = 0.00f
    var gift = 0.00f
    var toysAndGames = 0.00f
    var transportation = 0.00f
    var other = 0.00f
    private var selectedDatesSort = "weekly"
    private lateinit var sortedDate: List<Date>
    private lateinit var selectedDates: List<Date>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        loadPieChart()
    }

    private fun getArgumentsFromPFM() {
        val args = arguments
        val date = args?.getString("date")
        if (date != null) {
            Toast.makeText(context, ""+date, Toast.LENGTH_SHORT).show()
            selectedDatesSort = date
            transactionsArrayList.clear()
        }
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
                    if (transaction.transactionType == "Expense" || transaction.transactionType == "Expense (Maya")
                        transactionsArrayList.add(transaction)
                }
                sortedDate = getDatesOfTransactions()
                getDataBasedOnDate()
                calculatePercentages()
                setTopThreeCategories()
                topExpense()
                loadPieChartView()
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
                binding.tvExpenseBreakdown.text = "This Week's Income Categories"
            }
            "monthly" -> {
                /*val group = groupDates(sortedDate, "monthly")
                addData(group)*/
                val weeks = getWeeksOfCurrentMonth(sortedDate)
                computeDataForMonth(weeks)
                setTopThreeCategories()
                binding.tvExpenseBreakdown.text = "This Month's Income Categories"
                /*val group = groupDates(sortedDate, "month")
                iterateDatesByQuarter(group)*/
            }
            "quarterly" -> {
                /*val group = groupDates(sortedDate, "quarterly")
                addData(group)*/
                val group = getMonthsOfQuarter(sortedDate)
                computeDataForQuarter(group)
                setTopThreeCategories()
                binding.tvExpenseBreakdown.text = "This Quarter's Income Categories"
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
                    when (transaction.category) {
                        "Clothes" -> clothes += transaction.amount!!.toFloat()
                        "Food" -> food += transaction.amount!!.toFloat()
                        "Gift" -> gift += transaction.amount!!.toFloat()
                        "Toys & Games" -> toysAndGames += transaction.amount!!.toFloat()
                        "Transportation" -> transportation += transaction.amount!!.toFloat()
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
                            "Other" -> other += transaction.amount!!.toFloat()
                        }
                    }
                }
            }
        }
    }


    private fun calculatePercentages() {
        val total = clothes + food + gift + toysAndGames + transportation + other

        clothesPercentage = clothes / total * 100
        foodPercentage = food / total * 100
        giftPercentage = gift / total * 100
        toysAndGamesPercentage = toysAndGames / total * 100
        transportationPercentage = transportation / total * 100
        otherPercentage = other / total * 100
    }

    private fun setTopThreeCategories() {
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
    }


    private fun topExpense() {
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

        if (toysAndGamesPercentage > 0.00f)
            entries.add(PieEntry(toysAndGamesPercentage, "Gift"))

        if (transportationPercentage > 0.00f)
            entries.add(PieEntry(transportationPercentage, "Toys & Games"))

        if (transportationPercentage > 0.00f)
            entries.add(PieEntry(transportationPercentage, "Transportation"))

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