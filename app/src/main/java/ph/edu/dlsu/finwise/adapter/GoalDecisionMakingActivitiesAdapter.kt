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
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.model.DecisionMakingActivities
import ph.edu.dlsu.finwise.model.FinancialGoals

class GoalDecisionMakingActivitiesAdapter: RecyclerView.Adapter<GoalDecisionMakingActivitiesAdapter.GoalDecisionMakingActivitiesViewHolder> {

    private var decisionMakingActivitiesArrayList = ArrayList<DecisionMakingActivities>()
    private lateinit var context: Context

    private var firestore = Firebase.firestore

    public constructor(context: Context, decisionMakingActivitiesArrayList:ArrayList<DecisionMakingActivities>) {
        this.context = context
        this.decisionMakingActivitiesArrayList = decisionMakingActivitiesArrayList
    }

    override fun getItemCount(): Int {
        return decisionMakingActivitiesArrayList.size
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
        holder.bindGoal(decisionMakingActivitiesArrayList[position])
    }

    inner class GoalDecisionMakingActivitiesViewHolder(private val itemBinding: ItemDecisionMakingActivityBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(decisionMakingActivity: DecisionMakingActivities){
            itemBinding.tvName.text = decisionMakingActivity.decisonMakingActivity
        }

        override fun onClick(p0: View?) {

        }
    }
}