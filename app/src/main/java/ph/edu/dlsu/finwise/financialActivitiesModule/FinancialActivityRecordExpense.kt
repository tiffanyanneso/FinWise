package ph.edu.dlsu.finwise.financialActivitiesModule

import android.R
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityFinancialRecordExpenseBinding
import ph.edu.dlsu.finwise.model.BudgetCategory

class FinancialActivityRecordExpense : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialRecordExpenseBinding
    private var firestore = Firebase.firestore

    private lateinit var context:Context

    private lateinit var decisionMakingActivityID:String
    private lateinit var financialGoalID:String

    private var expenseCategories = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialRecordExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=  this

        var bundle: Bundle = intent.extras!!
        decisionMakingActivityID = bundle.getString("decisionMakingActivityID").toString()
        financialGoalID = bundle.getString("financialGoalID").toString()

        setExpenseCategories()

        binding.btnNext.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
            bundle.putString("financialGoalID", financialGoalID)
            bundle.putString("expenseName", binding.etExpenseName.text.toString())
            bundle.putString("expenseCategory", binding.spinnerExpenseCategory.selectedItem.toString())
            bundle.putFloat("amount", binding.etAmount.text.toString().toFloat())
            var confirmSpending = Intent(this, FinancialActivityConfirmSpending::class.java)
            confirmSpending.putExtras(bundle)
            context.startActivity(confirmSpending)
        }

        binding.btnCancel.setOnClickListener {
            var spendingActivity = Intent(this, SpendingActivity::class.java)
            context.startActivity(spendingActivity)
        }
    }

    private fun setExpenseCategories() {
        //TODO: NEEDS WORK
        firestore.collection("BudgetCategories").whereEqualTo("decisionMakingActivityID", "decisionMakingActivityID").get().addOnSuccessListener { results ->
            for (category in results) {
                var categoryObject = category.toObject<BudgetCategory>()
                println("category  " +  categoryObject.budgetCategory.toString())
                expenseCategories.add(categoryObject.budgetCategory.toString())

            }
            val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, expenseCategories)
            binding.spinnerExpenseCategory.adapter = adapter
        }
    }
}