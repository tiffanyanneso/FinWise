package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ph.edu.dlsu.finwise.NavbarFinlitExpert
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.SpecificAssessmentAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertSpecificAssessmentBinding
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.fragment.AssessmentDetailsFragment
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.fragment.AssessmentQuestionsFragment


class FinlitExpertSpecificAssessmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialAssessmentFinlitExpertSpecificAssessmentBinding
    private lateinit var assessmentID:String

    private val tabIcons = intArrayOf(
        ph.edu.dlsu.finwise.R.drawable.baseline_list_24,
        ph.edu.dlsu.finwise.R.drawable.question_mark
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentFinlitExpertSpecificAssessmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
         assessmentID = bundle.getString("assessmentID").toString()
        var assessmentCategory = bundle.getString("assessmentCategory")
        var assessmentType = bundle.getString("assessmentType")
        binding.tvAssessmentName.text = "$assessmentCategory - $assessmentType"


        loadBackButton()
        initializeFragments()

        // and initializes the navbar
        NavbarFinlitExpert(findViewById(R.id.bottom_nav_finlit_expert), this, R.id.nav_finlit_assessment)

        goToFinlitExpertEditAssessment()
    }

    private fun setupTabIcons() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
    }

    private fun initializeFragments() {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        var fragmentBundle = Bundle()
        fragmentBundle.putString("assessmentID", assessmentID)

        var assessmentDetailsFragment = AssessmentDetailsFragment()
        assessmentDetailsFragment.arguments = fragmentBundle

        var assessmentQuestionsFragment = AssessmentQuestionsFragment()
        assessmentQuestionsFragment.arguments = fragmentBundle

        adapter.addFragment(assessmentDetailsFragment,"Details")
        adapter.addFragment(assessmentQuestionsFragment,"Questions")
        setupTabIcons()

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

    private fun goToFinlitExpertEditAssessment() {
       binding.btnEdit.setOnClickListener() {
           val goToFinlitExpertEditAssessmentActivity = Intent(applicationContext, FinlitExpertEditAssessmentActivity::class.java)
           var bundle = Bundle()
           bundle.putString("assessmentID", assessmentID)
           goToFinlitExpertEditAssessmentActivity.putExtras(bundle)
           startActivity(goToFinlitExpertEditAssessmentActivity)
        }
    }
    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}


