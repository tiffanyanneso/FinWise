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
import ph.edu.dlsu.finwise.adapter.EarningOverdueAdapter
import ph.edu.dlsu.finwise.adapter.EarningPendingConfirmationAdapter
import ph.edu.dlsu.finwise.databinding.FragmentEarningCompletedBinding
import ph.edu.dlsu.finwise.databinding.FragmentEarningPendingConfirmationBinding
import ph.edu.dlsu.finwise.model.Users

class EarningPendingConfirmationFragment : Fragment() {

    private lateinit var binding: FragmentEarningPendingConfirmationBinding
    private lateinit var earningPendingConfirmationAdapter: EarningPendingConfirmationAdapter

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
        binding = FragmentEarningPendingConfirmationBinding.inflate(inflater, container, false)
        return binding .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCompletedEarning()
    }

    private fun getCompletedEarning() {
        var earningPendingArrayList = ArrayList<String>()
        firestore.collection("EarningActivities").whereEqualTo("childID", childID).whereEqualTo("status", "Pending").get().addOnSuccessListener { results ->
            for (earning in results)
                earningPendingArrayList.add(earning.id)

            if (isAdded) {
                if (!earningPendingArrayList.isEmpty())
                    loadRecyclerView(earningPendingArrayList)
                else
                    emptyList()
                binding.rvViewActivitiesCompleted.visibility = View.VISIBLE
                binding.loadingItems.stopShimmer()
                binding.loadingItems.visibility = View.GONE
            }
        }
    }

    private fun loadRecyclerView(earningPendingArrayList: ArrayList<String>) {
        earningPendingConfirmationAdapter = EarningPendingConfirmationAdapter(requireActivity().applicationContext, earningPendingArrayList)
        binding.rvViewActivitiesCompleted.adapter = earningPendingConfirmationAdapter
        binding.rvViewActivitiesCompleted.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
        earningPendingConfirmationAdapter.notifyDataSetChanged()
    }

    private fun emptyList() {
        binding.rvViewActivitiesCompleted.visibility = View.GONE
        binding.layoutEmptyActivity.visibility = View.VISIBLE
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            var userType = it.toObject<Users>()?.userType
            if (userType == "Parent")
                binding.tvEmptyListMessage.text = "There are no chores to check right now"
            else if (userType == "Child")
                binding.tvEmptyListMessage.text = "Complete your chores and have them reviewed to get your reward."
        }
    }

}