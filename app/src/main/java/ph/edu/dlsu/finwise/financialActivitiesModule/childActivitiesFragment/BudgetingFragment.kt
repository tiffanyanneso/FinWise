package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactBudgetingAdapter
import ph.edu.dlsu.finwise.databinding.DialogBudgetingReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogNewGoalWarningBinding
import ph.edu.dlsu.finwise.databinding.DialogSmartGoalInfoBinding
import ph.edu.dlsu.finwise.databinding.FragmentFinactBudgetingBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.performance.BudgetingPerformanceActivity
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class BudgetingFragment : Fragment() {

    private lateinit var binding: FragmentFinactBudgetingBinding
    private var firestore = Firebase.firestore
    private lateinit var budgetingAdapter: FinactBudgetingAdapter
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaPlayerDialog: MediaPlayer

    //contains only going budgeting activities for the recycler view
    var goalIDArrayList = ArrayList<String>()
    //used to get all budgeting activities to count parent involvement
    var budgetingArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<GoalFilter>()

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    //number of times the item was modified by the parent
    private var nParent = 0
    //number of budget items in total
    private var budgetItemCount = 0.00F
    //this is to count the number of budget items that have been already purchased in spending for budget accuracy
    private var purchasedBudgetItemCount  = 0.00F
    //budget variance
    private var totalBudgetAccuracy = 0.00F

    var nUpdates = 0.00F
    var nItems = 0.00F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goalIDArrayList.clear()
        budgetingArrayList.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinactBudgetingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = "Overall Budgeting Performance"
        getBudgeting()

        binding.btnSeeMore.setOnClickListener {
            pauseMediaPlayer(mediaPlayer)
            var goToPerformance = Intent(requireContext().applicationContext, BudgetingPerformanceActivity::class.java)
            this.startActivity(goToPerformance)
        }

        binding.btnSeeMore2.setOnClickListener {
            pauseMediaPlayer(mediaPlayer)
            var goToPerformance = Intent(requireContext().applicationContext, BudgetingPerformanceActivity::class.java)
            this.startActivity(goToPerformance)
        }

        binding.btnBudgetingReview.setOnClickListener{
            pauseMediaPlayer(mediaPlayer)
            showBudgetingReviewDialog()
        }
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }

    private fun getBudgeting() {
        goalIDArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            binding.tvTitleInProgress.text = "Budgeting Activities (" + results.size().toString() + ")"
            for (activity in results) {
                //add id to arraylit to load in recycler view
                var activityObject = activity.toObject<FinancialActivities>()
                goalIDArrayList.add(activityObject?.financialGoalID.toString())
            }
            getOverallBudgeting()
            if (!goalIDArrayList.isEmpty())
                loadRecyclerView(goalIDArrayList)
            else {
                binding.rvViewGoals.visibility = View.GONE
                binding.layoutEmptyActivity.visibility = View.VISIBLE
            }
        }.continueWith {
            binding.rvViewGoals.visibility = View.VISIBLE
            binding.loadingItems.stopShimmer()
            binding.loadingItems.visibility = View.GONE
        }
    }

    private fun getOverallBudgeting() {
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
            if (!results.isEmpty) {
                for (activity in results) {
                    firestore.collection("BudgetItems")
                        .whereEqualTo("financialActivityID", activity.id)
                        .whereEqualTo("status", "Active").get()
                        .addOnSuccessListener { budgetItems ->
                            println("print number of budget items" + budgetItems.size())
                            for (budgetItem in budgetItems) {
                                budgetItemCount++
                                var budgetItemObject = budgetItem.toObject<BudgetItem>()
                                if (budgetItemObject.status == "Edited")
                                    nUpdates++
                                //TODO: can't find this
                                //binding.tvAverageUpdates.text = (nUpdates / budgetItemCount).roundToInt().toString()


                                //parental involvement
                                firestore.collection("Users")
                                    .document(budgetItemObject.createdBy.toString()).get()
                                    .addOnSuccessListener { user ->
                                        //parent is the one who added the budget item
                                        if (user.toObject<Users>()!!.userType == "Parent")
                                            nParent++
                                    }.continueWith {
                                    getBudgetAccuracy(activity.id, budgetItem.id, budgetItemObject)
                                }
                            }
                        }
                }
            } else {
                binding.imgFace.setImageResource(R.drawable.peso_coin)
                binding.tvPerformancePercentage.text = "Get\nStarted!"
                binding.tvPerformanceText.text = "Complete budgeting activities to see your performance!"
            }
        }
    }

    private fun getBudgetAccuracy(budgetingActivityID:String, budgetItemID:String, budgetItemObject:BudgetItem) {
        firestore.collection("FinancialActivities").document(budgetingActivityID).get().addOnSuccessListener {
            firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", it.toObject<FinancialActivities>()!!.financialGoalID!!).whereEqualTo("financialActivityName", "Spending").get().addOnSuccessListener { spending ->
                var spendingActivity = spending.documents[0].toObject<FinancialActivities>()
                if (spendingActivity?.status == "Completed") {
                    //budget accuracy
                    purchasedBudgetItemCount++
                    firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID).get().addOnSuccessListener { transactions ->
                        var spent = 0.00F
                        for (transaction in transactions)
                            spent += transaction.toObject<Transactions>().amount!!
                        println("print budget accuracy " +  (100 - (abs(budgetItemObject.amount!! - spent) / budgetItemObject.amount!!) * 100))
                        if (budgetItemObject.amount!! !=0.00F)
                            totalBudgetAccuracy += (100 - (abs(budgetItemObject.amount!! - spent) / budgetItemObject.amount!!) * 100)
                    }.continueWith {
                        setOverall()
                    }
                } else
                    setOverall()
            }
        }
    }

    private fun setOverall() {
        var overall = 0.00F
        if (purchasedBudgetItemCount != 0.00F)
            overall = (  (totalBudgetAccuracy/purchasedBudgetItemCount) + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2
        else
            overall = (1 - (nParent.toFloat()/budgetItemCount)) * 100

        binding.tvPerformancePercentage.text = "${DecimalFormat("##0.0").format(overall)}%"

        //TODO: Change audio
        var audio = 0

        if (overall >= 96) {
            audio = R.raw.budgeting_performance_overall_excellent
            binding.imgFace.setImageResource(R.drawable.excellent)
            binding.textStatus.text = "Excellent"
            binding.textStatus.setTextColor(getResources().getColor(R.color.dark_green))
            binding.tvPerformanceText.text = "Keep up the excellent work! Budgeting is your strong point. Keep making those budgets!"
            showSeeMoreButton()
        } else if (overall < 96 && overall >= 86) {
            audio = R.raw.budgeting_performance_overall_amazing
            binding.imgFace.setImageResource(R.drawable.amazing)
            binding.textStatus.text = "Amazing"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "Amazing job! You are performing well. Budgeting is your strong point. Keep making those budgets!"
        } else if (overall < 86 && overall >= 76) {
            audio = R.raw.budgeting_performance_overall_great_good
            binding.imgFace.setImageResource(R.drawable.great)
            binding.textStatus.text = "Great"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "You are performing well. Keep making those budgets!"
            showSeeMoreButton()
        } else if (overall < 76 && overall >= 66) {
            audio = R.raw.budgeting_performance_overall_great_good
            binding.imgFace.setImageResource(R.drawable.good)
            binding.textStatus.text = "Good"
            binding.textStatus.setTextColor(getResources().getColor(R.color.light_green))
            binding.tvPerformanceText.text = "Good job! With a bit more attention to detail, you’ll surely up your performance!"
            showSeeMoreButton()
        } else if (overall < 66 && overall >= 56) {
            audio = R.raw.budgeting_performance_overall_average
            binding.imgFace.setImageResource(R.drawable.average)
            binding.textStatus.text = "Average"
            binding.textStatus.setTextColor(getResources().getColor(R.color.yellow))
            binding.tvPerformanceText.text = "Nice work! Work on improving your budget by always doublechecking. You’ll get there soon!"
            showSeeMoreButton()
        } else if (overall < 56 && overall >= 46) {
            audio = R.raw.budgeting_performance_overall_nearly_there
            binding.imgFace.setImageResource(R.drawable.nearly_there)
            binding.textStatus.text = "Nearly\nThere"
            binding.textStatus.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
            binding.tvPerformanceText.text = "You're nearly there! Click review to learn how to get there!"
            showSeeMoreButton()
        }  else if (overall < 46 && overall >= 36) {
            audio = R.raw.budgeting_performance_overall_almost_there
            binding.imgFace.setImageResource(R.drawable.almost_there)
            binding.textStatus.text = "Almost\nThere"
            binding.textStatus.setTextColor(getResources().getColor(R.color.almost_there_yellow))
            binding.tvPerformanceText.text = "Almost there! You need to work on your budgeting. Click review to learn how!"
            showSeeMoreButton()
        } else if (overall < 36 && overall >= 26) {
            audio = R.raw.budgeting_performance_overall_getting_there
            binding.imgFace.setImageResource(R.drawable.getting_there)
            binding.textStatus.text = "Getting\nThere"
            binding.textStatus.setTextColor(getResources().getColor(R.color.getting_there_orange))
            binding.tvPerformanceText.text = "Getting there! You need to work on your budgeting. Click review to learn how!"
            showSeeMoreButton()
        } else if (overall < 26 && overall >= 16) {
            audio = R.raw.budgeting_performance_overall_not_quite_there_yet
            binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
            binding.textStatus.text = "Not Quite\nThere"
            binding.textStatus.setTextColor(getResources().getColor(R.color.not_quite_there_red))
            binding.tvPerformanceText.text = "Not quite there yet! Don't give up. Click review to learn how to get there!"
            showSeeMoreButton()
        } else if (overall < 15) {
            audio = R.raw.budgeting_performance_accuracy_needs_improvement
            binding.imgFace.setImageResource(R.drawable.bad)
            binding.textStatus.text = "Needs\nImprovement"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Your budgeting performance needs a lot of improvement. Click review to learn how!"
            showSeeMoreButton()
        }

        loadAudio(audio)
    }
    private fun loadAudio(audio: Int) {
        //TODO: Change binding and Audio file in mediaPlayer

        binding.btnAudioOverallBudgetingPerformance.setOnClickListener {
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

        if (this::mediaPlayerDialog.isInitialized)
            releaseMediaPlayer(mediaPlayerDialog)

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


    private fun showSeeMoreButton() {
        binding.btnSeeMore.visibility = View.VISIBLE
        binding.layoutButtons.visibility = View.GONE
    }

    private fun showReviewButton() {
        binding.btnSeeMore.visibility = View.GONE
        binding.layoutButtons.visibility = View.VISIBLE
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        budgetingAdapter = FinactBudgetingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = budgetingAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext, LinearLayoutManager.VERTICAL, false)
        budgetingAdapter.notifyDataSetChanged()
    }

    private fun showBudgetingReviewDialog() {

        val dialogBinding= DialogBudgetingReviewBinding.inflate(layoutInflater)
        val dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.root)

        dialog.window!!.setLayout(1000, 1400)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }
        loadAudioDialog(dialogBinding)
        dialog.setOnDismissListener { pauseMediaPlayer(mediaPlayerDialog) }

        dialog.show()

    }

    private fun pauseMediaPlayer(mediaPlayer: MediaPlayer) {
        mediaPlayer.let {
            if (it.isPlaying) {
                it.pause()
                it.seekTo(0)
            }
        }
    }


    private fun loadAudioDialog(dialogBinding: DialogBudgetingReviewBinding) {
        /*TODO: Change binding and Audio file in mediaPlayer*/
        val audio = R.raw.dialog_budgeting_review

        dialogBinding.btnSoundBudgetReview.setOnClickListener {
            if (!this::mediaPlayerDialog.isInitialized) {
                mediaPlayerDialog = MediaPlayer.create(context, audio)
            }

            if (mediaPlayerDialog.isPlaying) {
                mediaPlayerDialog.pause()
                mediaPlayerDialog.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerDialog.start()
        }
    }


}