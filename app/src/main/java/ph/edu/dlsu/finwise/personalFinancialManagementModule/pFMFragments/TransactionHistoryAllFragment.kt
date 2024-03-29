package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.TransactionsAdapter
import ph.edu.dlsu.finwise.databinding.FragmentTransactionHistoryAllBinding
import ph.edu.dlsu.finwise.model.Transactions
import java.text.SimpleDateFormat
import java.util.*

class TransactionHistoryAllFragment : Fragment() {
    private lateinit var binding: FragmentTransactionHistoryAllBinding
    private var firestore = Firebase.firestore
    private lateinit var transactionAdapter: TransactionsAdapter
    private var childID  = FirebaseAuth.getInstance().currentUser!!.uid
    private var checkedBoxes: String? = null
    private var minAmount: String? = null
    private var maxAmount: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private val transactionIDArrayList = ArrayList<String>()
    private var transactionFilterArrayList = ArrayList<TransactionFilter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_transaction_history_income, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionHistoryAllBinding.bind(view)
        getIncomeTransactions()
       /* val childID  = FirebaseAuth.getInstance().currentUser!!.uid
        Toast.makeText(context, ""+childID, Toast.LENGTH_SHORT).show()*/
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sortTransactions(): ArrayList<TransactionFilter> {
        checkedBoxes = arguments?.getString("checkedBoxes").toString()
        getBundleValues()
        transactionFilterArrayList = checkSort()
        /*if (checkedBoxes == "both" || checkedBoxes == "income" || checkedBoxes == "expense" ) {
            getBundleValues()
            transactionFilterArrayList = checkSort()
        }*/
        return transactionFilterArrayList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkSort(): ArrayList<TransactionFilter> {

       if (minAmount?.toFloat()!! > 0.00f && startDate != "null")
            transactionFilterArrayList = sortAmountAndDate()
        else if (minAmount?.toFloat()!! > 0.00f)
            transactionFilterArrayList = sortAmount()
        else if (startDate != "null")
            transactionFilterArrayList = sortDate()

        return transactionFilterArrayList
    }

    private fun sortDate(): ArrayList<TransactionFilter> {
        val filteredArray = ArrayList<TransactionFilter>()
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


    private fun sortAmount(): ArrayList<TransactionFilter> {
        val filteredArray = ArrayList<TransactionFilter>()
        for (t in transactionFilterArrayList) {
            if (t.transaction?.amount!! >= minAmount!!.toFloat() &&
                t.transaction?.amount!! <= maxAmount!!.toFloat())
                filteredArray.add(TransactionFilter(t.transactionID, t.transaction))
        }
        transactionFilterArrayList.clear()
        return filteredArray
    }

    private fun sortAmountAndDate(): ArrayList<TransactionFilter> {
        val filteredArray = ArrayList<TransactionFilter>()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getIncomeTransactions() {
        transactionIDArrayList.clear()
        transactionFilterArrayList.clear()
        getChildID()
        firestore.collection("Transactions").whereEqualTo("userID", childID)
            .get().addOnSuccessListener { documents ->
            for (transactionSnapshot in documents) {
                //creating the object from list retrieved in db
                val transactionID = transactionSnapshot.id
                val transaction = transactionSnapshot.toObject<Transactions>()

                transactionFilterArrayList.add(TransactionFilter(transactionID, transaction))
            }
            transactionFilterArrayList.sortByDescending { it.transaction?.date }
            transactionFilterArrayList = sortTransactions()

            for (transactionFilter in transactionFilterArrayList)
                transactionIDArrayList.add(transactionFilter.transactionID!!)

           if (!transactionIDArrayList.isEmpty())
               loadRecyclerView(transactionIDArrayList)
           else {
               binding.scrollTransactions.visibility = View.GONE
               binding.layoutEmptyTransaction.visibility = View.VISIBLE
           }
        }
    }

    private fun getChildID() {
        childID = arguments?.getString("childID").toString()
    }

    class TransactionFilter(var transactionID: String?=null, var transaction: Transactions?=null)

    private fun loadRecyclerView(transactionIDArrayList: ArrayList<String>) {
        if (isAdded) {
            transactionAdapter = TransactionsAdapter(requireActivity(), transactionIDArrayList)
            binding.rvViewTransactions.adapter = transactionAdapter
            binding.rvViewTransactions.layoutManager = LinearLayoutManager(requireContext().applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
            binding.loadingItems.stopShimmer()
            binding.loadingItems.visibility = View.GONE
            binding.rvViewTransactions.visibility = View.VISIBLE
        }
    }
}