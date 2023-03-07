package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityRecordEarningSaleBinding
import java.text.SimpleDateFormat

class RecordEarningSaleActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRecordEarningSaleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordEarningSaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        var childID = bundle.getString("childID").toString()
        var savingActivityID = bundle.getString("savingActivityID").toString()

        binding.etDate.setOnClickListener {
            showCalendar()
        }

        binding.btnConfirm.setOnClickListener {
            var confirmSale = Intent(this, RecordEarningSaleConfirmationActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("childID", childID)
            sendBundle.putString("savingActivityID", savingActivityID)
            sendBundle.putString("saleName", binding.etName.text.toString())
            sendBundle.putSerializable("saleDate", SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString()))
            sendBundle.putFloat("saleAmount", binding.etAmount.text.toString().toFloat())
            confirmSale.putExtras(sendBundle)
            startActivity(confirmSale)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(R.id.et_date)
        calendar.maxDate = System.currentTimeMillis()

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
    }
}