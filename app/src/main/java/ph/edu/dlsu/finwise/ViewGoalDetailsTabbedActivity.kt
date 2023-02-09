package ph.edu.dlsu.finwise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalDetailsTabbedBinding

class ViewGoalDetailsTabbedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewGoalDetailsTabbedBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityViewGoalDetailsTabbedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ViewPagerAdapter(supportFragmentManager)

        val bundle = Bundle()

        //TODO get goal ID
        bundle.putString("goalID", "GOAL ID HERE")

        val goalDetails = GoalDetailsFragment()
        val goalFeedback = GoalFeedbackFragment()

        goalDetails.arguments = bundle
        goalFeedback.arguments = bundle

        adapter.addFragment(GoalDetailsFragment(),"Details")
        adapter.addFragment(GoalFeedbackFragment(),"Feedback")

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