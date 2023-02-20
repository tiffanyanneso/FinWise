package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityGoalTransactionsBinding

class GoalTransactionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalTransactionsBinding
    private lateinit var savingActivityID:String
    private lateinit var source:String

    private val tabIcons = intArrayOf(
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

        var goalDepositsFragment = GoalDepositFragment()
        goalDepositsFragment.arguments = fragmentBundle

        var goalWithdrawalFragment = GoalWithdrawalFragment()
        goalWithdrawalFragment.arguments = fragmentBundle

        adapter.addFragment(goalDepositsFragment,"Deposit")
        adapter.addFragment(goalWithdrawalFragment,"Withdrawal")
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun setupTabIcons() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
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