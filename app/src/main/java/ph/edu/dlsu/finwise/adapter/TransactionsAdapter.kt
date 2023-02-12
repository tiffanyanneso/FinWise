package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ItemTransactionBinding
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.TransactionFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.TransactionHistoryActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat


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
            // Might need to change the collection to "users" to find the specific transactions
            // (sub collection) for that user
            firestore.collection("Transactions").document(transactionID).get().addOnSuccessListener{ document ->
                val transaction = document.toObject<Transactions>()

                val dec = DecimalFormat("#,###.00")
                val amount = dec.format(transaction?.amount)
                itemBinding.tvTransactionId.text = document.id
                itemBinding.tvName.text = transaction?.transactionName

                //convert timestamp to date string
                val formatter = SimpleDateFormat("MM/dd/yyyy")
                val date = formatter.format(transaction?.date?.toDate())
                itemBinding.tvDate.text =  date.toString()
                if (transaction?.transactionType == "Income" || transaction?.transactionType == "Withdrawal") {
                    itemBinding.tvAmount.text = "+₱$amount"
                    itemBinding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.very_light_green))
                }
                else if (transaction?.transactionType == "Expense" || transaction?.transactionType == "Deposit") {
                    itemBinding.tvAmount.text = "-₱$amount"
                    itemBinding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_red))
                }
            }
        }

        override fun onClick(p0: View?) {
            /*val viewTransaction = Intent(context, ViewTransactionActivity::class.java)
            val bundle = Bundle()
            val transactionID = itemBinding.tvTransactionId.text.toString()
            bundle.putString ("transactionID", transactionID)
            viewTransaction.putExtras(bundle)
            viewTransaction.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(viewTransaction)*/

            val bundle = Bundle()
            val transactionID = itemBinding.tvTransactionId.text.toString()

            //Show dialog fragment
            val activity = context as FragmentActivity
            val fm: FragmentManager = activity.supportFragmentManager
            val dialogFragment = TransactionFragment()
            bundle.putString("transactionID", transactionID);
            dialogFragment.arguments = bundle
            dialogFragment.show(fm, "fragment_alert")
        }
    }
}