package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.MainActivity
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityParentFinancialActivitiesBinding
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentGoalFragment.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class ParentFinancialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentFinancialActivitiesBinding
    private var firestore = Firebase.firestore
    private lateinit var context: Context

    private lateinit var childID:String

    private var setOwnGoals = false
    private var autoApprove = false

    private val tabIcons = intArrayOf(
        ph.edu.dlsu.finwise.R.drawable.baseline_star_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_wallet_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_pie_chart_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_shopping_cart_checkout_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_check_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_do_not_disturb_24,
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentFinancialActivitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this



        initializeChildID()
        initializeParentNavbar()
        checkAge()



        val sendBundle = Bundle()
        sendBundle.putString("childID", childID)


        binding.topAppBar.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent (this, MainActivity::class.java)
                    startActivity (intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun initializeParentNavbar() {
        val bundleNavBar = Bundle()
        bundleNavBar.putString("childID", childID)
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)
    }

    private fun initializeChildID() {
            val bundle = intent.extras!!
            childID = bundle.getString("childID").toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAge() {
        firestore.collection("Users").document(childID).get().addOnSuccessListener {
            var child = it.toObject<Users>()
            //compute age
            val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var age = difference.years
            if (age == 10 || age == 11)
                setOwnGoals = true
            if (age == 12) {
                setOwnGoals = true
                autoApprove = true
                updateForReviewGoalStatus()
            }

        }.continueWith { initializeFragments() }
    }

    private fun initializeFragments() {

        val adapter = ViewPagerAdapter(supportFragmentManager)

        var fragmentBundle = Bundle()
        fragmentBundle.putString("childID", childID)

        var goalSettingFragment = ParentGoalSettingFragment()
        goalSettingFragment.arguments = fragmentBundle

        var savingFragment = ParentSavingFragment()
        savingFragment.arguments = fragmentBundle

        var budgetingFragment = ParentBudgetingFragment()
        budgetingFragment.arguments = fragmentBundle

        var spendingFragment = ParentSpendingFragment()
        spendingFragment.arguments = fragmentBundle

        var achievedFragment = ParentAchievedFragment()
        achievedFragment.arguments = fragmentBundle

        var disapprovedFragment = ParentDisapprovedFragment()
        disapprovedFragment.arguments = fragmentBundle

        if (setOwnGoals && !autoApprove)
            adapter.addFragment(goalSettingFragment,"Goal Setting")

        adapter.addFragment(savingFragment,"Saving")
        adapter.addFragment(budgetingFragment,"Budgeting")
        adapter.addFragment(spendingFragment,"Spending")
        adapter.addFragment(achievedFragment,"Achieved")
        adapter.addFragment(disapprovedFragment,"Disapproved")

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        setupTabIcons()
    }

    private fun updateForReviewGoalStatus() {
        firestore.collection("FinancialGoals").whereEqualTo("childID", childID).whereEqualTo("status", "For Review").get().addOnSuccessListener { results ->
            for (goal in results) {
                firestore.collection("FinancialGoals").document(goal.id).update("status", "In Progress").addOnSuccessListener {
                    firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", goal.id).whereEqualTo("financialActivityName", "Saving").get().addOnSuccessListener { financialActivity ->
                        var savingID = financialActivity.documents[0].id
                        firestore.collection("FinancialActivities").document(savingID).update("status", "In Progress")
                    }
                }
            }
        }
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