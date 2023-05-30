package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityParentPendingForReviewBinding
import ph.edu.dlsu.finwise.databinding.ActivityPfmtransactionHistoryBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.GoalTransactionsActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.goalTransactionsFragments.GoalDepositFragment
import ph.edu.dlsu.finwise.financialActivitiesModule.goalTransactionsFragments.GoalTransactionsFragment
import ph.edu.dlsu.finwise.financialActivitiesModule.goalTransactionsFragments.GoalWithdrawalFragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentPendingForReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lastLogin = intent.extras!!.get("lastLogin") as Date
        initializeFragments()
        //setNavigationBar()
        //setupTabIcons()

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {

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
}