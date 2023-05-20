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
import ph.edu.dlsu.finwise.databinding.ItemNotifEarningToReviewBinding
import ph.edu.dlsu.finwise.databinding.ItemNotifGoalBinding
import ph.edu.dlsu.finwise.databinding.ItemParentPendingGoalBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.BudgetActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.GoalSettingForEditingActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentSettingAGoalActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalDetails
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ParentPendingGoalsAdapter : RecyclerView.Adapter<ParentPendingGoalsAdapter.ParentPendingGoalsViewHolder>{

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
    ): ParentPendingGoalsAdapter.ParentPendingGoalsViewHolder {
        val itemBinding = ItemParentPendingGoalBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return ParentPendingGoalsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ParentPendingGoalsAdapter.ParentPendingGoalsViewHolder,
                                  position: Int) {
        holder.bindGoal(goalsIDArrayList[position])
    }

    inner class ParentPendingGoalsViewHolder(private val itemBinding: ItemParentPendingGoalBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(goalID: String){
            firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener{ document ->
                var goal = document.toObject<FinancialGoals>()
                itemBinding.tvGoalId.text = document.id
                itemBinding.tvChildId.text = goal?.childID
                itemBinding.tvGoalName.text = goal?.goalName
                itemBinding.tvAmount.text =  "₱ " + DecimalFormat("#,##0.00").format(goal?.targetAmount)
                itemBinding.tvDate.text= SimpleDateFormat("MM/dd/yyyy").format(goal?.targetDate?.toDate()).toString()

                firestore.collection("Users").document(goal?.childID!!).get().addOnSuccessListener {
                    itemBinding.tvChildName.text = it.toObject<Users>()!!.firstName
                }
            }
        }

        override fun onClick(p0: View?) {
            var rateGoal = Intent(context,ParentSettingAGoalActivity::class.java)
            var bundle=  Bundle()
            bundle.putString("financialGoalID", itemBinding.tvGoalId.text.toString())
            bundle.putString("childID", itemBinding.tvChildId.text.toString())
            rateGoal.putExtras(bundle)
            rateGoal.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(rateGoal)
        }
    }
}