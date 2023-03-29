package ph.edu.dlsu.finwise.financialAssessmentModule.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentAssessmentPerformanceBinding
import ph.edu.dlsu.finwise.model.FinancialAssessmentAttempts
import ph.edu.dlsu.finwise.model.FinancialAssessmentDetails

class AssessmentPerformanceFragment : Fragment() {
    private lateinit var binding: FragmentAssessmentPerformanceBinding
    private var firestore = Firebase.firestore
    private lateinit var childID: String
    private lateinit var user: String

    private val assessmentsTaken = ArrayList<FinancialAssessmentAttempts>()
    private var financialGoalsPercentage = 0.00F
    private var financialGoalsScores = ArrayList<Double?>()
    private var savingPercentage = 0.00F
    private var savingScores = ArrayList<Double?>()
    private var budgetingPercentage = 0.00F
    private var budgetingScores = ArrayList<Double?>()
    private var spendingPercentage = 0.00F
    private var spendingScores = ArrayList<Double?>()


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
// check SAVINGSCORES LANG kung may over 100%, saving percentage sunm, savinscore size, and saving percentage kung tama calculation
        //backup lang, make percentage funciton above to cap out at 100%
        val savingPercentageSum = savingScores.sumOf { it ?: 0.0 }
        savingPercentage = ((savingPercentageSum / (maxScore * savingScores.size)) * 100).toFloat()
        Log.d("fdsfsdsdss", "savingScores: "+ savingScores)
        Log.d("fdsfsdsdss", "savingPercentageSum: "+ savingPercentageSum)
        Log.d("fdsfsdsdss", "savingPercentage: "+ savingPercentage)
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
        if (percentage >= 96) {
            binding.ivScore.setImageResource(R.drawable.excellent)
            binding.textViewPerformanceText.text = "Excellent"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.dark_green))
            binding.tvPerformanceText.text =
                "Excellent! Your child consistently makes informed financial decisions and demonstrates a good understanding of key financial concepts.!"
            //showSeeMoreButton()
        } else if (percentage < 96 && percentage >= 86) {
            binding.ivScore.setImageResource(R.drawable.amazing)
            binding.textViewPerformanceText.text = "Amazing"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.amazing_green))
            binding.tvPerformanceText.text =
                "Great! Your child generally makes sound financial decisions and has a solid grasp of important financial concepts, but may make occasional mistakes or need some guidance in certain areas.!"
            //showSeeMoreButton()
        } else if (percentage < 86 && percentage >= 76) {
            binding.ivScore.setImageResource(R.drawable.great)
            binding.textViewPerformanceText.text = "Great"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.green))
            binding.tvPerformanceText.text =
                "Great! Your child generally makes sound financial decisions and has a solid grasp of important financial concepts, but may make occasional mistakes or need some guidance in certain areas.!"
            //showSeeMoreButton()
        } else if (percentage < 76 && percentage >= 66) {
            binding.ivScore.setImageResource(R.drawable.good)
            binding.textViewPerformanceText.text = "Good"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.light_green))
            binding.tvPerformanceText.text =
                "Good! Your child has some understanding of basic financial concepts and can make simple financial decisions, but may need significant guidance and education in more complex financial matters."
            //showSeeMoreButton()
        } else if (percentage < 66 && percentage >= 56) {
            binding.ivScore.setImageResource(R.drawable.average)
            binding.textViewPerformanceText.text = "Average"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.yellow))
            binding.tvPerformanceText.text =
                "Okay! Your child has limited financial knowledge and struggles to make good financial decisions without significant guidance and education."
            //showReviewButton()
        } else if (percentage < 56 && percentage >= 46) {
            binding.ivScore.setImageResource(R.drawable.nearly_there)
            binding.textViewPerformanceText.text = "Nearly There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            binding.tvPerformanceText.text =
                "Uh oh! Your child lacks basic financial literacy and requires immediate education and guidance to improve their financial decision-making."
            //showReviewButton()
        }  else if (percentage < 46 && percentage >= 36) {
            binding.ivScore.setImageResource(R.drawable.almost_there)
            binding.textViewPerformanceText.text = "Almost There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.almost_there_yellow))
            binding.tvPerformanceText.text =
                "Uh oh! Your child lacks basic financial literacy and requires immediate education and guidance to improve their financial decision-making."
            //showReviewButton()
        } else if (percentage < 36 && percentage >= 26) {
            binding.ivScore.setImageResource(R.drawable.getting_there)
            binding.textViewPerformanceText.text = "Getting There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.getting_there_orange))
            binding.tvPerformanceText.text =
                "Uh oh! Your child lacks basic financial literacy and requires immediate education and guidance to improve their financial decision-making."
            //showReviewButton()
        } else if (percentage < 26 && percentage >= 16) {
            binding.ivScore.setImageResource(R.drawable.not_quite_there_yet)
            binding.textViewPerformanceText.text = "Not Quite\n" +
                    "There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.not_quite_there_red))
            binding.tvPerformanceText.text =
                "Uh oh! Your child lacks basic financial literacy and requires immediate education and guidance to improve their financial decision-making."
            //showReviewButton()
        } else if (percentage < 15) {
            binding.ivScore.setImageResource(R.drawable.bad)
            binding.textViewPerformanceText.text = "Needs\n" +
                    "Improvement"
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
        } else if (percentage < 96 && percentage >= 86) {
            binding.ivScore.setImageResource(R.drawable.amazing)
            binding.textViewPerformanceText.text = "Amazing"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.amazing_green))
            binding.tvPerformanceText.text =
                "Great job! You have a good grasp of financial concepts and are starting to make smart choices with your money!"
            //showSeeMoreButton()
        } else if (percentage < 86 && percentage >= 76) {
            binding.ivScore.setImageResource(R.drawable.great)
            binding.textViewPerformanceText.text = "Great"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.green))
            binding.tvPerformanceText.text =
                "Uh oh! You have limited knowledge of financial concepts and there are many areas where you need to improve!"
            //showReviewButton()
        } else if (percentage < 76 && percentage >= 66) {
            binding.ivScore.setImageResource(R.drawable.good)
            binding.textViewPerformanceText.text = "Good"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.light_green))
            binding.tvPerformanceText.text =
                "Great! You have an average understanding of financial concepts and are making some smart choices with your money, but there is room for improvement!"
            //showSeeMoreButton()
        } else if (percentage < 56 && percentage >= 46) {
            binding.ivScore.setImageResource(R.drawable.average)
            binding.textViewPerformanceText.text = "Average"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.yellow))
            binding.tvPerformanceText.text =
                "Nice work! You have some basic knowledge of financial concepts, but there are many areas where you could improve!"
            //showReviewButton()
        } else if (percentage < 46 && percentage >= 36) {
            binding.ivScore.setImageResource(R.drawable.nearly_there)
            binding.textViewPerformanceText.text = "Nearly There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            binding.tvPerformanceText.text =
                "Uh oh! You have limited knowledge of financial concepts and there are many areas where you need to improve!"
            //showReviewButton()
        }  else if (percentage < 36 && percentage >= 26) {
            binding.ivScore.setImageResource(R.drawable.almost_there)
            binding.textViewPerformanceText.text = "Almost There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.almost_there_yellow))
            binding.tvPerformanceText.text =
                "Uh oh! You have limited knowledge of financial concepts and there are many areas where you need to improve!"
            //showReviewButton()
        } else if (percentage < 26 && percentage >= 16) {
            binding.ivScore.setImageResource(R.drawable.getting_there)
            binding.textViewPerformanceText.text = "Getting There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.getting_there_orange))
            binding.tvPerformanceText.text =
                "Uh oh! You have limited knowledge of financial concepts and there are many areas where you need to improve!"
            //showReviewButton()
        } else if (percentage < 26 && percentage >= 16) {
            binding.ivScore.setImageResource(R.drawable.not_quite_there_yet)
            binding.textViewPerformanceText.text = "Not Quite\n" +
                    "There"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.not_quite_there_red))
            binding.tvPerformanceText.text =
                "Uh oh! You have limited knowledge of financial concepts and there are many areas where you need to improve!"
            //showReviewButton()
        } else if (percentage < 15 ) {
            binding.ivScore.setImageResource(R.drawable.bad)
            binding.textViewPerformanceText.text = "Needs\nImprovement"
            binding.textViewPerformanceText.setTextColor(resources.getColor(R.color.red))
            binding.tvPerformanceText.text =
                "Uh oh! You have limited knowledge of financial concepts and there are many areas where you need to improve!"
            //showReviewButton()
        }
    }

    private fun setEmptyAssessmentText() {
        binding.ivScore.setImageResource(R.drawable.bad)
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

