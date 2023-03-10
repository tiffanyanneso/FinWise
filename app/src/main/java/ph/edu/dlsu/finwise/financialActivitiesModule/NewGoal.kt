package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityNewGoalBinding
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentGoalActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter


class NewGoal : AppCompatActivity() {

    private lateinit var binding : ActivityNewGoalBinding
    private var firestore = Firebase.firestore

    private lateinit var currentUserType:String
    private lateinit var childID:String

    private var maxAmount = 0.00F
    private var age = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        getCurrentUserType()

        binding.etTargetDate.setOnClickListener{
            showCalendar()
        }

        binding.btnNext.setOnClickListener {
            if(filledUp()) {
                var goToGoalConfirmation = Intent(this, GoalConfirmationActivity::class.java)
                var bundle = Bundle()

                var goalName =  binding.etGoal.text.toString()
                var activity = binding.dropdownActivity.text.toString()
                var amount = binding.etAmount.text.toString().toFloat()
                var targetDate = binding.etTargetDate.text.toString()

                var goalIsForSelf = binding.cbGoalSelf.isChecked

                bundle.putString("goalName", goalName)
                bundle.putString("activity", activity)
                bundle.putFloat("amount", amount)
                bundle.putSerializable("targetDate",  SimpleDateFormat("MM/dd/yyyy").parse(targetDate))
                bundle.putBoolean("goalIsForSelf", goalIsForSelf)

                if(currentUserType == "Parent")
                    bundle.putString("childID", childID)

                //TODO: reset spinner and date to default value
                binding.etGoal.text?.clear()
                binding.etAmount.text?.clear()

                goToGoalConfirmation.putExtras(bundle)
                goToGoalConfirmation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(goToGoalConfirmation)
            }
        }

        binding.btnCancel.setOnClickListener {
            val goalList = Intent(this, FinancialActivity::class.java)
            this.startActivity(goalList)
        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            var backBundle = intent.extras!!
            var source = backBundle.getString("source").toString()

            var navBundle = Bundle()
            navBundle.putString("childID", childID)

            if (source =="childFinancialActivity") {
                val goToFinancialActivity = Intent(applicationContext, FinancialActivity::class.java)
                goToFinancialActivity.putExtras(navBundle)
                goToFinancialActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(goToFinancialActivity)
            } else if (source =="parentGoalActivity") {
                val goToParentGoalActivity = Intent(applicationContext, ParentGoalActivity::class.java)
                goToParentGoalActivity.putExtras(navBundle)
                goToParentGoalActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(goToParentGoalActivity)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentUserType() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            if (it.toObject<Users>()!!.userType == "Parent"){
                currentUserType = "Parent"
                var childIDBundle = intent.extras!!
                childID = childIDBundle.getString("childID").toString()
            }
            else if (it.toObject<Users>()!!.userType == "Child"){
                currentUserType = "Child"
                childID = currentUser
            }
        }.continueWith {
            initializeDropDownForReasons()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeDropDownForReasons() {
        var dropdownContent = ArrayList<String>()

        firestore.collection("Users").document(childID).get().addOnSuccessListener {
            var child = it.toObject<Users>()

            //compute age
            val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var age = difference.years
            if (age == 9) {
                maxAmount = 3000F
                binding.tvMaxAmount.text = "The max amount that can be set is ???${DecimalFormat("#0.00").format(maxAmount)}"
            }
            dropdownContent.add("Buying Items")

            if (age == 10 || age == 11 || age == 12) {
                maxAmount = 5000F
                binding.tvMaxAmount.text = "The max amount that can be set is ???${DecimalFormat("#0.00").format(maxAmount)}"

                dropdownContent.add("Situational Shopping")
                dropdownContent.add("Donating To Charity")

                if (age == 12) {
                    maxAmount = 10000F
                    binding.tvMaxAmount.text = "The max amount that can be set is ???${DecimalFormat("#0.00").format(maxAmount)}"

                    dropdownContent.add("Planning An Event")
                    dropdownContent.add("Saving For Emergency Funds")
                }
            }
        }

        // for the dropdown
        val adapter = ArrayAdapter(this, R.layout.list_item, dropdownContent)
        binding.dropdownActivity.setAdapter(adapter)
    }

    private fun filledUp() : Boolean {
        var valid  = true

        if (binding.etGoal.text.toString().trim().isEmpty()) {
            binding.goalContainer.helperText = "Input goal name."
            valid = false
        } else
            binding.goalContainer.helperText = ""

      //TODO: VALIDATION FOR REASON

        if (binding.etAmount.text.toString().trim().isEmpty()) {
            binding.amountContainer.helperText = "Input target amount."
            valid = false
        } else {
            //amount field is not empty
            binding.amountContainer.helperText = ""
            //check if amount is greater than 0
            if (binding.etAmount.text.toString().toFloat() <= 0) {
                binding.amountContainer.helperText = "Input a valid amount."
                valid = false
            } else {
                binding.amountContainer.helperText = ""
                //check if the amount is within max amount
                if (binding.etAmount.text.toString().toFloat() > maxAmount) {
                    binding.amountContainer.helperText = "Amount is greater than maximum allowed "
                    valid = false
                }
                else
                    binding.amountContainer.helperText = ""
            }
        }

        if (binding.etTargetDate.text.toString().trim().isEmpty()) {
            binding.dateContainer.helperText = "Select target date."
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
        calendar.minDate = System.currentTimeMillis()

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etTargetDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
    }
}