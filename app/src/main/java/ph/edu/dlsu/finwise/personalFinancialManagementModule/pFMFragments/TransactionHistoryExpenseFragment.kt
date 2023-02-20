package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.annotation.SuppressLint
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
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.TransactionsAdapter
import ph.edu.dlsu.finwise.databinding.FragmentTransactionHistoryExpenseBinding
import ph.edu.dlsu.finwise.model.Transactions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TransactionHistoryExpenseFragment : Fragment() {

    private lateinit var binding: FragmentTransactionHistoryExpenseBinding
    private var firestore = Firebase.firestore
    private lateinit var transactionAdapter: TransactionsAdapter
    private var checkedBoxes: String? = null
    private var minAmount: String? = null
    private var maxAmount: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private val expenseIDArrayList = ArrayList<String>()
    private var transactionFilterArrayList = ArrayList<TransactionFilter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_transaction_history_expense, container, false)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionHistoryExpenseBinding.bind(view)
        getExpenseTransactions()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getExpenseTransactions() {
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
                    transactionFilterArrayList.add(
                        TransactionFilter(
                            transactionID,
                            transaction
                        )
                    )
                }
            }
            transactionFilterArrayList.sortBy { it.transaction?.date }
            transactionFilterArrayList = sortTransactions()

            for (transactionFilter in transactionFilterArrayList)
                expenseIDArrayList.add(transactionFilter.transactionID!!)

            loadRecyclerView(expenseIDArrayList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sortTransactions(): java.util.ArrayList<TransactionFilter> {
        checkedBoxes = arguments?.getString("checkedBoxes").toString()

        if (checkedBoxes == "both" || checkedBoxes == "expense") {
            getBundleValues()
            transactionFilterArrayList = checkSort()
        } else if (checkedBoxes == "income") transactionFilterArrayList.clear()
        return transactionFilterArrayList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkSort(): java.util.ArrayList<TransactionFilter> {

        if (minAmount?.toFloat()!! > 0.00f && startDate != "null")
            transactionFilterArrayList = sortAmountAndDate()
        else if (minAmount?.toFloat()!! > 0.00f)
            transactionFilterArrayList = sortAmount()
        else if (startDate != "null")
            transactionFilterArrayList = sortDate()

        return transactionFilterArrayList
    }

    private fun sortDate(): java.util.ArrayList<TransactionFilter> {
        val filteredArray = java.util.ArrayList<TransactionFilter>()
        for (t in transactionFilterArrayList) {
            // Convert dates to same string to be compared
            val sdf = SimpleDateFormat(
                "EE MMM dd HH:mm:ss z yyyy",
                Locale.ENGLISH
            )
            val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)

            val sDate = startDate?.let { sdf.parse(it) }
            val startingDate = formatter.format(sDate!!)
            val eDate = endDate?.let { sdf.parse(it) }
            val endingDate = formatter.format(eDate!!)
            val transactionDate = formatter.format(t.transaction?.date?.toDate()!!)

            if (transactionDate in startingDate..endingDate)
                filteredArray.add(TransactionFilter(t.transactionID, t.transaction))

        }
        transactionFilterArrayList.clear()
        return filteredArray
    }


    private fun sortAmount(): java.util.ArrayList<TransactionFilter> {
        val filteredArray = java.util.ArrayList<TransactionFilter>()
        for (t in transactionFilterArrayList) {
            if (t.transaction?.amount!! >= minAmount!!.toFloat() &&
                t.transaction?.amount!! <= maxAmount!!.toFloat())
                filteredArray.add(TransactionFilter(t.transactionID, t.transaction))
        }
        transactionFilterArrayList.clear()
        return filteredArray
    }

    private fun sortAmountAndDate(): java.util.ArrayList<TransactionFilter> {
        val filteredArray = java.util.ArrayList<TransactionFilter>()
        for (t in transactionFilterArrayList) {
            val sdf = SimpleDateFormat(
                "EE MMM dd HH:mm:ss z yyyy",
                Locale.ENGLISH
            )
            val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)

            val sDate = startDate?.let { sdf.parse(it) }
            val startingDate = formatter.format(sDate!!)
            val eDate = endDate?.let { sdf.parse(it) }
            val endingDate = formatter.format(eDate!!)
            val transactionDate = formatter.format(t.transaction?.date?.toDate()!!)

            if (t.transaction?.amount!! >= minAmount!!.toFloat() &&
                t.transaction?.amount!! <= maxAmount!!.toFloat()
                && transactionDate in startingDate..endingDate)
                filteredArray.add(TransactionFilter(t.transactionID, t.transaction))
        }
        transactionFilterArrayList.clear()
        return filteredArray
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getBundleValues() {
        minAmount = arguments?.getString("minAmount").toString()
        maxAmount = arguments?.getString("maxAmount").toString()
        startDate= arguments?.getSerializable("startDate").toString()
        endDate = arguments?.getSerializable("endDate").toString()
    }

    //To get the ID with the transaction object because it will be passed to the adapter to be loaded there
    class TransactionFilter(var transactionID: String?=null, var transaction: Transactions?=null)

    @SuppressLint("NotifyDataSetChanged")
    private fun loadRecyclerView(transactionIDArrayList: ArrayList<String>) {
        transactionAdapter = TransactionsAdapter(requireActivity(), transactionIDArrayList)
        binding.rvViewTransactions.adapter = transactionAdapter
        binding.rvViewTransactions.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        transactionAdapter.notifyDataSetChanged()
    }


}