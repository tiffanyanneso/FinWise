package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentPerformance

import android.app.Dialog
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityParentSpendingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.DialogParentOverspendingTipsBinding
import ph.edu.dlsu.finwise.databinding.DialogParentPurchasePlanningTipsBinding
import ph.edu.dlsu.finwise.databinding.DialogParentSpendingTipsBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.SpendingFragment
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class ParentSpendingPerformanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParentSpendingPerformanceBinding
    private var firestore = Firebase.firestore

    private var mediaPlayerOverspendingDialog: MediaPlayer? = null
    private var mediaPlayerSpendingPurchasePlanningDialog: MediaPlayer? = null

    var budgetItemsIDArrayList = ArrayList<SpendingFragment.BudgetItemAmount>()
    var goalFilterArrayList = ArrayList<SpendingFragment.GoalFilter>()

    private var overSpending = 0.00F
    private var nBudgetItems = 0.00F

    var overspendingPercentage = 0.00F
    var purchasePlanningPercentage = 0.00F
    var overallSpending = 0.00F

    private lateinit var childID: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentSpendingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle: Bundle = intent.extras!!
        childID = bundle.getString("childID").toString()

        initializeParentNavbar()

        budgetItemsIDArrayList.clear()
        getBudgeting()

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()

        binding.btnTips.setOnClickListener {
            showSpendingReivewDialog()
        }

        binding.btnOverspendingTips.setOnClickListener {
            showOverspendingReivewDialog()
        }

        binding.btnPurchasePlanningTips.setOnClickListener {
            showSpendingPurchasePlanningReviewDialog()
        }
    }

    private fun initializeParentNavbar() {
        val bundleNavBar = Bundle()
        bundleNavBar.putString("childID", childID)
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance, bundleNavBar)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getBudgeting() {
        val budgetingActivityIDArrayList = ArrayList<String>()
        //get only completed budgeting activities because they should complete budgeting first before they are able to spend
        firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
            for (spending in results) {
                var spendingActivity = spending.toObject<FinancialActivities>()
    //                budgetingActivityIDArrayList.add(activity.id)
                println("print number of budgeting activity " + budgetingActivityIDArrayList.size)

                firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", spendingActivity.financialGoalID).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Completed").get().addOnSuccessListener { budgeting ->
                    var budgetingID = budgeting.documents[0].id
                    firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetingID)
                        .whereEqualTo("status", "Active").get().addOnSuccessListener { results ->
                        nBudgetItems += results.size()
                        for (budgetItem in results) {

                            var budgetItemObject = budgetItem.toObject<BudgetItem>()
                            checkOverSpending(budgetItem.id, budgetItemObject.amount!!)
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkOverSpending(budgetItemID:String, budgetItemAmount:Float){
        firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID).whereEqualTo("transactionType", "Expense").get().addOnSuccessListener { spendingTransactions ->
            var amountSpent = 0.00F
            for (expense in spendingTransactions) {
                var expenseObject = expense.toObject<Transactions>()
                amountSpent+= expenseObject.amount!!
            }
            //they spent more than their allocated budget
            if (amountSpent > budgetItemAmount)
                overSpending++

        }.continueWith {
            overspendingPercentage = (overSpending/nBudgetItems)

            firestore.collection("Users").document(childID).get().addOnSuccessListener {
                var child = it.toObject<Users>()
                //compute age
                val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
                val from = LocalDate.now()
                val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
                val to = LocalDate.parse(date.toString(), dateFormatter)
                var difference = Period.between(to, from)

                var age = difference.years
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

            binding.progressBarOverspending.progress = overspendingPercentage.toInt()
            binding.textOverspendingProgress.text  = DecimalFormat("##0.00").format(overspendingPercentage) + "%"

            if (overspendingPercentage < 5) {
                binding.textOverspendingText.text = "Excellent"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.dark_green))
                binding.tvOverspendingText.text = "Your child is excellent at spending within their budget!"
            } else if (overspendingPercentage < 15 && overspendingPercentage >= 5) {
                binding.textOverspendingText.text = "Amazing"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.amazing_green))
                binding.tvOverspendingText.text = "Your child is amazing at spending within their budget!"
            } else if (overspendingPercentage < 25 && overspendingPercentage >= 15) {
                binding.textOverspendingText.text = "Great"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.green))
                binding.tvOverspendingText.text = "Great! Your child often spends within their budget."
            } else if (overspendingPercentage < 35 && overspendingPercentage >= 25) {
                binding.textOverspendingText.text = "Good"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.light_green))
                binding.tvOverspendingText.text = "Good! Remind your child to keep their budget in mind when spending."
            } else if (overspendingPercentage < 45 && overspendingPercentage >= 35) {
                binding.textOverspendingText.text = "Average"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.yellow))
                binding.tvOverspendingText.text = "Nice work! Remind your child to keep their budget in mind when spending."
                showOverspendingeButton()
            } else if (overspendingPercentage < 55 && overspendingPercentage >= 45) {
                binding.textOverspendingText.text = "Nearly There"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
                binding.tvOverspendingText.text = "Your child is nearly there! Click the tips button to learn how to help them there!"
                showOverspendingeButton()
            }  else if (overspendingPercentage < 65 && overspendingPercentage >= 55) {
                binding.textOverspendingText.text = "Almost There"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.almost_there_yellow))
                binding.tvOverspendingText.text = "Your child is almost there! Click the tips button to learn how to help them there!"
                showOverspendingeButton()
            } else if (overspendingPercentage < 75 && overspendingPercentage >= 65) {
                binding.textOverspendingText.text = "Getting There"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.getting_there_orange))
                binding.tvOverspendingText.text = "Your child is getting there! Encourage them to always refer to their budget. Click the tips button to learn how to help them get there!"
                showOverspendingeButton()
            } else if (overspendingPercentage < 85 && overspendingPercentage >= 75) {
                binding.textOverspendingText.text  = "Not Quite\nThere"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.not_quite_there_red))
                binding.tvOverspendingText.text = "Your child is not quite there yet! Click the tips button to learn how to help them get there!"
                showOverspendingeButton()
            } else if (overspendingPercentage > 84) {
                binding.textOverspendingText.text = "Needs\nImprovement"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.red))
                binding.tvOverspendingText.text = "Uh oh! Click the tips button to learn how to help them get there!"
                showOverspendingeButton()
            }
        }
    }

    private fun purchasePlanning() {
        //items planned / all the items they bought * 100
        var nPlanned = 0.00F
        var nTotalPurchased = 0.00F
        firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Spending").get().addOnSuccessListener { allSpendingActivities ->
            for (spendingActivityID in allSpendingActivities) {
                firestore.collection("ShoppingListItems").whereEqualTo("spendingActivityID", spendingActivityID.id).get().addOnSuccessListener { shoppingListItems ->
                    for (shoppingListItem in shoppingListItems) {
                        var shoppingListItemObject = shoppingListItem.toObject<ShoppingListItem>()
                        if (shoppingListItemObject.status == "Purchased")
                            nPlanned++
                    }
                }.continueWith {
                    firestore.collection("Transactions").whereEqualTo("financialActivityID", spendingActivityID.id).whereEqualTo("transactionType", "Expense").get().addOnSuccessListener { expenseTransactions ->
                        nTotalPurchased += expenseTransactions.size().toFloat()
                    }.continueWith {
                        val purchasePlanningPercentage = (nPlanned/nTotalPurchased)*100
                        binding.progressBarPurchasePlanning.progress = purchasePlanningPercentage.toInt()
                        binding.textPurchasePlanning.text  = DecimalFormat("##0.00").format(purchasePlanningPercentage) + "%"

                        if (purchasePlanningPercentage >= 96) {
                            binding.textPurchasePlanningText.text = "Excellent"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.dark_green))
                            binding.tvPurchasePlanningText.text = "Excellent! Your child always plans for their expenses."
                        } else if (purchasePlanningPercentage < 96 && purchasePlanningPercentage >= 86) {
                            binding.textPurchasePlanningText.text = "Amazing"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.amazing_green))
                            binding.tvPurchasePlanningText.text = "Your child is doing an amazing job! They always plan for your expenses."
                        } else if (purchasePlanningPercentage < 86 && purchasePlanningPercentage >= 76) {
                            binding.textPurchasePlanningText.text = "Great"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.green))
                            binding.tvPurchasePlanningText.text = "Your child is doing a great job planning their purchases."
                        } else if (purchasePlanningPercentage < 76 && purchasePlanningPercentage >= 66) {
                            binding.textPurchasePlanningText.text = "Good"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.light_green))
                            binding.tvPurchasePlanningText.text = "Your child is doing a good job! Encourage them to list down the items they wanna buy in their shopping list."
                        } else if (purchasePlanningPercentage < 66 && purchasePlanningPercentage >= 56) {
                            binding.textPurchasePlanningText.text = "Average"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.yellow))
                            binding.tvPurchasePlanningText.text = "Nice Work! To help your child improve, encourage them to plan thei expenses more via the shopping list."
                            showPurchasePlanningButton()
                        } else if (purchasePlanningPercentage < 56 && purchasePlanningPercentage >= 46) {
                            binding.textPurchasePlanningText.text = "Nearly There"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
                            binding.tvPurchasePlanningText.text = "Your child is nearly there! Click the tips button to learn how to help them get there!"
                            showPurchasePlanningButton()
                        }  else if (purchasePlanningPercentage < 46 && purchasePlanningPercentage >= 36) {
                            binding.textPurchasePlanningText.text = "Almost There"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.almost_there_yellow))
                            binding.tvPurchasePlanningText.text = "Almost there! You need to work on your spending. Click review to learn how!"
                            showPurchasePlanningButton()
                        } else if (purchasePlanningPercentage < 36 && purchasePlanningPercentage >= 26) {
                            binding.textPurchasePlanningText.text = "Getting\nThere"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.getting_there_orange))
                            binding.tvPurchasePlanningText.text = "Your child is getting there! Click the tips button to learn how to help them get there!"
                            showPurchasePlanningButton()
                        } else if (purchasePlanningPercentage < 26 && purchasePlanningPercentage >= 16) {
                            binding.textPurchasePlanningText.text  = "Not Quite\nThere"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.not_quite_there_red))
                            binding.tvPurchasePlanningText.text = "Your child is not quite there yet! Click the tips button to learn how to help them get there!"
                            showPurchasePlanningButton()
                        } else if (purchasePlanningPercentage < 15) {
                            binding.textPurchasePlanningText.text = "Needs\nImprovement"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.red))
                            binding.tvPurchasePlanningText.text = "Uh oh! Click the tips button to learn how to help them improve their expense planning!"
                            showPurchasePlanningButton()
                        }

                        overallSpending = (((1-overspendingPercentage)*100) + ((nPlanned/nTotalPurchased)*100)) /2
                        binding.tvPerformancePercentage.text ="${DecimalFormat("##0.0").format(overallSpending)}%"

                        overallPercentage()
                    }
                }
            }
        }
    }

    private fun overallPercentage() {
        if (overallSpending >= 96) {
            binding.imgFace.setImageResource(R.drawable.excellent)
            binding.tvPerformanceStatus.text = "Excellent"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.dark_green))
            binding.tvPerformanceText.text = "Your child excels at spending wisely. Encourage them to keep it up!"
        } else if (overallSpending < 96 && overallSpending >= 86) {
            binding.imgFace.setImageResource(R.drawable.amazing)
            binding.tvPerformanceStatus.text = "Amazing"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.amazing_green))
            binding.tvPerformanceText.text = "Your child is doing an amazing job at spending wisely. Encourage them to keep it up!"
        } else if (overallSpending < 86 && overallSpending >= 76) {
            binding.imgFace.setImageResource(R.drawable.great)
            binding.tvPerformanceStatus.text = "Great"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.green))
            binding.tvPerformanceText.text = "Your child is doing a great job of spending!"
        } else if (overallSpending < 76 && overallSpending >= 66) {
            binding.imgFace.setImageResource(R.drawable.good)
            binding.tvPerformanceStatus.text = "Good"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.light_green))
            binding.tvPerformanceText.text = "Your child is doing a good job of spending wisely!"
        } else if (overallSpending < 66 && overallSpending >= 56) {
            binding.imgFace.setImageResource(R.drawable.average)
            binding.tvPerformanceStatus.text = "Average"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.yellow))
            binding.tvPerformanceText.text = "Your child is doing a nice job of spending wisely! Encourage them to always planning ahead."
            showPerformanceButton()
        } else if (overallSpending < 56 && overallSpending >= 46) {
            binding.imgFace.setImageResource(R.drawable.nearly_there)
            binding.tvPerformanceStatus.text = "Nearly There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            binding.tvPerformanceText.text = "Your child is nearly there! Click the tips button to learn how to help them get there!"
            showPerformanceButton()
        }  else if (overallSpending < 46 && overallSpending >= 36) {
            binding.imgFace.setImageResource(R.drawable.almost_there)
            binding.tvPerformanceStatus.text = "Almost There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.almost_there_yellow))
            binding.tvPerformanceText.text = "Your child is almost there! Click the tips button to learn how to help them get there!"
            showPerformanceButton()
        } else if (overallSpending < 36 && overallSpending >= 26) {
            binding.imgFace.setImageResource(R.drawable.getting_there)
            binding.tvPerformanceStatus.text = "Getting There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.getting_there_orange))
            binding.tvPerformanceText.text = "Your child is getting there! Click the tips button to learn how to help them get there!"
            showPerformanceButton()
        } else if (overallSpending < 26 && overallSpending >= 16) {
            binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
            binding.tvPerformanceStatus.text = "Not Quite\nThere"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.not_quite_there_red))
            binding.tvPerformanceText.text = "Your child is not quite there yet! Click the tips button to learn how to help them get there!"
            showPerformanceButton()
        } else if (overallSpending < 15) {
            binding.imgFace.setImageResource(R.drawable.bad)
            binding.tvPerformanceStatus.text = "Needs\nImprovement"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.red))
            binding.tvPerformanceText.text = "Uh oh! Click the tips button to learn how to help them improve their goal setting!"
            showPerformanceButton()
        }
    }

    private fun showPerformanceButton(){
        binding.btnTips.visibility = View.VISIBLE
    }

    private fun showOverspendingeButton(){
        binding.btnOverspendingTips.visibility = View.VISIBLE
    }

    private fun showPurchasePlanningButton(){
        binding.btnPurchasePlanningTips.visibility = View.VISIBLE
    }
    private fun showSpendingReivewDialog() {

        var dialogBinding= DialogParentSpendingTipsBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1400)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        var audio = R.raw.sample
        dialogBinding.btnSoundParentSpending.setOnClickListener {
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

    private fun pauseMediaPlayer(mediaPlayer: MediaPlayer) {
        mediaPlayer.let {
            if (it.isPlaying) {
                it.pause()
                it.seekTo(0)
            }
        }
    }


    private fun showOverspendingReivewDialog() {

        val dialogBinding= DialogParentOverspendingTipsBinding.inflate(getLayoutInflater())
        val dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1400)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        //TODO: Change audio and dialogBinding
        val audio = R.raw.sample
        dialogBinding.btnSoundParentOverspending.setOnClickListener {
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

        val dialogBinding = DialogParentPurchasePlanningTipsBinding.inflate(getLayoutInflater())
        val dialog = Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1400)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        //TODO: Change audio and dialogBinding
        val audio = R.raw.sample
        dialogBinding.btnSoundParentPurchasePlanning.setOnClickListener {
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

    override fun onDestroy() {
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

}