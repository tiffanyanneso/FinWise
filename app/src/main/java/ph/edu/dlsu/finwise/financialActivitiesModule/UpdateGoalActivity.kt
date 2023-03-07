package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityChildEditGoalBinding
import ph.edu.dlsu.finwise.databinding.ActivityReviewUpdateGoalBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.SimpleDateFormat
import java.util.*

class UpdateGoalActivity: AppCompatActivity() {
    private lateinit var binding : ActivityReviewUpdateGoalBinding
    private var firestore = Firebase.firestore

    private lateinit var financialGoalID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewUpdateGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        setFields()

        binding.etTargetDate.setOnClickListener {
            showCalendar()
        }

        binding.btnSave.setOnClickListener {
            firestore.collection("FinancialGoals").document(financialGoalID).update("goalName", binding.etGoal.text.toString(),
            "targetAmount", binding.etAmount.text.toString().toFloat(), "targetDate", SimpleDateFormat("MM/dd/yyyy").parse(binding.etTargetDate.text.toString()),
            "status", "For Review").addOnSuccessListener {
                var goalDetails = Intent(this, ViewGoalDetailsTabbedActivity::class.java)
                var sendBundle = Bundle()
                sendBundle.putString("financialGoalID", financialGoalID)
                goalDetails.putExtras(sendBundle)
                startActivity(goalDetails)
            }
        }
    }

    private fun setFields() {
        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var goal = it.toObject<FinancialGoals>()
            binding.etGoal.setText(goal?.goalName.toString())
            binding.dropdownActivity.setText(goal?.financialActivity)
            binding.etAmount.setText(goal?.targetAmount!!.toInt().toString())
            binding.etTargetDate.setText(SimpleDateFormat("MM/dd/yyyy").format(goal?.targetDate!!.toDate()))
        }
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