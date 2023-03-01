package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.AssessmentQuestionsAdapter
import ph.edu.dlsu.finwise.databinding.FragmentAssessmentQuestionsBinding
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.FinancialAssessmentViewAllQuestions


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
        binding.btnViewAll.setOnClickListener {
            var viewAllQuestions = Intent(requireContext().applicationContext, FinancialAssessmentViewAllQuestions::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("assessmentID", assessmentID)
            viewAllQuestions.putExtras(sendBundle)
            startActivity(viewAllQuestions)
        }
        return binding.root
    }

    private fun getQuestions() {
        questionsID.clear()
        firestore.collection("AssessmentQuestions").whereEqualTo("assessmentID", assessmentID).whereEqualTo("isUsed", true).get().addOnSuccessListener { questions ->
            for (question in questions)
                questionsID.add(question.id)

            questionsAdapter = AssessmentQuestionsAdapter(requireContext().applicationContext, questionsID)
            binding.rvViewQuestions.adapter = questionsAdapter
            binding.rvViewQuestions.layoutManager = LinearLayoutManager(requireContext().applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }
}