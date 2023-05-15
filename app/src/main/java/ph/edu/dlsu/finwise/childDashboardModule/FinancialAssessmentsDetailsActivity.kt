package ph.edu.dlsu.finwise.childDashboardModule

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentsDetailsBinding
import ph.edu.dlsu.finwise.model.FinancialAssessmentAttempts
import ph.edu.dlsu.finwise.model.FinancialAssessmentDetails
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

class FinancialAssessmentsDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFinancialAssessmentsDetailsBinding

    private var selectedDateRange = "monthly"

    private var firestore = Firebase.firestore

    private lateinit var childID: String
    //private lateinit var user: String
    private var user = "child"

    private val assessmentsTaken = ArrayList<FinancialAssessmentAttempts>()
    private var nAttempt = 0
    private var financialGoalsPercentage = 0.00F
    private var financialGoalsScores = ArrayList<Double?>()
    private var savingPercentage = 0.00F
    private var savingScores = ArrayList<Double?>()
    private var budgetingPercentage = 0.00F
    private var budgetingScores = ArrayList<Double?>()
    private var spendingPercentage = 0.00F
    private var spendingScores = ArrayList<Double?>()

    private var xAxisPoint =0.00f
    private val goalSettingData = mutableListOf<Entry>()
    private val savingData = mutableListOf<Entry>()
    private val spendingData = mutableListOf<Entry>()
    private val budgetingData = mutableListOf<Entry>()

    private lateinit var sortedDate: List<Date>
    //private lateinit var days: List<Date>
    private var weeks: Map<Int, List<Date>>? = null
    private var months: Map<Int, List<Date>>? = null
    private var selectedDatesSort = "monthly"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getArgumentsBundle()
        getFinancialAssessmentScore()
    }

    private fun getArgumentsBundle() {
        val getBundle = intent.extras
        selectedDateRange = getBundle?.getString("date").toString()
        user = getBundle?.getString("user").toString()
        childID = getBundle?.getString("childID").toString()
    }

    private fun getFinancialAssessmentScore() {
        CoroutineScope(Dispatchers.Main).launch {
            getAssessmentAttempts()
            getDatesOfAttempts()
            setData()
            initializeGraph(findViewById(R.id.goal_setting_chart), goalSettingData)
            initializeGraph(findViewById(R.id.saving_chart), savingData)
            initializeGraph(findViewById(R.id.spending_chart), spendingData)
            initializeGraph(findViewById(R.id.budgeting_chart), budgetingData)
        }
    }

    private fun initializeGraph(chart: LineChart, data: List<Entry>) {

        val xAxis = chart.xAxis
        xAxis.granularity = 1f // Set a smaller granularity if there are fewer data points

        xAxis.position = XAxis.XAxisPosition.BOTTOM

        when (selectedDatesSort) {
            //"weekly" -> updateXAxisWeekly(xAxis)
            "monthly" -> updateXAxisMonthly(xAxis)
            "quarterly" -> updateXAxisQuarterly(xAxis)
        }

        val yAxis = chart.axisLeft
        yAxis.setDrawLabels(true) // Show X axis labels
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${"%.1f".format(value)}%"
            }
        }
        yAxis.axisMaximum = 100f // set maximum y-value to 100%

        // Create a dataset from the data
        val dataSet = LineDataSet(data, "Balance")
        dataSet.color = R.color.red
        dataSet.setCircleColor(R.color.teal_200)
        dataSet.valueTextSize = 12f


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
                val formattedValue = String.format("%.1f", value)

                // Add a percent sign to the formatted value and return it
                return "$formattedValue%"
            }
        }
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

    private fun getDatesOfAttempts() {
        // Step 1: Map the objects to a list of Date objects
        val dates = assessmentsTaken.map { it.dateTaken!!.toDate() }

        // Step 2: Create a new list to store the unique dates
        val uniqueDates = mutableListOf<Date>()

        // Step 3-4: Loop through the dates and add unique dates to the list
        for (date in dates) {
            val uniqueDate = Date(date.year, date.month, date.date, 0, 0, 0)
            if (!uniqueDates.contains(uniqueDate)) {
                uniqueDates.add(uniqueDate)
            }
        }
        sortedDate =  uniqueDates.sorted()
    }

    @SuppressLint("NewApi")
    private suspend fun setData() {
        when (selectedDatesSort) {
            /*"weekly" -> {
                selectedDates = getDaysOfWeek(sortedDate)
                graphData = addWeeklyData(selectedDates)
                binding.tvBalanceTitle.text = "This Week's Personal Financial Score Trend"
            }*/
            "monthly" -> {
                weeks = getWeeksOfCurrentMonth(sortedDate)
                iterateWeeksOfCurrentMonth(weeks!!)
                /*val group = groupDates(sortedDate, "month")
                iterateDatesByQuarter(group)*/
                binding.tvGoalSettingTitle.text = "This Month's Goal Setting Assessments Score Trend"
                binding.tvSavingTitle.text = "This Month's Saving Assessments Score Trend"
                binding.tvSpendingTitle.text = "This Month's Spending Assessments Score Trend"
                binding.tvBudgetingTitle.text = "This Month's Budgeting Assessments Score Trend"
            }
            "quarterly" -> {
                months = getMonthsOfQuarter(sortedDate)
                forEachDateInMonths(months!!)
                binding.tvGoalSettingTitle.text = "This Quarter's Goal Setting Assessments Score Trend"
                binding.tvSavingTitle.text = "This Quarter's Saving Assessments Score Trend"
                binding.tvSpendingTitle.text = "This Quarter's Spending Assessments Score Trend"
                binding.tvBudgetingTitle.text = "This Quarter's Budgeting Assessments Score Trend"
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

    @SuppressLint("NewApi")
    private suspend fun forEachDateInMonths(months: Map<Int, List<Date>>) {
        for ((month, datesInMonth) in months) {
            //var totalAmount = 0.00F
            for (date in datesInMonth) {
                for (attempt in assessmentsTaken) {
                    //Convert both dates to LocalDate so they can be compared regardless of the time
                    val weekDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    val attemptDate = attempt.dateTaken?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                    if (attemptDate != null && weekDate == attemptDate) {
                        if (assessmentsTaken.isNotEmpty()) {
                            nAttempt++
                            val assessmentDocument = firestore.collection("Assessments")
                                .document(attempt.assessmentID!!).get().await()
                            val assessmentObject =
                                assessmentDocument.toObject<FinancialAssessmentDetails>()

                            val percentage = getPercentage(attempt)
                            when (assessmentObject?.assessmentCategory) {
                                "Goal Setting" -> financialGoalsScores.add(percentage)
                                "Saving" -> savingScores.add(percentage)
                                "Budgeting" -> budgetingScores.add(percentage)
                                "Spending" -> spendingScores.add(percentage)
                            }
                            computeForPercentages()
                        }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun iterateWeeksOfCurrentMonth(weeks: Map<Int, List<Date>>) {
        weeks.forEach { (weekNumber, datesInWeek) ->
            //var totalAmount = 0.00F
            datesInWeek.forEach { date ->
                for (attempt in assessmentsTaken) {
                    //Convert both dates to LocalDate so they can be compared regardless of the time
                    val weekDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    val attemptDate = attempt.dateTaken?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                    if (attemptDate != null && weekDate == attemptDate) {
                        if (assessmentsTaken.isNotEmpty()) {
                            nAttempt++
                            val assessmentDocument = firestore.collection("Assessments")
                                .document(attempt.assessmentID!!).get().await()
                            val assessmentObject =
                                assessmentDocument.toObject<FinancialAssessmentDetails>()

                            val percentage = getPercentage(attempt)

                            when (assessmentObject?.assessmentCategory) {
                                "Goal Setting" -> financialGoalsScores.add(percentage)
                                "Saving" -> savingScores.add(percentage)
                                "Budgeting" -> budgetingScores.add(percentage)
                                "Spending" -> spendingScores.add(percentage)
                            }
                            computeForPercentages()
                        }
                    }
                }
            }
        }
    }

    private suspend fun getAssessmentAttempts() {
        val assessmentAttemptsDocuments =  firestore.collection("AssessmentAttempts")
            .whereEqualTo("childID", childID).get().await()

        for (assessments in assessmentAttemptsDocuments) {
            val assessmentObject = assessments.toObject<FinancialAssessmentAttempts>()
            assessmentsTaken.add(assessmentObject)
        }
        assessmentsTaken.sortByDescending { it.dateTaken }

    }

    private fun computeForPercentages() {
        val maxScore = 100
        val savingPercentageSum = savingScores.sumOf { it ?: 0.0 }
        savingPercentage = ((savingPercentageSum / (maxScore * savingScores.size)) * 100).toFloat()
        val spendingPercentageSum = spendingScores.sumOf { it ?: 0.0 }
        spendingPercentage = ((spendingPercentageSum / (maxScore * spendingScores.size)) * 100).toFloat()
        val budgetingPercentageSum = budgetingScores.sumOf { it ?: 0.0 }
        budgetingPercentage = ((budgetingPercentageSum / (maxScore * budgetingScores.size)) * 100).toFloat()
        val financialGoalsPercentageSum = financialGoalsScores.sumOf { it ?: 0.0 }
        financialGoalsPercentage = ((financialGoalsPercentageSum / (maxScore * financialGoalsScores.size)) * 100).toFloat()
        checkIfNaN()
        addDataPointsForChart()
    }

    private fun checkIfNaN() {
        val percentages = mutableListOf(savingPercentage, spendingPercentage, budgetingPercentage,
            financialGoalsPercentage)

        for (i in percentages.indices) {
            if (percentages[i].isNaN()) {
                when (i) {
                    0 -> savingPercentage = 0.00f
                    1 -> spendingPercentage = 0.00f
                    2 -> budgetingPercentage = 0.00f
                    3 -> financialGoalsPercentage = 0.00f
                }
            }
        }
    }

    private fun addDataPointsForChart() {
        goalSettingData.add(Entry(xAxisPoint, financialGoalsPercentage))
        savingData.add(Entry(xAxisPoint, savingPercentage))
        spendingData.add(Entry(xAxisPoint, spendingPercentage))
        budgetingData.add(Entry(xAxisPoint, budgetingPercentage))
        xAxisPoint++
    }

    private fun getPercentage(assessment: FinancialAssessmentAttempts): Double {
        val correctAnswers: Int? = assessment.nAnsweredCorrectly
        val totalAnswers: Int? = assessment.nQuestions

        val percentage = if(totalAnswers != null && correctAnswers != null && totalAnswers != 0) {
            (correctAnswers.toDouble() / totalAnswers.toDouble()) * 100
        } else {
            0.0
        }

        return percentage.coerceAtMost(100.0)
    }



}