package ph.edu.dlsu.finwise.parentFinancialManagementModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Timestamp
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
import org.json.JSONObject
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityParentMayaConfirmPaymentBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ParentConfirmPayment : AppCompatActivity() {
    private lateinit var binding: ActivityParentMayaConfirmPaymentBinding
    private lateinit var context: Context
    private var firestore = Firebase.firestore
    private var bundle: Bundle? = null
    private val parentID = FirebaseAuth.getInstance().currentUser!!.uid
    lateinit var name : String
    lateinit var phone : String
    lateinit var amount : String
    lateinit var paymentType : String
    lateinit var date : String
    lateinit var selectedChildID : String
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

        val bundle = intent.extras!!
        val childID = bundle.getString("childID").toString()
        val bundleNavBar = Bundle()
        bundleNavBar.putString("childID", childID)
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_dashboard, bundleNavBar)
        setText()
        payMaya()
        loadBackButton()
        cancel()
    }

    private fun payMaya(){
        binding.btnConfirm.setOnClickListener{
            if (paymentType == "Maya")
                payWithPayMayaClient.startSinglePaymentActivityForResult(this, buildSinglePaymentRequest())
            else if (paymentType == "Cash")
                addTransaction()
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
                addTransaction()
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

    private fun addTransaction() {
        adjustUserBalance()
        val transaction = hashMapOf(
            "childName" to name,
            "transactionName" to "Sending Money",
            "category" to "Sending Money to Child",
            "transactionType" to "Parent",
            "paymentType" to paymentType,
            "date" to bundle!!.getSerializable("date"),
            "userID" to parentID,
            "amount" to amount.toFloat(),
            "phoneNumber" to phone
        )
        firestore.collection("Transactions").add(transaction)

        val incomeChild = hashMapOf(
            "userID" to childID,
            "transactionName" to "Received money from parent",
            "transactionType" to "Income",
            "category" to "Gift",
            "date" to bundle!!.getSerializable("date"),
            "amount" to amount.toFloat(),
            "paymentType" to paymentType
        )

        firestore.collection("Transactions").add(incomeChild).continueWith {
            goToPFM()
        }

    }

    private fun adjustUserBalance() {
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        firestore.collection("ChildWallet").whereEqualTo("childID", selectedChildID)
            .whereEqualTo("type", paymentType)
            .get().addOnSuccessListener   { documents ->
                if (!documents.isEmpty) {
                    val adjustedBalance = amount.toDouble()

                    val id = documents.documents[0].id
                    updateChildWallet(id, adjustedBalance)
                } else {
                    createWallet()
                }
            }
    }

    private fun updateChildWallet(id: String, adjustedBalance: Double) {
        val updates = hashMapOf(
            "lastUpdated" to com.google.firebase.Timestamp.now(),
            "currentBalance" to FieldValue.increment(adjustedBalance)
        )
        firestore.collection("ChildWallet").document(id)
            .update(updates)
    }

    private fun createWallet() {
        val childWallet = hashMapOf(
            "childID" to selectedChildID,
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



    private fun goToPFM() {
        val goToSuccessPayment = Intent(applicationContext, ParentFinancialManagementActivity::class.java)
        val bundle = Bundle()
        bundle.putString("childID", childID)
        goToSuccessPayment.putExtras(bundle)
        startActivity(goToSuccessPayment)
    }


    private fun setText() {
        getText()

        val textAmount = getTextAmount()

        binding.tvExpenseAmount.text = "₱$textAmount"
        binding.tvName.text = name
        binding.tvPhoneNumber.text = phone
        binding.tvPaymentType.text = paymentType
        //binding.tvGoal.text = goal
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val dateSerializable = bundle!!.getSerializable("date")
        val dateText = formatter.format(dateSerializable).toString()
        binding.tvDate.text = dateText

        checkIfMaya()
        getBalanceChild()
    }

    private fun checkIfMaya() {
        if (paymentType == "Maya") {
            binding.llPhone.visibility = View.VISIBLE
            binding.tvPhoneNumber.text = phone
        }
    }

    private fun getBalanceChild() {
        firestore.collection("ChildWallet").whereEqualTo("childID", selectedChildID)
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
        paymentType = bundle!!.getString("paymentType").toString()
        /*balance = bundle!!.getFloat("balance")*/
        phone = bundle!!.getString("phone").toString()
        selectedChildID = bundle!!.getString("selectedChildID").toString()
        childID = bundle!!.getString("childID").toString()
        date = bundle!!.getSerializable("date").toString()
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun cancel() {
        binding.btnCancel.setOnClickListener {
            val goBack = Intent(applicationContext, ParentFinancialManagementActivity::class.java)
            startActivity(goBack)
        }
    }

}