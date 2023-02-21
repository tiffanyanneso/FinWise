package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivitySpendingBinding
import ph.edu.dlsu.finwise.databinding.ActivityWithdrawBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class WithdrawActivity : AppCompatActivity() {

    private lateinit var binding:ActivityWithdrawBinding
    private var firestore = Firebase.firestore

    private lateinit var financialGoalID:String
    private lateinit var savingActivityID:String

    private lateinit var bundle:Bundle

    private var savedAmount = 0.00F


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFields()

        binding.etDate.setOnClickListener {
            showCalendar()
        }

        binding.btnNext.setOnClickListener {
            var sendBundle = Bundle()
            sendBundle.putString("goalName", binding.tvGoalName.text.toString())
            sendBundle.putString("financialGoalID",financialGoalID)
            //bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
            sendBundle.putFloat("amount", binding.etAmount.text.toString().toFloat())
            sendBundle.putSerializable("date", SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString()))
            sendBundle.putString("savingActivityID", savingActivityID)
            sendBundle.putFloat("savedAmount", savedAmount)


            var confirmWithdraw = Intent (this, ConfirmWithdraw::class.java)
            confirmWithdraw.putExtras(sendBundle)
            confirmWithdraw.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(confirmWithdraw)
        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            var bundle = Bundle()
            bundle.putString("financialGoalID",financialGoalID)

            val goToGoal = Intent(applicationContext, ViewGoalActivity::class.java)
            goToGoal.putExtras(bundle)
            goToGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToGoal)
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

    private fun setFields() {
        bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()
        savedAmount = bundle.getFloat("savedAmount")
        binding.pbProgress.progress = bundle.getInt("progress")


        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var financialGoal = it.toObject<FinancialGoals>()
            binding.tvGoalName.text = financialGoal?.goalName
            binding.tvProgressAmount.text = "₱ " + DecimalFormat("###0.00").format(bundle.getFloat("savedAmount")) +
                    " / ₱ " + DecimalFormat("###0.00").format(financialGoal?.targetAmount)
            binding.tvSavings.text = "You currently have ₱ ${DecimalFormat("###0.00").format(savedAmount)} in your savings"
        }
    }
}