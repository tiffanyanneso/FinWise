package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentPerformance

import android.app.Dialog
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityParentSavingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.DialogParentSavingCategoryTipsBinding
import ph.edu.dlsu.finwise.databinding.DialogParentSavingDurationTipsBinding
import ph.edu.dlsu.finwise.databinding.DialogParentSavingTipsBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.performance.SavingPerformanceActivity
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentGoalFragment.ParentSavingFragment
import java.text.DecimalFormat

class ParentSavingPerformanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentSavingPerformanceBinding
    private var firestore = Firebase.firestore

    private var ongoingGoals = 0
    //includes achieved goals in count
    private var totalGoals = 0

    private var mediaPlayerGoalDialog: MediaPlayer? = null
    private var mediaPlayerDurationDialog: MediaPlayer? = null
    private var mediaPlayerCategoryDialog: MediaPlayer? = null

    var goalIDArrayList = ArrayList<String>()
    var savingsArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<ParentSavingFragment.GoalFilter>()

    private var setOwnGoals = false
    private lateinit var childID: String

    //var for duration for pie chart
    var nShort = 0
    var nMedium = 0
    var nLong = 0

    //vars for activity types for pie chart
    var nBuyingItem = 0
    var nEvent = 0
    var nEmergency = 0
    var nCharity = 0
    var nSituational =0
    var nEarning = 0

    private var specificDuration = "Short"
    private var specificCategory = "Buying Items"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentSavingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle: Bundle = intent.extras!!
        childID = bundle?.getString("childID").toString()

        initializeParentNavbar()

        ongoingGoals = 0
        totalGoals = 0
        goalIDArrayList.clear()
        savingsArrayList.clear()

        supportActionBar?.hide()

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.btnTips.setOnClickListener{
            showGoalDialog()
        }

        binding.btnReviewDuration.setOnClickListener{
            showDurationDialog()
        }

        binding.btnReviewConcept.setOnClickListener{
            showCategoryDialog()
        }

