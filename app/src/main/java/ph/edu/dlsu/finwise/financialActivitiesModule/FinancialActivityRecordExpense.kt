package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.databinding.ActivityFinancialRecordExpenseBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FinancialActivityRecordExpense : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialRecordExpenseBinding
    private var firestore = Firebase.firestore

    private lateinit var savingActivityID:String
    private lateinit var spendingActivityID:String
    private lateinit var budgetItemID:String
    private var shoppingListItemID:String?=null

    private var expenseCategories = ArrayList<String>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialRecordExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(ph.edu.dlsu.finwise.R.id.bottom_nav), this, ph.edu.dlsu.finwise.R.id.nav_goal)


        var bundle: Bundle = intent.extras!!
        savingActivityID = bundle.getString("savingActivityID").toString()
        spendingActivityID = bundle.getString("spendingActivityID").toString()
        budgetItemID = bundle.getString("budgetItemID").toString()
        if (bundle.containsKey("shoppingListItem"))
            shoppingListItemID = bundle.getString("shoppingListItem")
        //TODO: ADD HOW MUCH THEY HAVE LEFT IN THEIR SAVINGS
        binding.tvRemainingBudget.text = "You currently have ₱${DecimalFormat("#,##0.00").format(bundle.getFloat("remainingBudget"))} left in budget AND___ LEFT IN SAVINGS"


        binding.etTransactionDate.setOnClickListener{
            showCalendar()
        }

        binding.btnNext.setOnClickListener {
            var sendBundle = Bundle()
            sendBundle.putString("expenseName", binding.etExpenseName.text.toString())
            sendBundle.putFloat("amount", binding.etAmount.text.toString().toFloat())
            sendBundle.putSerializable("date", SimpleDateFormat("MM/dd/yyyy").parse(binding.etTransactionDate.text.toString()))
            sendBundle.putString("savingActivityID", savingActivityID)
            sendBundle.putString("spendingActivityID", spendingActivityID)
            println("print budgetItemID " + budgetItemID)
            sendBundle.putString("budgetItemID", budgetItemID)
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