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
import ph.edu.dlsu.finwise.personalFinancialManagementModule.ViewTransactionActivity
import ph.edu.dlsu.finwise.databinding.ItemTransactionBinding
import ph.edu.dlsu.finwise.model.Transactions

class TransactionsAdapter: RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder> {

    private var transactionIDArrayList = ArrayList<String>()
    private var context: Context
    private var firestore = Firebase.firestore

    constructor(context: Context, transactionIDArrayList:ArrayList<String>) {
        this.context = context
        this.transactionIDArrayList = transactionIDArrayList
    }
    override fun getItemCount(): Int {
        return transactionIDArrayList.size
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
        holder.bindTransaction(transactionIDArrayList[position])
    }

    inner class TransactionViewHolder(private val itemBinding: ItemTransactionBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var transaction  = Transactions()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindTransaction(transactionID: String){
            firestore.collection("Transactions").document(transactionID).get().addOnSuccessListener{ document ->

                var transaction = document.toObject<Transactions>()
                itemBinding.tvTransactionId.text = document.id
                itemBinding.tvName.text = transaction?.transactionName
                itemBinding.tvDate.text = transaction?.date
                if (transaction?.transactionType == "income")
                    itemBinding.tvAmount.text = "+₱"+ transaction?.amount.toString()
                else
                    itemBinding.tvAmount.text = "-₱"+ transaction?.amount.toString()
            }
        }

        override fun onClick(p0: View?) {
            var viewTransaction = Intent(context, ViewTransactionActivity::class.java)
            var bundle = Bundle()

            var transactionID = itemBinding.tvTransactionId.text.toString()
            bundle.putString ("transactionID", transactionID)
            viewTransaction.putExtras(bundle)
            viewTransaction.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(viewTransaction)
        }
    }
}