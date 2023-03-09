package ph.edu.dlsu.finwise.financialActivitiesModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.SpendingExpenseAdapter
import ph.edu.dlsu.finwise.databinding.ActivityReasonExpensesBinding
import ph.edu.dlsu.finwise.model.Transactions

class ReasonExpensesActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReasonExpensesBinding
    private var firestore = Firebase.firestore

    private lateinit var budgetActivityID:String

    private lateinit var spendingExpensesAdapter: SpendingExpenseAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReasonExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        budgetActivityID = bundle.getString("budgetActivityID").toString()

        getExpenseList()

        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
    }

    private fun getExpenseList() {
        var expensesArrayList = ArrayList<Transactions>()
        firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetActivityID).get().addOnSuccessListener { budgetItems ->
            for (item in budgetItems) {
                firestore.collection("Transactions").whereEqualTo("budgetItemID", item.id).get().addOnSuccessListener { transactions ->
                    expensesArrayList.addAll(transactions.toObjects())
                }.continueWith {
                    spendingExpensesAdapter = SpendingExpenseAdapter(this, expensesArrayList)
                    binding.rvViewItms.adapter = spendingExpensesAdapter
                    binding.rvViewItms.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    spendingExpensesAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}