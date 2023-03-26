package ph.edu.dlsu.finwise.profileModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.AddFriendsAdapter
import ph.edu.dlsu.finwise.databinding.ActivityAddFriendsBinding
import ph.edu.dlsu.finwise.model.Friends

class AddFriendsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFriendsBinding

    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private lateinit var addFriendsAdapter: AddFriendsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fillRecyclerView()
        binding.etSearch.addTextChangedListener{ textInput ->
            searchFriends(textInput.toString())}

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToViewFriends = Intent(applicationContext, ViewFriendsActivity::class.java)
            this.startActivity(goToViewFriends)
        }

        // Initializes the navbar
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_profile)
    }

    private fun fillRecyclerView() {
        var userIDs = ArrayList<String>()
        firestore.collection("Users").whereEqualTo("userType", "Child").get().addOnSuccessListener { results ->
            //TODO: WAY TO CHECK IF USERS ARE ALREADY FRIENDS
            for (child in results) {
                if (child.id != currentUser)
                    userIDs.add(child.id)
            }

            addFriendsAdapter = AddFriendsAdapter(this, userIDs, object:AddFriendsAdapter.AddFriendClick{
                override fun addFriend(childID: String) {
                    sendFriendRequest(childID)
                }
            })
            binding.rvViewUsers.adapter = addFriendsAdapter
            binding.rvViewUsers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            addFriendsAdapter.notifyDataSetChanged()
        }
    }

    private fun searchFriends(textInput:String){
        var usernameQuery = ArrayList<String>()
        firestore.collection("Users").whereGreaterThanOrEqualTo("username", textInput).whereLessThanOrEqualTo("username", textInput + "\uf8ff").whereEqualTo("userType", "Child").get().addOnSuccessListener { results ->
            //TODO: WAY TO CHECK IF USERS ARE ALREADY FRIENDS
            for (child in results) {
                if (child.id != currentUser)
                usernameQuery.add(child.id)
            }

            addFriendsAdapter = AddFriendsAdapter(this, usernameQuery, object:AddFriendsAdapter.AddFriendClick{
                override fun addFriend(childID: String) {
                    sendFriendRequest(childID)
                }
            })
            binding.rvViewUsers.adapter = addFriendsAdapter
            binding.rvViewUsers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            addFriendsAdapter.notifyDataSetChanged()
        }
    }

    private fun sendFriendRequest(receiverUserID:String) {
        val childID  = FirebaseAuth.getInstance().currentUser!!.uid

        val friendRequest = hashMapOf(
            "senderID" to childID,
            "receiverID" to receiverUserID,
            "status" to "Pending"
        )
        firestore.collection("Friends").add(friendRequest).addOnSuccessListener {
            Toast.makeText(this, "Friend Request Sent", Toast.LENGTH_SHORT).show()
        }
    }
}