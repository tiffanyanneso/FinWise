package ph.edu.dlsu.finwise.financialActivitiesModule.performance

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityGoalSettingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.DialogSmartIndividualBinding
import ph.edu.dlsu.finwise.databinding.DialogSmartReviewBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.NewGoal
import ph.edu.dlsu.finwise.model.GoalRating
import java.text.DecimalFormat
import kotlin.concurrent.timer
import kotlin.math.roundToInt

class GoalSettingPerformanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalSettingPerformanceBinding
    private var firestore = Firebase.firestore
    private var nRatings = 0
    private var nOverall = 0.00F
    private var nSpecific = 0.00F
    private var nMeasurable = 0.00F
    private var nAchievable = 0.00F
    private var nRelevant = 0.00F
    private var nTimeBound = 0.00F

    private var SMARTIndividual = "Specific"

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    data class Rating(var name: String? = null, var score: Float = 0.00F)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalSettingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        initializeRating()
        loadBackButton()

        binding.btnViewSMARTGoalsInfo.setOnClickListener{
            showGoalDialog()
        }

        binding.btnReviewConcept.setOnClickListener{
            showIndividualDialog()
        }
    }
    private fun RatingObject(name: String, score: Float): Rating {
        val rating = Rating()

        rating.name = name
        rating.score = score

        return rating
    }
    @SuppressLint("SetTextI18n")
    private fun initializeRating() {
        firestore.collection("GoalRating").whereEqualTo("childID", currentUser).get().addOnSuccessListener { results ->
            nRatings = results.size()
            for (rating in results) {
                var ratingObject = rating.toObject<GoalRating>()
                nOverall += ratingObject.overallRating!!
                nSpecific += ratingObject.specific!!
                nMeasurable += ratingObject.measurable!!
                nAchievable +=ratingObject.achievable!!
                nRelevant +=ratingObject.relevant!!
                nTimeBound += ratingObject.timeBound!!
            }
        }.continueWith {
            val overall = nOverall/nRatings
            binding.tvOverallRating.text ="${DecimalFormat("0.0").format(overall)}/5.0"
            var percentage = (overall / 5) * 100

            if (percentage >= 96) {
                binding.imgFace.setImageResource(R.drawable.excellent)
                binding.tvPerformanceStatus.text = "Excellent"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                binding.tvPerformanceText.text = "Excellent work! Goal Setting is your strong point. Keep setting SMART goals!"
            } else if (percentage < 96 && percentage >= 86) {
                binding.imgFace.setImageResource(R.drawable.amazing)
                binding.tvPerformanceStatus.text = "Amazing"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                binding.tvPerformanceText.text = "Amazing job! Goal Setting is your strong point. Keep setting SMART goals!"
            } else if (percentage < 86 && percentage >= 76) {
                binding.imgFace.setImageResource(R.drawable.great)
                binding.tvPerformanceStatus.text = "Great"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                binding.tvPerformanceText.text = "Great job! You are performing well. Keep setting SMART goals!"
            } else if (percentage < 76 && percentage >= 66) {
                binding.imgFace.setImageResource(R.drawable.good)
                binding.tvPerformanceStatus.text = "Good"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                binding.tvPerformanceText.text = "Good job! By reviewing what SMART goals are, you’ll surely up your performance!"
            } else if (percentage < 66 && percentage >= 56) {
                binding.imgFace.setImageResource(R.drawable.average)
                binding.tvPerformanceStatus.text = "Average"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                binding.tvPerformanceText.text = "Nice work! Work on improving your goal setting performance. Set SMART Goals!"
            } else if (percentage < 56 && percentage >= 46) {
                binding.imgFace.setImageResource(R.drawable.nearly_there)
                binding.tvPerformanceStatus.text = "Nearly There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "You're nearly there! Improve your SMART goal setting to get there!"
            } else if (percentage < 46 && percentage >= 36) {
                binding.imgFace.setImageResource(R.drawable.almost_there)
                binding.tvPerformanceStatus.text = "Almost There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Almost there! You need to improve your SMART goal setting!"
            } else if (percentage < 36 && percentage >= 26) {
                binding.imgFace.setImageResource(R.drawable.getting_there)
                binding.tvPerformanceStatus.text = "Getting There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Getting there! You need to improve your SMART goal setting!"
            } else if (percentage < 26 && percentage >= 16) {
                binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                binding.tvPerformanceStatus.text = "Not Quite\nThere"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Not quite there yet! Don't give up. Improve your SMART goal setting!"
            } else if (percentage < 15) {
                binding.imgFace.setImageResource(R.drawable.bad)
                binding.tvPerformanceStatus.text = "Needs\nImprovement"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Uh oh! You need to work on your SMART goal setting!"
            }

            val ratingArray = ArrayList<Rating>()

            ratingArray.add(RatingObject("Specific", (nSpecific/nRatings.toFloat())))
            ratingArray.add(RatingObject("Measurable", (nMeasurable/nRatings.toFloat())))
            ratingArray.add(RatingObject("Achievable", (nAchievable/nRatings.toFloat())))
            ratingArray.add(RatingObject("Relevant", (nRelevant/nRatings.toFloat())))
            ratingArray.add(RatingObject("Time Bound", (nTimeBound/nRatings.toFloat())))

            ratingArray.sortByDescending{it.score}

            for (i in ratingArray.indices) {
                var num = i + 1

                if (num == 1) {
                    binding.tvTopPerformingSMART.text = ratingArray[i].name
                    binding.tvTopPerformingRating.text = DecimalFormat("#.0").format(ratingArray[i].score) + "/5.0"
                } else if (num == 2) {
                    binding.tvSMART2nd.text = ratingArray[i].name
                    binding.tvConcept2Rating.text = DecimalFormat("#.0").format(ratingArray[i].score) + "/5.0"
                } else if (num == 3) {
                    binding.tvSMART3rd.text = ratingArray[i].name
                    binding.tvSMART3Rating.text = DecimalFormat("#.0").format(ratingArray[i].score) + "/5.0"
                } else if (num == 4) {
                    binding.tvSMART4th.text = ratingArray[i].name
                    binding.tvSMART4Rating.text = DecimalFormat("#.0").format(ratingArray[i].score) + "/5.0"
                } else if (num == 5) {
                    binding.tvSMART5th.text = ratingArray[i].name
                    binding.tvSMART5Rating.text = DecimalFormat("#.0").format(ratingArray[i].score) + "/5.0"
                    SMARTIndividual =  ratingArray[i].name.toString()
                }
            }

//            binding.progressBarSpecific.progress = (nSpecific/nRatings).roundToInt()
//            binding.ratingSpecific.text = "${(nSpecific/nRatings)}/5"
//            binding.progressBarMeasurable.progress = (nMeasurable/nRatings).roundToInt()
//            binding.ratingMeasurable.text = "${(nMeasurable/nRatings)}/5"
//            binding.progressBarAchievable.progress = (nAchievable/nRatings).roundToInt()
//            binding.ratingAchievable.text = "${(nAchievable/nRatings)}/5"
//            binding.progressBarRelevant.progress = (nRelevant/nRatings).roundToInt()
//            binding.ratingRelevant.text = "${(nRelevant/nRatings)}/5"
//            binding.progressBarTime.progress = (nTimeBound/nRatings).roundToInt()
//            binding.ratingTime.text = "${(nTimeBound/nRatings)}/5"
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun showGoalDialog() {

        var dialogBinding= DialogSmartReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

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

        dialog.show()
    }

    private fun showIndividualDialog() {

        var dialogBinding= DialogSmartIndividualBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);

        if (SMARTIndividual == "Specific") {
            dialogBinding.tvName.text = "Specific"
            dialogBinding.tvDefinition.text = "Specific goals are very clear with what should be achieved."
            dialogBinding.tvGuideQuestions.text = "1. What do I want to achieve?\n2. Is the goal clear?\n3. Have I included everything I want to achieve?"
        } else if (SMARTIndividual == "Measurable") {
            dialogBinding.tvName.text = "Measurable"
            dialogBinding.tvDefinition.text = "Measurable goals have target amounts. "
            dialogBinding.tvGuideQuestions.text = "1. Can my goal be measured?\n" + "2. How much do I need to save?\n" + "3. Have I indicated a target amount?"
        } else if (SMARTIndividual == "Achievable") {
            dialogBinding.tvName.text = "Achievable"
            dialogBinding.tvDefinition.text = "Achievable goals are realistic."
            dialogBinding.tvGuideQuestions.text = "1. Can I achieve the goal on or before the target date?\n" + "2. Will I be able to save or earn enough money?\n"
        } else if (SMARTIndividual == "Relevant") {
            dialogBinding.tvName.text = "Relevant"
            dialogBinding.tvDefinition.text = "Relevant goals are important to you and with what you want to do."
            dialogBinding.tvGuideQuestions.text = "1.Is this goal important to me?\n" + "2. Why do I want to achieve this goal?\n3. How will this goal benefit me?"
        } else if (SMARTIndividual == "Time-Bound") {
            dialogBinding.tvName.text = "Time-Bound"
            dialogBinding.tvDefinition.text = "Time-bound goals have a target or end date."
            dialogBinding.tvGuideQuestions.text = "1. How long will it take me to complete this goal?\n" + "2. Can I complete this goal on or before the target date?"
        }

        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1000)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}