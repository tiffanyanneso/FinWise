package ph.edu.dlsu.finwise.financialActivitiesModule.goalDetailsFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.FragmentGoalFeedbackBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.EditGoal
import ph.edu.dlsu.finwise.financialActivitiesModule.UpdateGoalActivity
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.GoalRating

class GoalFeedbackFragment : Fragment() {

    private lateinit var binding: FragmentGoalFeedbackBinding
    private var firestore = Firebase.firestore
    private lateinit var financialGoalID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getArguments() != null) {
            financialGoalID = arguments?.getString("financialGoalID").toString();
        }
        getGoalReview()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalFeedbackBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnEditGoal.setOnClickListener {
            var sendBundle = Bundle()
            sendBundle.putString("financialGoalID", financialGoalID)
            var editGoal = Intent(requireContext().applicationContext, UpdateGoalActivity::class.java)
            editGoal.putExtras(sendBundle)
            startActivity(editGoal)
        }
    }

    private fun getGoalReview() {
        firestore.collection("GoalRating").whereEqualTo("financialGoalID", financialGoalID).get().addOnSuccessListener { result ->
            var goalReviewObjects= result.toObjects<GoalRating>()
            goalReviewObjects = goalReviewObjects.sortedByDescending { it.lastUpdated }
            var goalReview = goalReviewObjects[0]
            binding.tvOverallRating.text = "${goalReview?.overallRating} / 5.0"
            binding.ratingBarSpecific.rating = goalReview?.specific!!
            binding.ratingBarMeasurable.rating = goalReview.measurable!!
            binding.ratingBarAchievable.rating = goalReview.achievable!!
            binding.ratingBarRelevant.rating = goalReview.relevant!!
            binding.ratingBarTimeBound.rating = goalReview.timeBound!!
            binding.tvComment.text = goalReview.comment
        }.continueWith {
            firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
                var goal = it.toObject<FinancialGoals>()
                if (goal?.status == "For Editing")
                    binding.btnEditGoal.visibility = View.VISIBLE
                else
                    binding.btnEditGoal.visibility = View.GONE

                binding.tvStatus.text = goal?.status
            }
        }
    }
}