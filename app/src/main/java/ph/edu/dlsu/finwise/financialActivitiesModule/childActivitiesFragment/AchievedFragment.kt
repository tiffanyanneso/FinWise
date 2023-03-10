package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.FinactAchievedAdapter
import ph.edu.dlsu.finwise.databinding.FragmentAchievedBinding

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
        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser).whereEqualTo("status", "Completed").get().addOnSuccessListener { documents ->
            for (goalSnapshot in documents)
                goalIDArrayList.add(goalSnapshot.id)

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
    }
}