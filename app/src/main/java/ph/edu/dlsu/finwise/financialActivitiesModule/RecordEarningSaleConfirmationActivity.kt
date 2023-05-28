package ph.edu.dlsu.finwise.financialActivitiesModule

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityRecordEarningSaleConfirmationBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningSellingActivity
import java.text.DecimalFormat

class RecordEarningSaleConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordEarningSaleConfirmationBinding

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private var savingActivityID:String?=null

    private var firestore = Firebase.firestore

    private lateinit var bundle:Bundle

    private lateinit var depositTo:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordEarningSaleConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bundle = intent.extras!!
        depositTo = bundle.getString("depositTo").toString()
        setFields()
        loadButtons()
        setNavigationBar()

        binding.btnConfirm.setOnClickListener {

            if (bundle.getString("depositTo") == "Financial Goal") {
                savingActivityID = bundle.getString("savingActivityID")
                var sellingItem = hashMapOf(
                    "itemName" to bundle.getString("saleName"),
                    "amount" to bundle.getFloat("saleAmount"),
                    "date" to bundle.getSerializable("saleDate"),
                    "childID" to currentUser,
                    "paymentType" to bundle.getString("paymentType"),
                    "savingActivityID" to bundle.getString("savingActivityID"),
                    "depositTo" to bundle.getString("depositTo")
                )
                firestore.collection("SellingItems").add(sellingItem)
            } else if (bundle.getString("depositTo") == "Personal Finance") {
                var sellingItem = hashMapOf(
                    "itemName" to bundle.getString("saleName"),
                    "amount" to bundle.getFloat("saleAmount"),
                    "date" to bundle.getSerializable("saleDate"),
                    "childID" to currentUser,
                    "depositTo" to bundle.getString("depositTo"),
                    "paymentType" to bundle.getString("paymentType"),
                    )
                firestore.collection("SellingItems").add(sellingItem)
            }
            addTransactions()
        }
    }

    private fun addTransactions() {
        var incomeTransaction = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Income",
            "transactionName" to bundle.getString("saleName") + " Sale",
            "amount" to bundle.getFloat("saleAmount"),
            "category" to "Sale",
            "paymentType" to bundle.getString("paymentType"),
            "date" to bundle.getSerializable("saleDate"),
        )

        firestore.collection("Transactions").add(incomeTransaction)

        if (depositTo == "Personal Finance")
            adjustUserBalance(bundle.getFloat("saleAmount").toDouble())

        else if (depositTo == "Financial Goal") {
            firestore.collection("FinancialActivities").document(savingActivityID!!).get().addOnSuccessListener { activityResult ->
                var activity = activityResult.toObject<FinancialActivities>()
                firestore.collection("FinancialGoals").document(activity?.financialGoalID!!).get().addOnSuccessListener { goalResult ->
                    var goal = goalResult.toObject<FinancialGoals>()

                    var depositTransaction = hashMapOf(
                        "userID" to currentUser,
                        "transactionType" to "Deposit",
                        "transactionName" to goal?.goalName + " Deposit",
                        "amount" to bundle.getFloat("saleAmount"),
                        "financialActivityID" to savingActivityID,
                        "category" to "Deposit",
                        "paymentType" to bundle.getString("paymentType"),
                        "date" to bundle.getSerializable("saleDate")
                    )

                    firestore.collection("Transactions").add(depositTransaction).addOnSuccessListener {
                        //update current savings in goal
                        firestore.collection("FinancialGoals").document(activity?.financialGoalID!!).update("currentSavings", FieldValue.increment(bundle.getFloat("saleAmount").toDouble()))

                        var sellingActivity = Intent(this, EarningSellingActivity::class.java)
                        var sendBundle = Bundle()
                        sendBundle.putString("childID", currentUser)
                        sendBundle.putString("savingActivityID", savingActivityID)
                        sellingActivity.putExtras(sendBundle)
                        startActivity(sellingActivity)
                    }
                }
            }
        }
    }

    private fun adjustUserBalance(amount:Double) {
        firestore.collection("ChildWallet").whereEqualTo("childID", currentUser).get().addOnSuccessListener { document ->
                val id = document.documents[0].id
                firestore.collection("ChildWallet").document(id).update("currentBalance", FieldValue.increment(amount)).addOnSuccessListener {
                    var sellingActivity = Intent(this, EarningSellingActivity::class.java)
                    var sendBundle = Bundle()
                    sendBundle.putString("childID", currentUser)
                    sellingActivity.putExtras(sendBundle)
                    startActivity(sellingActivity)
                }
            }
    }


    @SuppressLint("NewApi")
    private fun setFields() {
        binding.tvName.text = bundle.getString("saleName")
        binding.tvAmount.text =  "₱ " + DecimalFormat("#,##0.00").format(bundle.getFloat("saleAmount"))
        binding.tvDate.text = SimpleDateFormat("MM/dd/yyyy").format(bundle.getSerializable("saleDate"))
        binding.tvPaymentType.text = bundle.getString("paymentType")
        binding.tvDepositTo.text = bundle.getString("depositTo")
        setBalance()
    }

    private fun setBalance() {
        if (depositTo == "Personal Finance") {
            firestore.collection("ChildWallet").whereEqualTo("childID", currentUser)
                .get().addOnSuccessListener { document ->
                    var walletAmount = 0.00f
                    for (wallets in document) {
                        val childWallet = wallets.toObject<ChildWallet>()
                        walletAmount += childWallet.currentBalance!!
                    }
                    var amount = DecimalFormat("#,##0.00").format(walletAmount + bundle.getFloat("saleAmount"))
                    if (walletAmount < 0.00F)
                        amount = "0.00"
                    binding.tvWalletBalance.text = "₱$amount"
                }
            binding.layoutGoal.visibility = View.GONE
        } else if (depositTo  == "Financial Goal") {
            binding.layoutGoal.visibility = View.VISIBLE
            savingActivityID = bundle.getString("savingActivityID")
            firestore.collection("FinancialActivities").document(savingActivityID!!).get().addOnSuccessListener {
                firestore.collection("FinancialGoals").document(it.toObject<FinancialActivities>()!!.financialGoalID!!).get().addOnSuccessListener { goal ->
                    var goalObject = goal.toObject<FinancialGoals>()
                    binding.tvGoalName.text = goalObject?.goalName
                    binding.tvWalletBalance.text = "₱${ DecimalFormat("#,##0.00").format(goalObject?.currentSavings!! + bundle.getFloat("saleAmount"))}"
                }
            }
        }
    }


    private fun loadButtons() {
        loadBackButton()
    }

    private fun setNavigationBar() {

        var navUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(navUser).get().addOnSuccessListener {

            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent

            if (it.toObject<Users>()!!.userType == "Parent") {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                //sends the ChildID to the parent navbar
                val bundle = intent.extras!!
                val childID = bundle.getString("childID").toString()
                val bundleNavBar = Bundle()
                bundleNavBar.putString("childID", childID)
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)
            } else if (it.toObject<Users>()!!.userType == "Child")  {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)
            }
        }
    }
    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.btnCancel.setOnClickListener {
            var recordSale = Intent(this, RecordEarningSaleActivity::class.java)
           recordSale.putExtras(bundle)
            startActivity(recordSale)
        }
    }

}