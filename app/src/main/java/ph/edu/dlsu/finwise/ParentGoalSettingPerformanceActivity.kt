package ph.edu.dlsu.finwise

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityGoalSettingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.ActivityParentGoalSettingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.DialogParentSmartConceptTipBinding
import ph.edu.dlsu.finwise.databinding.DialogParentSmartTipBinding
import ph.edu.dlsu.finwise.databinding.DialogSmartIndividualBinding
import ph.edu.dlsu.finwise.databinding.DialogSmartReviewBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.NewGoal
import ph.edu.dlsu.finwise.model.GoalRating
import kotlin.math.roundToInt

class ParentGoalSettingPerformanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParentGoalSettingPerformanceBinding

    private var firestore = Firebase.firestore
    private var nRatings = 0
    private var nOverall = 0.00F
    private var nSpecific = 0.00F
    private var nMeasurable = 0.00F
    private var nAchievable = 0.00F
    private var nRelevant = 0.00F
    private var nTimeBound = 0.00F

    private var SMARTIndividual = "Specific"
    private lateinit var childID:String

    data class Rating(var name: String? = null, var score: Int = 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentGoalSettingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = Bundle()
        childID = bundle.getString("childID").toString()

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav_parent), this, R.id.nav_goal)

        initializeRating()

        binding.btnViewSMARTGoalsTip.setOnClickListener{
            showGoalDialog()
        }

        binding.btnConceptTip.setOnClickListener{
            showIndividualDialog()
        }

    }

    private fun RatingObject(name: String, score: Int): GoalSettingPerformanceActivity.Rating {
        val rating = GoalSettingPerformanceActivity.Rating()

        rating.name = name
        rating.score = score

        return rating
    }
    private fun initializeRating() {
        firestore.collection("GoalRating").whereEqualTo("childID", childID).get().addOnSuccessListener { results ->
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
            binding.tvOverallRating.text ="${overall}/5"
            var percentage = (overall / 5) * 100

            if (percentage >= 96) {
                binding.imgFace.setImageResource(R.drawable.excellent)
                binding.tvPerformanceStatus.text = "Excellent"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                binding.tvPerformanceText.text = "Your child excels at goal setting. Encourage them to continue."
            } else if (percentage < 96 && percentage >= 86) {
                binding.imgFace.setImageResource(R.drawable.amazing)
                binding.tvPerformanceStatus.text = "Amazing"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                binding.tvPerformanceText.text = "Your child is amazing at goal setting! Encourage them to keep setting those goals!"
            } else if (percentage < 86 && percentage >= 76) {
                binding.imgFace.setImageResource(R.drawable.great)
                binding.tvPerformanceStatus.text = "Great"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                binding.tvPerformanceText.text = "Your child is doing a great job of setting goals!"
            } else if (percentage < 76 && percentage >= 66) {
                binding.imgFace.setImageResource(R.drawable.good)
                binding.tvPerformanceStatus.text = "Good"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                binding.tvPerformanceText.text = "Your child is doing a good job of setting goals! Encourage them to follow the SMART framework."
            } else if (percentage < 66 && percentage >= 56) {
                binding.imgFace.setImageResource(R.drawable.average)
                binding.tvPerformanceStatus.text = "Average"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                binding.tvPerformanceText.text = "Your child is doing a nice job of setting goals! Encourage them to follow the SMART framework."
            } else if (percentage < 56 && percentage >= 46) {
                binding.imgFace.setImageResource(R.drawable.nearly_there)
                binding.tvPerformanceStatus.text = "Nearly There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Your child is nearly there! Click the tips button to learn how to help them get there!"
            } else if (percentage < 46 && percentage >= 36) {
                binding.imgFace.setImageResource(R.drawable.almost_there)
                binding.tvPerformanceStatus.text = "Almost There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Your child is almost there! Click the tips button to learn how to help them get there!"
            } else if (percentage < 36 && percentage >= 26) {
                binding.imgFace.setImageResource(R.drawable.getting_there)
                binding.tvPerformanceStatus.text = "Getting There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Your child is getting there! Click the tips button to learn how to help them get there!"
            } else if (percentage < 26 && percentage >= 16) {
                binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                binding.tvPerformanceStatus.text = "Not Quite There Yet"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Your child is not quite there yet! Click the tips button to learn how to help them get there!"
            } else if (percentage < 15) {
                binding.imgFace.setImageResource(R.drawable.bad)
                binding.tvPerformanceStatus.text = "Needs Improvement"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Uh oh! Click the tips button to learn how to help them improve their goal setting!"
            }

            val ratingArray = ArrayList<GoalSettingPerformanceActivity.Rating>()

            ratingArray.add(RatingObject("Specific", (nSpecific/nRatings).roundToInt()))
            ratingArray.add(RatingObject("Measurable", (nMeasurable/nRatings).roundToInt()))
            ratingArray.add(RatingObject("Achievable", (nAchievable/nRatings).roundToInt()))
            ratingArray.add(RatingObject("Relevant", (nRelevant/nRatings).roundToInt()))
            ratingArray.add(RatingObject("Time Bound", (nTimeBound/nRatings).roundToInt()))

            ratingArray.sortByDescending{it.score}

            for (i in ratingArray.indices) {
                var num = i + 1

                if (num == 1) {
                    binding.tvTopPerformingSMART.text = ratingArray[i].name
                    binding.tvTopPerformingRating.text = ratingArray[i].score.toString() + "/5"
                } else if (num == 2) {
                    binding.tvSMART2nd.text = ratingArray[i].name
                    binding.tvConcept2Rating.text = ratingArray[i].score.toString() + "/5"
                } else if (num == 3) {
                    binding.tvSMART3rd.text = ratingArray[i].name
                    binding.tvSMART3Rating.text = ratingArray[i].score.toString() + "/5"
                } else if (num == 4) {
                    binding.tvSMART4th.text = ratingArray[i].name
                    binding.tvSMART4Rating.text = ratingArray[i].score.toString() + "/5"
                } else if (num == 5) {
                    binding.tvSMART5th.text = ratingArray[i].name
                    binding.tvSMART5Rating.text = ratingArray[i].score.toString() + "/5"
                    SMARTIndividual =  ratingArray[i].name.toString()
                }
            }
        }
    }

    private fun showGoalDialog() {

        var dialogBinding= DialogParentSmartTipBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

//        dialogBinding.btnGotIt.setOnClickListener {
//            dialog.dismiss()
//        }

//        dialogBinding.btnReviewGoals.setOnClickListener {
//            dialog.dismiss()
//            var goToGoalSetting = Intent(this, FinancialActivity::class.java)
//            this.startActivity(goToGoalSetting)
//        }

//        dialogBinding.btnSetNewGoal.setOnClickListener {
//            dialog.dismiss()
//            var goToNewGoal = Intent(this, NewGoal::class.java)
//            var bundle = Bundle()
//            bundle.putString("source", "childFinancialActivity")
//            goToNewGoal.putExtras(bundle)
//            goToNewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            this.startActivity(goToNewGoal)
//        }

        dialog.show()
    }

    private fun showIndividualDialog() {

        var dialogBinding= DialogParentSmartConceptTipBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);

