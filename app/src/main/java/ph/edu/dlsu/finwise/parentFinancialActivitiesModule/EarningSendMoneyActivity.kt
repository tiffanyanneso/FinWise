package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
import ph.edu.dlsu.finwise.databinding.ActivityEarningSendMoneyBinding
import ph.edu.dlsu.finwise.databinding.DialogEditChoreRewardBinding
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import java.io.File
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class EarningSendMoneyActivity : AppCompatActivity() {

    private lateinit var binding:ActivityEarningSendMoneyBinding

    private var firestore = Firebase.firestore

    private var amount = 0.00F

    private lateinit var earningActivityID:String
    private lateinit var savingActivityID:String
    private lateinit var childID:String
    private var maxAmount = 0.0
    private lateinit var paymentType:String

    private lateinit var source:String

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private lateinit var dialogBinding:DialogEditChoreRewardBinding

    // For Pay With PayMaya API: This is instantiating the Pay With PayMaya API.
    private val payWithPayMayaClient = PayWithPayMaya.newBuilder()
        .clientPublicKey("pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(LogLevel.ERROR)
        .build()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningSendMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras!!
        earningActivityID = bundle.getString("earningActivityID").toString()
        childID = bundle.getString("childID").toString()
        //paymentType = bundle.getString("paymentType").toString()
        checkUser()
        loadBackButton()

        // Initializes the navbar

        loadEarningActivityText()
        loadSendMoneyButton()
        loadEditAmountButton()
    }

    private fun loadEarningActivityText() {
        firestore.collection("EarningActivities").document(earningActivityID).get().addOnSuccessListener {
            val earning = it.toObject<EarningActivityModel>()
            binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(earning?.targetDate!!.toDate())
            binding.tvFinishDate.text = SimpleDateFormat("MM/dd/yyyy").format(earning.dateCompleted!!.toDate())
            binding.tvActivity.text = earning.activityName
            binding.tvDuration.text = earning.requiredTime.toString() + " Minutes"
            binding.tvAmountEarned.text = "₱ " + DecimalFormat("#,##0.00").format(earning?.amount)
            amount = earning.amount!!.toFloat()
            binding.tvStatus.text = earning.status
            binding.tvSource.text = earning.depositTo
            binding.tvPaymentType.text = earning.paymentType
            paymentType = earning.paymentType!!
            source = earning.depositTo!!
            childID = earning.childID!!

            if (earning.depositTo == "Financial Goal") {
                savingActivityID = earning.savingActivityID!!
                binding.layoutGoalName.visibility = View.VISIBLE
                firestore.collection("FinancialActivities").document(earning?.savingActivityID!!).get().addOnSuccessListener {
                    var goalID = it.toObject<FinancialActivities>()!!.financialGoalID
                    firestore.collection("FinancialGoals").document(goalID!!).get().addOnSuccessListener {
                        binding.tvGoalName.text = it.toObject<FinancialGoals>()!!.goalName
                    }
                }
            } else
                binding.layoutGoalName.visibility = View.GONE

            if (earning.status == "Completed") {
                binding.layoutText.visibility = View.GONE
                binding.layoutButtons.visibility = View.GONE
            }

            if (earning.requirePicture == true) {
                binding.layoutProof.visibility = View.VISIBLE
                loadProofImage()
            }
            else {
                binding.layoutProof.visibility = View.GONE
                binding.layoutLoading.visibility = View.GONE
                binding.layoutMain.visibility = View.VISIBLE
            }

        }
    }

    private fun loadProofImage() {
        var storageReference = Firebase.storage.reference.child("images/$earningActivityID")
        val file = File.createTempFile("tempImage", "jpg")
        storageReference.getFile(file).addOnSuccessListener {

            val exifInterface = ExifInterface(file.absolutePath)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            val rotationAngle = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }

            val originalBitmap = BitmapFactory.decodeFile(file.absolutePath)

            val matrix = Matrix()
            matrix.postRotate(rotationAngle.toFloat())

            val rotatedBitmap = Bitmap.createBitmap(
                originalBitmap,
                0,
                0,
                originalBitmap.width,
                originalBitmap.height,
                matrix,
                true
            )
            binding.imageProof.setImageBitmap(rotatedBitmap)
            binding.layoutLoading.visibility = View.GONE
            binding.layoutMain.visibility = View.VISIBLE
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

    private fun loadEditAmountButton() {
        binding.btnEditAmount.setOnClickListener {
            checkAge()
            dialogBinding = DialogEditChoreRewardBinding.inflate(getLayoutInflater())
            var dialog = Dialog(this);
            dialog.setContentView(dialogBinding.getRoot())
            dialog.window!!.setLayout(900, 800)
            dialogBinding.containerAmount.helperText = ""

            dialogBinding.btnSave.setOnClickListener {
                var newAmount = dialogBinding.etAmount.text.toString()
                if (newAmount.isNotEmpty()) {
                    amount = newAmount.toFloat()
                    firestore.collection("EarningActivities").document(earningActivityID).update("amount", newAmount.toFloat())

                    if (paymentType == "Maya")
                        payWithPayMayaClient.startSinglePaymentActivityForResult(this, buildSinglePaymentRequest())
                    else if (paymentType == "Cash")
                        logTransactions()
                } else
                    dialogBinding.containerAmount.helperText = "Input an amount"
            }

            dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

            dialog.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAge() {
        firestore.collection("Users").document(childID).get().addOnSuccessListener {
            var child = it.toObject<Users>()

            //compute age
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date = SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var age = difference.years
            if (age == 9)
                maxAmount = 100.0
            else if (age == 10 || age == 11)
                maxAmount = 300.0
            else
                maxAmount = 500.0

            dialogBinding.tvMaxAmount.text = "The max amount that can be given is ₱${DecimalFormat("#0.00").format(maxAmount)}"
        }
    }


    private fun loadSendMoneyButton() {
        binding.btnSendMoney.setOnClickListener {
            if (paymentType == "Maya")
                payWithPayMayaClient.startSinglePaymentActivityForResult(this, buildSinglePaymentRequest())
            else if (paymentType == "Cash")
                logTransactions()

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

                logTransactions()
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

    private fun logTransactions() {
        firestore.collection("EarningActivities").document(earningActivityID).update("status", "Completed")
        firestore.collection("EarningActivities").document(earningActivityID).update("dateSent", Timestamp.now())

        if (source == "Financial Goal")
            makeTransactionsGoal()
        else if (source == "Personal Finance")
            makeTransactionsPersonalFinance()
    }

    private fun adjustUserBalance() {
        firestore.collection("ChildWallet").whereEqualTo("childID", childID).whereEqualTo("type", paymentType).get().addOnSuccessListener { document ->
            val id = document.documents[0].id
            val adjustedBalance = amount.toDouble()

            firestore.collection("ChildWallet").document(id).update("currentBalance", FieldValue.increment(adjustedBalance)).addOnSuccessListener {
                Toast.makeText(this, "Successfully sent reward", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makeTransactionsPersonalFinance() {
        val income = hashMapOf(
            "userID" to childID,
            "transactionName" to "Income from ${binding.tvActivity.text.toString()}",
            "transactionType" to "Income",
            "category" to "Rewards",
            "date" to Timestamp.now(),
            "amount" to amount,
            "paymentType" to paymentType
        )
        firestore.collection("Transactions").add(income).addOnSuccessListener {
            adjustUserBalance()
            val earning = Intent(this, EarningActivity::class.java)
            val bundle = Bundle()
            bundle.putString("childID", childID)
            earning.putExtras(bundle)
            this.startActivity(earning)
        }
    }

    private fun makeTransactionsGoal() {
        val income = hashMapOf(
            "userID" to childID,
            "transactionName" to "Income from ${binding.tvActivity.text}",
            "transactionType" to "Income",
            "category" to "Rewards",
            "date" to Timestamp.now(),
            "financialActivityID" to savingActivityID,
            "amount" to amount,
            "paymentType" to paymentType

        )
        firestore.collection("Transactions").add(income).addOnSuccessListener {
            var deposit = hashMapOf(
                "userID" to childID,
                "transactionName" to "Deposit income from ${binding.tvActivity.text}",
                "transactionType" to "Deposit",
                "category" to "Goal",
                "date" to Timestamp.now(),
                "financialActivityID" to savingActivityID,
                "amount" to amount,
                "paymentType" to paymentType
            )
            firestore.collection("Transactions").add(deposit).addOnSuccessListener {
                firestore.collection("FinancialActivities").document(savingActivityID).get().addOnSuccessListener {
                    var activity = it.toObject<FinancialActivities>()
                    firestore.collection("FinancialGoals").document(activity?.financialGoalID!!).update("currentSavings", FieldValue.increment(amount.toLong())).addOnSuccessListener {
                        Toast.makeText(this, "Successfully sent reward", Toast.LENGTH_SHORT).show()
                        var earning = Intent(this, EarningActivity::class.java)
                        var bundle = Bundle()
                        bundle.putString("savingActivityID", savingActivityID)
                        bundle.putString("childID", childID)
                        earning.putExtras(bundle)
                        this.startActivity(earning)
                    }
                }
            }
        }
    }

    private fun checkUser() {
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            //user is a child
            if (it.toObject<Users>()!!.userType == "Child")
                binding.btnSendMoney.visibility = View.GONE
            
            val user = it.toObject<Users>()!!
            //current user is a child
            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent
            if (user.userType == "Child") {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)
                binding.btnSendMoney.visibility = View.GONE
                binding.layoutText.visibility = View.GONE
                binding.btnEditAmount.visibility = View.GONE
            } else if (user.userType == "Parent") {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                //sends the ChildID to the parent navbar
                val bundleNavBar = Bundle()
                bundleNavBar.putString("childID", childID)
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)
            }
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}