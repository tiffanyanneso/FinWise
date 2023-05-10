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

class AchievedFragment : Fragment() {

    private lateinit var binding: FragmentAchievedBinding
    private var firestore = Firebase.firestore
    private lateinit var goalAdapter: FinactAchievedAdapter

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAchievedGoals()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAchievedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private fun getAchievedGoals() {
        var goalIDArrayList = ArrayList<String>()
        goalIDArrayList.clear()
        firestore.collection("FinancialActivities").whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "Completed").get().addOnSuccessListener { documents ->
            for (financialActivity in documents) {
                var finactObject = financialActivity.toObject<FinancialActivities>()
                goalIDArrayList.add(finactObject.financialGoalID!!)
            }

            loadRecyclerView(goalIDArrayList)
        }
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        goalAdapter = FinactAchievedAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = goalAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        goalAdapter.notifyDataSetChanged()
        binding.rvViewGoals.visibility = View.VISIBLE
        binding.loadingItems.stopShimmer()
        binding.loadingItems.visibility = View.GONE
    }
}