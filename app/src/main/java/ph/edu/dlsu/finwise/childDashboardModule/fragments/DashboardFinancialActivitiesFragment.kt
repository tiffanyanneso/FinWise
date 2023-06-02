package ph.edu.dlsu.finwise.childDashboardModule.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentDashboardFinancialActivitiesBinding
import ph.edu.dlsu.finwise.model.*
import ph.edu.dlsu.finwise.parentFinancialManagementModule.ParentFinancialManagementActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class DashboardFinancialActivitiesFragment : Fragment() {
    private lateinit var binding: FragmentDashboardFinancialActivitiesBinding
    private var firestore = Firebase.firestore
    private var userType = "child"

    private var xAxisPoint =0.00f
    private var iteration =0.00f
    private val data = mutableListOf<Entry>()
    var mediaPlayer: MediaPlayer? = MediaPlayer()

    private lateinit var userID: String
    private var age = 0


    private var weeks: Map<Int, List<Date>>? = null
    private var months: Map<Int, List<Date>>? = null
    private var selectedDatesSort = "monthly"
    private lateinit var chart: LineChart

    private lateinit var sortedDateGoalSetting: List<Date>
    private lateinit var sortedDateSaving: List<Date>
    private lateinit var sortedDateSpending: List<Date>
    private lateinit var sortedDateBudgeting: List<Date>
    private lateinit var sortedDate: List<Date>

    var nGoals = 0.00F
    var nOnTime = 0.00F
    var nRatings = 0
    var overallRating = 0.00F

    private var budgetItemCount = 0.00F
    private var nBudgetItems = 0.00F
    private var purchasedBudgetItemCount = 0.00F

    private var overSpending = 0.00F
    private var nPlanned = 0.00F
    private var nTotalPurchased = 0.00F

    private var nParent = 0

    private var savingPercentage = 0.00F
    private var spendingPercentage = 0.00F
    private var goalSettingPercentage = 0.00F
    private var budgetingPercentage = 0.00F
    private var overspendingPercentage = 0.00F

    private var savingPercentageArray = ArrayList<Float?>()
    private var spendingPercentageArray = ArrayList<Float?>()
    private var goalSettingPercentageArray = ArrayList<Float?>()
    private var budgetingPercentageArray = ArrayList<Float?>()
    private var overspendingPercentageArray = ArrayList<Float?>()

    private var nGoalSettingCompleted = 0
    private var nSpendingCompleted = 0
    private var nBudgetingCompleted = 0

    private var totalBudgetAccuracy = 0.00F

    private var financialActivitiesPerformance = 0.00F

    private var goalRatingObjectArray = ArrayList<GoalRating>()
    private var savingObjectArray = ArrayList<FinancialGoals>()
    private var spendingObjectArray = ArrayList<FinancialActivities>()
    private var spendingDocumentArray = ArrayList<QueryDocumentSnapshot>()
    private var budgetingDocumentArray = ArrayList<QueryDocumentSnapshot>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_financial_activities, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDashboardFinancialActivitiesBinding.bind(view)
        getArgumentsBundle()
        initializeBalanceLineGraph()

        binding.title.text = "Financial Activities Performance"
    }

    private fun initializeBalanceLineGraph() {
        CoroutineScope(Dispatchers.Main).launch {
            getChildAge()
            getFinancialActivities()
            getUniqueDates()
            setData()
            initializeGraph()
            setPerformanceView()
            loadView()
        }
    }

    private fun loadView() {
        binding.layoutLoading.visibility = View.GONE
        binding.layoutMain.visibility = View.VISIBLE
    }

    private suspend fun getData(weeks: Map<Int, List<Date>>) {
        for ((weekNumber, datesInWeek) in weeks) {
            datesInWeek.forEach { date ->

                if (age in 10..11 && goalRatingObjectArray.isNotEmpty()) {
                    for (goal in goalRatingObjectArray) {
                        getGoalSettingPerformance(date, goal)
                    }
                }

                if (savingObjectArray.isNotEmpty()) {
                    for (goal in savingObjectArray) {
                        getSavingPerformance(date, goal)
                    }
                }

                if (budgetingDocumentArray.isNotEmpty()) {
                    for (activity in budgetingDocumentArray) {
                        getBudgetingPerformanceScore(date, activity)
                    }
                }

                if (spendingObjectArray.isNotEmpty()) {
                    for (spending in spendingObjectArray) {
                        getOverspending(date, spending)
                    }
                }

                if (age > 9 && spendingDocumentArray.isNotEmpty()) {
                    for (spendingActivity in spendingDocumentArray) {
                        getPurchasePlanning(date, spendingActivity)
                    }
                }

                getPercentages()
            }
            computeForPercentages()
            resetVariables()
        }
    }


    private fun resetVariables() {
        nGoalSettingCompleted = 0
        nBudgetingCompleted = 0
        nSpendingCompleted = 0

        // Budgeting
        nParent = 0
        purchasedBudgetItemCount = 0F
        totalBudgetAccuracy = 0F

        //Spending
        nPlanned = 0F
        nTotalPurchased = 0F

        //Overspending
        nBudgetItems = 0F
        overSpending = 0F

        nGoals = 0.00F
        nOnTime = 0.00F
        nRatings = 0
        overallRating = 0.00F
    }

    private fun getPercentages() {

        if (age in 10..11 && goalRatingObjectArray.isNotEmpty()) {
            var goalSettingPercentage  = ((overallRating / nRatings)/5)* 100
            goalSettingPercentage = checkIfNaNPercentage(goalSettingPercentage)
            goalSettingPercentageArray.add(goalSettingPercentage)
        }


        //Saving
        var savingPercentage = (nOnTime/nGoals)*100
        savingPercentage = checkIfNaNPercentage(savingPercentage)
        savingPercentageArray.add(savingPercentage)



        // Budgeting
        var budgetingPercentage = if (isSpendingActivityCompleted) {
            if (purchasedBudgetItemCount != 0.00F)
                ((totalBudgetAccuracy/purchasedBudgetItemCount)
                        + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2
            else
                ((1 - (nParent.toFloat()/budgetItemCount)) * 100)

        } else {
            if (purchasedBudgetItemCount != 0.00F)
                ((totalBudgetAccuracy/purchasedBudgetItemCount) + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2
            else
                ((1 - (nParent.toFloat()/budgetItemCount)) * 100)
        }
        Log.d("liven", "budgetingPercentage: "+budgetingPercentage)
        budgetingPercentage = checkIfNaNPercentage(budgetingPercentage)
        budgetingPercentageArray.add(budgetingPercentage)

        //Overspending
        var overspendingPercentage = (overSpending/nBudgetItems)
        overspendingPercentage = checkIfNaNPercentage(overspendingPercentage)
        overspendingPercentageArray.add(overspendingPercentage)

        //Spending
        var spendingPercentage = ((1-overspendingPercentage)*100 + ((nPlanned/nTotalPurchased)*100)) /2
        spendingPercentage = checkIfNaNPercentage(spendingPercentage)
        spendingPercentageArray.add(spendingPercentage)
    }

    private fun checkIfNaNPercentage(percentage: Float): Float {
        var value = percentage
        if (percentage.isNaN())
            value = 0F
        return value
    }

    private fun computeForPercentages() {

        // Calculate total percentages for each array
        savingPercentage = calculateAveragePercentage(savingPercentageArray)
        spendingPercentage = calculateAveragePercentage(spendingPercentageArray)
        goalSettingPercentage = calculateAveragePercentage(budgetingPercentageArray)
        budgetingPercentage = calculateAveragePercentage(goalSettingPercentageArray)
        overspendingPercentage = calculateAveragePercentage(overspendingPercentageArray)

        clearArrays()
        checkIfNaN()
        calculateFinancialActivitiesScore()
    }

    private fun calculateAveragePercentage(percentageArray: ArrayList<Float?>): Float {
        Log.d("pkliop", "percentageArray: "+percentageArray)
        val nonNullValues = percentageArray.filterNotNull()
        val sum = nonNullValues.sum()
        val count = nonNullValues.size
        val average = if (count > 0) sum / count else 0.0F
        return average.coerceIn(0.0F, 100.0F)
    }


    private fun checkIfNaN() {
        val percentages = mutableListOf(savingPercentage, spendingPercentage, budgetingPercentage,
            goalSettingPercentage, overspendingPercentage)


        for (i in percentages.indices) {
            Log.d("hayabusa", "checkIfNaN: "+percentages[i])
            if (percentages[i].isNaN()) {
                when (i) {
                    0 -> savingPercentage = 0.00f
                    1 -> spendingPercentage = 0.00f
                    2 -> budgetingPercentage = 0.00f
                    3 -> goalSettingPercentage = 0.00f
                    4 -> overspendingPercentage = 0.00f
                }
            }
        }
    }

    private fun clearArrays() {
        savingPercentageArray.clear()
        spendingPercentageArray.clear()
        goalSettingPercentageArray.clear()
        budgetingPercentageArray.clear()
        overspendingPercentageArray.clear()
    }


    private suspend fun setData() {
        when (selectedDatesSort) {
            /*"weekly" -> {
                selectedDates = getDaysOfWeek(sortedDate)
                graphData = addWeeklyData(selectedDates)
                binding.tvBalanceTitle.text = "This Week's Personal Financial Score Trend"
            }*/
            "monthly" -> {
                weeks = getWeeksOfCurrentMonth(sortedDate)
                Log.d("lapos", "weeks: "+weeks)
                getData(weeks!!)
                /*val group = groupDates(sortedDate, "month")
                iterateDatesByQuarter(group)*/
                binding.tvBalanceTitle.text = "This Month's Financial Activities Score Trend"
            }
            "quarterly" -> {
                months = getMonthsOfQuarter(sortedDate)
                Log.d("lapos", "months: "+months)
                getData(months!!)
                binding.tvBalanceTitle.text = "This Quarter's Financial Activities Score Trend"
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

    private fun initializeGraph() {
        chart = binding.financialActivitiesChart

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
        val dataSet = LineDataSet(data, "Financial Activities Performance")
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
        chart.description.isEnabled = false

        // Add a Peso sign in the data points in the graph
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val formattedValue = String.format("%.1f", value)

                // Add a percent sign to the formatted value and return it
                return "$formattedValue%"
            }
        }

        chart.setNoDataText("You have no data yet. Come back to this module after you have used the app.")
        chart.invalidate()
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


    private fun getUniqueDates() {
        val dates = mutableListOf<Date>()
        val datesGoalSetting = mutableListOf<Date>()
        val datesSaving = mutableListOf<Date>()
        val datesSpending = mutableListOf<Date>()
        val datesBudgeting = mutableListOf<Date>()

        // Add dates from goalRatingArray
        for (goalRating in goalRatingObjectArray) {
            goalRating.lastUpdated?.let { timestamp ->
                val date = timestamp.toDate()
                dates.add(date)
                datesGoalSetting.add(date)
            }
        }

        // Add dates from savingArray
        for (saving in savingObjectArray) {
            saving.dateCompleted?.let { timestamp ->
                val date = timestamp.toDate()
                dates.add(date)
                datesSaving.add(date)
            }
        }

        // Add dates from spendingArray
        for (spending in spendingObjectArray) {
            spending.dateCompleted?.let { timestamp ->
                val date = timestamp.toDate()
                dates.add(date)
                datesSpending.add(date)
            }
        }

        // Add dates from budgetingArray
        for (budgeting in budgetingDocumentArray) {
            val budgetingObject = budgeting.toObject<FinancialActivities>()
            budgetingObject.dateCompleted?.let { timestamp ->
                val date = timestamp.toDate()
                dates.add(date)
                datesBudgeting.add(date)
            }
        }

        // Remove duplicates and sort the dates
        sortedDate = dates.distinct().sorted()
        sortedDateGoalSetting = datesGoalSetting.distinct().sorted()
        sortedDateSaving = datesSaving.distinct().sorted()
        sortedDateSpending = datesSpending.distinct().sorted()
        sortedDateBudgeting = datesBudgeting.distinct().sorted()
    }


    private suspend fun getFinancialActivities() {
        getGoalSettingActivities()
        getSavingActivities()
        getSpendingActivities()
        getBudgetingActivities()
    }

    private suspend fun getBudgetingActivities() {
        val financialActivitiesDocuments = firestore.collection("FinancialActivities")
            .whereEqualTo("childID", userID)
            .whereEqualTo("financialActivityName", "Spending")
            .whereEqualTo("status", "Completed").get().await()

        for (budgeting in financialActivitiesDocuments) {
            //val budgetingObject = budgeting.toObject<FinancialActivities>()
            budgetingDocumentArray.add(budgeting)
        }

    }

    private suspend fun getSpendingActivities() {
        val financialActivitiesDocuments = firestore.collection("FinancialActivities")
            .whereEqualTo("childID", userID)
            .whereEqualTo("financialActivityName", "Spending")
            .whereEqualTo("status", "Completed").get().await()

        for (spending in financialActivitiesDocuments) {
            spendingDocumentArray.add(spending)
            val spendingObject = spending.toObject<FinancialActivities>()
            spendingObjectArray.add(spendingObject)
        }
        spendingObjectArray.sortByDescending { it.dateCompleted }
    }

    private suspend fun getSavingActivities() {
        val financialGoalsDocuments = firestore.collection("FinancialGoals")
            .whereEqualTo("childID", userID)
            .whereEqualTo("status", "Completed").get().await()

        for (saving in financialGoalsDocuments) {
            val savingObject = saving.toObject<FinancialGoals>()
            savingObjectArray.add(savingObject)
        }
        savingObjectArray.sortByDescending { it.dateCompleted }
    }

    private suspend fun getGoalSettingActivities() {
        val goalRatingDocuments = firestore.collection("GoalRating")
            .whereEqualTo("childID", userID).get().await()

        for (rating in goalRatingDocuments) {
            val ratingObject = rating.toObject<GoalRating>()
            goalRatingObjectArray.add(ratingObject)
        }
        goalRatingObjectArray.sortByDescending { it.lastUpdated }
    }

    private suspend fun getOverspending(date: Date, spending: FinancialActivities) {
        val weekDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val activityDate = spending.dateCompleted?.toDate()?.toInstant()
            ?.atZone(ZoneId.systemDefault())?.toLocalDate()
        if (activityDate != null && weekDate == activityDate) {
            nSpendingCompleted++
            val financialActivityDocuments = firestore.collection("FinancialActivities")
                .whereEqualTo("financialGoalID", spending.financialGoalID)
                .whereEqualTo("financialActivityName", "Budgeting")
                .whereEqualTo("status", "Completed").get().await()

            val budgetingID = financialActivityDocuments.documents[0].id
            val budgetItemsDocuments = firestore.collection("BudgetItems")
                .whereEqualTo("financialActivityID", budgetingID)
                .whereEqualTo("status", "Active").get().await()
            nBudgetItems += budgetItemsDocuments.size()

            for (budgetItem in budgetItemsDocuments) {
                val budgetItemObject = budgetItem.toObject<BudgetItem>()
                checkOverSpending(budgetItem.id, budgetItemObject.amount!!)
            }
        }
    }

    private suspend fun checkOverSpending(budgetItemID:String, budgetItemAmount:Float){
        Log.d("kelan", "budgetItemID: "+budgetItemID)
        Log.d("kelan", "budgetItemAmount: "+budgetItemAmount)
        val transactionsDocuments = firestore.collection("Transactions")
            .whereEqualTo("budgetItemID", budgetItemID)
            .whereEqualTo("transactionType", "Expense").get().await()

        var amountSpent = 0.00F
            for (expense in transactionsDocuments) {
                val expenseObject = expense.toObject<Transactions>()
                amountSpent+= expenseObject.amount!!
            }
        //they spent more than their allocated budget
            if (amountSpent > budgetItemAmount)
                overSpending++

    }

    private fun calculateFinancialActivitiesScore() {
        checkIfNaNFinancialActivitiesScores()
        var financialActivitiesPerformanceScore = 0F
        if (age == 9 || age == 12) {
            if (nGoals == 0.00F) {
                var bitmap = BitmapFactory.decodeResource(resources, R.drawable.peso_coin)
                binding.ivScore.setImageBitmap(bitmap)
                binding.tvPerformanceStatus.visibility = View.GONE
                binding.tvPerformancePercentage.visibility = View.GONE
                binding.tvPerformanceText.text = "Complete financial activities to see your score here"
            }
            else if (nBudgetingCompleted == 0)
                financialActivitiesPerformanceScore =  savingPercentage
            else if (nSpendingCompleted == 0)
                financialActivitiesPerformanceScore = (savingPercentage + budgetingPercentage)/2
            else
                financialActivitiesPerformanceScore = ((savingPercentage + budgetingPercentage + spendingPercentage) / 3)
        }
        else if (age == 10 || age == 11)
            if (nGoalSettingCompleted == 0) {
                var bitmap = BitmapFactory.decodeResource(resources, R.drawable.peso_coin)
                binding.ivScore.setImageBitmap(bitmap)
                binding.tvPerformanceStatus.visibility = View.GONE
                binding.tvPerformancePercentage.visibility = View.GONE
                binding.tvPerformanceText.text = "Complete financial activities to see your score here"
            }
            else if (nGoals == 0.00F)
                financialActivitiesPerformance = goalSettingPercentage
            else if (nBudgetingCompleted == 0)
                financialActivitiesPerformanceScore =  (goalSettingPercentage + savingPercentage) /2
            else if (nSpendingCompleted == 0)
                financialActivitiesPerformanceScore = (goalSettingPercentage + savingPercentage + budgetingPercentage)/3
            else
                financialActivitiesPerformanceScore = ((goalSettingPercentage + savingPercentage + budgetingPercentage + spendingPercentage) / 4)

        iteration++
        financialActivitiesPerformance += financialActivitiesPerformanceScore

        val roundedFinancialActivitiesPerformance = (financialActivitiesPerformanceScore * 10).roundToInt() / 10
        data.add(Entry(xAxisPoint, roundedFinancialActivitiesPerformance.toFloat()))
        xAxisPoint++
    }



    private suspend fun getPurchasePlanning(date: Date, spendingActivity: QueryDocumentSnapshot) {
        //items planned / all the items they bought * 100
        val spendingObject = spendingActivity.toObject<FinancialActivities>()
        if (spendingObject.status == "Completed") {
            val weekDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val activityDate = spendingObject.dateCompleted?.toDate()?.toInstant()
                ?.atZone(ZoneId.systemDefault())?.toLocalDate()
            if (activityDate != null && weekDate == activityDate) {
                val shoppingListItemsDocuments = firestore.collection("ShoppingListItems")
                    .whereEqualTo("spendingActivityID", spendingActivity.id).get().await()
                for (shoppingListItem in shoppingListItemsDocuments) {
                    val shoppingListItemObject = shoppingListItem.toObject<ShoppingListItem>()
                    if (shoppingListItemObject.status == "Purchased")
                        nPlanned++
                }
                val transactionsDocuments = firestore.collection("Transactions")
                    .whereEqualTo("financialActivityID", spendingActivity.id)
                    .whereEqualTo("transactionType", "Expense").get().await()
                nTotalPurchased += transactionsDocuments.size().toFloat()
            }
        }
    }

    private suspend fun getBudgetingPerformanceScore(date: Date, activity: QueryDocumentSnapshot) {
        val budgetingObject = activity.toObject<FinancialActivities>()
        val weekDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val activityDate = budgetingObject.dateCompleted?.toDate()?.toInstant()
            ?.atZone(ZoneId.systemDefault())?.toLocalDate()
        if (activityDate != null && weekDate == activityDate) {
            nBudgetingCompleted++
            processBudgetItems(activity)
        }
    }


    private suspend fun processBudgetItems(activity: QueryDocumentSnapshot) {
        val budgetItemsDocuments = firestore.collection("BudgetItems")
            .whereEqualTo("financialActivityID", activity.id)
            .whereEqualTo("status", "Active").get().await()

        for (budgetItem in budgetItemsDocuments) {
            budgetItemCount++
            val budgetItemObject = budgetItem.toObject<BudgetItem>()
            //parental involvement
            getParentalInvolvementBudget(budgetItemObject)
            getBudgetAccuracy(activity.id, budgetItem.id, budgetItemObject)
        }

    }

    private var isSpendingActivityCompleted = true

    private suspend fun getBudgetAccuracy(budgetingActivityID:String, budgetItemID:String,
                                          budgetItemObject:BudgetItem) {
        val financialActivitiesDocument = firestore.collection("FinancialActivities")
            .document(budgetingActivityID).get().await()
        val financialActivitiesDocuments = firestore.collection("FinancialActivities")
            .whereEqualTo("financialGoalID",
                financialActivitiesDocument.toObject<FinancialActivities>()!!.financialGoalID!!)
            .whereEqualTo("financialActivityName", "Spending").get().await()
        val spendingActivity = financialActivitiesDocuments.documents[0].toObject<FinancialActivities>()

        //TODO: get date first
        if (spendingActivity?.status == "Completed") {
            //budget accuracy
            purchasedBudgetItemCount++
            val transactionsDocuments = firestore.collection("Transactions")
                .whereEqualTo("budgetItemID", budgetItemID).get().await()
            var spent = 0.00F
            for (transaction in transactionsDocuments)
                spent += transaction.toObject<Transactions>().amount!!
            totalBudgetAccuracy +=
                (100 - (kotlin.math.abs(budgetItemObject.amount!! - spent)
                        / budgetItemObject.amount!!) * 100)


        } else {
            isSpendingActivityCompleted = false
            budgetingPercentage = if (purchasedBudgetItemCount != 0.00F)
                ((totalBudgetAccuracy/purchasedBudgetItemCount) + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2
            else
                ((1 - (nParent.toFloat()/budgetItemCount)) * 100)
        }
        //println("print budgeting " + budgetingPercentage )
    }


    private suspend fun getParentalInvolvementBudget(budgetItemObject: BudgetItem) {
        val userDocument = firestore.collection("Users")
            .document(budgetItemObject.createdBy.toString()).get().await()
        //parent is the one who added the budget item
        if (userDocument.toObject<Users>()?.userType == "Parent")
            nParent++
    }

    private fun getGoalSettingPerformance(date: Date, goal: GoalRating) {
            //Convert both dates to LocalDate so they can be compared regardless of the time
            val weekDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val activityDate = goal.lastUpdated?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            if (activityDate != null && weekDate == activityDate) {
                nGoalSettingCompleted++
                nRatings++
                overallRating += goal.overallRating!!
            }
    }

    private fun getSavingPerformance(date: Date, goal: FinancialGoals) {
        val weekDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val activityDate = goal.dateCompleted?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
        if (activityDate != null && weekDate == activityDate) {
            nGoals++
            if (goal.dateCompleted != null) {
                val targetDate = goal.targetDate!!.toDate()
                val completedDate = goal.dateCompleted!!.toDate()

                //goal was completed before the target date, meaning it was completed on time
                if (completedDate.before(targetDate) || completedDate == targetDate)
                    nOnTime++
            }
        }
    }


    private fun setPerformanceView() {

        if (financialActivitiesPerformance.isNaN())
            financialActivitiesPerformance = 0.0F

        financialActivitiesPerformance /= iteration

        if (financialActivitiesPerformance.isNaN())
            financialActivitiesPerformance = 0.0F

        val imageView = binding.ivScore
        val message: String
        val performance: String
        val bitmap: Bitmap

        //TODO: Change audio
        var audio = 0

        if (financialActivitiesPerformance >= 96F) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_activities_excellent
            else
                R.raw.dashboard_financial_activities_excellent

            performance = "Excellent!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.dark_green))
            message = if (userType == "Parent")
                "Your child is a financial superstar! Their skills in goal setting, saving, budgeting, and spending are commendable!"
            else "Excellent work superstar! Continue exploring and exercising your financial decision-making skills."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.excellent)
        } else if (financialActivitiesPerformance in 86.0..95.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_activities_amazing
            else
                R.raw.dashboard_financial_activities_amazing

            performance = "Amazing!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.amazing_green))
            message = if (userType == "Parent")
                "Your child's financial skills are exceptional. Encourage them to continue completing goals and financial activities!"
            else "Amazing work! Keep exploring and accomplishing different financial activities."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.amazing)
        } else if (financialActivitiesPerformance in 76.0..85.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_activities_great
            else
                R.raw.dashboard_financial_activities_great

            performance = "Great!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.green))
            message = if (userType == "Parent")
                "Your child's performance in activities showcases their strong financial decision-making skills. Encourage them to keep it up!"
            else "Keep mastering the art of financial decision-making through financial activities! Your financial future looks bright!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.great)
        } else if (financialActivitiesPerformance in 66.0..75.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_activities_good
            else
                R.raw.dashboard_financial_activities_good

            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.light_green))
            performance = "Good!"
            message = if (userType == "Parent")
                "Your child is becoming better at performing financial activities. Encourage them to continue!"
            else "Good job! Continue building your experience in performing financial activities, it’ll be useful in the long run."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.good)
        } else if (financialActivitiesPerformance in 56.0..65.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_activities_average
            else
                R.raw.dashboard_financial_activities_average

            performance = "Average"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.yellow))
            message = if (userType == "Parent")
                "Your child has a solid performance in activities. Allow them to participate in financial activities at home!"
            else "Keep exploring new financial activities, tracking your progress, and making smart financial choices!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.average)
        } else if (financialActivitiesPerformance in 46.0..55.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_activities_nearly_there
            else
                R.raw.dashboard_financial_activities_nearly_there

            performance = "Nearly\nThere"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            message = if (userType == "Parent")
                "Your child's commitment to financial activities is paying off. They are developing valuable skills!"
            else "Keep honing your financial decision-making through financial activities. Through practice, you'll get there soon!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.nearly_there)
        } else if (financialActivitiesPerformance in 36.0..45.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_activities_almost_there
            else
                R.raw.dashboard_financial_activities_almost_there

            performance = "Almost\nThere"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.almost_there_yellow))
            message = if (userType == "Parent")
                "Your child is gaining a better understanding of financial activities. Encourage them to set continue to set SMART goals!"
            else "Keep practicing goal setting, saving, budgeting, and spending. Your dedication will pay off!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.almost_there)
        } else if (financialActivitiesPerformance in 26.0..35.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_activities_getting_there
            else
                R.raw.dashboard_financial_activities_getting_there

            performance = "Getting\nThere"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.getting_there_orange))
            message = if (userType == "Parent")
                "Your child is taking steps towards financial literacy. Encourage them to keep performing financial activities!"
            else "Keep exploring ways to better set goals, save, budget, and spend. You’ll get there!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.getting_there)
        } else if (financialActivitiesPerformance in 16.0..25.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_activities_not_quite_there
            else
                R.raw.dashboard_financial_activities_not_quite_there

            performance = "Not Quite\nThere"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.not_quite_there_red))
            message = if (userType == "Parent")
                "Your child is beginning to get the hang of things. Help them out by allowing them to participate in household financial activities!"
            else "You are beginning to get the hang of things. Keep practicing by accomplishing financial activities!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.not_quite_there_yet)
        } else if (financialActivitiesPerformance < 15) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_activities_needs_improvement
            else
                R.raw.dashboard_financial_activities_needs_improvement

            performance = "Needs\nImprovement"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.red))
            message = if (userType == "Parent")
                "Help your child improve by encouraging and guiding them to perform financial activities!"
            else "Keep practicing your goal setting, saving, budgeting, and spending. You’ll get there!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        }
        else {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_activities_default
            else
                R.raw.dashboard_financial_activities_default

            performance = "Get Started!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.black))
            /*var date = "month"
            if (selectedDatesSort == "quarterly")
                date = "quarter"*/
            message = if (userType == "Parent")
                "Your child hasn't accomplished any financial activities yet!"
            else "You haven't accomplished any financial activities. Click the button to start!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.peso_coin)
            binding.tvPerformancePercentage.visibility = View.GONE
        }

        imageView.setImageBitmap(bitmap)
        binding.tvPerformanceText.text = message
        binding.tvPerformanceStatus.text = performance

        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.UP
        val roundedValue = df.format(financialActivitiesPerformance)
        binding.tvPerformancePercentage.text = "${roundedValue}%"

        mediaPlayer = MediaPlayer.create(context, audio)
        loadOverallAudio()
        loadButton()
    }

    private fun loadOverallAudio() {
        binding.btnAudioFinancialActivitiesScore.setOnClickListener {
            try {
                mediaPlayer.let { player ->
                    if (player?.isPlaying == true) {
                        player.pause()
                        player.seekTo(0)
                        return@setOnClickListener
                    }

                    player?.start()
                }
            } catch (e: IllegalStateException) {
                // Handle the exception, e.g., log an error or display a message
            }
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
        } catch (e: IllegalStateException) {
            // Handle the exception gracefully
            e.printStackTrace()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        releaseMediaPlayer()
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun initializeParentFinancialActivityBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString("childID", userID)
        return bundle
    }

    private fun loadButton() {
        binding.btnSeeMore.setOnClickListener {
            releaseMediaPlayer()
            val goToActivity = if (userType == "Parent") {
                val bundle = initializeParentFinancialActivityBundle()
                val intent = Intent(context, ParentFinancialManagementActivity::class.java)
                intent.putExtras(bundle)
                intent
            } else {
                Intent(context, PersonalFinancialManagementActivity::class.java)
            }
            startActivity(goToActivity)
        }
    }


    private fun checkIfNaNFinancialActivitiesScores() {
        val percentages = mutableListOf(savingPercentage, spendingPercentage, budgetingPercentage,
            goalSettingPercentage)



        for (i in percentages.indices) {
            if (percentages[i].isNaN()) {
                when (i) {
                    0 -> savingPercentage = 0.00f
                    1 -> spendingPercentage = 0.00f
                    2 -> budgetingPercentage = 0.00f
                    3 -> goalSettingPercentage = 0.00f
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getChildAge() {
        val ageDocument = firestore.collection("Users").document(userID).get().await()
        val child = ageDocument.toObject<Users>()
        //compute age
        Log.d("fsdfxcvx", "getChildAge: "+child?.birthday?.toDate())
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val from = LocalDate.now()
        val date = SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
        val to = LocalDate.parse(date.toString(), dateFormatter)
        val difference = Period.between(to, from)

        age = difference.years
    }

    private fun getArgumentsBundle() {
        val args = arguments

        val date = args?.getString("date")
        userID = args?.getString("childID").toString()

        if (date != null) {
            selectedDatesSort = date
            xAxisPoint = 0.00F
            financialActivitiesPerformance = 0F
            goalRatingObjectArray.clear()
            savingObjectArray.clear()
            spendingObjectArray.clear()
            spendingDocumentArray.clear()
            budgetingDocumentArray.clear()
        }
    }


}