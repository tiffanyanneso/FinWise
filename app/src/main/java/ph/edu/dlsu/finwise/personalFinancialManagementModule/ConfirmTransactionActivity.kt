package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityPfmconfirmTransactionBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.math.abs

class ConfirmTransactionActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPfmconfirmTransactionBinding
    private var firestore = Firebase.firestore
    var bundle: Bundle? = null
    lateinit var transactionType : String
    lateinit var name : String
    var balance = 0.00f
    var amount = 0.00f
    lateinit var category : String
    lateinit var date : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmconfirmTransactionBinding.inflate(layoutInflater)
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
        bundle = intent.extras!!
        name = bundle!!.getString("transactionName").toString()
        category = bundle!!.getString("category").toString()
        amount = bundle!!.getFloat("amount")
        balance = bundle!!.getFloat("balance")
        date = bundle!!.getSerializable("date").toString()

        transactionType = bundle!!.getString("transactionType").toString()
        if (transactionType == "Income") {
            binding.tvTransactionType.text = "Income"
            binding.tvWalletBalance.text = "₱ " +
                    DecimalFormat("#,##0.00")
                        .format(balance + amount)
        } else {
            binding.tvTransactionType.text = "Expense"
            binding.tvWalletBalance.text = "₱ " +
                    DecimalFormat("#,##0.00")
                        .format(balance - amount)
        }

        if (transactionType == "Expense") {
            binding.tvWalletBalance.text = "₱ " +
                    DecimalFormat("#,##0.00")
                        .format(balance - amount)
        } else if (transactionType == "Income") {
            binding.tvWalletBalance.text = "₱ " +
                    DecimalFormat("#,##0.00")
                        .format(balance + amount)
        }


        binding.tvName.text = name
        binding.tvCategory.text = category
        binding.tvAmount.text = "₱$amount"
        //binding.tvGoal.text = goal
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val dateSerializable = bundle!!.getSerializable("date")
        val dateText = formatter.format(dateSerializable).toString()
        binding.tvDate.text = dateText


    }

    private fun confirm() {
        binding.btnConfirm.setOnClickListener {
            //TODO: add the createdBy
            val transaction = hashMapOf(
                //TODO: add childID, createdBy
                "transactionName" to name,
                "transactionType" to transactionType,
                "category" to category,
                "date" to bundle!!.getSerializable("date"),
                "createdBy" to "",
                "amount" to amount
            )
            adjustUserBalance()
            // TODO: Change where transaction is added
            firestore.collection("Transactions").add(transaction).addOnSuccessListener {
                val goToPFM = Intent(this, PersonalFinancialManagementActivity::class.java)
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
            .get().addOnSuccessListener { document ->
               val id = document.documents[0].id
                var adjustedBalance = amount.toDouble()
                if (transactionType == "Expense")
                    adjustedBalance = -abs(adjustedBalance)

                firestore.collection("ChildWallet").document(id)
                    .update("currentBalance", FieldValue.increment(adjustedBalance))
            }
        }

    }
