package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

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
import java.util.ArrayList

class ParentPendingForReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentPendingForReviewBinding
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentPendingForReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        var pendingGoalFragment = ParentPendingGoalFragment()
        pendingGoalFragment.arguments = fragmentBundle

        var pendingEarningFragment = ParentPendingEarningFragment()
        pendingEarningFragment.arguments = fragmentBundle

        var transactionReviewFragment = ParentTransactionReviewFragment()
        transactionReviewFragment.arguments = fragmentBundle

        adapter.addFragment(pendingGoalFragment,"Goals")
        adapter.addFragment(pendingEarningFragment,"Chores")
        adapter.addFragment(transactionReviewFragment,"Transactions")
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)


        var bundle = intent.extras!!
        if (bundle.getString("view") == "goal")
            binding.viewPager.currentItem = 0
        else if (bundle.getString("view") == "earning")
            binding.viewPager.currentItem = 1
        else if (bundle.getString("view") == "transaction")
            binding.viewPager.currentItem = 2
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