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
import ph.edu.dlsu.finwise.databinding.FragmentEarningCompletedBinding
import ph.edu.dlsu.finwise.databinding.FragmentEarningOverdueBinding
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.Users
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class EarningOverdueFragment : Fragment() {

    private lateinit var binding: FragmentEarningOverdueBinding
    private lateinit var earningOverdueAdapter:EarningOverdueAdapter

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
        binding = FragmentEarningOverdueBinding.inflate(inflater, container, false)
        return binding .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getOverdueEarning()
    }

    private fun getOverdueEarning() {
        var earningOverdueArrayList = ArrayList<String>()
        firestore.collection("EarningActivities").whereEqualTo("childID", childID).whereEqualTo("status", "Ongoing").get().addOnSuccessListener { results ->
            var dateToday = Date()
            for (earning in results){
                var earningObject = earning.toObject<EarningActivityModel>()
                if (dateToday.after(earningObject.targetDate!!.toDate()))
                    earningOverdueArrayList.add(earning.id)
            }

            if (!earningOverdueArrayList.isEmpty())
                loadRecyclerView(earningOverdueArrayList)
            else
                emptyList()
            binding.rvViewActivitiesCompleted.visibility = View.VISIBLE
            binding.loadingItems.stopShimmer()
            binding.loadingItems.visibility = View.GONE
        }
    }

    private fun loadRecyclerView(earningOverdueArrayList: ArrayList<String>) {
        earningOverdueAdapter = EarningOverdueAdapter(requireActivity().applicationContext, earningOverdueArrayList)
        binding.rvViewActivitiesCompleted.adapter = earningOverdueAdapter
        binding.rvViewActivitiesCompleted.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
        earningOverdueAdapter.notifyDataSetChanged()
    }

    private fun emptyList() {
        binding.rvViewActivitiesCompleted.visibility = View.GONE
        binding.layoutEmptyActivity.visibility = View.VISIBLE
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            var userType = it.toObject<Users>()?.userType
            if (userType == "Parent")
                binding.tvEmptyListMessage.text = "Your child doesn't have any overdue chores."
            else if (userType == "Child")
                binding.tvEmptyListMessage.text = "Good job! You've been completing your chores on time!"
        }
    }

}