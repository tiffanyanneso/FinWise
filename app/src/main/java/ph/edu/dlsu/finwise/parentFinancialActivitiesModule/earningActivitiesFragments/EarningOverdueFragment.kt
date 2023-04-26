package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.earningActivitiesFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.EarningCompletedAdapter
import ph.edu.dlsu.finwise.adapter.EarningOverdueAdapter
import ph.edu.dlsu.finwise.databinding.FragmentEarningCompletedBinding
import ph.edu.dlsu.finwise.databinding.FragmentEarningOverdueBinding
import ph.edu.dlsu.finwise.model.EarningActivityModel
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class EarningOverdueFragment : Fragment() {

    private lateinit var binding: FragmentEarningOverdueBinding
    private lateinit var earningOverdueAdapter:EarningOverdueAdapter

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

            earningOverdueAdapter = EarningOverdueAdapter(requireActivity().applicationContext, earningOverdueArrayList)
            binding.rvViewActivitiesCompleted.adapter = earningOverdueAdapter
            binding.rvViewActivitiesCompleted.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
            earningOverdueAdapter.notifyDataSetChanged()
        }
    }

}