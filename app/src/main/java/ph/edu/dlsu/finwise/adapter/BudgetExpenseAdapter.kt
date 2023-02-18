package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemBudgetExpenseBinding
import ph.edu.dlsu.finwise.model.BudgetExpense
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class BudgetExpenseAdapter : RecyclerView.Adapter<BudgetExpenseAdapter.BudgetCategoryItemViewHolder>{

    private var expenseArrayList = ArrayList<BudgetExpense>()
    private var context: Context

    private var firestore = Firebase.firestore

    constructor(context: Context, expenseArrayList:ArrayList<BudgetExpense>) {
        this.context = context
        this.expenseArrayList = expenseArrayList
    }

    override fun getItemCount(): Int {
        return expenseArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BudgetExpenseAdapter.BudgetCategoryItemViewHolder {
        val itemBinding = ItemBudgetExpenseBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return BudgetCategoryItemViewHolder(itemBinding)
    }



    override fun onBindViewHolder(holder: BudgetExpenseAdapter.BudgetCategoryItemViewHolder,
                                  position: Int) {
        holder.bindItem(expenseArrayList[position])
    }

    inner class BudgetCategoryItemViewHolder(private val itemBinding: ItemBudgetExpenseBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(expense: BudgetExpense){
            itemBinding.tvItem.text = expense?.expenseName
            itemBinding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(expense?.amount)
            itemBinding.tvDate.text = SimpleDateFormat("MM/dd/yyyy").format(expense?.date!! .toDate())
        }

        override fun onClick(p0: View?) {
        }
    }
}