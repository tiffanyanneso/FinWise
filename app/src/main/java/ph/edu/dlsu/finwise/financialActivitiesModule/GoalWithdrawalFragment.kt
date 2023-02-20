package ph.edu.dlsu.finwise.financialActivitiesModule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.GoalTransactionsAdapater
import ph.edu.dlsu.finwise.databinding.FragmentGoalExpenseBinding
import ph.edu.dlsu.finwise.model.Transactions
import java.util.*
import kotlin.collections.ArrayList

class GoalWithdrawalFragment : Fragment() {

    private lateinit var binding: FragmentGoalExpenseBinding
    private var firestore = Firebase.firestore

    private lateinit var savingActivityID:String

    private lateinit var goalTransactionsAdapter: GoalTransactionsAdapater
    private var transactionsIDArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var bundle = arguments
            savingActivityID = bundle?.getString("savingActivityID").toString()
            getGoalWithdrawal()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalExpenseBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private fun getGoalWithdrawal() {
        var transactionFilterArrayList = ArrayList<TransactionFilter>()

        firestore.collection("Transactions").whereEqualTo("financialActivityID", savingActivityID).whereEqualTo("transactionType", "Withdrawal").get().addOnSuccessListener { results ->
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()
                transactionFilterArrayList.add(TransactionFilter(transaction.id, transactionObject.date!!.toDate()))
            }

            transactionFilterArrayList.sortBy { it.transactionDate }
            for (filter in transactionFilterArrayList)
                transactionsIDArrayList.add(filter.transactionID)

            goalTransactionsAdapter = GoalTransactionsAdapater(requireContext(), transactionsIDArrayList)
            binding.rvViewTransactions.adapter = goalTransactionsAdapter
            binding.rvViewTransactions.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            goalTransactionsAdapter.notifyDataSetChanged()
        }
    }

    class TransactionFilter(var transactionID:String, var transactionDate: Date)

}