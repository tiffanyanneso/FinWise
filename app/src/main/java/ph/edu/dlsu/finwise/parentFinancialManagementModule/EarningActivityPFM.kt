package ph.edu.dlsu.finwise.parentFinancialManagementModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.earningActivitiesFragments.*
import ph.edu.dlsu.finwise.parentFinancialManagementModule.earningActivitiesPFMFragments.EarningCompletedPFMFragment
import ph.edu.dlsu.finwise.parentFinancialManagementModule.earningActivitiesPFMFragments.EarningPendingConfirmationPFMFragment
import ph.edu.dlsu.finwise.parentFinancialManagementModule.earningActivitiesPFMFragments.EarningToDoPFMFragment

class EarningActivityPFM : AppCompatActivity() {
    private lateinit var binding:ActivityEarningBinding

    private lateinit var savingActivityID:String

    private var firestore = Firebase.firestore

    private lateinit var childID:String
    private lateinit var user:String

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

        val bundle = intent.extras!!
        childID = bundle.getString("childID").toString()
        user = bundle.getString("user").toString()
        Toast.makeText(this, ""+childID, Toast.LENGTH_SHORT).show()


        checkUser()
        initializeFragments()

        binding.btnAddEarningActivity.setOnClickListener {
            var newEarning = Intent(this, NewEarningActivityPFM::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("childID", childID)
            newEarning.putExtras(sendBundle)
            startActivity(newEarning)
        }
    }


    private fun initializeFragments() {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        var fragmentBundle = Bundle()
        fragmentBundle.putString("childID", childID)

        var earningToDoFragment = EarningToDoPFMFragment()
        earningToDoFragment.arguments = fragmentBundle

        var earningPendingFragment = EarningPendingConfirmationPFMFragment()
        earningPendingFragment.arguments = fragmentBundle

        var earningCompletedFragment = EarningCompletedPFMFragment()
        earningCompletedFragment.arguments = fragmentBundle


        firestore.collection("ChildUser").document(currentUser).get().addOnSuccessListener {
            println("print "  + it.exists())
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
            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent
            if (it.exists()) {
                binding.btnAddEarningActivity.visibility = View.GONE
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)
            } else {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance)
            }
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
}