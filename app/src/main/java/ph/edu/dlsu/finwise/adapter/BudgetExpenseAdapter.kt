package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemBudgetExpenseBinding
import ph.edu.dlsu.finwise.model.BudgetCategoryItem
import ph.edu.dlsu.finwise.model.BudgetExpense
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat

class BudgetExpenseAdapter : RecyclerView.Adapter<BudgetExpenseAdapter.BudgetCategoryItemViewHolder>{

    private var expenseTransactionIDArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore

    constructor(context: Context, expenseTransactionIDArrayList:ArrayList<String>) {
        this.context = context
        this.expenseTransactionIDArrayList = expenseTransactionIDArrayList
    }

    override fun getItemCount(): Int {
        return expenseTransactionIDArrayList.size
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
        holder.bindItem(expenseTransactionIDArrayList[position])
    }

    inner class BudgetCategoryItemViewHolder(private val itemBinding: ItemBudgetExpenseBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(expenseTransactionID: String){
            firestore.collection("BudgetExpenses").document(expenseTransactionID).get().addOnSuccessListener {
                var expense = it.toObject<BudgetExpense>()
                itemBinding.tvItem.text = expense?.expenseName
                itemBinding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(expense?.amount)
            }
        }

        override fun onClick(p0: View?) {
        }
    }
}