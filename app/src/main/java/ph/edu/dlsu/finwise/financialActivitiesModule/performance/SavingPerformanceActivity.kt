package ph.edu.dlsu.finwise.financialActivitiesModule.performance

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.viewbinding.ViewBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.*
import ph.edu.dlsu.finwise.databinding.ActivitySavingPerformanceBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.NewGoal
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class SavingPerformanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavingPerformanceBinding
    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private var ongoingGoals = 0
    //includes achieved goals in count
    private var totalGoals = 0

    private var setOwnGoals = false

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

    private var specificDuration = "Short Term"
    private var specificCategory = "Buying Items"

    private var mediaPlayer: MediaPlayer? = null
    private var mediaPlayerGoalDialog: MediaPlayer? = null
    private var mediaPlayerDurationDialog: MediaPlayer? = null
    private var mediaPlayerCategoryDialog: MediaPlayer? = null

    data class DurationRating(var name: String? = null, var score: Float)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.title.text = "Overall Saving Performance"
        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        getGoals()
        loadBackButton()
        computeOverallScore()
        checkAge()
    }

    private fun getGoals() {
        val childID  = FirebaseAuth.getInstance().currentUser!!.uid

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

        binding.btnReview.setOnClickListener{
            mediaPlayer?.let { pauseMediaPlayer(it) }
            showGoalDialog()
        }

        binding.btnReviewDuration.setOnClickListener{
            mediaPlayer?.let { pauseMediaPlayer(it) }
            showDurationDialog()
        }

        binding.btnReviewConcept.setOnClickListener{
            mediaPlayer?.let { pauseMediaPlayer(it) }
            showCategoryDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun computeOverallScore() {
        var nTotal = 0.00F
        var nOnTime =0.00F
        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
            //TODO: Change audio
            var audio = 0
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
                binding.btnReview.visibility = View.GONE



            if (overall >= 96) {
                audio = R.raw.saving_performance_overall_excellent
                binding.imgFace.setImageResource(R.drawable.excellent)
                binding.tvPerformanceStatus.text = "Excellent"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                binding.tvPerformanceText.text = "Keep up the excellent work! You are good at saving. Keep completing your goals!"
            } else if (overall < 96 && overall >= 86) {
                audio = R.raw.saving_performance_overall_amazing
                binding.imgFace.setImageResource(R.drawable.amazing)
                binding.tvPerformanceStatus.text = "Amazing"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.amazing_green))
                binding.tvPerformanceText.text = "Amazing job! Saving is your strong point. Keep completing your goals!"
            } else if (overall < 86 && overall >= 76) {
                audio = R.raw.saving_performance_overall_great
                binding.imgFace.setImageResource(R.drawable.great)
                binding.tvPerformanceStatus.text = "Great"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                binding.tvPerformanceText.text = "Great job! You are performing well. Keep completing your goals!"
            } else if (overall < 76 && overall >= 66) {
                audio = R.raw.saving_performance_overall_good
                binding.imgFace.setImageResource(R.drawable.good)
                binding.tvPerformanceStatus.text = "Good"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                binding.tvPerformanceText.text = "Good job! Up your performance by keeping your goals in mind!"
            } else if (overall < 66 && overall >= 56) {
                audio = R.raw.saving_performance_overall_average
                binding.imgFace.setImageResource(R.drawable.average)
                binding.tvPerformanceStatus.text = "Average"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                binding.tvPerformanceText.text = "Nice work! Up your performance by consistently setting money aside!"
                showPerformanceButton()
            } else if (overall < 56 && overall >= 46) {
                audio = R.raw.saving_performance_overall_nearly_there
                binding.imgFace.setImageResource(R.drawable.nearly_there)
                binding.tvPerformanceStatus.text = "Nearly There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
                binding.tvPerformanceText.text = "You're nearly there! Up your performance by setting money aside and earning through activities."
                showPerformanceButton()
            }  else if (overall < 46 && overall >= 36) {
                audio = R.raw.saving_performance_overall_almost_there
                binding.imgFace.setImageResource(R.drawable.almost_there)
                binding.tvPerformanceStatus.text = "Almost There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.almost_there_yellow))
                binding.tvPerformanceText.text = "Almost there! Up your performance by setting money aside and earning through activities."
                showPerformanceButton()
            } else if (overall < 36 && overall >= 26) {
                audio = R.raw.saving_performance_overall_getting_there
                binding.imgFace.setImageResource(R.drawable.getting_there)
                binding.tvPerformanceStatus.text = "Getting There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.getting_there_orange))
                binding.tvPerformanceText.text = "Getting there! Up your performance by setting money aside and earning through activities."
                showPerformanceButton()
            } else if (overall < 26 && overall >= 16) {
                audio = R.raw.saving_performance_overall_not_quite_there_yet
                binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                binding.tvPerformanceStatus.text = "Not Quite\nThere"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.not_quite_there_red))
                binding.tvPerformanceText.text = "Not quite there yet! Up your performance by setting money aside and earning through activities. Don't give up!"
                showPerformanceButton()
            } else if (overall < 15) {
                audio = R.raw.saving_performance_overall_needs_improvement
                binding.imgFace.setImageResource(R.drawable.bad)
                binding.tvPerformanceStatus.text = "Needs\nImprovement"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Don't worry! Up your performance by setting money aside and earning through activities. You can do it!"
                showPerformanceButton()
            }
            } else {
                audio = R.raw.saving_performance_overall_default
                binding.tvPerformanceStatus.text = "Get Started!"
                binding.tvPerformanceText.text = "Complete goals to see your performance!"
            }
            loadAudio(audio)
        }
    }

    private fun loadAudio(audio: Int) {
        /*TODO: Change binding and Audio file in mediaPlayer*/
        binding.btnAudioOverallSavingPerformance.setOnClickListener {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, audio)
            }

            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                mediaPlayer?.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayer?.start()
        }
    }

    override fun onDestroy() {
        releaseMediaPlayer(mediaPlayer)
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




    private fun showPerformanceButton(){
        binding.btnReview.visibility = View.VISIBLE
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
        colors.add(resources.getColor( R.color.yellow))
        colors.add(resources.getColor(R.color.red))

        // setting colors.
        dataSet.colors = colors

        var data = PieData(dataSet)

        binding.pcDuration.data = data
        binding.pcDuration.invalidate()

        val durationRatingArray = ArrayList<DurationRating>()

        durationRatingArray.add(DurationRating("Short Term", percentageShort))
        durationRatingArray.add(DurationRating("Medium Term",percentageMedium))
        durationRatingArray.add(DurationRating("Long Term", percentageLong))

        durationRatingArray.sortByDescending{it.score}

        for (i in durationRatingArray.indices) {
            var num = i + 1

            if (num == 1) {
                binding.tvTopPerformingDuration.text = durationRatingArray[i].name.toString()
                binding.tvTopPerformingRating.text = DecimalFormat("##0.0").format(durationRatingArray[i].score) + "%"
            } else if (num == 2) {
                binding.tvDuration2nd.text = durationRatingArray[i].name.toString()
                binding.tvDuration2Rating.text = DecimalFormat("##0.0").format(durationRatingArray[i].score) + "%"
            } else if (num == 3) {
                binding.tvDuration3rd.text = durationRatingArray[i].name.toString()
                binding.tvDuration3Rating.text = DecimalFormat("##0.0").format(durationRatingArray[i].score) + "%"
                specificDuration = durationRatingArray[i].name.toString()
            }
        }
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
        colors.add(resources.getColor( R.color.yellow))
        colors.add(resources.getColor(R.color.red))
        colors.add(resources.getColor( R.color.dark_green))
        colors.add(resources.getColor( R.color.teal_200))
        colors.add(resources.getColor( R.color.black))


        // setting colors.
        dataSet.colors = colors

        var data = PieData(dataSet)

        binding.pcCategory.data = data
        binding.pcCategory.invalidate()

        val categoryRatingArray = ArrayList<DurationRating>()

        categoryRatingArray.add(DurationRating("Buying Items", percentageBuying))
        categoryRatingArray.add(DurationRating("Planning An Event", percentageEvent))
        categoryRatingArray.add(DurationRating("Saving For Emergency Funds", percentageEmergency))
        categoryRatingArray.add(DurationRating("Situational Shopping", percentageSituational))
        categoryRatingArray.add(DurationRating("Donating To Charity", percentageDonating))

        categoryRatingArray.sortByDescending{it.score}

        for (i in categoryRatingArray.indices) {
            var num = i + 1

            if (num == 1) {
                binding.tvTopPerformingActivity.text = categoryRatingArray[i].name.toString()
                binding.tvTopPerformingActivityPercentage.text = DecimalFormat("##0.0").format(categoryRatingArray[i].score) + "%"
            } else if (num == 2) {
                binding.tvActivity2nd.text = categoryRatingArray[i].name.toString()
                binding.tvConcept2Activity.text = DecimalFormat("##0.0").format(categoryRatingArray[i].score) + "%"
            } else if (num == 3) {
                binding.tvActivity3rd.text = categoryRatingArray[i].name.toString()
                binding.tvActivity3Rating.text = DecimalFormat("##0.0").format(categoryRatingArray[i].score) + "%"
            } else if (num == 4) {
                binding.tvActivity4th.text = categoryRatingArray[i].name.toString()
                binding.tvActivity4Rating.text = DecimalFormat("##0.0").format(categoryRatingArray[i].score) + "%"
            } else if (num == 5) {
                binding.tvActivity5th.text = categoryRatingArray[i].name.toString()
                binding.tvActivity5Rating.text = DecimalFormat("##0.0").format(categoryRatingArray[i].score) + "%"
                specificCategory = categoryRatingArray[i].name.toString()
            }
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
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
            if (age == 10 || age == 11 || age == 12)
                setOwnGoals = true

        }.continueWith { }
    }

    private fun showGoalDialog() {

        var dialogBinding= DialogSavingReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
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
            var goToGoalSetting = Intent(this, FinancialActivity::class.java)
            this.startActivity(goToGoalSetting)
        }

        dialogBinding.btnSetNewGoal.setOnClickListener {
            dialog.dismiss()
            var goToNewGoal = Intent(this, NewGoal::class.java)
            var bundle = Bundle()
            bundle.putString("source", "Child")
            goToNewGoal.putExtras(bundle)
            goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToNewGoal)
        }
        //TODO: Change audio and dialogBinding
        val audio = R.raw.dialog_saving_review
        dialogBinding.btnSoundSaving.setOnClickListener {
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

        //TODO: Change audio and dialogBinding
        var audio = R.raw.sample

        var dialogBinding= DialogSavingDurationReviewBinding.inflate(layoutInflater)
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.root)

        if (specificDuration == "Short Term") {
            audio = R.raw.saving_performance_short_term
            dialogBinding.tvTitle.text = "Short Term"
            dialogBinding.tvDefinition.text = "1. Can be achieved in a short amount of time\n" + "2. Duration: Less than 2 weeks\n" + "3. Focused on immediate wants or needs"
            dialogBinding.tvExamples.text = "1. Saving for a Fried Chicken Sandwich\n" +
                    "2. Saving for a Small Toy\n" +
                    "3. Saving for a Book"
            dialogBinding.tvTips.text = "1. Set aside money consistently\n" +
                    "2. Keep the target date in mind\n" + "3. Accomplish chores or sell items for extra money"
        }  else if (specificDuration == "Medium Term") {
            audio = R.raw.saving_performance_medium
            dialogBinding.tvTitle.text = "Medium Term"
            dialogBinding.tvDefinition.text = "1. Takes a longer time to achieve and involves bigger target amounts \n" +
                    "2. Duration: 2 to 4 weeks"
            dialogBinding.tvExamples.text = "1. Saving for school supplies\n" +
                    "2. Saving for a larger toy"
            dialogBinding.tvTips.text = "1. Set aside money consistently\n" +
                    "2. Earn extra by helping with chores and selling items\n" +
                    "3. Keep the target date in mind"
        } else if (specificDuration == "Long Term") {
            audio = R.raw.saving_performance_long
            dialogBinding.tvTitle.text = "Long Term"
            dialogBinding.tvDefinition.text = "1. Takes a long time to achieve and involves bigger target amounts \n" +
                    "2. Duration: Over a month"
            dialogBinding.tvExamples.text = "1. Saving for a trip \n" +
                    "2. Saving for a birthday party"
            dialogBinding.tvTips.text = "1. Start saving early. Even if the target date may seem far away, target amounts for long term goals tend to be larger\n" +
                    "2. Set aside money consistently\n" +
                    "3. Earn extra by helping with chores and selling items"
        }

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSoundSavingDuration.setOnClickListener {
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

    private fun pauseMediaPlayer(mediaPlayer: MediaPlayer) {
        mediaPlayer.let {
            if (it.isPlaying) {
                it.pause()
                it.seekTo(0)
            }
        }
    }

    private fun showCategoryDialog() {
        //TODO: Change audio and dialogBinding
        var audio = R.raw.sample
        var dialog= Dialog(this);
        var dialogBinding= DialogSavingCategoryReviewBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        if (specificCategory == "Buying Items") {
            audio = R.raw.saving_performance_buying_items
            dialogBinding.tvTitle.text = "Buying Items"
            dialogBinding.tvDefinition.text = "Purchasing things such as goods or services."
            dialogBinding.tvExamples.text = "1. Buying a toy\n" +
            "2. Buying a book \n"
            dialogBinding.tvTips.text = "1. Set aside money consistently.\n" +
                    "2. Keep your target date in mind.\n" + "3. Accomplish chores or sell items for extra money."
        } else if (specificCategory == "Planning An Event") {
            audio = R.raw.saving_performance_planning_event
            dialogBinding.tvTitle.text = "Planning An Event"
            dialogBinding.tvDefinition.text = "Organizing an event and ensuring that all needed materials or services are accounted for."
            dialogBinding.tvExamples.text = "1. Birthday party\n" +
                    "2. Out of town trip"
            dialogBinding.tvTips.text = "1. Set aside money consistently.\n" +
            "2. Start saving early. Goals related to planning events tend to have larger target amounts.\n" +
            "3. Accomplish chores or sell items for extra money."

        } else if (specificCategory == "Saving For Emergency Funds") {
            audio = R.raw.saving_performance_emergency_funds
            dialogBinding.tvTitle.text = "Saving For Emergency Funds"
            dialogBinding.tvDefinition.text = "1. Saving money to be used in the future for unexpected situations.\n" +
                    "2. Important to be prepared for these situations."
            dialogBinding.tvExamples.text = "1. Loss of valuables \n" +
                    "2. Unexpected expenses"
            dialogBinding.tvTips.text = "1. Set aside money consistently.\n" +
                    "2. Keep your future in mind.\n" + "3. Accomplish chores or sell items for extra money."
        } else if (specificCategory == "Donating To Charity") {
            audio = R.raw.saving_performance_charity
            println("print in donating to charity")
            dialogBinding.tvTitle.text = "Donating To Charity"
            dialogBinding.tvDefinition.text = "Giving money to a non-profit organization to support an advocacy."
            dialogBinding.tvExamples.text = "1. Donating to a church\n" +
                    "2. Donating to Red Cross"
            dialogBinding.tvTips.text = "1. Donate to an organization that you share an advocacy with.\n" +
                    "2. Set aside money consistently.\n" + "3. Accomplish chores or sell items for extra money."
        } else if (specificCategory == "Situational Shopping") {
            audio = R.raw.saving_performance_situational
            dialogBinding.tvTitle.text = "Situational Shopping"
            dialogBinding.tvDefinition.text = "Shopping for a certain event or happening."
            dialogBinding.tvExamples.text = "1. Grocery shopping \n" +
                    "2. Back to school shopping"
            dialogBinding.tvTips.text = "1. Set aside money consistently.\n" +
                    "2. Keep your target date in mind.\n" + "3. Accomplish chores or sell items for extra money."
        }

        dialog.window!!.setLayout(1000, 1500)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSoundSavingCategory.setOnClickListener {
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
}