package ph.edu.dlsu.finwise.personalFinancialManagementModule.breakdownFragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.TransactionsAdapter
import ph.edu.dlsu.finwise.model.Transactions


class ExpenseFragment : Fragment() {
    lateinit var pieChart: PieChart
    private var firestore = Firebase.firestore
    private var transactionsArrayList = ArrayList<Transactions>()



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

        loadPieChart(R.id.pc_expense)
    }

    private fun loadPieChart(pieChartLayout: Int) {
            //TODO:change to get transactions of current user
            //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
            //firestore.collection("Transactions").whereEqualTo("transactionID", currentUser).get()
            // .addOnSuccessListener{ documents ->

            firestore.collection("Transactions").get().addOnSuccessListener { documents ->
                for (transactionSnapshot in documents) {
                    //creating the object from list retrieved in db
                    val transactionID = transactionSnapshot.id
                    //transactionIDArrayList.add(transactionID)
                }

            }


        pieChart = view?.findViewById(pieChartLayout)!!
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
        // TODO: compute for percentage based on user
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(25f, "Food"))
        entries.add(PieEntry(10f, "Game"))
        entries.add(PieEntry(2f, "Toy"))
        entries.add(PieEntry(15f, "Transportation"))
        entries.add(PieEntry(3f, "Other"))



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


}