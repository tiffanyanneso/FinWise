package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ph.edu.dlsu.finwise.databinding.ItemGoalBinding
import ph.edu.dlsu.finwise.model.FinancialGoals

class ChildGoalAdapter : RecyclerView.Adapter<ChildGoalAdapter.ChildGoalViewHolder>{

    private var goalsArrayList = ArrayList<FinancialGoals>()
    private lateinit var context: Context

    public constructor(context: Context, goalsArrayList:ArrayList<FinancialGoals>) {
        this.context = context
        this.goalsArrayList = goalsArrayList
    }

    override fun getItemCount(): Int {
        return goalsArrayList.size
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
        holder.bindGoal(goalsArrayList[position])
    }

    inner class ChildGoalViewHolder(private val itemBinding: ItemGoalBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        var goal  = FinancialGoals()

        init {
            itemView.setOnClickListener(this)
        }

        fun bindGoal(goal: FinancialGoals){
            this.goal = goal
            itemBinding.tvGoal.text = goal.goalName
            itemBinding.tvTargetDate.text = goal.targetDate
            itemBinding.tvProgressAmount.text = goal.currentAmount.toString()  + "/" + goal.targetAmount.toString()
        }

        override fun onClick(p0: View?) {
            /*var viewGoal = Intent(context, ViewGoal::class.java)
            var bundle = Bundle()

            bundle.putString("title", internship.title)
            bundle.putString("function", internship.function)
            bundle.putString("type", internship.type)
            bundle.putString("description", internship.description)
            bundle.putString("link", internship.link)

            goToSpecificJobListing.putExtras(bundle)
            goToSpecificJobListing.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(viewGoal)*/
        }
    }
}