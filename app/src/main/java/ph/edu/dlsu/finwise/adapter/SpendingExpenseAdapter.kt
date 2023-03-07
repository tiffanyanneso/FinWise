package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemSpendingExpenseBinding
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class SpendingExpenseAdapter : RecyclerView.Adapter<SpendingExpenseAdapter.BudgetCategoryItemViewHolder>{

    private var expenseArrayList = ArrayList<Transactions>()
    private var context: Context

    private var firestore = Firebase.firestore

    constructor(context: Context, expenseArrayList:ArrayList<Transactions>) {
        this.context = context
        this.expenseArrayList = expenseArrayList
    }

    override fun getItemCount(): Int {
        return expenseArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SpendingExpenseAdapter.BudgetCategoryItemViewHolder {
        val itemBinding = ItemSpendingExpenseBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return BudgetCategoryItemViewHolder(itemBinding)
    }



    override fun onBindViewHolder(holder: SpendingExpenseAdapter.BudgetCategoryItemViewHolder,
                                  position: Int) {
        holder.bindItem(expenseArrayList[position])
    }

    inner class BudgetCategoryItemViewHolder(private val itemBinding: ItemSpendingExpenseBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(transaction: Transactions){
            itemBinding.tvItem.text = transaction?.transactionName
            itemBinding.tvAmount.text = "- ₱ " + DecimalFormat("#,##0.00").format(transaction?.amount)
            itemBinding.tvDate.text = SimpleDateFormat("MM/dd/yyyy").format(transaction?.date!!.toDate())
        }

        override fun onClick(p0: View?) {
        }
    }
}