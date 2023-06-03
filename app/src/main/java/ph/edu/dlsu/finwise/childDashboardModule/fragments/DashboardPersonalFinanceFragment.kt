package ph.edu.dlsu.finwise.childDashboardModule.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentDashboardPersonalFinanceBinding
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentFinancialActivity
import ph.edu.dlsu.finwise.parentFinancialManagementModule.ParentFinancialManagementActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class DashboardPersonalFinanceFragment : Fragment() {
    private lateinit var binding: FragmentDashboardPersonalFinanceBinding
    private var firestore = Firebase.firestore

    private var mediaPlayer: MediaPlayer? = MediaPlayer()

    private lateinit var childID: String

    private var userType = "child"

    val transactionsArrayList = ArrayList<Transactions>()
    private lateinit var sortedDate: List<Date>
    //private lateinit var days: List<Date>
    private var weeks: Map<Int, List<Date>>? = null
    private var months: Map<Int, List<Date>>? = null
    private var selectedDatesSort = "monthly"
    private lateinit var chart: LineChart
    var graphData = mutableListOf<Entry>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_personal_finance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDashboardPersonalFinanceBinding.bind(view)
        getArgumentsBundle()
        initializeBalanceLineGraph()
        //getPersonalFinancePerformance()

        binding.title.text = "Personal Finance Performance"
    }

    private fun initializeBalanceLineGraph() {
        firestore.collection("Transactions").whereEqualTo("userID", childID)
            .get().addOnSuccessListener { documents ->
                initializeTransactions(documents)
                sortedDate = getDatesOfTransactions(transactionsArrayList)
                setData()
                initializeGraph()
                loadView()
            }
    }

    private fun loadView() {
        binding.layoutLoading.visibility = View.GONE
        binding.layoutMain.visibility = View.VISIBLE
    }

    private fun initializeTransactions(documents: QuerySnapshot) {
        for (transactionSnapshot in documents) {
            //creating the object from list retrieved in db
            val transaction = transactionSnapshot.toObject<Transactions>()
            transactionsArrayList.add(transaction)
        }
        transactionsArrayList.sortByDescending { it.date }

    }

    private fun getDatesOfTransactions(transactionsArrayList: ArrayList<Transactions>): List<Date> {
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

    private fun  setData(): MutableList<Entry> {

        when (selectedDatesSort) {
            /*"weekly" -> {
                selectedDates = getDaysOfWeek(sortedDate)
                graphData = addWeeklyData(selectedDates)
                binding.tvBalanceTitle.text = "This Week's Personal Financial Score Trend"
            }*/
            "monthly" -> {
                weeks = getWeeksOfCurrentMonth(sortedDate)
                Log.d("agustus", "weeks: "+weeks)
                graphData = getDataOfWeeksOfCurrentMonth(weeks!!)

                /*val group = groupDates(sortedDate, "month")
                iterateDatesByQuarter(group)*/
                binding.tvBalanceTitle.text = "This Month's Personal Financial Score Trend"
            }
            "quarterly" -> {
                months = getMonthsOfQuarter(sortedDate)
                graphData =  getDataOfMonthsOfCurrentQuarter(months!!)
                Log.d("zaza", "QUARTER: "+graphData)
                binding.tvBalanceTitle.text = "This Quarter's Personal Financial Score Trend"
            }
        }
        return graphData
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

    private fun getDataOfMonthsOfCurrentQuarter(months: Map<Int, List<Date>>): MutableList<Entry> {
        val data = mutableListOf<Entry>()
        var totalIncome = 0.00f
        var income = 0.00f
        var totalExpense= 0.00f
        var expense= 0.00f
        var xAxisPoint =0.00f

        for ((month, datesInMonth) in months) {
            //var totalAmount = 0.00F
            for (date in datesInMonth) {
                for (transaction in transactionsArrayList) {
                    if (date.compareTo(transaction.date?.toDate()) == 0) {
                        if (transaction.transactionType == "Income"){
                            //totalAmount += transaction.amount!!
                            totalIncome += transaction.amount!!
                            income += transaction.amount!!
                        }
                        else {
                            //totalAmount -= transaction.amount!!
                            totalExpense += transaction.amount!!
                            expense += transaction.amount!!
                        }
                    }
                }
            }
            var personalFinancePerformancePercent = income/expense * 100
            if (personalFinancePerformancePercent > 200)
                personalFinancePerformancePercent = 200F

            val personalFinancePerformance = personalFinancePerformancePercent / 2

            data.add(Entry(xAxisPoint, personalFinancePerformance))
            income = 0.00F
            expense = 0.00F
            xAxisPoint++
        }

        val totalPersonalFinancePerformance = calculatePersonalFinancePerformance(totalIncome, totalExpense)
        setTotals(totalPersonalFinancePerformance)
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

    private fun getDataOfWeeksOfCurrentMonth(weeks: Map<Int, List<Date>>): MutableList<Entry> {
        val data = mutableListOf<Entry>()
        var totalIncome = 0.00f
        var income = 0.00f
        var totalExpense= 0.00f
        var expense= 0.00f
        var xAxisPoint =0.00f

        weeks.forEach { (weekNumber, datesInWeek) ->
            //var totalAmount = 0.00F
            datesInWeek.forEach { date ->
                for (transaction in transactionsArrayList) {
                    if (date.compareTo(transaction.date?.toDate()) == 0) {
                        if (transaction.transactionType == "Income"){
                            //totalAmount += transaction.amount!!
                            totalIncome += transaction.amount!!
                            income += transaction.amount!!
                        }
                        else {
                            //totalAmount -= transaction.amount!!
                            totalExpense += transaction.amount!!
                            expense += transaction.amount!!
                        }
                    }
                }
            }
            var personalFinancePerformancePercent = income/expense * 100
            if (personalFinancePerformancePercent > 200)
                personalFinancePerformancePercent = 200F

            val personalFinancePerformance = personalFinancePerformancePercent / 2

            data.add(Entry(xAxisPoint, personalFinancePerformance))
            income = 0.00F
            expense = 0.00F
            xAxisPoint++
        }

        val totalPersonalFinancePerformance = calculatePersonalFinancePerformance(totalIncome, totalExpense)
        setTotals(totalPersonalFinancePerformance)
        return data
    }

    private fun calculatePersonalFinancePerformance(totalIncome: Float, totalExpense: Float): Float {
        var personalFinancePerformancePercent = totalIncome/totalExpense * 100
        if (personalFinancePerformancePercent > 200)
            personalFinancePerformancePercent = 200F

        return personalFinancePerformancePercent / 2
    }

    private fun setTotals(totalPersonalFinancePerformance: Float) {
        var personalFinancePerformance = totalPersonalFinancePerformance
        if (totalPersonalFinancePerformance.isNaN())
            personalFinancePerformance = 0.00F

        val df = DecimalFormat("#.#")
        df.roundingMode = java.math.RoundingMode.UP
        val roundedValue = df.format(personalFinancePerformance)

        binding.tvPerformancePercentage.text = "${roundedValue}%"

        val imageView = binding.ivScore
        val message: String
        val performance: String
        val bitmap: Bitmap

        //TODO: Change audio
        var audio = 0

        if (personalFinancePerformance >= 96F) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_excellent
            else
                R.raw.dashboard_pfm_excellent

            performance = "Excellent!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.dark_green))
            message = if (userType == "Parent")
                "Your child is excellent at personal finance! They make exceptional financial decisions."
            else  "You are excellent at personal finance! Keep making exceptional financial decisions."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.excellent)
        } else if (personalFinancePerformance in 86.0..95.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_amazing
            else
                R.raw.dashboard_pfm_amazing

            performance = "Amazing!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.amazing_green))
            message = if (userType == "Parent")
                "Your child's dedication to managing money is paying off. Continue to encourage them to make financial decisions!"
            else  "Your dedication to managing money is paying off. Continue to make amazing financial decisions!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.amazing)
        } else if (personalFinancePerformance in 76.0..85.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_great
            else
                R.raw.dashboard_pfm_great

            performance = "Great!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.green))
            message = if (userType == "Parent")
                "Your child is making wise choices with their money. Keep encouraging them to seek opportunities to grow their savings!"
            else  "You're making wise choices with your money, and it shows. Keep seeking opportunities to grow your savings!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.great)
        } else if (personalFinancePerformance in 66.0..75.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_good
            else
                R.raw.dashboard_pfm_good

            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.light_green))
            performance = "Good!"
            message = if (userType == "Parent")
                "Your child has a good grasp of managing their income and expenses. Encourage them to keep it up!"
            else  "You have a good grasp of managing your income and expenses. Keep up the good work!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.good)
        } else if (personalFinancePerformance in 56.0..65.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_average
            else
                R.raw.dashboard_pfm_average

            performance = "Average"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.yellow))
            message = if (userType == "Parent")
                "Your child's financial skills are improving, and they're becoming more confident in managing their money!"
            else  "Your financial skills are improving, and you're becoming more confident in managing your money!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.average)
        } else if (personalFinancePerformance in 46.0..55.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_nearly_there
            else
                R.raw.dashboard_parent_pfm_nearly_there

            performance = "Nearly\nThere"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            message = if (userType == "Parent")
                "Your child is making balanced choices with their money. Encourage them to practice their financial decision making!"
            else  "You're making balanced choices with your money. Continue practicing your financial decision making!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.nearly_there)
        } else if (personalFinancePerformance in 36.0..45.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_almost_there
            else
                R.raw.dashboard_pfm_almost_there
            performance = "Almost\nThere"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.almost_there_yellow))
            message = if (userType == "Parent")
                "Your child is becoming more mindful of their money choices. Encourage them to keep making smart decisions!"
            else  "You're becoming more mindful of your money choices. Keep making smart decisions to improve!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.almost_there)
        } else if (personalFinancePerformance in 26.0..35.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_getting_there
            else
                R.raw.dashboard_pfm_getting_there

            performance = "Getting\nThere"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.getting_there_orange))
            message = if (userType == "Parent")
                "They're getting the hang of managing their income and expenses. Encourage them to explore different ways to save and budget their money!"
            else  "You're getting the hang of managing your income and expenses. Explore different ways to save and budget your money!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.getting_there)
        } else if (personalFinancePerformance in 16.0..25.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_not_quite_there
            else
                R.raw.dashboard_pfm_not_quite

            performance = "Not Quite\nThere"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.not_quite_there_red))
            message = if (userType == "Parent")
                "They're making progress in understanding how money works. Remind them to track their expenses and save money for their future!"
            else  "You're making progress in understanding how money works. Remember to track your expenses and save money for the future!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.not_quite_there_yet)
        } else if (personalFinancePerformance < 15) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_needs_improvement
            else
                R.raw.dashboard_pfm_needs_improvement

            performance = "Needs\nImprovement"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.red))
            message = if (userType == "Parent")
                "Don't worry, your child is still developing financially. Encourage them to save money and track their spending!"
            else "Everyone starts from somewhere! Remember to manage your expenses and save consistently!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        }
        else {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_default
            else
                R.raw.dashboard_pfm_default

            performance = "Get\nStarted!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.black))
            var date = "month"
            if (selectedDatesSort == "quarterly")
                date = "quarter"
            message = if (userType == "Parent")
                "They have no data yet for this $date. Remind your child to update the app regularly!"
            else  "You have no data yet for this $date. Click the income button above to add your money."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.peso_coin)
            binding.tvPerformancePercentage.visibility = View.GONE
        }

        imageView.setImageBitmap(bitmap)
        binding.tvPerformanceText.text = message
        binding.tvPerformanceStatus.text = performance

        mediaPlayer = MediaPlayer.create(context, audio)
        loadOverallAudio()
        loadButton()
    }

    private fun loadOverallAudio() {
        binding.btnAudioPersonalFinanceScore.setOnClickListener {
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
        bundle.putString("childID", childID)
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

    private fun initializeGraph() {
        chart = view?.findViewById(R.id.balance_chart)!!


        val xAxis = chart.xAxis
        xAxis.granularity = 1f // Set a smaller granularity if there are fewer data points

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        Log.d("sabong", "selectedDatesSort: "+ selectedDatesSort)

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
        yAxis.axisMaximum = 100f
        yAxis.axisMinimum = 0f

        // Create a dataset from the data
        val dataSet = LineDataSet(graphData, "Personal Financial Performance")
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
        chart.setNoDataText("You have no data yet. Come back to this module after you have used the app.")
        chart.invalidate()
    }

    /*private fun updateXAxisWeekly(xAxis: XAxis?) {
        val dateFormatter = SimpleDateFormat("EEE")
        val dates = selectedDates.distinct()

        if (dates.size < graphData.size) {
            // There are fewer dates than xAxis entries, reduce the number of xAxis entries
            graphData = graphData.take(dates.size) as MutableList<Entry>
        }
        xAxis?.valueFormatter = IndexAxisValueFormatter(dates.map { dateFormatter.format(it) }.toTypedArray())
    }*/

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



    private fun getArgumentsBundle() {
        val args = arguments

        val date = args?.getString("date")
        val childIDBundle = args?.getString("childID")
        val currUser = args?.getString("user")
        if (currUser != null) {
            userType = currUser
        }

        if (childIDBundle != null)
            childID = childIDBundle

        if (date != null) {
            selectedDatesSort = date
            transactionsArrayList.clear()
        }
    }

   /* private fun getDaysOfWeek(dates: List<Date>): List<Date> {
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

        var totalIncome = 0.00f
        var income = 0.00f
        var totalExpense= 0.00f
        var expense= 0.00f
        var xAxisPoint = 0.00f
        for (date in selectedDates) {
            //var totalAmount = 0.00F
            for (transaction in transactionsArrayList) {
                //comparing the dates if they are equal
                if (date.compareTo(transaction.date?.toDate()) == 0) {

                    if (transaction.transactionType == "Income"){
                        //totalAmount += transaction.amount!!
                        totalIncome += transaction.amount!!
                        income += transaction.amount!!
                    }
                    else {
                        //totalAmount -= transaction.amount!!
                        totalExpense += transaction.amount!!
                        expense += transaction.amount!!
                    }
                }
            }
            var personalFinancePerformancePercent = income/expense * 100
            if (personalFinancePerformancePercent > 200)
                personalFinancePerformancePercent = 200F

            var personalFinancePerformance = personalFinancePerformancePercent / 2
            if (personalFinancePerformance.isNaN()) {
                personalFinancePerformance = 0.0f
            }
            data.add(Entry(xAxisPoint, personalFinancePerformance))
            income = 0.00F
            expense = 0.00F
            xAxisPoint++
        }
        Log.d("akala", "Data set size: ${data.size}")
        Log.d("akala", "data: $data")
        val totalPersonalFinancePerformance = calculatePersonalFinancePerformance(totalIncome, totalExpense)
        setTotals(totalPersonalFinancePerformance)
        return data
    }*/

}