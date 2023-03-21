package ph.edu.dlsu.finwise

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityParentSavingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.DialogParentSavingCategoryTipsBinding
import ph.edu.dlsu.finwise.databinding.DialogParentSavingDurationTipsBinding
import ph.edu.dlsu.finwise.databinding.DialogParentSavingTipsBinding
import ph.edu.dlsu.finwise.databinding.DialogSavingCategoryReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogSavingDurationReviewBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.NewGoal
import ph.edu.dlsu.finwise.financialActivitiesModule.SavingPerformanceActivity
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

        var bundle = Bundle()
        childID = bundle?.getString("childID").toString()
        ongoingGoals = 0
        totalGoals = 0
        goalIDArrayList.clear()
        savingsArrayList.clear()

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.title.text = "Overall Saving Performance"
        binding.tvPerformancePercentage.text = "0.00%"
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

    private fun getGoals() {
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

                binding.tvPerformancePercentage.text ="${overallRoundedNumber}%"

                if (overall >= 96) {
                    binding.imgFace.setImageResource(R.drawable.excellent)
                    binding.tvPerformanceStatus.text = "Excellent"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                    binding.tvPerformanceText.text = "Your child excels at saving! Encourage them to continue completing their goals."
                } else if (overall < 96 && overall >= 86) {
                    binding.imgFace.setImageResource(R.drawable.amazing)
                    binding.tvPerformanceStatus.text = "Amazing"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
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
                } else if (overall < 56 && overall >= 46) {
                    binding.imgFace.setImageResource(R.drawable.nearly_there)
                    binding.tvPerformanceStatus.text = "Nearly There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Your child is nearly there! Click the tips button to learn how to help them get there!"
                }  else if (overall < 46 && overall >= 36) {
                    binding.imgFace.setImageResource(R.drawable.almost_there)
                    binding.tvPerformanceStatus.text = "Almost There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Your child is almost there! Click the tips button to learn how to help them get there!"
                } else if (overall < 36 && overall >= 26) {
                    binding.imgFace.setImageResource(R.drawable.getting_there)
                    binding.tvPerformanceStatus.text = "Getting There"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Your child is getting there! Click the tips button to learn how to help them get there!"
                } else if (overall < 26 && overall >= 16) {
                    binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                    binding.tvPerformanceStatus.text = "Not Quite There Yet"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Your child is not quite there yet! Click the tips button to learn how to help them get there!"
                } else if (overall < 15) {
                    binding.imgFace.setImageResource(R.drawable.bad)
                    binding.tvPerformanceStatus.text = "Needs Improvement"
                    binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                    binding.tvPerformanceText.text = "Uh oh! Click the tips button to learn how to help them improve their saving performance!"
                }
            } else {
                binding.imgFace.setImageResource(R.drawable.good)
                binding.tvPerformanceStatus.text = ""
                binding.tvPerformanceText.text = "Child has yet to complete goals."
            }
        }
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
                binding.tvTopPerformingDuration.text = durationRatingArray[i].name.toString() + " Term"
                binding.tvTopPerformingRating.text = DecimalFormat("##0.00").format(durationRatingArray[i].score) + "%"
            } else if (num == 2) {
                binding.tvDuration2nd.text = durationRatingArray[i].name.toString() + " Term"
                binding.tvDuration2Rating.text = DecimalFormat("##0.00").format(durationRatingArray[i].score) + "%"
            } else if (num == 3) {
                binding.tvDuration3rd.text = durationRatingArray[i].name.toString() + " Term"
                binding.tvDuration3Rating.text = DecimalFormat("##0.00").format(durationRatingArray[i].score) + "%"
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

        if (totalGoals!=0) {
            percentageBuying = ((nBuyingItem.toFloat() / totalGoals.toFloat()) * 100)
            percentageEvent = ((nEvent.toFloat() / totalGoals.toFloat()) * 100)
            percentageEmergency = ((nEmergency.toFloat() / totalGoals.toFloat()) * 100)
            percentageSituational = ((nSituational.toFloat() / totalGoals.toFloat()) * 100)
            percentageDonating = ((nCharity.toFloat() / totalGoals.toFloat()) * 100)
        }



        val entries = listOf(
            PieEntry(percentageBuying, "Buying Items"),
            PieEntry(percentageEvent, "Planning An Event"),
            PieEntry(percentageEmergency, "Saving For Emergency Funds"),
            PieEntry(percentageSituational, "Situational Shopping"),
            PieEntry(percentageDonating, "Donating To Charity"),
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

        val categoryRatingArray = ArrayList<SavingPerformanceActivity.DurationRating>()

        categoryRatingArray.add(
            SavingPerformanceActivity.DurationRating(
                "Buying Items",
                percentageBuying
            )
        )
        categoryRatingArray.add(
            SavingPerformanceActivity.DurationRating(
                "Planning an Event",
                percentageEvent
            )
        )
        categoryRatingArray.add(
            SavingPerformanceActivity.DurationRating(
                "Saving for Emergency Funds",
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
                "Donating to Charity",
                percentageDonating
            )
        )

        categoryRatingArray.sortByDescending{it.score}

        for (i in categoryRatingArray.indices) {
            var num = i + 1

            if (num == 1) {
                binding.tvTopPerformingActivity.text = categoryRatingArray[i].name
                binding.tvTopPerformingActivityPercentage.text = DecimalFormat("##0.00").format(categoryRatingArray[i].score) + "%"
            } else if (num == 2) {
                binding.tvActivity2nd.text = categoryRatingArray[i].name
                binding.tvConcept2Activity.text = DecimalFormat("##0.00").format(categoryRatingArray[i].score) + "%"
            } else if (num == 3) {
                binding.tvActivity3rd.text = categoryRatingArray[i].name
                binding.tvActivity3Rating.text = DecimalFormat("##0.00").format(categoryRatingArray[i].score) + "%"
            } else if (num == 4) {
                binding.tvActivity4th.text = categoryRatingArray[i].name
                binding.tvActivity4Rating.text = DecimalFormat("##0.00").format(categoryRatingArray[i].score) + "%"
            } else if (num == 5) {
                binding.tvActivity5th.text = categoryRatingArray[i].name
                binding.tvActivity5Rating.text = DecimalFormat("##0.00").format(categoryRatingArray[i].score) + "%"
                specificCategory = categoryRatingArray[i].name.toString()
            }
        }
    }

    private fun showGoalDialog() {

        var dialogBinding= DialogParentSavingTipsBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

//        dialogBinding.btnGotIt.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialogBinding.btnReviewGoals.setOnClickListener {
//            dialog.dismiss()
//            var goToGoalSetting = Intent(requireContext().applicationContext, FinancialActivity::class.java)
//            this.startActivity(goToGoalSetting)
//        }
//
//        dialogBinding.btnSetNewGoal.setOnClickListener {
//            dialog.dismiss()
//            var goToNewGoal = Intent(requireContext().applicationContext, NewGoal::class.java)
//            var bundle = Bundle()
//            // TODO update source
//            bundle.putString("source", "childFinancialActivity")
//            goToNewGoal.putExtras(bundle)
//            goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            this.startActivity(goToNewGoal)
//        }

        dialog.show()
    }

    private fun showDurationDialog() {

        var dialogBinding= DialogParentSavingDurationTipsBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);

//        if (specificDuration == "Short") {
//            dialogBinding.tvTitle.text = "Short Term"
//            dialogBinding.tvDefinition.text = "1. Can be achieved in a short amount of time and have \n" + "2. Duration: Less than 2 weeks"
//            dialogBinding.tvExamples.text = "1. Saving for a Fried Chicken Sandwich\n" +
//                    "2. Saving for a Small Toy\n" +
//                    "3. Saving for a Book"
//            dialogBinding.tvTips.text = "1. Set aside money consistently\n" +
//                    "2. Keep the target date in mind"
//        }  else if (specificDuration == "Medium") {
//            dialogBinding.tvTitle.text = "Medium Term"
//            dialogBinding.tvDefinition.text = "1. Takes a longer time to achieve and usually involves bigger target amounts \n" +
//                    "2. Duration: 2 to 4 weeks"
//            dialogBinding.tvExamples.text = "1. Saving for school supplies\n" +
//                    "2. Saving for a larger toy"
//            dialogBinding.tvTips.text = "1. Set aside money consistently\n" +
//                    "2. Earn extra by helping with chores and selling items\n" +
//                    "3. Keep the target date in mind"
//        } else if (specificDuration == "Long") {
//            dialogBinding.tvTitle.text = "Long Term"
//            dialogBinding.tvDefinition.text = "1. Takes a long time to achieve and involves bigger target amounts \n" +
//                    "2. Duration: Over a month"
//            dialogBinding.tvExamples.text = "1. Saving for a trip \n" +
//                    "2. Saving for a birthday party"
//            dialogBinding.tvTips.text = "1. SStart saving early. Even if the target date may seem far away, target amounts for long term goals tend to be larger.\n" +
//                    "2. Set aside money consistently\n" +
//                    "3. Earn extra by helping with chores and selling items"
//        }

        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

//        dialogBinding.btnGotIt.setOnClickListener {
//            dialog.dismiss()
//        }

        dialog.show()
    }

    private fun showCategoryDialog() {

        var dialogBinding= DialogParentSavingCategoryTipsBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);

