package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentPerformance

import android.app.Dialog
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.*
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.BudgetingFragment
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.roundToInt

class ParentBudgetingPerformanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParentBudgetingPerformanceBinding
    private var firestore = Firebase.firestore

    //used to get all budgeting activities to count parent involvement
    var budgetingArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<BudgetingFragment.GoalFilter>()

    private var mediaPlayerBudgetingReviewDialog: MediaPlayer? = null
    private var mediaPlayerBudgetingAccuracyDialog: MediaPlayer? = null

    //arraylist that holds all user IDs for createdBy fields in BudgetItem, for parental involvement
    private var createdByUserIDArrayList = ArrayList<String>()

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    //number of times the item was modified by the parent
    private var nParent = 0
    //number of budget items in total
    private var budgetItemCount = 0.00F
    //this is to count the number of budget items that have been already purchased in spending for budget accuracy
    private var purchasedBudgetItemCount  = 0.00F
    //budget variance
    private var totalBudgetAccuracy = 0.00F

    private var budgetingActivityIDArrayList = ArrayList<String>()
    private var budgetItemIDArrayList = ArrayList<String>()

    private var parentalInvolvementPercentage = 0.00F
    private var budgetAmountAccuracyPercentage = 0.00F
    private var budgetItemAccuracyPercentage = 0.00F

    var nUpdates = 0.00F
    var nItems = 0.00F

    private lateinit var childID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentBudgetingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        var bundle: Bundle = intent.extras!!
        childID = bundle.getString("childID").toString()

        initializeParentNavbar()

        getOverallBudgeting()


        binding.btnTips.setOnClickListener {
            showBudgetingReivewDialog()
        }

        binding.btnBudgetAccuracyTips.setOnClickListener{
            showBudgetAccuracyAmountReivewDialog()
        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initializeParentNavbar() {
        val bundleNavBar = Bundle()
        bundleNavBar.putString("childID", childID)
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance, bundleNavBar)
    }


    private fun getOverallBudgeting() {
        firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
            for (activity in results) {
                firestore.collection("BudgetItems").whereEqualTo("financialActivityID", activity.id).whereEqualTo("status", "Active").get().addOnSuccessListener { budgetItems ->
                    for (budgetItem in budgetItems) {
                        budgetItemCount++
                        var budgetItemObject = budgetItem.toObject<BudgetItem>()
                        if (budgetItemObject.status == "Edited")
                            nUpdates++

                        //parental involvement
                        firestore.collection("Users").document(budgetItemObject.createdBy.toString()).get().addOnSuccessListener { user ->
                            //parent is the one who added the budget item
                            if (user.toObject<Users>()!!.userType == "Parent")
                                nParent++
                        }.continueWith {
                            getBudgetAccuracy(activity.id, budgetItem.id, budgetItemObject)
                        }
                    }
                }
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
                            spent += transaction.toObject<Transactions>()!!.amount!!
                        println("print budget accuracy " +  (100 - (abs(budgetItemObject.amount!! - spent) / budgetItemObject.amount!!) * 100))
                        if (budgetItemObject.amount!! !=0.00F)
                            totalBudgetAccuracy += (100 - (abs(budgetItemObject.amount!! - spent) / budgetItemObject.amount!!) * 100)
                    }.continueWith {
                        setBudgetAccuracy()
                        setParentalInvolvement()
                        setOverall()
                    }
                } else {
                    setBudgetAccuracy()
                    setParentalInvolvement()
                    setOverall()
                }
            }
        }
    }

    private fun setParentalInvolvement() {
        var parentalPercentage = nParent.toFloat()/ budgetItemCount *100
        binding.textViewProgressParentalInvolvement.text = DecimalFormat("##0.##").format(parentalPercentage)+ "%"
        binding.progressBarParentalInvolvement.progress = parentalPercentage.toInt()
        if (parentalPercentage < 5) {
            binding.textViewPerformanceTextParentalInvolvement.text = "Excellent"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(
                R.color.dark_green
            ))
            binding.tvParentalInvolvementText.text = "Keep up the excellent work! Your child is able to budget independently."
        } else if (parentalPercentage < 15 && parentalPercentage >= 5) {
            binding.textViewPerformanceTextParentalInvolvement.text = "Amazing"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(
                R.color.green
            ))
            binding.tvParentalInvolvementText.text = "Amazing job! Your child is able to budget independently."
        } else if (parentalPercentage < 25 && parentalPercentage >= 15) {
            binding.textViewPerformanceTextParentalInvolvement.text = "Great"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(
                R.color.green
            ))
            binding.tvParentalInvolvementText.text = "Good! Your child is able to budget independently."
        } else if (parentalPercentage < 35 && parentalPercentage >= 25) {
            binding.textViewPerformanceTextParentalInvolvement.text = "Good"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(
                R.color.light_green
            ))
            binding.tvParentalInvolvementText.text = "Good job! Your child only occasionally asks for help budgeting."
        } else if (parentalPercentage < 45 && parentalPercentage >= 35) {
            binding.textViewPerformanceTextParentalInvolvement.text = "Average"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(
                R.color.yellow
            ))
            binding.tvParentalInvolvementText.text = "Nice work! Encourage your child to try budgeting more independently."
        } else if (parentalPercentage < 55 && parentalPercentage >= 45) {
            binding.textViewPerformanceTextParentalInvolvement.text = "Nearly There"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(
                R.color.red
            ))
            binding.tvParentalInvolvementText.text = "You're nearly there! You may give your child guidance, but try to encourage them to budget more independently."
        } else if (parentalPercentage < 65 && parentalPercentage >= 55) {
            binding.textViewPerformanceTextParentalInvolvement.text = "Almost There"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(
                R.color.red
            ))
            binding.tvParentalInvolvementText.text = "Almost there! You may give your child guidance, but try to encourage them to budget more independently."
        } else if (parentalPercentage < 75 && parentalPercentage >= 65) {
            binding.textViewPerformanceTextParentalInvolvement.text = "Getting There"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(
                R.color.red
            ))
            binding.tvParentalInvolvementText.text = "Getting there! You may give your child guidance, but try to encourage them to budget more independently."
        } else if (parentalPercentage < 85 && parentalPercentage >= 75) {
            binding.textViewPerformanceTextParentalInvolvement.text = "Not Quite\nThere"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(
                R.color.red
            ))
            binding.tvParentalInvolvementText.text = "Not quite there yet! You may give your child guidance, but try to encourage them to budget more independently."
        } else if (parentalPercentage > 84) {
            binding.textViewPerformanceTextParentalInvolvement.text = "Needs\nImprovement"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(
                R.color.red
            ))
            binding.tvParentalInvolvementText.text = "Uh Oh! Guide your child so that they can eventually budget independently!"
        }
    }

    private fun setBudgetAccuracy() {
        var budgetAccuracy = (totalBudgetAccuracy/ purchasedBudgetItemCount).roundToInt()
        binding.textViewBudgetAccuracyProgress.text =  DecimalFormat("##0.##").format(totalBudgetAccuracy/ purchasedBudgetItemCount) + "%"
        binding.progressBarBudgetAccuracy.progress = budgetAccuracy

        binding.btnBudgetAccuracyTips.visibility = View.GONE

        if (budgetAccuracy >= 96) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Excellent"
            binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.dark_green))
            binding.tvBudgetAccuracyText.text = "Your child is doing excellent! Their budget is often accurate."
        } else if (budgetAccuracy in 86..95) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Amazing"
            binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.amazing_green))
            binding.tvBudgetAccuracyText.text = "Your child is doing an amazing job! They create accurate budgets."
        } else if (budgetAccuracy in 76..85) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Great"
            binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.green))
            binding.tvBudgetAccuracyText.text = "Your child is performing well. They create accurate budgets."
        } else if (budgetAccuracy in 66..75) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Good"
            binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.light_green))
            binding.tvBudgetAccuracyText.text = "Your child is doing a good job! Encourage them to double check their budgets."
        } else if (budgetAccuracy in 56..65) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Average"
            binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.yellow))
            binding.tvBudgetAccuracyText.text = "Your child is doing well! Encourage them to doublecheck their budget items and amoounts."
            showAccuracyButton()
        } else if (budgetAccuracy in 46..55) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Nearly There"
            binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
            binding.tvBudgetAccuracyText.text = "You child is nearly there! Click on the tips button to learn how to help them get there!"
            showAccuracyButton()
        }  else if (budgetAccuracy in 36..45) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Almost There"
            binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.almost_there_yellow))
            binding.tvBudgetAccuracyText.text = "Your child is almost there! They need to work on their budget accuracy. Click tips to learn how to help!"
            showAccuracyButton()
        } else if (budgetAccuracy in 26..35) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Getting There"
            binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.getting_there_orange))
            binding.tvBudgetAccuracyText.text = "Your child is getting there! Click tips to learn how to help!"
            showAccuracyButton()
        } else if (budgetAccuracy in 16..25) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Not Quite\nThere"
            binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.not_quite_there_red))
            binding.tvBudgetAccuracyText.text = "Your child is not quite there yet!  Click tips to learn how to help!"
            showAccuracyButton()
        } else if (budgetAccuracy < 15) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Needs\nImprovement"
            binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.red))
            binding.tvBudgetAccuracyText.text = "Your child's budget accuracy needs a lot of improvement. Click tips to learn how to help!"
            showAccuracyButton()
        }
    }

    private fun setOverall() {
        var overall = ((totalBudgetAccuracy/purchasedBudgetItemCount) + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2

        binding.tvPerformancePercentage.text = "${DecimalFormat("##0.0").format(overall)}%"
        binding.btnTips.visibility = View.GONE

        if (overall >= 96) {
            binding.imgFace.setImageResource(R.drawable.excellent)
            binding.textStatus.text = "Excellent"
            binding.textStatus.setTextColor(getResources().getColor(R.color.dark_green))
            binding.tvPerformanceText.text = "Your child excels at budgeting. Encourage them to keep up the excellent work."
        } else if (overall < 96 && overall >= 86) {
            binding.imgFace.setImageResource(R.drawable.amazing)
            binding.textStatus.text = "Amazing"
            binding.textStatus.setTextColor(getResources().getColor(R.color.amazing_green))
            binding.tvPerformanceText.text = "Your child is amazing at budgeting! Encourage them to keep up the excellent work."
        } else if (overall < 86 && overall >= 76) {
            binding.imgFace.setImageResource(R.drawable.great)
            binding.textStatus.text = "Great"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "Your child is doing a great job of budgeting!"
        } else if (overall < 76 && overall >= 66) {
            binding.imgFace.setImageResource(R.drawable.good)
            binding.textStatus.text = "Good"
            binding.textStatus.setTextColor(getResources().getColor(R.color.light_green))
            binding.tvPerformanceText.text = "Your child is doing a good job of budgeting! Encourage them to review their budget."
        } else if (overall < 66 && overall >= 56) {
            binding.imgFace.setImageResource(R.drawable.average)
            binding.textStatus.text = "Average"
            binding.textStatus.setTextColor(getResources().getColor(R.color.yellow))
            binding.tvPerformanceText.text = "Your child is doing a nice job of budgeting! Encourage them to always doublecheck their budget."
            showPerformanceButton()
        } else if (overall < 56 && overall >= 46) {
            binding.imgFace.setImageResource(R.drawable.nearly_there)
            binding.textStatus.text = "Nearly There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
            binding.tvPerformanceText.text = "Your child is nearly there! Click the tips button to learn how to get there!"
            showPerformanceButton()
        }  else if (overall < 46 && overall >= 36) {
            binding.imgFace.setImageResource(R.drawable.almost_there)
            binding.textStatus.text = "Almost There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.almost_there_yellow))
            binding.tvPerformanceText.text = "Your child is almost there! Click the tips button to learn how to help them get there!"
            showPerformanceButton()
        } else if (overall < 36 && overall >= 26) {
            binding.imgFace.setImageResource(R.drawable.getting_there)
            binding.textStatus.text = "Getting There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.getting_there_orange))
            binding.tvPerformanceText.text = "Your child is getting there! Click the tips button to learn how to help them get there!"
            showPerformanceButton()
        } else if (overall < 26 && overall >= 16) {
            binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
            binding.textStatus.text = "Not Quite\nThere"
            binding.textStatus.setTextColor(getResources().getColor(R.color.not_quite_there_red))
            binding.tvPerformanceText.text = "Your child is not quite there yet! Click the tips button to learn how to help them get there!"
            showPerformanceButton()
        } else if (overall < 15) {
            binding.imgFace.setImageResource(R.drawable.bad)
            binding.textStatus.text = "Needs\nImprovement"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Uh oh! Click the tips button to learn how to help them get there!"
            showPerformanceButton()
        }
    }

    private fun showPerformanceButton(){
        binding.btnTips.visibility = View.VISIBLE
    }

    private fun showAccuracyButton(){
        binding.btnBudgetAccuracyTips.visibility = View.VISIBLE
    }

    private fun showBudgetingReivewDialog() {

        var dialogBinding= DialogParentBudgetingTipsBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1400)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        //TODO: Change audio and dialogBinding
        val audio = R.raw.sample
        dialogBinding.btnSoundParentBudgeting.setOnClickListener {
            if (mediaPlayerBudgetingReviewDialog == null) {
                mediaPlayerBudgetingReviewDialog = MediaPlayer.create(this, audio)
            }

            if (mediaPlayerBudgetingReviewDialog?.isPlaying == true) {
                mediaPlayerBudgetingReviewDialog?.pause()
                mediaPlayerBudgetingReviewDialog?.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerBudgetingReviewDialog?.start()
        }
        dialog.setOnDismissListener { mediaPlayerBudgetingReviewDialog?.let { it1 -> pauseMediaPlayer(it1) } }

        dialog.show()
    }



    private fun showBudgetAccuracyAmountReivewDialog() {

        var dialogBinding= DialogParentBudgetAccuracyAmountTipsBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1000)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        //TODO: Change audio and dialogBinding
        var audio = R.raw.sample
        dialogBinding.btnSoundBudgetAccuracyParent.setOnClickListener {
            if (mediaPlayerBudgetingAccuracyDialog == null) {
                mediaPlayerBudgetingAccuracyDialog = MediaPlayer.create(this, audio)
            }

            if (mediaPlayerBudgetingAccuracyDialog?.isPlaying == true) {
                mediaPlayerBudgetingAccuracyDialog?.pause()
                mediaPlayerBudgetingAccuracyDialog?.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerBudgetingAccuracyDialog?.start()
        }

        dialog.setOnDismissListener { mediaPlayerBudgetingAccuracyDialog?.let { it1 -> pauseMediaPlayer(it1) } }

        dialog.show()
    }

    override fun onDestroy() {
        releaseMediaPlayer(mediaPlayerBudgetingReviewDialog)
        releaseMediaPlayer(mediaPlayerBudgetingAccuracyDialog)
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

    private fun pauseMediaPlayer(mediaPlayer: MediaPlayer) {
        mediaPlayer.let {
            if (it.isPlaying) {
                it.pause()
                it.seekTo(0)
            }
        }
    }

}