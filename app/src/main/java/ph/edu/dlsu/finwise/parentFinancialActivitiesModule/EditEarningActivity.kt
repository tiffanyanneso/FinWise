package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityEditEarningBinding
import ph.edu.dlsu.finwise.databinding.ActivityNewEarningBinding
import ph.edu.dlsu.finwise.databinding.DialogConfirmDeleteEarningBinding
import ph.edu.dlsu.finwise.databinding.DialogNewCustomChoreBinding
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.math.max

class EditEarningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditEarningBinding

    private var savingActivityID:String?=null
    private lateinit var childID:String
    private lateinit var earningActivityID:String

    private var firestore = Firebase.firestore

    private var maxAmount = 0.00


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditEarningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        childID = bundle.getString("childID").toString()
        earningActivityID = bundle.getString("earningActivityID").toString()

        checkAge()
        fillUpFields()

        loadBackButton()
        cancel()

        // Initializes the navbar
        //sends the ChildID to the parent navbar
        val bundleNavBar = Bundle()
        bundleNavBar.putString("childID", childID)
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)

        binding.etDate.setOnClickListener { showCalendar() }


        binding.dropdownDestination.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                if (binding.dropdownDestination.text.toString() == "Personal Finance")
                    binding.layoutGoal.visibility = View.GONE
                else if (binding.dropdownDestination.text.toString() == "Financial Goal")
                    binding.layoutGoal.visibility = View.VISIBLE
            }

        binding.btnConfirm.setOnClickListener {
            if (filledUp() && validAmount()) {
                var targetDate = SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString())
                var amount = binding.etAmount.text.toString().toFloat()
                firestore.collection("EarningActivities").document(earningActivityID).update("targetDate", targetDate, "amount", amount)
                val earning = Intent(this, EarningActivity::class.java)
                val sendBundle = Bundle()
                sendBundle.putString("childID", childID)
                earning.putExtras(sendBundle)
                startActivity(earning)
            }
        }

        binding.btnDelete.setOnClickListener {
            confirmDeleteDialog()
        }
    }

    private fun confirmDeleteDialog() {
        var dialogBinding= DialogConfirmDeleteEarningBinding.inflate(layoutInflater)
        var dialog= Dialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.window!!.setLayout(850, 680)
        dialogBinding.btnYes.setOnClickListener {
            firestore.collection("EarningActivities").document(earningActivityID).update("status", "Deleted").addOnSuccessListener {
                val earning = Intent(this, EarningActivity::class.java)
                val sendBundle = Bundle()
                sendBundle.putString("childID", childID)
                earning.putExtras(sendBundle)
                startActivity(earning)
            }
        }

        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }



    private fun fillUpFields() {
       firestore.collection("EarningActivities").document(earningActivityID).get().addOnSuccessListener {
           var earning = it.toObject<EarningActivityModel>()!!
           binding.dropdownChore.setText(earning.activityName.toString())
           binding.etDuration.setText(earning.requiredTime.toString())
           binding.etDate.setText(SimpleDateFormat("MM/dd/yyyy").format(earning.targetDate!!.toDate()))
           binding.dropdownDestination.setText(earning.depositTo.toString())
           if (earning.depositTo.toString() == "Financial Goal") {
               binding.layoutGoal.visibility = View.VISIBLE
               firestore.collection("FinancialActivities").document(earning.savingActivityID!!).get().addOnSuccessListener {
                   var goalID = it.toObject<FinancialActivities>()!!.financialGoalID
                   firestore.collection("FinancialGoals").document(goalID!!).get().addOnSuccessListener { goal ->
                       var goalObject = goal.toObject<FinancialGoals>()!!
                       binding.dropdownGoal.setText(goal.toObject<FinancialGoals>()!!.goalName)
                       binding.tvProgressAmount.text = "₱ ${DecimalFormat("#,##0.00").format(goalObject.currentSavings)} / ₱${DecimalFormat("#,##0.00").format(goalObject.targetAmount)}"
                   }
               }
           } else
               binding.layoutGoal.visibility = View.GONE

           binding.dropdownTypeOfPayment.setText(earning.paymentType.toString())
           binding.etAmount.setText(earning.amount!!.toInt().toString())
       }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAge() {
        firestore.collection("Users").document(childID).get().addOnSuccessListener {
            var child = it.toObject<Users>()

            //compute age
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date = SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var age = difference.years
            if (age == 9)
                maxAmount = 100.0
            else if (age == 10 || age == 11)
                maxAmount = 300.0
            else
                maxAmount = 500.0
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(ph.edu.dlsu.finwise.R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(ph.edu.dlsu.finwise.R.id.et_date)
        calendar.minDate = System.currentTimeMillis()

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun validAmount():Boolean {
        println("print " + binding.etAmount.text.toString().toFloat())
        println("print " + maxAmount)
        if (binding.etAmount.text.toString().toFloat() > maxAmount) {
            binding.amountContainer.helperText = "You cannot set an amount greater than the maximum"
            return false
        }
        else
            return true
    }

    private fun filledUp(): Boolean {
        var valid = true

        if (binding.dropdownChore.text.toString().isEmpty()) {
            binding.containerChore.helperText = "Please select a chore"
            valid = false
        } else
            binding.containerChore.helperText = ""

        if (binding.etDate.text.toString().trim().isEmpty()) {
            binding.dateContainer.helperText = "Please select a target date"
            valid = false
        } else
            binding.dateContainer.helperText = ""

        if (binding.dropdownDestination.text.toString().trim().isEmpty()) {
            binding.containerDestination.helperText = "Please select a destination"
            valid = false
        } else
            binding.containerDestination.helperText = ""

        if (binding.etAmount.text.toString().trim().isEmpty()) {
            binding.amountContainer.helperText = "Please input an amount"
            valid = false
        } else
            binding.amountContainer.helperText = ""

        if (binding.dropdownTypeOfPayment.text.toString().trim().isEmpty()) {
            binding.containerTypeOfPayment.helperText = "Please select a type of payment"
            valid = false
        } else
            binding.containerTypeOfPayment.helperText = ""

        return valid
    }

    private fun cancel() {
        binding.btnCancel.setOnClickListener {
            val goBack = Intent(applicationContext, EarningActivity::class.java)
            startActivity(goBack)
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}