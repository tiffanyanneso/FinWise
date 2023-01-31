package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityPfmconfirmTransactionBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import kotlin.math.abs

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
        name = bundle!!.getString("transactionName")
        category = bundle!!.getString("category")
        amount = bundle!!.getFloat("amount").toString()
        goal = bundle!!.getString("goal")

        date = bundle!!.getSerializable("date").toString()

        transactionType = bundle!!.getString("transactionType")
        if (transactionType == "income") {
            binding.tvTitle.text = "Confirm Income"
            binding.tvTransactionType.text = "Income Amount"
        } else {
            binding.tvTitle.text = "Confirm Expense"
            binding.tvTransactionType.text = "Expense Amount"
        }
        val dec = DecimalFormat("#,###.00")
        var textAmount = dec.format(bundle!!.getFloat("amount"))
        binding.tvName.text = name
        binding.tvCategory.text = category
        binding.tvAmount.text = "₱$textAmount"
        //binding.tvGoal.text = goal
        binding.tvDate.text = date

    }

    private fun confirm() {
        binding.btnConfirm.setOnClickListener {

            var transaction = hashMapOf(
                //TODO: add childID, createdBy
                "transactionName" to name,
                "transactionType" to transactionType,
                "category" to category,
                "date" to bundle!!.getSerializable("date"),
                "createdBy" to "",
                "amount" to amount?.toFloat(),
                "goal" to goal,
            )
            adjustUserBalance()
            // change collection
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
        firestore.collection("ChildWallet").whereEqualTo("childID", "JoCGIUSVMWTQ2IB7Rf41ropAv3S2")
            .get().addOnSuccessListener { documents ->
                lateinit var id: String
                for (document in documents) {
                    id = document.id
                }
                var adjustedBalance = amount?.toDouble()
                if (transactionType == "expense")
                    adjustedBalance = -abs(adjustedBalance!!)

                firestore.collection("ChildWallet").document(id)
                    .update("currentBalance", FieldValue.increment(adjustedBalance!!))
            }
        }

    }
