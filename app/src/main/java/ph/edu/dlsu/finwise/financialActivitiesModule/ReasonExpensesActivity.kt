package ph.edu.dlsu.finwise.financialActivitiesModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
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

    private lateinit var budgetingActivityID:String

    private lateinit var spendingExpensesAdapter: SpendingExpenseAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReasonExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        budgetingActivityID = bundle.getString("budgetingActivityID").toString()

        getExpenseList()
        loadBackButton()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
    }

    private fun getExpenseList() {
        var expensesArrayList = ArrayList<Transactions>()
        firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetingActivityID).get().addOnSuccessListener { budgetItems ->
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

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}