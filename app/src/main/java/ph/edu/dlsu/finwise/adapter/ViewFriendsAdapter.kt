package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemAddFriendBinding
import ph.edu.dlsu.finwise.databinding.ItemViewFriendBinding
import ph.edu.dlsu.finwise.model.ChildUser

class ViewFriendsAdapter : RecyclerView.Adapter<ViewFriendsAdapter.ViewFriendsViewHolder>{

    private var userIDArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore


    constructor(context: Context, userIDArrayList:ArrayList<String>) {
        this.context = context
        this.userIDArrayList = userIDArrayList
    }

    override fun getItemCount(): Int {
        return userIDArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewFriendsAdapter.ViewFriendsViewHolder {
        val itemBinding = ItemViewFriendBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return ViewFriendsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewFriendsAdapter.ViewFriendsViewHolder,
                                  position: Int) {
        holder.bindItem(userIDArrayList[position])
    }

    inner class ViewFriendsViewHolder(private val itemBinding: ItemViewFriendBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(childID: String){
            firestore.collection("ChildUser").document(childID).get().addOnSuccessListener {
                var child = it.toObject<ChildUser>()
                itemBinding.tvUsername.text = child?.username
            }
        }

        override fun onClick(p0: View?) {

        }
    }
}