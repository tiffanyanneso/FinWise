package ph.edu.dlsu.finwise.childDashboardModule.fragments

import android.annotation.SuppressLint
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
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.childDashboardModule.FinancialAssessmentsDetailsActivity
import ph.edu.dlsu.finwise.databinding.FragmentDashboardFinancialAssessmentsBinding
import ph.edu.dlsu.finwise.model.FinancialAssessmentAttempts
import ph.edu.dlsu.finwise.model.FinancialAssessmentDetails
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

class DashboardFinancialAssessmentsFragment : Fragment() {
    private lateinit var binding: FragmentDashboardFinancialAssessmentsBinding
    private var firestore = Firebase.firestore

    private lateinit var childID: String
    //private lateinit var user: String
    private var user = "child"

    private val assessmentsTaken = ArrayList<FinancialAssessmentAttempts>()
    private var financialAssessmentTotalPercentage = 0.0
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
    private val data = mutableListOf<Entry>()

    private lateinit var sortedDate: List<Date>
    //private lateinit var days: List<Date>
    private var weeks: Map<Int, List<Date>>? = null
    private var months: Map<Int, List<Date>>? = null
    private var selectedDatesSort = "monthly"
    private lateinit var chart: LineChart


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_financial_assessments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDashboardFinancialAssessmentsBinding.bind(view)

