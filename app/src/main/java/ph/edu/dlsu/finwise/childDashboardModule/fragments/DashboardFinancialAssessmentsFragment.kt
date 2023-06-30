package ph.edu.dlsu.finwise.childDashboardModule.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.DialogDashboardFinancialActivitiesBinding
import ph.edu.dlsu.finwise.databinding.DialogDashboardFinancialAssessmentsBinding
import ph.edu.dlsu.finwise.databinding.DialogDashboardGoalDifferenceBinding
import ph.edu.dlsu.finwise.databinding.DialogDashboardMonthlyComparisonBinding
import ph.edu.dlsu.finwise.databinding.FragmentDashboardFinancialAssessmentsBinding
import ph.edu.dlsu.finwise.financialAssessmentModule.FinancialAssessmentLandingPageActivity
import ph.edu.dlsu.finwise.model.FinancialAssessmentAttempts
import ph.edu.dlsu.finwise.model.FinancialAssessmentDetails
import ph.edu.dlsu.finwise.model.SettingsModel
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import kotlin.math.roundToInt

class DashboardFinancialAssessmentsFragment : Fragment() {
    private lateinit var binding: FragmentDashboardFinancialAssessmentsBinding
    private var firestore = Firebase.firestore

    private var isViewCreated = false

    private var mediaPlayer: MediaPlayer? = MediaPlayer()

    private lateinit var childID: String
    //private lateinit var user: String
    private var userType = "child"

    private val assessmentsTaken = ArrayList<FinancialAssessmentAttempts>()
    //private var financialAssessmentTotalPercentage = 0.0
    private var finAssessmentPerformanceCurrentMonth = 0.00F
    private var finAssessmentPerformancePreviousMonth = 0.00F
    private var nAttemptCurrentMonth = 0
    private var nAttemptPreviousMonth = 0
    private var financialGoalsPercentage = 0.00F
    private var financialGoalsScores = ArrayList<Double?>()
    private var savingPercentage = 0.00F
    private var savingScores = ArrayList<Double?>()
    private var budgetingPercentage = 0.00F
    private var budgetingScores = ArrayList<Double?>()
    private var spendingPercentage = 0.00F
    private var spendingScores = ArrayList<Double?>()

    private var xAxisPoint =0.00f
    private val graphData = mutableListOf<Entry>()

    private lateinit var sortedDate: List<Date>
    //private lateinit var days: List<Date>
    private var weeksCurrentMonth: Map<Int, List<Date>>? = null
    private var weeksPreviousMonth: Map<Int, List<Date>>? = null
    private var isCurrentMonth = true
    private var months: Map<Int, List<Date>>? = null
    private var selectedDatesSort = "current"
    private lateinit var chart: LineChart

    private lateinit var endTimestampSelectedMonth: Timestamp
    private lateinit var startTimestampPreviousMonth: Timestamp

    private var coroutineScope = CoroutineScope(Dispatchers.Main)

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
        isViewCreated = true
        binding.title.text = "Financial Assessment Performance"
        getArgumentsBundle()
        getFinancialAssessmentScore()

        binding.layoutFinancialAssessmentScore.setOnClickListener {
            var dialogBinding= DialogDashboardFinancialAssessmentsBinding.inflate(getLayoutInflater())
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
            dialog.window!!.setLayout(1000, 900)
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

    private fun getArgumentsBundle() {
        val args = arguments
        childID = args?.getString("childID").toString()
        userType = arguments?.getString("user").toString()

        val currUser = args?.getString("user")
        val childIDBundle = args?.getString("childID")

        if (childIDBundle != null)
            childID = childIDBundle

        if (currUser != null) {
            userType = currUser
        }

    }

    private fun getFinancialAssessmentScore() {
      coroutineScope.launch {
            //loadOverallScore()
            initializeGraphDate()
            //initializeDetailsButton()
            //setPerformanceView()
        }
    }

   /* private suspend fun loadOverallScore() {
        val copiedAssessmentsTaken: ArrayList<FinancialAssessmentAttempts> =
            assessmentsTaken.toMutableList() as ArrayList<FinancialAssessmentAttempts>

        for (attempt in copiedAssessmentsTaken) {
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
        computeForPercentages()
        calculateFinancialAssessmentScoreFinal()
        *//*setPerformanceView()
        loadView()*//*
    }*/

    private fun initializeGraphDate() {
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
                hideView()
                loadLoadingChart()
                val selectedMonth = months[position]
                // Do something with the selected month
                selectedDatesSort = selectedMonth
                getCurrentAndPreviousMonth(selectedMonth, months)
               coroutineScope.launch {
                    getAssessmentAttempts()
                    getDatesOfAttempts()
                    clearData()
                    setData()
                    if (isAdded)
                        setPerformanceView()
                    loadView()
                    hideLoadingChart()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                loadLoadingChart()
                hideView()
                // Handle case when no month is selected
                selectedDatesSort = "current"
                coroutineScope.launch {
                    resetVariables()
                    setData()
                    if (isAdded)
                        setPerformanceView()
                    loadView()
                    hideLoadingChart()
                }
            }
        }
    }

