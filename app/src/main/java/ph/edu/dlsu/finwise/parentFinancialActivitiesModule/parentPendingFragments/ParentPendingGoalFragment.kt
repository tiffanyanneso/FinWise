package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentPendingFragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.adapter.GoalToReviewNotificationAdapter
import ph.edu.dlsu.finwise.adapter.ParentChildrenAdapter
import ph.edu.dlsu.finwise.adapter.ParentPendingGoalsAdapter
import ph.edu.dlsu.finwise.databinding.DialogNotifNewGoalToRateParentBinding
import ph.edu.dlsu.finwise.databinding.FragmentFinactBudgetingBinding
import ph.edu.dlsu.finwise.databinding.FragmentParentPendingGoalsBinding

class ParentPendingGoalFragment: Fragment() {

    private lateinit var binding: FragmentParentPendingGoalsBinding
    private var firestore = Firebase.firestore

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private var childIDArrayList = ArrayList<String>()
    private var newGoals = ArrayList<String>()

    private lateinit var parentPendingGoalsAdapter:ParentPendingGoalsAdapter

    private var coroutineScope =  CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        childIDArrayList.clear()
        newGoals.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        coroutineScope.launch {
            loadChildren()
            getPendingGoals()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope.cancel()
    }

    private suspend fun getPendingGoals() {
        for (childID in childIDArrayList) {
            var goals = firestore.collection("FinancialGoals").whereEqualTo("childID", childID).whereEqualTo("status", "For Review").get().await()
            for (goal in goals)
                newGoals.add(goal.id)
        }

        if (isAdded) {
            if (!newGoals.isEmpty()) {
                parentPendingGoalsAdapter = ParentPendingGoalsAdapter(requireActivity().applicationContext, newGoals)
                binding.rvGoals.adapter = parentPendingGoalsAdapter
                binding.rvGoals.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
                parentPendingGoalsAdapter.notifyDataSetChanged()
            } else
                binding.layoutEmptyActivity.visibility = View.VISIBLE

            binding.loadingItems.stopShimmer()
            binding.loadingItems.visibility = View.GONE
            binding.rvGoals.visibility = View.VISIBLE
        }
    }

    private suspend fun loadChildren() {
        var children =  firestore.collection("Users").whereEqualTo("userType", "Child").whereEqualTo("parentID", currentUser).get().await()
        if (children.size()!=0) {
            for (child in children)
                childIDArrayList.add(child.id)
        }
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