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
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.databinding.ItemGoalBinding
import ph.edu.dlsu.finwise.model.FinancialGoals

class ChildGoalAdapter : RecyclerView.Adapter<ChildGoalAdapter.ChildGoalViewHolder>{

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
    ): ChildGoalAdapter.ChildGoalViewHolder {
        val itemBinding = ItemGoalBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return ChildGoalViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ChildGoalAdapter.ChildGoalViewHolder,
                                  position: Int) {
        holder.bindGoal(goalsIDArrayList[position])
    }

    inner class ChildGoalViewHolder(private val itemBinding: ItemGoalBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(goalID: String){
            firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener{ document ->

                var goal = document.toObject<FinancialGoals>()
                itemBinding.tvGoalId.text = document.id
                itemBinding.tvGoal.text = goal?.goalName
                itemBinding.tvTargetDate.text = goal?.targetDate
                itemBinding.tvProgressAmount.text = goal?.currentAmount.toString()  + "/" + goal?.targetAmount.toString()
                /*for (goalSnapshot in documents) {
                    val goalID = goalSnapshot.id
                    goalIDArrayList.add(goalID!!)
                }*/
            }
        }

        override fun onClick(p0: View?) {
            var viewGoal = Intent(context, ViewGoalActivity::class.java)
            var bundle = Bundle()

            var goalID = itemBinding.tvGoalId.text.toString()
            bundle.putString ("goalID", goalID)
            viewGoal.putExtras(bundle)
            viewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(viewGoal)
        }
    }
}