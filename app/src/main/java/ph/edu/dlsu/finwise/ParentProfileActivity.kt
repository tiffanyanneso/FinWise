package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
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
import ph.edu.dlsu.finwise.loginRegisterModule.ParentRegisterChildActivity
import ph.edu.dlsu.finwise.parentDashboardModule.ParentDashboardActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentPendingForReviewActivity

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

        val sharedPrefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
        val color = sharedPrefs.getInt("color", Color.BLACK)
        binding.circularImageView.setColorFilter(color, PorterDuff.Mode.SRC_IN)

        binding.btnEditProfile.setOnClickListener {
            val gotoEditProfile = Intent(this, EditProfileActivity::class.java)
            context.startActivity(gotoEditProfile)
        }

        NavbarParentFirst(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_first_profile)

        binding.btnEditProfile.setOnClickListener {
            val gotoParentEditProfile = Intent(this, ParentEditProfileActivity::class.java)
            context.startActivity(gotoParentEditProfile)}

        binding.btnAddChild.setOnClickListener {
            val goToParentRegisterChildActivity = Intent (this, ParentRegisterChildActivity::class.java)
            context.startActivity(goToParentRegisterChildActivity)
        }

        binding.topAppBar.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.btn_notification -> {
                    var notificationList = Intent (this, ParentPendingForReviewActivity::class.java)
                    var bundle = Bundle()
                    bundle.putString("view", "goal")
                    notificationList.putExtras(bundle)
                    startActivity(notificationList)
                    true
                }

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
        // Retrieve the color value from the "ColorPreferences" collection
        val colorPreferencesRef = firestore.collection("ColorPreferences").document(currentUser)
        colorPreferencesRef.get().addOnSuccessListener { documentSnapshot ->
            val colorData = documentSnapshot.data
            if (colorData != null && colorData.containsKey("color")) {
                val color = colorData["color"] as Long
                binding.circularImageView.setColorFilter(color.toInt(), PorterDuff.Mode.SRC_IN)
            }
        }

        firestore.collection("Users").document(currentUser).get().addOnSuccessListener { documentSnapshot ->
            var parent = documentSnapshot.toObject<Users>()

            if (parent?.firstName != null && parent?.lastName != null)
                binding.tvName.setText(parent?.firstName.toString() + " " + parent?.lastName.toString())
        }
    }

    private fun loadChildren() {
        var childIDArrayList = ArrayList<String>()
        var childFilterArrayList = ArrayList<ParentDashboardActivity.ChildFilter>()
        firestore.collection("Users").whereEqualTo("userType", "Child").whereEqualTo("parentID", currentUser).get().addOnSuccessListener { results ->
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