package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
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
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentIncomeBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningMenuActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentFinancialActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.TransactionHistoryActivity
import java.text.DecimalFormat
import java.util.*


class IncomeFragment : Fragment(R.layout.fragment_income) {
    private lateinit var binding: FragmentIncomeBinding
    private var firestore = Firebase.firestore
    private val bundle = Bundle()
    private lateinit var mediaPlayer: MediaPlayer


    private var transactionsArrayList = ArrayList<Transactions>()
    var allowancePercentage = 0.00f
    var giftPercentage = 0.00f
    var rewardPercentage = 0.00f
    var otherPercentage = 0.00f
    var allowance = 0.00f
    var total = 0.00f
    var gift = 0.00f
    var reward = 0.00f
    var other = 0.00f
    private var selectedDatesSort = "weekly"
    private var user = "child"
    private lateinit var childID: String
    lateinit var topIncomeCategory: String
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
        checkUser()
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
        firestore.collection("Transactions").whereEqualTo("userID", childID)
            .get().addOnSuccessListener { transactionsSnapshot ->
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
                setSummary()
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
                binding.tvIncomeTitle.text = "This Week's Income"
            }
            "monthly" -> {
                /*val group = groupDates(sortedDate, "monthly")
                addData(group)*/
                val weeks = getWeeksOfCurrentMonth(sortedDate)
                computeDataForMonth(weeks)
                binding.tvIncomeTitle.text = "This Month's Income"
                /*val group = groupDates(sortedDate, "month")
                iterateDatesByQuarter(group)*/
            }
            "quarterly" -> {
                /*val group = groupDates(sortedDate, "quarterly")
                addData(group)*/
                val group = getMonthsOfQuarter(sortedDate)
                computeDataForQuarter(group)
                binding.tvIncomeTitle.text = "This Quarter's Income"
            }
        }

    }

    private fun setSummary() {
        if (total.isNaN())
            total = 0.00F
        val dec = DecimalFormat("#,###.00")
        val totalText = dec.format(total)
        var dateRange = "week"
        when (selectedDatesSort) {
            "monthly" -> dateRange = "month"
            "yearly" -> dateRange = "quarter"
        }
        Log.d("kahoot", "setSummary: "+total)

        //TODO: Change audio
        var audio = 0
        if (user == "child" && total > 0) {
            audio = R.raw.sample
            binding.tvSummary.text = "You've earned ₱$totalText from $dateRange! "
            binding.tvTips.text = "Go to \"Financial Activities\" to further develop your Financial Literacy!"
            loadChildFinancialActivitiesButton()
        } else if (user == "child" && total < 0) {
            audio = R.raw.sample
            binding.tvSummary.text = "Uh oh! You've earned ₱$totalText from $dateRange"
            binding.tvTips.text = "Consider selling your stuff or doing some chores to earn money"
            loadEarningChild()
        } else if (user == "parent" && total > 0) {
            audio = R.raw.sample
            binding.tvSummary.text = "Your child earned ₱$totalText from $dateRange!"
            binding.tvTips.text = "Go to \"Financial Activities\" to further develop your child's Financial Literacy!"
            loadParentFinancialActivitiesButton()
        } else if (user == "parent" && total < 0) {
            audio = R.raw.sample
            binding.tvSummary.text = "Uh oh! Your child only earned ₱$totalText from $dateRange"
            binding.tvTips.text = "Consider giving your child some chores so they can earn some money!"
            loadEarningParent()
        } else if (user == "parent" && total == 0.0F) {
            audio = R.raw.sample
            binding.tvSummary.text = "Uh oh! Your child has no income for this $dateRange"
            binding.tvTips.text = "Consider giving your child some chores so they can earn some money!"
            loadEarningParent()
        } else if (user == "child" && total == 0.0F) {
            audio = R.raw.sample
            binding.tvSummary.text = "Uh oh! You've earned nothing from $dateRange"
            binding.tvTips.text = "Consider selling your stuff or doing some chores to earn money"
            loadEarningParent()
        }
        loadAudio(audio)
    }

    private fun loadAudio(audio: Int) {
        //TODO: Change binding and Audio file in mediaPlayer

        binding.btnAudioFragmentIncome.setOnClickListener {
            if (!this::mediaPlayer.isInitialized) {
                mediaPlayer = MediaPlayer.create(context, audio)
            }

            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                mediaPlayer.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayer.start()
        }
    }

    override fun onDestroy() {
        if (this::mediaPlayer.isInitialized)
            releaseMediaPlayer(mediaPlayer)

        super.onDestroy()
    }

    private fun releaseMediaPlayer(mediaPlayer: MediaPlayer) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
        mediaPlayer.stop()
        mediaPlayer.release()
    }



    private fun loadEarningChild() {
        binding.btnAction.setOnClickListener {
            if (this::mediaPlayer.isInitialized) {
                pauseMediaPlayer(mediaPlayer)
            }
            val goToEarningActivity = Intent(context, EarningMenuActivity::class.java)
            bundle.putString("childID", childID)
            bundle.putString("module", "pfm")
            goToEarningActivity.putExtras(bundle)
            startActivity(goToEarningActivity)
        }
    }

    private fun pauseMediaPlayer(mediaPlayer: MediaPlayer) {
        mediaPlayer.let {
            if (it.isPlaying) {
                it.pause()
                it.seekTo(0)
            }
        }
    }

    private fun loadEarningParent() {
        binding.btnAction.setOnClickListener {
            val goToHomeRewardsActivity = Intent(context, EarningActivity::class.java)
            bundle.putString("childID", childID)
            bundle.putString("module", "pfm")
            goToHomeRewardsActivity.putExtras(bundle)
            startActivity(goToHomeRewardsActivity)
        }
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


    private fun loadParentFinancialActivitiesButton() {
        //TODO: double chekc kung tama link
        binding.btnAction.setOnClickListener {
            bundle.putString("childID",  childID)
            val parentGoal = Intent(context, ParentFinancialActivity::class.java)
            parentGoal.putExtras(bundle)
            startActivity(parentGoal)
        }
    }

    private fun loadChildFinancialActivitiesButton() {
        //TODO: double chekc kung tama link
        binding.btnAction.setOnClickListener {
            val goToFinancialActivity = Intent(context, FinancialActivity::class.java)
            startActivity(goToFinancialActivity)
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
        total = allowance + gift + reward + other
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

    private fun topIncome() {
        val dec = DecimalFormat("#,###.00")
        if (allowance >= gift && allowance >= reward && allowance >= other) {
            topIncomeCategory = "Allowance"
            binding.tvTopIncome.text = topIncomeCategory
            val amount = dec.format(allowance)
            binding.tvIncomeTotal.text = "₱$amount"
        } else if (gift >= allowance && gift >= reward && gift >= other) {
            topIncomeCategory = "Gift"
            binding.tvTopIncome.text = topIncomeCategory
            val amount = dec.format(gift)
            binding.tvIncomeTotal.text = "₱$amount"
        } else if (reward >= allowance && reward >= gift && reward >= other) {
            topIncomeCategory = "Reward"
            binding.tvTopIncome.text = topIncomeCategory
            val amount = dec.format(reward)
            binding.tvIncomeTotal.text = "₱$amount"
        } else {
            topIncomeCategory = "Other"
            binding.tvTopIncome.text = topIncomeCategory
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

        pieChart.legend.isEnabled = false
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
        //pieChart.legend.textSize = 14f

        // undo all highlights
        pieChart.highlightValues(null)

        
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

    private fun checkUser() {
        var user = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(user).get().addOnSuccessListener {

            if (it.toObject<Users>()!!.userType == "Parent") {
             binding.btnAudioFragmentIncome.visibility = View.GONE
                binding.incomeChild.visibility = View.GONE
                binding.incomeParent.visibility= View.VISIBLE}
                else if (it.toObject<Users>()!!.userType == "Child"){
            binding.btnAudioFragmentIncome.visibility = View.VISIBLE
                binding.incomeChild.visibility= View.VISIBLE
                binding.incomeParent.visibility= View.GONE
            }
        }
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