//        if (specificCategory == "Buying Items") {
//            dialogBinding.tvTitle.text = "Buying Items"
//            dialogBinding.tvDefinition.text = "Purchasing things such as goods or services."
//            dialogBinding.tvExamples.text = "1. Buying a toy\n" +
//                    "2. Buying a book \n"
//            dialogBinding.tvTips.text = "1. Set aside money consistently.\n" +
//                    "2. Keep your target date in mind.\n"
//        } else if (specificCategory == "Planning An Event") {
//            dialogBinding.tvTitle.text = "Planning An Event"
//            dialogBinding.tvDefinition.text = "Organizing an event and ensuring that all needed materials or services are accounted for."
//            dialogBinding.tvExamples.text = "1. Birthday party\n" +
//                    "2. Out of town trip"
//            dialogBinding.tvTips.text = "1. Set aside money consistently.\n" +
//                    "2. Start saving early. Goals related to planning events tend to have larger target amounts.\n" +
//                    "3. Keep your target date in mind."
//
//        } else if (specificCategory == "Saving For Emergency Funds") {
//            dialogBinding.tvTitle.text = "Saving For Emergency Funds"
//            dialogBinding.tvDefinition.text = "1. Saving money to be used in the future for unexpected situations \n" +
//                    "2. Important to be prepared for these situations"
//            dialogBinding.tvExamples.text = "1. Loss of valuables \n" +
//                    "2. Unexpected expenses"
//            dialogBinding.tvTips.text = "1. Set aside money consistently.\n" +
//                    "2. Think about your future."
//        } else if (specificCategory == "Donating To Charity") {
//            dialogBinding.tvTitle.text = "Donating To Charity"
//            dialogBinding.tvDefinition.text = "1. Giving money to a non-profit organization to support an advocacy.\n" +
//                    "2. Important to think about others"
//            dialogBinding.tvExamples.text = "1. Donating to a church \n" +
//                    "2. Donating to Red Cross"
//            dialogBinding.tvTips.text = "1. Donate to an organization that you share an advocacy with \n" +
//                    "2. Set aside money consistently"
//        } else if (specificCategory == "Situational Shopping") {
//            dialogBinding.tvTitle.text = "Situational Shopping"
//            dialogBinding.tvDefinition.text = "Shopping for a certain event or happening."
//            dialogBinding.tvExamples.text = "1. Grocery shopping \n" +
//                    "2. Back to school shopping"
//            dialogBinding.tvTips.text = "1. Set aside money consistently. \n" +
//                    "2. Keep your target date in mind."
//        }

        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

//        dialogBinding.btnGotIt.setOnClickListener {
//            dialog.dismiss()
//        }

        dialog.show()
    }
}