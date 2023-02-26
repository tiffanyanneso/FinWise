package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentIncomeBinding
import ph.edu.dlsu.finwise.model.Transactions
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


class IncomeFragment : Fragment(R.layout.fragment_income) {
    private lateinit var binding: FragmentIncomeBinding
    private var firestore = Firebase.firestore
    private var transactionsArrayList = ArrayList<Transactions>()
    var allowancePercentage = 0.00f
    var giftPercentage = 0.00f
    var rewardPercentage = 0.00f
    var otherPercentage = 0.00f
    var allowance = 0.00f
    var gift = 0.00f
    var reward = 0.00f
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
        return inflater.inflate(R.layout.fragment_income, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentIncomeBinding.bind(view)
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
                for (document in transactionsSnapshot) {
                    val transaction = document.toObject<Transactions>()
                    if (transaction.transactionType == "Income")
                        transactionsArrayList.add(transaction)
                }
                sortedDate = getDatesOfTransactions()
                getDataBasedOnDate()
                calculatePercentages()
                setTopThreeCategories()
                topIncome()
                loadPieChartView()
            }
    }

   /* private fun createDataForPieChart() {
        //TODO: lagay dito yuing loop
        for (transaction in transactionsArrayList) {
            when (transaction.category) {
                "Allowance" -> allowance += transaction.amount!!.toFloat()
                "Gift" -> gift += transaction.amount!!.toFloat()
                "Reward" -> reward += transaction.amount!!.toFloat()
                "Other" -> other += transaction.amount!!.toFloat()
            }
        }

    }*/

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
                binding.tvIncomeTitle.text = "This Week's Income Categories"
            }
            "monthly" -> {
                /*val group = groupDates(sortedDate, "monthly")
                addData(group)*/
                val weeks = getWeeksOfCurrentMonth(sortedDate)
                computeDataForMonth(weeks)
                setTopThreeCategories()
                binding.tvIncomeTitle.text = "This Month's Income Categories"
                /*val group = groupDates(sortedDate, "month")
                iterateDatesByQuarter(group)*/
            }
            "quarterly" -> {
                /*val group = groupDates(sortedDate, "quarterly")
                addData(group)*/
                val group = getMonthsOfQuarter(sortedDate)
                computeDataForQuarter(group)
                setTopThreeCategories()
                binding.tvIncomeTitle.text = "This Quarter's Income Categories"
            }
        }

    }

    private fun setTopThreeCategories() {
        val totals = mapOf(
            "Allowance" to allowance,
            "Gift" to gift,
            "Reward" to reward,
            "Other" to other
        )
        val top3Categories = totals.entries.sortedByDescending { it.value }.take(3)


        val dec = DecimalFormat("#,###.00")
        binding.tvTopIncome2.text = top3Categories[1].key
        binding.tvTopIncomeTotal2.text = "₱"+dec.format(top3Categories[1].value)
        binding.tvTopIncome3.text = top3Categories[2].key
        binding.tvTopIncomeTotal3.text = "₱"+dec.format(top3Categories[2].value)
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
                            "Allowance" -> allowance += transaction.amount!!.toFloat()
                            "Gift" -> gift += transaction.amount!!.toFloat()
                            "Reward" -> reward += transaction.amount!!.toFloat()
                            "Other" -> other += transaction.amount!!.toFloat()
                        }
                    }
                }
            }
        }
    }

    private fun resetAmounts() {
        allowance = 0.00f
        gift = 0.00f
        reward = 0.00f
        other = 0.00f
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
                            "Allowance" -> allowance += transaction.amount!!.toFloat()
                            "Gift" -> gift += transaction.amount!!.toFloat()
                            "Reward" -> reward += transaction.amount!!.toFloat()
                            "Other" -> other += transaction.amount!!.toFloat()
                        }
                    }
                }
            }
        }
    }

    private fun calculatePercentages() {
        val total = allowance + gift + reward + other
        allowancePercentage = allowance / total * 100
        giftPercentage = gift / total * 100
        rewardPercentage = reward / total * 100
        otherPercentage = other / total * 100
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
                        "Allowance" -> allowance += transaction.amount!!.toFloat()
                        "Gift" -> gift += transaction.amount!!.toFloat()
                        "Reward" -> reward += transaction.amount!!.toFloat()
                        "Other" -> other += transaction.amount!!.toFloat()
                    }
                }
            }
        }
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

    private fun topIncome() {
        val dec = DecimalFormat("#,###.00")
        if (allowance >= gift && allowance >= reward && allowance >= other) {
            binding.tvTopIncome.text = "Allowance"
            val amount = dec.format(allowance)
            binding.tvIncomeTotal.text = "₱$amount"
        } else if (gift >= allowance && gift >= reward && gift >= other) {
            binding.tvTopIncome.text = "Gift"
            val amount = dec.format(gift)
            binding.tvIncomeTotal.text = "₱$amount"
        } else if (reward >= allowance && reward >= gift && reward >= other) {
            binding.tvTopIncome.text = "Reward"
            val amount = dec.format(reward)
            binding.tvIncomeTotal.text = "₱$amount"
        } else {
            binding.tvTopIncome.text = "Other"
            val amount = dec.format(other)
            binding.tvIncomeTotal.text = "₱$amount"
        }
    }

    private fun loadPieChartView() {
        val pieChart: PieChart = view?.findViewById(R.id.pc_income)!!
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

        // setting pie data set
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(12f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data
        pieChart.legend.textSize = 14f

        // undo all highlights
        pieChart.highlightValues(null)

        // loading chart
        pieChart.invalidate()
    }

    private fun initializeEntriesPieChart(): ArrayList<PieEntry> {
        val entries: ArrayList<PieEntry> = ArrayList()

        if (allowancePercentage > 0.00f)
            entries.add(PieEntry(allowancePercentage, "Allowance"))

        if (giftPercentage > 0.00f)
            entries.add(PieEntry(giftPercentage, "Gift"))

        if (rewardPercentage > 0.00f)
            entries.add(PieEntry(rewardPercentage, "Reward"))

        if (otherPercentage > 0.00f)
            entries.add(PieEntry(otherPercentage, "Other"))

        return entries
    }

    /*  private fun groupDates(dates: List<Date>, range: String): Map<Int, List<Date>> {
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

    /*  private fun addData(groups: Map<Int, List<Date>>) {
          for ((x, dates) in groups) {
              for (date in dates) {
                  for (transaction in transactionsArrayList) {
                      //comparing the dates if they are equal
                      if (date.compareTo(transaction.date?.toDate()) == 0) {
                          when (transaction.category) {
                              "Allowance" -> allowance += transaction.amount!!.toFloat()
                              "Gift" -> gift += transaction.amount!!.toFloat()
                              "Reward" -> reward += transaction.amount!!.toFloat()
                              "Other" -> other += transaction.amount!!.toFloat()
                          }
                      }
                  }
              }
          }
      }*/

}