package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.edu.dlsu.finwise.*
import ph.edu.dlsu.finwise.databinding.ActivityFinancialBinding
import ph.edu.dlsu.finwise.databinding.DialogNewGoalWarningBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.*
import ph.edu.dlsu.finwise.model.ChildUser
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.GoalSettings
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class FinancialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialBinding
    private var firestore = Firebase.firestore


    private var childID  = FirebaseAuth.getInstance().currentUser!!.uid

    //used to check if goal setting fragment should be included
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAge()

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAge() {
        firestore.collection("ChildUser").document(childID).get().addOnSuccessListener {
            var child = it.toObject<ChildUser>()
            //compute age
            val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var age = difference.years
            if (age == 10 || age == 1)
                setOwnGoals = true
            if (age == 12) {
                setOwnGoals = true
                autoApprove = true
            }

        }.continueWith { initializeFragments() }
    }


    private fun initializeFragments() {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        if (setOwnGoals && !autoApprove)
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