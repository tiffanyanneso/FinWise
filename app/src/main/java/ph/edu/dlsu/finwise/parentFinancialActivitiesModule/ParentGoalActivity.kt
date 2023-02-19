package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityParentGoalBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.ChildNewGoal
import ph.edu.dlsu.finwise.model.ChildUser
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentGoalFragment.*
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity

class ParentGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentGoalBinding
    private var firestore = Firebase.firestore
    private lateinit var context: Context

    private lateinit var childID:String

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
        binding = ActivityParentGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        var bundle = intent.extras!!
        childID = bundle.getString("childID").toString()
        setChildName()
        setGoalCount()

        var sendBundle = Bundle()
        sendBundle.putString("childID", childID)

        binding.btnNewGoal.setOnClickListener {
            var newGoal = Intent(this, ChildNewGoal::class.java)
            newGoal.putExtras(sendBundle)
            newGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(newGoal)
        }

        binding.topAppBar.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.btn_settings -> {
                    var settings = Intent(this, GoalSettingsActivity::class.java)
                    settings.putExtras(sendBundle)
                    settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    this.startActivity(settings)
                    true
                }
                else -> false
            }
        }

        val adapter = ViewPagerAdapter(supportFragmentManager)
        //checkSettings()

        // TODO: change the fragments added based on parent approval
        adapter.addFragment(ParentGoalSettingFragment(),"Goal Setting")
        adapter.addFragment(ParentSavingFragment(),"Saving")
        adapter.addFragment(ParentBudgetingFragment(),"Budgeting")
        adapter.addFragment(ParentSavingFragment(),"Spending")
        adapter.addFragment(ParentAchievedFragment(),"Achieved")
        adapter.addFragment(ParentDisapprovedFragment(),"Disapproved")
        sendDataToFragment()

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        setupTabIcons()
    }

    private fun setupTabIcons() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
        binding.tabLayout.getTabAt(2)?.setIcon(tabIcons[2])
        binding.tabLayout.getTabAt(3)?.setIcon(tabIcons[3])
        binding.tabLayout.getTabAt(4)?.setIcon(tabIcons[4])
        binding.tabLayout.getTabAt(5)?.setIcon(tabIcons[5])
    }

    private fun setChildName() {
        firestore.collection("ChildUser").document(childID).get().addOnSuccessListener {
            var child = it.toObject<ChildUser>()
            //binding.tvChildName.text = child?.firstName + "'s Goals"
        }
    }

    private fun setGoalCount() {
        var ongoing = 0
        firestore.collection("FinancialGoals").whereEqualTo("childID", childID).get().addOnSuccessListener { results ->
            for (goalSnapshot in results) {
                var goal = goalSnapshot.toObject<FinancialGoals>()
                if (goal?.status == "In Progress")
                    ongoing++
            }
            binding.tvGoalsInProgress.text = ongoing.toString()
        }
    }

    private fun sendDataToFragment() {
        val mFragmentManager = supportFragmentManager
        var fragmentBundle = Bundle()
        fragmentBundle.putString("childID", childID)


        val inProgressFragmentTransaction = mFragmentManager.beginTransaction()
        var inProgressFragment = ParentSavingFragment()
        inProgressFragment.arguments = fragmentBundle
        inProgressFragmentTransaction.add(binding.viewPager.id, inProgressFragment).commit()

        val forReviewFragmentTransaction = mFragmentManager.beginTransaction()
        var forReviewFragment = ParentBudgetingFragment()
        forReviewFragment.arguments = fragmentBundle
        forReviewFragmentTransaction.add(binding.viewPager.id, forReviewFragment).commit()

        val forEditingFragmentTransaction = mFragmentManager.beginTransaction()
        var forEditingFragment = ParentGoalSettingFragment()
        forEditingFragment.arguments = fragmentBundle
        forEditingFragmentTransaction.add(binding.viewPager.id, forEditingFragment).commit()

        val disapprovedFragmentTransaction = mFragmentManager.beginTransaction()
        var disapprovedFragment = ParentDisapprovedFragment()
        disapprovedFragment.arguments = fragmentBundle
        disapprovedFragmentTransaction.add(binding.viewPager.id, disapprovedFragment).commit()

        val achievedFragmentTransaction = mFragmentManager.beginTransaction()
        var achievedFragment = ParentAchievedFragment()
        achievedFragment.arguments = fragmentBundle
        achievedFragmentTransaction.add(binding.viewPager.id, achievedFragment).commit()
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