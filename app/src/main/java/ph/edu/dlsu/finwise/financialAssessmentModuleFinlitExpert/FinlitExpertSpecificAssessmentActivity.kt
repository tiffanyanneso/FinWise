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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.NavbarFinlitExpert
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.SpecificAssessmentAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertSpecificAssessmentBinding
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.fragment.AssessmentDetailsFragment
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.fragment.AssessmentQuestionsFragment
import ph.edu.dlsu.finwise.model.FinancialAssessmentDetails


class FinlitExpertSpecificAssessmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialAssessmentFinlitExpertSpecificAssessmentBinding
    private lateinit var assessmentID:String
    private lateinit var assessmentCategory: String // Add this line
    private lateinit var assessmentType: String // Add this line

    private var firestore = Firebase.firestore

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
        firestore.collection("Assessments").document(assessmentID).get().addOnSuccessListener {
            var assessment = it.toObject<FinancialAssessmentDetails>()
            binding.tvAssessmentName.text = "${assessment?.assessmentCategory} - ${assessment?.assessmentType}"
        }

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

    override fun onBackPressed() {
        val specificAssessment = Intent(applicationContext, FinlitExpertAssessmentTypeActivity::class.java)
        var sendBundle = Bundle()
        sendBundle.putString("assessmentID", assessmentID)
        specificAssessment.putExtras(sendBundle)
        startActivity(specificAssessment)
        //finish()
    }

}


