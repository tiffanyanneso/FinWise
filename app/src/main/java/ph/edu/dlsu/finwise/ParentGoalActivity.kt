package ph.edu.dlsu.finwise

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityFinancialBinding
import ph.edu.dlsu.finwise.databinding.ActivityParentGoalBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity

class ParentGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentGoalBinding
    private var firestore = Firebase.firestore
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        val adapter = ViewPagerAdapter(supportFragmentManager)
        //checkSettings()

        // TODO: change the fragments added based on parent approval
        adapter.addFragment(ParentInProgressFragment(),"In Progress")
        adapter.addFragment(ParentForReviewFragment(),"For Review")
        adapter.addFragment(ParentForEditingFragment(),"For Editing")
        adapter.addFragment(ParentDisapprovedFragment(),"Disapproved")
        adapter.addFragment(ParentAchievedFragment(),"Achieved")

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