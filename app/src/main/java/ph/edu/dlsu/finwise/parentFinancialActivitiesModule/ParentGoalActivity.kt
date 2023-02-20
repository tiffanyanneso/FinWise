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
            sendBundle.putString("source", "parentGoalActivity")
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

        //checkSettings()
        initializeFragments()
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

    private fun initializeFragments() {
        var fragmentBundle = Bundle()
        fragmentBundle.putString("childID", childID)

        var inProgressFragment = ParentSavingFragment()
        inProgressFragment.arguments = fragmentBundle

        var forReviewFragment = ParentBudgetingFragment()
        forReviewFragment.arguments = fragmentBundle

        var forEditingFragment = ParentGoalSettingFragment()
        forEditingFragment.arguments = fragmentBundle

        var disapprovedFragment = ParentDisapprovedFragment()
        disapprovedFragment.arguments = fragmentBundle

        var achievedFragment = ParentAchievedFragment()
        achievedFragment.arguments = fragmentBundle

        val adapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(ParentGoalSettingFragment(),"Goal Setting")
        adapter.addFragment(ParentSavingFragment(),"Saving")
        adapter.addFragment(ParentBudgetingFragment(),"Budgeting")
        adapter.addFragment(ParentSpendingFragment(),"Spending")
        adapter.addFragment(ParentAchievedFragment(),"Achieved")
        adapter.addFragment(ParentDisapprovedFragment(),"Disapproved")

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        setupTabIcons()
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