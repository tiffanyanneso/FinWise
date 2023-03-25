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
import ph.edu.dlsu.finwise.databinding.ItemEarningPendingConfirmationBinding
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningSendMoneyActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class EarningPendingConfirmationAdapter : RecyclerView.Adapter<EarningPendingConfirmationAdapter.EarningCompletedViewHolder>{

    private var earningPendingArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore


    constructor(context: Context, earningPendingArrayList:ArrayList<String>) {
        this.context = context
        this.earningPendingArrayList = earningPendingArrayList
    }

    override fun getItemCount(): Int {
        return earningPendingArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EarningPendingConfirmationAdapter.EarningCompletedViewHolder {
        val itemBinding = ItemEarningPendingConfirmationBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return EarningCompletedViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: EarningPendingConfirmationAdapter.EarningCompletedViewHolder,
                                  position: Int) {
        holder.bindItem(earningPendingArrayList[position])
    }

    inner class EarningCompletedViewHolder(private val itemBinding: ItemEarningPendingConfirmationBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(earningID: String){
            firestore.collection("EarningActivities").document(earningID).get().addOnSuccessListener {
                var earning = it.toObject<EarningActivityModel>()
                itemBinding.tvActivity.text = earning?.activityName
                itemBinding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(earning?.amount)
                // itemBinding.tvDuration.text = earning?.requiredTime.toString() + " minutes"
                itemBinding.tvSource.text = earning?.depositTo
                itemBinding.tvEarningActivityId.text = earningID
                itemBinding.tvChildId.text = earning?.childID
                itemBinding.tvPaymentType.text = earning?.paymentType
                itemBinding.tvDateCompleted.text = SimpleDateFormat("MM/dd/yyyy").format(earning?.dateCompleted!!.toDate())

                if (earning?.depositTo == "Financial Goal") {
                    firestore.collection("FinancialActivities").document(earning?.savingActivityID!!).get().addOnSuccessListener {
                        var goalID = it.toObject<FinancialActivities>()!!.financialGoalID
                        firestore.collection("FinancialGoals").document(goalID!!).get().addOnSuccessListener {
                            itemBinding.tvGoalName.text = it.toObject<FinancialGoals>()!!.goalName
                        }
                    }
                } else
                    itemBinding.tvGoalName.visibility = View.GONE
            }
        }

        override fun onClick(p0: View?) {
            val confirm = Intent (context, EarningSendMoneyActivity::class.java)
            val bundle = Bundle()
            bundle.putString("earningActivityID", itemBinding.tvEarningActivityId.text.toString())
            bundle.putString("childID", itemBinding.tvChildId.text.toString())
            bundle.putString("paymentType", itemBinding.tvPaymentType.text.toString())
            confirm.putExtras(bundle)
            confirm.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(confirm)
        }
    }
}