package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityIncomeBinding
import ph.edu.dlsu.finwise.model.EarningActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.CompletedEarningActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class IncomeActivity : AppCompatActivity() {

    private lateinit var binding:ActivityIncomeBinding

    private var firestore = Firebase.firestore

    private lateinit var earningActivityID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        earningActivityID = bundle.getString("earningActivityID").toString()

       getInfo()

        binding.btnCompleted.setOnClickListener {
            completeEarningActivity()
        }
    }

    private fun getInfo() {
        firestore.collection("EarningActivities").document(earningActivityID).get().addOnSuccessListener {
            var earningActivity = it.toObject<EarningActivity>()
            binding.tvActivity.text = earningActivity?.activityName
            binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").parse(earningActivity?.targetDate!!.toDate().toString()).toString()
            binding.tvDuration.text = earningActivity?.requiredTime.toString() + " minutes"
            binding.tvAmount.text = DecimalFormat("#,##0.00").format(earningActivity?.amount)
        }
    }

    private fun completeEarningActivity() {
        var completeEarning = Intent(this, CompletedEarningActivity::class.java)
        var bundle = Bundle()
        bundle.putString("earningActivityID", earningActivityID)
        completeEarning.putExtras(bundle)
        startActivity(completeEarning)
    }
}