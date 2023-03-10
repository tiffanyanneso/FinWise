package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityMarkChoreCompletedBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class MarkChoreCompletedActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMarkChoreCompletedBinding

    private var firestore = Firebase.firestore

    private lateinit var earningActivityID:String
    private lateinit var childID:String

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarkChoreCompletedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkUser()

        var bundle = intent.extras!!
        earningActivityID = bundle.getString("earningActivityID").toString()
        childID = bundle.getString("childID").toString()

        getDetails()

        binding.btnCompleted.setOnClickListener {
            val completedEarning = Intent(this, CompletedEarningActivity::class.java)
            firestore.collection("EarningActivities").document(earningActivityID).update("dateCompleted", Timestamp.now())
            val bundle = Bundle()
            bundle.putString("earningActivityID", earningActivityID)
            bundle.putString("childID", childID)
            completedEarning.putExtras(bundle)
            startActivity(completedEarning)
        }
    }

    private fun getDetails() {
        firestore.collection("EarningActivities").document(earningActivityID).get().addOnSuccessListener {
            val earning = it.toObject<ph.edu.dlsu.finwise.model.EarningActivityModel>()
            binding.tvActivity.text = earning?.activityName
            binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(earning?.targetDate!!.toDate()).toString()
            binding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(earning?.amount)
            binding.tvDuration.text = earning?.requiredTime.toString() + " minutes"
            binding.tvSource.text = earning?.depositTo
        }
    }

    private fun checkUser() {
        firestore.collection("ParentUser").document(currentUser).get().addOnSuccessListener {
            //current user is a parent
            if (it.exists())
                binding.btnCompleted.visibility = View.GONE
        }
    }
}