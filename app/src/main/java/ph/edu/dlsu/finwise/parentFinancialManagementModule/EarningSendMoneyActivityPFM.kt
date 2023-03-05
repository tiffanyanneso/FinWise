package ph.edu.dlsu.finwise.parentFinancialManagementModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityEarningSendMoneyPfmBinding
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalBinding
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class EarningSendMoneyActivityPFM : AppCompatActivity() {
    private lateinit var binding: ActivityEarningSendMoneyPfmBinding

    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private lateinit var earningActivityID:String
    private lateinit var childID:String

    private var amount = 0.00F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningSendMoneyPfmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        earningActivityID = bundle.getString("earningActivityID").toString()
        childID = bundle.getString("childID").toString()

        checkUser()

        firestore.collection("EarningActivities").document(earningActivityID).get().addOnSuccessListener {
            var earning = it.toObject<EarningActivityModel>()
            binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(earning?.targetDate!!.toDate())
            binding.tvFinishDate.text = SimpleDateFormat("MM/dd/yyyy").format(earning?.dateCompleted!!.toDate())
            binding.tvActivityName.text = earning?.activityName
            binding.tvDuration.text = earning?.requiredTime.toString() + " Minutes"
            binding.tvAmountEarned.text = "₱ " + DecimalFormat("#,##0.00").format(earning?.amount)
            amount = earning?.amount!!.toFloat()
            binding.tvStatus.text = earning?.status
        }

        binding.btnSendMoney.setOnClickListener {
            firestore.collection("EarningActivities").document(earningActivityID).update("status", "Completed")
            firestore.collection("EarningActivities").document(earningActivityID).update("dateCompleted", Timestamp.now())
            makeTransactions()
        }
    }

    private fun makeTransactions() {
        //TODO ADD TRANSACTION ON PARENT SIDE SENDING MONEY
        var income = hashMapOf(
            "userID" to childID,
            "transactionName" to binding.tvActivityName.text.toString(),
            "transactionType" to "Income",
            "category" to "Rewards",
            "date" to Timestamp.now(),
            "amount" to amount)
        firestore.collection("Transactions").add(income).addOnSuccessListener {
            var earning = Intent(this, EarningActivityPFM::class.java)
            var bundle = Bundle()
            bundle.putString("childID", childID)
            earning.putExtras(bundle)
            this.startActivity(earning)
        }
    }


    private fun checkUser() {
        firestore.collection("ChildUser").document(currentUser).get().addOnSuccessListener {
            //user is a child
            if (it.exists())
                binding.btnSendMoney.visibility = View.GONE
        }
    }
}