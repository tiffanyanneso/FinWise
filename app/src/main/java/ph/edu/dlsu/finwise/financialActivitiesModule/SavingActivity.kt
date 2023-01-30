package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.GoalViewDepositAdapater
import ph.edu.dlsu.finwise.databinding.ActivitySavingBinding
import ph.edu.dlsu.finwise.model.DecisionMakingActivities
import ph.edu.dlsu.finwise.model.Transactions

class SavingActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySavingBinding
    private var firestore = Firebase.firestore
    private lateinit var context: Context

    private lateinit  var goalViewDepositAdapater:GoalViewDepositAdapater


    private lateinit var decisionMakingActivityID:String
    private lateinit var goalID:String
    private var currentAmount:Float = 0.00F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        var bundle: Bundle = intent.extras!!
        decisionMakingActivityID = bundle.getString("decisionMakingActivityID").toString()
        goalID = bundle.getString("goalID").toString()

        getDepositHistory()
        getSavingProgress()


        binding.btnDeposit.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("goalID", goalID)
            bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
            var savingActivity = Intent(this, FinancialActivityGoalDeposit::class.java)
            savingActivity.putExtras(bundle)
            context.startActivity(savingActivity)
        }

        binding.tvViewAllDeposit.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
            var viewDeposit = Intent(this, SavingViewDepositActivity::class.java)
            viewDeposit.putExtras(bundle)
            context.startActivity(viewDeposit)
        }
    }

    private fun getDepositHistory() {
        firestore.collection("Transactions").whereEqualTo("decisionMakingActivityID", decisionMakingActivityID).get().addOnSuccessListener { transactionsSnapshot ->
            currentAmount = 0.00F
            var transactionsArrayList = ArrayList<Transactions>()
            for (document in transactionsSnapshot) {
                var transaction = document.toObject<Transactions>()
                transactionsArrayList.add(transaction)
                currentAmount += transaction.amount!!.toFloat()
            }
            goalViewDepositAdapater = GoalViewDepositAdapater(this, transactionsArrayList)
            binding.rvViewDepositHistory.adapter = goalViewDepositAdapater
            binding.rvViewDepositHistory.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun getSavingProgress() {
        firestore.collection("DecisionMakingActivities").document(decisionMakingActivityID).get().addOnSuccessListener {
            var decisionMakingActvity = it.toObject<DecisionMakingActivities>()
            binding.tvGoalAmount.text = "₱ " + currentAmount.toString() + " / ₱ " + decisionMakingActvity!!.targetAmount.toString()
            binding.progressBar.progress = currentAmount.toInt()
            binding.progressBar.max = decisionMakingActvity.targetAmount!!.toInt()
        }
    }
}