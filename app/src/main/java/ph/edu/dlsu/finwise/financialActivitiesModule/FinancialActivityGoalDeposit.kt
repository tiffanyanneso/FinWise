package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialGoalDepositBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FinancialActivityGoalDeposit : AppCompatActivity() {

    private lateinit var binding : ActivityFinancialGoalDepositBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialGoalDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        var dataBundle: Bundle = intent.extras!!
        var goalID = dataBundle.getString("goalID").toString()
        var savingActivityID = dataBundle.getString("savingActivityID")
//        var decisionActivityID = dataBundle.getString("decisionMakingActivityID").toString()
//
        firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener {
            var financialGoal = it.toObject<FinancialGoals>()
            binding.tvGoalName.text = financialGoal?.goalName
            //binding.tvProgressAmount.text = "₱ " + dataBundle.getFloat("currentAmount") + " / ₱ " + dataBundle.getFloat("targetAmount")
            //binding.pbProgress.progress = dataBundle.getInt("progress")
        }

        binding.etDate.setOnClickListener {
            showCalendar()
        }


        binding.btnNext.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("goalName", binding.tvGoalName.text.toString())
            bundle.putString("goalID",goalID)
            //bundle.putString("decisionMakingActivityID", decisionActivityID)
            bundle.putFloat("amount", binding.etAmount.text.toString().toFloat())
            //bundle.putString("source", "DirectGoalDeposit")
            bundle.putSerializable("date", SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString()))
            bundle.putString("savingActivityID", savingActivityID)


            var goToDepositConfirmation = Intent(context, FinancialActivityConfirmDeposit::class.java)
            goToDepositConfirmation.putExtras(bundle)
            goToDepositConfirmation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(goToDepositConfirmation)
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
}