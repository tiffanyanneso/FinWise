package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemEarningPendingConfirmationBinding
import ph.edu.dlsu.finwise.databinding.ItemEarningRequestBinding
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningSendMoneyActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.NewEarningActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class EarningChoreRequestAdapter : RecyclerView.Adapter<EarningChoreRequestAdapter.EarningCompletedViewHolder>{

    private var earningPendingArrayList = ArrayList<String>()
    private var context: Context

    private var requestClick: ChoreClick

    private var firestore = Firebase.firestore


    constructor(context: Context, earningPendingArrayList:ArrayList<String>, requestClick: ChoreClick) {
        this.context = context
        this.earningPendingArrayList = earningPendingArrayList
        this.requestClick = requestClick
    }

    override fun getItemCount(): Int {
        return earningPendingArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EarningChoreRequestAdapter.EarningCompletedViewHolder {
        val itemBinding = ItemEarningRequestBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return EarningCompletedViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: EarningChoreRequestAdapter.EarningCompletedViewHolder,
                                  position: Int) {
        holder.bindItem(earningPendingArrayList[position])
    }

    inner class EarningCompletedViewHolder(private val itemBinding: ItemEarningRequestBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(earningID: String){
            firestore.collection("EarningActivities").document(earningID).get().addOnSuccessListener {
                itemBinding.tvChildId.text = it.toObject<EarningActivityModel>()?.childID!!
                firestore.collection("Users").document(it.toObject<EarningActivityModel>()?.childID!!).get().addOnSuccessListener {
                    itemBinding.tvEarningActivityId.text = earningID
                    itemBinding.tvActivity.text = "Chore Request"
                    itemBinding.tVChildName.text = it.toObject<Users>()?.firstName
                }
            }
        }

        override fun onClick(p0: View?) {
           requestClick.clickRequest(itemBinding.tvEarningActivityId.text.toString(), itemBinding.tvChildId.text.toString())
        }
    }

    interface ChoreClick {
        fun clickRequest(earningID:String, childID:String)
    }
}