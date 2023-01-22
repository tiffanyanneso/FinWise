package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ph.edu.dlsu.finwise.PFMTransactionHistoryActivity
import ph.edu.dlsu.finwise.databinding.ItemTransactionBinding
import ph.edu.dlsu.finwise.model.Transactions

class TransactionsAdapter(
    private var transactionsArrayList: ArrayList<Transactions>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    interface OnItemClickListener {
        fun onLoadClick(position: Int)
    }

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
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onLoadClick(position)
                }
            }
        }

        fun bindtransaction(transaction: Transactions){
            this.transaction = transaction
            itemBinding.tvName.text = transaction.transactionName
            itemBinding.tvDate.text = transaction.date
            if (transaction.transactionType == "income")
                itemBinding.tvAmount.text = "+₱"+ transaction.amount.toString()
            else
                itemBinding.tvAmount.text = "-₱"+ transaction.amount.toString()
        }

        override fun onClick(p0: View?) {
        }
    }
}