package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityCompletedEarningBinding

class CompletedEarningActivity : AppCompatActivity() {

    private lateinit var binding:ActivityCompletedEarningBinding

    private var firestore = Firebase.firestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompletedEarningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        var earningActivityID = bundle.getString("earningActivityID").toString()
        var savingActivityID = bundle.getString("savingActivityID").toString()
        var childID = bundle.getString("childID").toString()


        println("print"  + earningActivityID)

        firestore.collection("EarningActivities").document(earningActivityID).get().addOnSuccessListener {
            var earning = it.toObject<ph.edu.dlsu.finwise.model.EarningActivity>()
            binding.tvName.text = earning?.activityName
            binding.tvAmountEarned.text = earning?.amount.toString()
            //TODO: UPDATED GOAL SAVINGS AMOUNT
        }

        firestore.collection("EarningActivities").document(earningActivityID).update("status", "Completed")
        firestore.collection("EarningActivities").document(earningActivityID).update("dateCompleted", Timestamp.now())


        binding.btnFinish.setOnClickListener {
            var earning = Intent(this, EarningActivity::class.java)
            var bundle = Bundle()
            bundle.putString("savingActivityID", savingActivityID)
            bundle.putString("childID", childID)
            earning.putExtras(bundle)
            this.startActivity(earning)
        }
    }
}