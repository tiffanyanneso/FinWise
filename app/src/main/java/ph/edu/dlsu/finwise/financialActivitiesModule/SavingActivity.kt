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
import ph.edu.dlsu.finwise.adapter.GoalViewDepositAdapater
import ph.edu.dlsu.finwise.databinding.ActivitySavingBinding
import ph.edu.dlsu.finwise.model.DecisionMakingActivities
import ph.edu.dlsu.finwise.model.Transactions

class SavingActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySavingBinding
    private var firestore = Firebase.firestore
    private lateinit var context: Context

    private lateinit  var goalViewDepositAdapater:GoalViewDepositAdapater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        var bundle: Bundle = intent.extras!!
        var decisionMakingActivityID = bundle.getString("decisionActivityID").toString()
        var goalID = bundle.getString("goalID")

        var currentAmount:Float = 0.00F


        firestore.collection("Transactions").whereEqualTo("decisionMakingActivityID", decisionMakingActivityID).get().addOnSuccessListener { transactionsSnapshot ->
            var transactionsArrayList = ArrayList<Transactions>()
            for (document in transactionsSnapshot) {
                var transaction = document.toObject<Transactions>()
                transactionsArrayList.add(transaction)
                currentAmount += transaction.amount!!.toFloat()
            }
            goalViewDepositAdapater = GoalViewDepositAdapater(this, transactionsArrayList)
            binding.rvViewDepositHistory.adapter = goalViewDepositAdapater
            binding.rvViewDepositHistory.layoutManager = LinearLayoutManager(applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
        }

        firestore.collection("DecisionMakingActivities").document(decisionMakingActivityID).get().addOnSuccessListener {
            var decisionMakingActvity = it.toObject<DecisionMakingActivities>()
            binding.tvGoalAmount.text = currentAmount.toString() + " / " + decisionMakingActvity!!.targetAmount.toString()
        }


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
}