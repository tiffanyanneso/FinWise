package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.edu.dlsu.finwise.*
import ph.edu.dlsu.finwise.databinding.ActivityFinancialBinding
import ph.edu.dlsu.finwise.databinding.DialogNewGoalWarningBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.*
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.GoalSettings

class FinancialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialBinding
//    private lateinit var goalAdapter: ChildGoalAdapter
//    private var goalIDArrayList = ArrayList<String>()
//    private lateinit var status: String
    private var firestore = Firebase.firestore

    private var shortTermRate = 0.00F
    private var mediumTermRate = 0.00F
    private var longTermRate = 0.00F

    private var ongoingGoals = 0

    private val tabIcons = intArrayOf(
        ph.edu.dlsu.finwise.R.drawable.baseline_star_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_wallet_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_pie_chart_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_shopping_cart_checkout_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_check_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_do_not_disturb_24,
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlobalScope.launch(Dispatchers.IO) {
//            setGoalCount()
            withContext(Dispatchers.Main) {
                val adapter = ViewPagerAdapter(supportFragmentManager)
                //checkSettings()

                // TODO: change the fragments added based on parent approval
                adapter.addFragment(GoalSettingFragment(),"Goal Setting")
                adapter.addFragment(SavingFragment(),"Saving")
                adapter.addFragment(BudgetingFragment(),"Budgeting")
                adapter.addFragment(SpendingFragment(),"Spending")
                adapter.addFragment(AchievedFragment(),"Achieved")
                adapter.addFragment(DisapprovedFragment(),"Disapproved")

                binding.viewPager.adapter = adapter
                binding.tabLayout.setupWithViewPager(binding.viewPager)
                setupTabIcons()
            }
        }

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
    }

    private fun setupTabIcons() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
        binding.tabLayout.getTabAt(2)?.setIcon(tabIcons[2])
        binding.tabLayout.getTabAt(3)?.setIcon(tabIcons[3])
        binding.tabLayout.getTabAt(4)?.setIcon(tabIcons[4])
        binding.tabLayout.getTabAt(5)?.setIcon(tabIcons[5])
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