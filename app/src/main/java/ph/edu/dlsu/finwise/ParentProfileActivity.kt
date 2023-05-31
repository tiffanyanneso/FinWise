package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.ParentChildrenAdapter
import ph.edu.dlsu.finwise.databinding.ActivityParentProfileBinding
import ph.edu.dlsu.finwise.databinding.ActivityProfileBinding
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.profileModule.EditProfileActivity
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.parentDashboardModule.ParentDashboardActivity

class ParentProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentProfileBinding
    private var firestore = Firebase.firestore
    private lateinit var context: Context

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var childAdapter:ParentChildrenAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this

        loadChildren()
        getProfileData()

        //Initializes the navbar
        //sends the ChildID to the parent navbar
        val childID = intent.getStringExtra("childID")
        val bundleNavBar = Bundle().apply {
            putString("childID", childID)
        }
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_profile, bundleNavBar)

        binding.btnEditProfile.setOnClickListener {
            val gotoParentEditProfile = Intent(this, ParentEditProfileActivity::class.java)
            context.startActivity(gotoParentEditProfile)}

        binding.topAppBar.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent (this, MainActivity::class.java)
                    startActivity (intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun getProfileData() {
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener { documentSnapshot ->
            var parent = documentSnapshot.toObject<Users>()

            if (parent?.firstName != null && parent?.lastName != null)
                binding.tvName.setText(parent?.firstName.toString() + " " + parent?.lastName.toString())
        }
    }

    private fun loadChildren() {
        var parentID = FirebaseAuth.getInstance().currentUser?.uid
        var childIDArrayList = ArrayList<String>()
        var childFilterArrayList = ArrayList<ParentDashboardActivity.ChildFilter>()
        firestore.collection("Users").whereEqualTo("userType", "Child").whereEqualTo("parentID", parentID).get().addOnSuccessListener { results ->
            if (results.size()!=0) {
                for (child in results)
                    childFilterArrayList.add(ParentDashboardActivity.ChildFilter(child.id, child.toObject<Users>().firstName!!))

                var filtered = childFilterArrayList.sortedBy { it.childFirstName }
                for (child in filtered)
                    childIDArrayList.add(child.childID)

                childAdapter = ParentChildrenAdapter(this, childIDArrayList)
                binding.rvViewChildren.adapter = childAdapter
                binding.rvViewChildren.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            }
        }
    }
}