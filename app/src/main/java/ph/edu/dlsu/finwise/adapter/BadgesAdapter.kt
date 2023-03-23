package ph.edu.dlsu.finwise.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ItemBadgeBinding
import ph.edu.dlsu.finwise.databinding.ItemLeaderboardRankingBinding
import ph.edu.dlsu.finwise.model.UserBadges

class BadgesAdapter(
    badge: List<UserBadges>
) : RecyclerView.Adapter<BadgesAdapter.BadgesViewHolder>() {

    private var badges = badge


    override fun getItemCount(): Int {
        return badges.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BadgesAdapter.BadgesViewHolder {
        val itemBinding = ItemBadgeBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        return BadgesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: BadgesAdapter.BadgesViewHolder,
        position: Int
    ) {
        holder.bindItem(badges[position])
    }

    inner class BadgesViewHolder(private val itemBinding: ItemBadgeBinding) :
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(badge: UserBadges) {
            itemBinding.tvName.text = badge.badgeName
            itemBinding.tvDescription.text = badge.badgeDescription
            itemBinding.tvFinishDate.text = badge.dateEarned
            val imageView = itemBinding.ivBadge
            var badgeImage = R.drawable.excellent

            when (badge.badgeName) {
                "Budgeting Genius" -> badgeImage = R.drawable.badge_brainy_budgeting
                "Spending Genius" -> badgeImage = R.drawable.badge_brainy_spending
                "Saving Genius" -> badgeImage = R.drawable.badge_brainy_saving
                "Goal Setting Genius" -> badgeImage = R.drawable.badge_brainy_goal_setting
            }
            imageView.setImageResource(badgeImage)
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
