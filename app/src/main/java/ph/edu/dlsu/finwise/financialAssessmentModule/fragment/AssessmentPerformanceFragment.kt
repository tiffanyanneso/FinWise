package ph.edu.dlsu.finwise.financialAssessmentModule.fragment

import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.DialogParentAssessmentBinding
import ph.edu.dlsu.finwise.databinding.DialogSmartGoalInfoBinding
import ph.edu.dlsu.finwise.databinding.DialogSmartIndividualBinding
import ph.edu.dlsu.finwise.databinding.FragmentAssessmentPerformanceBinding
import ph.edu.dlsu.finwise.model.FinancialAssessmentAttempts
import ph.edu.dlsu.finwise.model.FinancialAssessmentDetails

class AssessmentPerformanceFragment : Fragment() {
    private lateinit var binding: FragmentAssessmentPerformanceBinding
    private var firestore = Firebase.firestore
    private lateinit var childID: String
    private lateinit var user: String
    private var mediaPlayer: MediaPlayer? = null

    private val assessmentsTaken = ArrayList<FinancialAssessmentAttempts>()
    private var financialGoalsPercentage = 0.00F
    private var financialGoalsScores = ArrayList<Double?>()
    private var savingPercentage = 0.00F
    private var savingScores = ArrayList<Double?>()
    private var budgetingPercentage = 0.00F
    private var budgetingScores = ArrayList<Double?>()
    private var spendingPercentage = 0.00F
    private var spendingScores = ArrayList<Double?>()

    private var concept = "Goal Setting"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assessment_performance, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAssessmentPerformanceBinding.bind(view)
        binding.title.text = "Overall Assessment Performance"

        binding.btnReviewConcept.setOnClickListener {
            showParentAssessmentDialog()
        }

