package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ph.edu.dlsu.finwise.NavbarFinlitExpert
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.SpecificAssessmentAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertSpecificAssessmentBinding
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.fragment.AssessmentDetailsFragment


class FinlitExpertSpecificAssessmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialAssessmentFinlitExpertSpecificAssessmentBinding

    var tabTitle = arrayOf("Details", "Questions")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentFinlitExpertSpecificAssessmentBinding.inflate(layoutInflater)
        setContentView(binding.root)



        var pager = findViewById<ViewPager2>(R.id.viewPager2)
        var tl = findViewById<TabLayout>(R.id.tabLayout)
        pager.adapter = SpecificAssessmentAdapter(supportFragmentManager, lifecycle)

        TabLayoutMediator(tl, pager){
            tab, position ->
                tab.text = tabTitle[position]
        } .attach()

        //pass data data to fragment
        var bundle = intent.extras!!
        var assessmentCategory = bundle.getString("assessmentCategory")
        var assessmentType = bundle.getString("assessmentType")
        val mFragmentManager = supportFragmentManager
        var fragmentBundle = Bundle()
        fragmentBundle.putString("assessmentCategory", assessmentCategory)
        fragmentBundle.putString("assessmentType", assessmentType)

        val assessmentDetailsFragmentTransaction = mFragmentManager.beginTransaction()
        var assessmentDetailsFragment = AssessmentDetailsFragment()
        assessmentDetailsFragment.arguments = fragmentBundle
        assessmentDetailsFragmentTransaction.add(binding.viewPager2.id, assessmentDetailsFragment).commit()


        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        NavbarFinlitExpert(findViewById(R.id.bottom_nav_finlit_expert), this, R.id.nav_finlit_assessment)

        goToFinlitExpertEditAssessment()
    }


    private fun goToFinlitExpertEditAssessment() {
       binding.btnEdit.setOnClickListener() {
            val goToFinlitExpertEditAssessmentActivity = Intent(applicationContext, FinlitExpertEditAssessmentActivity::class.java)
            startActivity(goToFinlitExpertEditAssessmentActivity)
        }
    }

}


