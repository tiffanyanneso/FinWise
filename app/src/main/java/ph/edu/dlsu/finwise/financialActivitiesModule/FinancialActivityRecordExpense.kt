package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialRecordExpenseBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.ShoppingListItem
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FinancialActivityRecordExpense : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialRecordExpenseBinding
    private var firestore = Firebase.firestore

    private lateinit var savingActivityID:String
    private lateinit var budgetingActivityID:String
    private lateinit var spendingActivityID:String
    private lateinit var budgetItemID:String
    private var shoppingListItemID:String?=null
    private var remainingBudget = 0.00F

    private var expenseCategories = ArrayList<String>()

    private lateinit var bundle:Bundle

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private var cashBalance = 0.00F
    private var mayaBalance = 0.00f

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialRecordExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFields()

        if (bundle.containsKey("shoppingListItem")) {
            shoppingListItemID = bundle.getString("shoppingListItem")
            firestore.collection("ShoppingListItems").document(shoppingListItemID.toString()).get().addOnSuccessListener {
                binding.etExpenseName.setText(it.toObject<ShoppingListItem>()!!.itemName)
            }
        }
        //TODO: ADD HOW MUCH THEY HAVE LEFT IN THEIR SAVINGS
        binding.tvRemainingBudget.text = "You currently have ₱${DecimalFormat("#,##0.00").format(bundle.getFloat("remainingBudget"))} left in budget"


        binding.btnNext.setOnClickListener {
            var sendBundle = Bundle()
            sendBundle.putString("expenseName", binding.etExpenseName.text.toString())
            sendBundle.putFloat("amount", binding.etAmount.text.toString().toFloat())
            sendBundle.putSerializable("date", SimpleDateFormat("MM/dd/yyyy").parse(binding.etTransactionDate.text.toString()))
            sendBundle.putString("savingActivityID", savingActivityID)
            sendBundle.putString("budgetingActivityID", budgetingActivityID)
            sendBundle.putString("spendingActivityID", spendingActivityID)
            sendBundle.putString("budgetItemID", budgetItemID)
            sendBundle.putString("paymentType", binding.dropPaymentType.text.toString())
            sendBundle.putFloat("cashBalance", cashBalance)
            sendBundle.putFloat("mayaBalance", mayaBalance)
            if (bundle.containsKey("shoppingListItem"))
                sendBundle.putString("shoppingListItemID", shoppingListItemID)
            var confirmSpending = Intent(this, FinancialActivityConfirmExpense::class.java)
            confirmSpending.putExtras(sendBundle)
            this.startActivity(confirmSpending)
        }

        binding.btnCancel.setOnClickListener {
           //TODO: CANCEL BUTTON
        }
    }

    private fun changeDisplayedSavings() {
        var paymentType = binding.dropPaymentType.text.toString()
        if (paymentType == "Cash") {
            binding.tvCashBalance.visibility = View.VISIBLE
            binding.tvMayaBalance.visibility = View.GONE
        } else if (paymentType =="Maya") {
            binding.tvCashBalance.visibility = View.GONE
            binding.tvMayaBalance.visibility = View.VISIBLE
        }
    }

    private fun setFields() {
        bundle = intent.extras!!
        savingActivityID = bundle.getString("savingActivityID").toString()
        budgetingActivityID = bundle.getString("budgetingActivityID").toString()
        spendingActivityID = bundle.getString("spendingActivityID").toString()
        budgetItemID = bundle.getString("budgetItemID").toString()
        remainingBudget = bundle.getFloat("remainingBudget")
        binding.tvRemainingBudget.text = "You currently have ₱${DecimalFormat("#,##0.00").format(remainingBudget)} left in budget"
        binding.etTransactionDate.setText(SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate()))

        binding.dropPaymentType.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            changeDisplayedSavings()
            checkEnough()
        }

        // Hides actionbar, and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, ph.edu.dlsu.finwise.R.id.nav_goal)


        binding.etTransactionDate.setOnClickListener{
            showCalendar()
        }

        val paymentTypeDropdown = ArrayAdapter (this, R.layout.list_item, resources.getStringArray(R.array.payment_type))
        binding.dropPaymentType.setAdapter(paymentTypeDropdown)

        getBalances()

        binding.etAmount.doOnTextChanged { text, start, before, count ->
            //checks if the balance of their selected fund source is enough to pay for the amount they inputted
            checkEnough()
        }
    }

    private fun checkEnough() {
        var amount =0.00F
        if (!(binding.etAmount.text?.isEmpty())!!)
            amount = binding.etAmount.text.toString().toFloat()
        var paymentType = binding.dropPaymentType.text.toString()

        //TODO: CHANGE TEXT COLOR FOR WARNING
        if (paymentType == "Cash") {
            if (amount > cashBalance)
                binding.containerAmount.helperText = "You do not have enough Cash savings to pay for the item. The remaining amount will be deducted from your Maya savings"
            else
                binding.containerAmount.helperText = ""

        } else if (paymentType == "Maya") {
            if (amount > mayaBalance)
                binding.containerAmount.helperText = "You do not have enough Maya savings to pay for the item. The remaining amount will be deducted from your Cash savings"
            else
                binding.containerAmount.helperText = ""
        }
    }

    private fun getBalances() {
        binding.tvCashBalance.visibility = View.VISIBLE
        binding.tvMayaBalance.visibility = View.VISIBLE
        binding.dropPaymentType.isEnabled = true
        binding.dropPaymentType.isClickable = true
        binding.dropPaymentType.text.clear()

        firestore.collection("Transactions").whereEqualTo("financialActivityID", savingActivityID).get().addOnSuccessListener { transactions ->
            for (transaction in transactions) {
                var transactionObject = transaction.toObject<Transactions>()
                if (transactionObject.paymentType == "Cash") {
                    if (transactionObject?.transactionType == "Deposit")
                        cashBalance += transactionObject?.amount!!
                    else if (transactionObject.transactionType == "Withdrawal")
                        cashBalance -= transactionObject?.amount!!
                }

                else if (transactionObject.paymentType == "Maya") {
                    if (transactionObject?.transactionType == "Deposit")
                        mayaBalance += transactionObject?.amount!!
                    else if (transactionObject.transactionType == "Withdrawal")
                        mayaBalance -= transactionObject?.amount!!
                }
            }
            binding.tvCashBalance.text = "You currently have ₱${DecimalFormat("#,##0.00").format(cashBalance)} savings in cash"
            binding.tvMayaBalance.text = "You currently have ₱${DecimalFormat("#,##0.00").format(mayaBalance)} savings in maya"

            if (cashBalance == 0.00F) {
                binding.tvCashBalance.visibility = View.GONE
                binding.dropPaymentType.setText("Maya")
                //disable them to select other fund source
                binding.dropPaymentType.isEnabled = false
                binding.dropPaymentType.isClickable = false
            }
            else if (mayaBalance == 0.00F) {
                binding.tvMayaBalance.visibility = View.GONE
                binding.dropPaymentType.setText("Cash")
                //disable them to select other fund source
                binding.dropPaymentType.isEnabled = false
                binding.dropPaymentType.isClickable = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(ph.edu.dlsu.finwise.R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(ph.edu.dlsu.finwise.R.id.et_date)
        calendar.maxDate = System.currentTimeMillis()

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etTransactionDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
    }
}