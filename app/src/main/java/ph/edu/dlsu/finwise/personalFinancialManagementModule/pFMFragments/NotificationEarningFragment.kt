package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.adapter.ChildNotificationSummaryEarningAdapter
import ph.edu.dlsu.finwise.adapter.ParentPendingEarningAdapter
import ph.edu.dlsu.finwise.databinding.FragmentParentPendingEarningBinding
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.Users
import java.util.*
import kotlin.collections.ArrayList

class NotificationEarningFragment: Fragment() {

    private lateinit var binding: FragmentParentPendingEarningBinding
    private var firestore = Firebase.firestore

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var lastLogin: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lastLogin = arguments?.getSerializable("lastLogin") as Date
        CoroutineScope(Dispatchers.Main).launch {
            getPendingEarning()
        }
    }

    class EarningNotificationObject (var type:String, var earningID:String)

    private suspend fun getPendingEarning() {
        var earningActivitiesArrayList = ArrayList<EarningNotificationObject>()

        var newEarnings = firestore.collection("EarningActivities").whereEqualTo("childID", currentUser).whereEqualTo("status", "Ongoing").get().await()
        for (earning in newEarnings)
                earningActivitiesArrayList.add(EarningNotificationObject("New Earning", earning.id))

        var sentEarnings = firestore.collection("EarningActivities").whereEqualTo("childID", currentUser).whereEqualTo("status", "Completed").get().await()
        for (earning in sentEarnings) {
            var dateSent = earning.toObject<EarningActivityModel>().dateSent!!.toDate()
            if (dateSent.after(lastLogin))
                earningActivitiesArrayList.add(EarningNotificationObject("Reward Sent" , earning.id))
        }

        if (!earningActivitiesArrayList.isEmpty()) {
            var earningAdapter = ChildNotificationSummaryEarningAdapter(requireActivity().applicationContext, earningActivitiesArrayList)
            binding.rvEarning.adapter = earningAdapter
            binding.rvEarning.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
            earningAdapter.notifyDataSetChanged()
        } else
            binding.layoutEmptyActivity.visibility = View.VISIBLE

        binding.loadingItems.stopShimmer()
        binding.loadingItems.visibility = View.GONE
        binding.rvEarning.visibility = View.VISIBLE
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentPendingEarningBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


}