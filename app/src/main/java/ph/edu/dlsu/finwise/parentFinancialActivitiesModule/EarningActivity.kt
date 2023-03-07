package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityEarningBinding
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.earningActivitiesFragments.EarningCompletedFragment
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.earningActivitiesFragments.EarningPendingConfirmationFragment
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.earningActivitiesFragments.EarningToDoFragment

class EarningActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEarningBinding

    private lateinit var savingActivityID:String

    private var firestore = Firebase.firestore

    private lateinit var childID:String

    private var user = "child"

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private val tabIcons = intArrayOf(
        ph.edu.dlsu.finwise.R.drawable.baseline_directions_run_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_access_alarm_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_check_circle_24
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        savingActivityID = bundle.getString("savingActivityID").toString()
        childID = bundle.getString("childID").toString()


        checkUser()
        initializeFragments()
        loadButtons()


        binding.btnAddEarningActivity.setOnClickListener {
            var newEarning = Intent(this, NewEarningActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("savingActivityID", savingActivityID)
            sendBundle.putString("childID", childID)
            newEarning.putExtras(sendBundle)
            startActivity(newEarning)
        }
    }

    private fun loadButtons(){
        setNavigationBar()
        loadBackButton()
    }

    private fun setNavigationBar() {
        val bottomNavigationViewChild = binding.bottomNav
        val bottomNavigationViewParent = binding.bottomNavParent

        if (user == "child") {
            bottomNavigationViewChild.visibility = View.VISIBLE
            bottomNavigationViewParent.visibility = View.GONE
            Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
        } else {
            bottomNavigationViewChild.visibility = View.GONE
            bottomNavigationViewParent.visibility = View.VISIBLE
            NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal)
        }
    }
    private fun initializeFragments() {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        var fragmentBundle = Bundle()
        fragmentBundle.putString("childID", childID)

        var earningToDoFragment = EarningToDoFragment()
        earningToDoFragment.arguments = fragmentBundle

        var earningPendingFragment = EarningPendingConfirmationFragment()
        earningPendingFragment.arguments = fragmentBundle

        var earningCompletedFragment = EarningCompletedFragment()
        earningCompletedFragment.arguments = fragmentBundle


        firestore.collection("ChildUser").document(currentUser).get().addOnSuccessListener {
            //current user is a child
            if (it.exists()) {
                adapter.addFragment(earningToDoFragment,"To Do")
                adapter.addFragment(earningPendingFragment,"Pending")
                adapter.addFragment(earningCompletedFragment,"Completed")
                setupTabIconsChild()
            } else {
                adapter.addFragment(earningPendingFragment,"Pending")
                adapter.addFragment(earningToDoFragment,"To Do")
                adapter.addFragment(earningCompletedFragment,"Completed")
                setupTabIconsParent()
            }
        }.continueWith {
            binding.viewPager.adapter = adapter
            binding.tabLayout.setupWithViewPager(binding.viewPager)
        }
    }

    private fun setupTabIconsChild() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
        binding.tabLayout.getTabAt(2)?.setIcon(tabIcons[2])
    }

    private fun setupTabIconsParent() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[1])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(2)?.setIcon(tabIcons[2])
    }

    private fun checkUser() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("ChildUser").document(currentUser).get().addOnSuccessListener {
            //current user is a child
            if (it.exists())
                binding.btnAddEarningActivity.visibility = View.GONE
        }
    }

    class ViewPagerAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm){

        private val mFrgmentList = ArrayList<Fragment>()
        private val mFrgmentTitleList = ArrayList<String>()
        override fun getCount() = mFrgmentList.size
        override fun getItem(position: Int) = mFrgmentList[position]
        override fun getPageTitle(position: Int) = mFrgmentTitleList[position]

        fun addFragment(fragment: Fragment, title:String){
            mFrgmentList.add(fragment)
            mFrgmentTitleList.add(title)
        }
    }
    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}