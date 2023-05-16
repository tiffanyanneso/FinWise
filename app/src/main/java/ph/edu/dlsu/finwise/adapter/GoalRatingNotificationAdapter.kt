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
import ph.edu.dlsu.finwise.databinding.ItemFinactBudgetingBinding
import ph.edu.dlsu.finwise.databinding.ItemFinactDisapprovedBinding
import ph.edu.dlsu.finwise.databinding.ItemFinactSavingBinding
import ph.edu.dlsu.finwise.databinding.ItemFinactSpendingBinding
import ph.edu.dlsu.finwise.databinding.ItemNotifGoalBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.BudgetActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.GoalSettingForEditingActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentSettingAGoalActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalDetails
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class GoalRatingNotificationAdapter : RecyclerView.Adapter<GoalRatingNotificationAdapter.GoalRatingNotificationViewHolder>{

    private var goalsIDArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore

     constructor(context: Context, goalsIDArrayList:ArrayList<String>) {
        this.context = context
        this.goalsIDArrayList = goalsIDArrayList
    }

    override fun getItemCount(): Int {
        return goalsIDArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GoalRatingNotificationAdapter.GoalRatingNotificationViewHolder {
        val itemBinding = ItemNotifGoalBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return GoalRatingNotificationViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: GoalRatingNotificationAdapter.GoalRatingNotificationViewHolder,
                                  position: Int) {
        holder.bindGoal(goalsIDArrayList[position])
    }

    inner class GoalRatingNotificationViewHolder(private val itemBinding: ItemNotifGoalBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(goalID: String){
            firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener{ document ->
                var goal = document.toObject<FinancialGoals>()
                itemBinding.tvGoalId.text = goalID
                itemBinding.tvGoal.text = goal?.goalName
                itemBinding.tvStatus.text = goal?.status
                itemBinding.tvChildId.text = goal?.childID
            }
        }

        override fun onClick(p0: View?) {
            var status = itemBinding.tvStatus.text
            var bundle = Bundle()
            bundle.putString("financialGoalID", itemBinding.tvGoalId.text.toString())
            bundle.putString("childID", itemBinding.tvChildId.text.toString())
            if (status == "For Editing") {
                var reviewGoal = Intent(context, GoalSettingForEditingActivity::class.java)
                reviewGoal.putExtras(bundle)
                reviewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(reviewGoal)
            } else if (status == "In Progress") {
                var reviewGoal = Intent(context, ViewGoalActivity::class.java)
                reviewGoal.putExtras(bundle)
                reviewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(reviewGoal)
            }
        }
    }
}