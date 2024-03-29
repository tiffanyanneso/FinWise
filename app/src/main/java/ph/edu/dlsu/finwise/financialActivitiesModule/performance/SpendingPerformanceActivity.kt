package ph.edu.dlsu.finwise.financialActivitiesModule.performance

import android.app.Dialog
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivitySpendingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.DialogOverspendingReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogPurchasePlanningReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogSpendingReviewBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.SpendingFragment
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.ShoppingListItem
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class SpendingPerformanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpendingPerformanceBinding
    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private var mediaPlayerOverall: MediaPlayer? = null
    private var mediaPlayerOverspending: MediaPlayer? = null
    private var mediaPlayerPurchasePlanning: MediaPlayer? = null
    private var mediaPlayerSpendingDialog: MediaPlayer? = null
    private var mediaPlayerOverspendingDialog: MediaPlayer? = null
    private var mediaPlayerSpendingPurchasePlanningDialog: MediaPlayer? = null


    var budgetItemsIDArrayList = ArrayList<String>()
    var goalFilterArrayList = ArrayList<SpendingFragment.GoalFilter>()

    private var overSpending = 0.00F
    private var nBudgetItems = 0.00F

    var overspendingPercentage = 0.00F
    var purchasePlanningPercentage = 0.00F
    var overallSpending = 0.00F

    private var nSpendingCompleted = 0

    private var age = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpendingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        budgetItemsIDArrayList.clear()
        binding.title.text = "Overall Spending Performance"

        CoroutineScope(Dispatchers.Main).launch {
            checkAge()
            getBudgeting()
            checkOverSpending()
            if (age > 9 ) {
                binding.linearLayoutOverspending.visibility = View.VISIBLE
                binding.linearLayoutPurchasePlanning.visibility = View.VISIBLE
                purchasePlanning()
            }
            else {
                overallSpending = (1-overspendingPercentage)*100
                binding.tvPerformancePercentage.text ="${DecimalFormat("##0.0").format(overallSpending)}%"
                overallPercentage()
                binding.linearLayoutOverspending.visibility = View.VISIBLE
                binding.linearLayoutPurchasePlanning.visibility = View.GONE
            }
        }

        setNavigationBar()
        loadBackButton()

        binding.btnReview.setOnClickListener {
            mediaPlayerOverall?.let { pauseMediaPlayer(it) }
            showSpendingReviewDialog()
        }

        binding.btnOverspendingReview.setOnClickListener {
            mediaPlayerOverall?.let { pauseMediaPlayer(it) }
            showOverspendingReviewDialog()
        }

        binding.btnPurchasePlanningReview.setOnClickListener {
            mediaPlayerOverall?.let { pauseMediaPlayer(it) }
            showSpendingPurchasePlanningReviewDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getBudgeting() {
        var spendingCompleted = firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "Completed").get().await()
        nSpendingCompleted = spendingCompleted.size()
        if (nSpendingCompleted > 0) {
            //get only completed budgeting activities because they should complete budgeting first before they are able to spend
            var budgetingActivity =
                firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser)
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

        var overspendingDisplay = overspendingPercentage*100

        //TODO: Change audio
        var audio = 0
        binding.progressBarOverspending.progress = overspendingDisplay.toInt()
        binding.textOverspendingProgress.text  = DecimalFormat("##0.00").format(overspendingDisplay) + "%"
        binding.btnOverspendingReview.visibility = View.GONE

        if (overspendingDisplay < 5) {
            audio = R.raw.spending_performance_overspending_excellent
            binding.textOverspendingText.text = "Excellent"
            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.dark_green))
            binding.tvOverspendingText.text = "Excellent work! You always spend within your budget. Keep it up"
        } else if (overspendingDisplay < 15 && overspendingDisplay >= 5) {
            audio = R.raw.spending_performance_overspending_amazing
            binding.imgFace.setImageResource(R.drawable.amazing)
            binding.textOverspendingText.text = "Amazing"
            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.amazing_green))
            binding.tvOverspendingText.text = "Amazing job! You always spend within your budget. Keep it up!"
        } else if (overspendingDisplay < 25 && overspendingDisplay >= 15) {
            audio = R.raw.spending_performance_overspending_great
            binding.textOverspendingText.text = "Great"
            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.green))
            binding.tvOverspendingText.text = "Great job! You often spend within your budget. Keep it up!"
        } else if (overspendingDisplay < 35 && overspendingDisplay >= 25) {
            audio = R.raw.spending_performance_overspending_good
            binding.textOverspendingText.text = "Good"
            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.light_green))
            binding.tvOverspendingText.text = "Good job! Remember to keep your budget in mind when spending!"
            showOverspendingButton()
        } else if (overspendingDisplay < 45 && overspendingDisplay >= 35) {
            audio = R.raw.spending_performance_overspending_average
            binding.textOverspendingText.text = "Average"
            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.yellow))
            binding.tvOverspendingText.text = "Nice work! Work on improving your performance by using your budget as a guide and thinking before you buy."
            showOverspendingButton()
        } else if (overspendingDisplay < 55 && overspendingDisplay >= 45) {
            audio = R.raw.spending_performance_overspending_nearly_there
            binding.imgFace.setImageResource(R.drawable.nearly_there)
            binding.textOverspendingText.text = "Nearly There"
            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
            binding.tvOverspendingText.text = "You're nearly there! Work on improving your performance by using your budget as a guide and thinking before you buy."
            showOverspendingButton()
        }  else if (overspendingDisplay < 65 && overspendingDisplay >= 55) {
            audio = R.raw.spending_performance_overspending_almost_there
            binding.imgFace.setImageResource(R.drawable.almost_there)
            binding.textOverspendingText.text = "Almost There"
            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.almost_there_yellow))
            binding.tvOverspendingText.text = "Almost there! Work on improving your performance by using your budget as a guide and thinking before you buy."
            showOverspendingButton()
        } else if (overspendingDisplay < 75 && overspendingDisplay >= 65) {
            audio = R.raw.spending_performance_overspending_getting_there
            binding.imgFace.setImageResource(R.drawable.getting_there)
            binding.textOverspendingText.text = "Getting There"
            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.getting_there_orange))
            binding.tvOverspendingText.text = "Getting there! Work on improving your performance by using your budget as a guide and thinking before you buy."
            showOverspendingButton()
        } else if (overspendingDisplay < 85 && overspendingDisplay >= 75) {
            audio = R.raw.spending_performance_overspending_not_quite_there
            binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
            binding.textOverspendingText.text  = "Not Quite\nThere"
            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.not_quite_there_red))
            binding.tvOverspendingText.text = "Not quite there yet! Don't give up. Work on improving your performance by using your budget as a guide and thinking before you buy."
            showOverspendingButton()
        } else if (overspendingDisplay > 84) {
            audio = R.raw.spending_performance_overspending_needs_improvement
            binding.textOverspendingText.text = "Needs\nImprovement"
            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.red))
            binding.tvOverspendingText.text = "Don't give up! Work on improving your performance by using your budget as a guide and thinking before you buy."
            showOverspendingButton()
        }
        loadOverspendingAudio(audio)
    }


    private suspend fun purchasePlanning() {
        //items planned / all the items they bought * 100
        var nPlanned = 0.00F
        var nTotalPurchased = 0.00F
        var spendingActivities = firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "Completed").get().await()

        for (spendingActivityID in spendingActivities) {
            var shoppingListItems = firestore.collection("ShoppingListItems").whereEqualTo("spendingActivityID", spendingActivityID.id).whereEqualTo("status", "Purchased").get().await()
            nPlanned  += shoppingListItems.size()

            nTotalPurchased += firestore.collection("Transactions").whereEqualTo("financialActivityID", spendingActivityID.id).whereEqualTo("transactionType", "Expense").get().await().size().toFloat()
        }

        val purchasePlanningPercentage = (nPlanned/nTotalPurchased)*100
        binding.progressBarPurchasePlanning.progress = purchasePlanningPercentage.toInt()
        binding.textPurchasePlanning.text  = DecimalFormat("##0.00").format(purchasePlanningPercentage) + "%"
        binding.btnPurchasePlanningReview.visibility = View.GONE
        //TODO: Change audio
        var audio = 0
        if (purchasePlanningPercentage >= 96) {
            audio = R.raw.spending_performance_purchase_planning_excellent
            binding.textPurchasePlanningText.text = "Excellent"
            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.dark_green))
            binding.tvPurchasePlanningText.text = "Excellent job! You always plan for your expenses by putting them in your shopping list. Keep this up!"
        } else if (purchasePlanningPercentage < 96 && purchasePlanningPercentage >= 86) {
            audio = R.raw.spending_performance_purchase_planning_amazing
            binding.textOverspendingText.text = "Amazing"
            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.amazing_green))
            binding.tvPurchasePlanningText.text = "Amazing job! You always plan for your expenses. Keep it up!"
        } else if (purchasePlanningPercentage < 86 && purchasePlanningPercentage >= 76) {
            audio = R.raw.spending_performance_great
            binding.textPurchasePlanningText.text = "Great"
            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.green))
            binding.tvPurchasePlanningText.text = "Great job planning your purchases in your shopping list. Keep this up!"
        } else if (purchasePlanningPercentage < 76 && purchasePlanningPercentage >= 66) {
            audio = R.raw.spending_performance_purchase_planning_good
            binding.textPurchasePlanningText.text = "Good"
            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.light_green))
            binding.tvPurchasePlanningText.text = "Good job! Up your performance by listing down the items you wanna buy in the shopping list."
        } else if (purchasePlanningPercentage < 66 && purchasePlanningPercentage >= 56) {
            audio = R.raw.spending_performance_purchase_planning_average
            binding.textPurchasePlanningText.text = "Average"
            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.yellow))
            binding.tvPurchasePlanningText.text = "Nice Work! To improve, you may want to plan your expenses more via the shopping list."
            showPlanningButton()
        } else if (purchasePlanningPercentage < 56 && purchasePlanningPercentage >= 46) {
            audio = R.raw.spending_performance_purchase_planning_nearly_there
            binding.textPurchasePlanningText.text = "Nearly There"
            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
            binding.tvPurchasePlanningText.text = "You're nearly there! To improve, you may want to plan your expenses more via the shopping list."
            showPlanningButton()
        }  else if (purchasePlanningPercentage < 46 && purchasePlanningPercentage >= 36) {
            audio = R.raw.spending_performance_purchase_planning_almost_there
            binding.textPurchasePlanningText.text = "Almost There"
            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.almost_there_yellow))
            binding.tvPurchasePlanningText.text = "Almost there! You need to work on your spending. Plan your expenses more via the shopping list."
            showPlanningButton()
        } else if (purchasePlanningPercentage < 36 && purchasePlanningPercentage >= 26) {
            audio = R.raw.spending_performance_purchase_planning_getting_there
            binding.textPurchasePlanningText.text = "Getting There"
            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.getting_there_orange))
            binding.tvPurchasePlanningText.text = "Getting there! You need to work on your spending. Plan your expenses more via the shopping list!"
            showPlanningButton()
        } else if (purchasePlanningPercentage < 26 && purchasePlanningPercentage >= 16) {
            audio = R.raw.spending_performance_purchase_planning_not_quite_there_yet
            binding.textPurchasePlanningText.text  = "Not Quite\nThere"
            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.not_quite_there_red))
            binding.tvPurchasePlanningText.text = "Not quite there yet! Don't give up. Plan your expenses more via the shopping list!"
            showPlanningButton()
        } else if (purchasePlanningPercentage < 15) {
            audio = R.raw.spending_performance_needs_improvement
            binding.textPurchasePlanningText.text = "Needs\nImprovement"
            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.red))
            binding.tvPurchasePlanningText.text = "Uh oh! Seems like you haven’t really been planning your expenses by putting them in your shopping list. Try this out next time!"
            showPlanningButton()
        }


        overallSpending = (((1-overspendingPercentage)*100) + purchasePlanningPercentage) /2
        binding.tvPerformancePercentage.text ="${DecimalFormat("##0.0").format(overallSpending)}%"

        loadPurchasePlanningAudio(audio)
        overallPercentage()

    }

    private fun loadOverspendingAudio(audio: Int) {
        /*TODO: Change binding and Audio file in mediaPlayer*/
        binding.btnAudioOverspending.setOnClickListener {
            if (mediaPlayerOverspending == null) {
                mediaPlayerOverspending = MediaPlayer.create(this, audio)
            }

            if (mediaPlayerOverspending?.isPlaying == true) {
                mediaPlayerOverspending?.pause()
                mediaPlayerOverspending?.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerOverspending?.start()
        }
    }


    private fun loadPurchasePlanningAudio(audio: Int) {
        binding.btnAudioPurchasePlanning.setOnClickListener {
            if (mediaPlayerPurchasePlanning == null) {
                mediaPlayerPurchasePlanning = MediaPlayer.create(this, audio)
            }

            if (mediaPlayerPurchasePlanning?.isPlaying == true) {
                mediaPlayerPurchasePlanning?.pause()
                mediaPlayerPurchasePlanning?.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerPurchasePlanning?.start()
        }
    }


    private fun overallPercentage() {
        //TODO: Change audio
        var audio = 0

        if (overallSpending >= 96) {
            audio = R.raw.spending_performance_excellent
            binding.imgFace.setImageResource(R.drawable.excellent)
            binding.tvPerformanceStatus.text = "Excellent"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.dark_green))
            binding.tvPerformanceText.text = "Keep up the excellent work! Spending wisely is your strong point."
        } else if (overallSpending < 96 && overallSpending >= 86) {
            audio = R.raw.spending_performance_amazing
            binding.imgFace.setImageResource(R.drawable.amazing)
            binding.tvPerformanceStatus.text = "Amazing"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.amazing_green))
            binding.tvPerformanceText.text = "Amazing job! You are performing well. Spending wisely is your strong point."
        } else if (overallSpending < 86 && overallSpending >= 76) {
            audio = R.raw.spending_performance_great
            binding.imgFace.setImageResource(R.drawable.great)
            binding.tvPerformanceStatus.text = "Great"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.green))
            binding.tvPerformanceText.text = " Great job! You are performing well. Keep spending wisely!"
        } else if (overallSpending < 76 && overallSpending >= 66) {
            audio = R.raw.spending_performance_good
            binding.imgFace.setImageResource(R.drawable.good)
            binding.tvPerformanceStatus.text = "Good"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.light_green))
            binding.tvPerformanceText.text = "Good job! With a bit more planning, you’ll surely up your performance!"
        } else if (overallSpending < 66 && overallSpending >= 56) {
            audio = R.raw.spending_performance_average
            binding.imgFace.setImageResource(R.drawable.average)
            binding.tvPerformanceStatus.text = "Average"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.yellow))
            binding.tvPerformanceText.text = "Nice work! Work on improving your spending performance by always planning ahead. You’ll get there soon!"
            showPerformanceButton()
        } else if (overallSpending < 56 && overallSpending >= 46) {
            audio = R.raw.spending_performance_nearly_there
            binding.imgFace.setImageResource(R.drawable.nearly_there)
            binding.tvPerformanceStatus.text = "Nearly There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            binding.tvPerformanceText.text = "You're nearly there! Remember to always think before you buy and plan ahead!"
            showPerformanceButton()
        }  else if (overallSpending < 46 && overallSpending >= 36) {
            audio = R.raw.spending_performance_almost_there
            binding.imgFace.setImageResource(R.drawable.almost_there)
            binding.tvPerformanceStatus.text = "Almost There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.almost_there_yellow))
            binding.tvPerformanceText.text = "Almost there! Remember to always think before you buy and plan ahead!"
            showPerformanceButton()
        } else if (overallSpending < 36 && overallSpending >= 26) {
            audio = R.raw.spending_performance_getting_there
            binding.imgFace.setImageResource(R.drawable.getting_there)
            binding.tvPerformanceStatus.text = "Getting There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.getting_there_orange))
            binding.tvPerformanceText.text = "Getting there! You need to work on your spending. Remember to always think before you buy and plan ahead!"
            showPerformanceButton()
        } else if (overallSpending < 26 && overallSpending >= 16) {
            audio = R.raw.spending_performance_not_quite_there_yet
            binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
            binding.tvPerformanceStatus.text = "Not Quite\nThere"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            binding.tvPerformanceText.text = "Not quite there yet! Remember to always think before you buy and plan ahead! Don't give up."
            showPerformanceButton()
        } else if (overallSpending < 15) {
            audio = R.raw.spending_performance_needs_improvement
            binding.imgFace.setImageResource(R.drawable.bad)
            binding.tvPerformanceStatus.text = "Needs\nImprovement"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.red))
            binding.tvPerformanceText.text = "Your spending performance needs a lot of improvement. Remember to always think before you buy and plan ahead, don't give up!"
            showPerformanceButton()
        }  else {
            audio = R.raw.spending_performance_default
        binding.imgFace.setImageResource(R.drawable.good)
        binding.tvPerformanceStatus.text = ""
        binding.tvPerformanceText.text = "Finish spending to see your performance."
        }
        binding.layoutLoading.visibility = View.GONE
        binding.mainLayout.visibility = View.VISIBLE
        loadOverallAudio(audio)
    }

    private fun showPerformanceButton(){
        binding.btnReview.visibility = View.VISIBLE
    }

    private fun showOverspendingButton(){
        binding.btnOverspendingReview.visibility = View.VISIBLE
    }

    private fun showPlanningButton(){
        binding.btnPurchasePlanningReview.visibility = View.VISIBLE
    }
    private fun showSpendingReviewDialog() {

        var dialogBinding= DialogSpendingReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }
        //TODO: Change audio and dialogBinding
        var audio = R.raw.dialog_spending_review
        dialogBinding.btnSoundSpending.setOnClickListener {
            if (mediaPlayerSpendingDialog == null) {
                mediaPlayerSpendingDialog = MediaPlayer.create(this, audio)
            }

            if (mediaPlayerSpendingDialog?.isPlaying == true) {
                mediaPlayerSpendingDialog?.pause()
                mediaPlayerSpendingDialog?.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerSpendingDialog?.start()
        }

        dialog.setOnDismissListener { mediaPlayerSpendingDialog?.let { it1 -> pauseMediaPlayer(it1) } }

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


    private fun showOverspendingReviewDialog() {

        var dialogBinding= DialogOverspendingReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        //TODO: Change audio and dialogBinding
        var audio = R.raw.dialog_overspending_review
        dialogBinding.btnSoundOverspending.setOnClickListener {
            if (mediaPlayerOverspendingDialog == null) {
                mediaPlayerOverspendingDialog = MediaPlayer.create(this, audio)
            }

            if (mediaPlayerOverspendingDialog?.isPlaying == true) {
                mediaPlayerOverspendingDialog?.pause()
                mediaPlayerOverspendingDialog?.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerOverspendingDialog?.start()
        }

        dialog.setOnDismissListener { mediaPlayerOverspendingDialog?.let { it1 -> pauseMediaPlayer(it1) } }

        dialog.show()
    }

    private fun showSpendingPurchasePlanningReviewDialog() {

        var dialogBinding= DialogPurchasePlanningReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        //TODO: Change audio and dialogBinding
        var audio = R.raw.dialog_purchase_plannning
        dialogBinding.btnSoundPurchasePlanning.setOnClickListener {
            if (mediaPlayerSpendingPurchasePlanningDialog == null) {
                mediaPlayerSpendingPurchasePlanningDialog = MediaPlayer.create(this, audio)
            }

            if (mediaPlayerSpendingPurchasePlanningDialog?.isPlaying == true) {
                mediaPlayerSpendingPurchasePlanningDialog?.pause()
                mediaPlayerSpendingPurchasePlanningDialog?.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerSpendingPurchasePlanningDialog?.start()
        }

        dialog.setOnDismissListener { mediaPlayerSpendingPurchasePlanningDialog?.let { it1 -> pauseMediaPlayer(it1) } }

        dialog.show()
    }

    private fun setNavigationBar() {
        var navUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(navUser).get().addOnSuccessListener {

            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent

            if (it.toObject<Users>()!!.userType == "Parent") {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                //sends the ChildID to the parent navbar
                val bundle = intent.extras!!
                val childID = bundle.getString("childID").toString()
                val bundleNavBar = Bundle()
                bundleNavBar.putString("childID", childID)
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)
            } else if (it.toObject<Users>()!!.userType == "Child") {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

            }
        }
    }

    private fun loadOverallAudio(audio: Int) {
        /*TODO: Change binding and Audio file in mediaPlayer*/
        binding.btnAudioOverallSpendingPerformance.setOnClickListener {
            if (mediaPlayerOverall == null) {
                mediaPlayerOverall = MediaPlayer.create(this, audio)
            }

            if (mediaPlayerOverall?.isPlaying == true) {
                mediaPlayerOverall?.pause()
                mediaPlayerOverall?.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerOverall?.start()
        }
    }


    override fun onDestroy() {
        releaseMediaPlayer(mediaPlayerOverall)
        releaseMediaPlayer(mediaPlayerPurchasePlanning)
        releaseMediaPlayer(mediaPlayerOverspending)
        releaseMediaPlayer(mediaPlayerOverspendingDialog)
        releaseMediaPlayer(mediaPlayerSpendingPurchasePlanningDialog)
        releaseMediaPlayer(mediaPlayerSpendingPurchasePlanningDialog)
        super.onDestroy()
    }

    private fun releaseMediaPlayer(mediaPlayer: MediaPlayer?) {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }


    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
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