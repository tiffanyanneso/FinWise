package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

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
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
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
import ph.edu.dlsu.finwise.parentDashboardModule.ParentDashboardActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentPendingFragments.ParentPendingEarningFragment
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentPendingFragments.ParentPendingGoalFragment
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentPendingFragments.ParentTransactionReviewFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import java.util.ArrayList

class ParentPendingForReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentPendingForReviewBinding
    private lateinit var context: Context

    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentPendingForReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeFragments()
        initializeNavBar()

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            var pfm = Intent (this, ParentDashboardActivity::class.java)
            startActivity(pfm)
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