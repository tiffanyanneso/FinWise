package ph.edu.dlsu.finwise.personalFinancialManagementModule.mayaAPI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import ph.edu.dlsu.finwise.databinding.ActivityMayaQrConfirmPaymentBinding
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.math.abs

class MayaConfirmPayment : AppCompatActivity() {
    private var firestore = Firebase.firestore
    var bundle: Bundle? = null
    lateinit var name : String
    lateinit var category : String
    lateinit var amount : String
    lateinit var merchant : String
    var balance = 0.00f
    lateinit var phone : String
    lateinit var date : String


    private lateinit var binding: ActivityMayaQrConfirmPaymentBinding

    // For Pay With PayMaya API: This is instantiating the Pay With PayMaya API.
    private val payWithPayMayaClient = PayWithPayMaya.newBuilder()
        .clientPublicKey("pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(LogLevel.ERROR)
        .build()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMayaQrConfirmPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializes the navbar
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)
        setText()
        payMaya()
        cancel()
    }

    private fun setText() {
        bundle = intent.extras!!
        name = bundle!!.getString("transactionName").toString()
        category = bundle!!.getString("category").toString()
        merchant = bundle!!.getString("merchant").toString()
        amount = bundle!!.getFloat("amount").toString()
        balance = bundle!!.getFloat("balance")
        phone = bundle!!.getString("phone").toString()
        date = bundle!!.getSerializable("date").toString()

        val dec = DecimalFormat("#,###.00")
        val textAmount = dec.format(bundle!!.getFloat("amount"))

        binding.tvExpenseAmount.text = "₱$textAmount"
        binding.tvName.text = name
        binding.tvCategory.text = category
        binding.tvPhoneNumber.text = phone
        binding.tvMerchant.text = merchant
        //binding.tvGoal.text = goal
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val dateSerializable = bundle!!.getSerializable("date")
        val dateText = formatter.format(dateSerializable).toString()
        binding.tvDate.text = dateText

        binding.tvWalletBalance.text = "₱ " +
                DecimalFormat("#,##0.00")
                    .format(balance - amount.toFloat())

    }

    // For Pay With PayMaya API: This is where the payment request is built and payment details can be defined.
    /* private fun buildSinglePaymentRequest(): SinglePaymentRequest {
        val amount = BigDecimal(amount)
        val requestReferenceNumber = "0"

        // For Pay With PayMaya API: Will redirect customers to these urls upon results of payment.
        // Note: We are not using this but this needs to be here or else the API will have an error.
        val redirectUrl = RedirectUrl(
            success = "http://success.com",
            failure = "http://failure.com",
            cancel = "http://cancel.com"
        )
        val metadata = JSONObject()
        metadata.put("customerName", "John Doe")
        metadata.put("customerEmail", "johndoe@example.com")
        return SinglePaymentRequest(
            TotalAmount(
                amount,
                "PHP",
                AmountDetails()
            ),
            requestReferenceNumber,
            redirectUrl,
            metadata
        )
        *//*null as JSONObject?*//*
    }*/
    private fun buildSinglePaymentRequest(): SinglePaymentRequest {
        val amount = BigDecimal(amount)
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

    private fun payMaya(){
        binding.btnConfirm.setOnClickListener{
            //TODO: Paymaya dito lagay
            payWithPayMayaClient.startSinglePaymentActivityForResult(this, buildSinglePaymentRequest())
        }
    }

    // For Pay With PayMaya API: Once A Pay With PayMaya Activity finishes, this function receives the results.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        payWithPayMayaClient.onActivityResult(requestCode, resultCode, data)?.let {
            processCheckoutResult(it)
        }
    }


    // For Pay With PayMaya API: This processes the results from onActivityResult() and gets the Payment ID from the transaction.
    private fun processCheckoutResult(result: PayWithPayMayaResult) {
        when (result) {
            is SinglePaymentResult.Success -> {
                val resultID: String = result.paymentId
                Toast.makeText(this, "Payment Successful. Payment ID: $resultID", Toast.LENGTH_LONG)
                    .show()
                Log.d("PayMayaa", resultID)
                adjustUserBalance()
                addPayMayaTransaction()
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

    private fun addPayMayaTransaction() {
        val childID  = FirebaseAuth.getInstance().currentUser!!.uid

        val transaction = hashMapOf(
            //TODO: add childID, createdBy
            "transactionName" to name,
            "transactionType" to "Expense (Maya)",
            "category" to category,
            "date" to bundle!!.getSerializable("date"),
            "createdBy" to childID,
            "amount" to amount.toFloat(),
            "merchant" to merchant,
            "phoneNumber" to phone
        )
        firestore.collection("Transactions").add(transaction).addOnSuccessListener {
            goToPFM()
        }

    }

    private fun goToPFM() {
        val goToMayaQRSuccessfulPayment = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
        startActivity(goToMayaQRSuccessfulPayment)
    }

    private fun adjustUserBalance() {
        //TODO: Change user based on who is logged in
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        val currentUser  = FirebaseAuth.getInstance().currentUser!!.uid

        firestore.collection("ChildWallet").whereEqualTo("childID", currentUser)
            .get().addOnSuccessListener { document ->
                var adjustedBalance = amount.toDouble()
                    adjustedBalance = -abs(adjustedBalance)

                firestore.collection("ChildWallet").document(document.documents[0].id)
                    .update("currentBalance", FieldValue.increment(adjustedBalance))
            }
    }




    private fun cancel() {
        binding.btnCancel.setOnClickListener {
            val goBack = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goBack)
        }
    }
}