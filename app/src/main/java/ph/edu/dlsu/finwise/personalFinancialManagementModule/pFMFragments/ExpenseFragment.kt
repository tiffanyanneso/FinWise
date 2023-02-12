package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import ph.edu.dlsu.finwise.databinding.FragmentExpenseBinding
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentExpenseBinding.bind(view)
        loadPieChart()
    }

    private fun loadPieChart() {
        //TODO: Update data based on user
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        firestore.collection("Transactions").get()
            .addOnSuccessListener { transactionsSnapshot ->
                lateinit var id: String
                for (document in transactionsSnapshot) {
                    var transaction = document.toObject<Transactions>()
                    if (transaction.transactionType == "Expense" || transaction.transactionType == "Expense (Maya")
                        transactionsArrayList.add(transaction)
                }

                calculatePercentage()
            }
    }

    private fun calculatePercentage() {
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

}