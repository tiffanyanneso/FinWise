package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.FragmentSellingItemBinding
import ph.edu.dlsu.finwise.databinding.FragmentTransactionBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.SellingItems
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.text.SimpleDateFormat


class SellingDetailsFragment : DialogFragment() {
    private lateinit var binding: FragmentSellingItemBinding
    var bundle = Bundle()
    private var firestore = Firebase.firestore

    private lateinit var sellingObject :SellingItems



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(ph.edu.dlsu.finwise.R.layout.fragment_selling_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSellingItemBinding.bind(view)
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
        val sellingID = bundle?.getString("sellingID")

        if (sellingID != null) {
            firestore.collection("SellingItems").document(sellingID).get().addOnSuccessListener { document ->
                if (document != null) {
                    sellingObject = document.toObject<SellingItems>()!!
                    binding.tvName.text = sellingObject.itemName
                    binding.tvDepositTo.text = sellingObject.depositTo
                    binding.tvPaymentType.text = sellingObject.paymentType
                    binding.tvDate.text = SimpleDateFormat("MM/dd/yyyy").format(sellingObject.date!!.toDate())
                    binding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(sellingObject.amount)

                    if (sellingObject?.depositTo == "Financial Goal") {
                        firestore.collection("FinancialActivities").document(sellingObject?.savingActivityID!!).get().addOnSuccessListener {
                            var goalID = it.toObject<FinancialActivities>()!!.financialGoalID
                            firestore.collection("FinancialGoals").document(goalID!!).get().addOnSuccessListener {
                                binding.tvGoalName.text = it.toObject<FinancialGoals>()!!.goalName
                                binding.layoutGoalName.visibility = View.VISIBLE
                            }
                        }
                    } else
                        binding.layoutGoalName.visibility = View.GONE
                }
            }
        }
    }

    private fun initializeButton() {
        binding.btnDone.setOnClickListener {
            dismiss()
        }
    }
}