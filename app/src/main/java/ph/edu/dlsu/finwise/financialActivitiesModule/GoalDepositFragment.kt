package ph.edu.dlsu.finwise.financialActivitiesModule

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
import ph.edu.dlsu.finwise.databinding.FragmentGoalDepositBinding
import ph.edu.dlsu.finwise.model.Transactions
import java.util.*
import kotlin.collections.ArrayList

class GoalDepositFragment : Fragment() {

    private lateinit var binding: FragmentGoalDepositBinding
    private var firestore = Firebase.firestore

    private lateinit var savingActivityID:String

    private lateinit var goalTransactionsAdapter: GoalTransactionsAdapater
    private var transactionsIDArrayList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoalDepositBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGoalDepositBinding.bind(view)
        var bundle = arguments
        savingActivityID = bundle?.getString("savingActivityID").toString()
        getGoalDeposits()
    }

    private fun getGoalDeposits() {
        var transactionFilterArrayList = ArrayList<TransactionFilter>()
        firestore.collection("Transactions").whereEqualTo("financialActivityID", savingActivityID).whereEqualTo("transactionType", "Deposit").get().addOnSuccessListener { results ->
            println("print results in deposits " + results.size())
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()
                transactionFilterArrayList.add(TransactionFilter(transaction.id, transactionObject.date!!.toDate()))
            }

            transactionFilterArrayList.sortBy { it.transactionDate }
            for (filter in transactionFilterArrayList)
                transactionsIDArrayList.add(filter.transactionID)

            goalTransactionsAdapter = GoalTransactionsAdapater(requireActivity().applicationContext, transactionsIDArrayList)
            binding.rvViewTransactions.adapter = goalTransactionsAdapter
            binding.rvViewTransactions.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
            goalTransactionsAdapter.notifyDataSetChanged()
        }
    }

    class TransactionFilter(var transactionID:String, var transactionDate: Date)
}