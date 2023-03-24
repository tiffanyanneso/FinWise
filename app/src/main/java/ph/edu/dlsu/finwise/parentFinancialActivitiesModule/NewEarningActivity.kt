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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityNewEarningBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.math.max

class NewEarningActivity : AppCompatActivity() {

    private lateinit var binding:ActivityNewEarningBinding

    private var savingActivityID:String?=null
    private lateinit var module:String
    private lateinit var childID:String

    private var firestore = Firebase.firestore

    private var maxAmount = 0.00F

    private var goalDropDownArrayList = ArrayList<GoalDropDown>()
    private lateinit var goalAdapter: ArrayAdapter<String>


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewEarningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        childID = bundle.getString("childID").toString()
        module = bundle.getString("module").toString()

        fillUpFields()

        initializeDropDowns()
        loadBackButton()
        cancel()

        // Initializes the navbar
        //sends the ChildID to the parent navbar
        val getNavbarBundle = Bundle()
        val childID = getNavbarBundle.getString("childID").toString()
        val bundleNavBar = Bundle()
        bundleNavBar.putString("childID", childID)
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)

        binding.etDate.setOnClickListener { showCalendar() }

        binding.dropdownChore.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                changeDuration()
            }

        binding.dropdownDestination.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                if (binding.dropdownDestination.text.toString() == "Personal Finance")
                    binding.layoutGoal.visibility = View.GONE
                else if (binding.dropdownDestination.text.toString() == "Financial Goal")
                    binding.layoutGoal.visibility = View.VISIBLE
            }

        binding.dropdownGoal.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                savingActivityID = goalDropDownArrayList[position].savingActivityID

                firestore.collection("FinancialActivities").document(savingActivityID.toString())
                    .get().addOnSuccessListener { saving ->
                    firestore.collection("FinancialGoals")
                        .document(saving.toObject<FinancialActivities>()?.financialGoalID!!).get()
                        .addOnSuccessListener { goal ->
                            binding.tvProgressAmount.text =
                                "₱ " + DecimalFormat("#,##0.00").format(goal.toObject<FinancialGoals>()?.currentSavings)!! +
                                        " / ₱ " + DecimalFormat("#,##0.00").format(goal.toObject<FinancialGoals>()?.targetAmount!!)
                        }
                }
            }

        binding.btnConfirm.setOnClickListener {
            if (filledUp() && validAmount()) {
                if (binding.dropdownDestination.text.toString() == "Personal Finance") {
                    val earningActivity = hashMapOf(
                        "activityName" to binding.dropdownChore.text.toString(),
                        "targetDate" to SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString()),
                        "requiredTime" to binding.etDuration.text.toString().toInt(),
                        "amount" to binding.etAmount.text.toString().toFloat(),
                        "childID" to childID,
                        "status" to "Ongoing",
                        "paymentType" to binding.dropdownTypeOfPayment.text.toString(),
                        "depositTo" to binding.dropdownDestination.text.toString()
                    )
                    firestore.collection("EarningActivities").add(earningActivity)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Earning activity saved", Toast.LENGTH_SHORT)
                                .show()
                        }
                } else if (binding.dropdownDestination.text.toString() == "Financial Goal") {
                    var earningActivity = hashMapOf(
                        "activityName" to binding.dropdownChore.text.toString(),
                        "targetDate" to SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString()),
                        "requiredTime" to binding.etDuration.text.toString().toInt(),
                        "amount" to binding.etAmount.text.toString().toFloat(),
                        "childID" to childID,
                        "savingActivityID" to savingActivityID,
                        "status" to "Ongoing",
                        "paymentType" to binding.dropdownTypeOfPayment.text.toString(),
                        "depositTo" to binding.dropdownDestination.text.toString()
                    )
                    firestore.collection("EarningActivities").add(earningActivity)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Earning activity saved", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
                val earning = Intent(this, EarningActivity::class.java)
                val sendBundle = Bundle()
                sendBundle.putString("childID", childID)
                earning.putExtras(sendBundle)
                startActivity(earning)
            }
        }
    }

    private fun validAmount():Boolean {
        if (binding.etAmount.text.toString().toFloat() > maxAmount) {
            binding.amountContainer.helperText = "You cannot deposit more than your current balance"
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

    private fun fillUpFields() {
        if (module == "pfm") {
            binding.dropdownDestination.setText("Personal Finance")
            binding.dropdownGoal.visibility = View.GONE
        } else if (module == "finact") {
            var bundle = intent.extras!!
            var financialGoalID = bundle.getString("financialGoalID").toString()

            binding.dropdownDestination.setText("Financial Goal")
            binding.layoutGoal.visibility = View.VISIBLE
            firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener { goal ->
                binding.tvProgressAmount.text = "₱ " + DecimalFormat("#,##0.00").format(goal.toObject<FinancialGoals>()?.currentSavings)!! +
                        " / ₱ " + DecimalFormat("#,##0.00").format(goal.toObject<FinancialGoals>()?.targetAmount!!)
                binding.dropdownGoal.setText(goal.toObject<FinancialGoals>()!!.goalName)
                firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", financialGoalID).whereEqualTo("financialActivityName", "Saving").get().addOnSuccessListener {
                    savingActivityID = it.documents[0].id
                }
            }
        }
    }


    class GoalDropDown(var savingActivityID:String, val goalName:String)

    private fun changeDuration() {
        var chore = binding.dropdownChore.text.toString()

        if (chore == "Put Away Laundry" || chore == "Set Table")
            binding.etDuration.setText("5")
        else if (chore == "Put Away Groceries" || chore == "Clean Floor" || chore == "Fold Laundry" || chore == "Change Bed Sheets" || chore == "Feed Pets" || chore =="Mop Floor")
            binding.etDuration.setText("10")
        else if (chore == "Help Parent Prepare Meal" || chore == "Prepare Snack" || chore == "Wash Dishes")
            binding.etDuration.setText("15")
        else if (chore == "Clean Bathroom" || chore == "Wash Car" || chore == "Prepare Meal" || chore == "Take Care Of Younger Sibling")
            binding.etDuration.setText("30")

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeDropDowns() {
        val paymentTypeItems = resources.getStringArray(R.array.payment_type)
        val adapterPaymentTypeItems = ArrayAdapter (this, R.layout.list_item, paymentTypeItems)
        binding.dropdownTypeOfPayment.setAdapter(adapterPaymentTypeItems)

        val dropdownContent = ArrayList<String>()
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
                maxAmount = 100F
            }
            //chores for age 9-12
            dropdownContent.add("Put Away Groceries")
            dropdownContent.add("Put Away Laundry")
            dropdownContent.add("Clean Floor")
            dropdownContent.add("Set Table")

            if (age == 10 || age == 11 || age == 12) {
                maxAmount = 300F

                dropdownContent.add("Fold Laundry")
                dropdownContent.add("Help Parent Prepare Meal")
                dropdownContent.add("Prepare Snack")
                dropdownContent.add("Change Bed Sheets")
                dropdownContent.add("Feed Pets")

                if (age == 12) {
                    maxAmount = 500F

                    dropdownContent.add("Mop Floor")
                    dropdownContent.add("Clean Bathroom")
                    dropdownContent.add("Wash Dishes")
                    dropdownContent.add("Wash Car")
                    dropdownContent.add("Prepare Meal")
                    dropdownContent.add("Take Care Of Younger Sibling")}
            }
            binding.tvMaxAmount.text = "The max amount that can be given is ₱${DecimalFormat("#0.00").format(maxAmount)}"
        }

        // for the dropdown
        val choresAdapter = ArrayAdapter(this, R.layout.list_item, dropdownContent)
        binding.dropdownChore.setAdapter(choresAdapter)

        val incomeDestinationAdapter = ArrayAdapter(this, R.layout.list_item, resources.getStringArray(R.array.income_destination))
        binding.dropdownDestination.setAdapter(incomeDestinationAdapter)

        firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Saving").whereEqualTo("status", "In Progress").get().addOnSuccessListener { activityResults ->
            for (saving in activityResults) {
                firestore.collection("FinancialGoals").document(saving.toObject<FinancialActivities>().financialGoalID!!).get().addOnSuccessListener { goal ->
                    goalDropDownArrayList.add(GoalDropDown(saving.id, goal.toObject<FinancialGoals>()?.goalName!!))
                }.continueWith {
                    goalAdapter = ArrayAdapter(this, R.layout.list_item, goalDropDownArrayList.map { it.goalName })
                    binding.dropdownGoal.setAdapter(goalAdapter)
                }
            }
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