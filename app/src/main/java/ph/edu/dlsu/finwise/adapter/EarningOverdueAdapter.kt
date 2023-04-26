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
import ph.edu.dlsu.finwise.databinding.ItemEarningBinding
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.OverdueChoreActivity
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class EarningOverdueAdapter : RecyclerView.Adapter<EarningOverdueAdapter.EarningToDoViewHolder> {

    private var earningToDoArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore


    constructor(context: Context, earningToDoArrayList: ArrayList<String>) {
        this.context = context
        this.earningToDoArrayList = earningToDoArrayList
    }

    override fun getItemCount(): Int {
        return earningToDoArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EarningOverdueAdapter.EarningToDoViewHolder {
        val itemBinding = ItemEarningBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        return EarningToDoViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: EarningOverdueAdapter.EarningToDoViewHolder, position: Int) {
        holder.bindItem(earningToDoArrayList[position])
    }

    inner class EarningToDoViewHolder(private val itemBinding: ItemEarningBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(earningID: String) {
            itemBinding.tvEarningActivityId.text = earningID
            firestore.collection("EarningActivities").document(earningID).get().addOnSuccessListener {
                var earning = it.toObject<EarningActivityModel>()
                itemBinding.tvActivity.text = earning?.activityName
                itemBinding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(earning?.amount)
                itemBinding.tvDuration.text = earning?.requiredTime.toString() + " minutes"
                itemBinding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(earning?.targetDate!!.toDate()).toString()
                itemBinding.tvSource.text = earning?.depositTo
                itemBinding.tvChildId.text = earning?.childID

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
            var income = Intent (context, OverdueChoreActivity::class.java)
            var bundle = Bundle()
            bundle.putString("earningActivityID", itemBinding.tvEarningActivityId.text.toString())
            bundle.putString("childID", itemBinding.tvChildId.text.toString())
            income.putExtras(bundle)
            income.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(income)
        }
    }
}