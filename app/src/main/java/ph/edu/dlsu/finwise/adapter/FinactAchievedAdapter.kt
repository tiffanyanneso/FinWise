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
import ph.edu.dlsu.finwise.databinding.ItemFinactAchievedBinding
import ph.edu.dlsu.finwise.databinding.ItemFinactSavingBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentSettingAGoalActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class FinactAchievedAdapter : RecyclerView.Adapter<FinactAchievedAdapter.FinactAchievedViewHolder>{

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
    ): FinactAchievedAdapter.FinactAchievedViewHolder {
        val itemBinding = ItemFinactAchievedBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return FinactAchievedViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: FinactAchievedAdapter.FinactAchievedViewHolder,
                                  position: Int) {
        holder.bindGoal(goalsIDArrayList[position])
    }

    inner class FinactAchievedViewHolder(private val itemBinding: ItemFinactAchievedBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(goalID: String){
            firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener{ document ->

                var goal = document.toObject<FinancialGoals>()
                itemBinding.tvGoalId.text = document.id
                goalStatus = goal?.status.toString()
                childID = goal?.childID.toString()
                itemBinding.tvGoal.text = goal?.goalName
                // convert timestamp to date
                val date = SimpleDateFormat("MM/dd/yyyy").format(goal?.dateCompleted?.toDate())
                itemBinding.tvDateAchieved.text = "Date Achieved: " + date.toString()
                itemBinding.tvTargetAmount.text = "₱ " + DecimalFormat("#,##0.00").format(goal?.currentSavings)


            }
        }

        override fun onClick(p0: View?) {
            var bundle = Bundle()
            var financialGoalID = itemBinding.tvGoalId.text.toString()
            bundle.putString ("financialGoalID", financialGoalID)
            bundle.putString ("childID", childID)
            var viewGoal = Intent(context, ViewGoalActivity::class.java)
            viewGoal.putExtras(bundle)
            viewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(viewGoal)

        }
    }
}