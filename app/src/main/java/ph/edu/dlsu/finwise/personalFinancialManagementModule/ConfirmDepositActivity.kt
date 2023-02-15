package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityPfmconfirmDepositBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class ConfirmDepositActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmconfirmDepositBinding
    private var firestore = Firebase.firestore

    var bundle: Bundle? = null
    var amount : String? =null
    var goal : String? =null
    var date : Date? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmconfirmDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)

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
        val bundle: Bundle = intent.extras!!
        amount = bundle.getFloat("amount").toString()
        goal = bundle.getString("goal")
        date = bundle.getSerializable("date") as Date
        val dec = DecimalFormat("#,##0.00")
        val textAmount = dec.format(bundle.getFloat("amount"))
        binding.tvAmount.text = "₱$textAmount"
        binding.tvGoal.text = goal
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val dateSerializable = bundle.getSerializable("date")
        val dateText = formatter.format(dateSerializable).toString()
        binding.tvDate.text = dateText

    }

    private fun confirm() {
        binding.btnConfirm.setOnClickListener {

            var goalName = "Deposit to '$goal'"
            var transaction = hashMapOf(
                //TODO: add childID, createdBy
                "transactionName" to goalName,
                "transactionType" to "Deposit",
                "date" to date ,
                "category" to "Goal",
                "createdBy" to "",
                "amount" to amount?.toFloat(),
                "goal" to goal,
            )
            adjustUserBalance()
            // TODO: Change where transaction is added
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

    private fun adjustUserBalance() {
        //TODO: Change user based on who is logged in
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        firestore.collection("ChildWallet").whereEqualTo("childID", "eWZNOIb9qEf8kVNdvdRzKt4AYrA2")
            .get().addOnSuccessListener { documents ->
                lateinit var id: String
                for (document in documents) {
                    id = document.id
                }
                var adjustedBalance = amount?.toDouble()
                    adjustedBalance = -abs(adjustedBalance!!)
                Toast.makeText(this, adjustedBalance.toString(), Toast.LENGTH_SHORT).show()

                firestore.collection("ChildWallet").document(id)
                    .update("currentBalance", FieldValue.increment(adjustedBalance))
            }
    }


}