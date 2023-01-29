package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialConfirmDepositBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class FinancialActivityConfirmDeposit : AppCompatActivity() {

    private lateinit var binding : ActivityFinancialConfirmDepositBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context

    //Time
    val formatter = SimpleDateFormat("MM/dd/yyyy")
    val time = Calendar.getInstance().time
    val current = formatter.format(time)

    var amount : String? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialConfirmDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        val bundle: Bundle = intent.extras!!
        binding.tvDate.text = current
        amount = bundle.getFloat("amount").toString()
        binding.tvAmount.text = "₱ " + bundle.getFloat("amount").toString()
        binding.tvGoal.text= bundle.getString("goalName")

        binding.btnConfirm.setOnClickListener {
            val goalName = bundle.getString("goalName").toString()
            val decisionActivityID = bundle.getString("decisionMakingActivityID").toString()

            val transaction = hashMapOf(
                "transactionName" to goalName,
                "transactionType" to "Deposit",
                //"category" to "Deposit",
                "date" to current,
                "createdBy" to "",
                "amount" to bundle.getFloat("amount"),
                "decisionMakingActivityID" to decisionActivityID
            )

            //adjustUserBalance()

            firestore.collection("Transactions").add(transaction).addOnSuccessListener {
                val bundle = Bundle()
                bundle.putString("decisionMakingActivityID", decisionActivityID)
                val viewDeposits = Intent(this, SavingActivity::class.java)
                viewDeposits.putExtras(bundle)
                viewDeposits.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(viewDeposits)
                finish()
            }
        }
    }

    private fun adjustUserBalance() {
        //TODO: Change user based on who is logged in
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        firestore.collection("ChildWallet").whereEqualTo("childID", "JoCGIUSVMWTQ2IB7Rf41ropAv3S2")
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