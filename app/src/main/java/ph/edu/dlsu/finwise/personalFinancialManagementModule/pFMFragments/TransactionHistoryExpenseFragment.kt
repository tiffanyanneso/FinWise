package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.TransactionsAdapter
import ph.edu.dlsu.finwise.databinding.FragmentTransactionHistoryExpenseBinding
import ph.edu.dlsu.finwise.model.Transactions
import kotlin.collections.ArrayList

class TransactionHistoryExpenseFragment : Fragment() {

    private lateinit var binding: FragmentTransactionHistoryExpenseBinding
    private var firestore = Firebase.firestore
    private lateinit var transactionAdapter: TransactionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // getExpenseTransactions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionHistoryExpenseBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private fun loadRecyclerView(transactionIDArrayList: ArrayList<String>) {
        transactionAdapter = TransactionsAdapter(requireActivity(), transactionIDArrayList)
        binding.rvViewTransactions.adapter = transactionAdapter
        binding.rvViewTransactions.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        transactionAdapter.notifyDataSetChanged()
    }

    /* class TransactionFilter(var transactionID: String?=null, var transactionDate: Transactions =null){
    }

   private fun getExpenseTransactions() {
        var expenseIDArrayList = ArrayList<String>()
        var transactionFilterArrayList = ArrayList<TransactionFilter>()
//
//        //TODO:change to get transactions of current user
//        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
//        firestore.collection("Transactions").whereEqualTo("companyID", currentUser).get()
//            .addOnSuccessListener { documents ->

        firestore.collection("Transactions").get().addOnSuccessListener { documents ->
            for (transactionSnapshot in documents) {
                //creating the object from list retrieved in db

                val transactionID = transactionSnapshot.id
                val transaction = transactionSnapshot.toObject<Transactions>()

                if (transaction.transactionType == "Expense" || transaction.transactionType == "Deposit" ||
                    transaction.transactionType == "Expense (Maya)" ) {
                    transactionFilterArrayList.add(TransactionFilter(transactionID, transaction.date!!.toDate()))
                }
            }
            transactionFilterArrayList.sortBy { it.transactionDate }

            for (transactionFilter in transactionFilterArrayList)
                expenseIDArrayList.add(transactionFilter.transactionID!!)

            loadRecyclerView(expenseIDArrayList)
        }
    }*/
}