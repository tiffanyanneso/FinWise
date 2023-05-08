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
import ph.edu.dlsu.finwise.databinding.ItemFinactGoalSettingBinding
import ph.edu.dlsu.finwise.databinding.ItemFinactSavingBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.GoalSettingForEditingActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentSettingAGoalActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalDetailsTabbedActivity
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class FinactGoalSettingAdapter : RecyclerView.Adapter<FinactGoalSettingAdapter.FinactGoalSettingViewHolder>{

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
    ): FinactGoalSettingAdapter.FinactGoalSettingViewHolder {
        val itemBinding = ItemFinactGoalSettingBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return FinactGoalSettingViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: FinactGoalSettingAdapter.FinactGoalSettingViewHolder,
                                  position: Int) {
        holder.bindGoal(goalsIDArrayList[position])
    }

    inner class FinactGoalSettingViewHolder(private val itemBinding: ItemFinactGoalSettingBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(goalID: String){
            firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener{ document ->
                var goal = document.toObject<FinancialGoals>()
                itemBinding.tvGoalId.text = document.id
                childID = goal?.childID.toString()
                itemBinding.tvGoal.text = goal?.goalName
                // convert timestamp to date
                val date = SimpleDateFormat("MM/dd/yyyy").format(goal?.targetDate?.toDate())
                itemBinding.tvTargetDate.text = date.toString()
                itemBinding.tvTargetAmount.text = "₱ " + DecimalFormat("#,##0.00").format(goal?.targetAmount)
                itemBinding.tvStatus.text = goal?.status.toString()
            }
        }

        override fun onClick(p0: View?) {
            var bundle = Bundle()
            var financialGoalID = itemBinding.tvGoalId.text.toString()
            bundle.putString ("financialGoalID", financialGoalID)
            bundle.putString ("childID", childID)

            var status = itemBinding.tvStatus.text.toString()
            var reviewGoal = Intent(context, GoalSettingForEditingActivity::class.java)
            reviewGoal.putExtras(bundle)
            reviewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(reviewGoal)
//            if (status == "For Review") {
//                var reviewGoal = Intent(context, ParentSettingAGoalActivity::class.java)
//                reviewGoal.putExtras(bundle)
//                reviewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                context.startActivity(reviewGoal)
//            } else if (status == "For Editing") {
//                var reviewGoal = Intent(context, GoalSettingForEditingActivity::class.java)
//                reviewGoal.putExtras(bundle)
//                reviewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                context.startActivity(reviewGoal)
//            }

        }
    }
}