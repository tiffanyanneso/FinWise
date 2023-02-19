package ph.edu.dlsu.finwise

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalDetailsTabbedBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity

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

        binding.topAppBar.setNavigationOnClickListener {
            val goToGoal = Intent(applicationContext, ViewGoalActivity::class.java)
            goToGoal.putExtras(bundle)
            goToGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToGoal)
        }
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