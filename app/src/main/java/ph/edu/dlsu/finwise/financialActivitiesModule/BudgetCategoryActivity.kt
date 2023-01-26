package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.BudgetCategoryAdapter
import ph.edu.dlsu.finwise.adapter.BudgetCategoryItemAdapter
import ph.edu.dlsu.finwise.databinding.ActivityBudgetBinding
import ph.edu.dlsu.finwise.databinding.ActivityBudgetCategoryBinding
import ph.edu.dlsu.finwise.model.BudgetCategory
import ph.edu.dlsu.finwise.model.BudgetCategoryItem

class BudgetCategoryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBudgetCategoryBinding
    private var firestore = Firebase.firestore
    private lateinit var budgetCategoryItemAdapter: BudgetCategoryItemAdapter

    private lateinit var decisionMakingActivityID:String
    private lateinit var budgetCategoryID: String
    private lateinit var bundle:Bundle

    private var budgetItemIDArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bundle = intent.extras!!
        decisionMakingActivityID = bundle.getString("decisionMakingActivityID").toString()
        budgetCategoryID = bundle.getString("categoryID").toString()


        firestore.collection("BudgetCategories").document(budgetCategoryID).get().addOnSuccessListener {
            var budgetCategory = it.toObject<BudgetCategory>()
            binding.tvCategoryName.text = budgetCategory?.budgetCategory
            binding.tvCategoryAmount.text = "₱ " +  budgetCategory?.amount.toString()
        }

        getBudgetItems()

        binding.btnNewCategory.setOnClickListener {
            showNewItemDialog()
        }
    }

    private fun getBudgetItems() {
        firestore.collection("BudgetItems").whereEqualTo("budgetCategoryID", budgetCategoryID).get().addOnSuccessListener { budgetCategories ->
            for (document in budgetCategories) {
                var budgetItem = document.id
                budgetItemIDArrayList.add(budgetItem)
            }
            budgetCategoryItemAdapter = BudgetCategoryItemAdapter(this, budgetItemIDArrayList)
            binding.rvViewItems.adapter = budgetCategoryItemAdapter
            binding.rvViewItems.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun showNewItemDialog() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_new_budget_item)

        val btnSave = dialog.findViewById<Button>(R.id.btn_save)
        var btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)


        btnSave.setOnClickListener {
            var itemName = dialog.findViewById<EditText>(R.id.dialog_et_item_name).text.toString()
            var itemAmount = dialog.findViewById<EditText>(R.id.dialog_et_item_amount).text.toString().toFloat()
            var budgetItem = BudgetCategoryItem(itemName, budgetCategoryID, decisionMakingActivityID, itemAmount)
            firestore.collection("BudgetItems").add(budgetItem).addOnSuccessListener {
                budgetItemIDArrayList.add(it.id)
                budgetCategoryItemAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}