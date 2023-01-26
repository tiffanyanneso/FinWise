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
import ph.edu.dlsu.finwise.databinding.ItemDecisionMakingActivityBinding
import ph.edu.dlsu.finwise.databinding.ItemGoalBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.BudgetActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.SavingActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.SpendingActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.model.DecisionMakingActivities
import ph.edu.dlsu.finwise.model.FinancialGoals

class GoalDecisionMakingActivitiesAdapter: RecyclerView.Adapter<GoalDecisionMakingActivitiesAdapter.GoalDecisionMakingActivitiesViewHolder> {

    private var decisionMakingActivitiesIDArrayList = ArrayList<String>()
    private var goalID:String
    private lateinit var context: Context

    private var firestore = Firebase.firestore

    public constructor(context: Context, decisionMakingActivitiesIDArrayList:ArrayList<String>, goalID:String) {
        this.context = context
        this.decisionMakingActivitiesIDArrayList = decisionMakingActivitiesIDArrayList
        this.goalID = goalID
    }

    override fun getItemCount(): Int {
        return decisionMakingActivitiesIDArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GoalDecisionMakingActivitiesAdapter.GoalDecisionMakingActivitiesViewHolder {
        val itemBinding = ItemDecisionMakingActivityBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return GoalDecisionMakingActivitiesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: GoalDecisionMakingActivitiesAdapter.GoalDecisionMakingActivitiesViewHolder,
                                  position: Int) {
        holder.bindGoal(decisionMakingActivitiesIDArrayList[position])
    }

    inner class GoalDecisionMakingActivitiesViewHolder(private val itemBinding: ItemDecisionMakingActivityBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(decisionMakingActivityID: String){
            firestore.collection("DecisionMakingActivities").document(decisionMakingActivityID).get().addOnSuccessListener {
                var decisionActivity = it.toObject<DecisionMakingActivities>()
                itemBinding.tvDecisionActivityId.text= it.id
                itemBinding.tvName.text = decisionActivity?.decisonMakingActivity
            }
        }

        override fun onClick(p0: View?) {
            var decisionActivityName = itemBinding.tvName.text.toString()
            var decisionActivityID = itemBinding.tvDecisionActivityId.text.toString()
            var bundle = Bundle()
            bundle.putString ("decisionMakingActivityID", decisionActivityID)
            bundle.putString("goalID", goalID)
            
            if (decisionActivityName.equals("Setting a Budget")) {
                var budgetActivity = Intent(context, BudgetActivity::class.java)
                budgetActivity.putExtras(bundle)
                budgetActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(budgetActivity)
            }

            if (decisionActivityName.equals("Deciding to Save")) {
                var savingActivity = Intent(context, SavingActivity::class.java)
                savingActivity.putExtras(bundle)
                savingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(savingActivity)
            }

            if (decisionActivityName.equals("Deciding to Spend")) {
                var spendingActivity = Intent(context, SpendingActivity::class.java)
                spendingActivity.putExtras(bundle)
                spendingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(spendingActivity)
            }
            
        }
    }
}