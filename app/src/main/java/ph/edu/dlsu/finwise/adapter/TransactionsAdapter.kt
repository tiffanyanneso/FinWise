package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ph.edu.dlsu.finwise.databinding.ItemTransactionBinding
import ph.edu.dlsu.finwise.model.Transactions

class TransactionsAdapter(
    private var context: Context,
    private var transactionsArrayList: ArrayList<Transactions>
) : RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    override fun getItemCount(): Int {
        return transactionsArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionsAdapter.TransactionViewHolder {
        val itemBinding = ItemTransactionBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return TransactionViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: TransactionsAdapter.TransactionViewHolder,
                                  position: Int) {
        holder.bindtransaction(transactionsArrayList[position])
    }

    inner class TransactionViewHolder(private val itemBinding: ItemTransactionBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var transaction  = Transactions()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindtransaction(transaction: Transactions){
            this.transaction = transaction
            itemBinding.tvName.text = transaction.transactionName
            itemBinding.tvDate.text = transaction.date
            itemBinding.tvAmount.text = "₱"+ transaction.amount.toString()
        }

        override fun onClick(p0: View?) {
        }
    }
}