package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Context
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParentFirst
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityParentPendingForReviewBinding
import ph.edu.dlsu.finwise.databinding.ActivityPfmtransactionHistoryBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.GoalTransactionsActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.goalTransactionsFragments.GoalDepositFragment
import ph.edu.dlsu.finwise.financialActivitiesModule.goalTransactionsFragments.GoalTransactionsFragment
import ph.edu.dlsu.finwise.financialActivitiesModule.goalTransactionsFragments.GoalWithdrawalFragment
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentPendingFragments.ParentPendingEarningFragment
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentPendingFragments.ParentPendingGoalFragment
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentPendingFragments.ParentTransactionReviewFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.NotificationEarningFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.NotificationGoalFragment
import java.util.*

class ChildNotificationSummary : AppCompatActivity() {

    private lateinit var binding: ActivityParentPendingForReviewBinding
    private lateinit var context: Context
    private lateinit var lastLogin: Date

    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentPendingForReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.Main).launch {

            if (intent.extras!!.containsKey("lastLogin"))
                lastLogin = intent.extras!!.get("lastLogin") as Date
            else
                lastLogin = firestore.collection("Users").document(currentUser).get().await().toObject<Users>()?.lastLogin!!.toDate()

            initializeFragments()
            initializeNavBar()
        }
        //setNavigationBar()
        //setupTabIcons()

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            var pfm = Intent (this, PersonalFinancialManagementActivity::class.java)
            startActivity(pfm)
        }
    }

    private fun initializeFragments() {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        var fragmentBundle = Bundle()
        fragmentBundle.putSerializable("lastLogin", lastLogin)

        var notificationGoalFragment = NotificationGoalFragment()
        notificationGoalFragment.arguments = fragmentBundle

        var notificationEarningFragment = NotificationEarningFragment()
        notificationEarningFragment.arguments = fragmentBundle

        adapter.addFragment(notificationGoalFragment,"Goals")
        adapter.addFragment(notificationEarningFragment,"Chores")

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
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

    private fun initializeNavBar() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            val userType = it.toObject<Users>()!!.userType.toString()
            //current user is a child
            if (userType == "Child") {
                binding.bottomNavChild.visibility = View.VISIBLE
                binding.bottomNavParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav_child), this, R.id.nav_finance)
            } else if (userType == "Parent") {
                binding.bottomNavChild.visibility = View.GONE
                binding.bottomNavParent.visibility = View.VISIBLE
                NavbarParentFirst(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_first_dashboard)
            }
        }
    }
}