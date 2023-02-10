package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.common.models.AmountDetails
import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.common.models.TotalAmount
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMaya
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMayaResult
import com.paymaya.sdk.android.paywithpaymaya.SinglePaymentResult
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import kotlinx.coroutines.*
import org.json.JSONObject
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.TransactionHistoryExpenseFragment
import ph.edu.dlsu.finwise.TransactionHistoryIncomeFragment
import ph.edu.dlsu.finwise.adapter.TransactionsAdapter
import ph.edu.dlsu.finwise.databinding.ActivityPfmtransactionHistoryBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childGoalFragment.*
import java.math.BigDecimal


class TransactionHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPfmtransactionHistoryBinding
    private lateinit var context: Context
    private lateinit var transactionAdapter: TransactionsAdapter
    private var firestore = Firebase.firestore
    private var transactionIDArrayList = ArrayList<String>()
    private lateinit var type: String


    // For Pay With PayMaya API: This is instantiating the Pay With PayMaya API.

    private val payWithPayMayaClient = PayWithPayMaya.newBuilder()
        .clientPublicKey("client_public_key")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(LogLevel.ERROR)
        .build()

    // For Pay With PayMaya API: Will redirect customers to these urls upon results of payment.
    // Note: We are not using this but this needs to be here or else the API will have an error.
    private val redirectUrl = RedirectUrl(
    success = "http://success.com",
    failure = "http://failure.com",
    cancel = "http://cancel.com"
    )

    // For Pay With PayMaya API: This is where the payment request is built and payment details can be defined.
    private fun buildSinglePaymentRequest(): SinglePaymentRequest {
        val amount = BigDecimal("100.00")
        val requestReferenceNumber = "0"
        return SinglePaymentRequest(
            TotalAmount(
                amount,
                "PHP",
                AmountDetails()
            ),
            requestReferenceNumber,
            redirectUrl,
            null as JSONObject?
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmtransactionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this


        loadBackButton()

        // Initializes the navbar
        Navbar(findViewById(ph.edu.dlsu.finwise.R.id.bottom_nav), this, ph.edu.dlsu.finwise.R.id.nav_finance)

       // transactionIDArrayList.clear()

        //        getAllTransactions()
//        sortTransactions()



        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                val adapter = ViewPagerAdapter(supportFragmentManager)

                adapter.addFragment(TransactionHistoryIncomeFragment(), "Income")
                adapter.addFragment(TransactionHistoryExpenseFragment(), "Expense")

                binding.viewPager.adapter = adapter
                binding.tabLayout.setupWithViewPager(binding.viewPager)
            }
        }
    }

    private fun loadBackButton() {
       /* binding.topA33333ppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topA33333ppBar.setNavigationOnClickListener {
            payWithPayMayaClient.startSinglePaymentActivityForResult(this, buildSinglePaymentRequest())
            *//*val goToPFM = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goToPFM)*//*
        }*/
        binding.btn.setOnClickListener {
            payWithPayMayaClient.startSinglePaymentActivityForResult(this, buildSinglePaymentRequest())
        }
    }

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
                Log.d("PayMaya-Jaj", resultID)
            }

            is SinglePaymentResult.Cancel -> {
                val resultID: String? = result.paymentId

                Toast.makeText(this, "Payment Cancelled. Payment ID: $resultID", Toast.LENGTH_LONG)
                    .show()
                if (resultID != null) {
                    Log.d("PayMaya-Jaj", resultID)
                }
            }

            is SinglePaymentResult.Failure -> {
                val resultID: String? = result.paymentId
                val message =
                    "Failure, checkoutId: ${resultID}, exception: ${result.exception}"
                if (result.exception is BadRequestException) {
                    Log.d("PayMaya-Jaj", (result.exception as BadRequestException).error.toString())
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }



    class ViewPagerAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm){

        private val mFrgmentList = ArrayList<Fragment>()
        private val mFrgmentTitleList = ArrayList<String>()
        override fun getCount() = mFrgmentList.size
        override fun getItem(position: Int) = mFrgmentList[position]
        override fun getPageTitle(position: Int) = mFrgmentTitleList[position]

        fun addFragment(fragment: Fragment, title:String){
            mFrgmentList.add(fragment)
            mFrgmentTitleList.add(title)
        }
    }

//    private fun sortTransactions() {
//        val sortSpinner = binding.spinnerSort
//        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parentView: AdapterView<*>?,
//                selectedItemView: View?,
//                position: Int,
//                id: Long
//            ) {
//                type = sortSpinner.selectedItem.toString()
//
//                if (type == "All") {
//                    getAllTransactions()
//                }
//                else {
//                    transactionIDArrayList.clear()
//                    var transactionType = "transactionType"
//                    if (type == "Goal")
//                        transactionType = "category"
//                    Toast.makeText(applicationContext, transactionType+" "+ type, Toast.LENGTH_SHORT).show()
//                    firestore.collection("Transactions").whereEqualTo(transactionType, type)
//                        .orderBy("date", Query.Direction.DESCENDING)
//                        .get().addOnSuccessListener { documents ->
//                            for (transactionSnapshot in documents) {
//                                //creating the object from list retrieved in db
//                                val transactionID = transactionSnapshot.id
//                                transactionIDArrayList.add(transactionID)
//                            }
//                            loadRecyclerView(transactionIDArrayList)
//                        }
//                }
//
//            }
//
//            override fun onNothingSelected(parentView: AdapterView<*>?) {
//                // your code here
//            }
//        }
//    }

//    private fun getAllTransactions() {
//        var allTransactions = ArrayList<String>()
//        //TODO:change to get transactions of current user
//        //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
//        //firestore.collection("Transactions").whereEqualTo("companyID", currentUser).get().addOnSuccessListener{ documents ->
//        firestore.collection("Transactions").orderBy("date", Query.Direction.DESCENDING)
//            .get().addOnSuccessListener { documents ->
//            for (transactionSnapshot in documents) {
//                //creating the object from list retrieved in db
//                val transactionID = transactionSnapshot.id
//                allTransactions.add(transactionID)
//            }
//            loadRecyclerView(allTransactions)
//        }
//    }

//    private fun loadRecyclerView(transactionIDArrayList: ArrayList<String>) {
//        transactionAdapter = TransactionsAdapter(this, transactionIDArrayList)
//        binding.rvViewTransactions.adapter = transactionAdapter
//        binding.rvViewTransactions.layoutManager = LinearLayoutManager(applicationContext,
//            LinearLayoutManager.VERTICAL,
//            false)
//        transactionAdapter.notifyDataSetChanged()
//    }

}