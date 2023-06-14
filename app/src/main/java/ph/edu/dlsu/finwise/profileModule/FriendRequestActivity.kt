package ph.edu.dlsu.finwise.profileModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.AddFriendsAdapter
import ph.edu.dlsu.finwise.adapter.FriendRequestAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFriendRequestBinding
import ph.edu.dlsu.finwise.model.Friends

class FriendRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendRequestBinding

    private var firestore = Firebase.firestore
    private lateinit var friendRequestAdapter:FriendRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CoroutineScope(Dispatchers.Main).launch {
            getFriendRequests()
        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToViewFriends = Intent(applicationContext, ViewFriendsActivity::class.java)
            this.startActivity(goToViewFriends)
        }
        // Initializes the navbar
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_profile)
    }

    private suspend  fun getFriendRequests() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        var friendRequestIDArrayList = ArrayList<String>()
        var friendRequests = firestore.collection("Friends").whereEqualTo("receiverID", currentUser).whereEqualTo("status", "Pending").get().await()
        for (friendRequest in friendRequests)
            friendRequestIDArrayList.add(friendRequest.id)

        friendRequestAdapter = FriendRequestAdapter(this, friendRequestIDArrayList, object: FriendRequestAdapter.ResponseClick{
            override fun acceptRequest(requestID: String) {
                firestore.collection("Friends").document(requestID).update("status", "Accepted").addOnSuccessListener {
                    Toast.makeText(this@FriendRequestActivity, "Friend Request Accepted", Toast.LENGTH_SHORT).show()
                    CoroutineScope(Dispatchers.Main).launch {
                        getFriendRequests()
                    }
                }
            }

            override fun rejectRequest(requestID: String) {
                firestore.collection("Friends").document(requestID).update("status", "Rejected").addOnSuccessListener {
                    Toast.makeText(this@FriendRequestActivity, "Friend Request Rejected", Toast.LENGTH_SHORT).show()
                    CoroutineScope(Dispatchers.Main).launch {
                        getFriendRequests()
                    }
                }
            }
        })
        binding.rvViewRequests.adapter = friendRequestAdapter
        binding.rvViewRequests.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        friendRequestAdapter.notifyDataSetChanged()
    }
}