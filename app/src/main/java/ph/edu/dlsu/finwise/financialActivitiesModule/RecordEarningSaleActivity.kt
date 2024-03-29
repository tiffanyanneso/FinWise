package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
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
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityRecordEarningSaleBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningSellingActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.NewEarningActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class RecordEarningSaleActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRecordEarningSaleBinding
    private var firestore = Firebase.firestore

    private var goalDropDownArrayList = ArrayList<NewEarningActivity.GoalDropDown>()
    private lateinit var goalAdapter: ArrayAdapter<String>

    private lateinit var savingActivityID:String

    private lateinit var bundle: Bundle

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordEarningSaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bundle = intent.extras!!
        val childID = bundle.getString("childID").toString()
        binding.etDate.setText(SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate()))

        checkBundle()
        loadButtons()
        initializeDropDowns()
        setNavigationBar()


        binding.etDate.setOnClickListener { showCalendar() }

        binding.dropdownDestination.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if (binding.dropdownDestination.text.toString() == "Personal Finance")
                binding.layoutGoal.visibility = View.GONE
            else if (binding.dropdownDestination.text.toString() == "Financial Goal")
                binding.layoutGoal.visibility = View.VISIBLE
        }

        binding.dropdownGoal.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            savingActivityID = goalDropDownArrayList[position].savingActivityID

            firestore.collection("FinancialActivities").document(savingActivityID).get().addOnSuccessListener { saving ->
                firestore.collection("FinancialGoals").document(saving.toObject<FinancialActivities>()?.financialGoalID!!).get().addOnSuccessListener { goal ->
                    binding.tvProgressAmount.text = "₱ " + DecimalFormat("#,##0.00").format(goal.toObject<FinancialGoals>()?.currentSavings)!! +
                            " / ₱ " + DecimalFormat("#,##0.00").format(goal.toObject<FinancialGoals>()?.targetAmount!!)
                    binding.pbProgress.progress = ((goal.toObject<FinancialGoals>()?.currentSavings!! / goal.toObject<FinancialGoals>()?.targetAmount!!)*100).toInt()
                }
            }
        }

        binding.btnConfirm.setOnClickListener {
            if (filledUp()) {
                var confirmSale = Intent(this, RecordEarningSaleConfirmationActivity::class.java)
                var sendBundle = Bundle()
                sendBundle.putString("childID", childID)
                sendBundle.putString("saleName", binding.etName.text.toString())
                sendBundle.putSerializable("saleDate", SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString()))
                sendBundle.putFloat("saleAmount", binding.etAmount.text.toString().toFloat())
                sendBundle.putString("depositTo", binding.dropdownDestination.text.toString())
                sendBundle.putString("paymentType", binding.dropdownTypeOfPayment.text.toString())
                if (binding.dropdownDestination.text.toString() == "Financial Goal")
                    sendBundle.putString("savingActivityID", savingActivityID)
                confirmSale.putExtras(sendBundle)
                startActivity(confirmSale)
            }
        }
    }

    private fun filledUp() :Boolean {
        var valid = true

        if (binding.etName.text.toString().trim().isEmpty()) {
            binding.containerSaleName.helperText = "Please input name of sale item"
            valid = false
        } else
            binding.containerSaleName.helperText = ""

        if (binding.etAmount.text.toString().trim().isEmpty()) {
            binding.amountContainer.helperText = "Please input an amount"
            valid = false
        } else {
            if (binding.etAmount.text.toString().toFloat() <= 0) {
                binding.amountContainer.helperText = "Input a valid amount."
                valid = false
            } else
                binding.amountContainer.helperText = ""
        }

        if (binding.dropdownDestination.text.toString().trim().isEmpty()) {
            binding.containerDestination.helperText = "Please select a destination"
            valid = false
        } else
            binding.containerDestination.helperText = ""

        if (binding.etDate.text.toString().trim().isEmpty()) {
            binding.dateContainer.helperText = "Please select a date"
            valid = false
        } else
            binding.dateContainer.helperText = ""

        if (binding.dropdownTypeOfPayment.text.toString().trim().isEmpty()) {
            binding.containerTypeOfPayment.helperText = "Please select a type of payment"
            valid = false
        } else
            binding.containerTypeOfPayment.helperText = ""


        return valid
    }


    private fun initializeDropDowns() {
        var incomeDestinationArrayList = ArrayList<String>()
        incomeDestinationArrayList.add("Personal Finance")

        val paymentTypeItems = resources.getStringArray(R.array.payment_type)
        val adapterPaymentTypeItems = ArrayAdapter (this, R.layout.list_item, paymentTypeItems)
        binding.dropdownTypeOfPayment.setAdapter(adapterPaymentTypeItems)


        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Saving").whereEqualTo("status", "In Progress").get().addOnSuccessListener { activityResults ->
            for (saving in activityResults) {
                firestore.collection("FinancialGoals").document(saving.toObject<FinancialActivities>().financialGoalID!!).get().addOnSuccessListener { goal ->
                    goalDropDownArrayList.add(
                        NewEarningActivity.GoalDropDown(
                            saving.id,
                            goal.toObject<FinancialGoals>()?.goalName!!
                        )
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

        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        val calendar = dialog.findViewById<DatePicker>(R.id.et_date)
        calendar.maxDate = System.currentTimeMillis()

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun loadButtons() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.btnCancel.setOnClickListener {
            var saleList = Intent(this, EarningSellingActivity::class.java)
            var bundle = Bundle()
            bundle.putString("childID", currentUser)
            saleList.putExtras(bundle)
            startActivity(saleList)
        }
    }

    private fun setNavigationBar() {

        var navUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(navUser).get().addOnSuccessListener {

            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent

            if (it.toObject<Users>()!!.userType == "Parent"){
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                //sends the ChildID to the parent navbar
                val bundle = intent.extras!!
                val childID = bundle.getString("childID").toString()
                val bundleNavBar = Bundle()
                bundleNavBar.putString("childID", childID)
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)
            } else if (it.toObject<Users>()!!.userType == "Child"){
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
            }
        }
    }

    private fun checkBundle() {
        if (bundle.containsKey("saleName"))
            binding.etName.setText(bundle.getString("saleName"))

        if (bundle.containsKey("saleAmount"))
            binding.etAmount.setText(bundle.getFloat("saleAmount").toInt().toString())

        if (bundle.containsKey("paymentType"))
            binding.dropdownTypeOfPayment.setText(bundle.getString("paymentType"))

        if (bundle.containsKey("depositTo")) {
            binding.dropdownDestination.setText(bundle.getString("depositTo"))

            if (bundle.getString("depositTo") == "Financial Goal") {
                binding.layoutGoal.visibility = View.VISIBLE

                firestore.collection("FinancialActivities").document(bundle.getString("savingActivityID")!!).get().addOnSuccessListener {
                    var goalID = it.toObject<FinancialActivities>()?.financialGoalID
                    firestore.collection("FinancialGoals").document(goalID!!).get().addOnSuccessListener {
                        var goalObject = it.toObject<FinancialGoals>()
                        binding.dropdownGoal.setText(goalObject?.goalName)
                        binding.tvProgressAmount.text = "₱ " + DecimalFormat("#,##0.00").format(goalObject?.currentSavings)!! +
                                " / ₱ " + DecimalFormat("#,##0.00").format(goalObject?.targetAmount!!)
                        binding.pbProgress.progress = ((goalObject?.currentSavings!! / goalObject?.targetAmount!!)*100).toInt()
                    }
                }
            }
        }
    }
}