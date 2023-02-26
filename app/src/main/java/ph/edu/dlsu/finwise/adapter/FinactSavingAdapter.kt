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
import ph.edu.dlsu.finwise.databinding.ItemFinactSavingBinding
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentSettingAGoalActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class FinactSavingAdapter : RecyclerView.Adapter<FinactSavingAdapter.ChildGoalViewHolder>{

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
    ): FinactSavingAdapter.ChildGoalViewHolder {
        val itemBinding = ItemFinactSavingBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return ChildGoalViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: FinactSavingAdapter.ChildGoalViewHolder,
                                  position: Int) {
        holder.bindGoal(goalsIDArrayList[position])
    }

    inner class ChildGoalViewHolder(private val itemBinding: ItemFinactSavingBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
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
                val date = SimpleDateFormat("MM/dd/yyyy").format(goal?.targetDate?.toDate())
                itemBinding.tvTargetDate.text = date.toString()
                itemBinding.tvProgressAmount.text = "₱ " +  DecimalFormat("#,##0.00").format(goal?.currentSavings) + " of ₱ " + DecimalFormat("#,##0.00").format(goal?.targetAmount)
                itemBinding.progressBar.progress = (goal?.currentSavings!! / goal?.targetAmount!! * 100).toInt()
            }
        }

        override fun onClick(p0: View?) {
            var bundle = Bundle()
            var financialGoalID = itemBinding.tvGoalId.text.toString()
            bundle.putString ("financialGoalID", financialGoalID)
            var viewGoal = Intent(context, ViewGoalActivity::class.java)
            viewGoal.putExtras(bundle)
            viewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(viewGoal)
        }
    }
}