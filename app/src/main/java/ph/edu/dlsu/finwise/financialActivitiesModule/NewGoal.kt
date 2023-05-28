package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityNewGoalBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.SettingsModel
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentFinancialActivity
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
    private var currentChildAge = 0

    private var maxAmount = 0.00F

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNavigationBar()
        getCurrentUserType()
        loadBackButton()

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
                bundle.putString("childID", childID)

                //TODO: reset spinner and date to default value
                binding.etGoal.text?.clear()
                binding.etAmount.text?.clear()
                binding.cbGoalSelf.isChecked = false

                goToGoalConfirmation.putExtras(bundle)
                goToGoalConfirmation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(goToGoalConfirmation)
            }
        }

        binding.btnCancel.setOnClickListener {
            // Determine the source of the user
            val source = intent.getStringExtra("source")

            // Create a bundle with any data you want to pass to the next activity
            val bundle = Bundle()
            bundle.putString("childID", childID)

            // Depending on the source, navigate to the appropriate activity
            when (source) {
                "Child" -> {
                    val intent = Intent(this, FinancialActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
                "Parent" -> {
                    val intent = Intent(this, ParentFinancialActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
                // Add additional cases for other sources
                else -> {
                    // If the source is unknown or not specified, just finish the current activity
                    finish()
                }
            }
        }
        binding.topAppBar.setNavigationOnClickListener {
            // Determine the source of the user
            val source = intent.getStringExtra("source")

            // Create a bundle with any data you want to pass to the next activity
            val bundle = Bundle()
            bundle.putString("childID", childID)

            // Depending on the source, navigate to the appropriate activity
            when (source) {
                "Child" -> {
                    val intent = Intent(this, FinancialActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
                "Parent" -> {
                    val intent = Intent(this, ParentFinancialActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
                // Add additional cases for other sources
                else -> {
                    // If the source is unknown or not specified, just finish the current activity
                    finish()
                }
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
                binding.layoutCommonGoalsChild.visibility = View.GONE
            }
            else if (it.toObject<Users>()!!.userType == "Child"){
                currentUserType = "Child"
                childID = currentUser
                binding.layoutCommonGoalsParents.visibility = View.GONE
            }

            CoroutineScope(Dispatchers.Main).launch {
                getCommonGoals()
            }
        }.continueWith {
            initializeDropDownForReasons()
        }
    }

    private suspend fun getCommonGoals() {
        var goalArrayList = ArrayList<String>()
        var goalCount = hashMapOf<String, Int>()

        var goals = firestore.collection("FinancialGoals").get().await()
        if (!goals.isEmpty) {
            for (goal in goals) {
                var goalCreatedBy = goal.toObject<FinancialGoals>().createdBy
                var goalChildID = goal.toObject<FinancialGoals>().childID
                //get the user type of the user who created the goal
                var userType = firestore.collection("Users").document(goalCreatedBy!!).get().await().toObject<Users>()!!.userType

                if (currentUserType == userType) {
                    //check if the age of the child and the child id in the goal is the same
                    var childObject = firestore.collection("Users").document(goalChildID!!).get().await().toObject<Users>()

                    //compute age
                    val date = SimpleDateFormat("MM/dd/yyyy").format(childObject?.birthday?.toDate())
                    val to = LocalDate.parse(date.toString(), DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                    var difference = Period.between(to, LocalDate.now())
                    var age = difference.years

                    if (currentChildAge == age)
                        goalArrayList.add(goal.toObject<FinancialGoals>().financialActivity!!)
                }

                if (!goalArrayList.isEmpty()) {
                    //count how many times the chore appear in the array list
                    for (goal in goalArrayList) {
                        if (goalCount.containsKey(goal))
                            goalCount[goal] = goalCount[goal]!! + 1
                        else
                            goalCount[goal] = 1
                    }

                    // Sort the HashMap by value in descending order
                    var sortedList = goalCount.entries.sortedByDescending { it.value }

                    if (sortedList.size > 3)
                        sortedList = sortedList.take(3)

                    var commonChoreText = ""
                    sortedList.forEachIndexed { index, (goal, count) ->
                        commonChoreText += "${index+1}. ${goal}"
                        if (!(index == sortedList.size - 1))
                            commonChoreText += "\n"
                    }

                    if (currentUserType == "Parent")
                        binding.tvCommonChoresParent.text = commonChoreText
                    else if (currentUserType == "Child")
                        binding.tvCommonChoresKids.text = commonChoreText
                } else {
                    binding.layoutCommonGoalsParents.visibility = View.GONE
                    binding.layoutCommonGoalsChild.visibility = View.GONE
                }

            }
        } else {
            binding.layoutCommonGoalsParents.visibility = View.GONE
            binding.layoutCommonGoalsChild.visibility = View.GONE
        }

    }

    private suspend fun getCommonGoalsChild() {

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeDropDownForReasons() {
        var dropdownContent = ArrayList<String>()

        firestore.collection("Settings").whereEqualTo("childID", childID).get().addOnSuccessListener {
            if (!it.isEmpty) {
                var settings = it.documents[0].toObject<SettingsModel>()
                maxAmount = settings?.maxAmountActivities!!.toFloat()
                binding.tvMaxAmount.text = "The max amount that can be set is ₱${DecimalFormat("#0.00").format(maxAmount)}"

                if (settings.buyingItem == true)
                    dropdownContent.add("Buying Items")
                if (settings.donatingCharity == true)
                    dropdownContent.add("Donating To Charity")
                if (settings.situationalShopping == true)
                    dropdownContent.add("Situational Shopping")
                if (settings.planingEvent == true)
                    dropdownContent.add("Planning An Event")
                if (settings.emergencyFund == true)
                    dropdownContent.add("Saving For Emergency Funds")

                // for the dropdown
                val adapter = ArrayAdapter(this, R.layout.list_item, dropdownContent)
                binding.dropdownActivity.setAdapter(adapter)
            }
        }

//        firestore.collection("Users").document(childID).get().addOnSuccessListener {
//            var child = it.toObject<Users>()
//
//            //compute age
//            val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
//            val from = LocalDate.now()
//            val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
//            val to = LocalDate.parse(date.toString(), dateFormatter)
//            var difference = Period.between(to, from)
//
//            currentChildAge = difference.years
//            if (currentChildAge == 9) {
//                maxAmount = 3000F
//                binding.tvMaxAmount.text = "The max amount that can be set is ₱${DecimalFormat("#0.00").format(maxAmount)}"
//            }
//            dropdownContent.add("Buying Items")
//
//            if (currentChildAge == 10 || currentChildAge == 11 || currentChildAge == 12) {
//                maxAmount = 5000F
//                binding.tvMaxAmount.text = "The max amount that can be set is ₱${DecimalFormat("#0.00").format(maxAmount)}"
//
//                dropdownContent.add("Situational Shopping")
//                dropdownContent.add("Donating To Charity")
//
//                if (currentChildAge == 12) {
//                    maxAmount = 10000F
//                    binding.tvMaxAmount.text = "The max amount that can be set is ₱${DecimalFormat("#0.00").format(maxAmount)}"
//
//                    dropdownContent.add("Planning An Event")
//                    dropdownContent.add("Saving For Emergency Funds")
//                }
//            }
//        }
    }

    private fun filledUp() : Boolean {
        var valid  = true

        if (binding.etGoal.text.toString().trim().isEmpty()) {
            binding.goalContainer.helperText = "Input goal name."
            valid = false
        } else
            binding.goalContainer.helperText = ""

        if (binding.dropdownActivity.text.toString().trim().isEmpty()) {
            binding.containerActivity.helperText = "Input goal name."
            valid = false
        } else
            binding.containerActivity.helperText = ""

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
    private fun setNavigationBar() {

        val navUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(navUser).get().addOnSuccessListener {

            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent

            if (it.toObject<Users>()!!.userType == "Parent") {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                //sends the ChildID to the parent navbar
                val bundle = intent.extras!!
                val childID = bundle.getString("childID").toString()
                val bundleNavBar = Bundle()
                bundleNavBar.putString("childID", childID)
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)
            } else if (it.toObject<Users>()!!.userType == "Child") {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

            }
        }
    }
    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}