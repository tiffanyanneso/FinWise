package ph.edu.dlsu.finwise.financialActivitiesModule.performance

import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityBudgetingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.DialogBudgetAccuracyAmountReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogBudgetingReviewBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.BudgetingFragment
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.roundToInt

class BudgetingPerformanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetingPerformanceBinding
    private var firestore = Firebase.firestore
    private var mediaPlayerOverall: MediaPlayer? = null
    private var mediaPlayerParentalInvolvement: MediaPlayer? = null
    private var mediaPlayerBudgetingAccuracy: MediaPlayer? = null
    private var mediaPlayerBudgetingReviewDialog: MediaPlayer? = null
    private var mediaPlayerBudgetingAccuracyDialog: MediaPlayer? = null

    //used to get all budgeting activities to count parent involvement
    var budgetingArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<BudgetingFragment.GoalFilter>()

    //arraylist that holds all user IDs for createdBy fields in BudgetItem, for parental involvement
    private var createdByUserIDArrayList = ArrayList<String>()

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    //number of times the item was modified by the parent
    private var nParent = 0

    //number of budget items in total
    private var budgetItemCount = 0.00F

    //this is to count the number of budget items that have been already purchased in spending for budget accuracy
    private var purchasedBudgetItemCount = 0.00F

    //budget variance
    private var totalBudgetAccuracy = 0.00F

    private var budgetingActivityIDArrayList = ArrayList<String>()
    private var budgetItemIDArrayList = ArrayList<String>()

    private var parentalInvolvementPercentage = 0.00F
    private var budgetAmountAccuracyPercentage = 0.00F
    private var budgetItemAccuracyPercentage = 0.00F

    var nUpdates = 0.00F
    var nItems = 0.00F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getOverallBudgeting()
        setNavigationBar()
        loadBackButton()

        binding.btnReview.setOnClickListener {
            mediaPlayerOverall?.let { pauseMediaPlayer(it) }
            showBudgetingReivewDialog()
        }

        binding.btnBudgetAccuracyReview.setOnClickListener {
            mediaPlayerOverall?.let { pauseMediaPlayer(it) }
            showBudgetAccuracyAmountReivewDialog()
        }
    }

    private fun getOverallBudgeting() {
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
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

    private fun getBudgetAccuracy(budgetingActivityID: String, budgetItemID: String, budgetItemObject: BudgetItem) {
        firestore.collection("FinancialActivities").document(budgetingActivityID).get().addOnSuccessListener {
            firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", it.toObject<FinancialActivities>()!!.financialGoalID!!).whereEqualTo("financialActivityName", "Spending").get().addOnSuccessListener { spending ->
                val spendingActivity = spending.documents[0].toObject<FinancialActivities>()
                if (spendingActivity?.status == "Completed") {
                    //budget accuracy
                    purchasedBudgetItemCount++
                    firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID).get().addOnSuccessListener { transactions ->
                        var spent = 0.00F
                        for (transaction in transactions)
                            spent += transaction.toObject<Transactions>()!!.amount!!
                        println("print budget accuracy " + (100 - (abs(budgetItemObject.amount!! - spent) / budgetItemObject.amount!!) * 100))
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
        //TODO: Change audio
        var audio = 0
        var parentalPercentage = nParent.toFloat()/ budgetItemCount *100
        binding.textViewProgressParentalInvolvement.text = DecimalFormat("##0.00").format(parentalPercentage)+ "%"
        binding.progressBarParentalInvolvement.progress = parentalPercentage.toInt()
        if (parentalPercentage < 5) {
            audio = R.raw.sample
            binding.textViewPerformanceTextParentalInvolvement.text = "Excellent"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.dark_green))
            binding.tvParentalInvolvementText.text = "Keep up the excellent work! You are able to budget independently."
        } else if (parentalPercentage < 15 && parentalPercentage >= 5) {
            audio = R.raw.sample
            binding.textViewPerformanceTextParentalInvolvement.text = "Amazing"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.green))
            binding.tvParentalInvolvementText.text = "Amazing job! Keep up budgeting independently."
        } else if (parentalPercentage < 25 && parentalPercentage >= 15) {
            audio = R.raw.sample
            binding.textViewPerformanceTextParentalInvolvement.text = "Great"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.green))
            binding.tvParentalInvolvementText.text = "You are performing well! Keep up budgeting independently."
        } else if (parentalPercentage < 35 && parentalPercentage >= 25) {
            audio = R.raw.sample
            binding.textViewPerformanceTextParentalInvolvement.text = "Good"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.light_green))
            binding.tvParentalInvolvementText.text = "Good job! Try to create your budget on your own before asking mom or dad to help!"
        } else if (parentalPercentage < 45 && parentalPercentage >= 35) {
            audio = R.raw.sample
            binding.textViewPerformanceTextParentalInvolvement.text = "Average"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.yellow))
            binding.tvParentalInvolvementText.text = "Nice work! Try to create your budget on your own before asking mom or dad to help!"
        } else if (parentalPercentage < 55 && parentalPercentage >= 45) {
            audio = R.raw.sample
            binding.textViewPerformanceTextParentalInvolvement.text = "Nearly There"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.red))
            binding.tvParentalInvolvementText.text = "You're nearly there! Try to create your budget on your own before asking mom or dad to help!"
        } else if (parentalPercentage < 65 && parentalPercentage >= 55) {
            audio = R.raw.sample
            binding.textViewPerformanceTextParentalInvolvement.text = "Almost There"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.red))
            binding.tvParentalInvolvementText.text = "Almost there! Try to create your budget on your own before asking mom or dad to help!"
        } else if (parentalPercentage < 75 && parentalPercentage >= 65) {
            audio = R.raw.sample
            binding.textViewPerformanceTextParentalInvolvement.text = "Getting There"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.red))
            binding.tvParentalInvolvementText.text = "Getting there! Try to create your budget on your own before asking mom or dad to help!"
        } else if (parentalPercentage < 85 && parentalPercentage >= 75) {
            audio = R.raw.sample
            binding.textViewPerformanceTextParentalInvolvement.text = "Not Quite\nThere"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.red))
            binding.tvParentalInvolvementText.text = "Not quite there yet! Try to create your budget on your own before asking mom or dad to help!."
        } else if (parentalPercentage > 84) {
            audio = R.raw.sample
            binding.textViewPerformanceTextParentalInvolvement.text = "Needs\nImprovement"
            binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.red))
            binding.tvParentalInvolvementText.text = "Don't Worry! Try to create your budget on your own before asking mom or dad to help!"
        }
        loadParentalInvolvementAudio(audio)
    }

    private fun loadParentalInvolvementAudio(audio: Int) {
        /*TODO: Change binding and Audio file in mediaPlayer*/
        binding.tvParentalInvolvementText.setOnClickListener {
            if (mediaPlayerParentalInvolvement == null) {
                mediaPlayerParentalInvolvement = MediaPlayer.create(this, audio)
            }

            if (mediaPlayerParentalInvolvement?.isPlaying == true) {
                mediaPlayerParentalInvolvement?.pause()
                mediaPlayerParentalInvolvement?.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerParentalInvolvement?.start()
        }
    }

    private fun loadOverallAudio(audio: Int) {
        /*TODO: Change binding and Audio file in mediaPlayer*/
        binding.imgFace.setOnClickListener {
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
        releaseMediaPlayer(mediaPlayerParentalInvolvement)
        releaseMediaPlayer(mediaPlayerBudgetingAccuracy)
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


    private fun setBudgetAccuracy() {
        //TODO: Change audio
        var audio = 0
        if (purchasedBudgetItemCount!=0.0F) {
            var budgetAccuracy = (totalBudgetAccuracy/ purchasedBudgetItemCount).roundToInt()
            binding.textViewBudgetAccuracyProgress.text = DecimalFormat("##0.00").format(totalBudgetAccuracy/ purchasedBudgetItemCount) + "%"
            binding.progressBarBudgetAccuracy.progress = budgetAccuracy

            binding.btnBudgetAccuracyReview.visibility = View.GONE

            if (budgetAccuracy >= 96) {
                audio = R.raw.sample
                binding.textViewBudgetAccuracyPerformanceText.text = "Excellent"
                binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.dark_green))
                binding.tvBudgetAccuracyText.text =
                "Keep up the excellent work! Your budget is often accurate."
            } else if (budgetAccuracy in 86..95) {
                audio = R.raw.sample
                binding.textViewBudgetAccuracyPerformanceText.text = "Amazing"
                binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.amazing_green))
                binding.tvBudgetAccuracyText.text =
                    "Amazing job! You are performing well. You are amazing at creating accurate budgets."
            } else if (budgetAccuracy in 76..85) {
                audio = R.raw.sample
                binding.textViewBudgetAccuracyPerformanceText.text = "Great"
                binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.green))
                binding.tvBudgetAccuracyText.text =
                    "You are performing well. Keep making those accurate budgets!"
            } else if (budgetAccuracy in 66..75) {
                audio = R.raw.sample
                binding.textViewBudgetAccuracyPerformanceText.text = "Good"
                binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.light_green))
                binding.tvBudgetAccuracyText.text = "Good job! With a bit more attention to detail, you’ll surely up your performance!"
            } else if (budgetAccuracy in 56..65) {
                audio = R.raw.sample
                binding.textViewBudgetAccuracyPerformanceText.text = "Average"
                binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.yellow))
                binding.tvBudgetAccuracyText.text =
                    "Nice work! Work on improving your budget by always doublechecking it. You’ll get there soon!"
                showAccuracyButton()
            } else if (budgetAccuracy in 46..55) {
                audio = R.raw.sample
                binding.textViewBudgetAccuracyPerformanceText.text = "Nearly There"
                binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
                binding.tvBudgetAccuracyText.text =
                    "You're nearly there! Always review your budget to up your accuracy!"
                showAccuracyButton()
            } else if (budgetAccuracy in 36..45) {
                audio = R.raw.sample
                binding.textViewBudgetAccuracyPerformanceText.text = "Almost There"
                binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.almost_there_yellow))
                binding.tvBudgetAccuracyText.text =
                    "Almost there! Always review your budget to up your accuracy!"
                showAccuracyButton()
            } else if (budgetAccuracy in 26..35) {
                audio = R.raw.sample
                binding.textViewBudgetAccuracyPerformanceText.text = "Getting There"
                binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.getting_there_orange))
                binding.tvBudgetAccuracyText.text =
                    "Getting there! Always review your budget to up your accuracy!"
                showAccuracyButton()
            } else if (budgetAccuracy in 16..25) {
                audio = R.raw.sample
                binding.textViewBudgetAccuracyPerformanceText.text = "Not Quite\nThere"
                binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.not_quite_there_red))
                binding.tvBudgetAccuracyText.text =
                    "Not quite there yet! Always review your budget to up your accuracy!"
                showAccuracyButton()
            } else if (budgetAccuracy < 15) {
                audio = R.raw.sample
                binding.textViewBudgetAccuracyPerformanceText.text = "Needs\nImprovement"
                binding.textViewBudgetAccuracyPerformanceText.setTextColor(getResources().getColor(R.color.red))
                binding.tvBudgetAccuracyText.text =
                    "Your budget accuracy needs a lot of improvement. Always review your budget to up your accuracy!"
                showAccuracyButton()
            }
        } else {
            audio = R.raw.sample
            binding.tvBudgetAccuracyText.text =
                "Complete spending activities to know your budget accuracy"
        }
        loadBudgetingAccuracyAudio(audio)
    }

    private fun loadBudgetingAccuracyAudio(audio: Int) {
        /*TODO: Change binding and Audio file in mediaPlayer*/
        binding.tvBudgetAccuracyText.setOnClickListener {
            if (mediaPlayerBudgetingAccuracy == null) {
                mediaPlayerBudgetingAccuracy = MediaPlayer.create(this, audio)
            }

            if (mediaPlayerBudgetingAccuracy?.isPlaying == true) {
                mediaPlayerBudgetingAccuracy?.pause()
                mediaPlayerBudgetingAccuracy?.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerBudgetingAccuracy?.start()
        }
    }


    private fun setOverall() {
        var overall = 0.00F
        if (purchasedBudgetItemCount != 0.00F)
            overall = (  (totalBudgetAccuracy/purchasedBudgetItemCount) + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2
        else
            overall = (1 - (nParent.toFloat()/budgetItemCount)) * 100

        binding.tvPerformancePercentage.text = "${DecimalFormat("##0.0").format(overall)}%"

        binding.btnReview.visibility = View.GONE

        //TODO: Change audio
        var audio = 0
        if (overall >= 96) {
            audio = R.raw.sample
            binding.imgFace.setImageResource(R.drawable.excellent)
            binding.textStatus.text = "Excellent"
            binding.textStatus.setTextColor(getResources().getColor(R.color.dark_green))
            binding.tvPerformanceText.text =
                "Keep up the excellent work! Budgeting is your strong point. Keep making those budgets!"
        } else if (overall < 96 && overall >= 86) {
            audio = R.raw.sample
            binding.imgFace.setImageResource(R.drawable.amazing)
            binding.textStatus.text = "Amazing"
            binding.textStatus.setTextColor(getResources().getColor(R.color.amazing_green))
            binding.tvPerformanceText.text = "Amazing job! You are performing well. Budgeting is your strong point. Keep making those budgets!"
        } else if (overall < 86 && overall >= 76) {
            audio = R.raw.sample
            binding.imgFace.setImageResource(R.drawable.great)
            binding.textStatus.text = "Great"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "You are performing well. Keep making those budgets!"
        } else if (overall < 76 && overall >= 66) {
            audio = R.raw.sample
            binding.imgFace.setImageResource(R.drawable.good)
            binding.textStatus.text = "Good"
            binding.textStatus.setTextColor(getResources().getColor(R.color.light_green))
            binding.tvPerformanceText.text = "You are performing well. Keep making those budgets!"
        } else if (overall < 66 && overall >= 56) {
            audio = R.raw.sample
            binding.imgFace.setImageResource(R.drawable.average)
            binding.textStatus.text = "Average"
            binding.textStatus.setTextColor(getResources().getColor(R.color.yellow))
            binding.tvPerformanceText.text =
                "Nice work! Work on improving your budget by always doublechecking. You’ll get there soon!"
            showPerformanceButton()
        } else if (overall < 56 && overall >= 46) {
            audio = R.raw.sample
            binding.imgFace.setImageResource(R.drawable.nearly_there)
            binding.textStatus.text = "Nearly There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
            binding.tvPerformanceText.text =
                "You're nearly there! Work on improving your budget by always doublechecking!"
            showPerformanceButton()
        } else if (overall < 46 && overall >= 36) {
            audio = R.raw.sample
            binding.imgFace.setImageResource(R.drawable.almost_there)
            binding.textStatus.text = "Almost There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.almost_there_yellow))
            binding.tvPerformanceText.text =
                "Almost there! Work on improving your budget by always doublechecking!"
            showPerformanceButton()
        } else if (overall < 36 && overall >= 26) {
            audio = R.raw.sample
            binding.imgFace.setImageResource(R.drawable.getting_there)
            binding.textStatus.text = "Getting There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.getting_there_orange))
            binding.tvPerformanceText.text =
                "Getting there!Work on improving your budget by always doublechecking!"
            showPerformanceButton()
        } else if (overall < 26 && overall >= 16) {
            audio = R.raw.sample
            binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
            binding.textStatus.text = "Not Quite\nThere"
            binding.textStatus.setTextColor(getResources().getColor(R.color.not_quite_there_red))
            binding.tvPerformanceText.text =
                "Not quite there yet! Work on improving your budget by always doublechecking!"
            showPerformanceButton()
        } else if (overall < 15) {
            audio = R.raw.sample
            binding.imgFace.setImageResource(R.drawable.bad)
            binding.textStatus.text = "Needs\nImprovement"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text =
                "Don't give up! Work on improving your budget by always doublechecking"
            showPerformanceButton()
        }
        loadOverallAudio(audio)
    }

    private fun showPerformanceButton(){
        binding.btnReview.visibility = View.VISIBLE
    }

    private fun showAccuracyButton(){
        binding.btnBudgetAccuracyReview.visibility = View.VISIBLE
    }

    private fun showBudgetingReivewDialog() {

        var dialogBinding = DialogBudgetingReviewBinding.inflate(getLayoutInflater())
        var dialog = Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1600)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        //TODO: Change audio and dialogBinding
        val audio = R.raw.sample
        dialogBinding.btnSoundBudgetReview.setOnClickListener {
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

    private fun pauseMediaPlayer(mediaPlayer: MediaPlayer) {
        mediaPlayer.let {
            if (it.isPlaying) {
                it.pause()
                it.seekTo(0)
            }
        }
    }


    private fun showBudgetAccuracyAmountReivewDialog() {

        var dialogBinding = DialogBudgetAccuracyAmountReviewBinding.inflate(getLayoutInflater())
        var dialog = Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1300)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        //TODO: Change audio and dialogBinding
        var audio = R.raw.sample
        dialogBinding.btnSoundBudgetAccuracy.setOnClickListener {
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

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}