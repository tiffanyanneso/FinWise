package ph.edu.dlsu.finwise.financialActivitiesModule

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
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

    private lateinit var childID:String
    private lateinit var savingActivityID:String

    private var firestore = Firebase.firestore

    private lateinit var bundle:Bundle

    private var user = "child"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordEarningSaleConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bundle = intent.extras!!
        setFields()
        loadButtons()

        binding.btnConfirm.setOnClickListener {
            var sellingItem = hashMapOf(
                "itemName" to bundle.getString("saleName"),
                "amount" to bundle.getFloat("saleAmount"),
                "date" to bundle.getSerializable("saleDate"),
                "childID" to childID,
                "savingActivityID" to savingActivityID
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

            firestore.collection("Transactions").add(incomeTransaction)

            firestore.collection("FinancialActivities").document(savingActivityID).get().addOnSuccessListener { activityResult ->
                var activity = activityResult.toObject<FinancialActivities>()
                firestore.collection("FinancialGoals").document(activity?.financialGoalID.toString()).get().addOnSuccessListener { goalResult ->
                    var goal = goalResult.toObject<FinancialGoals>()

                    var depositTransaction = hashMapOf(
                        "childID" to childID,
                        "transactionType" to "Deposit",
                        "transactionName" to goal?.goalName + " Deposit",
                        "amount" to bundle.getFloat("saleAmount"),
                        "financialActivityID" to savingActivityID,
                        "category" to "Sale",
                        "date" to bundle.getSerializable("saleDate")
                    )

                    firestore.collection("Transactions").add(depositTransaction).addOnSuccessListener {
                        var sellingActivity = Intent(this, EarningSellingActivity::class.java)
                        var sendBundle = Bundle()
                        sendBundle.putString("childID", childID)
                        sendBundle.putString("savingActivityID", savingActivityID)
                        sellingActivity.putExtras(sendBundle)
                        startActivity(sellingActivity)
                    }
                }
            }
        }
    }


    @SuppressLint("NewApi")
    private fun setFields() {
        childID = bundle.getString("childID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()

        binding.tvName.text = bundle.getString("saleName")
        binding.tvAmount.text =  "₱ " + DecimalFormat("#,##0.00").format(bundle.getFloat("saleAmount"))
        binding.tvDate.text = SimpleDateFormat("MM/dd/yyyy").format(bundle.getSerializable("saleDate"))
    }

    private fun loadButtons() {
        setNavigationBar()
        loadBackButton()
    }

    private fun setNavigationBar() {
        val bottomNavigationViewChild = binding.bottomNav
        val bottomNavigationViewParent = binding.bottomNavParent

        if (user == "child") {
            bottomNavigationViewChild.visibility = View.VISIBLE
            bottomNavigationViewParent.visibility = View.GONE
            Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        } else {
            bottomNavigationViewChild.visibility = View.GONE
            bottomNavigationViewParent.visibility = View.VISIBLE
            NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal)
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}