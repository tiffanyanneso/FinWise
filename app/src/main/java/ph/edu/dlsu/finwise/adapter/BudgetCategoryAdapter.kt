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
import ph.edu.dlsu.finwise.databinding.ItemGoalBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.BudgetCategoryActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.model.BudgetCategory
import ph.edu.dlsu.finwise.model.FinancialGoals
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

        fun bindCategory(budgetCategoryID: String){
            firestore.collection("BudgetCategories").document(budgetCategoryID).get().addOnSuccessListener {
                var budgetCategory = it.toObject<BudgetCategory>()
                itemBinding.tvCategoryId.text = it.id
                //itemBinding.decisionMakingActivityId.text = budgetCategory?.decisionMakingActivityID
                itemBinding.tvCategory.text = budgetCategory?.budgetCategory
                itemBinding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(budgetCategory?.amount)
            }
        }

        override fun onClick(p0: View?) {
            var budgetCategory = Intent(context, BudgetCategoryActivity::class.java)
            var bundle = Bundle()

            bundle.putString ("categoryID", itemBinding.tvCategoryId.text.toString())
            bundle.putString ("decisionMakingActivityID", itemBinding.decisionMakingActivityId.text.toString())
            budgetCategory.putExtras(bundle)
            budgetCategory.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(budgetCategory)
        }
    }
}