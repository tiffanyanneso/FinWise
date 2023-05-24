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
import ph.edu.dlsu.finwise.databinding.ItemNotifEarningToReviewBinding
import ph.edu.dlsu.finwise.databinding.ItemNotifGoalBinding
import ph.edu.dlsu.finwise.databinding.ItemNotifGoalToRateBinding
import ph.edu.dlsu.finwise.databinding.ItemNotifTransactionBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.GoalSettingForEditingActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningSendMoneyActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentSettingAGoalActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class TransactionNotifAdapter : RecyclerView.Adapter<TransactionNotifAdapter.TransactionNotifViewHolder>{

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
    ): TransactionNotifAdapter.TransactionNotifViewHolder {
        val itemBinding = ItemNotifTransactionBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return TransactionNotifViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: TransactionNotifAdapter.TransactionNotifViewHolder,
                                  position: Int) {
        holder.bindGoal(transactionIDArrayList[position])
    }

    inner class TransactionNotifViewHolder(private val itemBinding: ItemNotifTransactionBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(transactionID: String){
            firestore.collection("Transactions").document(transactionID).get().addOnSuccessListener { transaction ->
                var transactionObject = transaction.toObject<Transactions>()
                itemBinding.tvTransactionId.text = transaction.id
                itemBinding.tvName.text = transactionObject?.transactionName
                itemBinding.tvAmount.text = "₱ " +  DecimalFormat("#,##0.00").format(transactionObject?.amount)
                itemBinding.tvDate.text = SimpleDateFormat("MM/dd/yyyy").format(transactionObject?.date?.toDate())
                firestore.collection("Users").document(transactionObject?.userID!!).get().addOnSuccessListener {
                    itemBinding.tvChildName.text = it.toObject<Users>()?.firstName
                }
            }
        }

        override fun onClick(p0: View?) {

        }
    }
}