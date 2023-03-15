package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.GoalSettingPerformanceActivity
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.*
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

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

    private var specificDuration = "Short"
    private var specificCategory = "Buying Items"

    data class DurationRating(var name: String? = null, var score: Int = 0)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getGoals()
        loadBackButton()
        computeOverallScore()
        checkAge()
    }

    private fun getGoals() {
        val childID  = FirebaseAuth.getInstance().currentUser!!.uid

        firestore.collection("FinancialGoals").whereEqualTo("childID", childID).get().addOnSuccessListener { results ->
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
            showGoalDialog()
        }

        binding.btnReviewDuration.setOnClickListener{
            showDurationDialog()
        }

        binding.btnReviewConcept.setOnClickListener{
            showCategoryDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun computeOverallScore() {
        var nTotal = 0.00F
        var nOnTime =0.00F
        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).get().addOnSuccessListener { results ->
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

            val overall = (nOnTime/nTotal) * 100
            val overallRoundedNumber = "%.1f".format(overall).toFloat()

            binding.tvPerformancePercentage.text ="${overallRoundedNumber}%"

            if (overall >= 96) {
                binding.imgFace.setImageResource(R.drawable.excellent)
                binding.tvPerformanceStatus.text = "Excellent"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                binding.tvPerformanceText.text = "Keep up the excellent work! Saving is your strong point. Keep completing those goals!"
            } else if (overall < 96 && overall >= 86) {
                binding.imgFace.setImageResource(R.drawable.amazing)
                binding.tvPerformanceStatus.text = "Amazing"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                binding.tvPerformanceText.text = "Amazing job! You are performing well. Saving is your strong point. Keep completing those goals!"
            } else if (overall < 86 && overall >= 76) {
                binding.imgFace.setImageResource(R.drawable.great)
                binding.tvPerformanceStatus.text = "Great"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                binding.tvPerformanceText.text = "Great job! You are performing well. Keep completing those goals!"
            } else if (overall < 76 && overall >= 66) {
                binding.imgFace.setImageResource(R.drawable.good)
                binding.tvPerformanceStatus.text = "Good"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                binding.tvPerformanceText.text = "Good job! With a bit more dedication and effort, you’ll surely up your performance!"
            } else if (overall < 66 && overall >= 56) {
                binding.imgFace.setImageResource(R.drawable.average)
                binding.tvPerformanceStatus.text = "Average"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                binding.tvPerformanceText.text = "Nice work! Work on improving your saving performance through time and effort. You’ll get there soon!"
            } else if (overall < 56 && overall >= 46) {
                binding.imgFace.setImageResource(R.drawable.nearly_there)
                binding.tvPerformanceStatus.text = "Nearly There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "You're nearly there! Click review to learn how to get there!"
            }  else if (overall < 46 && overall >= 36) {
                binding.imgFace.setImageResource(R.drawable.almost_there)
                binding.tvPerformanceStatus.text = "Almost There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Almost there! You need to work on your saving. Click review to learn how!"
            } else if (overall < 36 && overall >= 26) {
                binding.imgFace.setImageResource(R.drawable.getting_there)
                binding.tvPerformanceStatus.text = "Getting There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Getting there! You need to work on your saving. Click review to learn how!"
            } else if (overall < 26 && overall >= 16) {
                binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                binding.tvPerformanceStatus.text = "Not Quite There Yet"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Not quite there yet! Don't give up. Click review to learn how to get there!"
            } else if (overall < 15) {
                binding.imgFace.setImageResource(R.drawable.bad)
                binding.tvPerformanceStatus.text = "Needs Improvement"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Uh oh! Your saving performance needs a lot of improvement.  Click review to learn how!"
            }
        }
    }

    private fun DurationRatingObject(name: String, score: Int): DurationRating {
        val rating = DurationRating()

        rating.name = name
        rating.score = score

        return rating
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

        val entries = listOf(
            PieEntry(percentageShort, "Short"),
            PieEntry(percentageMedium, "Medium"),
            PieEntry(percentageLong, "Long"),
        )

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

        durationRatingArray.add(DurationRatingObject("Short Term", DecimalFormat("###0.00").format(percentageShort).toInt()))
        durationRatingArray.add(DurationRatingObject("Medium Term", DecimalFormat("###0.00").format(percentageMedium).toInt()))
        durationRatingArray.add(DurationRatingObject("Long Term", DecimalFormat("###0.00").format(percentageLong).toInt()))

        durationRatingArray.sortByDescending{it.score}

        for (i in durationRatingArray.indices) {
            var num = i + 1

            if (num == 1) {
                binding.tvTopPerformingDuration.text = durationRatingArray[i].name.toString() + " Term"
                binding.tvTopPerformingRating.text = durationRatingArray[i].score.toString() + "%"
            } else if (num == 2) {
                binding.tvDuration2nd.text = durationRatingArray[i].name.toString() + " Term"
                binding.tvDuration2Rating.text = durationRatingArray[i].score.toString() + "%"
            } else if (num == 3) {
                binding.tvDuration3rd.text = durationRatingArray[i].name.toString() + " Term"
                binding.tvDuration3Rating.text = durationRatingArray[i].score.toString() + "%"
                specificDuration = durationRatingArray[i].name.toString()
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
        var percentageEarning = 0.00F

        if (totalGoals!=0) {
            percentageBuying = ((nBuyingItem.toFloat() / totalGoals.toFloat()) * 100)
            percentageEvent = ((nEvent.toFloat() / totalGoals.toFloat()) * 100)
            percentageEmergency = ((nEmergency.toFloat() / totalGoals.toFloat()) * 100)
            percentageSituational = ((nSituational.toFloat() / totalGoals.toFloat()) * 100)
            percentageDonating = ((nCharity.toFloat() / totalGoals.toFloat()) * 100)
            percentageEarning = ((nEarning.toFloat() / totalGoals.toFloat()) * 100)
        }



        val entries = listOf(
            PieEntry(percentageBuying, "Buying Items"),
            PieEntry(percentageEvent, "Planning An Event"),
            PieEntry(percentageEmergency, "Saving For Emergency Funds"),
            PieEntry(percentageSituational, "Situational Shopping"),
            PieEntry(percentageDonating, "Donating To Charity"),
            PieEntry(percentageEarning, "Earning Money"),
        )

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

        categoryRatingArray.add(DurationRatingObject("Buying Items", DecimalFormat("###0.00").format(percentageBuying).toInt()))
        categoryRatingArray.add(DurationRatingObject("Planning an Event", DecimalFormat("###0.00").format(percentageEvent).toInt()))
        categoryRatingArray.add(DurationRatingObject("Saving for Emergency Funds", DecimalFormat("###0.00").format(percentageEmergency).toInt()))
        categoryRatingArray.add(DurationRatingObject("Situational Shopping", DecimalFormat("###0.00").format(percentageSituational).toInt()))
        categoryRatingArray.add(DurationRatingObject("Donating to Charity", DecimalFormat("###0.00").format(percentageDonating).toInt()))
        categoryRatingArray.add(DurationRatingObject("Earning Money", DecimalFormat("###0.00").format(percentageEarning).toInt()))

        categoryRatingArray.sortByDescending{it.score}

        for (i in categoryRatingArray.indices) {
            var num = i + 1

            if (num == 1) {
                binding.tvTopPerformingActivity.text = categoryRatingArray[i].name
                binding.tvTopPerformingActivityPercentage.text = categoryRatingArray[i].score.toString() + "%"
            } else if (num == 2) {
                binding.tvActivity2nd.text = categoryRatingArray[i].name
                binding.tvConcept2Activity.text = categoryRatingArray[i].score.toString() + "%"
            } else if (num == 3) {
                binding.tvActivity3rd.text = categoryRatingArray[i].name
                binding.tvActivity3Rating.text = categoryRatingArray[i].score.toString() + "%"
            } else if (num == 4) {
                binding.tvActivity4th.text = categoryRatingArray[i].name
                binding.tvActivity4Rating.text = categoryRatingArray[i].score.toString() + "%"
            } else if (num == 5) {
                binding.tvActivity5th.text = categoryRatingArray[i].name
                binding.tvActivity5Rating.text = categoryRatingArray[i].score.toString() + "%"
                specificCategory = categoryRatingArray[i].name.toString()
            }
        }

//        binding.progressBarBuyingItems.progress = percentageBuying.toInt()
//        binding.percentageBuyingItems.text = DecimalFormat("###0.00").format(percentageBuying) + "%"
//        binding.progressBarPlanningEvent.progress = percentageEvent.toInt()
//        binding.percentagePlanningEvent.text = DecimalFormat("###0.00").format(percentageEvent) + "%"
//        binding.progressBarEmergencyFunds.progress = percentageEmergency.toInt()
//        binding.percentageEmergencyFunds.text =DecimalFormat("###0.00").format(percentageEmergency) + "%"
//        binding.progressBarSituationalShopping.progress = percentageSituational.toInt()
//        binding.percentageSituationalShopping.text = DecimalFormat("###0.00").format(percentageSituational) + "%"
//        binding.progressBarDonating.progress = percentageDonating.toInt()
//        binding.percentageDonating.text = DecimalFormat("###0.00").format(percentageDonating) + "%"
//        binding.progressBarEarningMoney.progress = percentageEarning.toInt()
//        binding.percentageEarningMoney.text =DecimalFormat("###0.00").format(percentageEarning) + "%"
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
            bundle.putString("source", "childFinancialActivity")
            goToNewGoal.putExtras(bundle)
            goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToNewGoal)
        }
        dialog.show()
    }

    private fun showDurationDialog() {

        var dialogBinding= DialogSavingDurationReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);

        if (specificDuration == "Short") {
            dialogBinding.tvTitle.text = "Short Term"
            dialogBinding.tvDefinition.text = "1. Can be achieved in a short amount of time and have \n" + "2. Duration: Less than 2 weeks"
            dialogBinding.tvExamples.text = "1. Saving for a Fried Chicken Sandwich\n" +
                    "2. Saving for a Small Toy\n" +
                    "3. Saving for a Book"
            dialogBinding.tvTips.text = "1. Set aside money consistently\n" +
                    "2. Keep the target date in mind"
        }  else if (specificDuration == "Medium") {
            dialogBinding.tvTitle.text = "Medium Term"
            dialogBinding.tvDefinition.text = "1. Takes a longer time to achieve and usually involves bigger target amounts \n" +
                    "2. Duration: 2 to 4 weeks"
            dialogBinding.tvExamples.text = "1. Saving for school supplies\n" +
                    "2. Saving for a larger toy"
            dialogBinding.tvTips.text = "1. Set aside money consistently\n" +
                    "2. Earn extra by helping with chores and selling items\n" +
                    "3. Keep the target date in mind"
        } else if (specificDuration == "Long") {
            dialogBinding.tvTitle.text = "Long Term"
            dialogBinding.tvDefinition.text = "1. Takes a long time to achieve and involves bigger target amounts \n" +
                    "2. Duration: Over a month"
            dialogBinding.tvExamples.text = "1. Saving for a trip \n" +
                    "2. Saving for a birthday party"
            dialogBinding.tvTips.text = "1. SStart saving early. Even if the target date may seem far away, target amounts for long term goals tend to be larger.\n" +
                    "2. Set aside money consistently\n" +
                    "3. Earn extra by helping with chores and selling items"
        }

        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showCategoryDialog() {

        var dialogBinding= DialogSavingCategoryReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);

        if (specificCategory == "Buying Items") {
            dialogBinding.tvTitle.text = "Buying Items"
            dialogBinding.tvDefinition.text = "Purchasing things such as goods or services."
            dialogBinding.tvExamples.text = "1. Buying a toy\n" +
            "2. Buying a book \n"
            dialogBinding.tvTips.text = "1. Set aside money consistently.\n" +
                    "2. Keep your target date in mind.\n"
        } else if (specificCategory == "Planning An Event") {
            dialogBinding.tvTitle.text = "Planning An Event"
            dialogBinding.tvDefinition.text = "Organizing an event and ensuring that all needed materials or services are accounted for."
            dialogBinding.tvExamples.text = "1. Birthday party\n" +
                    "2. Out of town trip"
            dialogBinding.tvTips.text = "1. Set aside money consistently.\n" +
            "2. Start saving early. Goals related to planning events tend to have larger target amounts.\n" +
            "3. Keep your target date in mind."

        } else if (specificCategory == "Saving For Emergency Funds") {
            dialogBinding.tvTitle.text = "Saving For Emergency Funds"
            dialogBinding.tvDefinition.text = "1. Saving money to be used in the future for unexpected situations \n" +
                    "2. Important to be prepared for these situations"
            dialogBinding.tvExamples.text = "1. Loss of valuables \n" +
                    "2. Unexpected expenses"
            dialogBinding.tvTips.text = "1. Set aside money consistently.\n" +
                    "2. Think about your future."
        } else if (specificCategory == "Donating To Charity") {
            dialogBinding.tvTitle.text = "Donating To Charity"
            dialogBinding.tvDefinition.text = "1. Giving money to a non-profit organization to support an advocacy.\n" +
                    "2. Important to think about others"
            dialogBinding.tvExamples.text = "1. Donating to a church \n" +
                    "2. Donating to Red Cross"
            dialogBinding.tvTips.text = "1. Donate to an organization that you share an advocacy with \n" +
                    "2. Set aside money consistently"
        } else if (specificCategory == "Situational Shopping") {
            dialogBinding.tvTitle.text = "Situational Shopping"
            dialogBinding.tvDefinition.text = "Shopping for a certain event or happening."
            dialogBinding.tvExamples.text = "1. Grocery shopping \n" +
                    "2. Back to school shopping"
            dialogBinding.tvTips.text = "1. Set aside money consistently. \n" +
                    "2. Keep your target date in mind."
        }

        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}