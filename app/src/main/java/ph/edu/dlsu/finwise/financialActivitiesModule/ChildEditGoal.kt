package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityChildEditGoalBinding
import ph.edu.dlsu.finwise.databinding.ActivityChildNewGoalBinding

class ChildEditGoal : AppCompatActivity() {
    private lateinit var binding : ActivityChildEditGoalBinding
    private var firestore = Firebase.firestore

    private lateinit var financialGoalID:String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChildEditGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        getFinancialGoal()

        /*if (currentUserType == "Child") {
            binding.tvFinancialDecisionMakingActivity.visibility = View.GONE
            binding.checkBoxes.visibility = View.GONE
        } else if (currentUserType == "Parent") {
            binding.tvFinancialDecisionMakingActivity.visibility = View.VISIBLE
            binding.checkBoxes.visibility = View.VISIBLE
        }*/

        // for the dropdown
        val items = resources.getStringArray(R.array.financial_activity)
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        binding.dropdownActivity.setAdapter(adapter)

        binding.etTargetDate.setOnClickListener{
            showCalendar()
        }

        binding.btnSave.setOnClickListener {
            updateGoal()
        }

        binding.btnDelete.setOnClickListener {
            deleteGoal()
        }

    }

    private fun getFinancialGoal() {
        //TODO: update when goal list is done
        /*val goal:String = id of goal
        firestore.collection("FinancialGoals").document(goal).get().addOnSuccessListener {
            var goal = it.toObject<Company>()
            if (goal?.goalName != null)
                binding.etGoal.setText(goal?.goalName)
            if (goal?.amount != null)
                binding.etAmount.setText(goal?.amount)
            if (goal?.targetDate != null)
                //change how to set date
                binding.etTargetDate.setText(goal?.about)

            if (goal?.financialActivity != null) {
                var financialActivityArray = getResources().getStringArray(R.array.financial_activity)
                var finActivityIndex: Int = 0

                for (i in financialActivityArray.indices)
                    if (financialActivityArray[i].toString() == goal?.financialActivity.toString())
                        finActivityIndex = i

                binding.spinnerActivity.setSelection(finActivityIndex)
            }
        }*/
    }

    private fun updateGoal() {
        //firestore.collection("FinancialGoals").document(financialGoalID).update()
    }

    private fun deleteGoal() {
        firestore.collection("FinancialGoals").document(financialGoalID).update("status", "Deleted")
        var goalList = Intent(this, FinancialActivity::class.java)
        this.startActivity(goalList)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(R.id.et_date)

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etTargetDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
    }
}