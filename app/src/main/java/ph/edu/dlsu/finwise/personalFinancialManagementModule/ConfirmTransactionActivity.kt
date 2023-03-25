package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.common.models.TotalAmount
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMaya
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMayaResult
import com.paymaya.sdk.android.paywithpaymaya.SinglePaymentResult
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import org.json.JSONObject
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityPfmconfirmTransactionBinding
import java.math.BigDecimal
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
    lateinit var paymentType : String
    lateinit var date : String
    var merchant = ""
    var phone = ""
    val childID  = FirebaseAuth.getInstance().currentUser!!.uid
    // For Pay With PayMaya API: This is instantiating the Pay With PayMaya API.
    val payWithPayMayaClient = PayWithPayMaya.newBuilder()
        .clientPublicKey("pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(LogLevel.ERROR)
        .build()


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
        name = bundle!!.getString("transactionName").toString()
        category = bundle!!.getString("category").toString()
        paymentType = bundle!!.getString("paymentType").toString()
        phone = bundle?.getString("phone").toString()
        merchant = bundle?.getString("merchant").toString()
        amount = bundle!!.getFloat("amount")
        balance = bundle!!.getFloat("balance")
        date = bundle!!.getSerializable("date").toString()
        transactionType = bundle!!.getString("transactionType").toString()

        checkIfMaya()

        if (transactionType == "Income") {
            binding.tvTransactionType.text = "Income"
            binding.tvWalletBalance.text = "₱ " + DecimalFormat("#,##0.00").format(balance + amount)
        } else {
            binding.tvTransactionType.text = "Expense"
            binding.tvWalletBalance.text = "₱ " + DecimalFormat("#,##0.00").format(balance - amount)
        }

        if (transactionType == "Expense") {
            binding.tvWalletBalance.text = "₱ " + DecimalFormat("#,##0.00").format(balance - amount)
        } else if (transactionType == "Income") {
            binding.tvWalletBalance.text = "₱ " + DecimalFormat("#,##0.00").format(balance + amount)
        }


        binding.tvName.text = name
        binding.tvCategory.text = category
        binding.tvPaymentType.text = paymentType
        binding.tvAmount.text = "₱${DecimalFormat("#,##0.00").format(amount)}"
        //binding.tvGoal.text = goal
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val dateSerializable = bundle!!.getSerializable("date")
        val dateText = formatter.format(dateSerializable).toString()
        binding.tvDate.text = dateText

    }

    private fun checkIfMaya() {
        if (paymentType == "Maya" && transactionType == "Expense") {
            binding.llPhone.visibility = View.VISIBLE
            binding.tvPhoneNumber.text = phone
            binding.llMerchant.visibility = View.VISIBLE
            binding.tvMerchant.text = merchant
        }
    }

    private fun confirm() {
        binding.btnConfirm.setOnClickListener {
            if (paymentType == "Maya" && transactionType == "Expense")
                payWithPayMayaClient.startSinglePaymentActivityForResult(this, buildSinglePaymentRequest())
            else if (paymentType == "Cash") {
                adjustUserBalance()
            }
        }
    }

    // For Pay With PayMaya API: Once A Pay With PayMaya Activity finishes, this function receives the results.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        payWithPayMayaClient.onActivityResult(requestCode, resultCode, data)?.let {
            processCheckoutResult(it)
        }
    }

    private fun processCheckoutResult(result: PayWithPayMayaResult) {
        when (result) {
            is SinglePaymentResult.Success -> {
                val resultID: String = result.paymentId
                Toast.makeText(this, "Payment Successful. Payment ID: $resultID", Toast.LENGTH_LONG)
                    .show()
                Log.d("PayMayaa", resultID)
                //adjustUserBalance()
                adjustUserBalance()
            }

            is SinglePaymentResult.Cancel -> {
                val resultID: String? = result.paymentId

                Toast.makeText(this, "Payment Cancelled. Payment ID: $resultID", Toast.LENGTH_LONG)
                    .show()
                if (resultID != null) {
                    Log.d("PayMayaa", resultID)
                }
            }

            is SinglePaymentResult.Failure -> {
                val resultID: String? = result.paymentId
                val message =
                    "Failure, checkoutId: ${resultID}, exception: ${result.exception}"
                if (result.exception is BadRequestException) {
                    Log.d("PayMayaa", (result.exception as BadRequestException).error.toString())
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                Log.d("PayMayaa", message)

            }
            else -> {}
        }
    }


    private fun buildSinglePaymentRequest(): SinglePaymentRequest {
        val amount = BigDecimal(amount.toDouble())
        val currency = "PHP"
        val redirectUrl = RedirectUrl(
            success = "http://success.com",
            failure = "http://failure.com",
            cancel = "http://cancel.com"
        )
        /* val metadata = JSONObject()
         metadata.put("customerName", "John Doe")
         metadata.put("customerEmail", "johndoe@example.com")
 */
        return SinglePaymentRequest(
            TotalAmount(amount, currency),
            "123456789",
            redirectUrl,
            null as JSONObject?
        )
    }


    private fun adjustUserBalance() {
        firestore.collection("ChildWallet").whereEqualTo("childID", childID)
            .whereEqualTo("type", paymentType)
            .get().addOnSuccessListener   { documents ->

                if (!documents.isEmpty) {
                    var adjustedBalance = amount.toDouble()
                    if (transactionType == "Expense")
                        adjustedBalance = -abs(adjustedBalance)

                    val id = documents.documents[0].id
                    updateChildWallet(id, adjustedBalance)
                } else {
                    createWallet()
                }

            }.continueWith { addTransaction() }
        }

    private fun updateChildWallet(id: String, adjustedBalance: Double) {
        val updates = hashMapOf(
            "lastUpdated" to com.google.firebase.Timestamp.now(),
            "currentBalance" to FieldValue.increment(adjustedBalance)
        )
        firestore.collection("ChildWallet").document(id)
            .update(updates)
    }

    private fun addTransaction() {
        val transaction = hashMapOf(
            "transactionName" to name,
            "transactionType" to transactionType,
            "category" to category,
            "date" to bundle!!.getSerializable("date"),
            "userID" to childID,
            "amount" to amount,
            "paymentType" to paymentType,
            "merchant" to merchant,
            "phoneNumber" to phone
        )

        firestore.collection("Transactions").add(transaction).addOnSuccessListener {
            Log.d("xcvvvv", "updateChildWallet: loobfirestore"+ it.id)
            goToPFM()
        }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add transaction", Toast.LENGTH_SHORT).show()
            }
    }

    private fun goToPFM() {
        Toast.makeText(this, "Transaction Added", Toast.LENGTH_SHORT).show()
        val goToPFM = Intent(this, PersonalFinancialManagementActivity::class.java)
        goToPFM.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(goToPFM)
    }

    private fun createWallet() {
        val childWallet = hashMapOf(
            "childID" to childID,
            "currentBalance" to amount,
            "lastUpdated" to com.google.firebase.Timestamp.now(),
            "type" to paymentType
        )

        firestore.collection("ChildWallet").add(childWallet).addOnSuccessListener {
            val goToPFM = Intent(this, PersonalFinancialManagementActivity::class.java)
            goToPFM.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(goToPFM)
        }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add transaction", Toast.LENGTH_SHORT).show()
            }
    }

}
