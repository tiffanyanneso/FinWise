package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.TextInputEditText
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityTransactionSortBinding
import java.text.SimpleDateFormat
import java.util.*

class TransactionSortActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionSortBinding
    var bundle = Bundle()
    var minAmount: Float? = null
    var maxAmount: Float? = null
    var startDate: Date? = null
    var endDate: Date? = null
    var isSortable = false


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionSortBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeCancelButton()
        initializeDatePicker(binding.etStartDate, "startDate")
        initializeDatePicker(binding.etEndDate, "endDate")
        sortTransactions()
    }

    private fun initializeCancelButton() {
        binding.btnCancel.setOnClickListener {
            val goToTransactions = Intent(this, TransactionHistoryActivity::class.java)
            startActivity(goToTransactions)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sortTransactions() {
        binding.btnDone.setOnClickListener {

            val isAmountSorted: Boolean = setAmountSort()
            val isDateSorted: Boolean = setDateSort()
            val isCategorySorted: Boolean = setCategory()

            /*if (isAmountSorted || isDateSorted || isCategorySorted) {
                val goToSortedTransactions = Intent(this, TransactionHistoryActivity::class.java)
                goToSortedTransactions.putExtras(bundle)
                startActivity(goToSortedTransactions)
            } else {
                val goToTransactions = Intent(this, TransactionHistoryActivity::class.java)
                startActivity(goToTransactions)
            }*/

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

    private fun setCategory(): Boolean {
        val cbIncome = binding.cbIncome
        val cbExpense = binding.cbExpense
        var checkedBoxes = "none"
        var flag = true
        if (cbIncome.isChecked && cbExpense.isChecked) {
            checkedBoxes = "both"
        }
        else if (cbIncome.isChecked) {
            checkedBoxes = "income"
        } else if (cbExpense.isChecked) {
            checkedBoxes = "expense"
        }
        bundle.putString("category", checkedBoxes)

        if (checkedBoxes!= "none")
            flag = false

        return flag
    }

    private fun setDateSort(): Boolean {
        var flag = false
        val startDateInput = binding.etStartDate
        val endDateInput = binding.etEndDate

        if (startDateInput.text!!.isNotEmpty() || endDateInput.text!!.isNotEmpty()) {
            setDate(startDateInput, endDateInput)
            flag = validateDate(startDateInput, endDateInput)
            bundle.putSerializable("startDate", startDate)
            bundle.putSerializable("endDate", endDate)
        }
        return flag
    }

    private fun validateDate(startDateInput: TextInputEditText, endDateInput: TextInputEditText): Boolean {
        var flag = false
        if (startDate == null) {
            startDateInput.error = "Please enter a start date lower than the end date"
            startDateInput.requestFocus()
            Toast.makeText(this, "Please enter a start date lower than the end date",
                Toast.LENGTH_SHORT).show()
            endDateInput.error = null
        } else if (endDate == null) {
            endDateInput.error = "Please enter a end date higher than the start date"
            endDateInput.requestFocus()
            Toast.makeText(this, "Please enter a end date higher than the start date",
                Toast.LENGTH_SHORT).show()
            startDateInput.error = null
        } else if (startDate!! >= endDate) {
            startDateInput.error = "Please enter a start date lower than the end date"
            startDateInput.requestFocus()
            Toast.makeText(this, "Please enter a start date lower than the end date",
                Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "start"+ startDate+"end" +endDate, Toast.LENGTH_SHORT).show()
            flag = true
            startDateInput.error = null
        }
        return flag
    }

    private fun setDate(startDateInput: EditText, endDateInput: EditText) {

        if (startDateInput.text.isEmpty())
            startDate = null

        if (endDateInput.text.isEmpty())
            endDate = null
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeDatePicker(dateBinding: TextInputEditText, date: String) {
        dateBinding.setOnClickListener{
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()
            val dialog = Dialog(this)

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



    private fun setAmountSort(): Boolean {
        var flag = false
        val minAmountInput = binding.etMinAmount
        val maxAmountInput = binding.etMaxAmount

        if (minAmountInput.text.isNotEmpty() || maxAmountInput.text.isNotEmpty()) {
            setAmount(minAmountInput, maxAmountInput)
            flag = validateAmount(minAmountInput)

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
        }

        return flag
    }

    private fun setAmount(minAmountInput: EditText, maxAmountInput: EditText) {
        if (minAmountInput.text.isNotEmpty())
            minAmount = minAmountInput.text.toString().toFloat()
        else if (minAmountInput.text.isEmpty()) {
            minAmountInput.error = "Please enter an amount lower than the maximum amount"
            minAmountInput.requestFocus()
        }

        if (maxAmountInput.text.isNotEmpty())
            maxAmount = maxAmountInput.text.toString().toFloat()
        else if (maxAmountInput.text.isEmpty()) {
            maxAmountInput.error = "Please enter an amount lower than the maximum amount"
            maxAmountInput.requestFocus()
        }
    }

}