package ph.edu.dlsu.finwise.childDashboardModule.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentDashboardFinancialAssessmentsBinding
import ph.edu.dlsu.finwise.model.FinancialAssessmentAttempts
import java.text.DecimalFormat

class DashboardFinancialAssessmentsFragment : Fragment() {
    private lateinit var binding: FragmentDashboardFinancialAssessmentsBinding
    private var firestore = Firebase.firestore

    private lateinit var userID: String
    private var financialAssessmentPerformance = 0.00F


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_financial_assessments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDashboardFinancialAssessmentsBinding.bind(view)

        getArgumentsBundle()
        getFinancialAssessmentScore()
    }

    private fun getArgumentsBundle() {
        val args = arguments
        userID = args?.getString("userID").toString()
    }

    private fun getFinancialAssessmentScore() {
        CoroutineScope(Dispatchers.Main).launch {
            var nCorrect = 0
            var nQuestions = 0
            val assessmentAttemptsDocuments = firestore.collection("AssessmentAttempts")
                .whereEqualTo("childID", userID).get().await()
            if (assessmentAttemptsDocuments.size()!=0) {
                for (attempt in assessmentAttemptsDocuments) {
                    val assessmentAttempt = attempt.toObject<FinancialAssessmentAttempts>()
                    if (assessmentAttempt.nAnsweredCorrectly != null && assessmentAttempt.nQuestions != null) {
                        nCorrect += assessmentAttempt.nAnsweredCorrectly!!
                        nQuestions += assessmentAttempt.nQuestions!!
                    }
                }

                financialAssessmentPerformance = (nCorrect.toFloat() / nQuestions.toFloat()) * 100
                binding.progressBarFinancialAssessments.progress = financialAssessmentPerformance.toInt()
                binding.tvFinancialAssessmentPercenage.text =
                    DecimalFormat("##0.00").format(financialAssessmentPerformance) + "%"
                }
            }
        }
}