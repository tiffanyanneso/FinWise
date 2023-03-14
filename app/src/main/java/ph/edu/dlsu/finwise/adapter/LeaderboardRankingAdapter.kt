package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemLeaderboardRankingBinding
import ph.edu.dlsu.finwise.databinding.ItemViewFriendBinding

class LeaderboardRankingAdapter : RecyclerView.Adapter<LeaderboardRankingAdapter.LeaderboardRankingViewHolder> {

    private var friendsIDArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore


    constructor(context: Context, friendsIDArrayList: ArrayList<String>) {
        this.context = context
        this.friendsIDArrayList = friendsIDArrayList
    }

    override fun getItemCount(): Int {
        return friendsIDArrayList.size
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
        holder.bindItem(friendsIDArrayList[position])
    }

    inner class LeaderboardRankingViewHolder(private val itemBinding: ItemLeaderboardRankingBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(childID: String) {

        }

        override fun onClick(p0: View?) {

        }
    }
}