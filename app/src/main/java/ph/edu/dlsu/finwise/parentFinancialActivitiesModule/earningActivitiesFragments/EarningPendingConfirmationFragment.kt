package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.earningActivitiesFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.EarningCompletedAdapter
import ph.edu.dlsu.finwise.adapter.EarningPendingConfirmationAdapter
import ph.edu.dlsu.finwise.databinding.FragmentEarningCompletedBinding
import ph.edu.dlsu.finwise.databinding.FragmentEarningPendingConfirmationBinding

class EarningPendingConfirmationFragment : Fragment() {

    private lateinit var binding: FragmentEarningPendingConfirmationBinding
    private lateinit var earningPendingConfirmationAdapter: EarningPendingConfirmationAdapter

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

            earningPendingConfirmationAdapter = EarningPendingConfirmationAdapter(requireActivity().applicationContext, earningPendingArrayList)
            binding.rvViewActivitiesCompleted.adapter = earningPendingConfirmationAdapter
            binding.rvViewActivitiesCompleted.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
            earningPendingConfirmationAdapter.notifyDataSetChanged()
        }
    }

}