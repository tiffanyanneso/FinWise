package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactSpendingAdapter
import ph.edu.dlsu.finwise.databinding.DialogSpendingReviewBinding
import ph.edu.dlsu.finwise.databinding.FragmentFinactSpendingBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.performance.SpendingPerformanceActivity
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class SpendingFragment : Fragment(){

    class BudgetItemAmount(var budgetItemID:String, var amount:Float)

    private lateinit var binding: FragmentFinactSpendingBinding
    private var firestore = Firebase.firestore
    private lateinit var spendingAdapter: FinactSpendingAdapter
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaPlayerDialog: MediaPlayer


    var spendingActivityIDArrayList = ArrayList<String>()
    var budgetItemsIDArrayList = ArrayList<String>()
    var goalFilterArrayList = ArrayList<GoalFilter>()

    private var overSpending = 0.00F
    private var nBudgetItems = 0.00F

    var nPlanned = 0.00F
    var nTotalPurchased = 0.00F

    var overspendingPercentage = 0.00F
    var purchasePlanningPercentage = 0.00F
    var overallSpending = 0.00F


    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private var age = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        spendingActivityIDArrayList.clear()
        budgetItemsIDArrayList.clear()
        getSpending()
        //need to get the budgeting activities to be able to get the budget items
        CoroutineScope(Dispatchers.Main).launch {
            checkAge()
            getBudgeting()
            checkOverSpending()
            if (age > 9 )
                purchasePlanning()

            else {
                overallSpending = (100-overspendingPercentage)*100
                binding.tvPerformancePercentage.text ="${DecimalFormat("##0.0").format(overallSpending)}%"
                setOverall()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFinactSpendingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text= "Overall Spending Performance"

        binding.btnSpendingReview.setOnClickListener {
            if (this::mediaPlayer.isInitialized)
                pauseMediaPlayer(mediaPlayer)
            showSpendingReviewDialog()
        }

        binding.btnSeeMore.setOnClickListener {
            if (this::mediaPlayer.isInitialized)
                pauseMediaPlayer(mediaPlayer)
            var goToPerformance = Intent(requireContext().applicationContext, SpendingPerformanceActivity::class.java)
            this.startActivity(goToPerformance)
        }

        binding.btnSeeMore2.setOnClickListener {
            if (this::mediaPlayer.isInitialized)
                pauseMediaPlayer(mediaPlayer)
            var goToPerformance = Intent(requireContext().applicationContext, SpendingPerformanceActivity::class.java)
            this.startActivity(goToPerformance)
        }
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }

    private fun getSpending() {
        spendingActivityIDArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
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
        //get only completed budgeting activities because they should complete budgeting first before they are able to spend
        var budgetingActivity = firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Completed").get().await()

        for (budgeting in budgetingActivity) {
            //get budget items
            var budgetItems = firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgeting.id).whereEqualTo("status", "Active").get().await()
            nBudgetItems += budgetItems.size()

            for (budgetItem in budgetItems)
                budgetItemsIDArrayList.add(budgetItem.id)
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
        var spendingActivities = firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "Completed").get().await()

        for (spendingActivityID in spendingActivities) {
            var shoppingListItems = firestore.collection("ShoppingListItems").whereEqualTo("spendingActivityID", spendingActivityID.id).get().await().toObjects<ShoppingListItem>()
            for (shoppingListItem in shoppingListItems) {
                if (shoppingListItem.status == "Purchased")
                    nPlanned++
            }

            nTotalPurchased += firestore.collection("Transactions").whereEqualTo("financialActivityID", spendingActivityID.id).whereEqualTo("transactionType", "Expense").get().await().size().toFloat()
        }

        val purchasePlanningPercentage = (nPlanned/nTotalPurchased)*100
        overallSpending = (((1-overspendingPercentage)*100) + purchasePlanningPercentage) /2
        binding.tvPerformancePercentage.text ="${DecimalFormat("##0.0").format(overallSpending)}%"

        setOverall()

    }

    private fun setOverall() {
        println("print overspending " + ((1-overspendingPercentage)*100))
        println("print purchase planning" + (nPlanned/nTotalPurchased)*100)
        binding.tvPerformancePercentage.text ="${DecimalFormat("##0.0").format(overallSpending)}%"
        //TODO: Change audio
        var audio = 0
        if (overallSpending >= 96) {
            audio = R.raw.spending_performance_excellent
            binding.imgFace.setImageResource(R.drawable.excellent)
            binding.textStatus.text = "Excellent"
            binding.textStatus.setTextColor(getResources().getColor(R.color.dark_green))
            binding.tvPerformanceText.text = "Keep up the excellent work! Spending wisely is your strong point. Keep it up!"
            showSeeMoreButton()
        } else if (overallSpending < 96 && overallSpending >= 86) {
            audio = R.raw.spending_performance_amazing
            binding.imgFace.setImageResource(R.drawable.amazing)
            binding.textStatus.text = "Amazing"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "Amazing job! You are performing well. Spending wisely is your strong point. Keep completing those goals!"
            showSeeMoreButton()
        } else if (overallSpending < 86 && overallSpending >= 76) {
            audio = R.raw.spending_performance_great
            binding.imgFace.setImageResource(R.drawable.great)
            binding.textStatus.text = "Great"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = " Great job! You are performing well. Keep spending wisely!"
            showSeeMoreButton()
        } else if (overallSpending < 76 && overallSpending >= 66) {
            audio = R.raw.spending_performance_good
            binding.imgFace.setImageResource(R.drawable.good)
            binding.textStatus.text = "Good"
            binding.textStatus.setTextColor(getResources().getColor(R.color.light_green))
            binding.tvPerformanceText.text = "Good job! With a bit more planning to detail, you’ll surely up your performance!"
            showSeeMoreButton()
        } else if (overallSpending < 66 && overallSpending >= 56) {
            audio = R.raw.spending_performance_good
            binding.imgFace.setImageResource(R.drawable.average)
            binding.textStatus.text = "Average"
            binding.textStatus.setTextColor(getResources().getColor(R.color.yellow))
            binding.tvPerformanceText.text = "Nice work! Work on improving your spending performance by always planning ahead. You’ll get there soon!"
            showSeeMoreButton()
        } else if (overallSpending < 56 && overallSpending >= 46) {
            audio = R.raw.spending_performance_nearly_there
            binding.imgFace.setImageResource(R.drawable.nearly_there)
            binding.textStatus.text = "Nearly\nThere"
            binding.textStatus.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
            binding.tvPerformanceText.text = "You're nearly there! Click review to learn how to get there!"
            showSeeMoreButton()
        }  else if (overallSpending < 46 && overallSpending >= 36) {
            audio = R.raw.spending_performance_almost_there
            binding.imgFace.setImageResource(R.drawable.almost_there)
            binding.textStatus.text = "Almost\nThere"
            binding.textStatus.setTextColor(getResources().getColor(R.color.almost_there_yellow))
            binding.tvPerformanceText.text = "Almost there! You need to work on your spending. Click review to learn how!"
            showSeeMoreButton()
        } else if (overallSpending < 36 && overallSpending >= 26) {
            audio = R.raw.spending_performance_getting_there
            binding.imgFace.setImageResource(R.drawable.getting_there)
            binding.textStatus.text = "Getting\nThere"
            binding.textStatus.setTextColor(getResources().getColor(R.color.getting_there_orange))
            binding.tvPerformanceText.text = "Getting there! You need to work on your spending. Click review to learn how!"
            showSeeMoreButton()
        } else if (overallSpending < 26 && overallSpending >= 16) {
            audio = R.raw.spending_performance_not_quite_there_yet
            binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
            binding.textStatus.text = "Not Quite\nThere"
            binding.textStatus.setTextColor(getResources().getColor(R.color.not_quite_there_red))
            binding.tvPerformanceText.text = "Not quite there yet! Don't give up. Click review to learn how to get there!"
            showSeeMoreButton()
        } else if (overallSpending < 15) {
            audio = R.raw.spending_performance_needs_improvement
            binding.imgFace.setImageResource(R.drawable.bad)
            binding.textStatus.text = "Needs\nImprovement"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Your spending performance needs a lot of improvement. Click review to learn how!"
            showSeeMoreButton()
        }
        binding.layoutLoading.visibility= View.GONE
        binding.mainLayout.visibility = View.VISIBLE
        loadAudio(audio)
    }

    private fun loadAudio(audio: Int) {
        //TODO: Change binding and Audio file in mediaPlayer

        binding.btnAudioOverallSpendingPerformance.setOnClickListener {
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
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        spendingAdapter.notifyDataSetChanged()
    }

    private fun showSpendingReviewDialog() {

        var dialogBinding= DialogSpendingReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.getRoot())

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


    private fun loadAudioDialog(dialogBinding: DialogSpendingReviewBinding) {
        /*TODO: Change binding and Audio file in mediaPlayer*/
        val audio = R.raw.dialog_spending_review

        dialogBinding.btnSoundSpending.setOnClickListener {
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

    private suspend fun checkAge() {
        var child = firestore.collection("Users").document(currentUser).get().await().toObject<Users>()
        //compute age
        val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val from = LocalDate.now()
        val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
        val to = LocalDate.parse(date.toString(), dateFormatter)
        var difference = Period.between(to, from)

        age = difference.years
    }

}