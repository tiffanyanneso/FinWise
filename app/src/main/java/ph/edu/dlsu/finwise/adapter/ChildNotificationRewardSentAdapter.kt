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
import ph.edu.dlsu.finwise.databinding.ItemNotifEarningReceivedBinding
import ph.edu.dlsu.finwise.databinding.ItemNotifGoalApprovedBinding
import ph.edu.dlsu.finwise.databinding.ItemNotifNewEarningBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.GoalSettingForEditingActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.MarkChoreCompletedActivity
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ChildNotificationRewardSentAdapter : RecyclerView.Adapter<ChildNotificationRewardSentAdapter.ChildNotificationRewardSentViewHolder>{

    private var earningIDArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore

     constructor(context: Context, earningIDArrayList:ArrayList<String>) {
        this.context = context
        this.earningIDArrayList = earningIDArrayList
    }

    override fun getItemCount(): Int {
        return earningIDArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChildNotificationRewardSentAdapter.ChildNotificationRewardSentViewHolder {
        val itemBinding = ItemNotifEarningReceivedBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return ChildNotificationRewardSentViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ChildNotificationRewardSentAdapter.ChildNotificationRewardSentViewHolder,
                                  position: Int) {
        holder.bindGoal(earningIDArrayList[position])
    }

    inner class ChildNotificationRewardSentViewHolder(private val itemBinding: ItemNotifEarningReceivedBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(earningID: String){
            firestore.collection("EarningActivities").document(earningID).get().addOnSuccessListener {
                var earningObject = it.toObject<EarningActivityModel>()
                itemBinding.tvEarningActivityId.text = it.id
                itemBinding.tvChildId.text = earningObject?.childID
                itemBinding.tvEarning.text = earningObject?.activityName
                itemBinding.tvDateReceive.text = SimpleDateFormat("MM/dd/yyyy").format(earningObject?.dateSent!!.toDate())
                itemBinding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(earningObject?.amount)
            }
        }

        override fun onClick(p0: View?) {
        }
    }
}