//        checkAge()
        getGoals()
        computeOverallScore()

    }

    private fun initializeParentNavbar() {
        val bundleNavBar = Bundle()
        bundleNavBar.putString("childID", childID)
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance, bundleNavBar)
    }


    private fun getGoals() {
        firestore.collection("FinancialGoals").whereEqualTo("childID", childID).whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
            totalGoals = results.size()
            for (goal in results) {
                var goalObject = goal.toObject<FinancialGoals>()

                when (goalObject.goalLength) {
                    "Short" -> nShort++
                    "Medium" -> nMedium++
                    "Long" -> nLong++
                }

                when (goalObject.financialActivity) {
                    "Buying Items" -> nBuyingItem++
                    "Planning An Event" -> nEvent++
                    "Saving For Emergency Funds" -> nEmergency++
                    "Donating To Charity" -> nCharity++
                    "Situational Shopping" -> nSituational++
                }
            }
        }.continueWith {
            setDurationPieChart()
            setReasonPieChart()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun computeOverallScore() {
        var nTotal = 0.00F
        var nOnTime =0.00F
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
                }

                var overall = 0.00F
                if (nTotal != 0.00F)
                    overall = (nOnTime/nTotal) * 100

                val overallRoundedNumber = "%.1f".format(overall).toFloat()

                print("print saving overall " + overallRoundedNumber)

                binding.tvPerformancePercentage.text ="${overallRoundedNumber}%"

                if (overall >= 96) {
                    binding.imgFace.setImageResource(R.drawable.excellent)
                    binding.tvPerformanceStatus.text = "Excellent"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                    binding.tvPerformanceText.text = "Your child excels at saving! Encourage them to continue completing their goals."
                } else if (overall < 96 && overall >= 86) {
                    binding.imgFace.setImageResource(R.drawable.amazing)
                    binding.tvPerformanceStatus.text = "Amazing"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.amazing_green))
                    binding.tvPerformanceText.text = "Your child is amazing at goal setting! Encourage them to keep completing those goals!"
                } else if (overall < 86 && overall >= 76) {
                    binding.imgFace.setImageResource(R.drawable.great)
                    binding.tvPerformanceStatus.text = "Great"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                    binding.tvPerformanceText.text = "Your child is doing a great job of saving for their goals"
                } else if (overall < 76 && overall >= 66) {
                    binding.imgFace.setImageResource(R.drawable.good)
                    binding.tvPerformanceStatus.text = "Good"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                    binding.tvPerformanceText.text = "Your child is doing a great job of completing goals! Encourage them to save consistently."
                } else if (overall < 66 && overall >= 56) {
                    binding.imgFace.setImageResource(R.drawable.average)
                    binding.tvPerformanceStatus.text = "Average"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                    binding.tvPerformanceText.text = "Your child is doing a nice job of completing goals! Encourage them to save more consistently."
                    showPerformanceButton()
                } else if (overall < 56 && overall >= 46) {
                    binding.imgFace.setImageResource(R.drawable.nearly_there)
                    binding.tvPerformanceStatus.text = "Nearly There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
                    binding.tvPerformanceText.text = "Your child is nearly there! Click the tips button to learn how to help them get there!"
                    showPerformanceButton()
                }  else if (overall < 46 && overall >= 36) {
                    binding.imgFace.setImageResource(R.drawable.almost_there)
                    binding.tvPerformanceStatus.text = "Almost There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.almost_there_yellow))
                    binding.tvPerformanceText.text = "Your child is almost there! Click the tips button to learn how to help them get there!"
                    showPerformanceButton()
                } else if (overall < 36 && overall >= 26) {
                    binding.imgFace.setImageResource(R.drawable.getting_there)
                    binding.tvPerformanceStatus.text = "Getting There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.getting_there_orange))
                    binding.tvPerformanceText.text = "Your child is getting there! Click the tips button to learn how to help them get there!"
                    showPerformanceButton()
                } else if (overall < 26 && overall >= 16) {
                    binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                    binding.tvPerformanceStatus.text = "Not Quite\nThere"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.not_quite_there_red))
                    binding.tvPerformanceText.text = "Your child is not quite there yet! Click the tips button to learn how to help them get there!"
                    showPerformanceButton()
                } else if (overall < 15) {
                    binding.imgFace.setImageResource(R.drawable.bad)
                    binding.tvPerformanceStatus.text = "Needs\nImprovement"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Uh oh! Click the tips button to learn how to help them improve their saving performance!"
                    showPerformanceButton()
                }
            } else {
                binding.tvPerformanceStatus.text = ""
                binding.tvPerformanceText.text = "Child has yet to complete goals."
            }
            binding.layoutLoading.visibility= View.GONE
            binding.mainLayout.visibility = View.VISIBLE
        }
    }

    private fun showPerformanceButton(){
        binding.btnTips.visibility = View.VISIBLE
    }

    private fun setDurationPieChart() {
        var percentageShort = 0.00F
        var percentageMedium = 0.00F
        var percentageLong = 0.00F

        if (totalGoals!=0) {
            percentageShort = ((nShort.toFloat() / totalGoals.toFloat()) * 100)
            percentageMedium = ((nMedium.toFloat() / totalGoals.toFloat()) * 100)
            percentageLong = ((nLong.toFloat() / totalGoals.toFloat()) * 100)
        }

        var entries = mutableListOf<PieEntry>()
        if (percentageShort!= 0.00F)
            entries.add(PieEntry(percentageShort, "Short"))

        if (percentageMedium!= 0.00F)
            entries.add(PieEntry(percentageMedium, "Medium"))

        if (percentageLong != 0.00F)
            entries.add(PieEntry(percentageLong, "Long"))

        var dataSet = PieDataSet(entries, "Data")

        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.purple_200))
        colors.add(resources.getColor(R.color.yellow))
        colors.add(resources.getColor(R.color.red))

        // setting colors.
        dataSet.colors = colors

        var data = PieData(dataSet)

        binding.pcDuration.data = data
        binding.pcDuration.invalidate()

        val durationRatingArray = ArrayList<SavingPerformanceActivity.DurationRating>()

        durationRatingArray.add(
            SavingPerformanceActivity.DurationRating(
                "Short Term",
                percentageShort
            )
        )
        durationRatingArray.add(
            SavingPerformanceActivity.DurationRating(
                "Medium Term",
                percentageMedium
            )
        )
        durationRatingArray.add(
            SavingPerformanceActivity.DurationRating(
                "Long Term",
                percentageLong
            )
        )

        durationRatingArray.sortByDescending{it.score}

        for (i in durationRatingArray.indices) {
            var num = i + 1

            if (num == 1) {
                if (durationRatingArray[i].score != 0.00F) {
                    binding.tvTopPerformingDuration.text = durationRatingArray[i].name.toString()
                    binding.tvTopPerformingRating.text = DecimalFormat("##0.0").format(durationRatingArray[i].score) + "%"
                } else
                    binding.layoutDuration1.visibility = View.GONE
            } else if (num == 2) {
                if (durationRatingArray[i].score != 0.00F) {
                    binding.tvDuration2nd.text = durationRatingArray[i].name.toString()
                    binding.tvDuration2Rating.text = DecimalFormat("##0.0").format(durationRatingArray[i].score) + "%"
                } else
                    binding.layoutDuration2.visibility = View.GONE
            } else if (num == 3) {
                if (durationRatingArray[i].score != 0.00F) {
                    binding.tvDuration3rd.text = durationRatingArray[i].name.toString()
                    binding.tvDuration3Rating.text = DecimalFormat("##0.0").format(durationRatingArray[i].score) + "%"
                    specificDuration = durationRatingArray[i].name.toString()
                } else
                    binding.layoutDuration3.visibility = View.GONE
            }
        }
