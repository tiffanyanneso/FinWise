package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.FragmentTransactionBinding
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.text.SimpleDateFormat


class TransactionFragment : DialogFragment() {
    private lateinit var binding: FragmentTransactionBinding
    var bundle = Bundle()
    private var firestore = Firebase.firestore
    lateinit var transaction: Transactions



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(ph.edu.dlsu.finwise.R.layout.fragment_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionBinding.bind(view)
        loadTransactionDetails()
        initializeButton()
        setDialogSize()
    }

    private fun setDialogSize() {
        val width = resources.getDimensionPixelSize(ph.edu.dlsu.finwise.R.dimen.transaction_popup_width)
        val height = resources.getDimensionPixelSize(ph.edu.dlsu.finwise.R.dimen.transaction_popup_height)
        dialog!!.window!!.setLayout(width, height)
    }

    private fun loadTransactionDetails() {
        val bundle = arguments
        val transactionID = bundle?.getString("transactionID")

        if (transactionID != null) {
            firestore.collection("Transactions").document(transactionID).get().addOnSuccessListener { document ->
                if (document != null) {
                    transaction = document.toObject<Transactions>()!!
                    val name = transaction.transactionName.toString()
                    if (transaction.category == "Goal")
                        initializeText(name, false)
                    else if (transaction.transactionType == "Expense (Maya)")
                        initializeText(name, true)
                    else initializeText(name, false)

                }
            }
        }
    }

    private fun initializeText(name: String, isPayMayaExpense: Boolean) {
        val dec = DecimalFormat("#,###.00")
        val amount = dec.format(transaction.amount)
        binding.tvTransactionType.text = transaction.transactionType.toString()
        binding.tvAmount.text = "₱$amount"
        binding.tvName.text = name
        binding.tvCategory.text = transaction.category.toString()
        // convert timestamp to date string
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val date = formatter.format(transaction.date?.toDate()!!)
        binding.tvDate.text = date.toString()

        if (isPayMayaExpense)
            payMayaTransaction()
    }

    private fun payMayaTransaction() {
        binding.tvName.text = "example"
        binding.tvMerchant.visibility = View.VISIBLE
        binding.tvMerchantLabel.visibility = View.VISIBLE
        binding.tvMerchant.text = transaction.merchant.toString()
    }

    private fun initializeButton() {
        binding.btnDone.setOnClickListener {
            dismiss()
        }
    }
}