package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color.blue
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactSavingAdapter
import ph.edu.dlsu.finwise.databinding.DialogNewGoalWarningBinding
import ph.edu.dlsu.finwise.databinding.FragmentSavingBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.ChildNewGoal
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class SavingFragment : Fragment() {

    private lateinit var binding: FragmentSavingBinding
    private var firestore = Firebase.firestore
    private lateinit var goalAdapter: FinactSavingAdapter

    var goalIDArrayList = ArrayList<String>()
    var savingsArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<GoalFilter>()

    private var ongoingGoals = 0
    //includes achieved goals in count
    private var totalGoals = 0

    //vars for activity types for pie chart
    var nBuyingItem = 0
    var nEvent = 0
    var nEmergency = 0
    var nCharity = 0
    var nSituational =0
    var nEarning = 0

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ongoingGoals = 0
        totalGoals = 0
        goalIDArrayList.clear()
        savingsArrayList.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.titleOverallSavingPerformance.text = "Overall Saving\nPerformance"
        binding.tvPerformancePercentage.text = "0.00%"
        binding.btnNewGoal.setOnClickListener {
            if (ongoingGoals >= 5)
                buildDialog()
            else {
                var goToNewGoal = Intent(requireContext().applicationContext, ChildNewGoal::class.java)
                var bundle = Bundle()
                bundle.putString("source", "childFinancialActivity")
                goToNewGoal.putExtras(bundle)
                goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(goToNewGoal)
            }
        }
        checkAge()
        getGoals()
        getSavingActivities()
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){ }

    private fun getSavingActivities() {
        goalIDArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Saving").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            binding.tvInProgress.text = results.size().toString()
            for (activity in results) {
                var activityObject = activity.toObject<FinancialActivities>()
                goalIDArrayList.add(activityObject?.financialGoalID.toString())
            }
            loadRecyclerView(goalIDArrayList)
        }.continueWith { getTotalSavings() }
    }

    private fun getTotalSavings() {
        var savedAmount = 0.00F
        binding.tvGoalSavings.text = "₱ " + DecimalFormat("#,##0.00").format(savedAmount)

        firestore.collection("Transactions").whereEqualTo("createdBy", currentUser).whereIn("transactionType", Arrays.asList("Deposit", "Withdrawal")).get().addOnSuccessListener { results ->
            for (transaction in results) {
               var transactionObject = transaction.toObject<Transactions>()
               if (transactionObject?.transactionType == "Deposit")
                   savedAmount += transactionObject?.amount!!
               else if (transactionObject.transactionType == "Withdrawal")
                   savedAmount -= transactionObject?.amount!!
           }
        }.continueWith {
            binding.tvGoalSavings.text = "₱ " + DecimalFormat("#,##0.00").format(savedAmount)
        }
    }

    private fun setGoalCount() {
        var nearCompletion = 0
        var nearDeadline = 0
        binding.tvNearingCompletion.text = nearCompletion.toString()
        binding.tvNearingDeadline.text = nearDeadline.toString()
        var currentTime = Timestamp.now().toDate().time

        //set number of goals nearing completion and nearing deadline
        for (goalID in goalIDArrayList) {
            firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener {
                var goalObject = it.toObject<FinancialGoals>()

                //there is only 20% left before they are able to accomplish their goal, mark goal as near completion
                var amountRemaining =
                    ((goalObject?.targetAmount!! - goalObject?.currentSavings!!) / goalObject?.targetAmount!!) * 100
                if (amountRemaining <= 20)
                    nearCompletion++

                //there is only 20% left before their target date, mark goal as near deadline
                //target date in miliseconds
                var targetDate = goalObject?.targetDate!!.toDate().time
                var timeRemaining = ((targetDate!! - currentTime!!) / 100)
                if (timeRemaining <= 20)
                    nearDeadline++

            }.continueWith {
                binding.tvNearingCompletion.text = nearCompletion.toString()
                binding.tvNearingDeadline.text = nearDeadline.toString()
            }
        }
    }

    private fun setReasonPieChart() {
        var percentageBuying = 0.00F
        var percentageEvent = 0.00F
        var percentageEmergency = 0.00F
        var percentageSituational = 0.00F
        var percentageDonating = 0.00F
        var percentageEarning = 0.00F

        if (totalGoals!=0) {
             percentageBuying = ((nBuyingItem.toFloat() / totalGoals.toFloat()) * 100)
             percentageEvent = ((nEvent.toFloat() / totalGoals.toFloat()) * 100)
             percentageEmergency = ((nEmergency.toFloat() / totalGoals.toFloat()) * 100)
             percentageSituational = ((nSituational.toFloat() / totalGoals.toFloat()) * 100)
             percentageDonating = ((nCharity.toFloat() / totalGoals.toFloat()) * 100)
             percentageEarning = ((nEarning.toFloat() / totalGoals.toFloat()) * 100)
        }



        val entries = listOf(
            PieEntry(percentageBuying, "Buying Items"),
            PieEntry(percentageEvent, "Planning An Event"),
            PieEntry(percentageEmergency, "Saving For Emergency Funds"),
            PieEntry(percentageSituational, "Situational Shopping"),
            PieEntry(percentageDonating, "Donating To Charity"),
            PieEntry(percentageEarning, "Earning Money"),
        )

        var dataSet = PieDataSet(entries, "Data")

        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.purple_200))
        colors.add(resources.getColor( R.color.yellow))
        colors.add(resources.getColor(R.color.red))
        colors.add(resources.getColor( R.color.dark_green))
        colors.add(resources.getColor( R.color.teal_200))
        colors.add(resources.getColor( R.color.black))


        // setting colors.
        dataSet.colors = colors

        var data = PieData(dataSet)

        binding.pcReasonCategories.data = data
        binding.pcReasonCategories.invalidate()
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        goalAdapter = FinactSavingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = goalAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        goalAdapter.notifyDataSetChanged()
    }

    private fun getGoals() {
        var currentUser = "eWZNOIb9qEf8kVNdvdRzKt4AYrA2"
        //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).get().addOnSuccessListener { results ->
            totalGoals = results.size()
            for (goalSnapshot in results) {
                var goal = goalSnapshot.toObject<FinancialGoals>()
                if (goal.status == "In Progress")
                    ongoingGoals++

                when (goal.financialActivity) {
                    "Buying Items" -> nBuyingItem++
                    "Planning An Event" -> nEvent++
                    "Saving For Emergency Funds" -> nEmergency++
                    "Donating To Charity" -> nCharity++
                    "Situational Shopping" -> nSituational++
                    "Earning Money" -> nEarning++
                }
            }

        }.continueWith {
            setGoalCount()
            setReasonPieChart()
        }
    }


    private fun buildDialog() {

        var dialogBinding= DialogNewGoalWarningBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.getRoot())
        // Initialize dialog

        dialog.window!!.setLayout(900, 600)

        dialogBinding.tvMessage.text= "You have $ongoingGoals ongoing goals.\nAre you sure you want to start another one?"

        dialogBinding.btnOk.setOnClickListener {
            var newGoal = Intent (requireContext().applicationContext, ChildNewGoal::class.java)

            var bundle = Bundle()
            bundle.putString("source", "childFinancialActivity")
            newGoal.putExtras(bundle)
            newGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            this.startActivity(newGoal)
            dialog.dismiss()
        }
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAge() {
        firestore.collection("ChildUser").document(currentUser).get().addOnSuccessListener {
            var child = it.toObject<ChildUser>()
            //compute age
            val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var age = difference.years
            if (age == 9)
                binding.btnNewGoal.visibility = View.GONE
            else
                binding.btnNewGoal.visibility = View.VISIBLE

        }
    }

}