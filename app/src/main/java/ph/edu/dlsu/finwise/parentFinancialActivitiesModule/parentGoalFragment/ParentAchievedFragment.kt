package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentGoalFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.FinactSavingAdapter
import ph.edu.dlsu.finwise.databinding.FragmentParentAchievedBinding

class ParentAchievedFragment : Fragment() {

    private lateinit var binding: FragmentParentAchievedBinding
    private var firestore = Firebase.firestore
    private lateinit var goalAdapter: FinactSavingAdapter

    private lateinit var childID:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        childID = bundle?.getString("childID").toString()
        getAchievedGoals()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentAchievedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private fun getAchievedGoals() {
        var goalIDArrayList = ArrayList<String>()
        goalIDArrayList.clear()


        firestore.collection("FinancialGoals").whereEqualTo("childID", childID).whereEqualTo("status", "Achieved").get().addOnSuccessListener { documents ->
            for (goalSnapshot in documents) {
                //creating the object from list retrieved in db
                val goalID = goalSnapshot.id
                goalIDArrayList.add(goalID)
            }
            loadRecyclerView(goalIDArrayList)
        }
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        goalAdapter = FinactSavingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = goalAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext, LinearLayoutManager.VERTICAL, false)
        goalAdapter.notifyDataSetChanged()
    }
}