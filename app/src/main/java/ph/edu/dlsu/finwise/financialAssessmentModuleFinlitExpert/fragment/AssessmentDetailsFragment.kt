package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
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
            println("print in fragment " + assessmentID)
            setFields()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssessmentDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private fun setFields() {
        println("print set fields "  + assessmentID)
        firestore.collection("Assessments").document(assessmentID).get().addOnSuccessListener {
            var assessment = it.toObject<AssessmentDetails>()
            println("print set fields "  + assessment?.assessmentCategory)

            binding.tvCategory.text = assessment?.assessmentCategory
            binding.tvDateCreated.text= SimpleDateFormat("MM/dd/yyyy").format(assessment?.createdOn?.toDate())
            binding.tvNumberOfTakes.text = assessment?.nTakes.toString()
        }.continueWith {
            firestore.collection("AssessmentQuestions").whereEqualTo("assessmentID", assessmentID).get().addOnSuccessListener { questions ->
                binding.tvNumberOfQuestions.text = questions.size().toString()
            }
        }
    }
}