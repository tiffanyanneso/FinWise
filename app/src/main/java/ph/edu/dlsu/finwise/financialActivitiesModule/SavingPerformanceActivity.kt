package ph.edu.dlsu.finwise.financialActivitiesModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivitySavingPerformanceBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat

class SavingPerformanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavingPerformanceBinding
    private var firestore = Firebase.firestore

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
            
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getGoals()
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

        binding.progressBarShortTerm.progress = percentageShort.toInt()
        binding.percentageShortTerm.text = DecimalFormat("###0.00").format(percentageShort) + "%"
        binding.progressBarMediumTerm.progress = percentageMedium.toInt()
        binding.percentageMediumTerm.text = DecimalFormat("###0.00").format(percentageMedium) + "%"
        binding.progressBarLongTerm.progress = percentageLong.toInt()
        binding.percentageLongTerm.text =DecimalFormat("###0.00").format(percentageLong) + "%"
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

        binding.progressBarBuyingItems.progress = percentageBuying.toInt()
        binding.percentageBuyingItems.text = DecimalFormat("###0.00").format(percentageBuying) + "%"
        binding.progressBarPlanningEvent.progress = percentageEvent.toInt()
        binding.percentagePlanningEvent.text = DecimalFormat("###0.00").format(percentageEvent) + "%"
        binding.progressBarEmergencyFunds.progress = percentageEmergency.toInt()
        binding.percentageEmergencyFunds.text =DecimalFormat("###0.00").format(percentageEmergency) + "%"
        binding.progressBarSituationalShopping.progress = percentageSituational.toInt()
        binding.percentageSituationalShopping.text = DecimalFormat("###0.00").format(percentageSituational) + "%"
        binding.progressBarDonating.progress = percentageDonating.toInt()
        binding.percentageDonating.text = DecimalFormat("###0.00").format(percentageDonating) + "%"
        binding.progressBarEarningMoney.progress = percentageEarning.toInt()
        binding.percentageEarningMoney.text =DecimalFormat("###0.00").format(percentageEarning) + "%"
    }
}