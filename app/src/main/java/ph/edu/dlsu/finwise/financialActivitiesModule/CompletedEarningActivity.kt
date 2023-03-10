package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityCompletedEarningBinding
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningActivity
import java.text.DecimalFormat

class CompletedEarningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompletedEarningBinding

    private var firestore = Firebase.firestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompletedEarningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        var earningActivityID = bundle.getString("earningActivityID").toString()
        var childID = bundle.getString("childID").toString()



        firestore.collection("EarningActivities").document(earningActivityID).get().addOnSuccessListener {
            val earning = it.toObject<ph.edu.dlsu.finwise.model.EarningActivityModel>()
            binding.tvName.text = earning?.activityName
            binding.tvAmountEarned.text = "₱ " + DecimalFormat("#,##0.00").format(earning?.amount)
        }

        firestore.collection("EarningActivities").document(earningActivityID).update("status", "Pending")
       // firestore.collection("EarningActivities").document(earningActivityID).update("dateCompleted", Timestamp.now())


        binding.btnFinish.setOnClickListener {
            var earning = Intent(this, EarningActivity::class.java)
            var bundle = Bundle()
            bundle.putString("childID", childID)
            earning.putExtras(bundle)
            this.startActivity(earning)
        }
    }
}