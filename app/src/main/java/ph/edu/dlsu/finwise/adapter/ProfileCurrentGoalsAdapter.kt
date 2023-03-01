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
import ph.edu.dlsu.finwise.databinding.ItemProfileCurrentGoalsBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ProfileCurrentGoalsAdapter : RecyclerView.Adapter<ProfileCurrentGoalsAdapter.ProfileCurrentGoalsViewHolder>{

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
    ): ProfileCurrentGoalsAdapter.ProfileCurrentGoalsViewHolder {
        val itemBinding = ItemProfileCurrentGoalsBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return ProfileCurrentGoalsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ProfileCurrentGoalsAdapter.ProfileCurrentGoalsViewHolder,
                                  position: Int) {
        holder.bindGoal(goalsIDArrayList[position])
    }

    inner class ProfileCurrentGoalsViewHolder(private val itemBinding: ItemProfileCurrentGoalsBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(goalID: String){
            firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener{ document ->
                var goal = document.toObject<FinancialGoals>()
                itemBinding.tvGoalName.text = goal?.goalName
                val date = SimpleDateFormat("MM/dd/yyyy").format(goal?.targetDate?.toDate())
                itemBinding.tvTargetDate.text = "Target Date: " + date.toString()
            }
        }

        override fun onClick(p0: View?) {

        }
    }
}