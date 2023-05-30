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
import ph.edu.dlsu.finwise.databinding.ItemNotifEarningToReviewBinding
import ph.edu.dlsu.finwise.databinding.ItemNotifNewEarningBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.MarkChoreCompletedActivity
import ph.edu.dlsu.finwise.model.*
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningSendMoneyActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.NotificationEarningFragment
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ChildNotificationSummaryEarningAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private val VIEW_NEW_EARNING = 0
    private val VIEW_EARNING_SENT = 1

    private var earningArrayList = ArrayList<NotificationEarningFragment.EarningNotificationObject>()
    private var context: Context

    private var firestore = Firebase.firestore

     constructor(context: Context, earningArrayList:ArrayList<NotificationEarningFragment.EarningNotificationObject>) {
        this.context = context
        this.earningArrayList = earningArrayList
    }

    override fun getItemCount(): Int {
        return earningArrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = earningArrayList[position]

        return when (item.type) {
            "New Earning" -> VIEW_NEW_EARNING
            "Reward Sent" -> VIEW_EARNING_SENT
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_NEW_EARNING -> {
                val itemBinding = ItemNotifNewEarningBinding
                    .inflate(
                        LayoutInflater.from(parent.context),
                        parent, false)
                ChildNotificationNewEarningViewHolder(itemBinding)
            }
            VIEW_EARNING_SENT -> {
                val itemBinding = ItemNotifEarningReceivedBinding
                    .inflate(
                        LayoutInflater.from(parent.context),
                        parent, false)
                ChildNotificationRewardSentViewHolder(itemBinding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder,
                                  position: Int) {
        when (holder) {
            is ChildNotificationNewEarningViewHolder -> {
                // Bind data for item layout 1
                holder.bind(earningArrayList[position])
            }
            is ChildNotificationRewardSentViewHolder -> {
                // Bind data for item layout 2
                holder.bind(earningArrayList[position])
            }
        }
    }

    inner class ChildNotificationNewEarningViewHolder(private val itemBinding: ItemNotifNewEarningBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(earningNotifObject: NotificationEarningFragment.EarningNotificationObject){
            firestore.collection("EarningActivities").document(earningNotifObject.earningID).get().addOnSuccessListener {
                var earningObject = it.toObject<EarningActivityModel>()
                itemBinding.tvEarningActivityId.text = it.id
                itemBinding.tvChildId.text = earningObject?.childID
                itemBinding.tvEarning.text = earningObject?.activityName
                itemBinding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(earningObject?.targetDate!!.toDate())
                itemBinding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(earningObject.amount)
            }
        }

        override fun onClick(p0: View?) {
            var income = Intent (context, MarkChoreCompletedActivity::class.java)
            var bundle = Bundle()
            bundle.putString("earningActivityID", itemBinding.tvEarningActivityId.text.toString())
            bundle.putString("childID", itemBinding.tvChildId.text.toString())
            income.putExtras(bundle)
            income.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(income)
        }
    }

    inner class ChildNotificationRewardSentViewHolder(private val itemBinding: ItemNotifEarningReceivedBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(earningNotifObject: NotificationEarningFragment.EarningNotificationObject){
            firestore.collection("EarningActivities").document(earningNotifObject.earningID).get().addOnSuccessListener {
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