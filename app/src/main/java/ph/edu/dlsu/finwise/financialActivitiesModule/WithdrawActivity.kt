package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivitySpendingBinding
import ph.edu.dlsu.finwise.databinding.ActivityWithdrawBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.SimpleDateFormat

class WithdrawActivity : AppCompatActivity() {

    private lateinit var binding:ActivityWithdrawBinding
    private var firestore = Firebase.firestore

    private lateinit var goalID:String
    private lateinit var decisionMakingActivityID:String

    private lateinit var dataBundle:Bundle

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataBundle = intent.extras!!
        setData()

        binding.etDate.setOnClickListener {
            showCalendar()
        }

        binding.btnNext.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("goalName", binding.tvGoalName.text.toString())
            bundle.putString("goalID",goalID)
            //bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
            bundle.putFloat("amount", binding.etAmount.text.toString().toFloat())
            bundle.putSerializable("date", SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString()))
            bundle.putString("savingActivityID", dataBundle.getString("savingActivityID"))


            var confirmWithdraw = Intent (this, ConfirmWithdraw::class.java)
            confirmWithdraw.putExtras(bundle)
            confirmWithdraw.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(confirmWithdraw)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(R.id.et_date)

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setData() {
        goalID = dataBundle.getString("goalID").toString()
        decisionMakingActivityID = dataBundle.getString("decisionMakingActivityID").toString()

        firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener {
            var financialGoal = it.toObject<FinancialGoals>()
            binding.tvGoalName.text = financialGoal?.goalName
            //binding.tvProgressAmount.text = "₱ " + bundle.getFloat("currentAmount") + " / ₱ " + bundle.getFloat("targetAmount")
            //binding.pbProgress.progress = bundle.getInt("progress")
        }
    }
}