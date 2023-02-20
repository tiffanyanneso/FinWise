package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentTransactionSortBinding
import ph.edu.dlsu.finwise.personalFinancialManagementModule.TransactionHistoryActivity
import java.text.SimpleDateFormat
import java.util.Date

class TransactionSortFragment : DialogFragment() {
    private lateinit var binding : FragmentTransactionSortBinding
    var bundle: Bundle? = null
    private var minAmount: Float? = null
    private var maxAmount: Float? = null
    private var startDate: Date? = null
    private var endDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_sort, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionSortBinding.bind(view)
        setDialogSize()
        initializeDatePicker(binding.etStartDate, "startDate")
        initializeDatePicker(binding.etEndDate, "endDate")
        initializeBack()
        sortTransactions()

    }

    private fun initializeBack() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sortTransactions() {
        binding.btnDone.setOnClickListener {
            bundle = Bundle()
            val isAmountSorted: Boolean = setAmountSort()
            val isDateSorted: Boolean = setDateSort()
            val isCategorySorted: Boolean = setCategory()
            if ((isAmountSorted || isDateSorted) && isCategorySorted) {
                val goToSortedTransactions = Intent(context, TransactionHistoryActivity::class.java)
                goToSortedTransactions.putExtras(bundle!!)
                startActivity(goToSortedTransactions)
            } else if ((!isAmountSorted && !isDateSorted) && isCategorySorted)
                Toast.makeText(context, "Please input an amount and/or date", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAmountSort(): Boolean {
        var flag = false
        val minAmountInput = binding.etMinAmount
        val maxAmountInput = binding.etMaxAmount

        if (minAmountInput.text.isNotEmpty() || maxAmountInput.text.isNotEmpty()) {
            flag = setAmount(minAmountInput, maxAmountInput)
        } else {
            minAmountInput.error = null
            maxAmountInput.error = null
        }

        return flag
    }

    private fun validateAmount(minAmountInput: EditText): Boolean {
        var flag = false

        if (minAmount!! >= maxAmount!!) {
            minAmountInput.error = "Please enter an amount lower than the maximum amount"
            minAmountInput.requestFocus()
        } else {
            flag = true
            bundle?.putFloat("minAmount", minAmount!!)
            bundle?.putFloat("maxAmount", maxAmount!!)
        }

        return flag
    }

    private fun setAmount(minAmountInput: EditText, maxAmountInput: EditText): Boolean {
        var flag = false
        var isNotEmpty = false

        if (minAmountInput.text.isNotEmpty() && maxAmountInput.text.isNotEmpty()) {
            minAmount = minAmountInput.text.toString().toFloat()
            maxAmount = maxAmountInput.text.toString().toFloat()
            isNotEmpty = true
        } else if (minAmountInput.text.isEmpty()) {
            minAmountInput.error = "Please enter an amount lower than the maximum amount"
            minAmountInput.requestFocus()
        }
        else if (maxAmountInput.text.isEmpty()) {
            maxAmountInput.error = "Please enter an amount higher than the minimum amount"
            maxAmountInput.requestFocus()
        }

        if (isNotEmpty) {
            flag = validateAmount(minAmountInput)
        }

        return flag
    }

    private fun setDateSort(): Boolean {
        var flag = false
        val startDateInput = binding.etStartDate
        val endDateInput = binding.etEndDate

        if (startDateInput.text!!.isNotEmpty() || endDateInput.text!!.isNotEmpty()) {
            flag = validateDate(startDateInput, endDateInput)
        }
        return flag
    }

    private fun validateDate(startDateInput: TextInputEditText, endDateInput: TextInputEditText): Boolean {
        var flag = false
        if (startDate == null) {
            startDateInput.error = "Please enter a start date lower than the end date"
            startDateInput.requestFocus()
            endDateInput.error = null
        } else if (endDate == null) {
            endDateInput.error = "Please enter a end date higher than the start date"
            endDateInput.requestFocus()
            startDateInput.error = null
        } else if ( startDate!! >= endDate) {
            startDateInput.error = "Please enter a start date lower than the end date"
            startDateInput.requestFocus()
            endDateInput.error = null
        }
        else {
            flag = true
            startDateInput.error = null
        }

        if (flag) {
            bundle?.putSerializable("startDate", startDate)
            bundle?.putSerializable("endDate", endDate)
        }

        return flag
    }

    private fun setCategory(): Boolean {
        val cbIncome = binding.btnIncome
        val cbExpense = binding.btnExpense
        var checkedBoxes = "none"
        var flag = true

        if (cbIncome.isChecked && cbExpense.isChecked)
            checkedBoxes = "both"
        else if (cbIncome.isChecked)
            checkedBoxes = "income"
        else if (cbExpense.isChecked)
            checkedBoxes = "expense"

        bundle?.putString("checkedBoxes", checkedBoxes)


        if (checkedBoxes == "none") {
            flag = false
            binding.tvError.visibility = View.VISIBLE
        } else binding.tvError.visibility = View.GONE


        return flag
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeDatePicker(dateBinding: TextInputEditText, date: String) {
        dateBinding.setOnClickListener{
            val dialog = Dialog(requireContext())

            dialog.setContentView(R.layout.dialog_calendar)
            dialog.window!!.setLayout(1000, 1300)

            val calendar = dialog.findViewById<DatePicker>(R.id.et_date)

            dialog.show()
            calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
                dateBinding.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
                if (date == "startDate")
                    startDate = SimpleDateFormat("MM/dd/yyyy").parse((mMonth + 1).toString() + "/" +
                            mDay.toString() + "/" + mYear.toString()) as Date
                else if (date == "endDate")
                    endDate = SimpleDateFormat("MM/dd/yyyy").parse((mMonth + 1).toString() + "/" +
                            mDay.toString() + "/" + mYear.toString()) as Date

                dialog.dismiss()
            }
        }
    }

    private fun setDialogSize() {
        val width = resources.getDimensionPixelSize(R.dimen.sort_popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.sort_popup_height)
        dialog!!.window!!.setLayout(width, height)
    }





}