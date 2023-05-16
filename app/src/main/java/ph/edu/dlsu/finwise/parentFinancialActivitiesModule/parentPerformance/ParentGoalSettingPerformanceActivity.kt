package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentPerformance

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.financialActivitiesModule.performance.GoalSettingPerformanceActivity
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityParentGoalSettingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.DialogParentSmartConceptTipBinding
import ph.edu.dlsu.finwise.databinding.DialogParentSmartTipBinding
import ph.edu.dlsu.finwise.model.GoalRating
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentFinancialActivity
import java.text.DecimalFormat

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
    private lateinit var childID: String

    data class Rating(var name: String? = null, var score: Int = 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentGoalSettingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle: Bundle = intent.extras!!
        childID = bundle.getString("childID").toString()

        print("print child ID " + childID)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav_parent), this, R.id.nav_goal)

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        initializeRating()

        binding.btnViewSMARTGoalsTip.setOnClickListener{
            showGoalDialog()
        }

        binding.btnConceptTip.setOnClickListener{
            showIndividualDialog()
        }

    }

    private fun RatingObject(name: String, score: Float): GoalSettingPerformanceActivity.Rating {
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
            binding.tvOverallRating.text ="${DecimalFormat("0.0").format(overall)}/5.0"
            var percentage = (overall / 5) * 100

            print("print overll over rating " + nOverall + "/" + nRatings)

            if (percentage >= 96) {
                binding.imgFace.setImageResource(R.drawable.excellent)
                binding.tvPerformanceStatus.text = "Excellent"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                binding.tvPerformanceText.text = "Your child excels at goal setting. Encourage them to continue setting goals!"
            } else if (percentage < 96 && percentage >= 86) {
                binding.imgFace.setImageResource(R.drawable.amazing)
                binding.tvPerformanceStatus.text = "Amazing"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.amazing_green))
                binding.tvPerformanceText.text = "Your child is amazing at goal setting! Encourage them to keep setting goals!"
            } else if (percentage < 86 && percentage >= 76) {
                binding.imgFace.setImageResource(R.drawable.great)
                binding.tvPerformanceStatus.text = "Great"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                binding.tvPerformanceText.text = "Your child is doing a great job of setting goals! Encourage them to keep at it!"
            } else if (percentage < 76 && percentage >= 66) {
                binding.imgFace.setImageResource(R.drawable.good)
                binding.tvPerformanceStatus.text = "Good"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                binding.tvPerformanceText.text = "Your child is doing a good job of setting goals! Encourage them to continue following the SMART framework."
            } else if (percentage < 66 && percentage >= 56) {
                binding.imgFace.setImageResource(R.drawable.average)
                binding.tvPerformanceStatus.text = "Average"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                binding.tvPerformanceText.text = "Your child is doing a nice job of setting goals! Encourage them to follow the SMART framework."
            } else if (percentage < 56 && percentage >= 46) {
                binding.imgFace.setImageResource(R.drawable.nearly_there)
                binding.tvPerformanceStatus.text = "Nearly There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
                binding.tvPerformanceText.text = "Your child is nearly there! Encourage them to follow the SMART framework!"
            } else if (percentage < 46 && percentage >= 36) {
                binding.imgFace.setImageResource(R.drawable.almost_there)
                binding.tvPerformanceStatus.text = "Almost There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.almost_there_yellow))
                binding.tvPerformanceText.text = "Your child is almost there! Encourage them to follow the SMART framework! "
            } else if (percentage < 36 && percentage >= 26) {
                binding.imgFace.setImageResource(R.drawable.getting_there)
                binding.tvPerformanceStatus.text = "Getting There"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.getting_there_orange))
                binding.tvPerformanceText.text = "Your child is getting there! Encourage them to follow the SMART framework!"
            } else if (percentage < 26 && percentage >= 16) {
                binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                binding.tvPerformanceStatus.text = "Not Quite\nThere"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.not_quite_there_red))
                binding.tvPerformanceText.text = "Your child is not quite there yet! Encourage them to follow the SMART framework!"
            } else if (percentage < 15) {
                binding.imgFace.setImageResource(R.drawable.bad)
                binding.tvPerformanceStatus.text = "Needs\nImprovement"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Uh oh! Encourage your child to follow the SMART framework!"
            }

            val ratingArray = ArrayList<GoalSettingPerformanceActivity.Rating>()

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
                    binding.tvSMART5Rating.text =DecimalFormat("#.0").format(ratingArray[i].score) + "/5.0"
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

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnReviewGoals.setOnClickListener {
            dialog.dismiss()
            var goToGoalSetting = Intent(this, ParentFinancialActivity::class.java)
            this.startActivity(goToGoalSetting)
        }

        dialog.show()
    }

    private fun showIndividualDialog() {

        var dialogBinding= DialogParentSmartConceptTipBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);

        if (SMARTIndividual == "Specific") {
            dialogBinding.tvName.text = "Specific"
            dialogBinding.tvDefinition.text = "Specific goals are very clear with what should be achieved."
            dialogBinding.tvGuideQuestions.text = "1. Is the goal clear?\n" + "2. What is to be achieved?"
            dialogBinding.tvTips.text = "1. Encourage them to figure out what they want to accomplish.\n2. Help them put their goal into words."
        } else if (SMARTIndividual == "Measurable") {
            dialogBinding.tvName.text = "Measurable"
            dialogBinding.tvDefinition.text = "Measurable goals have target amounts. "
            dialogBinding.tvGuideQuestions.text = "1. Is there a way to measure the goal?\n" + "2. How much needs to be saved?\n" + "3. How will it be known that the goal has been achieved?"
            dialogBinding.tvTips.text = "1. It is important that a target amount be set.\n2. Ensure that this target amount is reasonable."
        } else if (SMARTIndividual == "Achievable") {
            dialogBinding.tvName.text = "Achievable"
            dialogBinding.tvDefinition.text = "Achievable goals are realistic."
            dialogBinding.tvGuideQuestions.text = "1. Is the goal achievable?\n" + "2. Is it possible to save or earn enough money?\n" + "3. Is the timeline realistic?"
            dialogBinding.tvTips.text = "1. Give them advice on whether or not it is realistic.\n2. Consider their current financial situation.\n3. Break their goals down into smaller ones."
        } else if (SMARTIndividual == "Relevant") {
            dialogBinding.tvName.text = "Relevant"
            dialogBinding.tvDefinition.text = "Relevant goals are important to you and with what you want to do."
            dialogBinding.tvGuideQuestions.text = "1.Is the goal important?\n" + "2. What is the reasoning behind the goal?"
            dialogBinding.tvTips.text = "1. Guide them in exploring their priorities.\n2. Encourage them to set goals that are meaningful to the."
        } else if (SMARTIndividual == "Time-Bound") {
            dialogBinding.tvName.text = "Time-Bound"
            dialogBinding.tvDefinition.text = "Time-bound goals have a target or end date."
            dialogBinding.tvGuideQuestions.text = "1. How long will it take for the goal to be accomplished?\n" + "2. When does the goal need to be completed?"
            dialogBinding.tvTips.text = "1. Take into account the child's schedule, energy levels, and experience to see if the timeline is appropriate.\n2. Guide them in setting a reasonable target date."
        }

        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1400)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}