        getBundles()
        getAssessments()
    }

    private fun getBundles() {
        childID = arguments?.getString("childID").toString()
        user = arguments?.getString("user").toString()

    }

    private fun getAssessments() {
        firestore.collection("AssessmentAttempts").whereEqualTo("childID", childID)
            .get().addOnSuccessListener { documents ->
                for (assessments in documents) {
                    val assessmentObject = assessments.toObject<FinancialAssessmentAttempts>()
                    assessmentsTaken.add(assessmentObject)
                }
            }.continueWith { getScores() }
    }


    private fun getScores() {
        CoroutineScope(Dispatchers.Main).launch {
            if (assessmentsTaken.isNotEmpty()) {
                for (assessment in assessmentsTaken) {
                    val assessmentDocument = firestore.collection("Assessments")
                        .document(assessment.assessmentID!!).get().await()
                    val assessmentObject = assessmentDocument.toObject<FinancialAssessmentDetails>()

                    val percentage = getPercentage(assessment)
                    when (assessmentObject?.assessmentCategory) {
                        "Goal Setting" -> financialGoalsScores.add(percentage)
                        "Saving" -> savingScores.add(percentage)
                        "Budgeting" -> budgetingScores.add(percentage)
                        "Spending" -> spendingScores.add(percentage)
                    }
                }
                computeForPercentages()
            } else setEmptyAssessmentText()
        }

    }

    private fun computeForPercentages() {
        val maxScore = 100
        val savingPercentageSum = savingScores.sumOf { it ?: 0.0 }
        savingPercentage = ((savingPercentageSum / (maxScore * savingScores.size)) * 100).toFloat()
        val spendingPercentageSum = spendingScores.sumOf { it ?: 0.0 }
        spendingPercentage = ((spendingPercentageSum / (maxScore * spendingScores.size)) * 100).toFloat()
        val budgetingPercentageSum = budgetingScores.sumOf { it ?: 0.0 }
        budgetingPercentage = ((budgetingPercentageSum / (maxScore * budgetingScores.size)) * 100).toFloat()
        val financialGoalsPercentageSum = financialGoalsScores.sumOf { it ?: 0.0 }
        financialGoalsPercentage = ((financialGoalsPercentageSum / (maxScore * financialGoalsScores.size)) * 100).toFloat()
        checkIfNaN()
        setPerformanceView()
        setRanking()
    }

    private fun checkIfNaN() {
        val percentages = mutableListOf(savingPercentage, spendingPercentage, budgetingPercentage,
            financialGoalsPercentage)

        for (i in percentages.indices) {
            if (percentages[i].isNaN()) {
                when (i) {
                    0 -> savingPercentage = 0.00f
                    1 -> spendingPercentage = 0.00f
                    2 -> budgetingPercentage = 0.00f
                    3 -> financialGoalsPercentage = 0.00f
                }
            }
        }
    }

    private fun getPercentage(assessment: FinancialAssessmentAttempts): Double? {
        val correctAnswers: Int? = assessment.nAnsweredCorrectly
        val totalAnswers: Int? = assessment.nQuestions

        val percentage = if(totalAnswers != null && correctAnswers != null && totalAnswers != 0) {
            (correctAnswers.toDouble() / totalAnswers.toDouble()) * 100
        } else {
            0.0
        }

        return percentage.coerceAtMost(100.0)
    }

    private fun setRanking() {
        val totals = mapOf(
            "Spending" to spendingPercentage,
            "Saving" to savingPercentage,
            "Goal Setting" to financialGoalsPercentage,
            "Budgeting" to budgetingPercentage
        )
        val topCategories = totals.entries.sortedByDescending { it.value }

        binding.tvTopPerformingConcept.text = topCategories[0].key
        binding.tvTopPerformingPercentage.text = String.format("%.1f%%", topCategories[0].value)

        binding.tvConcept2nd.text = topCategories[1].key
        binding.tvConcept2Percentage.text = String.format("%.1f%%", topCategories[1].value)

        binding.tvConcept3rd.text = topCategories[2].key
        binding.tvConcept3Percentage.text = String.format("%.1f%%", topCategories[2].value)

        binding.tvConcept4th.text = topCategories[3].key
        binding.tvConcept4Percentage.text = String.format("%.1f%%", topCategories[3].value)
        concept = topCategories[3].key

        binding.layoutLoading.visibility = View.GONE
        binding.layoutMain.visibility = View.VISIBLE

    }

    private fun setPerformanceView() {
        val totalSum = spendingPercentage + savingPercentage + financialGoalsPercentage + budgetingPercentage
        val maxPossibleSum = 4 * 100  // assuming the maximum possible value for each variable is 100

        val percentage = (totalSum.toDouble() / maxPossibleSum) * 100

        //Update to be used in the leaderboard
        /*firestore.collection("ChildUser").document(childID).update("assessmentPerformance", percentage)*/

        binding.textViewProgress.text = String.format("%.1f%%", percentage)

        if (user == "Child")
            setTextPerformanceChild(percentage)
        else setTextPerformanceParent(percentage)

    }

    private fun setTextPerformanceParent(percentage: Double) {
        //TODO: Change audio
        var audio = 0
        if (percentage >= 96) {
            binding.ivScore.setImageResource(R.drawable.excellent)
            binding.textViewPerformanceText.text = "Excellent"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.dark_green))
            binding.tvPerformanceText.text =
                "Excellent! Your child demonstrates a good understanding of key financial concepts!"
        } else if (percentage < 96 && percentage >= 86) {
            binding.ivScore.setImageResource(R.drawable.amazing)
            binding.textViewPerformanceText.text = "Amazing"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.amazing_green))
            binding.tvPerformanceText.text =
                "Great! Your child has a solid grasp of important financial concepts. Encourage them to keep up the good work!"
        } else if (percentage < 86 && percentage >= 76) {
            binding.ivScore.setImageResource(R.drawable.great)
            binding.textViewPerformanceText.text = "Great"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.green))
            binding.tvPerformanceText.text =
                "Great! Your child has a solid grasp of important financial concepts. Encourage them to keep up the good work!"
        } else if (percentage < 76 && percentage >= 66) {
            binding.ivScore.setImageResource(R.drawable.good)
            binding.textViewPerformanceText.text = "Good"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.light_green))
            binding.tvPerformanceText.text =
                "Good! Your child has a good grasp of financial concepts, but may make occasional mistakes and need some guidance."
        } else if (percentage < 66 && percentage >= 56) {
            binding.ivScore.setImageResource(R.drawable.average)
            binding.textViewPerformanceText.text = "Average"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.yellow))
            binding.tvPerformanceText.text =
                "Okay! Your child has good financial knowledge and makes good financial decisions."
        } else if (percentage < 56 && percentage >= 46) {
            binding.ivScore.setImageResource(R.drawable.nearly_there)
            binding.textViewPerformanceText.text = "Nearly There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            binding.tvPerformanceText.text =
                "Nearly there! Your child may need a bit of help and practice with financial concepts."
        }  else if (percentage < 46 && percentage >= 36) {
            binding.ivScore.setImageResource(R.drawable.almost_there)
            binding.textViewPerformanceText.text = "Almost There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.almost_there_yellow))
            binding.tvPerformanceText.text =
                "Almost there! Your child needs an extra push to better grasp financial concepts. Encourage them to continue."
        } else if (percentage < 36 && percentage >= 26) {
            binding.ivScore.setImageResource(R.drawable.getting_there)
            binding.textViewPerformanceText.text = "Getting There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.getting_there_orange))
            binding.tvPerformanceText.text =
                "Getting there! Your child needs an extra push to better grasp financial concepts. Encourage them to continue."
        } else if (percentage < 26 && percentage >= 16) {
            binding.ivScore.setImageResource(R.drawable.not_quite_there_yet)
            binding.textViewPerformanceText.text = "Not Quite There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.not_quite_there_red))
            binding.tvPerformanceText.text =
                "Not quite there! Your child needs an extra push to better grasp financial concepts. Encourage them to continue."
        } else if (percentage < 15) {
            binding.ivScore.setImageResource(R.drawable.bad)
            binding.textViewPerformanceText.text = "Needs Improvement"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.red))
            binding.tvPerformanceText.text =
                "Uh oh! Your child requires immediate education and guidance to improve their financial concept knowledge."
        }
    }

    private fun loadAudio(audio: Int) {
        binding.btnAudio.setOnClickListener {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, audio)
            }

            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                mediaPlayer?.seekTo(0)
                return@setOnClickListener
            }
            mediaPlayer?.start()
        }
    }

    override fun onPause() {
        releaseMediaPlayer()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
    }


    private fun releaseMediaPlayer() {
        mediaPlayer?.apply {
            if (isPlaying) {
                pause()
                seekTo(0)
            }
            stop()
            release()
        }
        mediaPlayer = null
    }

    private fun setTextPerformanceChild(percentage: Double) {
        //TODO: Change audio
        var audio = 0
        if (percentage >= 90) {
            audio = R.raw.assessment_performance_excellent
            binding.ivScore.setImageResource(R.drawable.excellent)
            binding.textViewPerformanceText.text = "Excellent"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.dark_green))
            binding.tvPerformanceText.text =
                "Keep up the excellent work! You have a strong understanding of financial concepts!"
            //showSeeMoreButton()
        } else if (percentage < 96 && percentage >= 86) {
            audio = R.raw.assessment_performance_amazing_great
            binding.ivScore.setImageResource(R.drawable.amazing)
            binding.textViewPerformanceText.text = "Amazing"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.amazing_green))
            binding.tvPerformanceText.text =
                "Great job! You have a good grasp of financial concepts!"
            //showSeeMoreButton()
        } else if (percentage < 86 && percentage >= 76) {
            audio = R.raw.assessment_performance_amazing_great
            binding.ivScore.setImageResource(R.drawable.great)
            binding.textViewPerformanceText.text = "Great"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.green))
            binding.tvPerformanceText.text =
                "Great! You have a good grasp of financial concepts!"
            //showReviewButton()
        } else if (percentage < 76 && percentage >= 66) {
            audio = R.raw.assessment_performance_good
            binding.ivScore.setImageResource(R.drawable.good)
            binding.textViewPerformanceText.text = "Good"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.light_green))
            binding.tvPerformanceText.text =
                "Good! You understand financial concepts. Keep improving!"
            //showSeeMoreButton()
        } else if (percentage < 66 && percentage >= 56) {
            audio = R.raw.assessment_performance_average
            binding.ivScore.setImageResource(R.drawable.average)
            binding.textViewPerformanceText.text = "Average"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.yellow))
            binding.tvPerformanceText.text =
                "Nice work! You have basic knowledge of financial concepts. Keep it up!"
            //showReviewButton()
        } else if (percentage < 56 && percentage >= 46) {
            audio = R.raw.assessment_performance_nearly_there
            binding.ivScore.setImageResource(R.drawable.nearly_there)
            binding.textViewPerformanceText.text = "Nearly There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            binding.tvPerformanceText.text =
                "Nearly there! You have some knowledge of financial concepts. Keep it up!"
            //showReviewButton()
        }  else if (percentage < 46 && percentage >= 36) {
            audio = R.raw.assessment_performance_almost_there
            binding.ivScore.setImageResource(R.drawable.almost_there)
            binding.textViewPerformanceText.text = "Almost There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.almost_there_yellow))
            binding.tvPerformanceText.text =
                "Almost there! Improve your knowledge of financial concepts. Click on Tips!"
            //showReviewButton()
        } else if (percentage < 36 && percentage >= 26) {
            audio = R.raw.assessment_performance_getting_there
            binding.ivScore.setImageResource(R.drawable.getting_there)
            binding.textViewPerformanceText.text = "Getting There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.getting_there_orange))
            binding.tvPerformanceText.text =
                "Getting there! Improve your knowledge of financial concepts. Click on Tips!"
            //showReviewButton()
        } else if (percentage < 26 && percentage >= 16) {
            audio = R.raw.assessment_performance_not_quite_needs_improvement
            binding.ivScore.setImageResource(R.drawable.not_quite_there_yet)
            binding.textViewPerformanceText.text = "Not Quite\n There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.not_quite_there_red))
            binding.tvPerformanceText.text =
                "Uh oh! Improve your knowledge of financial concepts. Click on Tips!"
            //showReviewButton()
        } else if (percentage < 15 ) {
            audio = R.raw.assessment_performance_not_quite_needs_improvement
            binding.ivScore.setImageResource(R.drawable.bad)
            binding.textViewPerformanceText.text = "Needs\nImprovement"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.red))
            binding.tvPerformanceText.text =
                "Uh oh! Improve your knowledge of financial concepts. Click on Tips!"
            //showReviewButton()
        }
        loadAudio(audio)
    }

    private fun showParentAssessmentDialog() {

        var dialogBinding= DialogParentAssessmentBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext());
        dialog.setContentView(dialogBinding.getRoot())

        if (concept == "Goal Setting") {
            dialogBinding.tvConcept.text = "Goal Setting"
            dialogBinding.tvDefinition.text = "Creating SMART Goals to achieve a financial target."
            dialogBinding.tvConcepts.text = "1. Concept of Goal Setting\n2. Types of Goals (Short, Medium, Long Term)\n3. SMART Goals"
        } else if (concept == "Saving") {
            dialogBinding.tvConcept.text = "Saving"
            dialogBinding.tvDefinition.text = "Setting aside money to meet financial goals, have money for the future, and afford bigger purchases.."
            dialogBinding.tvConcepts.text = "1. Concept of Saving\n2. Saving Strategies\n3. Earning\n4. Delayed Gratification"
        } else if (concept == "Budgeting") {
            dialogBinding.tvConcept.text = "Budgeting"
            dialogBinding.tvDefinition.text = "Achievable goals are realistic."
            dialogBinding.tvConcepts.text = "1. Concept of Budgeting\n2. Creating a Budget"
        } else if (concept == "Spending") {
            dialogBinding.tvConcept.text = "Spending"
            dialogBinding.tvDefinition.text = "Relevant goals are important to you and with what you want to do."
            dialogBinding.tvConcepts.text = "1. Concept of Spending\n2. Comparison Shopping\n3. Spending Within a Budget\n4. Needs vs Wants\n5. Impulse Buying"
        }

        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1200)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setEmptyAssessmentText() {
        binding.btnAudio.setImageResource(R.drawable.bad)
        binding.textViewProgress.visibility = View.GONE

        binding.textViewPerformanceText.text = "Bad"
        binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.red))
        val message = if (user == "Child")
            "You haven't taken any assessments yet!"
        else "Your child hasn't taken any assessments yet!"
        binding.tvPerformanceText.text = message

        binding.tvTopPerformingConcept.text = "N/A"

        binding.tvConcept2nd.text = "N/A"

        binding.tvConcept3rd.text = "N/A"

        binding.tvConcept4th.text = "N/A"
    }


    /*private fun showReviewButton() {
        binding.btnSeeMore.visibility = View.GONE
        binding.layoutButtons.visibility = View.VISIBLE
    }*/


}

