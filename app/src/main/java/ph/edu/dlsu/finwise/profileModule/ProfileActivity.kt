package ph.edu.dlsu.finwise.profileModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.*
import ph.edu.dlsu.finwise.databinding.ActivityProfileBinding
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.profileModule.fragments.ProfileBadgesFragment
import ph.edu.dlsu.finwise.profileModule.fragments.ProfileCurrentGoalsFragment

class ProfileActivity : AppCompatActivity(){

    private lateinit var binding: ActivityProfileBinding
    private var firestore = Firebase.firestore
    private lateinit var context: Context

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private val tabIcons = intArrayOf(
        R.drawable.baseline_star_24,
        R.drawable.baseline_gpp_good_24
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this

        setupFragments()
        getProfileData()

        binding.btnEditProfile.setOnClickListener {
            val gotoEditProfile = Intent(this, EditProfileActivity::class.java)
            context.startActivity(gotoEditProfile)
        }

        binding.tvFriends.setOnClickListener {
            val goToViewFriends = Intent(this, ViewFriendsActivity::class.java)
            context.startActivity(goToViewFriends)
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
                else -> false
            }
        }


        // Initializes the navbar
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_profile)
    }

    private fun setupFragments() {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        var fragmentBundle = Bundle()
        fragmentBundle.putString("childID", currentUser)

        var profileCurrentGoalsFragment = ProfileCurrentGoalsFragment()
        profileCurrentGoalsFragment.arguments = fragmentBundle

        var profileBadgesFragment = ProfileBadgesFragment()
        profileBadgesFragment.arguments = fragmentBundle

        adapter.addFragment(profileCurrentGoalsFragment,"Current Goals")
        adapter.addFragment(profileBadgesFragment,"Badges")
        setupTabIcons()

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

    }

    private fun setupTabIcons() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
    }

    fun getProfileData() {

        firestore.collection("Users").document(currentUser).get().addOnSuccessListener { documentSnapshot ->
            var child = documentSnapshot.toObject<Users>()

            //TODO get profile picture
            if (child?.username != null)
                binding.tvUsername.setText("@" + child?.username.toString())
            if (child?.firstName != null && child?.lastName != null)
                binding.tvName.setText(child?.firstName.toString() + " " + child?.lastName.toString())
        }
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