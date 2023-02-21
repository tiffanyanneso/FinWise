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
import ph.edu.dlsu.finwise.adapter.FinactSavingAdapter
import ph.edu.dlsu.finwise.databinding.FragmentParentSavingBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import java.util.*
import kotlin.collections.ArrayList

class ParentSavingFragment : Fragment() {

    private lateinit var binding: FragmentParentSavingBinding
    private var firestore = Firebase.firestore
    private lateinit var savingAdapter: FinactSavingAdapter

    var goalIDArrayList = ArrayList<String>()
    var savingsArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<GoalFilter>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goalIDArrayList.clear()
        savingsArrayList.clear()
        getSaving()
        //getGoals()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentSavingBinding.inflate(inflater, container, false)
        return binding.root
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){ }

    private fun getSaving() {
        goalIDArrayList.clear()
        //TODO: GET GOALS OF SELECTED CHILD
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialActivities").whereEqualTo("financialActivityName", "Saving").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            for (saving in results) {
                var savingActivity = saving.toObject<FinancialActivities>()
                savingsArrayList.add(savingActivity)

                goalIDArrayList.add(savingActivity.financialGoalID.toString())
                loadRecyclerView(goalIDArrayList)
            }
        }
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        savingAdapter = FinactSavingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = savingAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        savingAdapter.notifyDataSetChanged()
    }
}