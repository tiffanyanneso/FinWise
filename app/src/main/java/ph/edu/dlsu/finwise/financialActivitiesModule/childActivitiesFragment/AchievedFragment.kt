package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.FinactAchievedAdapter
import ph.edu.dlsu.finwise.databinding.FragmentAchievedBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AchievedFragment : Fragment() {

    private lateinit var binding: FragmentAchievedBinding
    private var firestore = Firebase.firestore
    private lateinit var goalAdapter: FinactAchievedAdapter

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAchievedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            CoroutineScope(Dispatchers.Main).launch {
                getAchievedGoals()
            }
        }
    }

    private suspend fun getAchievedGoals() {
        var goalIDArrayList = ArrayList<String>()
        goalIDArrayList.clear()
        var goals = firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).whereEqualTo("status", "Completed").orderBy("dateCompleted", Query.Direction.DESCENDING).get().await()
        for (goal in goals)
            goalIDArrayList.add(goal.id)

        if (!goalIDArrayList.isEmpty())
            loadRecyclerView(goalIDArrayList)
        else {
            binding.rvViewGoals.visibility = View.GONE
            binding.layoutEmptyActivity.visibility = View.VISIBLE
        }
        binding.loadingItems.stopShimmer()
        binding.loadingItems.visibility = View.GONE
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        goalAdapter = FinactAchievedAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = goalAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        goalAdapter.notifyDataSetChanged()
        binding.rvViewGoals.visibility = View.VISIBLE
    }
}