package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

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
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactSpendingAdapter
import ph.edu.dlsu.finwise.databinding.FragmentSpendingBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import java.util.*
import kotlin.collections.ArrayList

class SpendingFragment : Fragment(){

    private lateinit var binding: FragmentSpendingBinding
    private var firestore = Firebase.firestore
    private lateinit var spendingAdapter: FinactSpendingAdapter

    var goalIDArrayList = ArrayList<String>()
    var budgetingArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<GoalFilter>()

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goalIDArrayList.clear()
        budgetingArrayList.clear()
        getSpending()

        binding.progressBar.progress = 75
        binding.textPerformance.text = "75%"
        binding.textStatus.text = "Good"
        binding.textPerformance.setTextColor(getResources().getColor(R.color.dark_green))
        binding.textStatus.setTextColor(getResources().getColor(R.color.dark_green))

        binding.progressBarOverspending.progress = 10
        binding.textOverspending.text = "80%"
        binding.textOverpsneidngStatus.text = "Bad"
        binding.textOverspending.setTextColor(getResources().getColor(R.color.red))
        binding.textOverpsneidngStatus.setTextColor(getResources().getColor(R.color.red))
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
        goalIDArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            for (activity in results) {
                var activityObject = activity.toObject<FinancialActivities>()
                goalIDArrayList.add(activityObject?.financialGoalID.toString())
            }
            loadRecyclerView(goalIDArrayList)
        }
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        spendingAdapter = FinactSpendingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = spendingAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        spendingAdapter.notifyDataSetChanged()
    }
}