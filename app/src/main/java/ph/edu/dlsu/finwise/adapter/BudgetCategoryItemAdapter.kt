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
import ph.edu.dlsu.finwise.databinding.ItemCategoryItemBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.BudgetCategoryActivity
import ph.edu.dlsu.finwise.model.BudgetCategory
import ph.edu.dlsu.finwise.model.BudgetCategoryItem
import java.text.DecimalFormat

class BudgetCategoryItemAdapter : RecyclerView.Adapter<BudgetCategoryItemAdapter.BudgetCategoryItemViewHolder>{

    private var budgetItemIDArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore

    constructor(context: Context, budgetItemIDArrayList:ArrayList<String>) {
        this.context = context
        this.budgetItemIDArrayList = budgetItemIDArrayList
    }

    override fun getItemCount(): Int {
        return budgetItemIDArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BudgetCategoryItemAdapter.BudgetCategoryItemViewHolder {
        val itemBinding = ItemCategoryItemBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return BudgetCategoryItemViewHolder(itemBinding)
    }



    override fun onBindViewHolder(holder: BudgetCategoryItemAdapter.BudgetCategoryItemViewHolder,
                                  position: Int) {
        holder.bindItem(budgetItemIDArrayList[position])
    }

    inner class BudgetCategoryItemViewHolder(private val itemBinding: ItemCategoryItemBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(budgetItemID: String){
            firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
                var budgetItem = it.toObject<BudgetCategoryItem>()
                itemBinding.tvItem.text = budgetItem?.budgetCategoryItem.toString()
                itemBinding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(budgetItem?.amount.toString())
            }
        }

        override fun onClick(p0: View?) {
        }
    }
}