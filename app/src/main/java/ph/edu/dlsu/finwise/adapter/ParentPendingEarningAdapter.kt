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
import ph.edu.dlsu.finwise.model.*
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningSendMoneyActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ParentPendingEarningAdapter : RecyclerView.Adapter<ParentPendingEarningAdapter.ParentPendingEarningViewHolder>{

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
    ): ParentPendingEarningAdapter.ParentPendingEarningViewHolder {
        val itemBinding = ItemNotifEarningToReviewBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return ParentPendingEarningViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ParentPendingEarningAdapter.ParentPendingEarningViewHolder,
                                  position: Int) {
        holder.bindGoal(earningIDArrayList[position])
    }

    inner class ParentPendingEarningViewHolder(private val itemBinding: ItemNotifEarningToReviewBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(earningID: String){
            firestore.collection("EarningActivities").document(earningID).get().addOnSuccessListener {
                var earning = it.toObject<EarningActivityModel>()!!
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