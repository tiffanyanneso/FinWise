package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentTransactionSortBinding

class TransactionSortFragment : DialogFragment() {
    private lateinit var binding: FragmentTransactionSortBinding
    lateinit var bundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_sort, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionSortBinding.bind(view)
        setDialogSize()

    }

    private fun setDialogSize() {
        val width = resources.getDimensionPixelSize(R.dimen.transaction_popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.transaction_popup_height)
        dialog!!.window!!.setLayout(width, height)
    }



}