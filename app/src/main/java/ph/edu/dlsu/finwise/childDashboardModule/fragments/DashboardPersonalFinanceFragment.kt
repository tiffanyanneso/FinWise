package ph.edu.dlsu.finwise.childDashboardModule.fragments

import android.app.Dialog
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.DialogDashboardFinancialActivitiesBinding
import ph.edu.dlsu.finwise.databinding.DialogDashboardGoalDifferenceBinding
import ph.edu.dlsu.finwise.databinding.DialogDashboardMonthlyComparisonBinding
import ph.edu.dlsu.finwise.databinding.DialogDashboardPfmScoreBinding
import ph.edu.dlsu.finwise.databinding.FragmentDashboardPersonalFinanceBinding
import ph.edu.dlsu.finwise.model.FinancialAssessmentAttempts
import ph.edu.dlsu.finwise.model.SettingsModel
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.parentFinancialManagementModule.ParentFinancialManagementActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class DashboardPersonalFinanceFragment : Fragment() {
    private lateinit var binding: FragmentDashboardPersonalFinanceBinding
    private var firestore = Firebase.firestore

    private var mediaPlayer: MediaPlayer? = MediaPlayer()

    private var isViewCreated = false


    private lateinit var childID: String

    private var userType = "child"

    val transactionsArrayList = ArrayList<Transactions>()
    private lateinit var sortedDate: List<Date>
    //private lateinit var days: List<Date>
    private var weeksPreviousMonth: Map<Int, List<Date>>? = null
    private var weeksCurrentMonth: Map<Int, List<Date>>? = null
    private var months: Map<Int, List<Date>>? = null
    private var selectedDatesSort = "current"
    private lateinit var chart: LineChart
    var graphData = mutableListOf<Entry>()

    private var pfmScoreCurrentMonth = 0.00f
    private var totalIncomeCurrentMonth = 0.00f
    private var totalExpenseCurrentMonth = 0.00f
    private var pfmScorePreviousMonth = 0.00f
    private var totalIncomePreviousMonth = 0.00f
    private var totalExpensePreviousMonth = 0.00f


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

        isViewCreated = true

        if (isAdded) {
            binding.title.text = "Personal Finance Performance"
            binding.layoutLoading.visibility = View.VISIBLE
            binding.layoutMain.visibility = View.GONE
        }

        getArgumentsBundle()
        initializeFragment()
        //getPersonalFinancePerformance()

        binding.layoutPfmScore.setOnClickListener {
            var dialogBinding= DialogDashboardPfmScoreBinding.inflate(getLayoutInflater())
            var dialog= Dialog(requireContext());
            dialog.setContentView(dialogBinding.getRoot())
            dialog.window!!.setLayout(1000, 900)
            dialog.show()

            dialogBinding.btnGotIt.setOnClickListener {
                dialog.dismiss()
            }
        }

        binding.layoutGoalDifference.setOnClickListener {
            var dialogBinding= DialogDashboardGoalDifferenceBinding.inflate(getLayoutInflater())
            var dialog= Dialog(requireContext());
            dialog.setContentView(dialogBinding.getRoot())
            dialog.window!!.setLayout(1000, 1000)
            dialog.show()

            dialogBinding.btnGotIt.setOnClickListener {
                dialog.dismiss()
            }
        }

        binding.layoutMonthlyIncrease.setOnClickListener {
            var dialogBinding= DialogDashboardMonthlyComparisonBinding.inflate(getLayoutInflater())
            var dialog= Dialog(requireContext());
            dialog.setContentView(dialogBinding.getRoot())
            dialog.window!!.setLayout(1000, 900)
            dialog.show()

            dialogBinding.btnGotIt.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun initializeFragment() {
        firestore.collection("Transactions").whereEqualTo("userID", childID)
            .get().addOnSuccessListener { documents ->
                initializeTransactions(documents)
                sortedDate = getDatesOfTransactions(transactionsArrayList)
                initializeDateSelectionSpinner()
                /*val totalPersonalFinancePerformance = calculatePersonalFinancePerformance()*/
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

    private fun initializeDateSelectionSpinner() {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)

        val months = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        val filteredMonths = months.sliceArray(0..currentMonth)

        val spinner = binding.monthSpinner
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, filteredMonths)
        spinner.setSelection(currentMonth)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMonth = months[position]
                // Do something with the selected month
                selectedDatesSort = selectedMonth
                setData()
                setTotals()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when no month is selected
                selectedDatesSort = "current"
                setData()
                setTotals()
            }
        }
    }

    private fun setData() {
        clearData()

        val month: Int
        if (selectedDatesSort == "current") {
            /*val group = groupDates(sortedDate, "month")
            iterateDatesByQuarter(group)*/
            month = getCurrentMonth()
            binding.tvBalanceTitle.text = "This Month's Personal Financial Score Trend"
        } else {
            month = getMonthIndex(selectedDatesSort) - 1
            /*val group = groupDates(sortedDate, "month")
            iterateDatesByQuarter(group)*/
            binding.tvBalanceTitle.text = "Personal Financial Score Trend of $selectedDatesSort"
            binding.title.text = "Personal Financial Score of $selectedDatesSort"
        }
        val previousMonth = getPreviousMonth(month)
        weeksCurrentMonth = getWeeksOfMonth(sortedDate, month)
        weeksPreviousMonth = getWeeksOfMonth(sortedDate, previousMonth)
        getDataOfWeeksOfCurrentMonth(weeksCurrentMonth!!, true)
        getDataOfWeeksOfCurrentMonth(weeksPreviousMonth!!, false)
        initializeGraph()
    }

    private fun clearData() {
        totalIncomeCurrentMonth = 0f
        totalExpenseCurrentMonth = 0f
        totalIncomePreviousMonth = 0f
        totalExpensePreviousMonth = 0f
        graphData.clear()
    }

    private fun getPreviousMonth(month: Int): Int {
        var previousMonth = month
        if (previousMonth == 1)
            previousMonth = 0
        else previousMonth -= 1
        return previousMonth
    }

    private fun getMonthIndex(selectedMonth: String): Int {
        val months = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        val monthIndex = months.indexOf(selectedMonth)
        return if (monthIndex != -1) monthIndex + 1 else -1
    }

    private fun getCurrentMonth(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.MONTH)
    }

    private fun getWeeksOfMonth(dates: List<Date>, month: Int): Map<Int, List<Date>> {
        val calendar = Calendar.getInstance()

        // Get the current year
        val currentYear = calendar.get(Calendar.YEAR)

        // Filter the dates that belong to the specified month and current year
        val filteredDates = dates.filter { date ->
            calendar.time = date
            calendar.get(Calendar.MONTH) == month &&
                    calendar.get(Calendar.YEAR) == currentYear
        }

        // Group the filtered dates by week
        return filteredDates.groupBy { date ->
            calendar.time = date
            calendar.get(Calendar.WEEK_OF_MONTH)
        }
    }

    private fun getDataOfWeeksOfCurrentMonth(weeks: Map<Int, List<Date>>, isCurrentMonth: Boolean) {
        var income = 0.00f
        var expense= 0.00f
        var xAxisPoint =0.00f

        weeks.forEach { (weekNumber, datesInWeek) ->
            //var totalAmount = 0.00F
            datesInWeek.forEach { date ->
                for (transaction in transactionsArrayList) {
                    if (date.compareTo(transaction.date?.toDate()) == 0) {
                        if (transaction.transactionType == "Income"){
                            //totalAmount += transaction.amount!!
                            income += transaction.amount!!
                            if (isCurrentMonth)
                                totalIncomeCurrentMonth += transaction.amount!!
                            else totalIncomePreviousMonth += transaction.amount!!
                        }
                        else {
                            //totalAmount -= transaction.amount!!
                            expense += transaction.amount!!
                            if (isCurrentMonth)
                                totalExpenseCurrentMonth += transaction.amount!!
                            else totalExpensePreviousMonth += transaction.amount!!
                        }
                    }
                }
            }
            if (isCurrentMonth) {
                var personalFinancePerformancePercent = income/expense * 100
                if (personalFinancePerformancePercent > 200)
                    personalFinancePerformancePercent = 200F

                val personalFinancePerformance = personalFinancePerformancePercent / 2

                graphData.add(Entry(xAxisPoint, personalFinancePerformance))
                income = 0.00F
                expense = 0.00F
                xAxisPoint++
            }
        }

    }

   /* private fun addTotalIIncomeAndExpensePFM(transaction: Transactions) {
        if (transaction.transactionType == "Income")
            totalIncome += transaction.amount!!
        else totalExpense += transaction.amount!!
    }

    private fun calculatePersonalFinancePerformance(): Float {
        val copiedTransactionsArrayList: ArrayList<Transactions> =
            transactionsArrayList.toMutableList() as ArrayList<Transactions>
        for (transaction in copiedTransactionsArrayList)
            addTotalIIncomeAndExpensePFM(transaction)

        var personalFinancePerformancePercent = totalIncome/totalExpense * 100
        if (personalFinancePerformancePercent > 200)
            personalFinancePerformancePercent = 200F

        return personalFinancePerformancePercent / 2
    }*/

    private fun setTotals() {
        if (!isViewCreated || !isAdded) {
            return
        }

        calculatePFMScore()

        val personalFinancePerformance = pfmScoreCurrentMonth

        val imageView = binding.ivScore
        val message: String
        val performance: String
        val bitmap: Bitmap

        val context = requireContext()
        val resources = context.resources

        //TODO: Change audio
        var audio = 0

        if (personalFinancePerformance >= 96F) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_excellent
            else
                R.raw.dashboard_pfm_excellent

            performance = "Excellent!"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.dark_green))
            message = if (userType == "Parent")
                "Your child is excellent at personal finance! They make exceptional financial decisions."
            else  "You are excellent at personal finance! Keep making exceptional financial decisions."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.excellent)
        } else if (personalFinancePerformance >= 86.0 && personalFinancePerformance < 96.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_amazing
            else
                R.raw.dashboard_pfm_amazing

            performance = "Amazing!"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.amazing_green))
            message = if (userType == "Parent")
                "Your child's dedication to managing money is paying off. Continue to encourage them to make financial decisions!"
            else  "Your dedication to managing money is paying off. Continue to make amazing financial decisions!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.amazing)
        } else if (personalFinancePerformance >= 76.0 && personalFinancePerformance < 86.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_great
            else
                R.raw.dashboard_pfm_great

            performance = "Great!"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.green))
            message = if (userType == "Parent")
                "Your child is making wise choices with their money. Keep encouraging them to seek opportunities to grow their savings!"
            else  "You're making wise choices with your money, and it shows. Keep seeking opportunities to grow your savings!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.great)
        } else if (personalFinancePerformance >= 66.0 && personalFinancePerformance < 76.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_good
            else
                R.raw.dashboard_pfm_good

            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.light_green))
            performance = "Good!"
            message = if (userType == "Parent")
                "Your child has a good grasp of managing their income and expenses. Encourage them to keep it up!"
            else  "You have a good grasp of managing your income and expenses. Keep up the good work!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.good)
        } else if (personalFinancePerformance >= 56.0 && personalFinancePerformance < 66.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_average
            else
                R.raw.dashboard_pfm_average

            performance = "Average"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.yellow))
            message = if (userType == "Parent")
                "Your child's financial skills are improving, and they're becoming more confident in managing their money!"
            else  "Your financial skills are improving, and you're becoming more confident in managing your money!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.average)
        } else if (personalFinancePerformance >= 46.0 && personalFinancePerformance < 56.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_nearly_there
            else
                R.raw.dashboard_parent_pfm_nearly_there

            performance = "Nearly\nThere"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.nearly_there_yellow))
            message = if (userType == "Parent")
                "Your child is making balanced choices with their money. Encourage them to practice their financial decision making!"
            else  "You're making balanced choices with your money. Continue practicing your financial decision making!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.nearly_there)
        } else if (personalFinancePerformance >= 36.0 && personalFinancePerformance < 46.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_almost_there
            else
                R.raw.dashboard_pfm_almost_there
            performance = "Almost\nThere"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.almost_there_yellow))
            message = if (userType == "Parent")
                "Your child is becoming more mindful of their money choices. Encourage them to keep making smart decisions!"
            else  "You're becoming more mindful of your money choices. Keep making smart decisions to improve!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.almost_there)
        } else if (personalFinancePerformance >= 26.0 && personalFinancePerformance < 36.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_getting_there
            else
                R.raw.dashboard_pfm_getting_there

            performance = "Getting\nThere"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.getting_there_orange))
            message = if (userType == "Parent")
                "They're getting the hang of managing their income and expenses. Encourage them to explore different ways to save and budget their money!"
            else  "You're getting the hang of managing your income and expenses. Explore different ways to save and budget your money!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.getting_there)
        } else if (personalFinancePerformance >= 16.0 && personalFinancePerformance < 26.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_not_quite_there
            else
                R.raw.dashboard_pfm_not_quite

            performance = "Not Quite\nThere"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.not_quite_there_red))
            message = if (userType == "Parent")
                "They're making progress in understanding how money works. Remind them to track their expenses and save money for their future!"
            else  "You're making progress in understanding how money works. Remember to track your expenses and save money for the future!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.not_quite_there_yet)
        } else if (personalFinancePerformance < 16.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_pfm_needs_improvement
            else
                R.raw.dashboard_pfm_needs_improvement

            performance = "Needs\nImprovement"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.red))
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
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.black))
            var date = "month"
            if (selectedDatesSort == "quarterly")
                date = "quarter"
            message = if (userType == "Parent")
                "They have no data for this $date. Remind your child to use the app regularly!"
            else  "You have no data for this $date. Use the app regularly to see your progress!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.peso_coin)
            binding.tvPerformancePercentage.visibility = View.GONE
        }

        imageView.setImageBitmap(bitmap)
        binding.tvPerformancePercentage.setTextColor(ContextCompat.getColor(context,
            R.color.black))
        val roundedValue = String.format("%.1f", personalFinancePerformance)
        binding.tvPerformancePercentage.text = "$roundedValue%"
        binding.tvPerformanceText.text = message
        binding.tvPerformanceStatus.text = performance

        mediaPlayer = MediaPlayer.create(context, audio)
        loadOverallAudio()
        loadDifferenceFromGoal()
        loadPreviousMonth()
        loadButton()
    }

    private fun loadPreviousMonth() {
        if (!isViewCreated || !isAdded) {
            return
        }
        val context = requireContext()

        val performance: String
        val bitmap: Bitmap
        val difference: Float

        if (pfmScoreCurrentMonth > pfmScorePreviousMonth) {
            difference = pfmScoreCurrentMonth - pfmScorePreviousMonth
            performance = "Month's Increase"
            binding.tvPreviousPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.dark_green))
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.up_arrow)
        } else if (pfmScoreCurrentMonth < pfmScorePreviousMonth) {
            difference = pfmScorePreviousMonth - pfmScoreCurrentMonth
            performance = "Month's Decrease"
            binding.tvPreviousPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.red))
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.down_arrow)
        } else {
            difference = 0.0F
            performance = "No Increase"
            binding.tvPreviousPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.yellow))
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_equal)
        }


        binding.tvPreviousPerformancePercentage.setTextColor(ContextCompat.getColor(context, R.color.dark_grey))
        binding.ivPreviousMonthImg.setImageBitmap(bitmap)
        val roundedValue = String.format("%.1f", difference)
        binding.tvPreviousPerformancePercentage.text = "$roundedValue%"
        binding.tvPreviousPerformanceStatus.text = performance
    }

    private fun loadDifferenceFromGoal() {
        firestore.collection("Settings").whereEqualTo("childID", childID).get().addOnSuccessListener {
            if (!it.isEmpty) {
                var literacyGoal =it.documents[0].toObject<SettingsModel>()?.literacyGoal
                var lower = 0.00F
                var upper = 0.00F
                when (literacyGoal) {
                    "Excellent" -> {
                        lower = 90.0F
                        upper = 100F
                    }

                    "Amazing" -> {
                        lower = 80.0F
                        upper = 89.99F
                    }

                    "Great" -> {
                        lower = 70.0F
                        upper = 79.9F
                    }

                    "Good" -> {
                        lower = 60.0F
                        upper = 69.9F
                    }
                }


                if (isAdded) {
                    if (pfmScoreCurrentMonth > upper) {
                        binding.tvGoalDiffPercentage.text = "${DecimalFormat("##0.0").format(pfmScoreCurrentMonth - upper)}%"
                        binding.tvGoalDiffStatus.text = "Above your target"
                        binding.tvGoalDiffStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_green))
                        binding.ivGoalDiffImg.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.up_arrow))
                    } else if (pfmScoreCurrentMonth in lower..upper) {
                        //binding.tvGoalDiffPercentage.text = "${DecimalFormat("##0.0").format(overallFinancialHealthCurrentMonth - upper)}%"
                        binding.tvGoalDiffStatus.text = "Within Target"
                        binding.tvGoalDiffStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
                        binding.ivGoalDiffImg.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.icon_equal))
                    } else if (pfmScoreCurrentMonth < lower) {
                        binding.tvGoalDiffPercentage.text = "${DecimalFormat("##0.0").format(lower - pfmScoreCurrentMonth)}%"
                        binding.tvGoalDiffStatus.text = "Below your target"
                        binding.tvGoalDiffStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        binding.ivGoalDiffImg.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.down_arrow))
                    }
                }
            }
        }
    }

    private fun calculatePFMScore() {
        pfmScoreCurrentMonth = totalIncomeCurrentMonth/totalExpenseCurrentMonth * 100
        if (pfmScoreCurrentMonth > 200)
            pfmScoreCurrentMonth = 200F
        pfmScorePreviousMonth = totalIncomePreviousMonth/totalExpensePreviousMonth * 100
        if (pfmScorePreviousMonth > 200)
            pfmScorePreviousMonth = 200F

        Log.d("wathcy", "totalIncomeCurrentMonth: "+totalIncomeCurrentMonth)
        Log.d("wathcy", "totalExpenseCurrentMonth: "+totalExpenseCurrentMonth)
        Log.d("wathcy", "totalIncomePreviousMonth: "+totalIncomePreviousMonth)
        Log.d("wathcy", "totalExpensePreviousMonth: "+totalExpensePreviousMonth)

        pfmScoreCurrentMonth /= 2
        pfmScorePreviousMonth /= 2

        Log.d("wathcy", "pfmScoreCurrentMonth: "+pfmScoreCurrentMonth)
        Log.d("wathcy", "pfmScorePreviousMonth: "+pfmScorePreviousMonth)


        checkIfNaN()
    }

    private fun checkIfNaN() {
        if (pfmScoreCurrentMonth.isNaN()) {
            pfmScoreCurrentMonth = 0.0f
        }
        if (pfmScorePreviousMonth.isNaN()) {
            pfmScorePreviousMonth = 0.0f
        }
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
        isViewCreated = false
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
        updateXAxisMonthly(xAxis)

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


    private fun updateXAxisMonthly(xAxis: XAxis) {
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "Week ${value.toInt() + 1}"
            }
        }
    }

    /*private fun updateXAxisQuarterly(xAxis: XAxis?) {

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
    }*/



    private fun getArgumentsBundle() {
        val args = arguments

        val childIDBundle = args?.getString("childID")
        val currUser = args?.getString("user")
        if (currUser != null) {
            userType = currUser
        }

        if (childIDBundle != null)
            childID = childIDBundle

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

      /*  private fun getMonthsOfQuarter(dates: List<Date>): Map<Int, List<Date>> {
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
  }*/


    /*private fun updateXAxisWeekly(xAxis: XAxis?) {
        val dateFormatter = SimpleDateFormat("EEE")
        val dates = selectedDates.distinct()

        if (dates.size < graphData.size) {
            // There are fewer dates than xAxis entries, reduce the number of xAxis entries
            graphData = graphData.take(dates.size) as MutableList<Entry>
        }
        xAxis?.valueFormatter = IndexAxisValueFormatter(dates.map { dateFormatter.format(it) }.toTypedArray())
    }*/


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