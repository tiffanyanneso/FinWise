package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityPfmconfirmDepositBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class ConfirmDepositActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmconfirmDepositBinding
    private var firestore = Firebase.firestore

    var bundle: Bundle? = null
    var amount = 0.00f
    var balance = 0.00f
    var goal : String? =null
    var date : Date? =null
    private var childID  = FirebaseAuth.getInstance().currentUser!!.uid



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
        loadBackButton()
    }
    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun cancel() {
        binding.btnCancel.setOnClickListener {
            val goBack = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goBack)
        }
    }

    private fun setText() {
        bundle = intent.extras!!
        balance = bundle!!.getFloat("balance")
        amount = bundle!!.getFloat("amount")
        goal = bundle!!.getString("goal")
        date = bundle!!.getSerializable("date") as Date


        binding.tvAmount.text = "₱${DecimalFormat("#,##0.00").format(bundle!!.getFloat("amount"))}"
        binding.tvGoal.text = goal.toString()
        binding.tvWalletBalance.text = "₱${DecimalFormat("#,##0.00").format(balance - amount)}"

        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val dateSerializable = bundle!!.getSerializable("date")
        val dateText = formatter.format(dateSerializable).toString()
        binding.tvDate.text = dateText
         getGoalAmount()
    }


    private fun getGoalAmount() {
        firestore.collection("FinancialGoals").whereEqualTo("goalName", goal)
            .get().addOnSuccessListener { document ->
                val financialGoal = document.documents[0].toObject<FinancialGoals>()
                val goalAmount = financialGoal?.targetAmount
                if (goalAmount != null) {
                    binding.tvUpdatedGoalAmount.text = "₱ " +
                            DecimalFormat("#,##0.00")
                                .format(goalAmount - amount)
                }
            }
    }

    private fun confirm() {
        binding.btnConfirm.setOnClickListener {
            val childID  = FirebaseAuth.getInstance().currentUser!!.uid

            val goalName = "Deposit to '$goal'"
            val transaction = hashMapOf(
                //TODO: add childID, createdBy
                "transactionName" to goalName,
                "transactionType" to "Deposit",
                "date" to date ,
                "category" to "Goal",
                "createdBy" to childID,
                "amount" to amount,
                "goal" to goal,
            )
            adjustUserBalance()
            // TODO: Change where transaction is added
            firestore.collection("Transactions").add(transaction).addOnSuccessListener {
                Toast.makeText(this, "Goal added", Toast.LENGTH_SHORT).show()
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
        firestore.collection("ChildWallet").whereEqualTo("childID", childID)
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