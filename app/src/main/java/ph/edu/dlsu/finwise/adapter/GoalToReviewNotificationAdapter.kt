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
import ph.edu.dlsu.finwise.databinding.ItemNotifGoalBinding
import ph.edu.dlsu.finwise.databinding.ItemNotifGoalToRateBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.GoalSettingForEditingActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentSettingAGoalActivity

class GoalToReviewNotificationAdapter : RecyclerView.Adapter<GoalToReviewNotificationAdapter.GoalToReviewNotificationViewHolder>{

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
    ): GoalToReviewNotificationAdapter.GoalToReviewNotificationViewHolder {
        val itemBinding = ItemNotifGoalToRateBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return GoalToReviewNotificationViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: GoalToReviewNotificationAdapter.GoalToReviewNotificationViewHolder,
                                  position: Int) {
        holder.bindGoal(goalsIDArrayList[position])
    }

    inner class GoalToReviewNotificationViewHolder(private val itemBinding: ItemNotifGoalToRateBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(goalID: String){
            firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener{ document ->
                var goal = document.toObject<FinancialGoals>()
                itemBinding.tvGoalId.text = goalID
                itemBinding.tvGoal.text = goal?.goalName
                itemBinding.tvChildId.text = goal?.childID

                firestore.collection("Users").document(goal?.childID!!).get().addOnSuccessListener {
                    itemBinding.tvChildName.text = it.toObject<Users>()?.firstName
                }
            }
        }

        override fun onClick(p0: View?) {
            var intent = Intent(context, ParentSettingAGoalActivity::class.java)
            var bundle = Bundle()
            bundle.putString("financialGoalID", itemBinding.tvGoalId.text.toString())
            bundle.putString("childID", itemBinding.tvChildId.text.toString())
            intent.putExtras(bundle)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}