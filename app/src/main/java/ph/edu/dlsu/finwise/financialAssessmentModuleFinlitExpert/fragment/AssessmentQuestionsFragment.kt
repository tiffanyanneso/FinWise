package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.AssessmentQuestionsAdapter
import ph.edu.dlsu.finwise.databinding.FragmentAssessmentQuestionsBinding


class AssessmentQuestionsFragment : Fragment() {

    private lateinit var binding:FragmentAssessmentQuestionsBinding

    private lateinit var assessmentID:String
    private lateinit var questionsAdapter:AssessmentQuestionsAdapter

    private var firestore = Firebase.firestore

    var questionsID = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var bundle = arguments
            assessmentID = bundle?.getString("assessmentID").toString()
            getQuestions()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssessmentQuestionsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private fun getQuestions() {
        println("print in questions fragment  " + assessmentID)
        firestore.collection("AssessmentQuestions").whereEqualTo("assessmentID", assessmentID).get().addOnSuccessListener { questions ->
            for (question in questions)
                questionsID.add(question.id)


            println("print questiosn " + questions.size())
            questionsAdapter = AssessmentQuestionsAdapter(requireContext().applicationContext, questionsID)
            binding.rvViewQuestions.adapter = questionsAdapter
            binding.rvViewQuestions.layoutManager = LinearLayoutManager(requireContext().applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
        }
    }
}