package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertSpecificAssessmentBinding
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentViewAllQuestionsBinding
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.fragment.QuestionsActiveFragment
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.fragment.QuestionsInactiveFragment

class FinancialAssessmentViewAllQuestions : AppCompatActivity() {

    private lateinit var binding:ActivityFinancialAssessmentViewAllQuestionsBinding
    private lateinit var assessmentID:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentViewAllQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        assessmentID = bundle.getString("assessmentID").toString()

        val adapter = ViewPagerAdapter(supportFragmentManager)
        var fragmentBundle = Bundle()
        fragmentBundle.putString("assessmentID", assessmentID)

        var questionsActiveFragment = QuestionsActiveFragment()
        questionsActiveFragment.arguments = fragmentBundle

        var questionsInactiveFragment = QuestionsInactiveFragment()
        questionsInactiveFragment.arguments = fragmentBundle

        adapter.addFragment(questionsActiveFragment,"Active Questions")
        adapter.addFragment(questionsInactiveFragment,"Inactive Questions")

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