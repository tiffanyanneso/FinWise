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
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivitySavingsDepositBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class SavingsDepositActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySavingsDepositBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context

    private lateinit var bundle:Bundle

    private lateinit var childID:String
    private lateinit var financialGoalID:String
    private lateinit var savingActivityID:String
    private lateinit var budgetingActivityID:String
    private lateinit var spendingActivityID:String

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private var savedAmount = 0.00F
    private var selectedWalletBalance = 0.00F
    private var cashBalance = 0.00F
    private var mayaBalance = 0.00F

    private lateinit var source:String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavingsDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        setFields()
        loadBackButton()


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
                if (::budgetingActivityID.isInitialized)
                    sendBundle.putString("budgetingActivityID", budgetingActivityID)
                if (::spendingActivityID.isInitialized)
                    sendBundle.putString("spendingActivityID", spendingActivityID)
                sendBundle.putString("paymentType", binding.dropPaymentType.text.toString())
                sendBundle.putString("source", source)

                var goToDepositConfirmation = Intent(context, FinancialActivityConfirmDeposit::class.java)
                goToDepositConfirmation.putExtras(sendBundle)
                goToDepositConfirmation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(goToDepositConfirmation)
            }
        }

        binding.btnCancel.setOnClickListener {
            var viewGoal = Intent(this, ViewGoalActivity::class.java)
            var bundle = Bundle()
            bundle.putString("financialGoalID", financialGoalID)
            bundle.putString("childID", currentUser)
            viewGoal.putExtras(bundle)
            startActivity(viewGoal)
        }
    }

    private fun setFields() {
        binding.dropPaymentType.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            changeDisplayedBalance()
        }

        binding.etDate.setOnClickListener {
            showCalendar()
        }

        bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()
        if (bundle.containsKey("budgetingActivityID"))
            budgetingActivityID = bundle.getString("budgetingActivityID").toString()
        if (bundle.containsKey("spendingActivityID"))
            spendingActivityID = bundle.getString("spendingActivityID").toString()
        source = bundle.getString("source").toString()
        if(source == "budgeting")
            binding.layoutProgress.visibility = View.GONE

        binding.etDate.setText(SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate()))


        val paymentTypeDropdown = ArrayAdapter (this, R.layout.list_item, resources.getStringArray(R.array.payment_type))
        binding.dropPaymentType.setAdapter(paymentTypeDropdown)

        firestore.collection("ChildWallet").whereEqualTo("childID", currentUser).get().addOnSuccessListener { results ->
            var totalBalance =  0.00F
            for (wallet in results) {
                var walletObject = wallet.toObject<ChildWallet>()
                totalBalance += walletObject.currentBalance!!
                if (walletObject.type == "Maya")
                    mayaBalance = walletObject.currentBalance!!
                else if (walletObject.type == "Cash")
                    cashBalance = walletObject.currentBalance!!
            }

            binding.tvBalance.text = "You currently have ₱${DecimalFormat("#,##0.00").format(totalBalance)}"
        }

        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var financialGoal = it.toObject<FinancialGoals>()
            binding.tvGoalName.text = financialGoal?.goalName
            binding.tvProgressAmount.text = "₱ " + DecimalFormat("#,##0.00").format(financialGoal?.currentSavings) +
                    " / ₱ " + DecimalFormat("#,##0.00").format(financialGoal?.targetAmount)
            binding.pbProgress.progress = ((financialGoal?.currentSavings!!/financialGoal?.targetAmount!!)*100).toInt()

        }
    }

    private fun changeDisplayedBalance() {
        if (binding.dropPaymentType.text.toString() == "Cash")
            selectedWalletBalance = cashBalance
        else if (binding.dropPaymentType.text.toString() == "Maya")
            selectedWalletBalance = mayaBalance
        binding.tvBalance.text = "You currently have ₱${DecimalFormat("#,##0.00").format(selectedWalletBalance)} in this wallet"
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

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}