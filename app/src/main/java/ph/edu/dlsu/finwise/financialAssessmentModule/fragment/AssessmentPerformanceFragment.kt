package ph.edu.dlsu.finwise.financialAssessmentModule.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentAssessmentPerformanceBinding
import ph.edu.dlsu.finwise.model.*

class AssessmentPerformanceFragment : Fragment() {
    private lateinit var binding: FragmentAssessmentPerformanceBinding
    private var firestore = Firebase.firestore
    private lateinit var childID: String
    private lateinit var user: String

    private val assessmentsTaken = ArrayList<FinancialAssessmentAttempts>()
    val assessmentsMap = mutableMapOf<String, Int>()
    private var financialGoalsPercentage = 0.00F
    private var financialGoalsScores = ArrayList<Float>()
    private var savingPercentage = 0.00F
    private var savingScores = ArrayList<Float>()
    private var budgetingPercentage = 0.00F
    private var budgetingScores = ArrayList<Float>()
    private var spendingPercentage = 0.00F
    private var spendingScores = ArrayList<Float>()
    private var nRatings = 0


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

        getBundles()
        getAssessments()
    }

    private fun getBundles() {
        childID = arguments?.getString("childID").toString()
        user = arguments?.getString("user").toString()
    }

    private fun getAssessments() {
        //TODO: change id when we can pass childid through navigation , intent.extras!! causes crash
        firestore.collection("AssessmentAttempts").whereEqualTo("childID", childID)
            .get().addOnSuccessListener { documents ->
                for (assessments in documents) {
                    val assessmentObject = assessments.toObject<FinancialAssessmentAttempts>()
                    assessmentsTaken.add(assessmentObject)
                }
            }.continueWith { getScores() }
    }


    private fun getScores() {
        if (assessmentsTaken.isNotEmpty()) {
            for (assessment in assessmentsTaken) {
                firestore.collection("Assessments").document(assessment.assessmentID!!)
                    .get().addOnSuccessListener { assessmentDocument ->
                        val assessmentObject = assessmentDocument.toObject<FinancialAssessmentDetails>()
                        val percentage = getPercentage(assessment)
                        when (assessmentObject?.assessmentCategory) {
                            "Goal Setting" -> financialGoalsScores.add(percentage)
                            "Saving" -> savingScores.add(percentage)
                            "Budgeting" -> budgetingScores.add(percentage)
                            "Spending" -> spendingScores.add(percentage)
                        }
                    }.continueWith { computeForPercentages() }
            }
        } else setEmptyAssessmentText()

    }

    private fun computeForPercentages() {
        val maxScore = 100
        savingPercentage = (savingScores.sum() / (maxScore * savingScores.size)) * 100
        spendingPercentage = (spendingScores.sum() / (maxScore * spendingScores.size)) * 100
        budgetingPercentage = (budgetingScores.sum() / (maxScore * budgetingScores.size)) * 100
        financialGoalsPercentage = (financialGoalsScores.sum() / (maxScore * financialGoalsScores.size)) * 100
        setPerformanceView()
        setRanking()
    }

    private fun getPercentage(assessment: FinancialAssessmentAttempts): Float {
        val percentage = if (assessment.nAnsweredCorrectly!! > 0) {
            (assessment.nAnsweredCorrectly!!.toFloat() / assessment.nQuestions!!.toFloat()) * 100
        } else {
            0.0
        }
        return percentage.toFloat()
    }

    private fun setEmptyAssessmentText() {
        binding.ivScore.setImageResource(R.drawable.bad)
        binding.textViewProgress.visibility = View.GONE

        binding.textViewPerformanceText.text = "Bad"
        binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.red))
        binding.tvPerformanceText.text =
            "You haven't taken any assessments yet!"

        binding.tvTopPerformingConcept.text = "N/A"

        binding.tvConcept2nd.text = "N/A"

        binding.tvConcept3rd.text = "N/A"

        binding.tvConcept4th.text = "N/A"
    }

    private fun setRanking() {
        val totals = mapOf(
            "Spending" to spendingPercentage,
            "Saving" to savingPercentage,
            "Goal Setting" to financialGoalsPercentage,
            "Budgeting" to budgetingPercentage
        )
        val top3Categories = totals.entries.sortedByDescending { it.value }

        binding.tvTopPerformingConcept.text = top3Categories[0].key
        binding.tvTopPerformingPercentage.text = String.format("%.1f%%", top3Categories[0].value)

        binding.tvConcept2nd.text = top3Categories[1].key
        binding.tvConcept2Percentage.text = String.format("%.1f%%", top3Categories[1].value)

        binding.tvConcept3rd.text = top3Categories[2].key
        binding.tvConcept3Percentage.text = String.format("%.1f%%", top3Categories[2].value)

        //TODO: Add binding
        binding.tvConcept4th.text = top3Categories[3].key
        binding.tvConcept4Percentage.text = String.format("%.1f%%", top3Categories[3].value)



    }

    private fun setPerformanceView() {
        //TODO: Add budgeting and adjust  maxpossiblesum
        val totalSum = spendingPercentage + savingPercentage + financialGoalsPercentage + budgetingPercentage
        val maxPossibleSum = 4 * 100  // assuming the maximum possible value for each variable is 100

        val percentage = (totalSum.toDouble() / maxPossibleSum) * 100

        //Update to be used in the leaderboard
        firestore.collection("ChildUser").document(childID).update("assessmentPerformance", percentage)

        binding.textViewProgress.text = String.format("%.1f%%", percentage)

        if (user == "child")
            setTextPerformanceChild(percentage)
        else setTextPerformanceParent(percentage)

    }

    private fun setTextPerformanceParent(percentage: Double) {
        if (percentage >= 90) {
            binding.ivScore.setImageResource(R.drawable.excellent)
            binding.textViewPerformanceText.text = "Excellent"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.dark_green))
            binding.tvPerformanceText.text =
                "Excellent! Your child consistently makes informed financial decisions and demonstrates a good understanding of key financial concepts.!"
            //showSeeMoreButton()
        } else if (percentage < 90 && percentage >= 80) {
            binding.ivScore.setImageResource(R.drawable.great)
            binding.textViewPerformanceText.text = "Great"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.green))
            binding.tvPerformanceText.text =
                "Great! Your child generally makes sound financial decisions and has a solid grasp of important financial concepts, but may make occasional mistakes or need some guidance in certain areas.!"
            //showSeeMoreButton()
        } else if (percentage < 80 && percentage >= 70) {
            binding.ivScore.setImageResource(R.drawable.good)
            binding.textViewPerformanceText.text = "Good"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.light_green))
            binding.tvPerformanceText.text =
                "Good! Your child has some understanding of basic financial concepts and can make simple financial decisions, but may need significant guidance and education in more complex financial matters."
            //showSeeMoreButton()
        } else if (percentage < 70 && percentage >= 60) {
            binding.ivScore.setImageResource(R.drawable.average)
            binding.textViewPerformanceText.text = "Average"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.yellow))
            binding.tvPerformanceText.text =
                "Okay! Your child has limited financial knowledge and struggles to make good financial decisions without significant guidance and education."
            //showReviewButton()
        } else if (percentage < 60) {
            binding.ivScore.setImageResource(R.drawable.bad)
            binding.textViewPerformanceText.text = "Bad"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.red))
            binding.tvPerformanceText.text =
                "Uh oh! Your child lacks basic financial literacy and requires immediate education and guidance to improve their financial decision-making."
            //showReviewButton()
        }
    }

    private fun setTextPerformanceChild(percentage: Double) {
        if (percentage >= 90) {
            binding.ivScore.setImageResource(R.drawable.excellent)
            binding.textViewPerformanceText.text = "Excellent"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.dark_green))
            binding.tvPerformanceText.text =
                "Keep up the excellent work! You have a strong understanding of financial concepts and are making smart choices with money!"
            //showSeeMoreButton()
        } else if (percentage < 90 && percentage >= 80) {
            binding.ivScore.setImageResource(R.drawable.great)
            binding.textViewPerformanceText.text = "Great"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.green))
            binding.tvPerformanceText.text =
                "Great job! You have a good grasp of financial concepts and are starting to make smart choices with your money!"
            //showSeeMoreButton()
        } else if (percentage < 80 && percentage >= 70) {
            binding.ivScore.setImageResource(R.drawable.good)
            binding.textViewPerformanceText.text = "Good"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.light_green))
            binding.tvPerformanceText.text =
                "Great! You have an average understanding of financial concepts and are making some smart choices with your money, but there is room for improvement!"
            //showSeeMoreButton()
        } else if (percentage < 70 && percentage >= 60) {
            binding.ivScore.setImageResource(R.drawable.average)
            binding.textViewPerformanceText.text = "Average"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.yellow))
            binding.tvPerformanceText.text =
                "Nice work! You have some basic knowledge of financial concepts, but there are many areas where you could improve!"
            //showReviewButton()
        } else if (percentage < 60 ) {
            binding.ivScore.setImageResource(R.drawable.bad)
            binding.textViewPerformanceText.text = "Bad"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.red))
            binding.tvPerformanceText.text =
                "Uh oh! You have limited knowledge of financial concepts and there are many areas where you need to improve!"
            //showReviewButton()
        }
    }

    /*private fun showReviewButton() {
        binding.btnSeeMore.visibility = View.GONE
        binding.layoutButtons.visibility = View.VISIBLE
    }*/


}

