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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import ph.edu.dlsu.finwise.databinding.ActivityChildNewGoalBinding
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date


class ChildNewGoal : AppCompatActivity() {

    private lateinit var binding : ActivityChildNewGoalBinding
    private var firestore = Firebase.firestore

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChildNewGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        // for the dropdown
        val items = resources.getStringArray(R.array.financial_activity)
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        binding.dropdownActivity.setAdapter(adapter)

        //TODO: how to bring back values to edit text fields
        /*var bundle: Bundle = intent.extras!!
        if (bundle!=null) {
            binding.etGoal.text = bundle.getString("goalName")
            binding.tvActivity.text = bundle.getString("activity")
            binding.etAmount.text = bundle.getString("amount")
            binding.etTargetDate.text = bundle.getString("targetDate")
        }*/


        /*if (currentUserType == "Child") {
            binding.tvFinancialDecisionMakingActivity.visibility = View.GONE
            binding.checkBoxes.visibility = View.GONE
        } else if (currentUserType == "Parent") {
            binding.tvFinancialDecisionMakingActivity.visibility = View.VISIBLE
            binding.checkBoxes.visibility = View.VISIBLE
        }*/

        binding.dropdownActivity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
               var currentSelected = binding.dropdownActivity.text.toString()
                resetCheckbox()
                if (currentSelected == "Buying Items" || currentSelected == "Planning An Event" || currentSelected == "Situational Shopping") {
                    setCheckbox (true, false, false, true, true, false)
                } else if (currentSelected == "Saving For Emergency Funds") {
                    setCheckbox (false, false, true, false, false, false)
                } else if (currentSelected == "Donating to Charity" ) {
                    setCheckbox (false, false, false, true, true, false)
                } else if (currentSelected == "Earning Money") {
                    setCheckbox (false, false, true, false, false, false)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                resetCheckbox() 
            }
        }

        binding.etTargetDate.setOnClickListener{
            showCalendar()
        }

        binding.btnNext.setOnClickListener {
            var goToGoalConfirmation = Intent(this, GoalConfirmationActivity::class.java)
            var bundle = Bundle()

            var goalName =  binding.etGoal.text.toString()
            var activity = binding.dropdownActivity.text.toString()
            var amount = binding.etAmount.text.toString().toFloat()
            var targetDate = binding.etTargetDate.toString()

//                SimpleDateFormat("MM-dd-yyyy").parse((binding.etTargetDate.month+1).toString() + "-" +
//                    binding.etTargetDate.dayOfMonth.toString() + "-" + binding.etTargetDate.year)
            var goalIsForSelf = binding.cbGoalSelf.isChecked

            //see which decision making activities were selected
            var decisionMakingActivities = ArrayList<String>()

            if (binding.cbBudgeting.isChecked)
                decisionMakingActivities.add("Setting a Budget")
            if (binding.cbSaving.isChecked)
                decisionMakingActivities.add("Deciding to Save")
            if (binding.cbSpending.isChecked)
                decisionMakingActivities.add("Deciding to Spend")

            bundle.putString("goalName", goalName)
            bundle.putString("activity", activity)
            bundle.putFloat("amount", amount)
            bundle.putSerializable("targetDate", targetDate)
            bundle.putStringArrayList("decisionActivities", decisionMakingActivities)
            bundle.putBoolean("goalIsForSelf", goalIsForSelf)

            //TODO: reset spinner and date to default value
            binding.etGoal.text?.clear()
            binding.etAmount.text?.clear()

            goToGoalConfirmation.putExtras(bundle)
            goToGoalConfirmation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToGoalConfirmation)
        }

        binding.btnCancel.setOnClickListener {
            val goalList = Intent(this, FinancialActivity::class.java)
            this.startActivity(goalList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(R.id.et_date)

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etTargetDate.setText((mMonth.toString() + 1) + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun resetCheckbox() {
        binding.cbBudgeting.isChecked = false
        binding.cbBudgeting.isClickable = true
        binding.cbSaving.isChecked = false
        binding.cbSaving.isClickable = true
        binding.cbSpending.isChecked = false
        binding.cbSpending.isClickable = true
    }

    private fun setCheckbox (budgetChecked:Boolean, budgetCheckable:Boolean, savingChecked:Boolean, savingCheckable:Boolean,
                                   spendingChecked :Boolean, spendingCheckable:Boolean) {
        binding.cbBudgeting.isChecked = budgetChecked
        binding.cbBudgeting.isClickable = budgetCheckable
        binding.cbSaving.isChecked = savingChecked
        binding.cbSaving.isClickable = savingCheckable
        binding.cbSpending.isChecked = spendingChecked
        binding.cbSpending.isClickable = spendingCheckable
    }
}