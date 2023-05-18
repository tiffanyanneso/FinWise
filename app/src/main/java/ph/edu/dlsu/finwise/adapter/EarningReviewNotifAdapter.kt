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
import ph.edu.dlsu.finwise.databinding.ItemNotifEarningToReviewBinding
import ph.edu.dlsu.finwise.databinding.ItemNotifGoalBinding
import ph.edu.dlsu.finwise.databinding.ItemNotifGoalToRateBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.GoalSettingForEditingActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningSendMoneyActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentSettingAGoalActivity
import java.text.DecimalFormat

class EarningReviewNotifAdapter : RecyclerView.Adapter<EarningReviewNotifAdapter.EarningReviewNotifViewHolder>{

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
    ): EarningReviewNotifAdapter.EarningReviewNotifViewHolder {
        val itemBinding = ItemNotifEarningToReviewBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return EarningReviewNotifViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: EarningReviewNotifAdapter.EarningReviewNotifViewHolder,
                                  position: Int) {
        holder.bindGoal(earningIDArrayList[position])
    }

    inner class EarningReviewNotifViewHolder(private val itemBinding: ItemNotifEarningToReviewBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(earningID: String){
            firestore.collection("EarningActivities").document(earningID).get().addOnSuccessListener{ document ->
                var earning = document.toObject<EarningActivityModel>()!!
                itemBinding.tvEarningActivityId.text = earningID
                itemBinding.tvEarning.text = earning.activityName
                itemBinding.tvAmount.text =  "₱ " +  DecimalFormat("#,##0.00").format(earning.amount)
                firestore.collection("Users").document(earning.childID!!).get().addOnSuccessListener {
                    itemBinding.tvChildName.text = it.toObject<Users>()?.firstName
                }
            }
        }

        override fun onClick(p0: View?) {
            var intent = Intent(context, EarningSendMoneyActivity::class.java)
            var bundle = Bundle()
            bundle.putString("earningActivityID", itemBinding.tvEarningActivityId.text.toString())
            bundle.putString("childID", itemBinding.tvChildId.text.toString())
            intent.putExtras(bundle)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}