    private fun getCurrentAndPreviousMonth(selectedMonth: String, months: Array<String>) {
        val calendar = Calendar.getInstance()

// Calculate the month index based on the selected month
        val selectedMonthIndex = months.indexOf(selectedMonth)

// Calculate the previous month index, accounting for the case when the selected month is January
        val previousMonthIndex = if (selectedMonthIndex == 0) {
            months.size - 1 // December (last month in the list)
        } else {
            selectedMonthIndex - 1
        }

// Set the start and end dates for the selected and previous months
        /*calendar.set(Calendar.MONTH, selectedMonthIndex)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDateSelectedMonth = calendar.time
        val startTimestampSelectedMonth = Timestamp(startDateSelectedMonth)*/

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDateSelectedMonth = calendar.time
        endTimestampSelectedMonth = Timestamp(endDateSelectedMonth)

        calendar.set(Calendar.MONTH, previousMonthIndex)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDatePreviousMonth = calendar.time
        startTimestampPreviousMonth = Timestamp(startDatePreviousMonth)

        /*calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDatePreviousMonth = calendar.time
        val endTimestampPreviousMonth = Timestamp(endDatePreviousMonth)*/
    }


    private fun loadLoadingChart() {
        binding.llLoadingChart.visibility = View.VISIBLE
        binding.financialAssessmentChart.visibility = View.GONE
    }

    private fun hideLoadingChart() {
        binding.llLoadingChart.visibility = View.GONE
        binding.financialAssessmentChart.visibility = View.VISIBLE
    }


    private fun loadView() {
        binding.layoutLoading.visibility = View.GONE
        binding.cvOverallScore.visibility = View.VISIBLE
    }

