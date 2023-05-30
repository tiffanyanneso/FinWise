package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.adapter.ChildNotificationSummaryGoalAdapter
import ph.edu.dlsu.finwise.adapter.GoalToReviewNotificationAdapter
import ph.edu.dlsu.finwise.adapter.ParentChildrenAdapter
import ph.edu.dlsu.finwise.adapter.ParentPendingGoalsAdapter
import ph.edu.dlsu.finwise.databinding.DialogNotifNewGoalToRateParentBinding
import ph.edu.dlsu.finwise.databinding.FragmentParentPendingGoalsBinding
import ph.edu.dlsu.finwise.model.GoalRating
import ph.edu.dlsu.finwise.model.Users
import java.util.*
import kotlin.collections.ArrayList

class NotificationGoalFragment: Fragment() {

    private lateinit var binding: FragmentParentPendingGoalsBinding
    private var firestore = Firebase.firestore
    private lateinit var lastLogin: Date

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lastLogin = arguments?.getSerializable("lastLogin") as Date
        CoroutineScope(Dispatchers.Main).launch {
            getPendingGoals()
        }
    }

    class GoalNotificationObject (var type:String, var goalID:String)

    private suspend fun getPendingGoals() {
        var goalsArrayList = ArrayList<GoalNotificationObject>()

        var goals = firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).whereEqualTo("status", "For Editing").get().await()
        for (goal in goals)
            goalsArrayList.add(GoalNotificationObject("For Editing", goal.id))

        var goalsReviewed = firestore.collection("GoalRating").whereEqualTo("childID", currentUser).whereEqualTo("status", "Approved").get().await()
        for (goalRating in goalsReviewed) {
            var dateGoalReviewed = goalRating.toObject<GoalRating>().lastUpdated!!.toDate()

            if (dateGoalReviewed.after(lastLogin))
                goalsArrayList.add(GoalNotificationObject("Goal Approved", goalRating.toObject<GoalRating>().financialGoalID!!))
        }

        if (!goalsArrayList.isEmpty()) {
            var parentPendingGoalsAdapter = ChildNotificationSummaryGoalAdapter(requireActivity().applicationContext, goalsArrayList)
            binding.rvGoals.adapter = parentPendingGoalsAdapter
            binding.rvGoals.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
            parentPendingGoalsAdapter.notifyDataSetChanged()
        } else
            binding.layoutEmptyActivity.visibility = View.VISIBLE

        binding.loadingItems.stopShimmer()
        binding.loadingItems.visibility = View.GONE
        binding.rvGoals.visibility = View.VISIBLE
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentPendingGoalsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


}