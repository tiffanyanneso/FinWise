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
import ph.edu.dlsu.finwise.databinding.ItemGoalTransactionBinding
import ph.edu.dlsu.finwise.databinding.ItemTransactionBinding
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.TransactionFragment
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class GoalTransactionsAdapater : RecyclerView.Adapter<GoalTransactionsAdapater.GoalViewDepositViewHolder> {

    private var transactionsArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore

    constructor(context: Context, transactionsArrayList:ArrayList<String>) {
        this.context = context
        this.transactionsArrayList = transactionsArrayList
    }

    override fun getItemCount(): Int {
        if(transactionsArrayList.size > 4)
            return 4
        else
            return transactionsArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GoalTransactionsAdapater.GoalViewDepositViewHolder {
        val itemBinding = ItemGoalTransactionBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return GoalViewDepositViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: GoalTransactionsAdapater.GoalViewDepositViewHolder,
                                  position: Int) {
        holder.bindGoal(transactionsArrayList[position])
    }

    inner class GoalViewDepositViewHolder(private val itemBinding: ItemGoalTransactionBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var transactions  = Transactions()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(transactionID: String){
            firestore.collection("Transactions").document(transactionID).get().addOnSuccessListener {
                var transaction = it.toObject<Transactions>()

                val date =  SimpleDateFormat("MM/dd/yyyy").format(transaction?.date?.toDate())
                itemBinding.tvDate.text = date.toString()
                itemBinding.tvName.text  = transaction?.transactionName

                if (transaction?.transactionType == "Deposit") {
                    itemBinding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.very_light_green))
                    itemBinding.tvAmount.text = "+ ₱ " + DecimalFormat("#,##0.00").format(transaction.amount)
                }
                else if (transaction?.transactionType == "Withdrawal") {
                    itemBinding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_red))
                    itemBinding.tvAmount.text = "- ₱ " + DecimalFormat("#,##0.00").format(transaction.amount)
                }
            }
        }

        override fun onClick(p0: View?) {
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