    private fun hideView() {
        binding.layoutLoading.visibility = View.VISIBLE
        binding.cvOverallScore.visibility = View.GONE
    }
    private fun setPerformanceView() {
        if (!isViewCreated || !isAdded) {
            return
        }


        computeFinAssessmentsPerformance()

        val financialAssessmentTotalPercentage = finAssessmentPerformanceCurrentMonth
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.UP
        val roundedValue = df.format(financialAssessmentTotalPercentage)


        val imageView = binding.ivScore
        val message: String
        val performance: String
        val bitmap: Bitmap

        val context = requireContext()
        val resources = context.resources

        //TODO: Change audio
        var audio = 0

        if (financialAssessmentTotalPercentage >= 96) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_assessments_excellent
            else
                R.raw.dashboard_financial_assessments_excellent

            performance = "Excellent!"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.dark_green))

            message = if (userType == "Parent")
                "Your child is a financial superstar in the realm of financial assessments. Encourage them to keep it up!"
            else "You've mastered financial concepts! Keep shining and inspiring others with your remarkable knowledge!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.excellent)
        } else if (financialAssessmentTotalPercentage >= 86.0 && financialAssessmentTotalPercentage < 96.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_assessments_amazing
            else
                R.raw.dashboard_financial_assessments_amazing

            performance = "Amazing!"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.amazing_green))

            message = if (userType == "Parent")
                "Your child's financial knowledge is impressive. Encourage them to apply their skills to real-life situations!"
            else "You're a financial whiz! Keep up the excellent work and inspire others with your financial know-how!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.amazing)
        } else if (financialAssessmentTotalPercentage >= 76.0 && financialAssessmentTotalPercentage < 86.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_assessments_great
            else
                R.raw.dashboard_financial_assessments_great

            performance = "Great!"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.green))
            message = if (userType == "Parent")
                "Your child's financial knowledge is flourishing! Encourage them to keep it up."
            else "Your financial knowledge is impressive! Keep honing it and applying it to real-life."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.great)
        } else if (financialAssessmentTotalPercentage >= 66.0 && financialAssessmentTotalPercentage < 76.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_assessments_good
            else
                R.raw.dashboard_financial_assessments_good

            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context, R.color.light_green))
            performance = "Good!"
            message = if (userType == "Parent")
                "Your child is demonstrating a solid understanding of financial concepts. Support them in applying this in real life!"
            else "You have a solid understanding of financial concepts! Keep up the good work and apply it in real life."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.good)
        } else if (financialAssessmentTotalPercentage >= 56.0 && financialAssessmentTotalPercentage < 66.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_assessments_average
            else
                R.raw.dashboard_financial_assessments_average

            performance = "Average"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.yellow))
            message = if (userType == "Parent")
                "Your child is building confidence in financial concepts! Encourage them to continue learning."
            else "Your understanding of financial concepts is great! Improve by deepening your knowledge and applying the concepts in real life."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.average)
        } else if (financialAssessmentTotalPercentage >= 46.0 && financialAssessmentTotalPercentage < 56.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_assessments_nearly_there
            else
                R.raw.dashboard_financial_assessments_nearly_there

            performance = "Nearly\nThere"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.nearly_there_yellow))

            message = if (userType == "Parent")
                "Your child's grasp of financial concepts is still on the works! Encourage them to continue!"
            else "Your knowledge of financial concepts are improving! Improve by deepening your knowledge and applying the concepts in real life."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.nearly_there)
        } else if (financialAssessmentTotalPercentage >= 36.0 && financialAssessmentTotalPercentage < 46.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_assessments_almost_there
            else
                R.raw.dashboard_financial_assessments_almost_there

            performance = "Almost\nThere"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.almost_there_yellow))
            message = if (userType == "Parent")
                "Your child is growing in their understanding of saving, budgeting, spending, and setting achievable financial goals!"
            else "You're getting the hang of financial concepts! Keep it up!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.almost_there)
        } else if (financialAssessmentTotalPercentage >= 26.0 && financialAssessmentTotalPercentage < 36.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_assessments_getting_there
            else
                R.raw.dashboard_financial_assessments_getting_there

            performance = "Getting\nThere"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.getting_there_orange))
            message = if (userType == "Parent")
                "Your child is on the path to great financial knowledge. Encourage them to keep it up!"
            else "You're gaining a better understanding of financial concepts. Keep learning and practicing to improve even more!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.getting_there)
        } else if (financialAssessmentTotalPercentage >= 16.0 && financialAssessmentTotalPercentage < 26.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_assessments_not_quite_there
            else
                R.raw.dashboard_financial_assessments_needs_improvement_not_quite_there

            performance = "Not Quite\nThere"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.not_quite_there_red))
            message = if (userType == "Parent")
                "Uh oh! Help expand your child's financial knowledge by checking out the concepts they can review!"
            else "Keep exploring and learning about financial concepts! Don't give up!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.not_quite_there_yet)
        } else if (financialAssessmentTotalPercentage < 16.0) {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_assessments_needs_improvement
            else
                R.raw.dashboard_financial_assessments_needs_improvement_not_quite_there

            performance = "Needs\nImprovement"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.red))
            message = if (userType == "Parent")
                "Your child needs support developing their financial knowledge. Check out the concepts they can review!"
            else "Keep exploring and learning about financial concepts! Don't give up!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        }
        else {
            audio = if (userType == "Parent")
                R.raw.dashboard_parent_financial_assessments_default
            else
                R.raw.dashboard_financial_assessments_default

            performance = "Get\nStarted!"
            binding.tvPerformanceStatus.setTextColor(ContextCompat.getColor(context,
                R.color.black))

            var date = "month"
            if (selectedDatesSort == "quarterly")
                date = "quarter"
            message = if (userType == "Parent")
                "Your child hasn't taken any assessments for this $date. Remind your child to use the app regularly!"
            else "You haven't taken any assessments for this $date. Use the app regularly to see your progress!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.peso_coin)
            binding.tvPerformancePercentage.visibility = View.GONE
        }

        imageView.setImageBitmap(bitmap)
        binding.tvPerformanceText.text = message
        binding.tvPerformanceStatus.text = performance
        binding.tvPerformancePercentage.text = "${roundedValue}%"

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

        if (finAssessmentPerformanceCurrentMonth > finAssessmentPerformancePreviousMonth) {
            difference = finAssessmentPerformanceCurrentMonth - finAssessmentPerformancePreviousMonth
            performance = "Increase from Last Month"
//            binding.tvPreviousPerformanceStatus.setTextColor(ContextCompat.getColor(context, R.color.dark_green))
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.up_arrow)
        } else if (finAssessmentPerformanceCurrentMonth < finAssessmentPerformancePreviousMonth) {
            difference = finAssessmentPerformancePreviousMonth - finAssessmentPerformanceCurrentMonth
            performance = "Decrease from Last Month"
//            binding.tvPreviousPerformanceStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.down_arrow)
        } else {
            difference = 0.0F
            performance = "No Increase from Last Month"
//            binding.tvPreviousPerformanceStatus.setTextColor(ContextCompat.getColor(context, R.color.yellow))
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_equal)
        }
        val decimalFormat = DecimalFormat("#.#")
        val roundedValue = decimalFormat.format(difference)

        binding.ivPreviousMonthImg.setImageBitmap(bitmap)
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
                    if (finAssessmentPerformanceCurrentMonth > upper) {
                        binding.tvGoalDiffPercentage.text = "${DecimalFormat("##0.0").format(finAssessmentPerformanceCurrentMonth - upper)}%"
                        binding.tvGoalDiffStatus.text = "Above Target"
                        binding.tvGoalDiffStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_green))
                        binding.ivGoalDiffImg.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.up_arrow))
                    } else if (finAssessmentPerformanceCurrentMonth in lower..upper) {
                        //binding.tvGoalDiffPercentage.text = "${DecimalFormat("##0.0").format(overallFinancialHealthCurrentMonth - upper)}%"
                        binding.tvGoalDiffStatus.text = "Within Target"
                        binding.tvGoalDiffStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
                        binding.ivGoalDiffImg.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.icon_equal))
                    } else if (finAssessmentPerformanceCurrentMonth < lower) {
                        binding.tvGoalDiffPercentage.text = "${DecimalFormat("##0.0").format(lower - finAssessmentPerformanceCurrentMonth)}%"
                        binding.tvGoalDiffStatus.text = "Below Target"
                        binding.tvGoalDiffStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        binding.ivGoalDiffImg.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.down_arrow))
                    }
                }
            }
        }
    }


    private fun computeFinAssessmentsPerformance() {
        checkIfNaNPerformance()
        finAssessmentPerformanceCurrentMonth = if (nAttemptCurrentMonth > 0) {
            finAssessmentPerformanceCurrentMonth / nAttemptCurrentMonth
        } else {
            0.0F // Handle the case where there are no attempts to avoid division by zero
        }
        finAssessmentPerformancePreviousMonth = if (nAttemptPreviousMonth > 0) {
            finAssessmentPerformancePreviousMonth / nAttemptPreviousMonth
        } else {
            0.0F // Handle the case where there are no attempts to avoid division by zero
        }
        checkIfNaNPerformance()
    }

    private fun checkIfNaNPerformance() {
        if (finAssessmentPerformanceCurrentMonth.isNaN())
            finAssessmentPerformanceCurrentMonth = 0.0F
        if (finAssessmentPerformancePreviousMonth.isNaN())
            finAssessmentPerformancePreviousMonth = 0.0F
    }

    private fun loadOverallAudio() {
        binding.btnAudioFinancialAssessmentScore.setOnClickListener {

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
        coroutineScope.cancel()
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
                val intent = Intent(context, FinancialAssessmentLandingPageActivity::class.java)
                intent.putExtras(bundle)
                intent
            } else {
                Intent(context, FinancialAssessmentLandingPageActivity::class.java)
            }
            startActivity(goToActivity)
        }
    }



    private fun initializeGraph() {
        chart = binding.financialAssessmentChart

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
        yAxis.axisMaximum = 100f // set maximum y-value to 100%
        yAxis.axisMinimum = 0f // set maximum y-value to 100%

        // Create a dataset from the data
        val dataSet = LineDataSet(graphData, "Financial Assessments Performance")
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

        val month: Int
        if (selectedDatesSort == "current") {
            /*val group = groupDates(sortedDate, "month")
            iterateDatesByQuarter(group)*/
            month = getCurrentMonth()
            binding.tvBalanceTitle.text = "This Month's Financial Assessment Score Trend"
        } else {

            month = getMonthIndex(selectedDatesSort) - 1
            binding.tvBalanceTitle.text = "Financial Assessment Score Trend of $selectedDatesSort"
            binding.title.text = "Financial Assessment Score of $selectedDatesSort"
        }
        val previousMonth = getPreviousMonth(month)
        weeksCurrentMonth = getWeeksOfMonth(sortedDate, month)
        weeksPreviousMonth = getWeeksOfMonth(sortedDate, previousMonth)
        isCurrentMonth = true
        getDataOfWeeksOfCurrentMonth(weeksCurrentMonth!!)
        isCurrentMonth = false
        getDataOfWeeksOfCurrentMonth(weeksPreviousMonth!!)
        initializeGraph()
        clearDocuments()
    }

    private fun clearDocuments() {
        assessmentsTaken.clear()
        sortedDate = listOf()
    }

    private fun getPreviousMonth(month: Int): Int {
        var previousMonth = month
        if (previousMonth == 1)
            previousMonth = 0
        else previousMonth -= 1
        return previousMonth
    }


    private fun clearData() {
        nAttemptCurrentMonth = 0
        nAttemptPreviousMonth = 0
        finAssessmentPerformanceCurrentMonth = 0F
        finAssessmentPerformancePreviousMonth = 0F
        graphData.clear()
        xAxisPoint = 0.00F
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



    /* private fun getMonthsOfQuarter(dates: List<Date>): Map<Int, List<Date>> {
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
                         if (assessmentsTaken.isNotEmpty()) {
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
                             financialAssessmentTotalPercentage += percentage
                             nAttempt++
                         }
                     }
                 }
             }
             computeForPercentages()
         }
     }*/



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
                                if (isCurrentMonth) {
                                    finAssessmentPerformanceCurrentMonth += percentage.toFloat()
                                    nAttemptCurrentMonth++
                                } else {
                                    finAssessmentPerformancePreviousMonth += percentage.toFloat()
                                    nAttemptPreviousMonth++
                                }
                                /*financialAssessmentTotalPercentage += percentage
                                nAttempt++*/
                            }
                        }
                    }
                }
            computeForPercentages()
            calculateFinancialAssessmentScore()
        }

    }

    private fun resetVariables() {
        graphData.clear()
        xAxisPoint = 0.00F
    }

    private suspend fun getAssessmentAttempts() {
        val assessmentAttemptsDocuments =  firestore.collection("AssessmentAttempts")
            .whereEqualTo("childID", childID)
            .whereGreaterThanOrEqualTo("dateTaken", startTimestampPreviousMonth)
            .whereLessThanOrEqualTo("dateTaken", endTimestampSelectedMonth)
            .get().await()

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
        clearScores()
        checkIfNaN()
    }

    private fun clearScores() {
        savingScores.clear()
        spendingScores.clear()
        budgetingScores.clear()
        financialGoalsScores.clear()
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

        val financialAssessmentPerformance = (totalSum.toDouble() / maxPossibleSum) * 100
        val roundedFinancialAssessmentPerformance = (financialAssessmentPerformance * 10).roundToInt() / 10

        if (isCurrentMonth) {
            graphData.add(Entry(xAxisPoint, roundedFinancialAssessmentPerformance.toFloat()))
            xAxisPoint++
        }
    }

    /*private fun calculateFinancialAssessmentScoreFinal() {
        val totalSum = spendingPercentage + savingPercentage + financialGoalsPercentage + budgetingPercentage
        val maxPossibleSum = 4 * 100  // assuming the maximum possible value for each variable is 100
        financialAssessmentTotalPercentage = (totalSum.toDouble() / maxPossibleSum) * 100
    }*/

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

        /*private fun initializeDetailsButton() {
        binding.btnDetails.setOnClickListener{
            val goToDetails = Intent(context, FinancialAssessmentsDetailsActivity::class.java)
            val bundle = Bundle()
            bundle.putString("date", selectedDatesSort)
            bundle.putString("user", userType)
            if (userType == "child")
                childID  = FirebaseAuth.getInstance().currentUser!!.uid

            bundle.putString("childID", childID)
            goToDetails.putExtras(bundle)
            startActivity(goToDetails)
        }
    }*/


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