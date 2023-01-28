package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.TransactionsAdapter
import ph.edu.dlsu.finwise.databinding.ActivityPersonalFinancialManagementBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import kotlin.math.abs


class PersonalFinancialManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalFinancialManagementBinding
    private lateinit var transactionAdapter: TransactionsAdapter
    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalFinancialManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)

        loadBalance()
        getTransactions()
        goToDepositGoalActivity()
        goToIncomeActivity()
        goToExpenseActivity()
        goToTransactionHistory()
    }

    private fun loadBalance() {
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        firestore.collection("ChildWallet").whereEqualTo("childID", "JoCGIUSVMWTQ2IB7Rf41ropAv3S2")
            .get().addOnSuccessListener { documents ->
                lateinit var id: String
                for (document in documents) {
                    id = document.id
                }
                firestore.collection("ChildWallet").document(id).get()
                    .addOnSuccessListener { document ->
                    val balance = document.toObject<ChildWallet>()
                        binding.tvBalance.text = balance?.currentBalance.toString()
                }
            }

    }

    private fun goToTransactionHistory() {
        binding.tvViewAll.setOnClickListener {
            val goToDepositGoalActivity = Intent(applicationContext, TransactionHistoryActivity::class.java)
            startActivity(goToDepositGoalActivity)
        }
    }

    private fun getTransactions() {
        var transactionIDArrayList = ArrayList<String>()
        //TODO:change to get transactions of current user
        // Dito may collection na transaction at hindi nakanest yung collection sa user
        //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        //firestore.collection("Transactions").whereEqualTo("transactionID", currentUser).get()
        // .addOnSuccessListener{ documents ->

        firestore.collection("Transactions").get().addOnSuccessListener { documents ->
            for (transactionSnapshot in documents) {
                //creating the object from list retrieved in db
                val transactionID = transactionSnapshot.id
                transactionIDArrayList.add(transactionID)
            }

            transactionAdapter = TransactionsAdapter(applicationContext, transactionIDArrayList)
            binding.rvViewTransactions.adapter = transactionAdapter
            binding.rvViewTransactions.layoutManager = LinearLayoutManager(applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
        }
    }

    private fun goToDepositGoalActivity() {
        binding.btnGoal.setOnClickListener {
            val goToDepositGoalActivity = Intent(applicationContext, RecordDepositActivity::class.java)
            startActivity(goToDepositGoalActivity)
        }
    }

    private fun goToIncomeActivity() {
        binding.btnIncome.setOnClickListener {
            val goToIncomeActivity = Intent(applicationContext, RecordIncomeActivity::class.java)
            startActivity(goToIncomeActivity)
        }
    }

    private fun goToExpenseActivity() {
        binding.btnExpense.setOnClickListener {
            val goToExpenseActivity = Intent(applicationContext, RecordExpenseActivity::class.java)
            startActivity(goToExpenseActivity)
        }
    }

}