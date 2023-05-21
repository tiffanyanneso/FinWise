package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentGoalFragment

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentPerformance.ParentSavingPerformanceActivity
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactSavingAdapter
import ph.edu.dlsu.finwise.databinding.DialogNewGoalWarningBinding
import ph.edu.dlsu.finwise.databinding.DialogParentSavingTipsBinding
import ph.edu.dlsu.finwise.databinding.DialogSmartGoalInfoBinding
import ph.edu.dlsu.finwise.databinding.FragmentParentSavingBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.NewGoal
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ParentSavingFragment : Fragment() {

    private lateinit var binding: FragmentParentSavingBinding
    private var firestore = Firebase.firestore
    private lateinit var goalAdapter: FinactSavingAdapter

    private lateinit var mediaPlayerGoalDialog: MediaPlayer
    private lateinit var mediaPlayerSmartDialog: MediaPlayer

    var goalIDArrayList = ArrayList<String>()
    var savingsArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<GoalFilter>()

    private var ongoingGoals = 0
    //includes achieved goals in count
    private var totalGoals = 0

    private var setOwnGoals = false
    private lateinit var childID: String

    //vars for activity types for pie chart
    var nBuyingItem = 0
    var nEvent = 0
    var nEmergency = 0
    var nCharity = 0
    var nSituational =0
    var nEarning = 0

//    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        childID = bundle?.getString("childID").toString()
        ongoingGoals = 0
        totalGoals = 0
        goalIDArrayList.clear()
        savingsArrayList.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentSavingBinding.inflate(inflater, container, false)
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
            else
                showSMARTGoalsDialog()
        }
        binding.btnTips.setOnClickListener{
            showGoalDialog()
        }

        binding.btnSeeMore.setOnClickListener {
            var sendBundle = Bundle()
            sendBundle.putString("childID", childID)

            var goToPerformance = Intent(requireContext().applicationContext, ParentSavingPerformanceActivity::class.java)
            goToPerformance.putExtras(sendBundle)
//            goToPerformance.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(goToPerformance)
        }

        binding.btnSeeMore2.setOnClickListener {
            var goToPerformance = Intent(requireContext().applicationContext, ParentSavingPerformanceActivity::class.java)

            var bundle = Bundle()

            bundle.putString("childID", childID)
            goToPerformance.putExtras(bundle)
            goToPerformance.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(goToPerformance)
        }

        getGoals()
        getSavingActivities()
        computeOverallScore()
    }

    private fun showSMARTGoalsDialog() {
        var dialogBinding= DialogSmartGoalInfoBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext());
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            var sendBundle = Bundle()
            sendBundle.putString("childID", childID)
            sendBundle.putString("source", "Parent")
            var newGoal = Intent(requireContext().applicationContext, NewGoal::class.java)
            newGoal.putExtras(sendBundle)
            startActivity(newGoal)
            dialog.dismiss()
        }

        loadSmartAudioDialog(dialogBinding)
        dialog.setOnDismissListener { pauseMediaPlayer(mediaPlayerSmartDialog) }

        dialog.show()
    }

    override fun onDestroy() {
        if (this::mediaPlayerSmartDialog.isInitialized)
            releaseMediaPlayer(mediaPlayerSmartDialog)

        if (this::mediaPlayerSmartDialog.isInitialized)
            releaseMediaPlayer(mediaPlayerSmartDialog)

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



    private fun loadSmartAudioDialog(dialogBinding: DialogSmartGoalInfoBinding) {
        /*TODO: Change binding and Audio file in mediaPlayer*/
        val audio = R.raw.sample

        dialogBinding.btnSoundSmartGoalInfo.setOnClickListener {
            if (!this::mediaPlayerSmartDialog.isInitialized) {
                mediaPlayerSmartDialog = MediaPlayer.create(context, audio)
            }

            if (mediaPlayerSmartDialog.isPlaying) {
                mediaPlayerSmartDialog.pause()
                mediaPlayerSmartDialog.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerSmartDialog.start()
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


    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){ }

    private fun getSavingActivities() {
        goalIDArrayList.clear()
        goalFilterArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialGoals").whereEqualTo("childID", childID).whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            binding.tvTitleInProgress.text = "My Goals (" + results.size().toString() + ")"
            for (goal in results) {
                var goalObject = goal.toObject<FinancialGoals>()
                goalFilterArrayList.add(
                    ParentSavingFragment.GoalFilter(
                        goal.id,
                        goalObject.targetDate!!.toDate()
                    )
                )
            }
            goalFilterArrayList.sortBy { it.goalTargetDate }

            for (goal in goalFilterArrayList)
                goalIDArrayList.add(goal.financialGoalID.toString())

            loadRecyclerView(goalIDArrayList)
        }.continueWith {
            binding.rvViewGoals.visibility = View.VISIBLE
            binding.loadingItems.stopShimmer()
            binding.loadingItems.visibility = View.GONE
            getTotalSavings()
            setGoalCount()}
    }

    private fun getTotalSavings() {
        var savedAmount = 0.00F
        var cashBalance = 0.00F
        var mayaBalance = 0.00F

        binding.tvGoalSavings.text = "₱ " + DecimalFormat("#,##0.00").format(savedAmount)

        firestore.collection("Transactions").whereEqualTo("userID", childID).whereIn("transactionType", Arrays.asList("Deposit", "Withdrawal")).get().addOnSuccessListener { results ->
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
        var overall = 0.00F
        firestore.collection("FinancialGoals").whereEqualTo("childID", childID).whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->

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

                    if (nTotal != 0.00F)
                        overall = (nOnTime/nTotal) * 100
                    val overallRoundedNumber = "%.1f".format(overall).toFloat()

                    binding.tvPerformancePercentage.text ="${overallRoundedNumber}%"
                }

                if (overall >= 96) {
                    binding.imgFace.setImageResource(R.drawable.excellent)
                    binding.tvPerformanceStatus.text = "Excellent"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                    binding.tvPerformanceText.text = "Your child excels at saving! Encourage them to continue completing their goals."
                    showSeeMoreButton()
                } else if (overall < 96 && overall >= 86) {
                    binding.imgFace.setImageResource(R.drawable.amazing)
                    binding.tvPerformanceStatus.text = "Amazing"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                    binding.tvPerformanceText.text = "Your child is amazing at goal setting! Encourage them to keep completing those goals!"
                    showSeeMoreButton()
                } else if (overall < 86 && overall >= 76) {
                    binding.imgFace.setImageResource(R.drawable.great)
                    binding.tvPerformanceStatus.text = "Great"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                    binding.tvPerformanceText.text = "Your child is doing a great job of saving for their goals"
                    showSeeMoreButton()
                } else if (overall < 76 && overall >= 66) {
                    binding.imgFace.setImageResource(R.drawable.good)
                    binding.tvPerformanceStatus.text = "Good"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                    binding.tvPerformanceText.text = "Your child is doing a great job of completing goals! Encourage them to save consistently."
                    showSeeMoreButton()
                } else if (overall < 66 && overall >= 56) {
                    binding.imgFace.setImageResource(R.drawable.average)
                    binding.tvPerformanceStatus.text = "Average"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                    binding.tvPerformanceText.text = "Your child is doing a nice job of completing goals! Encourage them to save more consistently."
                    showSeeMoreButton()
                } else if (overall < 56 && overall >= 46) {
                    binding.imgFace.setImageResource(R.drawable.nearly_there)
                    binding.tvPerformanceStatus.text = "Nearly There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Your child is nearly there! Click the tips button to learn how to help them get there!"
                    showSeeMoreButton()
                }  else if (overall < 46 && overall >= 36) {
                    binding.imgFace.setImageResource(R.drawable.almost_there)
                    binding.tvPerformanceStatus.text = "Almost There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Your child is almost there! Click the tips button to learn how to help them get there!"
                    showSeeMoreButton()
                } else if (overall < 36 && overall >= 26) {
                    binding.imgFace.setImageResource(R.drawable.getting_there)
                    binding.tvPerformanceStatus.text = "Getting There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Your child is getting there! Click the tips button to learn how to help them get there!"
                    showSeeMoreButton()
                } else if (overall < 26 && overall >= 16) {
                    binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                    binding.tvPerformanceStatus.text = "Not Quite\nThere"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Your child is not quite there yet! Click the tips button to learn how to help them get there!"
                    showSeeMoreButton()
                } else if (overall < 15) {
                    binding.imgFace.setImageResource(R.drawable.bad)
                    binding.tvPerformanceStatus.text = "Needs\nImprovement"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Uh oh! Click the tips button to learn how to help them improve their saving performance!"
                    showSeeMoreButton()
                }
            } else {
                binding.imgFace.setImageResource(R.drawable.peso_coin)
                binding.tvPerformancePercentage.visibility = View.GONE
                binding.tvPerformanceStatus.visibility = View.GONE
                binding.tvPerformanceText.text = "Your child hasn't completed any goals. Come back soon!"
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

        var dialogBinding= DialogNewGoalWarningBinding.inflate(layoutInflater)
        var dialog= Dialog(requireContext());
        dialog.setContentView(dialogBinding.root)
        // Initialize dialog

        dialog.window!!.setLayout(900, 800)

        dialogBinding.tvMessage.text= "Your child has $ongoingGoals ongoing goals.\nAre you sure you want to start another one?"

        dialogBinding.btnOk.setOnClickListener {
            var newGoal = Intent (requireContext(), NewGoal::class.java)

            var bundle = Bundle()
            bundle.putString("source", "Parent")
            newGoal.putExtras(bundle)
            newGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(newGoal)
            dialog.dismiss()
        }
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showGoalDialog() {

        var dialogBinding= DialogParentSavingTipsBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }
        loadGoalAudioDialog(dialogBinding)
        dialog.setOnDismissListener { pauseMediaPlayer(mediaPlayerGoalDialog) }
        dialog.show()
    }

    private fun loadGoalAudioDialog(dialogBinding: DialogParentSavingTipsBinding) {
        /*TODO: Change binding and Audio file in mediaPlayer*/
        val audio = R.raw.sample

        dialogBinding.btnSoundParentSavingTips.setOnClickListener {
            if (!this::mediaPlayerGoalDialog.isInitialized) {
                mediaPlayerGoalDialog = MediaPlayer.create(context, audio)
            }

            if (mediaPlayerGoalDialog.isPlaying) {
                mediaPlayerGoalDialog.pause()
                mediaPlayerGoalDialog.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerGoalDialog.start()
        }
    }

}