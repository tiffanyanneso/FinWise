package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactSavingAdapter
import ph.edu.dlsu.finwise.databinding.DialogNewGoalWarningBinding
import ph.edu.dlsu.finwise.databinding.DialogSavingReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogSmartReviewBinding
import ph.edu.dlsu.finwise.databinding.FragmentFinactSavingBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.NewGoal
import ph.edu.dlsu.finwise.financialActivitiesModule.SavingPerformanceActivity
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class SavingFragment : Fragment() {

    private lateinit var binding: FragmentFinactSavingBinding
    private var firestore = Firebase.firestore
    private lateinit var goalAdapter: FinactSavingAdapter

    var goalIDArrayList = ArrayList<String>()
    var savingsArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<GoalFilter>()

    private var ongoingGoals = 0
    //includes achieved goals in count
    private var totalGoals = 0

    private var setOwnGoals = false

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
        binding = FragmentFinactSavingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = "Overall Saving Performance"
        binding.tvPerformancePercentage.text = "0.00%"
        binding.btnNewGoal.setOnClickListener {
            if (ongoingGoals >= 5)
                buildDialog()
            else {
                var goToNewGoal = Intent(requireContext().applicationContext, NewGoal::class.java)
                var bundle = Bundle()
                bundle.putString("source", "childFinancialActivity")
                goToNewGoal.putExtras(bundle)
                goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(goToNewGoal)
            }
        }

        binding.btnReviewSaving.setOnClickListener{
            showGoalDialog()
        }

        binding.btnSeeMore.setOnClickListener {
            var goToPerformance = Intent(requireContext().applicationContext, SavingPerformanceActivity::class.java)
            startActivity(goToPerformance)
        }

        binding.btnSeeMore2.setOnClickListener {
            var goToPerformance = Intent(requireContext().applicationContext, SavingPerformanceActivity::class.java)
            startActivity(goToPerformance)
        }

        checkAge()
        getGoals()
        getSavingActivities()
        computeOverallScore()
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){ }

    private fun getSavingActivities() {
        goalIDArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            binding.tvTitleInProgress.text = "My Goals (" + results.size().toString() + ")"
            for (goal in results) {
                var goalObject = goal.toObject<FinancialGoals>()
                goalFilterArrayList.add(GoalFilter(goal.id, goalObject.targetDate!!.toDate()))
            }
            goalFilterArrayList.sortBy { it.goalTargetDate }

            for (goal in goalFilterArrayList)
                goalIDArrayList.add(goal.financialGoalID.toString())

            loadRecyclerView(goalIDArrayList)
        }.continueWith {
            getTotalSavings()
            setGoalCount()} 
    }

    private fun getTotalSavings() {
        var savedAmount = 0.00F
        var cashBalance = 0.00F
        var mayaBalance = 0.00F

        binding.tvGoalSavings.text = "₱ " + DecimalFormat("#,##0.00").format(savedAmount)

        firestore.collection("Transactions").whereEqualTo("userID", currentUser).whereIn("transactionType", Arrays.asList("Deposit", "Withdrawal")).get().addOnSuccessListener { results ->
            for (transaction in results) {
               var transactionObject = transaction.toObject<Transactions>()
               if (transactionObject?.transactionType == "Deposit")
                   savedAmount += transactionObject?.amount!!
               else if (transactionObject.transactionType == "Withdrawal")
                   savedAmount -= transactionObject?.amount!!

                if (transactionObject.paymentType == "Cash") {
                    if (transactionObject?.transactionType == "Deposit")
                        cashBalance += transactionObject?.amount!!
                    else if (transactionObject.transactionType == "Withdrawal")
                        cashBalance -= transactionObject?.amount!!
                }

                else if (transactionObject.paymentType == "Maya") {
                    if (transactionObject?.transactionType == "Deposit")
                        mayaBalance += transactionObject?.amount!!
                    else if (transactionObject.transactionType == "Withdrawal")
                        mayaBalance -= transactionObject?.amount!!
                }
           }
        }.continueWith {
            binding.tvGoalSavings.text = "₱ " + DecimalFormat("#,##0.00").format(savedAmount)
            binding.tvCashSavings.text = "₱ " + DecimalFormat("#,##0.00").format(cashBalance)
            binding.tvMayaSavings.text = "₱ " + DecimalFormat("#,##0.00").format(mayaBalance)

        }
    }

    private fun setGoalCount() {
        var nearCompletion = 0
        var nearDeadline = 0
//        binding.tvNearingCompletion.text = nearCompletion.toString()
//        binding.tvNearingDeadline.text = nearDeadline.toString()
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
//                binding.tvNearingCompletion.text = nearCompletion.toString()
//                binding.tvNearingDeadline.text = nearDeadline.toString()
            }
        }
    }

    private fun showSeeMoreButton() {
        binding.btnSeeMore.visibility = View.VISIBLE
        binding.layoutButtons.visibility = View.GONE
    }

    private fun showReviewButton() {
        binding.btnSeeMore.visibility = View.GONE
        binding.layoutButtons.visibility = View.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun computeOverallScore() {
        var nTotal = 0.00F
        var nOnTime =0.00F
        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->

            if (results.size()!=0) {
                nTotal = results.size().toFloat()
                for (goal in results) {
                    var goalObject = goal.toObject<FinancialGoals>()
                    if (goalObject.dateCompleted != null) {
                        var targetDate = goalObject?.targetDate!!.toDate()
                        var completedDate = goalObject?.dateCompleted!!.toDate()

                        //goal was completed before the target date, meaning it was completed on time
                        if (completedDate.before(targetDate) || completedDate.equals(targetDate)) {
                            nOnTime++
                        }
                    }
                }

                var overall = 0.00F
                if (nTotal != 0.00F)
                    overall = (nOnTime/nTotal) * 100

                val overallRoundedNumber = "%.1f".format(overall).toFloat()

                binding.tvPerformancePercentage.text ="${overallRoundedNumber}%"

                if (overall >= 96) {
                    binding.imgFace.setImageResource(R.drawable.excellent)
                    binding.tvPerformanceStatus.text = "Excellent"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                    binding.tvPerformanceText.text = "Keep up the excellent work! Saving is your strong point. Keep completing those goals!"
                    showSeeMoreButton()
                } else if (overall < 96 && overall >= 86) {
                    binding.imgFace.setImageResource(R.drawable.amazing)
                    binding.tvPerformanceStatus.text = "Amazing"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                    binding.tvPerformanceText.text = "Amazing job! You are performing well. Saving is your strong point. Keep completing those goals!"
                    showSeeMoreButton()
                } else if (overall < 86 && overall >= 76) {
                    binding.imgFace.setImageResource(R.drawable.great)
                    binding.tvPerformanceStatus.text = "Great"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                    binding.tvPerformanceText.text = "Great job! You are performing well. Keep completing those goals!"
                    showSeeMoreButton()
                } else if (overall < 76 && overall >= 66) {
                    binding.imgFace.setImageResource(R.drawable.good)
                    binding.tvPerformanceStatus.text = "Good"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                    binding.tvPerformanceText.text = "Good job! With a bit more dedication and effort, you’ll surely up your performance!"
                    showSeeMoreButton()
                } else if (overall < 66 && overall >= 56) {
                    binding.imgFace.setImageResource(R.drawable.average)
                    binding.tvPerformanceStatus.text = "Average"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                    binding.tvPerformanceText.text = "Nice work! Work on improving your saving performance through time and effort. You’ll get there soon!"
                    showSeeMoreButton()
                } else if (overall < 56 && overall >= 46) {
                    binding.imgFace.setImageResource(R.drawable.nearly_there)
                    binding.tvPerformanceStatus.text = "Nearly There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "You're nearly there! Click review to learn how to get there!"
                    showReviewButton()
                }  else if (overall < 46 && overall >= 36) {
                    binding.imgFace.setImageResource(R.drawable.almost_there)
                    binding.tvPerformanceStatus.text = "Almost There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Almost there! You need to work on your saving. Click review to learn how!"
                    showReviewButton()
                } else if (overall < 36 && overall >= 26) {
                    binding.imgFace.setImageResource(R.drawable.getting_there)
                    binding.tvPerformanceStatus.text = "Getting There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Getting there! You need to work on your saving. Click review to learn how!"
                    showReviewButton()
                } else if (overall < 26 && overall >= 16) {
                    binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                    binding.tvPerformanceStatus.text = "Not Quite There Yet"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Not quite there yet! Don't give up. Click review to learn how to get there!"
                    showReviewButton()
                } else if (overall < 15) {
                    binding.imgFace.setImageResource(R.drawable.bad)
                    binding.tvPerformanceStatus.text = "Needs Improvement"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Uh oh! Your saving performance needs a lot of improvement.  Click review to learn how!"
                    showReviewButton()
                }
            } else {
                binding.imgFace.setImageResource(R.drawable.good)
                binding.tvPerformanceStatus.text = ""
                binding.tvPerformanceText.text = "Complete your goals to see your performance"
            }
        }
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
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
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
            var newGoal = Intent (requireContext().applicationContext, NewGoal::class.java)

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
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            var child = it.toObject<Users>()
            //compute age
            val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var age = difference.years
            if (age == 9)
                binding.btnNewGoal.visibility = View.GONE
            else {
                binding.btnNewGoal.visibility = View.VISIBLE
                setOwnGoals = true
            }
        }
    }

    private fun showGoalDialog() {

        var dialogBinding= DialogSavingReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        if (!setOwnGoals)
            dialogBinding.btnSetNewGoal.visibility = View.GONE
        else
            dialogBinding.btnSetNewGoal.visibility = View.GONE

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnReviewGoals.setOnClickListener {
            dialog.dismiss()
            var goToGoalSetting = Intent(requireContext().applicationContext, FinancialActivity::class.java)
            this.startActivity(goToGoalSetting)
        }

        dialogBinding.btnSetNewGoal.setOnClickListener {
            dialog.dismiss()
            var goToNewGoal = Intent(requireContext().applicationContext, NewGoal::class.java)
            var bundle = Bundle()
            bundle.putString("source", "childFinancialActivity")
            goToNewGoal.putExtras(bundle)
            goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToNewGoal)
        }

        dialog.show()
    }

}