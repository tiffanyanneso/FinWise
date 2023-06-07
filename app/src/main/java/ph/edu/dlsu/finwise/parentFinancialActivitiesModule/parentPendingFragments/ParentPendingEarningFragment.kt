package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentPendingFragments

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
import ph.edu.dlsu.finwise.adapter.ParentPendingEarningAdapter
import ph.edu.dlsu.finwise.databinding.FragmentFinactBudgetingBinding
import ph.edu.dlsu.finwise.databinding.FragmentParentPendingEarningBinding
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.Users

class ParentPendingEarningFragment: Fragment() {

    private lateinit var binding: FragmentParentPendingEarningBinding
    private var firestore = Firebase.firestore

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private var childIDArrayList = ArrayList<String>()
    private var earningActivitiesArrayList = ArrayList<String>()

    private lateinit var earningReviewAdapter: ParentPendingEarningAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        childIDArrayList.clear()
        earningActivitiesArrayList.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            earningActivitiesArrayList.clear()
            loadChildren()
            getPendingEarning()
        }
    }

    private suspend fun getPendingEarning() {
        var lastLogin = firestore.collection("Users").document(currentUser).get().await().toObject<Users>()!!.lastLogin!!.toDate()
        for (childID in childIDArrayList) {
            var earnings = firestore.collection("EarningActivities").whereEqualTo("childID", childID).whereEqualTo("status", "Pending").get().await()
            for (earning in earnings) {
                var dateCompleted = earning.toObject<EarningActivityModel>().dateCompleted!!.toDate()
                if (dateCompleted.after(lastLogin))
                    earningActivitiesArrayList.add(earning.id)
            }
        }

        if (!earningActivitiesArrayList.isEmpty()) {
            binding.rvEarning.adapter = null
            earningReviewAdapter = ParentPendingEarningAdapter(requireActivity().applicationContext, earningActivitiesArrayList)
            binding.rvEarning.adapter = earningReviewAdapter
            binding.rvEarning.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
            earningReviewAdapter.notifyDataSetChanged()
        } else
            binding.layoutEmptyActivity.visibility = View.VISIBLE

        binding.loadingItems.stopShimmer()
        binding.loadingItems.visibility = View.GONE
        binding.rvEarning.visibility = View.VISIBLE
    }

    private suspend fun loadChildren() {
        var children =  firestore.collection("Users").whereEqualTo("userType", "Child").whereEqualTo("parentID", currentUser).get().await()
        if (children.size()!=0) {
            for (child in children)
                childIDArrayList.add(child.id)
        }
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