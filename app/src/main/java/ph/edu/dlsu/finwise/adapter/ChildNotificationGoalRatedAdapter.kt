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
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ChildNotificationGoalRatedAdapter : RecyclerView.Adapter<ChildNotificationGoalRatedAdapter.ChildNotificationGoalRatedViewHolder>{

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
    ): ChildNotificationGoalRatedAdapter.ChildNotificationGoalRatedViewHolder {
        val itemBinding = ItemNotifGoalApprovedBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return ChildNotificationGoalRatedViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ChildNotificationGoalRatedAdapter.ChildNotificationGoalRatedViewHolder,
                                  position: Int) {
        holder.bindGoal(goalsIDArrayList[position])
    }

    inner class ChildNotificationGoalRatedViewHolder(private val itemBinding: ItemNotifGoalApprovedBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(goalID: String){
            firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener{ document ->
                var goal = document.toObject<FinancialGoals>()
                itemBinding.tvGoalId.text = goalID
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