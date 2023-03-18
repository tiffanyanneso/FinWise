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
import ph.edu.dlsu.finwise.databinding.ItemFinactSavingBinding
import ph.edu.dlsu.finwise.databinding.ItemFinactSpendingBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.BudgetActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentSettingAGoalActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class FinactSpendingAdapter : RecyclerView.Adapter<FinactSpendingAdapter.FinactSpendingViewHolder>{

    private var goalsIDArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore

    private lateinit var childID:String
    private lateinit var goalStatus:String

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
    ): FinactSpendingAdapter.FinactSpendingViewHolder {
        val itemBinding = ItemFinactSpendingBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return FinactSpendingViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: FinactSpendingAdapter.FinactSpendingViewHolder,
                                  position: Int) {
        holder.bindGoal(goalsIDArrayList[position])
    }

    inner class FinactSpendingViewHolder(private val itemBinding: ItemFinactSpendingBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(goalID: String){
            firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener{ document ->

                var goal = document.toObject<FinancialGoals>()
                goalStatus = goal?.status.toString()
                childID = goal?.childID.toString()
                itemBinding.tvGoal.text = goal?.goalName

                firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", goalID).get().addOnSuccessListener { results ->
                    for (activity in results) {
                        var activityObject = activity.toObject<FinancialActivities>()
                        if (activityObject.financialActivityName == "Saving")
                            itemBinding.tvSavingActivityId.text = activity.id
                        else if (activityObject.financialActivityName == "Budgeting")
                            itemBinding.tvBudgetingActivityId.text = activity.id
                        else if (activityObject.financialActivityName == "Spending")
                            itemBinding.tvSpendingActivityId.text = activity.id
                    }
                }
            }
        }

        override fun onClick(p0: View?) {
            var bundle = Bundle()
            bundle.putString("savingActivityID",  itemBinding.tvSavingActivityId.text.toString())
            bundle.putString("budgetingActivityID", itemBinding.tvBudgetingActivityId.text.toString())
            bundle.putString("spendingActivityID", itemBinding.tvSpendingActivityId.text.toString())
            bundle.putString("childID", childID)
            var viewGoal = Intent(context, BudgetActivity::class.java)
            viewGoal.putExtras(bundle)
            viewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(viewGoal)
        }
    }
}