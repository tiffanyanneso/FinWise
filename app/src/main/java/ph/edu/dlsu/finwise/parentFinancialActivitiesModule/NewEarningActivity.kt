package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityNewEarningBinding
import java.text.SimpleDateFormat

class NewEarningActivity : AppCompatActivity() {

    private lateinit var binding:ActivityNewEarningBinding

    private lateinit var savingActivityID:String
    private lateinit var childID:String

    private var firestore = Firebase.firestore

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewEarningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        savingActivityID = bundle.getString("savingActivityID").toString()
        childID = bundle.getString("childID").toString()

        binding.etDate.setOnClickListener{
            showCalendar()
        }

        binding.btnConfirm.setOnClickListener {
            var earningActivity = hashMapOf(
                 "activityName" to binding.etName.text.toString(),
                 "targetDate" to SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString()),
                 "requiredTime" to binding.etDuration.text.toString().toInt(),
                 "amount" to binding.etAmount.text.toString().toFloat(),
                 "childID" to childID,
                "savingActivityID" to savingActivityID,
                "status" to "Ongoing"
            )
            firestore.collection("EarningActivities").add(earningActivity).addOnSuccessListener {
                var earning = Intent(this, EarningActivity::class.java)
                var sendBundle = Bundle()
                sendBundle.putString("childID", childID)
                sendBundle.putString("savingActivityID", savingActivityID)
                earning.putExtras(sendBundle)
                startActivity(earning)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(ph.edu.dlsu.finwise.R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(ph.edu.dlsu.finwise.R.id.et_date)
        calendar.minDate = System.currentTimeMillis()

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
    }
}