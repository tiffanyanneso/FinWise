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
import ph.edu.dlsu.finwise.adapter.ChildGoalAdapter
import ph.edu.dlsu.finwise.databinding.FragmentParentGoalSettingBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.util.*

class ParentGoalSettingFragment : Fragment() {

    private lateinit var binding: FragmentParentGoalSettingBinding
    private var firestore = Firebase.firestore
    private lateinit var goalAdapter: ChildGoalAdapter

    private lateinit var childID:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        childID = bundle?.getString("childID").toString()
        getForEditingGoals()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentGoalSettingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }

    private fun getForEditingGoals() {
        var goalIDArrayList = ArrayList<String>()
        var filter = "For Editing"
        var goalFilterArrayList = ArrayList<GoalFilter>()

        //TODO:change to get transactions of current user
        //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        //firestore.collection("Transactions").whereEqualTo("companyID", currentUser).get().addOnSuccessListener{ documents ->

        firestore.collection("FinancialGoals").whereEqualTo("status", filter).get().addOnSuccessListener { documents ->
            for (goalSnapshot in documents) {
                //creating the object from list retrieved in db
                var goalID = goalSnapshot.id
                var goal = goalSnapshot.toObject<FinancialGoals>()
                //goalIDArrayList.add(goalID)
                goalFilterArrayList.add(
                    GoalFilter(
                        goalID,
                        goal?.targetDate!!.toDate()
                    )
                )
            }
            goalFilterArrayList.sortBy { it.goalTargetDate }
            for (goalFilter in goalFilterArrayList)
                goalIDArrayList.add(goalFilter.financialGoalID!!)
            loadRecyclerView(goalIDArrayList)
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