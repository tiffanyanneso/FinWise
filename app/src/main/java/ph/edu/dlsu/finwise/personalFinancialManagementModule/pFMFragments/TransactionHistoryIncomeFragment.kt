package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.TransactionsAdapter
import ph.edu.dlsu.finwise.databinding.FragmentTransactionHistoryIncomeBinding
import ph.edu.dlsu.finwise.model.Transactions
import java.text.SimpleDateFormat
import java.util.*

class TransactionHistoryIncomeFragment : Fragment() {
    private lateinit var binding: FragmentTransactionHistoryIncomeBinding
    private var firestore = Firebase.firestore
    private lateinit var transactionAdapter: TransactionsAdapter
    private var checkedBoxes: String? = null
    private var minAmount: String? = null
    private var maxAmount: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private val incomeIDArrayList = ArrayList<String>()
    var transactionFilterArrayList = ArrayList<TransactionFilter>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionHistoryIncomeBinding.bind(view)
        getIncomeTransactions()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sortTransactions(): ArrayList<TransactionFilter> {
        checkedBoxes = arguments?.getString("checkedBoxes").toString()

        if (checkedBoxes == "both" || checkedBoxes == "income") {
            getBundleValues()
            transactionFilterArrayList = checkSort()
        }
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
            val sdf = SimpleDateFormat(
                "EE MMM dd HH:mm:ss z yyyy",
                Locale.ENGLISH
            )
            val sDate = startDate?.let { sdf.parse(it) }
            val eDate = endDate?.let { sdf.parse(it) }
            val dateConverted = SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy").parse(t.transaction?.date.toString())

            if (dateConverted!! >= sDate && dateConverted >= eDate)
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
            val startDateConverted = startDate?.let { sdf.parse(it) }
            val endDateConverted = endDate?.let { sdf.parse(it) }
            val dateConverted = sdf.parse(t.transaction?.date.toString())
            if (t.transaction?.amount!! >= minAmount!!.toFloat() &&
                t.transaction?.amount!! <= maxAmount!!.toFloat()
                && dateConverted!! >= startDateConverted && dateConverted >= endDateConverted)
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
        /*val sdf = SimpleDateFormat(
            "EE MMM dd HH:mm:ss z yyyy",
            Locale.ENGLISH
        )
        val startDateConverted = startDate?.let { sdf.parse(it) }
        val print = SimpleDateFormat("MM/dd/yyyy").format(startDateConverted)
        Toast.makeText(context, ""+print, Toast.LENGTH_SHORT).show()*/

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_transaction_history_income, container, false)
    }

    private fun loadRecyclerView(transactionIDArrayList: ArrayList<String>) {
        transactionAdapter = TransactionsAdapter(requireActivity(), transactionIDArrayList)
        binding.rvViewTransactions.adapter = transactionAdapter
        binding.rvViewTransactions.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        transactionAdapter.notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getIncomeTransactions() {
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

                if (transaction.transactionType == "Income" || transaction.transactionType == "Withdrawal") {
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
                incomeIDArrayList.add(transactionFilter.transactionID!!)

            loadRecyclerView(incomeIDArrayList)
        }
    }

    class TransactionFilter(var transactionID: String?=null, var transaction: Transactions?=null){
    }
}