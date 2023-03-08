package ph.edu.dlsu.finwise.parentFinancialManagementModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
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
import kotlinx.coroutines.NonCancellable.cancel
import org.json.JSONObject
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityParentMayaConfirmPaymentBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.math.abs

class ParentMayaConfirmPayment : AppCompatActivity() {
    private lateinit var binding: ActivityParentMayaConfirmPaymentBinding
    private lateinit var context: Context
    private var firestore = Firebase.firestore
    var bundle: Bundle? = null
    val parentID = FirebaseAuth.getInstance().currentUser!!.uid
    lateinit var name : String
    lateinit var phone : String
    lateinit var amount : String
    var note = "None"
    lateinit var date : String
    lateinit var childID : String

    // For Pay With PayMaya API: This is instantiating the Pay With PayMaya API.
    private val payWithPayMayaClient = PayWithPayMaya.newBuilder()
        .clientPublicKey("pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(LogLevel.ERROR)
        .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentMayaConfirmPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        Navbar(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance)
        setText()
        payMaya()
        cancel()
    }

    private fun payMaya(){
        binding.btnConfirm.setOnClickListener{
            //TODO: Paymaya dito lagay
            payWithPayMayaClient.startSinglePaymentActivityForResult(this, buildSinglePaymentRequest())
        }
    }

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
                //adjustUserBalance()
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
        val transaction = hashMapOf(
            //TODO: add childID, createdBy
            "childName" to name,
            "transactionName" to "Parent send money to child",
            "transactionType" to "Parent",
            "date" to bundle!!.getSerializable("date"),
            "createdBy" to parentID,
            "amount" to amount.toFloat(),
            "phoneNumber" to phone,
            "note" to note
        )
        adjustUserBalance()
        //TODO: fix transaction
        firestore.collection("Transactions").add(transaction).addOnSuccessListener {

            goToPFM()
        }

    }

    private fun adjustUserBalance() {
        //TODO: Change user based on who is logged in
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        firestore.collection("ChildWallet").whereEqualTo("childID", childID)
            .get().addOnSuccessListener { document ->
                val id = document.documents[0].id
                var adjustedBalance = amount.toDouble()

                firestore.collection("ChildWallet").document(id)
                    .update("currentBalance", FieldValue.increment(adjustedBalance))
            }
    }


    private fun goToPFM() {
        val goToSuccessPayment = Intent(applicationContext, ParentFinancialManagementActivity::class.java)
        startActivity(goToSuccessPayment)
    }


    private fun setText() {
        getText()

        val textAmount = getTextAmount()

        binding.tvExpenseAmount.text = "₱$textAmount"
        binding.tvName.text = name
        binding.tvPhoneNumber.text = phone
        binding.tvNote.text = note
        //binding.tvGoal.text = goal
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val dateSerializable = bundle!!.getSerializable("date")
        val dateText = formatter.format(dateSerializable).toString()
        binding.tvDate.text = dateText

        getBalanceChild()


    }

    private fun getBalanceChild() {
        firestore.collection("ChildWallet").whereEqualTo("childID", childID)
            .get().addOnSuccessListener { document ->
                val childWallet = document.documents[0].toObject<ChildWallet>()
                val balance = childWallet?.currentBalance!!
                binding.tvWalletBalance.text = "₱ " +
                        DecimalFormat("#,##0.00")
                            .format(balance + amount.toFloat())
            }

    }

    private fun getTextAmount(): String {
        val dec = DecimalFormat("#,###.00")
        return dec.format(bundle!!.getFloat("amount"))
    }

    private fun getText() {
        bundle = intent.extras!!
        name = bundle!!.getString("name").toString()
        phone = bundle!!.getString("merchant").toString()
        amount = bundle!!.getFloat("amount").toString()
        /*balance = bundle!!.getFloat("balance")*/
        phone = bundle!!.getString("phone").toString()
        childID = bundle!!.getString("childID").toString()
        date = bundle!!.getSerializable("date").toString()
    }

}