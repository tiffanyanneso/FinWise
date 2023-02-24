package ph.edu.dlsu.finwise.profileModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.ViewFriendsAdapter
import ph.edu.dlsu.finwise.databinding.ActivityViewFriendsBinding
import ph.edu.dlsu.finwise.model.Friends

class ViewFriendsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewFriendsBinding

    private var firestore = Firebase.firestore

    private var friendsUserIDArrayList = ArrayList<String>()

    private lateinit var viewFriendsAdapter: ViewFriendsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getFriends()

        binding.btnRequests.setOnClickListener {
            var requests = Intent(this, FriendRequestActivity::class.java)
            this.startActivity(requests)
        }

        binding.btnAddFriend.setOnClickListener {
            var addFriends = Intent(this, AddFriendsActivity::class.java)
            this.startActivity(addFriends)
        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToProfile = Intent(applicationContext, ProfileActivity::class.java)
            this.startActivity(goToProfile)
        }
    }

    private fun getFriends() {
        var currentUser = "eWZNOIb9qEf8kVNdvdRzKt4AYrA2"
        firestore.collection("Friends").whereEqualTo("senderID", currentUser).whereEqualTo("status", "Accepted").get().addOnSuccessListener { results ->
            for (friend in results) {
                var request = friend.toObject<Friends>()
                friendsUserIDArrayList.add(request.receiverID.toString())
            }
        }.continueWith {
            firestore.collection("Friends").whereEqualTo("receiverID", currentUser).whereEqualTo("status", "Accepted").get().addOnSuccessListener { results ->
                for (friend in results) {
                    var request = friend.toObject<Friends>()
                    friendsUserIDArrayList.add(request.senderID.toString())
                }

                viewFriendsAdapter = ViewFriendsAdapter(this, friendsUserIDArrayList)
                binding.rvViewFriends.adapter = viewFriendsAdapter
                binding.rvViewFriends.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

}