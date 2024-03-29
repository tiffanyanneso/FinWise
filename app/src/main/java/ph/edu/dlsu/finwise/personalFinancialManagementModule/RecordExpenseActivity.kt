package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordExpenseBinding
import ph.edu.dlsu.finwise.model.SettingsModel
import java.text.SimpleDateFormat
import java.util.*

class RecordExpenseActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmrecordExpenseBinding
    lateinit var bundle: Bundle
    private var firestore = Firebase.firestore

    private var goals = ArrayList<String>()
    private lateinit var adapterPaymentTypeItems: ArrayAdapter<String>
    private var selectedPaymentType = "Cash"
    lateinit var name: String
    lateinit var amount: String
    var balance = 0.00f
    lateinit var category: String
    private var phone = ""
    private var merchant = ""
    lateinit var paymentType: String
    lateinit var goal: String
    lateinit var date: Date

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmrecordExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(ph.edu.dlsu.finwise.R.id.bottom_nav), this, ph.edu.dlsu.finwise.R.id.nav_finance)

        initializeDropdown()
        initizlizeDatePicker()
       // getGoals()
        loadBackButton()
        goToConfirmation()
        cancel()
    }

    private fun isMayaPayment() {
        binding.dropdownTypeOfPayment.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            selectedPaymentType = adapterPaymentTypeItems.getItem(position).toString()
            if (selectedPaymentType == "Maya") {
                binding.phoneContainer.visibility = View.VISIBLE
                binding.merchantContainer.visibility = View.VISIBLE
            }
            else if (selectedPaymentType == "Cash") {
                binding.phoneContainer.visibility = View.GONE
                binding.merchantContainer.visibility = View.GONE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initizlizeDatePicker() {
//TODO: update date to set date now
        binding.etDate.setText(SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate()))
        date = SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString())

        binding.etDate.setOnClickListener{
            showCalendar()
        }
    }

    private fun initializeDropdown() {
        // for the dropdown
        val items = resources.getStringArray(ph.edu.dlsu.finwise.R.array.pfm_expense_category)
        val adapter = ArrayAdapter (this, ph.edu.dlsu.finwise.R.layout.list_item, items)
        binding.dropdownCategory.setAdapter(adapter)

        val paymentTypeItems = resources.getStringArray(ph.edu.dlsu.finwise.R.array.payment_type)
        adapterPaymentTypeItems = ArrayAdapter (this, ph.edu.dlsu.finwise.R.layout.list_item, paymentTypeItems)
        binding.dropdownTypeOfPayment.setAdapter(adapterPaymentTypeItems)
        isMayaPayment()
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    /* private fun getGoals() {
         //TODO: UPDATE LATER WITH CHILD ID
         goals.add("None")
         firestore.collection("FinancialGoals").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
             for (goal in results) {
                 var goalObject = goal.toObject<FinancialGoals>()
                 goals.add(goalObject.goalName.toString())
             }
 //            val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, goals)
 //            binding.spinnerGoal.adapter = adapter
         }
     }*/

    private fun validateAndSetUserInput(): Boolean {
        var valid = true

        if (selectedPaymentType == "Maya") {
            if (binding.etMerchant.text.toString().trim().isEmpty()) {
                binding.merchantContainer.helperText = "Please enter the name of the Merchant/Seller."
                valid = false
            } else {
                binding.merchantContainer.helperText = ""
                merchant = binding.etMerchant.text.toString().trim()
            }

            if (binding.etPhone.text.toString().trim().isEmpty() || binding.etPhone.text?.length!! < 11) {
                binding.phoneContainer.helperText = "Please enter the right 11 digit Phone Number."
                valid = false
            } else {
                binding.phoneContainer.helperText = ""
                phone = binding.etPhone.text.toString().trim()
            }
        }

        // Check if edit text is empty and valid
        if (binding.etName.text.toString().trim().isEmpty()) {
            binding.containerExpense.helperText = "Please enter the name of the transaction."
            valid = false
        } else {
            binding.containerExpense.helperText = ""
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


    private fun validAmount(): Boolean {
        val bundle2 = intent.extras!!
        balance = bundle2.getFloat("balance")
        Log.d("balance", "validAmount: "+balance)
        //trying to deposit more than their current balance
        if (binding.etAmount.text.toString().toFloat() > balance) {
            binding.etAmount.error =
                "You cannot deposit more than your current balance of ₱$balance"
            binding.etAmount.requestFocus()
            return false
        }
        else
            return true
    }


    private fun cancel() {
        binding.btnCancel.setOnClickListener {
            val goBack = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goBack)
        }
    }

    private fun goToConfirmation() {

        binding.btnConfirm.setOnClickListener {
            if (validateAndSetUserInput() && validAmount()) {
                firestore.collection("Settings").whereEqualTo("childID", currentUser).get().addOnSuccessListener {
                    var settings = it.documents[0].toObject<SettingsModel>()
                    if (amount.toFloat() >= settings?.alertAmount!!)
                        binding.amountContainer.helperText = "This amount is over the limit set by your parents. Discuss this expense with them first."
                    else {
                        binding.amountContainer.helperText = ""
                        setBundle()
                        val goToConfirmTransaction = Intent(this, ConfirmTransactionActivity::class.java)
                        goToConfirmTransaction.putExtras(bundle)
                        goToConfirmTransaction.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(goToConfirmTransaction)
                    }
                }

            } else {
                Toast.makeText(
                    baseContext, "Please fill up correctly the form.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setBundle() {
        //val goal = binding.spinnerGoal.selectedItem.toString()
        bundle = Bundle()
        bundle.putString("transactionName", name)
        bundle.putString("transactionType", "Expense")
        bundle.putString("category", category)
        bundle.putString("paymentType", paymentType)
        bundle.putString("merchant", merchant)
        bundle.putString("phone", phone)
        bundle.putFloat("amount", amount.toFloat())
        bundle.putFloat("balance", balance)
       // bundle.putString("goal", goal)
        bundle.putSerializable("date", date)

        //TODO: reset spinner and date to default value
        /* binding.etName.text.clear()
         binding.etAmount.text.clear()
         binding.spinnerCategory.clear()
         binding.spinnerGoal.adapter(null)*/
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
}