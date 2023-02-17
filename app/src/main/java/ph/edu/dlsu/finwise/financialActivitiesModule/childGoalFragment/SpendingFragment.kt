package ph.edu.dlsu.finwise.financialActivitiesModule.childGoalFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.ChildGoalAdapter
import ph.edu.dlsu.finwise.databinding.FragmentBudgetingBinding
import ph.edu.dlsu.finwise.databinding.FragmentSpendingBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import java.util.*
import kotlin.collections.ArrayList

class SpendingFragment : Fragment(){

    private lateinit var binding: FragmentSpendingBinding
    private var firestore = Firebase.firestore
    private lateinit var goalAdapter: ChildGoalAdapter

    var goalIDArrayList = ArrayList<String>()
    var budgetingArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<GoalFilter>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goalIDArrayList.clear()
        budgetingArrayList.clear()
        getSpending()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpendingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }

    private fun getSpending() {
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialActivities").whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            for (spending in results) {
                var spendingActivity = spending.toObject<FinancialActivities>()
                budgetingArrayList.add(spendingActivity)

                goalIDArrayList.add(spendingActivity.financialGoalID.toString())
                loadRecyclerView(goalIDArrayList)
            }
        }
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        goalAdapter = ChildGoalAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = goalAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        goalAdapter.notifyDataSetChanged()
    }
}