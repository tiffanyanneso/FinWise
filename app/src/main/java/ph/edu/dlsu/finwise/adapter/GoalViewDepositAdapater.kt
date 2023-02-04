package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ItemGoalBinding
import ph.edu.dlsu.finwise.databinding.ItemGoalTransactionBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.SavingActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class GoalViewDepositAdapater : RecyclerView.Adapter<GoalViewDepositAdapater.GoalViewDepositViewHolder> {

    private var depositTransactions = ArrayList<Transactions>()
    private var context: Context

    constructor(context: Context, depositTransactions:ArrayList<Transactions>) {
        this.context = context
        this.depositTransactions = depositTransactions
    }

    override fun getItemCount(): Int {
        return depositTransactions.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GoalViewDepositAdapater.GoalViewDepositViewHolder {
        val itemBinding = ItemGoalTransactionBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return GoalViewDepositViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: GoalViewDepositAdapater.GoalViewDepositViewHolder,
                                  position: Int) {
        holder.bindGoal(depositTransactions[position])
    }

    inner class GoalViewDepositViewHolder(private val itemBinding: ItemGoalTransactionBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var transactions  = Transactions()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(transaction: Transactions){
            val date =  SimpleDateFormat("MM/dd/yyyy").format(transaction.date?.toDate())
            itemBinding.tvTransactionDate.text = date.toString()
            if (transaction.transactionType == "Deposit") {
                itemBinding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.very_light_green))
                itemBinding.tvAmount.text = "+ ₱ " + DecimalFormat("#,##0.00").format(transaction.amount)
            }
            else if (transaction.transactionType == "Withdrawal") {
                itemBinding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_red))
                itemBinding.tvAmount.text = "- ₱ " + DecimalFormat("#,##0.00").format(transaction.amount)
            }

        }

        override fun onClick(p0: View?) {

        }
    }
}