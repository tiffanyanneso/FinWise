package ph.edu.dlsu.finwise.personalFinancialManagementModule


import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordDepositBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecordDepositActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmrecordDepositBinding
    var bundle = Bundle()
    private var firestore = Firebase.firestore

    private lateinit var adapterPaymentTypeItems: ArrayAdapter<String>
    private var selectedPaymentType = "Cash"
    lateinit var goal : String
    lateinit var amount: String
    lateinit var date: Date
    lateinit var paymentType: String
    var walletBalance = 0.00f

    private var childID = FirebaseAuth.getInstance().currentUser!!.uid

    private var savingActivityID:String?=null

    private var goalDropDownArrayList = ArrayList<GoalDropDown>()
    private lateinit var goalAdapter: ArrayAdapter<String>


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmrecordDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(ph.edu.dlsu.finwise.R.id.bottom_nav), this, ph.edu.dlsu.finwise.R.id.nav_finance)



        firestore.collection("ChildWallet").whereEqualTo("childID", childID).get().addOnSuccessListener {
            walletBalance = it.documents[0].toObject<ChildWallet>()!!.currentBalance!!

            binding.tvBalance.text = "You currently have ₱${DecimalFormat("#,##0.00").format(walletBalance)} in your wallet"
        }.continueWith {
            initializeDatePicker()
            getGoals()
            binding.dropdownGoal.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                savingActivityID = goalDropDownArrayList[position].savingActivityID
                binding.tvProgressAmount.text = "₱ " + DecimalFormat("#,##0.00").format(goalDropDownArrayList[position].currentSavings) +
                        " / ₱ " + DecimalFormat("#,##0.00").format(goalDropDownArrayList[position].targetAmount)
                binding.pbProgress.progress = ((goalDropDownArrayList[position].currentSavings/goalDropDownArrayList[position].targetAmount) *100).toInt()
            }

            initializePaymentTypeDropdown()

            loadBackButton()
            goToConfirmation()
            cancel()
        }
    }

    private fun initializePaymentTypeDropdown() {
        val paymentTypeItems = resources.getStringArray(R.array.payment_type)
        adapterPaymentTypeItems = ArrayAdapter (this, R.layout.list_item, paymentTypeItems)
        binding.dropdownTypeOfPayment.setAdapter(adapterPaymentTypeItems)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeDatePicker() {
//TODO: update date to set date now
        binding.etDate.setText(SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate()))
        date = SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString())

        binding.etDate.setOnClickListener{
            showCalendar()
        }
    }

    private fun getGoals() {
        firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Saving").whereEqualTo("status", "In Progress").get().addOnSuccessListener { activityResults ->
            for (saving in activityResults) {
                firestore.collection("FinancialGoals").document(saving.toObject<FinancialActivities>().financialGoalID!!).get().addOnSuccessListener { goal ->
                    val goalObject = goal.toObject<FinancialGoals>()
                    goalDropDownArrayList.add(GoalDropDown(saving.id, goalObject?.goalName!!, goalObject.currentSavings!!, goalObject.targetAmount!!))
                }.continueWith {
                    goalAdapter = ArrayAdapter(this, R.layout.list_item, goalDropDownArrayList.map { it.goalName })
                    binding.dropdownGoal.setAdapter(goalAdapter)
                }
            }
        }
    }

    class GoalDropDown(var savingActivityID:String, var goalName:String, var currentSavings:Float, var targetAmount:Float)

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun cancel() {
        binding.btnCancel.setOnClickListener {
            val goBack = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goBack)
        }
    }

    private fun validateAndSetUserInput(): Boolean {
        var valid = true
        // Check if edit text is empty and valid
        if (binding.etAmount.text.toString().trim().isEmpty()) {
            binding.etAmount.error = "Please enter the amount."
            binding.etAmount.requestFocus()
            valid = false
        } else amount = binding.etAmount.text.toString().trim()

        if (binding.dropdownGoal.text.toString() == "") {
            binding.dropdownGoal.error = "Please select a Goal."
            valid = false
        } else {
            binding.dropdownGoal.error = null
            goal = binding.dropdownGoal.text.toString()
        }

        if (binding.etDate.text.toString().trim().isEmpty()) {
            binding.etDate.error = "Please enter the name of the transaction."
            binding.etDate.requestFocus()
            valid = false
        } else {
            binding.etDate.error = null
            date = SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString())
        }

        if (binding.dropdownTypeOfPayment.text.toString() == "") {
            binding.dropdownTypeOfPayment.error = "Please select if you used cash or Maya"
            valid = false
        } else {
            binding.dropdownTypeOfPayment.error = null
            paymentType = binding.dropdownTypeOfPayment.text.toString()
        }

        return valid
    }

    private fun goToConfirmation() {
        binding.btnConfirm.setOnClickListener {
            if (validateAndSetUserInput() && validAmount()) {
                setBundle()
                val goToConfirmDeposit = Intent(this, ConfirmDepositActivity::class.java)
                goToConfirmDeposit.putExtras(bundle)
                goToConfirmDeposit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(goToConfirmDeposit)
            } else {
                Toast.makeText(baseContext, "Please fill up correctly the form.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun validAmount(): Boolean {
        //trying to deposit more than their current balance
        if (binding.etAmount.text.toString().toFloat() > walletBalance) {
            binding.etAmount.error =
                "You cannot deposit more than your current balance of ₱$walletBalance"
            binding.etAmount.requestFocus()
            return false
        }
        else
            return true
    }


    private fun setBundle() {
        //getCurrentTime()

        bundle.putString("transactionType", "Deposit")
        bundle.putFloat("amount", amount.toFloat())
        bundle.putString("goal", goal)
        bundle.putString("source", "PFMDepositToGoal")
        bundle.putString("savingActivityID", savingActivityID)
        bundle.putSerializable("date", date)
        bundle.putString("paymentType", paymentType)
        bundle.putFloat("balance", walletBalance)

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

        var calendar = dialog.findViewById<DatePicker>(ph.edu.dlsu.finwise.R.id.et_date)
        calendar.maxDate = System.currentTimeMillis()

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            date = SimpleDateFormat("MM/dd/yyyy").parse((mMonth + 1).toString() + "/" +
                    mDay.toString() + "/" + mYear.toString()) as Date
            dialog.dismiss()
        }
        dialog.show()
    }

    /* private fun getGoalProgress() {
     val sortSpinner = binding.dropdownActivity
     sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
         override fun onItemSelected(
             parentView: AdapterView<*>?,
             selectedItemView: View?,
             position: Int,
             id: Long
         ) {
             val goalID = goalArrayID[position]
             //check if may laman sa baba yung goalarrayID
             Toast.makeText(applicationContext, position,
                 Toast.LENGTH_LONG).show()
             firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener { document ->
                     goal = document.toObject<FinancialGoals>()!!
                     setProgressBar()
                 }
         }

         override fun onNothingSelected(parentView: AdapterView<*>?) {
             // your code here
         }
     }
 }

 private fun setProgressBar() {
     binding.progressGoal.max = goal.targetAmount!!.toInt()
     binding.progressGoal.progress = goal.currentAmount!!.toInt()
     val dec = DecimalFormat("#,###.00")
     var targetAmount = dec.format(goal.targetAmount)
     var currentAmount = dec.format(goal.currentAmount)
     binding.tvBalance.text = "₱$currentAmount / $targetAmount"
 }*/

}