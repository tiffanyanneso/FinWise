package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.FragmentAssessmentDetailsBinding
import ph.edu.dlsu.finwise.model.AssessmentDetails
import java.text.SimpleDateFormat

class AssessmentDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAssessmentDetailsBinding

    private var firestore = Firebase.firestore

    private lateinit var assessmentID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            var bundle = arguments
            assessmentID = bundle?.getString("assessmentID").toString()
            setFields()
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
            var assessment = it.toObject<AssessmentDetails>()

            binding.tvCategory.text = assessment?.assessmentCategory
            binding.tvDateCreated.text = SimpleDateFormat("MM/dd/yyyy").format(assessment?.createdOn?.toDate())
            binding.tvNumberOfTakes.text = assessment?.nTakes.toString()
            binding.tvNumberOfQuestions.text = assessment?.nQuestionsInAssessment.toString()
        }
    }
}