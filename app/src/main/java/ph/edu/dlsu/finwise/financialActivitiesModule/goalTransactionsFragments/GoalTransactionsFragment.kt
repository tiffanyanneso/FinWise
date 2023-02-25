package ph.edu.dlsu.finwise.financialActivitiesModule.goalTransactionsFragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.GoalTransactionsAdapater
import ph.edu.dlsu.finwise.adapter.GoalTransactionsHistoryAdapater
import ph.edu.dlsu.finwise.databinding.FragmentGoalExpenseBinding
import ph.edu.dlsu.finwise.databinding.FragmentGoalTransactionHistoryBinding
import ph.edu.dlsu.finwise.model.Transactions
import java.util.*
import kotlin.collections.ArrayList

class GoalTransactionsFragment : Fragment() {

    private lateinit var binding: FragmentGoalTransactionHistoryBinding
    private var firestore = Firebase.firestore

    private lateinit var savingActivityID:String

    private lateinit var goalTransactionsAdapter: GoalTransactionsHistoryAdapater
    private var transactionsIDArrayList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoalTransactionHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGoalTransactionHistoryBinding.bind(view)
        val bundle = arguments
        savingActivityID = bundle?.getString("savingActivityID").toString()
        getGoalTransactions()
    }

    private fun getGoalTransactions() {
        transactionsIDArrayList.clear()
        var transactionFilterArrayList = ArrayList<TransactionFilter>()

        firestore.collection("Transactions").whereEqualTo("financialActivityID", savingActivityID).get().addOnSuccessListener { results ->
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()
                transactionFilterArrayList.add(TransactionFilter(transaction.id, transactionObject.date!!.toDate()))
            }

            transactionFilterArrayList.sortByDescending { it.transactionDate }
            for (filter in transactionFilterArrayList)
                transactionsIDArrayList.add(filter.transactionID)

            goalTransactionsAdapter = GoalTransactionsHistoryAdapater(requireActivity().applicationContext, transactionsIDArrayList)
            binding.rvViewTransactions.adapter = goalTransactionsAdapter
            binding.rvViewTransactions.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
            goalTransactionsAdapter.notifyDataSetChanged()
        }
    }

    class TransactionFilter(var transactionID:String, var transactionDate: Date)

}