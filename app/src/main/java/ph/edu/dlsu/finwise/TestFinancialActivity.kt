package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.FinactSavingAdapter
import ph.edu.dlsu.finwise.databinding.ActivityTestFinancialBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.NewGoal
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.AchievedFragment
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.BudgetingFragment

class TestFinancialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestFinancialBinding
    private lateinit var context: Context
    private lateinit var goalAdapter: FinactSavingAdapter
    private var goalIDArrayList = ArrayList<String>()
    private lateinit var status: String


    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestFinancialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        val adapter = ViewPagerAdapter(supportFragmentManager)

        // TODO: change the fragments added based on parent approval
        adapter.addFragment(BudgetingFragment(),"In Progress")
//        adapter.addFragment(GoalForReviewFragment(),"For Review")
//        adapter.addFragment(GoalForEditingFragment(),"For Editing")
//        adapter.addFragment(GoalDisapprovedFragment(),"Disapproved")
        adapter.addFragment(AchievedFragment(),"Achieved")

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.btnNewGoal.setOnClickListener {
            context=this
            var goToNewGoal = Intent(context, NewGoal::class.java)
            context.startActivity(goToNewGoal)
        }

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
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