//        binding.progressBarShortTerm.progress = percentageShort.toInt()
//        binding.percentageShortTerm.text = DecimalFormat("###0.00").format(percentageShort) + "%"
//        binding.progressBarMediumTerm.progress = percentageMedium.toInt()
//        binding.percentageMediumTerm.text = DecimalFormat("###0.00").format(percentageMedium) + "%"
//        binding.progressBarLongTerm.progress = percentageLong.toInt()
//        binding.percentageLongTerm.text =DecimalFormat("###0.00").format(percentageLong) + "%"
    }

    private fun setReasonPieChart() {
        var percentageBuying = 0.00F
        var percentageEvent = 0.00F
        var percentageEmergency = 0.00F
        var percentageSituational = 0.00F
        var percentageDonating = 0.00F

        if (totalGoals!=0) {
            percentageBuying = ((nBuyingItem.toFloat() / totalGoals.toFloat()) * 100)
            percentageEvent = ((nEvent.toFloat() / totalGoals.toFloat()) * 100)
            percentageEmergency = ((nEmergency.toFloat() / totalGoals.toFloat()) * 100)
            percentageSituational = ((nSituational.toFloat() / totalGoals.toFloat()) * 100)
            percentageDonating = ((nCharity.toFloat() / totalGoals.toFloat()) * 100)
        }



        var entries = mutableListOf<PieEntry>()
        if (percentageBuying!=0.00F)
            entries.add(PieEntry(percentageBuying, "Buying Items"))

        if (percentageEvent!=0.00F)
            entries.add(PieEntry(percentageEvent, "Planning An Event"))

        if (percentageEmergency!=0.00F)
            entries.add(PieEntry(percentageEmergency, "Saving For Emergency Funds"))

        if (percentageSituational!=0.00F)
            entries.add(PieEntry(percentageSituational, "Situational Shopping"))

        if (percentageDonating!=0.00F)
            entries.add(PieEntry(percentageDonating, "Donating To Charity"))


        var dataSet = PieDataSet(entries, "Data")

        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.purple_200))
        colors.add(resources.getColor(R.color.yellow))
        colors.add(resources.getColor(R.color.red))
        colors.add(resources.getColor(R.color.dark_green))
        colors.add(resources.getColor(R.color.teal_200))
        colors.add(resources.getColor(R.color.black))


        // setting colors.
        dataSet.colors = colors

        var data = PieData(dataSet)

        binding.pcCategory.data = data
        binding.pcCategory.invalidate()

        val categoryRatingArray = ArrayList<SavingPerformanceActivity.DurationRating>()

        categoryRatingArray.add(
            SavingPerformanceActivity.DurationRating(
                "Buying Items",
                percentageBuying
            )
        )
        categoryRatingArray.add(
            SavingPerformanceActivity.DurationRating(
                "Planning An Event",
                percentageEvent
            )
        )
        categoryRatingArray.add(
            SavingPerformanceActivity.DurationRating(
                "Saving For Emergency Funds",
                percentageEmergency
            )
        )
        categoryRatingArray.add(
            SavingPerformanceActivity.DurationRating(
                "Situational Shopping",
                percentageSituational
            )
        )
        categoryRatingArray.add(
            SavingPerformanceActivity.DurationRating(
                "Donating To Charity",
                percentageDonating
            )
        )

        categoryRatingArray.sortByDescending{it.score}

        for (i in categoryRatingArray.indices) {
            var num = i + 1

            if (num == 1) {
                if (categoryRatingArray[i].score != 0.00F) {
                    binding.tvTopPerformingActivity.text = categoryRatingArray[i].name.toString()
                    binding.tvTopPerformingActivityPercentage.text = DecimalFormat("##0.0").format(categoryRatingArray[i].score) + "%"
                } else
                    binding.layoutCategory1.visibility = View.GONE
            } else if (num == 2) {
                if (categoryRatingArray[i].score != 0.00F) {
                    binding.tvActivity2nd.text = categoryRatingArray[i].name.toString()
                    binding.tvConcept2Activity.text = DecimalFormat("##0.0").format(categoryRatingArray[i].score) + "%"
                } else
                    binding.layoutCategory2.visibility = View.GONE
            } else if (num == 3) {
                if (categoryRatingArray[i].score != 0.00F) {
                    binding.tvActivity3rd.text = categoryRatingArray[i].name.toString()
                    binding.tvActivity3Rating.text = DecimalFormat("##0.0").format(categoryRatingArray[i].score) + "%"
                } else
                    binding.layoutCategory3.visibility = View.GONE
            } else if (num == 4) {
                if (categoryRatingArray[i].score != 0.00F) {
                    binding.tvActivity4th.text = categoryRatingArray[i].name.toString()
                    binding.tvActivity4Rating.text = DecimalFormat("##0.0").format(categoryRatingArray[i].score) + "%"
                } else
                    binding.layoutCategory4.visibility = View.GONE
            } else if (num == 5) {
                if (categoryRatingArray[i].score != 0.00F) {
                    binding.tvActivity5th.text = categoryRatingArray[i].name.toString()
                    binding.tvActivity5Rating.text = DecimalFormat("##0.0").format(categoryRatingArray[i].score) + "%"
                    specificCategory = categoryRatingArray[i].name.toString()
                } else
                    binding.layoutCategory5.visibility = View.GONE
            }
        }
    }

    private fun showGoalDialog() {

        var dialogBinding= DialogParentSavingTipsBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1500)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        //TODO: Change audio and dialogBinding
        val audio = R.raw.dialog_parent_saving_tips
        dialogBinding.btnSoundParentSavingTips.setOnClickListener {
            if (mediaPlayerGoalDialog == null) {
                mediaPlayerGoalDialog = MediaPlayer.create(this, audio)
            }

            if (mediaPlayerGoalDialog?.isPlaying == true) {
                mediaPlayerGoalDialog?.pause()
                mediaPlayerGoalDialog?.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerGoalDialog?.start()
        }
        dialog.setOnDismissListener { mediaPlayerGoalDialog?.let { it1 -> pauseMediaPlayer(it1) } }

        dialog.show()
    }

    private fun showDurationDialog() {

        var dialogBinding= DialogParentSavingDurationTipsBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this)

        //TODO: Change audio and dialogBinding
        var audio = 0

        if (specificDuration == "Short Term") {
            audio = R.raw.saving_performance_parent_short_term
            dialogBinding.tvTitle.text = "Short Term"
            dialogBinding.tvDefinition.text = "1. Can be achieved in a short amount of time and have lower target amounts.\n" + "2. Duration: Less than 2 weeks"
            dialogBinding.tvExamples.text = "1. Saving for a Fried Chicken Sandwich\n" +
                    "2. Saving for a Small Toy\n" +
                    "3. Saving for a Book"
            dialogBinding.tvTips.text = "1. Encourage your child to consistently set aside money.\n2. Remind them to take note of their target date as it is a short timeline.\n3. Set earning activities such as chores to help them out."

        }  else if (specificDuration == "Medium Term") {
            audio = R.raw.saving_performance_parent_medium_term
            dialogBinding.tvTitle.text = "Medium Term"
            dialogBinding.tvDefinition.text = "1. Takes a longer time to achieve and usually involves bigger target amounts \n" +
                    "2. Duration: 2 to 4 weeks"
            dialogBinding.tvExamples.text = "1. Saving for school supplies\n" +
                    "2. Saving for a larger toy"
            dialogBinding.tvTips.text = "1. Encourage them to set aside money consistently\n" +
                    "2. Set earning activities such as chores to help them out.\n" +
                    "3. Remind them to take note of their target date as it is a short timeline."
        } else if (specificDuration == "Long Term") {
            audio = R.raw.saving_performance_parent_long_term
            dialogBinding.tvTitle.text = "Long Term"
            dialogBinding.tvDefinition.text = "1. Takes a long time to achieve and involves bigger target amounts \n" +
                    "2. Duration: Over a month"
            dialogBinding.tvExamples.text = "1. Saving for a trip \n" +
                    "2. Saving for a birthday party"
            dialogBinding.tvTips.text = "1. Encourage them to start saving early. Even if the target date may seem far away, target amounts for long term goals tend to be larger.\n" +
                    "2. Encourage them to set aside money consistently" +
                    "3. Set earning activities such as chores to help them out."
        }

        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSoundParentSavingDuration.setOnClickListener {
            if (mediaPlayerDurationDialog == null) {
                mediaPlayerDurationDialog = MediaPlayer.create(this, audio)
            }

            if (mediaPlayerDurationDialog?.isPlaying == true) {
                mediaPlayerDurationDialog?.pause()
                mediaPlayerDurationDialog?.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerDurationDialog?.start()
        }

        dialog.setOnDismissListener { mediaPlayerDurationDialog?.let { it1 -> pauseMediaPlayer(it1) } }

        dialog.show()
    }

    private fun showCategoryDialog() {

        var dialogBinding= DialogParentSavingCategoryTipsBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);

        print("CATEgoRY " + specificCategory)

        //TODO: Change audio and dialogBinding
        var audio = 0
        if (specificCategory == "Buying Items") {
            audio = R.raw.saving_performance_parent_buying_items
            dialogBinding.tvTitle.text = "Buying Items"
            dialogBinding.tvDefinition.text = "Purchasing things such as goods or services."
            dialogBinding.tvExamples.text = "1. Buying a toy\n" +
                    "2. Buying a book"
            dialogBinding.tvTips.text = "1. Encourage them to set aside money consistently.\n2. Remind them to keep their target date in mind."
        } else if (specificCategory == "Planning An Event") {
            audio = R.raw.saving_performance_parent_planning_event
            dialogBinding.tvTitle.text = "Planning An Event"
            dialogBinding.tvDefinition.text = "Organizing an event and ensuring that all needed materials or services are accounted for."
            dialogBinding.tvExamples.text = "1. Birthday party\n" +
                    "2. Out of town trip"
            dialogBinding.tvTips.text = "1. Encourage them to set aside money consistently.\n" +
                    "2. Remind them to start saving early. Goals related to planning events tend to have larger target amounts.\n" +
                    "3. Remind them to keep their target date in mind."

        } else if (specificCategory == "Saving For Emergency Funds") {
            audio = R.raw.saving_performance_parent_emergency_funds
            dialogBinding.tvTitle.text = "Saving For Emergency Funds"
            dialogBinding.tvDefinition.text = "1. Saving money to be used in the future for unexpected situations \n" +
                    "2. Important to be prepared for these situations"
            dialogBinding.tvExamples.text = "1. Loss of valuables \n" +
                    "2. Unexpected expenses"
            dialogBinding.tvTips.text = "1. Encourage them to set aside money consistently.\n" +
                    "2. Encourage them to think about their future.\n3. Share instances where you have had unexpected expenses with them."
        } else if (specificCategory == "Donating To Charity") {
            audio = R.raw.saving_performance_parent_donating_charity
            dialogBinding.tvTitle.text = "Donating To Charity"
            dialogBinding.tvDefinition.text = "1. Giving money to a non-profit organization to support an advocacy.\n" +
                    "2. Important to think about others"
            dialogBinding.tvExamples.text = "1. Donating to a church \n" +
                    "2. Donating to Red Cross"
            dialogBinding.tvTips.text = "1. Encourage them to donate to an organization that they share an advocacy with \n" +
                    "2. Encourage them to set aside money consistently.\n3. Share experiences when you have donated to an organization."
        } else if (specificCategory == "Situational Shopping") {
            audio = R.raw.saving_performance_parent_situational_shopping
            dialogBinding.tvTitle.text = "Situational Shopping"
            dialogBinding.tvDefinition.text = "Shopping for a certain event or happening."
            dialogBinding.tvExamples.text = "1. Grocery shopping \n" +
                    "2. Back to school shopping"
            dialogBinding.tvTips.text = "1. Encourage them to set aside money consistently. \n" +
                    "2. Remind them to keep their target date in mind.\n3. Involve them in household shopping."
        }

        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSoundParentSavingCategory.setOnClickListener {
            if (mediaPlayerCategoryDialog == null) {
                mediaPlayerCategoryDialog = MediaPlayer.create(this, audio)
            }

            if (mediaPlayerCategoryDialog?.isPlaying == true) {
                mediaPlayerCategoryDialog?.pause()
                mediaPlayerCategoryDialog?.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayerCategoryDialog?.start()
        }
        dialog.setOnDismissListener { mediaPlayerCategoryDialog?.let { it1 -> pauseMediaPlayer(it1) } }

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

    override fun onDestroy() {
        releaseMediaPlayer(mediaPlayerGoalDialog)
        releaseMediaPlayer(mediaPlayerDurationDialog)
        releaseMediaPlayer(mediaPlayerCategoryDialog)
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