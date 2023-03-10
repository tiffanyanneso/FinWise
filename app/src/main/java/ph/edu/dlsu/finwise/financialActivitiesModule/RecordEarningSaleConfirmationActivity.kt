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
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
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
                    "depositTo" to bundle.getString("depositTo")
                )
                firestore.collection("SellingItems").add(sellingItem)
            }
            addTransactions()
        }
    }

    private fun addTransactions() {
        var incomeTransaction = hashMapOf(
            "childID" to currentUser,
            "transactionType" to "Income",
            "transactionName" to bundle.getString("saleName") + " Sale",
            "amount" to bundle.getFloat("saleAmount"),
            "category" to "Sale",
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
                        "childID" to currentUser,
                        "transactionType" to "Deposit",
                        "transactionName" to goal?.goalName + " Deposit",
                        "amount" to bundle.getFloat("saleAmount"),
                        "financialActivityID" to savingActivityID,
                        "category" to "Deposit",
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
    }

    private fun loadButtons() {
        loadBackButton()
    }

    private fun setNavigationBar() {

        var navUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("ParentUser").document(navUser).get().addOnSuccessListener {

            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent

            if (it.exists()) {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal)
            } else  {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
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