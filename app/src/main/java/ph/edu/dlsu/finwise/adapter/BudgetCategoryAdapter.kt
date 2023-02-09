package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemCategoryBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.BudgetExpenseActivity
import ph.edu.dlsu.finwise.model.BudgetItem
import java.text.DecimalFormat

class BudgetCategoryAdapter : RecyclerView.Adapter<BudgetCategoryAdapter.BudgetCategoryViewHolder>{

    private var budgetCategoryIDArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore

    constructor(context: Context, budgetCategoryIDArrayList:ArrayList<String>) {
        this.context = context
        this.budgetCategoryIDArrayList = budgetCategoryIDArrayList
    }

    override fun getItemCount(): Int {
        return budgetCategoryIDArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BudgetCategoryAdapter.BudgetCategoryViewHolder {
        val itemBinding = ItemCategoryBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return BudgetCategoryViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BudgetCategoryAdapter.BudgetCategoryViewHolder,
                                  position: Int) {
        holder.bindCategory(budgetCategoryIDArrayList[position])
    }

    inner class BudgetCategoryViewHolder(private val itemBinding: ItemCategoryBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindCategory(budgetItemID: String){
            firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
                var budgetCategory = it.toObject<BudgetItem>()
                itemBinding.tvBudgetItemId.text = it.id
                itemBinding.budgetActivityId.text = budgetCategory?.financialActivityID
                itemBinding.tvBudgetItemName.text = budgetCategory?.budgetItemName
                itemBinding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(budgetCategory?.amount)
            }
        }

        override fun onClick(p0: View?) {
            var budgetCategory = Intent(context, BudgetExpenseActivity::class.java)
            var bundle = Bundle()

            bundle.putString ("budgetItemID", itemBinding.tvBudgetItemId.text.toString())
            bundle.putString ("budgetActivityID", itemBinding.budgetActivityId.text.toString())
            budgetCategory.putExtras(bundle)
            budgetCategory.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(budgetCategory)
        }
    }
}