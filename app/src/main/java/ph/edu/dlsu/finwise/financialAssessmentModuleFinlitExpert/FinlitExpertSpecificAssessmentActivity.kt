package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentFinlitExpertSpecificAssessmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
         assessmentID = bundle.getString("assessmentID").toString()


        //setting fragments
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(AssessmentDetailsFragment(),"Details")
        adapter.addFragment(AssessmentQuestionsFragment(),"Questions")
        sendDataToFragment()

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        //end of setting fragments

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        NavbarFinlitExpert(findViewById(R.id.bottom_nav_finlit_expert), this, R.id.nav_finlit_assessment)

        goToFinlitExpertEditAssessment()
    }

    private fun sendDataToFragment() {
        val mFragmentManager = supportFragmentManager
        var fragmentBundle = Bundle()
        fragmentBundle.putString("assessmentID", assessmentID)

        val assessmentDetailsFragmentTransaction = mFragmentManager.beginTransaction()
        var assessmentDetailsFragment = AssessmentDetailsFragment()
        assessmentDetailsFragment.arguments = fragmentBundle
        assessmentDetailsFragmentTransaction.add(binding.viewPager.id, assessmentDetailsFragment).commit()

        var assessmentQuestionsFragmentTransaction = mFragmentManager.beginTransaction()
        var assessmentQuestionsFragment = AssessmentQuestionsFragment()
        assessmentQuestionsFragment.arguments = fragmentBundle
        assessmentQuestionsFragmentTransaction.add(binding.viewPager.id, assessmentQuestionsFragment).commit()
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

}


