package ph.edu.dlsu.finwise

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityGoalSettingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordDepositBinding
import ph.edu.dlsu.finwise.databinding.DialogSmartReviewBinding
import ph.edu.dlsu.finwise.model.GoalRating
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

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    data class Rating(var name: String? = null, var score: Int = 0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalSettingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeRating()

        binding.btnViewSMARTGoalsInfo.setOnClickListener{
            showGoalDialog()
        }
    }
    private fun RatingObject(name: String, score: Int): Rating {
        val rating = Rating()

        rating.name = name
        rating.score = score

        return rating
    }
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
            binding.tvOverallRating.text ="${overall}/5"
            var percentage = (overall / 5) * 100

            if (percentage >= 90) {
                binding.imgFace.setImageResource(R.drawable.excellent)
                binding.tvPerformanceStatus.text = "Excellent"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                binding.tvPerformanceText.text = "Keep up the excellent work! Goal Setting is your strong point. Keep setting those goals!"
            } else if (percentage < 90 && percentage >= 80) {
                binding.imgFace.setImageResource(R.drawable.great)
                binding.tvPerformanceStatus.text = "Great"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                binding.tvPerformanceText.text = "Great job! You are performing well. Keep setting those goals!"
            } else if (percentage < 80 && percentage >= 70) {
                binding.imgFace.setImageResource(R.drawable.good)
                binding.tvPerformanceStatus.text = "Good"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                binding.tvPerformanceText.text = "Good job! With a bit more dedication and effort, you’ll surely up your performance!"
            } else if (percentage < 70 && percentage >= 60) {
                binding.imgFace.setImageResource(R.drawable.average)
                binding.tvPerformanceStatus.text = "Average"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                binding.tvPerformanceText.text = "Nice work! Work on improving your goal setting performance. Review SMART Goals!"
            } else if (percentage < 60) {
                binding.imgFace.setImageResource(R.drawable.bad)
                binding.tvPerformanceStatus.text = "Bad"
                binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Uh oh! You need to work on your goal setting.  Click review to learn how!"
            }

            val ratingArray = ArrayList<Rating>()

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

    private fun showGoalDialog() {

        var dialogBinding= DialogSmartReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}