package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityPfmconfirmTransactionBinding

class ConfirmTransactionActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPfmconfirmTransactionBinding
    private var firestore = Firebase.firestore

    var bundle: Bundle? = null
    var transactionType : String? =null
    var name : String? =null
    var category : String? =null
    var amount : String? =null
    var goal : String? =null
    var date : String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmconfirmTransactionBinding.inflate(layoutInflater)
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
        bundle = intent.extras!!
        name = bundle!!.getString("transactionName")
        category = bundle!!.getString("category")
        amount = bundle!!.getFloat("amount").toString()
        goal = bundle!!.getString("goal")
        date = bundle!!.getString("date")
        transactionType = bundle!!.getString("transactionType")
        if (transactionType == "income") {
            binding.tvTitle.text = "Confirm Income"
            binding.tvTransactionType.text = "Income Amount"
        } else {
            binding.tvTitle.text = "Confirm Expense"
            binding.tvTransactionType.text = "Expense Amount"
        }

        binding.tvName.text = name
        binding.tvCategory.text = category
        binding.tvAmount.text = "₱$amount"
        binding.tvGoal.text = goal
        binding.tvDate.text = date
    }

    private fun confirm() {
        binding.btnConfirm.setOnClickListener {

            var transaction = hashMapOf(
                //TODO: add childID, createdBy
                "transactionName" to name,
                "transactionType" to transactionType,
                "category" to category,
                "date" to date ,
                "createdBy" to "",
                "amount" to amount?.toFloat(),
                "goal" to goal,
            )

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