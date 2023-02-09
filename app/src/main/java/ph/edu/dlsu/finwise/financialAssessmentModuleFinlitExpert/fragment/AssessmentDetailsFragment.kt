package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentAssessmentDetailsBinding

class AssessmentDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAssessmentDetailsBinding

    private var firestore = Firebase.firestore

    //TODO: CHANGE TO ASSESSMENT ID
    private lateinit var assessmentCategory:String
    private lateinit var assessmentType:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        assessmentCategory = bundle?.getString("assessmentCategory").toString()
        assessmentType = bundle?.getString("assessmentType").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssessmentDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assessment_details, container, false)
    }
}