        getArgumentsBundle()
        getFinancialAssessmentScore()
        initializeDetailsButton()
        loadFinancialAssessmentScore()
    }

    private fun loadFinancialAssessmentScore() {
        val df = DecimalFormat("#.#")
        df.roundingMode = java.math.RoundingMode.UP
        val roundedValue = df.format(financialAssessmentTotalPercentage)

        binding.tvFinancialAssessmentPercent.text = "${roundedValue}%"
        binding.progressBar.progress = financialAssessmentTotalPercentage.toInt()
    }

    private fun initializeDetailsButton() {
        binding.btnDetails.setOnClickListener{
            val goToDetails = Intent(context, FinancialAssessmentsDetailsActivity::class.java)
            val bundle = Bundle()
            bundle.putString("date", selectedDatesSort)
            bundle.putString("user", user)
            if (user == "child")
                childID  = FirebaseAuth.getInstance().currentUser!!.uid

            bundle.putString("childID", childID)
            goToDetails.putExtras(bundle)
            startActivity(goToDetails)
        }
    }

    private fun getArgumentsBundle() {
        val args = arguments
        childID = args?.getString("childID").toString()
        user = arguments?.getString("user").toString()

        val date = args?.getString("date")
        val currUser = args?.getString("user")
        val childIDBundle = args?.getString("childID")

        if (childIDBundle != null)
            childID = childIDBundle

        if (currUser != null) {
            user = currUser
        }

        if (date != null) {
            selectedDatesSort = date
            assessmentsTaken.clear()
            data.clear()
            xAxisPoint = 0.00F
        }

    }

    private fun getFinancialAssessmentScore() {
        CoroutineScope(Dispatchers.Main).launch {
            getAssessmentAttempts()
            getDatesOfAttempts()
            setData()
            initializeGraph()
        }
    }

    private fun initializeGraph() {
        chart = view?.findViewById(R.id.financial_assessment_chart)!!

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
        yAxis.axisMinimum = 0f // set maximum y-value to 100%

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
        Log.d("nnbaaaa", "getDatesOfAttempts: "+ sortedDate)
    }

    @SuppressLint("NewApi")
    private suspend fun  setData() {
        when (selectedDatesSort) {
            /*"weekly" -> {
                selectedDates = getDaysOfWeek(sortedDate)
                graphData = addWeeklyData(selectedDates)
                binding.tvBalanceTitle.text = "This Week's Personal Financial Score Trend"
            }*/
            "monthly" -> {
                weeks = getWeeksOfCurrentMonth(sortedDate)
                getDataOfWeeksOfCurrentMonth(weeks!!)
                /*val group = groupDates(sortedDate, "month")
                iterateDatesByQuarter(group)*/
                binding.tvBalanceTitle.text = "This Month's Financial Assessments Score Trend"
            }
            "quarterly" -> {
                months = getMonthsOfQuarter(sortedDate)
                getDataOfMonthsOfCurrentQuarter(months!!)
                binding.tvBalanceTitle.text = "This Quarter's Financial Assessments Score Trend"
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
    private suspend fun getDataOfMonthsOfCurrentQuarter(months: Map<Int, List<Date>>) {
        for ((month, datesInMonth) in months) {
            //var totalAmount = 0.00F
            for (date in datesInMonth) {
                for (attempt in assessmentsTaken) {
                    //Convert both dates to LocalDate so they can be compared regardless of the time
                    val weekDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    val attemptDate = attempt.dateTaken?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                    if (attemptDate != null && weekDate == attemptDate) {
                        Log.d("calvin", "attemptDate: "+attemptDate)
                        Log.d("calvin", "weekDate: "+weekDate)
                        if (assessmentsTaken.isNotEmpty()) {
                            nAttempt++
                            Log.d("nAttempt", "nAttempt: "+nAttempt)

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
                        }
                    }
                }
            }
            computeForPercentages()
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
    private suspend fun getDataOfWeeksOfCurrentMonth(weeks: Map<Int, List<Date>>) {
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
                        }
                    }
                }
            }
            computeForPercentages()
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
        calculateFinancialAssessmentScore()
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

    private fun calculateFinancialAssessmentScore() {
        val totalSum = spendingPercentage + savingPercentage + financialGoalsPercentage + budgetingPercentage
        val maxPossibleSum = 4 * 100  // assuming the maximum possible value for each variable is 100

        /*Log.d("dogdays", "spendingPercentage: "+spendingPercentage)
        Log.d("dogdays", "savingPercentage: "+savingPercentage)
        Log.d("dogdays", "financialGoalsPercentage: "+financialGoalsPercentage)
        Log.d("dogdays", "budgetingPercentage: "+budgetingPercentage)*/

        val financialAssessmentPerformance = (totalSum.toDouble() / maxPossibleSum) * 100
        Log.d("nAttempt", "financialAssessmentPerformance: "+financialAssessmentPerformance)

        financialAssessmentTotalPercentage += financialAssessmentPerformance
        data.add(Entry(xAxisPoint, financialAssessmentPerformance.toFloat()))
        xAxisPoint++

        //Update to be used in the leaderboard
        /*firestore.collection("ChildUser").document(childID).update("assessmentPerformance", percentage)*/

        /*if (user == "Child")
            setTextPerformanceChild(percentage)
        else setTextPerformanceParent(percentage)*/

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


/*    private suspend fun getScores() {
        if (assessmentsTaken.isNotEmpty()) {
            for (assessment in assessmentsTaken) {
                val assessmentDocument = firestore.collection("Assessments")
                    .document(assessment.assessmentID!!).get().await()
                val assessmentObject = assessmentDocument.toObject<FinancialAssessmentDetails>()

                val percentage = getPercentage(assessment)
                when (assessmentObject?.assessmentCategory) {
                    "Goal Setting" -> financialGoalsScores.add(percentage)
                    "Saving" -> savingScores.add(percentage)
                    "Budgeting" -> budgetingScores.add(percentage)
                    "Spending" -> spendingScores.add(percentage)
                }
            }
            computeForPercentages()
        } else setEmptyAssessmentText()
    }

    private fun setEmptyAssessmentText() {
        binding.ivScore.setImageResource(R.drawable.bad)
        binding.textViewProgress.visibility = View.GONE

        binding.textViewPerformanceText.text = "Bad"
        binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.red))
        val message = if (user == "Child")
            "You haven't taken any assessments yet!"
        else "Your child hasn't taken any assessments yet!"
        binding.tvPerformanceText.text = message

        binding.tvTopPerformingConcept.text = "N/A"

        binding.tvConcept2nd.text = "N/A"

        binding.tvConcept3rd.text = "N/A"

        binding.tvConcept4th.text = "N/A"
    }

    private fun setTextPerformanceChild(percentage: Double) {
        Log.d("xcvxcvxcxz", "setTextPerformanceChild: "+percentage)
        if (percentage >= 90) {
            binding.ivScore.setImageResource(R.drawable.excellent)
            binding.textViewPerformanceText.text = "Excellent"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.dark_green))
            binding.tvPerformanceText.text =
                "Keep up the excellent work! You have a strong understanding of financial concepts and are making smart choices with money!"
            //showSeeMoreButton()
        } else if (percentage < 96 && percentage >= 86) {
            binding.ivScore.setImageResource(R.drawable.amazing)
            binding.textViewPerformanceText.text = "Amazing"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.amazing_green))

            binding.tvPerformanceText.text =
                "Great job! You have a good grasp of financial concepts and are starting to make smart choices with your money!"
            //showSeeMoreButton()
        } else if (percentage < 86 && percentage >= 76) {
            binding.ivScore.setImageResource(R.drawable.great)
            binding.textViewPerformanceText.text = "Great"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.green))
            binding.tvPerformanceText.text =
                "Uh oh! You have limited knowledge of financial concepts and there are many areas where you need to improve!"
            //showReviewButton()
        } else if (percentage < 76 && percentage >= 66) {
            binding.ivScore.setImageResource(R.drawable.good)
            binding.textViewPerformanceText.text = "Good"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.light_green))
            binding.tvPerformanceText.text =
                "Great! You have an average understanding of financial concepts and are making some smart choices with your money, but there is room for improvement!"
            //showSeeMoreButton()
        } else if (percentage < 66 && percentage >= 56) {
            binding.ivScore.setImageResource(R.drawable.average)
            binding.textViewPerformanceText.text = "Average"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.yellow))
            binding.tvPerformanceText.text =
                "Nice work! You have some basic knowledge of financial concepts, but there are many areas where you could improve!"
            //showReviewButton()
        } else if (percentage < 56 && percentage >= 46) {
            binding.ivScore.setImageResource(R.drawable.nearly_there)
            binding.textViewPerformanceText.text = "Nearly There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            binding.tvPerformanceText.text =
                "Uh oh! You have limited knowledge of financial concepts and there are many areas where you need to improve!"
            //showReviewButton()
        }  else if (percentage < 46 && percentage >= 36) {
            binding.ivScore.setImageResource(R.drawable.almost_there)
            binding.textViewPerformanceText.text = "Almost There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.almost_there_yellow))
            binding.tvPerformanceText.text =
                "Uh oh! You have limited knowledge of financial concepts and there are many areas where you need to improve!"
            //showReviewButton()
        } else if (percentage < 36 && percentage >= 26) {
            binding.ivScore.setImageResource(R.drawable.getting_there)
            binding.textViewPerformanceText.text = "Getting There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.getting_there_orange))
            binding.tvPerformanceText.text =
                "Uh oh! You have limited knowledge of financial concepts and there are many areas where you need to improve!"
            //showReviewButton()
        } else if (percentage < 26 && percentage >= 16) {
            binding.ivScore.setImageResource(R.drawable.not_quite_there_yet)
            binding.textViewPerformanceText.text = "Not Quite\n There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.not_quite_there_red))
            binding.tvPerformanceText.text =
                "Uh oh! You have limited knowledge of financial concepts and there are many areas where you need to improve!"
            //showReviewButton()
        } else if (percentage < 15 ) {
            binding.ivScore.setImageResource(R.drawable.bad)
            binding.textViewPerformanceText.text = "Needs\nImprovement"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.red))
            binding.tvPerformanceText.text =
                "Uh oh! You have limited knowledge of financial concepts and there are many areas where you need to improve!"
            //showReviewButton()
        }
    }

    private fun setTextPerformanceParent(percentage: Double) {
        if (percentage >= 96) {
            binding.ivScore.setImageResource(R.drawable.excellent)
            binding.textViewPerformanceText.text = "Excellent"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.dark_green))
            binding.tvPerformanceText.text =
                "Excellent! Your child consistently makes informed financial decisions and demonstrates a good understanding of key financial concepts.!"
            //showSeeMoreButton()
        } else if (percentage < 96 && percentage >= 86) {
            binding.ivScore.setImageResource(R.drawable.amazing)
            binding.textViewPerformanceText.text = "Amazing"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.amazing_green))
            binding.tvPerformanceText.text =
                "Great! Your child generally makes sound financial decisions and has a solid grasp of important financial concepts, but may make occasional mistakes or need some guidance in certain areas.!"
            //showSeeMoreButton()
        } else if (percentage < 86 && percentage >= 76) {
            binding.ivScore.setImageResource(R.drawable.great)
            binding.textViewPerformanceText.text = "Great"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.green))
            binding.tvPerformanceText.text =
                "Great! Your child generally makes sound financial decisions and has a solid grasp of important financial concepts, but may make occasional mistakes or need some guidance in certain areas.!"
            //showSeeMoreButton()
        } else if (percentage < 76 && percentage >= 66) {
            binding.ivScore.setImageResource(R.drawable.good)
            binding.textViewPerformanceText.text = "Good"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.light_green))
            binding.tvPerformanceText.text =
                "Good! Your child has some understanding of basic financial concepts and can make simple financial decisions, but may need significant guidance and education in more complex financial matters."
            //showSeeMoreButton()
        } else if (percentage < 66 && percentage >= 56) {
            binding.ivScore.setImageResource(R.drawable.average)
            binding.textViewPerformanceText.text = "Average"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.yellow))
            binding.tvPerformanceText.text =
                "Okay! Your child has limited financial knowledge and struggles to make good financial decisions without significant guidance and education."
            //showReviewButton()
        } else if (percentage < 56 && percentage >= 46) {
            binding.ivScore.setImageResource(R.drawable.nearly_there)
            binding.textViewPerformanceText.text = "Nearly There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            binding.tvPerformanceText.text =
                "Uh oh! Your child lacks basic financial literacy and requires immediate education and guidance to improve their financial decision-making."
            //showReviewButton()
        }  else if (percentage < 46 && percentage >= 36) {
            binding.ivScore.setImageResource(R.drawable.almost_there)
            binding.textViewPerformanceText.text = "Almost There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.almost_there_yellow))
            binding.tvPerformanceText.text =
                "Uh oh! Your child lacks basic financial literacy and requires immediate education and guidance to improve their financial decision-making."
            //showReviewButton()
        } else if (percentage < 36 && percentage >= 26) {
            binding.ivScore.setImageResource(R.drawable.getting_there)
            binding.textViewPerformanceText.text = "Getting There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.getting_there_orange))
            binding.tvPerformanceText.text =
                "Uh oh! Your child lacks basic financial literacy and requires immediate education and guidance to improve their financial decision-making."
            //showReviewButton()
        } else if (percentage < 26 && percentage >= 16) {
            binding.ivScore.setImageResource(R.drawable.not_quite_there_yet)
            binding.textViewPerformanceText.text = "Not Quite\n" +
                    "There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.not_quite_there_red))
            binding.tvPerformanceText.text =
                "Uh oh! Your child lacks basic financial literacy and requires immediate education and guidance to improve their financial decision-making."
            //showReviewButton()
        } else if (percentage < 15) {
            binding.ivScore.setImageResource(R.drawable.bad)
            binding.textViewPerformanceText.text = "Needs\n" +
                    "Improvement"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.red))
            binding.tvPerformanceText.text =
                "Uh oh! Your child lacks basic financial literacy and requires immediate education and guidance to improve their financial decision-making."
            //showReviewButton()
        }
    }*/

}