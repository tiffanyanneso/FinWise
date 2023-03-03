package ph.edu.dlsu.finwise.financialAssessmentModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinancialAssessmentLandingPageAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentLandingPageBinding

class FinancialAssessmentLandingPage : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialAssessmentLandingPageBinding

    var tabTitle = arrayOf("Performance", "Leaderboard")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialAssessmentLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var pager = findViewById<ViewPager2>(R.id.viewPager2)
        var tl = findViewById<TabLayout>(R.id.tabLayout)
        pager.adapter = FinancialAssessmentLandingPageAdapter(supportFragmentManager, lifecycle)

        TabLayoutMediator(tl, pager){
            tab, position ->
                tab.text = tabTitle[position]
        }.attach()

        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_assessment)
    }

}