package ph.edu.dlsu.finwise

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.TransactionsAdapter
import ph.edu.dlsu.finwise.databinding.ActivityPersonalFinancialManagementBinding
import ph.edu.dlsu.finwise.model.Transactions


class PersonalFinancialManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalFinancialManagementBinding
    private lateinit var transactionAdapter: TransactionsAdapter
    private var firestore = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalFinancialManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getTransactions()

        goToDepositGoalActivity()
        goToIncomeActivity()
    }

    private fun getTransactions() {
        var transactionArrayList = ArrayList<Transactions>()
        //TODO:change to get transactions of current user
        //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        //firestore.collection("Transactions").whereEqualTo("companyID", currentUser).get().addOnSuccessListener{ documents ->

        firestore.collection("Transactions").get().addOnSuccessListener { documents ->
            for (transactionSnapshot in documents) {
                //creating the object from list retrieved in db
                val transaction = transactionSnapshot.toObject<Transactions>()
                transactionArrayList.add(transaction!!)
            }

            binding.rvViewTransactions.layoutManager = LinearLayoutManager(applicationContext)
            transactionAdapter = TransactionsAdapter(applicationContext, transactionArrayList)
            binding.rvViewTransactions.adapter = transactionAdapter

        }
    }

    private fun goToDepositGoalActivity() {
        binding.btnGoal.setOnClickListener {
            Toast.makeText(this, "This is my Toast message!",
                Toast.LENGTH_LONG).show()
            val goToActivity = Intent(applicationContext, PFMRecordDepositActivity::class.java)
            startActivity(goToActivity)
        }
    }

    private fun goToIncomeActivity() {
        binding.btnGoal.setOnClickListener {
            Toast.makeText(this, "This is my Toast message!",
                Toast.LENGTH_LONG).show()
            val goToActivity = Intent(applicationContext, PFMRecordIncomeActivity::class.java)
            startActivity(goToActivity)
        }
    }

}