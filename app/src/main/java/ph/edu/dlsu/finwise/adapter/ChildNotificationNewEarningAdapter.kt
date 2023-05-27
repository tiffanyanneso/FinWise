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
import ph.edu.dlsu.finwise.databinding.ItemNotifGoalApprovedBinding
import ph.edu.dlsu.finwise.databinding.ItemNotifNewEarningBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.GoalSettingForEditingActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.MarkChoreCompletedActivity
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ChildNotificationNewEarningAdapter : RecyclerView.Adapter<ChildNotificationNewEarningAdapter.ChildNotificationNewEarningViewHolder>{

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
    ): ChildNotificationNewEarningAdapter.ChildNotificationNewEarningViewHolder {
        val itemBinding = ItemNotifNewEarningBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return ChildNotificationNewEarningViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ChildNotificationNewEarningAdapter.ChildNotificationNewEarningViewHolder,
                                  position: Int) {
        holder.bindGoal(earningIDArrayList[position])
    }

    inner class ChildNotificationNewEarningViewHolder(private val itemBinding: ItemNotifNewEarningBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
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
}