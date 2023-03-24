package ph.edu.dlsu.finwise.financialActivitiesModule

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
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityGoalTransactionsBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.goalTransactionsFragments.GoalDepositFragment
import ph.edu.dlsu.finwise.financialActivitiesModule.goalTransactionsFragments.GoalTransactionsFragment
import ph.edu.dlsu.finwise.financialActivitiesModule.goalTransactionsFragments.GoalWithdrawalFragment
import ph.edu.dlsu.finwise.model.Users

class GoalTransactionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalTransactionsBinding
    private lateinit var savingActivityID:String
    private lateinit var source:String
    private var firestore = Firebase.firestore

    private val tabIcons = intArrayOf(
        R.drawable.baseline_receipt_long_24,
        R.drawable.baseline_star_24,
        R.drawable.baseline_wallet_24
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var bundle: Bundle = intent.extras!!
        savingActivityID = bundle.getString("savingActivityID").toString()

        //checkSettings()

        initializeFragments()
        setNavigationBar()

        setupTabIcons()

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
                val goToGoal = Intent(applicationContext, ViewGoalActivity::class.java)
                goToGoal.putExtras(bundle)
                goToGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(goToGoal)
            }


    }

    //https://stackoverflow.com/questions/63730459/how-to-pass-data-from-activity-to-fragment-with-tablayout
    private fun initializeFragments() {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        var fragmentBundle = Bundle()
        fragmentBundle.putString("savingActivityID", savingActivityID)

        var goalTransactionsFragment = GoalTransactionsFragment()
        goalTransactionsFragment.arguments = fragmentBundle

        var goalDepositsFragment = GoalDepositFragment()
        goalDepositsFragment.arguments = fragmentBundle

        var goalWithdrawalFragment = GoalWithdrawalFragment()
        goalWithdrawalFragment.arguments = fragmentBundle

        adapter.addFragment(goalTransactionsFragment,"All")
        adapter.addFragment(goalDepositsFragment,"Deposit")
        adapter.addFragment(goalWithdrawalFragment,"Withdrawal")
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun setupTabIcons() {
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(2)?.setIcon(tabIcons[1])
        binding.tabLayout.getTabAt(3)?.setIcon(tabIcons[2])
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

    private fun setNavigationBar() {

        var navUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(navUser).get().addOnSuccessListener {

            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent

            var user  = it.toObject<Users>()!!
            if (user.userType == "Parent") {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                //sends the ChildID to the parent navbar
                val bundle = intent.extras!!
                val childID = bundle.getString("childID").toString()
                val bundleNavBar = Bundle()
                bundleNavBar.putString("childID", childID)
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)
            } else  if (user.userType == "Child"){
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
            }
        }
    }
}