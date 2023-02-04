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
import ph.edu.dlsu.finwise.databinding.ActivityParentGoalBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.GoalSettingsActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.parentGoalFragment.*

class ParentGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentGoalBinding
    private var firestore = Firebase.firestore
    private lateinit var context: Context

    private lateinit var childID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        //var bundle = intent.extras!!
        //childID = bundle.getString("childID").toString()

        childID = "child id"

        binding.btnSettings.setOnClickListener {
            var settings = Intent(this, GoalSettingsActivity::class.java)
            var bundle = Bundle()
            bundle.putString("childID", childID)
            settings.putExtras(bundle)
            startActivity(settings)
        }

        val adapter = ViewPagerAdapter(supportFragmentManager)
        //checkSettings()

        // TODO: change the fragments added based on parent approval
        adapter.addFragment(ParentInProgressFragment(),"In Progress")
        adapter.addFragment(ParentForReviewFragment(),"For Review")
        adapter.addFragment(ParentForEditingFragment(),"For Editing")
        adapter.addFragment(ParentDisapprovedFragment(),"Disapproved")
        adapter.addFragment(ParentAchievedFragment(),"Achieved")
        sendDataToFragment()

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

    }

    private fun sendDataToFragment() {
        val mFragmentManager = supportFragmentManager
        var fragmentBundle = Bundle()
        fragmentBundle.putString("childID", childID)


        val inProgressFragmentTransaction = mFragmentManager.beginTransaction()
        var inProgressFragment = ParentInProgressFragment()
        inProgressFragment.arguments = fragmentBundle
        inProgressFragmentTransaction.add(binding.viewPager.id, inProgressFragment).commit()

        val forReviewFragmentTransaction = mFragmentManager.beginTransaction()
        var forReviewFragment = ParentForReviewFragment()
        forReviewFragment.arguments = fragmentBundle
        forReviewFragmentTransaction.add(binding.viewPager.id, forReviewFragment).commit()

        val forEditingFragmentTransaction = mFragmentManager.beginTransaction()
        var forEditingFragment = ParentForEditingFragment()
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