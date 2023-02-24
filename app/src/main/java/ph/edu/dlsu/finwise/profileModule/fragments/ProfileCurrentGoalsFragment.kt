package ph.edu.dlsu.finwise.profileModule.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.ProfileCurrentGoalsAdapter
import ph.edu.dlsu.finwise.databinding.FragmentProfileCurrentGoalsBinding

class ProfileCurrentGoalsFragment : Fragment() {

    private lateinit var binding:FragmentProfileCurrentGoalsBinding
    private var firestore = Firebase.firestore

    private lateinit var childID:String

    private var goalIDArrayList = ArrayList<String>()

    private lateinit var goalsAdapter:ProfileCurrentGoalsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var bundle = arguments
            childID = bundle?.getString("childID").toString()
            getCurrentGoals()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileCurrentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getCurrentGoals(){
        goalIDArrayList.clear()
        println("print " + childID)
        firestore.collection("FinancialGoals").whereEqualTo("childID", childID).whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            for (goal in results)
                goalIDArrayList.add(goal.id)

            goalsAdapter = ProfileCurrentGoalsAdapter(requireContext().applicationContext, goalIDArrayList)
            binding.rvCurrentGoals.adapter = goalsAdapter
            binding.rvCurrentGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }

}