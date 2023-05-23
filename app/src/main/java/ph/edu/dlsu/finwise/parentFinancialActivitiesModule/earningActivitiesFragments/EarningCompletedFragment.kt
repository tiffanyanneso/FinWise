package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.earningActivitiesFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.EarningCompletedAdapter
import ph.edu.dlsu.finwise.databinding.FragmentEarningCompletedBinding
import ph.edu.dlsu.finwise.model.Users

class EarningCompletedFragment : Fragment() {

    private lateinit var binding: FragmentEarningCompletedBinding
    private lateinit var earningCompletedAdapter:EarningCompletedAdapter

    private lateinit var childID:String

    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var bundle = arguments
            childID = bundle?.getString("childID").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEarningCompletedBinding.inflate(inflater, container, false)
        return binding .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCompletedEarning()
    }

    private fun getCompletedEarning() {
        var earningCompleteArrayList = ArrayList<String>()
        firestore.collection("EarningActivities").whereEqualTo("childID", childID).whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
            for (earning in results)
                earningCompleteArrayList.add(earning.id)

            if (!earningCompleteArrayList.isEmpty())
                loadRecyclerView(earningCompleteArrayList)
            else
                emptyList()
            binding.rvViewActivitiesCompleted.visibility = View.VISIBLE
            binding.loadingItems.stopShimmer()
            binding.loadingItems.visibility = View.GONE
        }
    }

    private fun loadRecyclerView(earningCompleteArrayList: ArrayList<String>) {
        earningCompletedAdapter = EarningCompletedAdapter(requireActivity().applicationContext, earningCompleteArrayList)
        binding.rvViewActivitiesCompleted.adapter = earningCompletedAdapter
        binding.rvViewActivitiesCompleted.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
        earningCompletedAdapter.notifyDataSetChanged()
    }

    private fun emptyList() {
        binding.rvViewActivitiesCompleted.visibility = View.GONE
        binding.layoutEmptyActivity.visibility = View.VISIBLE
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            var userType = it.toObject<Users>()?.userType
            if (userType == "Parent")
                binding.tvEmptyListMessage.text = "Your child hasn't completed any chores yet."
            else if (userType == "Child")
                binding.tvEmptyListMessage.text = "Complete your chores to see them here."
        }
    }

}