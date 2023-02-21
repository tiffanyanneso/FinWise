package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialGoalDepositBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FinancialActivityGoalDeposit : AppCompatActivity() {

    private lateinit var binding : ActivityFinancialGoalDepositBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context

    private lateinit var bundle:Bundle

    private lateinit var financialGoalID:String
    private lateinit var savingActivityID:String

    private var savedAmount = 0.00F
    private var walletBalance = 0.00F

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialGoalDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        setFields()

        binding.etDate.setOnClickListener {
            showCalendar()
        }


        binding.btnNext.setOnClickListener {
            if (filledUp() && validAmount()) {
                binding.containerAmount.helperText = ""
                var sendBundle = Bundle()
                sendBundle.putString("goalName", binding.tvGoalName.text.toString())
                sendBundle.putString("financialGoalID",financialGoalID)
                sendBundle.putFloat("savedAmount", savedAmount)
                sendBundle.putFloat("amount", binding.etAmount.text.toString().toFloat())
                //bundle.putString("source", "DirectGoalDeposit")
                sendBundle.putSerializable("date", SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString()))
                sendBundle.putString("savingActivityID", savingActivityID)

                var goToDepositConfirmation = Intent(context, FinancialActivityConfirmDeposit::class.java)
                goToDepositConfirmation.putExtras(sendBundle)
                goToDepositConfirmation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(goToDepositConfirmation)
            }
        }
    }

    private fun setFields() {
        bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()
        savedAmount = bundle.getFloat("savedAmount")
        binding.pbProgress.progress = bundle.getInt("progress")

        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var financialGoal = it.toObject<FinancialGoals>()
            binding.tvGoalName.text = financialGoal?.goalName
            binding.tvProgressAmount.text = "₱ " + DecimalFormat("###0.00").format(bundle.getFloat("savedAmount")) +
                    " / ₱ " + DecimalFormat("###0.00").format(financialGoal?.targetAmount)
        }

        firestore.collection("ChildWallet").whereEqualTo("childID", "eWZNOIb9qEf8kVNdvdRzKt4AYrA2").get().addOnSuccessListener {
            var wallet = it.documents[0].toObject<ChildWallet>()
            walletBalance = wallet?.currentBalance!!
            binding.tvBalance.text = "You currently have ₱${DecimalFormat("###0.00").format(walletBalance)} in your wallet"
        }
    }

    private fun validAmount():Boolean {
        //trying to deposit more than their current balance
        if (binding.etAmount.text.toString().toFloat() > walletBalance) {
            binding.containerAmount.helperText = "You cannot deposit more than your current balance"
            return false
        }
        else
            return true

    }

    private fun filledUp() : Boolean {
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


        if (binding.etDate.text.toString().trim().isEmpty()) {
            binding.dateContainer.helperText = "Select date of transaction."
            valid = false
        } else
            binding.dateContainer.helperText = ""

        return valid
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(R.id.et_date)

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
    }
}