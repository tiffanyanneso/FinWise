package ph.edu.dlsu.finwise.financialActivitiesModule

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
import ph.edu.dlsu.finwise.GoalSettingPerformanceActivity
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivitySavingPerformanceBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import kotlin.math.roundToInt

class SavingPerformanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavingPerformanceBinding
    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private var ongoingGoals = 0
    //includes achieved goals in count
    private var totalGoals = 0

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

    data class DurationRating(var name: String? = null, var score: Int = 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getGoals()
        loadBackButton()
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
                    "Earning Money" -> nEarning++
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

            if (overall >= 90) {
                binding.imgFace.setImageResource(R.drawable.excellent)
                binding.tvPerformanceStatus.text = "Excellent"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                binding.tvPerformanceText.text = "Keep up the excellent work! Saving is your strong point. Keep completing those goals!"
            } else if (overall < 90 && overall >= 80) {
                binding.imgFace.setImageResource(R.drawable.great)
                binding.tvPerformanceStatus.text = "Great"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                binding.tvPerformanceText.text = "Great job! You are performing well. Keep completing those goals!"
            } else if (overall < 80 && overall >= 70) {
                binding.imgFace.setImageResource(R.drawable.good)
                binding.tvPerformanceStatus.text = "Good"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                binding.tvPerformanceText.text = "Good job! With a bit more dedication and effort, you’ll surely up your performance!"
            } else if (overall < 70 && overall >= 60) {
                binding.imgFace.setImageResource(R.drawable.average)
                binding.tvPerformanceStatus.text = "Average"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                binding.tvPerformanceText.text = "Nice work! Work on improving your saving performance through time and effort. You’ll get there soon!"
            } else if (overall < 60) {
                binding.imgFace.setImageResource(R.drawable.bad)
                binding.tvPerformanceStatus.text = "Bad"
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
                binding.tvTopPerformingDuration.text = durationRatingArray[i].name
                binding.tvTopPerformingRating.text = durationRatingArray[i].score.toString() + "%"
            } else if (num == 2) {
                binding.tvDuration2nd.text = durationRatingArray[i].name
                binding.tvDuration2Rating.text = durationRatingArray[i].score.toString() + "%"
            } else if (num == 3) {
                binding.tvDuration3rd.text = durationRatingArray[i].name
                binding.tvDuration3Rating.text = durationRatingArray[i].score.toString() + "%"
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
            } else if (num == 6) {
                binding.tvActivity6th.text = categoryRatingArray[i].name
                binding.tvActivity6Rating.text = categoryRatingArray[i].score.toString() + "%"
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
}