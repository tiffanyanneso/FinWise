package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.AssessmentLowAccuracyQuestionsAdapter
import ph.edu.dlsu.finwise.adapter.AssessmentQuestionsAdapter
import ph.edu.dlsu.finwise.databinding.FragmentAssessmentDetailsBinding
import ph.edu.dlsu.finwise.model.FinancialAssessmentDetails
import ph.edu.dlsu.finwise.model.FinancialAssessmentQuestions
import java.text.SimpleDateFormat

class AssessmentDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAssessmentDetailsBinding

    private var firestore = Firebase.firestore

    private lateinit var assessmentID:String

    private var questionsID = ArrayList<String>()
    private lateinit var  questionsAdapter: AssessmentLowAccuracyQuestionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            var bundle = arguments
            assessmentID = bundle?.getString("assessmentID").toString()
            setFields()
            getLowCorrectAnswer()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssessmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setFields() {
        firestore.collection("Assessments").document(assessmentID).get().addOnSuccessListener {
            var assessment = it.toObject<FinancialAssessmentDetails>()

            binding.tvCategory.text = assessment?.assessmentCategory
            binding.tvDateCreated.text = SimpleDateFormat("MM/dd/yyyy").format(assessment?.createdOn?.toDate())
            binding.tvDescription.text = assessment?.description
            //binding.tvDateModified.text = SimpleDateFormat("MM/dd/yyyy").format(assessment?.mo?.toDate())
            binding.tvNumberOfTakes.text = assessment?.nTakes.toString()
            binding.tvNumberOfQuestions.text = assessment?.nQuestionsInAssessment.toString()
        }
    }

    private fun getLowCorrectAnswer() {
        questionsID.clear()
        firestore.collection("AssessmentQuestions").whereEqualTo("assessmentID", assessmentID).whereEqualTo("isUsed", true).get().addOnSuccessListener { questions ->
            for (question in questions) {
                var questionObject = question.toObject<FinancialAssessmentQuestions>()
                var answeredCorrectly  = questionObject?.nAnsweredCorrectly?.toFloat()
                var nAssessment =  questionObject?.nAssessments!!?.toFloat()
                var correctPercentage = 0.00F
                if (nAssessment!=0.00F) {
                    correctPercentage = (answeredCorrectly!! / nAssessment!!) * 100
                    if (correctPercentage <= 30)
                        questionsID.add(question.id)
                }
            }

            questionsAdapter = AssessmentLowAccuracyQuestionsAdapter(requireContext().applicationContext, questionsID, assessmentID)
            binding.rvQuestions.adapter = questionsAdapter
            binding.rvQuestions.layoutManager = LinearLayoutManager(requireContext().applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }
}