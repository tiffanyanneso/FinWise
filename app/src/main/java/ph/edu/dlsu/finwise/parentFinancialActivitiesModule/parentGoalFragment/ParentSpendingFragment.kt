package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentGoalFragment

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentPerformance.ParentSpendingPerformanceActivity
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactSpendingAdapter
import ph.edu.dlsu.finwise.databinding.DialogParentSpendingTipsBinding
import ph.edu.dlsu.finwise.databinding.FragmentParentSpendingBinding
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ParentSpendingFragment : Fragment() {

    private lateinit var binding: FragmentParentSpendingBinding
    private var firestore = Firebase.firestore
    private lateinit var spendingAdapter: FinactSpendingAdapter

    private lateinit var mediaPlayerDialog: MediaPlayer


    var spendingActivityIDArrayList = ArrayList<String>()
    var budgetItemsIDArrayList = ArrayList<String>()
    var goalIDArrayList = ArrayList<String>()
    var budgetingArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<GoalFilter>()

    private var overSpending = 0.00F
    private var nBudgetItems = 0.00F
    private var nSpendingCompleted = 0

    var nPlanned = 0.00F
    var nTotalPurchased = 0.00F

    var overspendingPercentage = 0.00F
    var purchasePlanningPercentage = 0.00F
    var overallSpending = 0.00F

    private lateinit var childID:String
    private var age = 0

    private var coroutineScope = CoroutineScope(Dispatchers.Main)


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        childID = bundle?.getString("childID").toString()
        goalIDArrayList.clear()
        budgetingArrayList.clear()
        spendingActivityIDArrayList.clear()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentSpendingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isAdded) {
            binding.title.text= "Overall Spending Performance"
            getSpending()
            //need to get the budgeting activities to be able to get the budget items
            coroutineScope.launch {
                checkAge()
                getBudgeting()
                checkOverSpending()
                if (age > 9 )
                    purchasePlanning()

                else {
                    overallSpending = (1-overspendingPercentage)*100
                    if (isAdded)
                        setOverall()
                }
            }
        }


        binding.btnSpendingTips.setOnClickListener {
            showSpendingReivewDialog()
        }

        binding.btnSeeMore.setOnClickListener {
            var goToPerformance = Intent(requireContext().applicationContext, ParentSpendingPerformanceActivity::class.java)

            var bundle = Bundle()

            bundle.putString("childID", childID)
            goToPerformance.putExtras(bundle)
            goToPerformance.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            this.startActivity(goToPerformance)
        }

        binding.btnSeeMore2.setOnClickListener {
            var goToPerformance = Intent(requireContext().applicationContext, ParentSpendingPerformanceActivity::class.java)

            var bundle = Bundle()

            bundle.putString("childID", childID)
            goToPerformance.putExtras(bundle)
            goToPerformance.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            this.startActivity(goToPerformance)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope.cancel()
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }


    private fun getSpending() {
        spendingActivityIDArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            for (activity in results) {
                var activityObject = activity.toObject<FinancialActivities>()
                spendingActivityIDArrayList.add(activityObject?.financialGoalID.toString())
            }
            if (!spendingActivityIDArrayList.isEmpty())
                loadRecyclerView(spendingActivityIDArrayList)
            else {
                binding.rvViewGoals.visibility = View.GONE
                binding.layoutEmptyActivity.visibility = View.VISIBLE
            }
        }.continueWith {
            binding.rvViewGoals.visibility = View.VISIBLE
            binding.loadingItems.stopShimmer()
            binding.loadingItems.visibility = View.GONE
            binding.tvTitleInProgress.text = "Spending Activities (" + spendingActivityIDArrayList.size.toString() + ")"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getBudgeting() {
        var spendingCompleted = firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "Completed").get().await()
        nSpendingCompleted = spendingCompleted.size()
        if (nSpendingCompleted > 0) {
            //get only completed budgeting activities because they should complete budgeting first before they are able to spend
            var budgetingActivity =
                firestore.collection("FinancialActivities").whereEqualTo("childID", childID)
                    .whereEqualTo("financialActivityName", "Budgeting")
                    .whereEqualTo("status", "Completed").get().await()

            for (budgeting in budgetingActivity) {
                //get budget items
                var budgetItems = firestore.collection("BudgetItems")
                    .whereEqualTo("financialActivityID", budgeting.id)
                    .whereEqualTo("status", "Active").get().await()
                nBudgetItems += budgetItems.size()

                for (budgetItem in budgetItems)
                    budgetItemsIDArrayList.add(budgetItem.id)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun checkOverSpending(){

        for (budgetItemID in budgetItemsIDArrayList) {
            var spendingTransactions = firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID).whereEqualTo("transactionType", "Expense").get().await().toObjects<Transactions>()
            var amountSpent = 0.00F
            for (expense in spendingTransactions) {
                amountSpent+= expense.amount!!
            }
            //they spent more than their allocated budget
            var budgetItemAmount = firestore.collection("BudgetItems").document(budgetItemID).get().await().toObject<BudgetItem>()?.amount!!
            if (amountSpent > budgetItemAmount)
                overSpending++
        }

        overspendingPercentage = (overSpending/nBudgetItems)
    }


    private suspend fun purchasePlanning() {
        //items planned / all the items they bought * 100
        var nPlanned = 0.00F
        var nTotalPurchased = 0.00F
        var spendingActivities = firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "Completed").get().await()

        for (spendingActivityID in spendingActivities) {
            var shoppingListItems = firestore.collection("ShoppingListItems").whereEqualTo("spendingActivityID", spendingActivityID.id).whereEqualTo("status", "Purchased").get().await()
            nPlanned  += shoppingListItems.size()

            nTotalPurchased += firestore.collection("Transactions").whereEqualTo("financialActivityID", spendingActivityID.id).whereEqualTo("transactionType", "Expense").get().await().size().toFloat()
        }

        val purchasePlanningPercentage = (nPlanned/nTotalPurchased)*100
        overallSpending = (((1-overspendingPercentage)*100) + purchasePlanningPercentage) /2

        if (isAdded)
            setOverall()

    }

    private fun setOverall() {
        if (nSpendingCompleted > 0 ) {
            binding.tvPerformancePercentage.text = "${DecimalFormat("##0.0").format(overallSpending)}%"
            binding.tvPerformanceText.visibility = View.VISIBLE


            if (overallSpending >= 96) {
                binding.imgFace.setImageResource(R.drawable.excellent)
                binding.tvPerformanceStatus.text = "Excellent"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                binding.tvPerformanceText.text =
                    "Your child excels at spending wisely. Encourage them to keep it up!"
                showSeeMoreButton()
            } else if (overallSpending < 96 && overallSpending >= 86) {
                binding.imgFace.setImageResource(R.drawable.amazing)
                binding.tvPerformanceStatus.text = "Amazing"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                binding.tvPerformanceText.text =
                    "Your child is amazing at spending! Encourage them to keep it up!"
                showSeeMoreButton()
            } else if (overallSpending < 86 && overallSpending >= 76) {
                binding.imgFace.setImageResource(R.drawable.great)
                binding.tvPerformanceStatus.text = "Great"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                binding.tvPerformanceText.text =
                    "Your child is doing a great job of spending wisely!"
                showSeeMoreButton()
            } else if (overallSpending < 76 && overallSpending >= 66) {
                binding.imgFace.setImageResource(R.drawable.good)
                binding.tvPerformanceStatus.text = "Good"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                binding.tvPerformanceText.text =
                    "Your child is doing a good job of spending wisely!"
                showSeeMoreButton()
            } else if (overallSpending < 66 && overallSpending >= 56) {
                binding.imgFace.setImageResource(R.drawable.average)
                binding.tvPerformanceStatus.text = "Average"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                binding.tvPerformanceText.text =
                    "Your child is doing a nice job of spending! Encourage them to plan ahead."
                showSeeMoreButton()
            } else if (overallSpending < 56 && overallSpending >= 46) {
                binding.imgFace.setImageResource(R.drawable.nearly_there)
                binding.tvPerformanceStatus.text = "Nearly There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
                binding.tvPerformanceText.text =
                    "Your child is nearly there! Click the tips button to learn how to help them get there!"
                showSeeMoreButton()
            } else if (overallSpending < 46 && overallSpending >= 36) {
                binding.imgFace.setImageResource(R.drawable.almost_there)
                binding.tvPerformanceStatus.text = "Almost There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.almost_there_yellow))
                binding.tvPerformanceText.text =
                    "Your child is almost there! Click the tips button to learn how to help them get there!"
                showSeeMoreButton()
            } else if (overallSpending < 36 && overallSpending >= 26) {
                binding.imgFace.setImageResource(R.drawable.getting_there)
                binding.tvPerformanceStatus.text = "Getting There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.getting_there_orange))
                binding.tvPerformanceText.text =
                    "Your child is getting there! Click the tips button to learn how to help them get there!"
                showSeeMoreButton()
            } else if (overallSpending < 26 && overallSpending >= 16) {
                binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                binding.tvPerformanceStatus.text = "Not Quite\nThere"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.not_quite_there_red))
                binding.tvPerformanceText.text =
                    "Your child is not quite there yet! Click the tips button to learn how to help them get there!"
                showSeeMoreButton()
            } else if (overallSpending < 15) {
                binding.imgFace.setImageResource(R.drawable.bad)
                binding.tvPerformanceStatus.text = "Needs\nImprovement"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text =
                    "Uh oh! Click the tips button to learn how to help them improve their spending!"
                showSeeMoreButton()
            }
        } else {
            binding.tvPerformancePercentage.text = "Get\nStarted!"
            binding.tvPerformanceText.text = "Your child hasn't completed any spending activities. Come back later to see their performance!"
        }
        binding.layoutLoading.visibility= View.GONE
        binding.mainLayout.visibility = View.VISIBLE
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
        spendingAdapter = FinactSpendingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = spendingAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext, LinearLayoutManager.VERTICAL, false)
        spendingAdapter.notifyDataSetChanged()
    }

    private fun showSpendingReivewDialog() {

        var dialogBinding= DialogParentSpendingTipsBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

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

    private fun loadAudioDialog(dialogBinding: DialogParentSpendingTipsBinding) {
        /*TODO: Change binding and Audio file in mediaPlayer*/
        val audio = R.raw.dialog_parent_spending_tips

        dialogBinding.btnSoundParentSpending.setOnClickListener {
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

    override fun onDestroy() {
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

    private suspend fun checkAge() {
        var child = firestore.collection("Users").document(childID).get().await().toObject<Users>()
        //compute age
        val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val from = LocalDate.now()
        val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
        val to = LocalDate.parse(date.toString(), dateFormatter)
        var difference = Period.between(to, from)

        age = difference.years
    }


}