//        if (SMARTIndividual == "Specific") {
//            dialogBinding.tvName.text = "Specific"
//            dialogBinding.tvDefinition.text = "Specific goals are very clear with what should be achieved."
//            dialogBinding.tvGuideQuestions.text = "1. Is the goal clear?\n" + "2. What do I want to achieve?"
//        } else if (SMARTIndividual == "Measurable") {
//            dialogBinding.tvName.text = "Measurable"
//            dialogBinding.tvDefinition.text = "Measurable goals have target amounts. "
//            dialogBinding.tvGuideQuestions.text = "1. Is there a way to measure the goal?\n" + "2. How much do I need to save?\n" + "3. How will I know if I have achieved the goal?"
//        } else if (SMARTIndividual == "Achievable") {
//            dialogBinding.tvName.text = "Achievable"
//            dialogBinding.tvDefinition.text = "Achievable goals are realistic."
//            dialogBinding.tvGuideQuestions.text = "1. Can I achieve the goal?\n" + "2. Will I be able to save or earn enough money?\n" + "3. Do I have enough time to achieve the goal?"
//        } else if (SMARTIndividual == "Relevant") {
//            dialogBinding.tvName.text = "Relevant"
//            dialogBinding.tvDefinition.text = "Relevant goals are important to you and with what you want to do."
//            dialogBinding.tvGuideQuestions.text = "1.Is this goal important to me?\n" + "2. Why do I want to achieve this goal?"
//        } else if (SMARTIndividual == "Time-Bound") {
//            dialogBinding.tvName.text = "Time-Bound"
//            dialogBinding.tvDefinition.text = "Time-bound goals have a target or end date."
//            dialogBinding.tvGuideQuestions.text = "1. How long will it take me to complete this goal?\n" + "2. When do I need to complete this goal?"
//        }

        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 700)

//        dialogBinding.btnGotIt.setOnClickListener {
//            dialog.dismiss()
//        }

        dialog.show()
    }
}