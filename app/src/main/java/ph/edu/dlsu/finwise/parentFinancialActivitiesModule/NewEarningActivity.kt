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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityNewEarningBinding
import ph.edu.dlsu.finwise.databinding.DialogNewCustomChoreBinding
import ph.edu.dlsu.finwise.model.*
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
    private var currentChildAge = 0

    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private var maxAmount = 0.00F

    private var goalDropDownArrayList = ArrayList<GoalDropDown>()
    private lateinit var goalAdapter: ArrayAdapter<String>

    private var dropdownChores = ArrayList<String>()
    private lateinit var choresAdapter:ArrayAdapter<String>


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewEarningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        choresAdapter = ArrayAdapter(this, R.layout.list_item, dropdownChores)

        var bundle = intent.extras!!
        childID = bundle.getString("childID").toString()
        module = bundle.getString("module").toString()

        fillUpFields()

        CoroutineScope(Dispatchers.Main).launch {
            initializeDropDowns()
            getCommonChores()
            binding.layoutLoading.visibility = View.GONE
            binding.mainLayout.visibility = View.VISIBLE
        }
        loadBackButton()
        cancel()

        // Initializes the navbar
        //sends the ChildID to the parent navbar
        val bundleNavBar = Bundle()
        bundleNavBar.putString("childID", childID)
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)

        binding.etDate.setOnClickListener { showCalendar() }

        binding.dropdownChore.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
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
                        "childAge" to currentChildAge,
                        "status" to "Ongoing",
                        "paymentType" to binding.dropdownTypeOfPayment.text.toString(),
                        "depositTo" to binding.dropdownDestination.text.toString(),
                        "dateAdded" to Timestamp.now()
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
                        "depositTo" to binding.dropdownDestination.text.toString(),
                        "dateAdded" to Timestamp.now()
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

    private fun fillUpFields() {
        if (module == "pfm") {
            binding.dropdownDestination.setText("Personal Finance")
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
        else if (chore=="+ Add Custom Chore")
            addCustomChore()
        //the chore is custom, query to db to get duration
        else {
            firestore.collection("Chores").whereEqualTo("createdBy", currentUser).whereEqualTo("choreName", chore).get().addOnSuccessListener {
                binding.etDuration.setText(it.documents[0].toObject<Chores>()?.duration.toString())
            }
        }
    }


    private fun addCustomChore() {
        var dialogBinding= DialogNewCustomChoreBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(800, 1000)

        dialogBinding.btnSave.setOnClickListener {
            dialogBinding.containerChoreName.helperText = ""
            dialogBinding.containerDuration.helperText = ""
            var choreName  = dialogBinding.etChoreName.text.toString()
            var duration  = dialogBinding.etDuration.text.toString()
            if (choreName.isNotEmpty() && duration.isNotEmpty()) {
                var chore = hashMapOf(
                    "choreName" to choreName,
                    "duration" to duration.toInt(),
                    "createdBy" to currentUser
                )
                firestore.collection("Chores").add(chore).addOnSuccessListener {
                    dialog.dismiss()
                    binding.dropdownChore.setText(choreName)
                    binding.etDuration.setText(duration.toString())
                    dropdownChores.add(choreName)
                    binding.dropdownChore.setAdapter(choresAdapter)
                }
            } else {
                if (choreName.isEmpty())
                    dialogBinding.containerChoreName.helperText = "Input chore name"
                if (duration.isEmpty())
                    dialogBinding.containerDuration.helperText = "Input duration"
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private suspend fun getCommonChores() {
        var choreArrayList = ArrayList<String>()
        var choreCount = hashMapOf<String, Int>()

        var chores = firestore.collection("EarningActivities")
            .whereEqualTo("childAge", currentChildAge)
            .whereEqualTo("status", "Completed").get().await()

        if (!chores.isEmpty) {
            for (chore in chores)
                choreArrayList.add(chore.toObject<EarningActivityModel>().activityName!!)


            //count how many times the chore appear in the array list
            for (chore in choreArrayList) {
                if (choreCount.containsKey(chore))
                    choreCount[chore] = choreCount[chore]!! + 1
                else
                    choreCount[chore] = 1
            }

            var sortedList = choreCount.entries.sortedByDescending { it.value }.take(3)
            val commonChoreText = sortedList.mapIndexed { index, (chore, _) ->
                "${index + 1}. $chore"
            }.joinToString("\n")

            binding.layoutCommonChores.visibility = View.VISIBLE
            binding.tvCommonChores.text = commonChoreText

        } else
            binding.layoutCommonChores.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun initializeDropDowns() {
        val paymentTypeItems = resources.getStringArray(R.array.payment_type)
        val adapterPaymentTypeItems = ArrayAdapter(this, R.layout.list_item, paymentTypeItems)
        binding.dropdownTypeOfPayment.setAdapter(adapterPaymentTypeItems)

        var child =  firestore.collection("Users").document(childID).get().await().toObject<Users>()

        //compute age
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val from = LocalDate.now()
        val date = SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
        val to = LocalDate.parse(date.toString(), dateFormatter)
        var difference = Period.between(to, from)

        currentChildAge = difference.years
        if (currentChildAge == 9) {
            maxAmount = 100F
        }
        //chores for age 9-12
        dropdownChores.add("Put Away Groceries")
        dropdownChores.add("Put Away Laundry")
        dropdownChores.add("Clean Floor")
        dropdownChores.add("Set Table")

        if (currentChildAge == 10 || currentChildAge == 11 || currentChildAge == 12) {
            maxAmount = 300F

            dropdownChores.add("Fold Laundry")
            dropdownChores.add("Help Parent Prepare Meal")
            dropdownChores.add("Prepare Snack")
            dropdownChores.add("Change Bed Sheets")

            if (currentChildAge == 12) {
                maxAmount = 500F

                dropdownChores.add("Mop Floor")
                dropdownChores.add("Clean Bathroom")
                dropdownChores.add("Wash Dishes")
                dropdownChores.add("Wash Car")
                dropdownChores.add("Prepare Meal") }
        }

        firestore.collection("Chores").whereEqualTo("createdBy", currentUser).get().addOnSuccessListener { chores ->
            for (chore in chores) {
                var dbChore = chore.toObject<Chores>()
                dropdownChores.add(dbChore.choreName.toString())
            }
        }.continueWith {
            dropdownChores.add("+ Add Custom Chore")
        }

        binding.tvMaxAmount.text = "The max amount that can be given is ₱${DecimalFormat("#0.00").format(maxAmount)}"


        // for the dropdown
        binding.dropdownChore.setAdapter(choresAdapter)

        var incomeDestinationArrayList = ArrayList<String>()
        incomeDestinationArrayList.add("Personal Finance")

        firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Saving").whereEqualTo("status", "In Progress").get().addOnSuccessListener { activityResults ->
            for (saving in activityResults) {
                firestore.collection("FinancialGoals").document(saving.toObject<FinancialActivities>().financialGoalID!!).get().addOnSuccessListener { goal ->
                    goalDropDownArrayList.add(
                        GoalDropDown(saving.id, goal.toObject<FinancialGoals>()?.goalName!!)
                    )
                }.continueWith {
                    goalAdapter = ArrayAdapter(this, R.layout.list_item, goalDropDownArrayList.map { it.goalName })
                    binding.dropdownGoal.setAdapter(goalAdapter)
                }
            }

            if (!activityResults.isEmpty)
                incomeDestinationArrayList.add("Financial Goal")

            val incomeDestinationAdapter = ArrayAdapter(this, R.layout.list_item, incomeDestinationArrayList)
            binding.dropdownDestination.setAdapter(incomeDestinationAdapter)
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