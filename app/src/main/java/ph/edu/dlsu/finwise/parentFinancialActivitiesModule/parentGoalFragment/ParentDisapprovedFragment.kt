package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentGoalFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.FinactDisapprovedAdapter
import ph.edu.dlsu.finwise.adapter.FinactSavingAdapter
import ph.edu.dlsu.finwise.databinding.FragmentDisapprovedBinding
import ph.edu.dlsu.finwise.databinding.FragmentParentDisapprovedBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.util.*

class ParentDisapprovedFragment : Fragment() {

    private lateinit var binding: FragmentParentDisapprovedBinding
    private var firestore = Firebase.firestore
    private lateinit var goalAdapter: FinactDisapprovedAdapter

    private lateinit var childID:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        childID = bundle?.getString("childID").toString()
        getDisapprovedGoals()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentDisapprovedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }

    private fun getDisapprovedGoals() {
        var goalIDArrayList = ArrayList<String>()
        var goalFilterArrayList = ArrayList<GoalFilter>()
        goalIDArrayList.clear()


        firestore.collection("FinancialGoals").whereEqualTo("childID", childID).whereEqualTo("status", "Disapproved").get().addOnSuccessListener { documents ->
            for (goalSnapshot in documents) {
                //creating the object from list retrieved in db
                var goalID = goalSnapshot.id
                var goal = goalSnapshot.toObject<FinancialGoals>()
                //goalIDArrayList.add(goalID)
                goalFilterArrayList.add(GoalFilter(goalID, goal?.targetDate!!.toDate()))
            }
            goalFilterArrayList.sortBy { it.goalTargetDate }
            for (goalFilter in goalFilterArrayList)
                goalIDArrayList.add(goalFilter.financialGoalID!!)
            if (!goalIDArrayList.isEmpty())
                loadRecyclerView(goalIDArrayList)
            else {
                binding.rvViewGoals.visibility = View.GONE
                binding.layoutEmptyActivity.visibility = View.VISIBLE
            }
            binding.loadingItems.stopShimmer()
            binding.loadingItems.visibility = View.GONE
        }
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        goalAdapter = FinactDisapprovedAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = goalAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        goalAdapter.notifyDataSetChanged()
        binding.rvViewGoals.visibility = View.VISIBLE
    }
}