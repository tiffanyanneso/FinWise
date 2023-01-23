package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityPfmconfirmDepositBinding

class ConfirmDepositActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmconfirmDepositBinding
    private var firestore = Firebase.firestore

    var bundle: Bundle? = null
    var amount : String? =null
    var goal : String? =null
    var date : String? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmconfirmDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setText()
        confirm()
        cancel()
    }

    private fun cancel() {
        binding.btnCancel.setOnClickListener {
            val goBack = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goBack)
        }
    }

    private fun setText() {
        var bundle: Bundle = intent.extras!!
        amount = bundle.getFloat("amount").toString()
        goal = bundle.getString("goal")
        date = bundle.getString("date")
        binding.tvAmount.text = amount
        binding.tvGoal.text = goal

    }

    private fun confirm() {
        binding.btnConfirm.setOnClickListener {
        val name = "Deposit to Goal"
            var transaction = hashMapOf(
                //TODO: add childID, createdBy
                "transactionName" to name,
                "transactionType" to "goal",
                "category" to "Deposit",
                "date" to date ,
                "createdBy" to "",
                "amount" to amount?.toFloat(),
                "goal" to goal,
            )
            Toast.makeText(this, name + date + amount + goal, Toast.LENGTH_SHORT).show()

            firestore.collection("Transactions").add(transaction).addOnSuccessListener {
                Toast.makeText(this, "Goal added", Toast.LENGTH_SHORT).show()
                var goToPFM = Intent(this, PersonalFinancialManagementActivity::class.java)
                goToPFM.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(goToPFM)
            }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add transaction", Toast.LENGTH_SHORT).show()
                }
        }
    }

}