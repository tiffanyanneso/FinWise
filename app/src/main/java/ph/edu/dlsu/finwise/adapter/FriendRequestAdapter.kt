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
import ph.edu.dlsu.finwise.databinding.ItemAddFriendBinding
import ph.edu.dlsu.finwise.databinding.ItemFriendRequestBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.model.Friends
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.profileModule.ProfileActivity

class FriendRequestAdapter : RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder>{

    private var userIDArrayList = ArrayList<String>()
    private var context: Context

    private var firestore = Firebase.firestore

    private var responseClick:ResponseClick

    constructor(context: Context, userIDArrayList:ArrayList<String>, responseClick:ResponseClick) {
        this.context = context
        this.userIDArrayList = userIDArrayList
        this.responseClick = responseClick
    }

    override fun getItemCount(): Int {
        return userIDArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendRequestAdapter.FriendRequestViewHolder {
        val itemBinding = ItemFriendRequestBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return FriendRequestViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: FriendRequestAdapter.FriendRequestViewHolder,
                                  position: Int) {
        holder.bindItem(userIDArrayList[position])
    }

    inner class FriendRequestViewHolder(private val itemBinding: ItemFriendRequestBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindItem(requestID: String){
            firestore.collection("Friends").document(requestID).get().addOnSuccessListener {
                var request = it.toObject<Friends>()
                firestore.collection("Users").document(request?.senderID.toString()).get().addOnSuccessListener {
                    var child = it.toObject<Users>()
                    itemBinding.tvUsername.text = child?.username
                    itemBinding.tvChildId.text = request?.senderID
                }
            }

            itemBinding.btnAccept.setOnClickListener {
                responseClick.acceptRequest(requestID)
            }

            itemBinding.btnDecline.setOnClickListener {
                responseClick.rejectRequest(requestID)
            }
        }

        override fun onClick(p0: View?) {
            var bundle = Bundle()
            var childID = itemBinding.tvChildId.text.toString()
            bundle.putString("childID", childID)
            var profile = Intent(context, ProfileActivity::class.java)
            profile.putExtras(bundle)
            profile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(profile)
        }
    }

    interface ResponseClick{
        fun acceptRequest (requestID:String)
        fun rejectRequest (requestID:String)
    }
}