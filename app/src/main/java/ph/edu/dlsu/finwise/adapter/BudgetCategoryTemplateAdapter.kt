package ph.edu.dlsu.finwise.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ItemBudgetCategorySelectBinding

class BudgetCategoryTemplateAdapter : RecyclerView.Adapter<BudgetCategoryTemplateAdapter.BudgetCategoryViewHolder>{

    private var budgetCategoryArrayList = ArrayList<String>()
    private var checkedItemsArrayList = ArrayList<CheckedItem>()
    private var recyclerView:RecyclerView
    private var context: Context

    class CheckedItem(var itemName:String, var amount:Float)

    constructor(context: Context, budgetCategoryArrayList: ArrayList<String>, recyclerView: RecyclerView) {
        this.context = context
        this.budgetCategoryArrayList = budgetCategoryArrayList
        this.recyclerView = recyclerView
    }

    override fun getItemCount(): Int {
        return budgetCategoryArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BudgetCategoryTemplateAdapter.BudgetCategoryViewHolder {
        val itemBinding = ItemBudgetCategorySelectBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return BudgetCategoryViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BudgetCategoryTemplateAdapter.BudgetCategoryViewHolder,
                                  position: Int) {
        holder.bindCategory(budgetCategoryArrayList[position])
    }

    fun returnCheckedBudgetItems(): ArrayList<CheckedItem> {
        for (i in 0 until budgetCategoryArrayList.size) {
            var viewHolder = recyclerView.findViewHolderForAdapterPosition(i)
            val itemView = viewHolder?.itemView!!
            val checkbox = itemView.findViewById<CheckBox>(R.id.cb_include)
            if (checkbox.isChecked)
                checkedItemsArrayList.add(CheckedItem(itemView.findViewById<TextView>(R.id.tv_budget_item_name).text.toString(),
                    itemView.findViewById<TextInputEditText>(R.id.et_amount).text.toString().toFloat()))
        }

        return checkedItemsArrayList
    }

    inner class BudgetCategoryViewHolder(private val itemBinding: ItemBudgetCategorySelectBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindCategory(budgetItem: String){
            itemBinding.tvBudgetItemName.text = budgetItem
            itemBinding.cbInclude.setOnClickListener {
                if (itemBinding.cbInclude.isChecked)
                    itemBinding.amountContainer.visibility = View.VISIBLE
                else
                    itemBinding.amountContainer.visibility = View.GONE
            }
        }

        override fun onClick(p0: View?) {
        }
    }
}