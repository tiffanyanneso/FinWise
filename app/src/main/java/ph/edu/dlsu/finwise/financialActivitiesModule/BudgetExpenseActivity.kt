package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.BudgetExpenseAdapter
import ph.edu.dlsu.finwise.databinding.ActivityBudgetExpenseBinding
import ph.edu.dlsu.finwise.model.BudgetExpense
import ph.edu.dlsu.finwise.model.BudgetItem
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class BudgetExpenseActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBudgetExpenseBinding
    private var firestore = Firebase.firestore
    private lateinit var budgetExpenseAdapter: BudgetExpenseAdapter

    private lateinit var budgetCategoryID: String
    private lateinit var bundle:Bundle

    private var spent = 0.00F
    private var totalBudgetCategory = 0.00F
    private var remainingBudget = 0.00F

    private lateinit var budgetActivityID:String
    private lateinit var budgetItemID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        bundle = intent.extras!!
        budgetActivityID = bundle.getString("budgetActivityID").toString()
        budgetItemID = bundle.getString("budgetItemID").toString()

        //checkUser()
        getInfo()

        binding.btnRecordExpense.setOnClickListener {
            var recordExpense = Intent (this, FinancialActivityRecordExpense::class.java)
            var bundle = Bundle()
            bundle.putString("budgetActivityID", budgetActivityID)
            bundle.putString("budgetItemID", budgetItemID)
            bundle.putFloat("remainingBudget", remainingBudget)
            recordExpense.putExtras(bundle)
            this.startActivity(recordExpense)
        }
    }

    private fun getInfo() {
        firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
            var budgetItem  = it.toObject<BudgetItem>()
            binding.tvBudgetItemName.text = budgetItem?.budgetItemName
            //binding.tvCategoryAmount.text = "₱ " + DecimalFormat("###0.00").format(budgetItem?.amount)
        }.continueWith { getExpenses() }
    }


    private fun getExpenses() {
        var expenseArrayList = ArrayList<BudgetExpense>()
        firestore.collection("BudgetExpenses").whereEqualTo("budgetCategoryID", budgetItemID).get().addOnSuccessListener { results ->
            for (expense in results)
                expenseArrayList.add(expense.toObject<BudgetExpense>())

            expenseArrayList.sortByDescending {it.date}
            budgetExpenseAdapter = BudgetExpenseAdapter(this, expenseArrayList)
            budgetExpenseAdapter.notifyDataSetChanged()
            binding.rvViewItems.adapter = budgetExpenseAdapter
            binding.rvViewItems.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }.continueWith { getAvailableBudget() }
    }

    private fun getAvailableBudget() {
        firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
            var budgetCategory = it.toObject<BudgetItem>()
            var categoryAmount = budgetCategory?.amount
            firestore.collection("BudgetExpenses").whereEqualTo("budgetCategoryID", budgetItemID)
                .get().addOnSuccessListener { results ->
                for (expense in results) {
                    var expenseObject = expense.toObject<BudgetExpense>()
                    spent += expenseObject.amount!!.toFloat()
                }
            }.continueWith {
                    remainingBudget = categoryAmount?.minus(spent)!!
                    binding.tvCategoryAmount.text = "₱ " + DecimalFormat("###0.00").format(remainingBudget).toString()
            }
        }
    }

    private fun checkUser() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("ParentUser").document(currentUser).get().addOnSuccessListener {
            //current user is parent
            if (it.exists()) {
                binding.btnRecordExpense.visibility = View.GONE
            }
        }
    }
}