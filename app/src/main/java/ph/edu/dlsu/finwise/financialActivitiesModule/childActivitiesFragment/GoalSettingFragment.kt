package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.FinactGoalSettingAdapter
import ph.edu.dlsu.finwise.databinding.FragmentGoalSettingBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import java.util.*
import kotlin.collections.ArrayList

class GoalSettingFragment : Fragment() {

    private lateinit var binding: FragmentGoalSettingBinding
    private var firestore = Firebase.firestore
    private lateinit var goalSettingAdapter: FinactGoalSettingAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getForReviewGoals()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalSettingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }


    private fun getForReviewGoals() {
        var goalIDArrayList = ArrayList<String>()
        var goalFilterArrayList = ArrayList<GoalFilter>()
        goalIDArrayList.clear()

        var currentUser = "eWZNOIb9qEf8kVNdvdRzKt4AYrA2"
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).whereEqualTo("status", "For Review").get().addOnSuccessListener { results ->
            for (goalForReview in results) {
                var goalObject = goalForReview.toObject<FinancialActivities>()
                goalIDArrayList.add(goalObject?.financialGoalID.toString())
            }
            loadRecyclerView(goalIDArrayList)
        }
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        goalSettingAdapter = FinactGoalSettingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = goalSettingAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        goalSettingAdapter.notifyDataSetChanged()
    }
}