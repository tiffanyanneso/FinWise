package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityProfileBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.ChildEditGoal
import ph.edu.dlsu.finwise.model.ChildUser

class ProfileActivity : AppCompatActivity(){

    private lateinit var binding: ActivityProfileBinding
    private var firestore = Firebase.firestore
    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(ProfileCurrentGoalsFragment(),"Current Goals")
        adapter.addFragment(ProfileBadgesFragment(),"Badges")

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        getProfileData()

        binding.btnEditProfile.setOnClickListener {
            var gotoEditProfile = Intent(context, EditProfileActivity::class.java)
            context.startActivity(gotoEditProfile)
        }

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_profile)
    }

    fun getProfileData() {
        //val currentUser:String = FirebaseAuth.getInstance().currentUser!!.uid
        val currentUser = "JoCGIUSVMWTQ2IB7Rf41ropAv3S2"

        firestore.collection("ChildUser").document(currentUser).get().addOnSuccessListener { documentSnapshot ->
            var child = documentSnapshot.toObject<ChildUser>()

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