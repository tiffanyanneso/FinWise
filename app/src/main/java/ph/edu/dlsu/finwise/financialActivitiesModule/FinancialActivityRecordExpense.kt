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

    private lateinit var context:Context

    private lateinit var budgetActivityID:String
    private lateinit var budgetItemID:String

    private var expenseCategories = ArrayList<String>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialRecordExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=  this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(ph.edu.dlsu.finwise.R.id.bottom_nav), this, ph.edu.dlsu.finwise.R.id.nav_goal)


        var bundle: Bundle = intent.extras!!
        budgetActivityID = bundle.getString("budgetActivityID").toString()
        budgetItemID = bundle.getString("budgetItemID").toString()
        binding.tvRemainingBudget.text = "You currently have ₱${DecimalFormat("###0.00").format(bundle.getFloat("remainingBudget"))} left in budget"


        binding.etTransactionDate.setOnClickListener{
            showCalendar()
        }

        binding.btnNext.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("expenseName", binding.etExpenseName.text.toString())
            bundle.putFloat("amount", binding.etAmount.text.toString().toFloat())
            bundle.putSerializable("date", SimpleDateFormat("MM/dd/yyyy").parse(binding.etTransactionDate.text.toString()))
            bundle.putString("budgetActivityID", budgetActivityID)
            bundle.putString("budgetItemID", budgetItemID)
            var confirmSpending = Intent(this, FinancialActivityConfirmExpense::class.java)
            confirmSpending.putExtras(bundle)
            context.startActivity(confirmSpending)
        }

        binding.btnCancel.setOnClickListener {
            var budgetExpense = Intent(this, BudgetExpenseActivity::class.java)
            var bundle = Bundle()
            bundle.putString("budgetActivityID", budgetActivityID)
            bundle.putString("budgetItemID", budgetItemID)
            context.startActivity(budgetExpense)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(ph.edu.dlsu.finwise.R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(ph.edu.dlsu.finwise.R.id.et_date)

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etTransactionDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
    }
}