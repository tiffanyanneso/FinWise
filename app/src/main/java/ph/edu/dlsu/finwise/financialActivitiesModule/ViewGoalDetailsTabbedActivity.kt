package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalDetailsTabbedBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.goalDetailsFragments.GoalDetailsFragment
import ph.edu.dlsu.finwise.financialActivitiesModule.goalDetailsFragments.GoalFeedbackFragment
import ph.edu.dlsu.finwise.model.Users

class ViewGoalDetailsTabbedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewGoalDetailsTabbedBinding

    private lateinit var financialGoalID:String
    private lateinit var childID:String

    private var firestore = Firebase.firestore

    //if the goal was reviewed by the parent or not
    private var hasFeedback = false

    private val tabIcons = intArrayOf(
        R.drawable.baseline_list_24,
        R.drawable.baseline_chat_24,
    )


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityViewGoalDetailsTabbedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNavigationBar()

        var bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        childID = bundle.getString("childID").toString()

        firestore.collection("GoalRating").whereEqualTo("financialGoalID", financialGoalID).get().addOnSuccessListener {
            //this goal was reviewed by the parent, add fragment to show feedback
            if (!it.isEmpty)
                hasFeedback = true
        }.continueWith {
            initializeFragments()
        }

        binding.topAppBar.setNavigationOnClickListener {
            val goToGoal = Intent(applicationContext, ViewGoalActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("financialGoalID", financialGoalID)
            sendBundle.putString("childID", childID)
            goToGoal.putExtras(sendBundle)
            goToGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToGoal)
        }
    }

    private fun setupTabIcons() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
    }

    private fun initializeFragments() {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        val fragmentBundle = Bundle()
        fragmentBundle.putString("financialGoalID", financialGoalID)

        var goalDetails = GoalDetailsFragment()
        goalDetails.arguments = fragmentBundle
        adapter.addFragment(goalDetails,"Details")

        if (hasFeedback) {
            var goalFeedback = GoalFeedbackFragment()
            goalFeedback.arguments = fragmentBundle
            adapter.addFragment(goalFeedback,"Feedback")
        }

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

    private fun setNavigationBar() {

        var navUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(navUser).get().addOnSuccessListener {

            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent

            if (it.toObject<Users>()!!.userType == "Parent") {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                //sends the ChildID to the parent navbar
                val bundle = intent.extras!!
                val childID = bundle.getString("childID").toString()
                val bundleNavBar = Bundle()
                bundleNavBar.putString("childID", childID)
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)
            } else if (it.toObject<Users>()!!.userType == "Child"){
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
            }
        }
    }
}