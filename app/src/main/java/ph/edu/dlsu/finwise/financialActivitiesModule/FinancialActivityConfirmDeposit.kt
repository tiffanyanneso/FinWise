package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityFinancialConfirmDepositBinding
import ph.edu.dlsu.finwise.databinding.ActivityPfmconfirmDepositBinding
import java.text.SimpleDateFormat
import java.util.*

class FinancialActivityConfirmDeposit : AppCompatActivity() {

    private lateinit var binding : ActivityFinancialConfirmDepositBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context

    //Time
    val formatter = SimpleDateFormat("MM/dd/yyyy")
    val time = Calendar.getInstance().time
    val current = formatter.format(time)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialConfirmDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        var bundle: Bundle = intent.extras!!
        binding.tvDate.text = current
        binding.tvAmount.text = "₱ " + bundle.getFloat("amount").toString()
        binding.tvGoal.text= bundle.getString("goalName")

        binding.btnConfirm.setOnClickListener {
            var goalName = bundle.getString("goalName").toString()
            var decisionActivityID = bundle.getString("decisionMakingActivityID").toString()

            var transaction = hashMapOf(
                "transactionName" to goalName,
                "transactionType" to "Deposit",
                //"category" to "Deposit",
                "date" to current,
                "createdBy" to "",
                "amount" to bundle.getFloat("amount"),
                "decisionMakingActivityID" to decisionActivityID
            )
            firestore.collection("Transactions").add(transaction).addOnSuccessListener {
                var bundle = Bundle()
                bundle.putString("decisionMakingActivityID", decisionActivityID)
                var viewDeposits = Intent(this, SavingViewDepositActivity::class.java)
                viewDeposits.putExtras(bundle)
                viewDeposits.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(viewDeposits)

            }
        }
    }
}