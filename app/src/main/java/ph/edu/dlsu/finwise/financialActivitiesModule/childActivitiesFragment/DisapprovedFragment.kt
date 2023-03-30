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
import ph.edu.dlsu.finwise.adapter.FinactDisapprovedAdapter
import ph.edu.dlsu.finwise.adapter.FinactSavingAdapter
import ph.edu.dlsu.finwise.databinding.FragmentDisapprovedBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.util.*
import kotlin.collections.ArrayList

class DisapprovedFragment : Fragment() {

    private lateinit var binding: FragmentDisapprovedBinding
    private var firestore = Firebase.firestore
    private lateinit var goalAdapter: FinactDisapprovedAdapter

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDisapprovedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDisapprovedGoals()
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }


    private fun getDisapprovedGoals() {
        var goalIDArrayList = ArrayList<String>()
        var goalFilterArrayList = ArrayList<GoalFilter>()

        goalIDArrayList.clear()

        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).whereEqualTo("status", "Disapproved").get().addOnSuccessListener { documents ->
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
            loadRecyclerView(goalIDArrayList)
        }
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        if (isAdded) {
            goalAdapter = FinactDisapprovedAdapter(requireActivity(), goalIDArrayList)
            binding.rvViewGoals.adapter = goalAdapter
            binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
            goalAdapter.notifyDataSetChanged()
        }

    }
}