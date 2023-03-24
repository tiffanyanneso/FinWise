package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
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
import ph.edu.dlsu.finwise.databinding.ActivityCashMayaBalanceBreakdownBinding
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.util.ArrayList

class CashMayaBalanceBreakdownActivity : AppCompatActivity() {

    private lateinit var binding:ActivityCashMayaBalanceBreakdownBinding
    private var firestore = Firebase.firestore
    private var transactionsArrayList = ArrayList<Transactions>()
    private var childID = FirebaseAuth.getInstance().currentUser!!.uid
    private var user = "child"
    private var cashBalance = 0.00f
    private var mayaBalance = 0.00f
    private var totalBalance = 0.00f
    private var cashPercentage = 0.00f
    private var mayaPercentage = 0.00f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCashMayaBalanceBreakdownBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBundleFromPFM()
        loadPieChart()

        setNavigationBar()
        loadBackButton()
    }

    private fun loadPieChart() {
        firestore.collection("ChildWallet").whereEqualTo("childID", childID)
            .get().addOnSuccessListener { transactionsSnapshot ->
                for (document in transactionsSnapshot) {
                    val wallet = document.toObject<ChildWallet>()
                    if (wallet.type == "Cash")
                        cashBalance += wallet.currentBalance!!
                    else if (wallet.type == "Maya")
                        mayaBalance += wallet.currentBalance!!
                }
            }.continueWith { initializeView() }
    }

    private fun initializeView() {
        loadBalanceText()
        calculatePercentages()
        loadChart()
    }

    private fun loadBalanceText() {
        val decimalFormat = DecimalFormat("#,##0.00")
        val cashBalanceFormatted = decimalFormat.format(cashBalance)
        val mayaBalanceFormatted = decimalFormat.format(mayaBalance)
        binding.tvPesoBalance.text = "₱$cashBalanceFormatted"
        binding.tvMayaBalance.text = "₱$mayaBalanceFormatted"
    }


    private fun calculatePercentages() {
        totalBalance = cashBalance + mayaBalance
        cashPercentage = cashBalance / totalBalance * 100
        mayaPercentage = mayaBalance / totalBalance * 100
    }

    private fun loadChart() {
        val chart: PieChart = findViewById(R.id.pc_cash_maya)!!

        val dataSet = setDataPieChart()
        setDataSettings(dataSet)
        chart.data = setPieDataSet(dataSet)
        addPesoSignToDataSet(dataSet)
        setPieChartSettings(chart)
        setLegendOfPieChart(chart)
        // loading chart
        chart.invalidate()
    }

    private fun setLegendOfPieChart(chart: PieChart) {
        // configure legend
        val legend = chart.legend
        legend.textSize = 14f
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.xEntrySpace = 10f
        legend.yEntrySpace = 0f
        legend.yOffset = 10f
        legend.textSize = 12f
    }

    private fun setPieChartSettings(chart: PieChart) {
        // undo all highlights
        chart.highlightValues(null)

        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.setExtraOffsets(5f, 10f, 5f, 5f)

        // disable hole in pie
        chart.isDrawHoleEnabled = false

        // setting drag for the pie chart
        chart.dragDecelerationFrictionCoef = 0.95f

        // setting circle color and alpha
        chart.setTransparentCircleColor(Color.WHITE)
        chart.setTransparentCircleAlpha(110)

        // setting center text
        chart.setDrawCenterText(true)

        // setting rotation for our pie chart
        chart.rotationAngle = 0f

        // enable rotation of the pieChart by touch
        chart.isRotationEnabled = true
        chart.isHighlightPerTapEnabled = true

        // setting animation for our pie chart
        chart.animateY(1400, Easing.EaseInOutQuad)
    }

    private fun addPesoSignToDataSet(dataSet: PieDataSet) {
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%.1f%%", value) // add the ₱ character to the data point values
            }
        }
    }

    private fun setPieDataSet(dataSet: PieDataSet): PieData? {
// setting pie data set
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(12f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        return data
    }

    private fun setDataSettings(dataSet: PieDataSet) {
        dataSet.colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.parseColor("#FFA500"))
        dataSet.valueTextSize = 14f

        // on below line we are setting icons.
        dataSet.setDrawIcons(true)

        // setting slice for pie
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        addColorsToDataSet(dataSet)
    }

    private fun addColorsToDataSet(dataSet: PieDataSet) {
        // add a lot of colors to list
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.dark_green))
        colors.add(resources.getColor( R.color.light_green))

        // setting colors.
        dataSet.colors = colors
    }

    private fun setDataPieChart(): PieDataSet {
        //  setting user percent value, setting description as enabled, and offset for pie chart
        val entries: ArrayList<PieEntry> = ArrayList()
        if (cashBalance > 0.00f)
            entries.add(PieEntry(cashPercentage, "Cash"))

        if (mayaBalance > 0.00f)
            entries.add(PieEntry(mayaPercentage, "Maya"))

        val TAG = "sdfdsdsfsf"
        Log.d(TAG, "setDataPieChart: "+cashBalance)
        Log.d(TAG, "setDataPieChart: "+mayaBalance)

        return PieDataSet(entries, "Values")
    }

    private fun getBundleFromPFM() {
        val getBundle = intent.extras
        if (getBundle?.containsKey("user") == true) {
            user = getBundle.getString("user").toString()
            if (user == "parent") {
                childID = getBundle.getString("childID").toString()
                binding.tvTitle.text = "You Child's Balance Breakdown"
            }
        }
    }


    private fun setNavigationBar() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val firestore = Firebase.firestore
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            val user  = it.toObject<Users>()!!
            //current user is a child
            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent
            if (user.userType == "Child") {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)
            } else  if (user.userType == "Parent"){
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                //sends the ChildID to the parent navbar
                val bundle = Bundle()
                val childID = bundle.getString("childID").toString()
                val bundleNavBar = Bundle()
                bundleNavBar.putString("childID", childID)
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance, bundleNavBar)
            }
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}