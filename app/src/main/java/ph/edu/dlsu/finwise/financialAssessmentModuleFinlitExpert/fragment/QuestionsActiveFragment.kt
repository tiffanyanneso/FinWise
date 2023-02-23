package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.AssessmentQuestionsAdapter
import ph.edu.dlsu.finwise.databinding.FragmentQuestionsActiveBinding

class QuestionsActiveFragment: Fragment() {

    private lateinit var binding: FragmentQuestionsActiveBinding

    private var firestore = Firebase.firestore

    private lateinit var assessmentID:String

    private var questionsIDArrayList = ArrayList<String>()
    private lateinit var questionsAdapter:AssessmentQuestionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            questionsIDArrayList.clear()
            var bundle = arguments
            assessmentID = bundle?.getString("assessmentID").toString()
            getActiveQuestions()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuestionsActiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getActiveQuestions() {
        firestore.collection("AssessmentQuestions").whereEqualTo("assessmentID", assessmentID).whereEqualTo("isUsed", true).get().addOnSuccessListener { results ->
            for (question in results)
                questionsIDArrayList.add(question.id)

            questionsAdapter = AssessmentQuestionsAdapter(requireContext().applicationContext, questionsIDArrayList)
            binding.rvQuestions.adapter = questionsAdapter
            binding.rvQuestions.layoutManager = LinearLayoutManager(requireContext().applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }
}