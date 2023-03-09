package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
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

    private lateinit var bundle:Bundle

    private var savedAmount = 0.00F


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavingsWithdrawBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                //bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
                sendBundle.putFloat("amount", binding.etAmount.text.toString().toFloat())
                sendBundle.putSerializable("date", SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString()))
                sendBundle.putString("savingActivityID", savingActivityID)
                sendBundle.putFloat("savedAmount", savedAmount)


                var confirmWithdraw = Intent (this, FinancialActivityConfirmWithdraw::class.java)
                confirmWithdraw.putExtras(sendBundle)
                confirmWithdraw.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(confirmWithdraw)
            }
        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            var bundle = Bundle()
            bundle.putString("financialGoalID",financialGoalID)

            val goToGoal = Intent(applicationContext, ViewGoalActivity::class.java)
            goToGoal.putExtras(bundle)
            goToGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToGoal)
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

        if (binding.etDate.text.toString().trim().isEmpty()) {
            binding.dateContainer.helperText = "Select date of transaction."
            valid = false
        } else
            binding.dateContainer.helperText = ""

        return valid
    }

    private fun validAmount():Boolean {
        //trying to deposit more than their current balance
        println("print " + savedAmount)
        if (binding.etAmount.text.toString().toFloat() > savedAmount) {
            binding.containerAmount.helperText = "You cannot withdraw more than your savings."
            return false
        }
        else
            return true

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

    private fun setFields() {
        bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()
        //savedAmount = bundle.getFloat("savedAmount")
        //binding.pbProgress.progress = bundle.getInt("progress")
        savedAmount = 0.00F

        firestore.collection("FinancialActivities").document(savingActivityID).get().addOnSuccessListener {
            var saving = it.toObject<FinancialActivities>()
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

        firestore.collection("Transactions").whereEqualTo("financialActivityID", savingActivityID).whereIn("transactionType", Arrays.asList("Deposit", "Withdrawal")).get().addOnSuccessListener { results ->
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()

                if (transactionObject.transactionType == "Deposit")
                    savedAmount += transactionObject.amount!!

                else if (transactionObject.transactionType == "Withdrawal")
                    savedAmount -= transactionObject.amount!!
            }
        }.continueWith {
            firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
                var financialGoal = it.toObject<FinancialGoals>()
                binding.tvGoalName.text = financialGoal?.goalName
                binding.pbProgress.progress = (savedAmount / financialGoal?.targetAmount!! * 100).toInt()
                binding.tvProgressAmount.text = "₱ " + DecimalFormat("#,##0.00").format(savedAmount) +
                        " / ₱ " + DecimalFormat("#,##0.00").format(financialGoal?.targetAmount)
                binding.tvSavings.text = "You currently have ₱ ${DecimalFormat("#,##0.00").format(savedAmount)} in your savings"
            }
        }


    }
}