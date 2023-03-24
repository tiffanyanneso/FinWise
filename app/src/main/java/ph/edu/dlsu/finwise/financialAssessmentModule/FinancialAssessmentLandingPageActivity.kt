package ph.edu.dlsu.finwise.financialAssessmentModule

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.PFMAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentLandingPageBinding
import ph.edu.dlsu.finwise.financialAssessmentModule.fragment.AssessmentLeaderboardFragment
import ph.edu.dlsu.finwise.financialAssessmentModule.fragment.AssessmentPerformanceFragment
import ph.edu.dlsu.finwise.model.Users

class FinancialAssessmentLandingPageActivity : AppCompatActivity() {

    private var childID = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var userType: String
    private var firestore = Firebase.firestore
    private lateinit var binding: ActivityFinancialAssessmentLandingPageBinding
    private val setBundle = Bundle()

    //TODO: Change to the right icons
    private val tabIcons2 = intArrayOf(
        R.drawable.baseline_performance_24,
        R.drawable.baseline_leaderboard_24
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkUser()
    }

    private fun setBundle() {
        // sending of friendchildID
        setBundle.putString("childID", childID)
        setBundle.putString("user", userType)

        /*if (getBundle?.containsKey("childID") == true) {
            childID = getBundle.getString("childID").toString()
            if (getBundle?.containsKey("friendChildID") == true) {
                val friendChildID = getBundle.getString("friendChildID").toString()
                setBundle.putString("friendChildID", friendChildID)
            }

            checkUser()
            setBundle.putString("childID", childID)
            // do something with myValue
        }*/
    }

    private fun checkUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            val user = it.toObject<Users>()!!
            userType = user.userType.toString()
            //current user is a child
            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent
            if (user.userType == "Child") {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_assessment)
            } else if (user.userType == "Parent") {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                //sends the ChildID to the parent navbar
                val bundle = intent.extras!!
                val childID = bundle.getString("childID").toString()
                val bundleNavBar = Bundle()
                bundleNavBar.putString("childID", childID)
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_assessment, bundleNavBar)
            }

            setUpChartTabs()
        }
    }


    private fun setUpChartTabs() {
        setBundle()

        val adapter = PFMAdapter(supportFragmentManager)
        val assessmentLeaderboardFragment = AssessmentLeaderboardFragment()
        val assessmentPerformanceFragment = AssessmentPerformanceFragment()
        assessmentLeaderboardFragment.arguments = setBundle
        assessmentPerformanceFragment.arguments = setBundle
        adapter.addFragment(assessmentPerformanceFragment, "Performance")
        adapter.addFragment(assessmentLeaderboardFragment, "Leaderboard")
        binding.viewpager.adapter = adapter
        binding.tab.setupWithViewPager(binding.viewpager)

        binding.tab.getTabAt(0)?.text = "Performance"
        binding.tab.getTabAt(1)?.text = "Leaderboard"
        setupTabIcons2()
    }
    private fun setupTabIcons2() {
        binding.tab.getTabAt(0)?.setIcon(tabIcons2[0])
        binding.tab.getTabAt(1)?.setIcon(tabIcons2[1])
    }


}
