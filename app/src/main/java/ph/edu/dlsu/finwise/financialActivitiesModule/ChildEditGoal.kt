package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityChildEditGoalBinding
import ph.edu.dlsu.finwise.databinding.ActivityChildNewGoalBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.SimpleDateFormat
import java.util.*

class ChildEditGoal : AppCompatActivity() {
    private lateinit var binding : ActivityChildEditGoalBinding
    private var firestore = Firebase.firestore

    private lateinit var financialGoalID:String
    private lateinit var targetDate: Date

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChildEditGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        getFinancialGoal()


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
            confirmDeleteGoal()
        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToGoal = Intent(applicationContext, ViewGoalActivity::class.java)
            goToGoal.putExtras(bundle)
            goToGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToGoal)
        }

    }

    private fun getFinancialGoal() {
        firestore.collection("FinancialGoals").document(financialGoalID).get()
            .addOnSuccessListener {
                var goalObject = it.toObject<FinancialGoals>()
                binding.etGoal.setText(goalObject?.goalName.toString())
                binding.etAmount.setText(goalObject?.targetAmount?.toInt().toString())
                binding.containerActivity.hint = goalObject?.financialActivity.toString()
                targetDate = goalObject?.targetDate?.toDate()!!
                binding.etTargetDate.setText(SimpleDateFormat("MM/dd/yyyy").format(goalObject?.targetDate?.toDate()).toString())
            }
    }

    private fun updateGoal() {
        var targetDate = SimpleDateFormat("MM/dd/yyyy").parse(binding.etTargetDate.text.toString())
        firestore.collection("FinancialGoals").document(financialGoalID).update("goalName", binding.etGoal.text.toString(),
            "targetAmount", binding.etAmount.text.toString().toFloat(), "targetDate", targetDate)
        Toast.makeText(this, "Goal has been updated", Toast.LENGTH_LONG).show()
        var goalDetails = Intent(this, ViewGoalActivity::class.java)
        var sendBundle = Bundle()
        sendBundle.putString("financialGoalID", financialGoalID)
        goalDetails.putExtras(sendBundle)
        startActivity(goalDetails)
        finish()
    }

    private fun confirmDeleteGoal() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Goal?")
        builder.setMessage("Are you sure you want to delete your goal?\nSavings will be returned to wallet")

        builder.setPositiveButton(android.R.string.yes) { dialog, which -> deleteGoal()}

        builder.setNegativeButton(android.R.string.no) { dialog, which -> dialog.dismiss() }

        builder.show()
    }

    private fun deleteGoal() {
        //mark financial goal as deleted
        firestore.collection("FinancialGoals").document(financialGoalID).update("status", "Deleted")
        //mark related activities as deleted
        firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", financialGoalID).get().addOnSuccessListener { results ->
            for (activity in results )
                firestore.collection("FinancialActivities").document(activity.id).update("status", "Deleted")
        }
        var goalList = Intent(this, FinancialActivity::class.java)
        this.startActivity(goalList)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(R.id.et_date)
        dialog.show()


        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etTargetDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
    }
}