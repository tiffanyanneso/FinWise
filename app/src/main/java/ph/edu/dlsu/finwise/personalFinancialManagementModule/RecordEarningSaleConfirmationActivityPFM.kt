package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityRecordEarningSaleConfirmationBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningSellingActivity
import ph.edu.dlsu.finwise.parentFinancialManagementModule.EarningSellingPFMActivity
import java.lang.Math.abs
import java.text.DecimalFormat

class RecordEarningSaleConfirmationActivityPFM : AppCompatActivity() {

    private lateinit var binding: ActivityRecordEarningSaleConfirmationBinding

    private lateinit var childID:String

    private var firestore = Firebase.firestore
    private val currentUser = FirebaseAuth.getInstance().currentUser!!.uid


    private lateinit var bundle:Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordEarningSaleConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bundle = intent.extras!!
        setFields()

        binding.btnConfirm.setOnClickListener {
            var sellingItem = hashMapOf(
                "itemName" to bundle.getString("saleName"),
                "amount" to bundle.getFloat("saleAmount"),
                "date" to bundle.getSerializable("saleDate"),
                "childID" to childID,
                "source" to "PFM"
            )
            firestore.collection("SellingItems").add(sellingItem)

            var incomeTransaction = hashMapOf(
                "childID" to childID,
                "transactionType" to "Income",
                "transactionName" to bundle.getString("saleName") + " Sale",
                "amount" to bundle.getFloat("saleAmount"),
                "category" to "Sale",
                "date" to bundle.getSerializable("saleDate"),
                "source" to "Financial Activity"
            )

            firestore.collection("Transactions").add(incomeTransaction).addOnSuccessListener {
                adjustUserBalance()
                var sellingActivity = Intent(this, EarningSellingPFMActivity::class.java)
                var sendBundle = Bundle()
                sendBundle.putString("childID", childID)
                sellingActivity.putExtras(sendBundle)
                startActivity(sellingActivity)
            }
        }

    }


    @SuppressLint("NewApi")
    private fun setFields() {
        childID = bundle.getString("childID").toString()

        binding.tvName.text = bundle.getString("saleName")
        binding.tvAmount.text =  "₱ " + DecimalFormat("#,##0.00").format(bundle.getFloat("saleAmount"))
        binding.tvDate.text = SimpleDateFormat("MM/dd/yyyy").format(bundle.getSerializable("saleDate"))

        firestore.collection("ChildWallet").whereEqualTo("childID", currentUser).get().addOnSuccessListener { result ->
            var walletObject = result.documents[0].toObject<ChildWallet>()
            binding.tvWalletBalance.text = "₱ " +  DecimalFormat("#,##0.00").format(walletObject?.currentBalance!! + bundle.getFloat("saleAmount"))
        }
    }

    private fun adjustUserBalance() {
        firestore.collection("ChildWallet").whereEqualTo("childID", currentUser).get().addOnSuccessListener { result ->
                var walletID = result.documents[0].id

                var adjustedBalance = bundle.getFloat("saleAmount")?.toDouble()
                adjustedBalance = abs(adjustedBalance!!)

                firestore.collection("ChildWallet").document(walletID).update("currentBalance", FieldValue.increment(adjustedBalance))
            }
    }

}