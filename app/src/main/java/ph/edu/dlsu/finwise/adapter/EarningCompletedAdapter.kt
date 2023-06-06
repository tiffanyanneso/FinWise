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
import ph.edu.dlsu.finwise.databinding.ItemEarningCompletedBinding
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningSendMoneyActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class EarningCompletedAdapter : RecyclerView.Adapter<EarningCompletedAdapter.EarningCompletedViewHolder>{

    private var earningCompletedArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore


    constructor(context: Context, earningCompletedArrayList:ArrayList<String>) {
        this.context = context
        this.earningCompletedArrayList = earningCompletedArrayList
    }

    override fun getItemCount(): Int {
        return earningCompletedArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EarningCompletedAdapter.EarningCompletedViewHolder {
        val itemBinding = ItemEarningCompletedBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return EarningCompletedViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: EarningCompletedAdapter.EarningCompletedViewHolder,
                                  position: Int) {
        holder.bindItem(earningCompletedArrayList[position])
    }

    inner class EarningCompletedViewHolder(private val itemBinding: ItemEarningCompletedBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(earningID: String){
            firestore.collection("EarningActivities").document(earningID).get().addOnSuccessListener {
                var earning = it.toObject<EarningActivityModel>()
                itemBinding.tvEarningId.text = it.id
                itemBinding.tvChildId.text = earning?.childID
                itemBinding.tvActivity.text = earning?.activityName
                itemBinding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(earning?.amount)
                itemBinding.tvDuration.text = earning?.requiredTime.toString() + " minutes"
                itemBinding.tvSource.text = earning?.depositTo
                itemBinding.tvFinishDate.text = SimpleDateFormat("MM/dd/yyyy").format(earning?.dateCompleted!!.toDate())

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
            bundle.putString("earningActivityID", itemBinding.tvEarningId.text.toString())
            bundle.putString("childID", itemBinding.tvChildId.text.toString())
            confirm.putExtras(bundle)
            confirm.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(confirm)
        }
    }
}