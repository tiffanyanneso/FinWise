package ph.edu.dlsu.finwise.financialAssessmentModule

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.PFMAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentLandingPageBinding
import ph.edu.dlsu.finwise.financialAssessmentModule.fragment.AssessmentLeaderboardFragment
import ph.edu.dlsu.finwise.financialAssessmentModule.fragment.AssessmentPerformanceFragment

class FinancialAssessmentLandingPageActivity : AppCompatActivity() {

    private var childID = FirebaseAuth.getInstance().currentUser!!.uid
    private var firestore = Firebase.firestore
    private lateinit var binding: ActivityFinancialAssessmentLandingPageBinding
    val setBundle = Bundle()

    //TODO: Change to the right icons
    private val tabIcons2 = intArrayOf(
        R.drawable.baseline_performance_24,
        R.drawable.baseline_leaderboard_24
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBundle()
        setUpChartTabs()
        //TODO: Initialize navbar
        checkUser()
       // Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_assessment)
    }

    private fun setBundle() {
        val getBundle = intent.extras
        //TODO: receive user bundle pero hindi ko alam ujng saan so hard code muna
        childID = "eWZNOIb9qEf8kVNdvdRzKt4AYrA2"
        setBundle.putString("childID", childID)
        Toast.makeText(this, ""+childID, Toast.LENGTH_SHORT).show()
        /*if (getBundle?.containsKey("childID") == true) {
            //childID = getBundle.getString("childID").toString()
            checkUser()
            setBundle.putString("childID", childID)
            // do something with myValue
        }*/
    }

    private fun checkUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("ChildUser").document(currentUser).get().addOnSuccessListener {
            //current user is a child
            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent
            if (it.exists()) {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_assessment)
            } else {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_assessment)
            }

        }
    }


    private fun setUpChartTabs() {
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