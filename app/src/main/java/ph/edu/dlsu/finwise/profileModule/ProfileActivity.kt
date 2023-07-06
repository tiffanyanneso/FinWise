package ph.edu.dlsu.finwise.profileModule

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.*
import ph.edu.dlsu.finwise.databinding.ActivityProfileBinding
import ph.edu.dlsu.finwise.model.Friends
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.profileModule.fragments.ProfileBadgesFragment
import ph.edu.dlsu.finwise.profileModule.fragments.ProfileCurrentGoalsFragment

class ProfileActivity : AppCompatActivity(){

    private lateinit var binding: ActivityProfileBinding
    private var firestore = Firebase.firestore
    private lateinit var context: Context

    private var friendsUserIDArrayList = ArrayList<String>()
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var childID:String

    private val tabIcons = intArrayOf(
        R.drawable.baseline_star_24,
        R.drawable.baseline_gpp_good_24
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this

        var bundle = intent.extras
        //different user is coming to view the profile
        if (bundle != null) {
            if (bundle?.containsKey("childID")!!) {
                childID = bundle.getString("childID").toString()
                binding.btnEditProfile.visibility = View.GONE
            }
        }
        //user is viewing their own profile
        else {
            childID = currentUser
            binding.tvFriends.setOnClickListener {
                val goToViewFriends = Intent(this, ViewFriendsActivity::class.java)
                context.startActivity(goToViewFriends)
            }
            binding.btnEditProfile.visibility = View.VISIBLE
        }
        setupFragments()

        CoroutineScope(Dispatchers.Main).launch {
            getProfileData()
            countFriends()
            binding.layoutLoading.visibility = View.GONE
            binding.mainLayout.visibility = View.VISIBLE
        }

        val sharedPrefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
        val color = sharedPrefs.getInt("color", Color.BLACK)
        binding.circularImageView.setColorFilter(color, PorterDuff.Mode.SRC_IN)

        binding.btnEditProfile.setOnClickListener {
            val gotoEditProfile = Intent(this, EditProfileActivity::class.java)
            context.startActivity(gotoEditProfile)
        }



        binding.topAppBar.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent (this, MainActivity::class.java)
                    startActivity (intent)
                    finish()
                    true
                }
                R.id.btn_settings -> {
                    val intent = Intent (this, ChildSettingsActivity::class.java)
                    startActivity (intent)
                    true
                }
                else -> false
            }
        }


        // Initializes the navbar
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_profile)
    }

    private fun setupFragments() {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        var fragmentBundle = Bundle()
        fragmentBundle.putString("childID", childID)

        var profileCurrentGoalsFragment = ProfileCurrentGoalsFragment()
        profileCurrentGoalsFragment.arguments = fragmentBundle

        var profileBadgesFragment = ProfileBadgesFragment()
        profileBadgesFragment.arguments = fragmentBundle

        adapter.addFragment(profileCurrentGoalsFragment,"Current Goals")
        adapter.addFragment(profileBadgesFragment,"Badges")

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.tabLayout.getTabAt(0)?.text = "Current Goals"
        binding.tabLayout.getTabAt(1)?.text = "Badges"

        setupTabIcons()

    }

    private fun setupTabIcons() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
    }

    private suspend fun getProfileData() {
        // Retrieve the color value from the "ColorPreferences" collection
        val colorPreferencesRef = firestore.collection("ColorPreferences").document(currentUser)
        colorPreferencesRef.get().addOnSuccessListener { documentSnapshot ->
            val colorData = documentSnapshot.data
            if (colorData != null && colorData.containsKey("color")) {
                val color = colorData["color"] as Long
                binding.circularImageView.setColorFilter(color.toInt(), PorterDuff.Mode.SRC_IN)
            }
        }

        var child = firestore.collection("Users").document(childID).get().await().toObject<Users>()

            if (child?.username != null)
                binding.tvUsername.setText("@" + child?.username.toString())
            if (child?.firstName != null && child?.lastName != null)
                binding.tvName.setText(child?.firstName.toString() + " " + child?.lastName.toString())
    }

    private suspend fun countFriends() {
        var sender = firestore.collection("Friends").whereEqualTo("senderID", childID).whereEqualTo("status", "Accepted").get().await()
        for (friend in sender) {
            var request = friend.toObject<Friends>()
            friendsUserIDArrayList.add(request.receiverID.toString())
        }

        var receiver = firestore.collection("Friends").whereEqualTo("receiverID", childID).whereEqualTo("status", "Accepted").get().await()
        for (friend in receiver) {
            var request = friend.toObject<Friends>()
            friendsUserIDArrayList.add(request.senderID.toString())
        }

        binding.tvFriends.text = "${friendsUserIDArrayList.size} Friends >"
    }

    class ViewPagerAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm){

        private val mFrgmentList = ArrayList<Fragment>()
        private val mFrgmentTitleList = ArrayList<String>()
        override fun getCount() = mFrgmentList.size
        override fun getItem(position: Int) = mFrgmentList[position]
        override fun getPageTitle(position: Int) = mFrgmentTitleList[position]

        fun addFragment(fragment:Fragment,title:String){
            mFrgmentList.add(fragment)
            mFrgmentTitleList.add(title)
        }
    }
}