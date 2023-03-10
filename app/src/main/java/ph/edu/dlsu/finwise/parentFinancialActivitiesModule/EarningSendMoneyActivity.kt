package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityCompletedEarningBinding
import ph.edu.dlsu.finwise.databinding.ActivityEarningSendMoneyBinding
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.FinancialActivities
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class EarningSendMoneyActivity : AppCompatActivity() {

    private lateinit var binding:ActivityEarningSendMoneyBinding

    private var firestore = Firebase.firestore

    private var amount = 0.00F

    private lateinit var earningActivityID:String
    private lateinit var savingActivityID:String
    private lateinit var childID:String

    private lateinit var source:String

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningSendMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        earningActivityID = bundle.getString("earningActivityID").toString()
        childID = bundle.getString("childID").toString()
        checkUser()
       loadBackButton()

        // Initializes the navbar
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal)

        firestore.collection("EarningActivities").document(earningActivityID).get().addOnSuccessListener {
            var earning = it.toObject<EarningActivityModel>()
            binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(earning?.targetDate!!.toDate())
            binding.tvFinishDate.text = SimpleDateFormat("MM/dd/yyyy").format(earning?.dateCompleted!!.toDate())
            binding.tvActivity.text = earning?.activityName
            binding.tvDuration.text = earning?.requiredTime.toString() + " Minutes"
            binding.tvAmountEarned.text = "₱ " + DecimalFormat("#,##0.00").format(earning?.amount)
            amount = earning?.amount!!.toFloat()
            binding.tvStatus.text = earning?.status
            binding.tvSource.text = earning?.source
            source = earning?.source!!
            if (source == "Financial Goal")
                savingActivityID = earning?.savingActivityID!!
        }


        binding.btnSendMoney.setOnClickListener {
            firestore.collection("EarningActivities").document(earningActivityID).update("status", "Completed")
            firestore.collection("EarningActivities").document(earningActivityID).update("dateCompleted", Timestamp.now())
            if (source == "Financial Goal")
                makeTransactionsGoal()
            else if (source == "Personal Finance")
                makeTransactionsPersonalFinance()
        }
    }

    private fun makeTransactionsPersonalFinance() {
        println("print ppersonal finance")
        val income = hashMapOf(
            "userID" to childID,
            "transactionName" to binding.tvActivity.text.toString(),
            "transactionType" to "Income",
            "category" to "Rewards",
            "date" to Timestamp.now(),
            "amount" to amount
        )
        firestore.collection("Transactions").add(income).addOnSuccessListener {
            //double
            val earning = Intent(this, EarningActivity::class.java)
            val bundle = Bundle()
            bundle.putString("childID", childID)
            earning.putExtras(bundle)
            this.startActivity(earning)
        }
    }

    private fun makeTransactionsGoal() {
        println("print goal")
        var income = hashMapOf(
            "createdBy" to childID,
            "transactionName" to binding.tvActivity.text.toString(),
            "transactionType" to "Income",
            "category" to "Rewards",
            "date" to Timestamp.now(),
            "financialActivityID" to savingActivityID,
            "amount" to amount)
        firestore.collection("Transactions").add(income).addOnSuccessListener {
            var deposit = hashMapOf(
                "createdBy" to childID,
                "transactionName" to binding.tvActivity.text.toString(),
                "transactionType" to "Deposit",
                "category" to "Goal",
                "date" to Timestamp.now(),
                "financialActivityID" to savingActivityID,
                "amount" to amount)
            firestore.collection("Transactions").add(deposit).addOnSuccessListener {
                firestore.collection("FinancialActivities").document(savingActivityID).get().addOnSuccessListener {
                    var activity = it.toObject<FinancialActivities>()
                    firestore.collection("FinancialGoals").document(activity?.financialGoalID!!).update("currentSavings", FieldValue.increment(amount.toLong())).addOnSuccessListener {
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
        firestore.collection("ChildUser").document(currentUser).get().addOnSuccessListener {
            //user is a child
            if (it.exists())
                binding.btnSendMoney.visibility = View.GONE
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}