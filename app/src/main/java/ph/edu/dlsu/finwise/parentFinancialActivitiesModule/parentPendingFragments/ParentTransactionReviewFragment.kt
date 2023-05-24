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
import ph.edu.dlsu.finwise.adapter.TransactionNotifAdapter
import ph.edu.dlsu.finwise.databinding.FragmentParentPendingEarningBinding
import ph.edu.dlsu.finwise.databinding.FragmentParentTransactionBinding
import ph.edu.dlsu.finwise.databinding.FragmentParentTransactionReviewBinding
import ph.edu.dlsu.finwise.model.*

class ParentTransactionReviewFragment: Fragment() {

    private lateinit var binding: FragmentParentTransactionReviewBinding
    private var firestore = Firebase.firestore

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private var childIDArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        CoroutineScope(Dispatchers.Main).launch {
            loadChildren()
            getPendingEarning()
        }
    }

    private suspend fun getPendingEarning() {
        var transactionArrayList = ArrayList<String>()
        for (childID in childIDArrayList) {
            var transactions = firestore.collection("OverTransactions").whereEqualTo("childID", childID).get().await()
            if (!transactions.isEmpty) {
                for (transaction in transactions) {
                    var transactionObject = transaction.toObject<OverThresholdExpenseModel>()
                    transactionArrayList.add(transactionObject.transactionID!!)
                }
            }
        }

        if (!transactionArrayList.isEmpty()) {
            var transactionsAdapter = TransactionNotifAdapter(requireActivity().applicationContext, transactionArrayList)
            binding.rvTransactions.adapter = transactionsAdapter
            binding.rvTransactions.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
            transactionsAdapter.notifyDataSetChanged()
        } else
            binding.layoutEmptyActivity.visibility = View.VISIBLE

        binding.loadingItems.stopShimmer()
        binding.loadingItems.visibility = View.GONE
        binding.rvTransactions.visibility = View.VISIBLE
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
        binding = FragmentParentTransactionReviewBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


}