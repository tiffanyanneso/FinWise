package ph.edu.dlsu.finwise.personalFinancialManagementModule.mayaAPI

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import java.text.SimpleDateFormat
import ph.edu.dlsu.finwise.databinding.ActivityMayaPaymentBinding
import java.util.*


class MayaPayment : AppCompatActivity() {

    private lateinit var binding: ActivityMayaPaymentBinding
    var bundle = Bundle()
    lateinit var name: String
    lateinit var amount: String
    lateinit var category: String
    lateinit var merchant: String
    lateinit var phone: String
    lateinit var date: Date
    var balance = 0.00f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMayaPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializes the navbar
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)
        initializeDropdown()
        loadBackButton()
        goToMayaQRConfirmPayment()
        goToPersonalFinancialManagement()
    }
    private fun initializeDropdown() {
        // for the dropdown
        val items = resources.getStringArray(R.array.pfm_expense_category)
        val adapter = ArrayAdapter (this, R.layout.list_item, items)
        binding.dropdownCategory.setAdapter(adapter)
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToPFM = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goToPFM)
        }
    }

    private fun goToMayaQRConfirmPayment(){
        binding.btnConfirm.setOnClickListener {
            Toast.makeText(this, "confirm", Toast.LENGTH_SHORT).show()
            /*if (validateAndSetUserInput() && validAmount()) {
                setBundle()
                val goToMayaQRConfirmPayment = Intent(applicationContext, MayaConfirmPayment::class.java)
                goToMayaQRConfirmPayment.putExtras(bundle)
                goToMayaQRConfirmPayment.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(goToMayaQRConfirmPayment)
            } else {
                Toast.makeText(
                    baseContext, "Please fill up the form correctly.",
                    Toast.LENGTH_SHORT
                ).show()
            }*/
        }
    }

    private fun validAmount(): Boolean {
        val bundle2 = intent.extras!!
        balance = bundle2.getFloat("balance")
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

    private fun goToPersonalFinancialManagement(){
        binding.btnCancel.setOnClickListener(){
            val goToPersonalFinancialManagementActivity = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goToPersonalFinancialManagementActivity)
        }
    }

    private fun setBundle() {
        //getCurrentTime()
//        val goal = binding.spinnerGoal.selectedItem.toString()
        bundle.putString("transactionName", name)
        bundle.putString("category", category)
        bundle.putFloat("amount", amount.toFloat())
        bundle.putString("phone", phone)
        bundle.putString("merchant", merchant)
        bundle.putFloat("balance", balance)
//        bundle.putString("goal", goal)
        bundle.putSerializable("date", date)

        //TODO: reset spinner and date to default value
        /* binding.etName.text.clear()
         binding.etAmount.text.clear()
         binding.spinnerCategory.clear()
         binding.spinnerGoal.adapter(null)*/
    }

    private fun validateAndSetUserInput(): Boolean {
        var valid = true
        // Check if edit text is empty and valid
        if (binding.etName.text.toString().trim().isEmpty()) {
            binding.etName.error = "Please enter the name of the transaction."
            binding.etName.requestFocus()
            valid = false
        } else name = binding.etName.text.toString().trim()

        if (binding.etMerchant.text.toString().trim().isEmpty()) {
            binding.etMerchant.error = "Please enter the name of the Merchant/Seller."
            binding.etMerchant.requestFocus()
            valid = false
        } else merchant = binding.etMerchant.text.toString().trim()

        if (binding.dropdownCategory.text.toString() == "") {
            binding.dropdownCategory.error = "Please select a category of the transaction."
            valid = false
        } else {
            binding.dropdownCategory.error = null
            category = binding.dropdownCategory.text.toString()
        }

        if (binding.etAmount.text.toString().trim().isEmpty()) {
            binding.etAmount.error = "Please enter the amount."
            binding.etAmount.requestFocus()
            valid = false
        } else amount = binding.etAmount.text.toString().trim()


        if (binding.etPhone.text.toString().trim().isEmpty() || binding.etPhone.text?.length!! < 11) {
            binding.etPhone.error = "Please enter the right 11 digit Phone Number."
            binding.etPhone.requestFocus()
            valid = false
        } else phone = binding.etPhone.text.toString().trim()


        val time = Calendar.getInstance()
        date = SimpleDateFormat("MM/dd/yyyy").parse((time.get(Calendar.MONTH) + 1).toString() + "/" +
                time.get(Calendar.DAY_OF_MONTH).toString() + "/" + time.get(Calendar.YEAR).toString()) as Date

        return valid
    }
}