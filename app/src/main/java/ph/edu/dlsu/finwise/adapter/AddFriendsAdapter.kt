package ph.edu.dlsu.finwise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ItemAddFriendBinding
import ph.edu.dlsu.finwise.model.Friends
import ph.edu.dlsu.finwise.model.Users

class AddFriendsAdapter : RecyclerView.Adapter<AddFriendsAdapter.AddFriendsViewHolder>{

    private var userIDArrayList = ArrayList<String>()
    private var currentUser:String
    private var context: Context

    private var firestore = Firebase.firestore

    private var addFriend:AddFriendClick

    constructor(context: Context, userIDArrayList:ArrayList<String>, currentUser:String, addFriend:AddFriendClick) {
        this.context = context
        this.userIDArrayList = userIDArrayList
        this.currentUser = currentUser
        this.addFriend = addFriend
    }

    override fun getItemCount(): Int {
        return userIDArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddFriendsAdapter.AddFriendsViewHolder {
        val itemBinding = ItemAddFriendBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return AddFriendsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: AddFriendsAdapter.AddFriendsViewHolder,
                                  position: Int) {
        holder.bindItem(userIDArrayList[position])
    }

    inner class AddFriendsViewHolder(private val itemBinding: ItemAddFriendBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(childID: String){
            firestore.collection("Users").document(childID).get().addOnSuccessListener {
                var child = it.toObject<Users>()
                itemBinding.tvUserID.text = childID
                itemBinding.tvUsername.text = child?.username
            }

            firestore.collection("Friends").whereEqualTo("senderID", childID).whereEqualTo("receiverID", currentUser).get().addOnSuccessListener { request ->
                if (!request.isEmpty) {
                    itemBinding.btnAddFriend.visibility = View.GONE
                    var request = request.documents[0].toObject<Friends>()
                    if (request?.status == "Pending")
                        itemBinding.imgRequestSent.visibility = View.VISIBLE
                    else if (request?.status == "Accepted")
                        itemBinding.imgAlreadyFriends.visibility = View.VISIBLE
                }
            }.continueWith {
                firestore.collection("Friends").whereEqualTo("receiverID", childID).whereEqualTo("senderID", currentUser).get().addOnSuccessListener { request ->
                    if (!request.isEmpty) {
                        itemBinding.btnAddFriend.visibility = View.GONE
                        var request = request.documents[0].toObject<Friends>()
                        if (request?.status == "Pending")
                            itemBinding.imgRequestSent.visibility = View.VISIBLE
                        else if (request?.status == "Accepted")
                            itemBinding.imgAlreadyFriends.visibility = View.VISIBLE
                    }
                }
            }

            itemBinding.btnAddFriend.setOnClickListener {
                addFriend.addFriend(itemBinding.tvUserID.text.toString())
                itemBinding.btnAddFriend.visibility = View.GONE
                itemBinding.imgRequestSent.visibility = View.VISIBLE
            }
        }

        override fun onClick(p0: View?) {

        }
    }

    interface AddFriendClick{
        fun addFriend(childID:String)
    }
}