package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemLeaderboardRankingBinding
import ph.edu.dlsu.finwise.financialAssessmentModule.fragment.AssessmentLeaderboardFragment

class LeaderboardRankingAdapter : RecyclerView.Adapter<LeaderboardRankingAdapter.LeaderboardRankingViewHolder> {

    private var childRanked = listOf<AssessmentLeaderboardFragment.FriendRanking?>()
    private var context: Context


    constructor(context: Context, childRanked: List<AssessmentLeaderboardFragment.FriendRanking>) {
        this.context = context
        this.childRanked = childRanked
    }

    override fun getItemCount(): Int {
        return childRanked.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LeaderboardRankingAdapter.LeaderboardRankingViewHolder {
        val itemBinding = ItemLeaderboardRankingBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        return LeaderboardRankingViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: LeaderboardRankingAdapter.LeaderboardRankingViewHolder,
        position: Int
    ) {
        holder.bindItem(childRanked[position])
    }

    inner class LeaderboardRankingViewHolder(private val itemBinding: ItemLeaderboardRankingBinding) :
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(child: AssessmentLeaderboardFragment.FriendRanking?) {
            itemBinding.tvUsername.text = child?.childUsers?.childUsersFilter?.username
            itemBinding.tvScore.text =
                String.format("%.1f%%", child?.childUsers?.assessmentPercentage)
            itemBinding.tvChildId.text = child?.childUsers?.id
            itemBinding.tvRank.text = child?.rank.toString()
        }

        override fun onClick(p0: View?) {
            /*val assessment = Intent (context, FinancialAssessmentLandingPageActivity::class.java)
            val bundle = Bundle()
            bundle.putString("friendChildID", itemBinding.tvChildId.text.toString())
            assessment.putExtras(bundle)
            assessment.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(assessment)*/
        }
    }

}
