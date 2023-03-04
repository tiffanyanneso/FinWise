package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityCompletedEarningBinding
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.FinancialActivities

class EarningSendMoneyActivity : AppCompatActivity() {

    private lateinit var binding:ActivityCompletedEarningBinding

    private var firestore = Firebase.firestore

    private var amount = 0.00F

    private lateinit var earningActivityID:String
    private lateinit var savingActivityID:String
    private lateinit var childID:String

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompletedEarningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        earningActivityID = bundle.getString("earningActivityID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()
        childID = bundle.getString("childID").toString()
        checkUser()


        firestore.collection("EarningActivities").document(earningActivityID).get().addOnSuccessListener {
            var earning = it.toObject<EarningActivityModel>()
            binding.tvName.text = earning?.activityName
            binding.tvAmountEarned.text = earning?.amount.toString()
            amount = earning?.amount!!.toFloat()
        }


        binding.btnFinish.setOnClickListener {
            firestore.collection("EarningActivities").document(earningActivityID).update("status", "Completed")
            firestore.collection("EarningActivities").document(earningActivityID).update("dateCompleted", Timestamp.now())
            makeTransactions()
        }
    }

    private fun makeTransactions() {
        var income = hashMapOf(
            "createdBy" to childID,
            "transactionName" to binding.tvName.text.toString(),
            "transactionType" to "Income",
            "category" to "Rewards",
            "date" to Timestamp.now(),
            "financialActivityID" to savingActivityID,
            "amount" to amount)
        firestore.collection("Transactions").add(income).addOnSuccessListener {
            var deposit = hashMapOf(
                "createdBy" to childID,
                "transactionName" to binding.tvName.text.toString(),
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
                binding.btnFinish.visibility = View.GONE
        }
    }

}