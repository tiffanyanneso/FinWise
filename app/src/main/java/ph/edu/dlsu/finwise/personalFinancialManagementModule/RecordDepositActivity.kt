package ph.edu.dlsu.finwise.personalFinancialManagementModule


import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordDepositBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecordDepositActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmrecordDepositBinding
    var bundle = Bundle()
    private var firestore = Firebase.firestore

    private var goals = ArrayList<String>()
    private var goalArrayID = ArrayList<String>()
    lateinit var goal : String
    lateinit var amount: String
    lateinit var date: Date
    var walletBalance = 0.00f

    private var childID = FirebaseAuth.getInstance().currentUser!!.uid

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
        }.continueWith {
            initializeDatePicker()
            getGoals()
            loadBackButton()
            goToConfirmation()
            cancel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeDatePicker() {
        binding.etDate.setText(SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate()))

        binding.etDate.setOnClickListener{
            showCalendar()
        }
    }

    private fun getGoals() {
        firestore.collection("FinancialGoals").whereEqualTo("childID", childID). whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            for (goal in results) {
                val goalObject = goal.toObject<FinancialGoals>()
                goals.add(goalObject.goalName.toString())
                goalArrayID.add(goal.id)
            }

            val adapter = ArrayAdapter(this, ph.edu.dlsu.finwise.R.layout.list_item, goals)
            binding.dropdownActivity.setAdapter(adapter)

            //getGoalProgress()
        }
    }
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

        if (binding.dropdownActivity.text.toString() == "") {
            binding.dropdownActivity.error = "Please select a Goal."
            valid = false
        } else {
            binding.dropdownActivity.error = null
            goal = binding.dropdownActivity.text.toString()
        }

        if (binding.etDate.text.toString().trim().isEmpty()) {
            binding.etDate.error = "Please enter the name of the transaction."
            binding.etDate.requestFocus()
            valid = false
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
                Toast.makeText(
                    baseContext, "Please fill up correctly the form.",
                    Toast.LENGTH_SHORT
                ).show()
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
        bundle.putSerializable("date", date)
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