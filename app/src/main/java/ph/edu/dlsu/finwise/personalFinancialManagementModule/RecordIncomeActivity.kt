package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Timestamp
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordIncomeBinding
import java.text.SimpleDateFormat
import java.util.*

class RecordIncomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmrecordIncomeBinding
    lateinit var bundle: Bundle
    lateinit var name: String
    lateinit var amount: String
    var balance = 0.00f
    lateinit var category: String
    lateinit var paymentType: String
    lateinit var date: Date

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmrecordIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(ph.edu.dlsu.finwise.R.id.bottom_nav), this, ph.edu.dlsu.finwise.R.id.nav_finance)

        initializeDropdown()
        initializeDatePicker()
        //getGoals()
        loadBackButton()
        goToConfirmation()
        cancel()
    }

    private fun initializeDropdown() {
        // for the dropdown
        val categoryItems = resources.getStringArray(ph.edu.dlsu.finwise.R.array.pfm_income_category)
        val adapterCategoryItems = ArrayAdapter (this, ph.edu.dlsu.finwise.R.layout.list_item, categoryItems)
        binding.dropdownCategory.setAdapter(adapterCategoryItems)

        val paymentTypeItems = resources.getStringArray(ph.edu.dlsu.finwise.R.array.payment_type)
        val adapterPaymentTypeItems = ArrayAdapter (this, ph.edu.dlsu.finwise.R.layout.list_item, paymentTypeItems)
        binding.dropdownTypeOfPayment.setAdapter(adapterPaymentTypeItems)


    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeDatePicker() {
        binding.etDate.setText(SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate()))
        date = SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString())

        binding.etDate.setOnClickListener{
            showCalendar()
        }
    }

    private fun validateAndSetUserInput(): Boolean {
        var valid = true
        // Check if edit text is empty and valid
        if (binding.etName.text.toString().trim().isEmpty()) {
            binding.containerIncome.helperText = "Please enter the name of the transaction."
            valid = false
        } else {
            binding.containerIncome.helperText = ""
            name = binding.etName.text.toString().trim()
        }

        if (binding.dropdownCategory.text.toString() == "") {
            binding.containerCategory.helperText = "Please select a category of the transaction."
            valid = false
        } else {
            binding.containerCategory.helperText = ""
            category = binding.dropdownCategory.text.toString()
        }

        if (binding.dropdownTypeOfPayment.text.toString() == "") {
            binding.containerTypeOfPayment.helperText = "Please select if you used cash or Maya"
            valid = false
        } else {
            binding.containerTypeOfPayment.helperText = ""
            paymentType = binding.dropdownTypeOfPayment.text.toString()
        }


        if (binding.etAmount.text.toString().trim().isEmpty()) {
            binding.amountContainer.helperText = "Please enter the amount."
            valid = false
        } else {
            binding.amountContainer.helperText = ""
            amount = binding.etAmount.text.toString().trim()
        }

//        date = SimpleDateFormat("MM-dd-yyyy").parse((binding.etDate.month+1).toString() + "-" +
//                binding.etDate.dayOfMonth.toString() + "-" + binding.etDate.year)

        if (binding.etDate.text.toString().trim().isEmpty()) {
            binding.dateContainer.helperText = "Please enter the name of the transaction."
            valid = false
        } else {
            binding.dateContainer.helperText = ""
            date = SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString())
        }

        return valid
    }

    private fun cancel() {
        binding.btnCancel.setOnClickListener {
            val goBack = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goBack)
        }
    }

    private fun goToConfirmation() {
        binding.btnConfirm.setOnClickListener {
            if (validateAndSetUserInput() ) {
                setBundle()
                val goToConfirmTransaction = Intent(this, ConfirmTransactionActivity::class.java)
                goToConfirmTransaction.putExtras(bundle)
                goToConfirmTransaction.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(goToConfirmTransaction)
            } else {
                Toast.makeText(
                    baseContext, "Please fill up the form correctly.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setBundle() {
        //getCurrentTime()
//        val goal = binding.spinnerGoal.selectedItem.toString()
        getBalance()
        bundle = Bundle()
        bundle.putString("transactionType", "Income")
        bundle.putString("transactionName", name)
        bundle.putString("category", category)
        bundle.putString("paymentType", paymentType)
        bundle.putFloat("balance", balance)
        bundle.putFloat("amount", amount.toFloat())
//        bundle.putString("goal", goal)
        bundle.putSerializable("date", date)


        //TODO: reset spinner and date to default value
        /* binding.etName.text.clear()
         binding.etAmount.text.clear()
         binding.spinnerCategory.clear()
         binding.spinnerGoal.adapter(null)*/
    }

    private fun getBalance() {
        val bundle2 = intent.extras!!
        balance = bundle2.getFloat("balance")
        //Toast.makeText(this, ""+balance, Toast.LENGTH_SHORT).show()
    }

    /*private fun getCurrentTime() {
        //Time
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val time = Calendar.getInstance().time
        date = formatter.format(time)
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(ph.edu.dlsu.finwise.R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        val calendar = dialog.findViewById<DatePicker>(ph.edu.dlsu.finwise.R.id.et_date)
        calendar.maxDate = System.currentTimeMillis()


        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            date = SimpleDateFormat("MM/dd/yyyy").parse((mMonth + 1).toString() + "/" +
                    mDay.toString() + "/" + mYear.toString()) as Date
            dialog.dismiss()
        }
        dialog.show()
    }

    /*private fun getGoals() {
    //TODO: UPDATE LATER WITH CHILD ID
    goals.add("None")
        firestore.collection("FinancialGoals").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            for (goal in results) {
                val goalObject = goal.toObject<FinancialGoals>()
                goals.add(goalObject.goalName.toString())
            }
//                val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, goals)
//                binding.spinnerGoal.adapter = adapter
        }

}*/


}