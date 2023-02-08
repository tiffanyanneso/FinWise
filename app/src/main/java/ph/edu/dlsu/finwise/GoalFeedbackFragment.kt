package ph.edu.dlsu.finwise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.FragmentAchievedBinding
import ph.edu.dlsu.finwise.databinding.FragmentGoalFeedbackBinding

class GoalFeedbackFragment : Fragment() {

    private lateinit var binding: FragmentGoalFeedbackBinding
    private var firestore = Firebase.firestore
    private var goalID = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getArguments() != null) {
            goalID = arguments?.getString("goalID").toString();
        }
        getGoalDetails(goalID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalFeedbackBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private fun getGoalDetails(goalID: String) {

        firestore.collection("FinancialGoals").whereEqualTo("goalID", goalID).get().addOnSuccessListener { documents ->
            for (goalSnapshot in documents) {
                // TODO Get details
            }
        }
    }
}