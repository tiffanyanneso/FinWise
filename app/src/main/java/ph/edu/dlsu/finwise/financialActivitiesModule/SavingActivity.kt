package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.GoalTransactionsAdapater
import ph.edu.dlsu.finwise.databinding.ActivitySavingBinding
import ph.edu.dlsu.finwise.model.DecisionMakingActivities
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class SavingActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySavingBinding
    private var firestore = Firebase.firestore
    private lateinit var context: Context

    private lateinit  var goalViewDepositAdapater:GoalTransactionsAdapater


    private lateinit var decisionMakingActivityID:String
    private lateinit var financialGoalID:String
    private var currentAmount:Float = 0.00F
    private var targetAmount:Float = 0.00F
    private var progress = 0.00F

    private lateinit var lineGraphView: GraphView

    private var transactionsArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        var bundle: Bundle = intent.extras!!
        decisionMakingActivityID = bundle.getString("decisionMakingActivityID").toString()
        financialGoalID = bundle.getString("financialGoalID").toString()

        getDepositHistory()

        binding.btnWithdraw.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("financialGoalID", financialGoalID)
            bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
            bundle.putInt("progress", progress.toInt())
            bundle.putFloat("currentAmount", currentAmount)
            bundle.putFloat("targetAmount", targetAmount)
            var goalWithdraw = Intent(this, WithdrawActivity::class.java)
            goalWithdraw.putExtras(bundle)
            context.startActivity(goalWithdraw)
        }

        binding.btnDeposit.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("financialGoalID", financialGoalID)
            bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
            bundle.putInt("progress", progress.toInt())
            bundle.putFloat("currentAmount", currentAmount)
            bundle.putFloat("targetAmount", targetAmount)
            var goalDeposit = Intent(this, FinancialActivityGoalDeposit::class.java)
            goalDeposit.putExtras(bundle)
            context.startActivity(goalDeposit)
        }

        binding.tvViewAllDeposit.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
            var viewDeposit = Intent(this, SavingViewTransactionsActivity::class.java)
            viewDeposit.putExtras(bundle)
            context.startActivity(viewDeposit)
        }
    }

    private fun getDepositHistory() {
        /*firestore.collection("Transactions").whereEqualTo("decisionMakingActivityID", decisionMakingActivityID).get().addOnSuccessListener { transactionsSnapshot ->
            currentAmount = 0.00F
            for (document in transactionsSnapshot) {
                var transaction = document.toObject<Transactions>()
                transactionsArrayList.add(transaction)
                if (transaction.transactionType == "Deposit")
                    currentAmount += transaction.amount!!
                else if (transaction.transactionType == "Withdrawal")
                    currentAmount -= transaction.amount!!
            }
            transactionsArrayList.sortByDescending { it.date }
            goalViewDepositAdapater = GoalTransactionsAdapater(this, transactionsArrayList)
            binding.rvViewDepositHistory.adapter = goalViewDepositAdapater
            binding.rvViewDepositHistory.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            getSavingProgress()
            initializeLineGraph()
        }*/
    }

    private fun getSavingProgress() {
        firestore.collection("DecisionMakingActivities").document(decisionMakingActivityID).get().addOnSuccessListener {
            var decisionMakingActvity = it.toObject<DecisionMakingActivities>()
            targetAmount = decisionMakingActvity?.targetAmount!!
            binding.tvGoalAmount.text = "₱ " + DecimalFormat("#,##0.00").format(currentAmount).toString() + " / ₱ " + DecimalFormat("#,##0.00").format(targetAmount).toString()
            progress = (currentAmount?.div(decisionMakingActvity?.targetAmount!!))?.times(100)!!
            if (progress != null) {
                binding.progressBar.progress  = progress.toInt()
            }
        }
    }

    //source: https://www.geeksforgeeks.org/android-line-graph-view-with-kotlin/
    private fun initializeLineGraph() {
        // on below line we are initializing
        // our variable with their ids.
        /*lineGraphView = findViewById(R.id.line_graph)

        var dates = ArrayList<Date>()
        var dataPoints = ArrayList<DataPoint>()

        //get unique dates in transaction arraylist
        for (transaction in transactionsArrayList) {
            //if array of dates doesn't contain date of the transaction, add the date to the arraylist
            if (!dates.contains(transaction.date?.toDate()))
                dates.add(transaction.date?.toDate()!!)
        }

        var sortedDate = dates.sortedBy { it }
        //get deposit for a specific date
        var xAxis =0.00
        for (date in sortedDate) {
            var depositTotal = 0.00F
            for (transaction in transactionsArrayList) {
                //comparing the dates if they are equal
                if (transaction != null ) {
                    if (date.compareTo(transaction.date?.toDate()) == 0) {
                        if (transaction.transactionType == "Deposit")
                            depositTotal += transaction.amount!!
                        else
                            depositTotal -= transaction.amount!!
                    }
                }
            }
            dataPoints.add(DataPoint(xAxis, depositTotal.toDouble()))
            xAxis++
        }


        //plot data to
        // on below line we are adding data to our graph view.
        val series: LineGraphSeries<DataPoint> = LineGraphSeries(dataPoints.toTypedArray())

        // on below line adding animation
        //lineGraphView.animate()

        // on below line we are setting scrollable
        // for point graph view
        //lineGraphView.viewport.isScrollable = true

        // on below line we are setting scalable.
        //lineGraphView.viewport.isScalable = true

        // on below line we are setting scalable y
        //lineGraphView.viewport.setScalableY(true)

        // on below line we are setting scrollable y
        //lineGraphView.viewport.setScrollableY(true)

        // on below line we are setting color for series.
        series.color = R.color.purple_200

        // on below line we are adding
        // data series to our graph view.
        lineGraphView.addSeries(series)*/
    }
}