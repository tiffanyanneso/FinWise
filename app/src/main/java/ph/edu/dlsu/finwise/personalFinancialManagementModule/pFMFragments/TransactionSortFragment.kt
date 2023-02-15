package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.app.Dialog
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
import java.text.SimpleDateFormat
import java.util.Date

class TransactionSortFragment : DialogFragment() {
    private lateinit var binding: FragmentTransactionSortBinding
    lateinit var bundle: Bundle
    var minAmount: Float? = null
    var maxAmount: Float? = null
    var isIncomeCategory = false
    var isExpenseCategory = false
    //TODO: check if this is the date format
    lateinit var startDate: Date
    lateinit var endDate: Date
    var isSortable = false



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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionSortBinding.bind(view)
        setDialogSize()
        initializeDatePicker(binding.etStartDatee, "startDate", R.id.et_date)
        initializeDatePicker(binding.etEndDatee, "endDate", R.id.et_end_date)
        sortTransactions()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sortTransactions() {
        binding.btnDone.setOnClickListener {
            setAmountSort()
            setDates()


            /* TODO:
        *    - Initialize default variables
        * - send if there are applied sort
        * - Validate fields
        *   - Amount
        *       - Validation
        *           - if there is min or max price, other field must have value
        *           - if min is lower than maximum
        *       - Initialize Values
        *       - Sort - if inside the min and max price
        *   -Date
        *       - Initialize Date to be clickable
        *       - Validation
        *           - if there is min or max price, other field must have value
        *           - if the end date is higher than the start date
        *           - Initialize Values
        *   - Category
        *       - Initialize what was clicked, else just apply to all
        *       - Redirect to which the sort will be applied
        *       - Show which fragment they will be directed to
        *   - Buttons
        *       - Cancel --> dismiss
        *       - Done --> Apply
        *    */
        }
    }

    private fun setDates() {
        var flag = false
        val startDatetInput = binding.etStartDatee
        val endDateInput = binding.etEndDatee
        Toast.makeText(context, "start "+startDatetInput + "end " + endDateInput, Toast.LENGTH_SHORT).show()
        /*if (minAmountInput.text.isNotEmpty() || maxAmountInput.text.isNotEmpty()) {
            setAmount(minAmountInput, maxAmountInput)

            if (minAmount!! >= maxAmount!!) {
                minAmountInput.error = "Please enter an amount lower than the maximum amount"
                minAmountInput.requestFocus()
                Toast.makeText(context, "min"+minAmount +"max"+maxAmount, Toast.LENGTH_SHORT).show()
            } else {
                flag = true
            }
        }*/
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeDatePicker(dateBinding: TextInputEditText, date: String, datePickerID: Int, ) {
        dateBinding.setOnClickListener{
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
            val dialog = Dialog(requireContext())

            dialog.setContentView(R.layout.dialog_calendar)
            dialog.window!!.setLayout(1000, 1200)

            var calendar = dialog.findViewById<DatePicker>(datePickerID)

            dialog.show()
            calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
                //dateBinding.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
                /*if (date == "startDate")
                startDate = SimpleDateFormat("MM/dd/yyyy").parse((mMonth + 1).toString() + "/" +
                        mDay.toString() + "/" + mYear.toString()) as Date
                else if (date == "endDate")
                    endDate = SimpleDateFormat("MM/dd/yyyy").parse((mMonth + 1).toString() + "/" +
                            mDay.toString() + "/" + mYear.toString()) as Date*/

                //dialog.dismiss()
            }
        }
    }



    private fun setAmountSort() {
        var flag = false
        val minAmountInput = binding.etMinAmount
        val maxAmountInput = binding.etMaxAmount


        if (minAmountInput.text.isNotEmpty() || maxAmountInput.text.isNotEmpty()) {
            setAmount(minAmountInput, maxAmountInput)

            if (minAmount!! >= maxAmount!!) {
                minAmountInput.error = "Please enter an amount lower than the maximum amount"
                minAmountInput.requestFocus()
                Toast.makeText(context, "min"+minAmount +"max"+maxAmount, Toast.LENGTH_SHORT).show()
            } else {
                flag = true
            }
        }
        //return flag
    }

    private fun setAmount(minAmountInput: EditText, maxAmountInput: EditText) {
        if (minAmountInput.text.isNotEmpty())
            minAmount = minAmountInput.text.toString().toFloat()
        else if (minAmountInput.text.isEmpty())
            minAmount = 0.00f

        if (maxAmountInput.text.isNotEmpty())
            maxAmount = maxAmountInput.text.toString().toFloat()
        else if (maxAmountInput.text.isEmpty())
            maxAmount = 0.00f
    }

    private fun setDialogSize() {
        val width = resources.getDimensionPixelSize(R.dimen.sort_popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.sort_popup_height)
        dialog!!.window!!.setLayout(width, height)
    }





}