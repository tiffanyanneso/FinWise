package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.BudgetExpenseAdapter
import ph.edu.dlsu.finwise.databinding.ActivityBudgetExpenseBinding
import ph.edu.dlsu.finwise.model.BudgetItem
import java.text.DecimalFormat

class BudgetExpenseActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBudgetExpenseBinding
    private var firestore = Firebase.firestore
    private lateinit var budgetExpenseAdapter: BudgetExpenseAdapter

    private lateinit var decisionMakingActivityID:String
    private lateinit var budgetCategoryID: String
    private lateinit var bundle:Bundle

    private var spent = 0.00F
    private var totalBudgetCategory = 0.00F

    private var expenseTransactionIDArrayList = ArrayList<String>()

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


        getInfo()


        //getSpentAmount()
        //getBudgetItems()
        //getBudgetCategoryAllocation()


        binding.btnRecordExpense.setOnClickListener {
            var recordExpense = Intent (this, FinancialActivityRecordExpense::class.java)
            var bundle = Bundle()
            bundle.putString("budgetActivityID", budgetActivityID)
            bundle.putString("budgetItemID", budgetItemID)
            recordExpense.putExtras(bundle)
            this.startActivity(recordExpense)
        }
    }

    private fun getExpenses() {
        println("printl "  + budgetItemID)

        firestore.collection("Transactions").whereEqualTo("category", budgetItemID).get().addOnSuccessListener { results ->
            for (expense in results)
                expenseTransactionIDArrayList.add(expense.id)

            budgetExpenseAdapter = BudgetExpenseAdapter(this, expenseTransactionIDArrayList)
            binding.rvViewItems.adapter = budgetExpenseAdapter
            binding.rvViewItems.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun getInfo() {
        firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
            var budgetItem  = it.toObject<BudgetItem>()
            binding.tvBudgetItemName.text = budgetItem?.budgetItemName
            binding.tvCategoryAmount.text = "₱ " + DecimalFormat("###0.00").format(budgetItem?.amount)
        }.continueWith { getExpenses() }
    }

    /*private fun getBudgetCategoryAllocation() {
        firestore.collection("BudgetCategories").document(budgetCategoryID).get().addOnSuccessListener {
            var budgetCategory = it.toObject<BudgetItem>()
            totalBudgetCategory = budgetCategory?.amount!!.toFloat()
            binding.tvCategoryName.text = budgetCategory?.budgetCategory
            binding.tvCategoryAmount.text = "₱ " + DecimalFormat("#,###.00").format(spent) + " / ₱ " +  DecimalFormat("#,###.00").format(budgetCategory?.amount)
        }
    }*/

    /*private fun getBudgetItems() {
        firestore.collection("BudgetItems").whereEqualTo("budgetCategoryID", budgetCategoryID).get().addOnSuccessListener { budgetItems ->
            for (document in budgetItems) {
                var budgetItemID = document.id
                budgetItemIDArrayList.add(budgetItemID)
            }
            budgetCategoryItemAdapter = BudgetCategoryItemAdapter(this, budgetItemIDArrayList)
            binding.rvViewItems.adapter = budgetCategoryItemAdapter
            binding.rvViewItems.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }*/

    /*private fun getSpentAmount() {
        firestore.collection("BudgetItems").whereEqualTo("budgetCategoryID", budgetCategoryID).get().addOnSuccessListener { documentSnapshot ->
            for (budgetItemSnapshot in documentSnapshot) {
                var budgetItem = budgetItemSnapshot.toObject<BudgetCategoryItem>()
                spent += budgetItem.amount!!
            }
        }
    }*/

    /*private fun updateSpent(itemAmount:Float) {
        spent += itemAmount
        binding.tvCategoryAmount.text = "₱ " + DecimalFormat("#,##0.00").format(spent) + " / ₱ "+ DecimalFormat("#,##0.00").format(totalBudgetCategory)
    }*/

    /*private fun showNewItemDialog() {

        var dialogBinding= DialogNewBudgetItemBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(800, 900)

        dialogBinding.btnSave.setOnClickListener {
            var itemName = dialog.findViewById<EditText>(R.id.dialog_et_item_name).text.toString()
            var itemAmount = dialog.findViewById<EditText>(R.id.dialog_et_item_amount).text.toString().toFloat()
            var budgetItem = BudgetCategoryItem(itemName, budgetCategoryID, decisionMakingActivityID, itemAmount)
            if (itemAmount+spent > totalBudgetCategory)
                dialog.findViewById<TextView>(R.id.tv_error).visibility = View.VISIBLE
            else {
                firestore.collection("BudgetItems").add(budgetItem).addOnSuccessListener {
                    dialog.findViewById<TextView>(R.id.tv_error).visibility = View.GONE
                    budgetItemIDArrayList.add(it.id)
                    budgetCategoryItemAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                    updateSpent(itemAmount)
                }
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }*/
}