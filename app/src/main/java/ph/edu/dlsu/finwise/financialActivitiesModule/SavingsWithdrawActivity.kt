package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivitySavingsWithdrawBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class SavingsWithdrawActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySavingsWithdrawBinding
    private var firestore = Firebase.firestore

    private lateinit var financialGoalID:String
    private lateinit var savingActivityID:String
    private lateinit var budgetingActivityID:String
    private lateinit var spendingActivityID:String

    private lateinit var bundle:Bundle

    private var savedAmount = 0.00F
    private var cashBalance = 0.00F
    private var mayaBalance = 0.00F
    private var selectedWalletBalance = 0.00F


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavingsWithdrawBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFields()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        getBalances()

        binding.dropPaymentType.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            changeDisplayedBalance()

        }

        binding.btnNext.setOnClickListener {
            if (filledUp() && validAmount()) {
                binding.containerAmount.helperText = ""
                var sendBundle = Bundle()
                sendBundle.putString("goalName", binding.tvGoalName.text.toString())
                sendBundle.putString("financialGoalID",financialGoalID)
                //bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
                sendBundle.putFloat("amount", binding.etAmount.text.toString().toFloat())
                sendBundle.putSerializable("date", SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString()))
                sendBundle.putString("savingActivityID", savingActivityID)
                sendBundle.putString("budgetingActivityID", budgetingActivityID)
                sendBundle.putString("spendingActivityID", spendingActivityID)
                sendBundle.putFloat("savedAmount", savedAmount)
                sendBundle.putString("paymentType", binding.dropPaymentType.text.toString())

                var confirmWithdraw = Intent (this, FinancialActivityConfirmWithdraw::class.java)
                confirmWithdraw.putExtras(sendBundle)
                confirmWithdraw.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(confirmWithdraw)
            }
        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            var bundle = Bundle()
            bundle.putString("financialGoalID",financialGoalID)

            val goToGoal = Intent(applicationContext, ViewGoalActivity::class.java)
            goToGoal.putExtras(bundle)
            goToGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToGoal)
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

    private fun changeDisplayedBalance() {
        if (binding.dropPaymentType.text.toString() == "Cash") {
            selectedWalletBalance = cashBalance
            binding.tvCashBalance.visibility = View.VISIBLE
            binding.tvMayaBalance.visibility = View.GONE
        }
        else if (binding.dropPaymentType.text.toString() == "Maya") {
            selectedWalletBalance = mayaBalance
            binding.tvMayaBalance.visibility = View.VISIBLE
            binding.tvCashBalance.visibility = View.GONE}
    }

    private fun getGoalInfo() {
        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var financialGoal = it.toObject<FinancialGoals>()!!
            binding.tvGoalName.text = financialGoal.goalName
            binding.pbProgress.progress = (financialGoal.currentSavings!! / financialGoal.targetAmount!! * 100).toInt()
            binding.tvProgressAmount.text = "₱ " + DecimalFormat("#,##0.00").format(financialGoal.currentSavings!!) +
                    " / ₱ " + DecimalFormat("#,##0.00").format(financialGoal?.targetAmount)
            savedAmount = financialGoal.currentSavings!!
        }
    }


    private fun filledUp(): Boolean{
        var valid  = true
        if (binding.etAmount.text.toString().trim().isEmpty()) {
            binding.containerAmount.helperText = "Please input an amount."
            valid = false
        } else {
            //amount field is not empty
            binding.containerAmount.helperText = ""
            //check if amount is greater than 0
            if (binding.etAmount.text.toString().toFloat() <= 0) {
                binding.containerAmount.helperText = "Input a valid amount."
                valid = false
            } else
                binding.containerAmount.helperText = ""
        }

        if (binding.dropPaymentType.text.toString().isEmpty()) {
            binding.containerPaymentType.helperText = "Please select fund source."
            valid = false
        } else
            binding.containerPaymentType.helperText = ""

        if (binding.etDate.text.toString().trim().isEmpty()) {
            binding.dateContainer.helperText = "Select date of transaction."
            valid = false
        } else
            binding.dateContainer.helperText = ""

        return valid
    }

    private fun validAmount():Boolean {
        //trying to deposit more than their current balance
        if (binding.etAmount.text.toString().toFloat() > selectedWalletBalance) {
            binding.containerAmount.helperText = "You cannot deposit more than your current balance"
            return false
        }
        else
            return true
    }

    private fun setFields() {
        bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()
        budgetingActivityID = bundle.getString("budgetingActivityID").toString()
        spendingActivityID = bundle.getString("spendingActivityID").toString()
        binding.etDate.setText(SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate()))

        //savedAmount = bundle.getFloat("savedAmount")
        //binding.pbProgress.progress = bundle.getInt("progress")
        savedAmount = 0.00F

        binding.etDate.setOnClickListener {
            showCalendar()
        }

        getGoalInfo()
        firestore.collection("FinancialActivities").document(savingActivityID).get().addOnSuccessListener {
            var saving = it.toObject<FinancialActivities>()
            //saving is completed, hide progress bar, and compute for current balance as they might have already spent their savings
            if (saving?.status == "Completed") {
                binding.tvProgress.visibility = View.GONE
                binding.tvProgressAmount.visibility = View.GONE
                binding.pbProgress.visibility = View.GONE
            } else {
                binding.tvProgress.visibility = View.VISIBLE
                binding.tvProgressAmount.visibility = View.VISIBLE
                binding.pbProgress.visibility = View.VISIBLE
            }
        }

        val paymentTypeDropdown = ArrayAdapter (this, R.layout.list_item, resources.getStringArray(R.array.payment_type))
        binding.dropPaymentType.setAdapter(paymentTypeDropdown)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(R.id.et_date)
        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var goal = it.toObject<FinancialGoals>()
            calendar.minDate = goal?.dateCreated!!.toDate().time
        }
        calendar.maxDate = System.currentTimeMillis()

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
    }
}