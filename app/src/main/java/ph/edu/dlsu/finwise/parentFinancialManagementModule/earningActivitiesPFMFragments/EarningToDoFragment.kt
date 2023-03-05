package ph.edu.dlsu.finwise.parentFinancialManagementModule.earningActivitiesPFMFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.EarningToDoAdapter
import ph.edu.dlsu.finwise.adapter.EarningToDoPFMAdapter
import ph.edu.dlsu.finwise.databinding.FragmentEarningToDoBinding

class EarningToDoPFMFragment : Fragment() {

    private lateinit var binding:FragmentEarningToDoBinding
    private lateinit var earningToDoAdapter: EarningToDoPFMAdapter
    private lateinit var childID:String

    private var firestore = Firebase.firestore

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
        binding = FragmentEarningToDoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getToDoEarning()
    }

    private fun getToDoEarning() {
        var earningToDoArrayList = ArrayList<String>()
        firestore.collection("EarningActivities").whereEqualTo("childID", childID).whereEqualTo("source", "PFM").whereEqualTo("status", "Ongoing").get().addOnSuccessListener { results ->
            for (earning in results)
                earningToDoArrayList.add(earning.id)

            earningToDoAdapter = EarningToDoPFMAdapter(requireActivity().applicationContext, earningToDoArrayList)
            binding.rvViewActivitiesToDo.adapter = earningToDoAdapter
            binding.rvViewActivitiesToDo.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
            earningToDoAdapter.notifyDataSetChanged()
        }
    }
}