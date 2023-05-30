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
import ph.edu.dlsu.finwise.databinding.ItemNotifGoalApprovedBinding
import ph.edu.dlsu.finwise.databinding.ItemNotifNewEarningBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.GoalSettingForEditingActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.MarkChoreCompletedActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.model.*
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningSendMoneyActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.NotificationEarningFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.NotificationGoalFragment
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ChildNotificationSummaryGoalAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private val VIEW_FOR_EDITING = 0
    private val VIEW_GOAL_APPROVED = 1

    private var goalArrayList = ArrayList<NotificationGoalFragment.GoalNotificationObject>()
    private var context: Context

    private var firestore = Firebase.firestore

     constructor(context: Context, goalArrayList:ArrayList<NotificationGoalFragment.GoalNotificationObject>) {
        this.context = context
        this.goalArrayList = goalArrayList
    }

    override fun getItemCount(): Int {
        return goalArrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = goalArrayList[position]

        return when (item.type) {
            "For Editing" -> VIEW_FOR_EDITING
            "Goal Approved" -> VIEW_GOAL_APPROVED
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_FOR_EDITING -> {
                val itemBinding = ItemNotifGoalApprovedBinding
                    .inflate(
                        LayoutInflater.from(parent.context),
                        parent, false)
                ChildNotificationGoalEditViewHolder(itemBinding)
            }
            VIEW_GOAL_APPROVED -> {
                val itemBinding = ItemNotifGoalApprovedBinding
                    .inflate(
                        LayoutInflater.from(parent.context),
                        parent, false)
                ChildNotificationGoalRatedViewHolder(itemBinding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder,
                                  position: Int) {
        when (holder) {
            is ChildNotificationGoalEditViewHolder -> {
                // Bind data for item layout 1
                holder.bind(goalArrayList[position])
            }
            is ChildNotificationGoalRatedViewHolder -> {
                // Bind data for item layout 2
                holder.bind(goalArrayList[position])
            }
        }
    }

    inner class ChildNotificationGoalEditViewHolder(private val itemBinding: ItemNotifGoalApprovedBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(goalNotifObject: NotificationGoalFragment.GoalNotificationObject){
            firestore.collection("FinancialGoals").document(goalNotifObject.goalID).get().addOnSuccessListener{ document ->
                var goal = document.toObject<FinancialGoals>()
                itemBinding.tvGoalId.text = goalNotifObject.goalID
                itemBinding.tvChildId.text = goal?.childID
                itemBinding.tvGoal.text = goal?.goalName
                itemBinding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(goal?.targetDate!!.toDate())
                itemBinding.tvTargetAmount.text = "₱ " + DecimalFormat("#,##0.00").format(goal?.targetAmount)
            }
        }

        override fun onClick(p0: View?) {
            var viewGoal = Intent(context, GoalSettingForEditingActivity::class.java)
            var bundle = Bundle()
            bundle.putString("financialGoalID", itemBinding.tvGoalId.text.toString())
            bundle.putString("childID", itemBinding.tvChildId.text.toString())
            viewGoal.putExtras(bundle)
            viewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(viewGoal)
        }
    }

    inner class ChildNotificationGoalRatedViewHolder(private val itemBinding: ItemNotifGoalApprovedBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(goalNotifObject: NotificationGoalFragment.GoalNotificationObject){
            firestore.collection("FinancialGoals").document(goalNotifObject.goalID).get().addOnSuccessListener{ document ->
                var goal = document.toObject<FinancialGoals>()
                itemBinding.tvGoalId.text = goalNotifObject.goalID
                itemBinding.tvChildId.text = goal?.childID
                itemBinding.tvGoal.text = goal?.goalName
                itemBinding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(goal?.targetDate!!.toDate())
                itemBinding.tvTargetAmount.text = "₱ " + DecimalFormat("#,##0.00").format(goal?.targetAmount)
            }
        }

        override fun onClick(p0: View?) {
            var viewGoal = Intent(context, ViewGoalActivity::class.java)
            var bundle = Bundle()
            bundle.putString("financialGoalID", itemBinding.tvGoalId.text.toString())
            bundle.putString("childID", itemBinding.tvChildId.text.toString())
            viewGoal.putExtras(bundle)
            viewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(viewGoal)
        }
    }
}