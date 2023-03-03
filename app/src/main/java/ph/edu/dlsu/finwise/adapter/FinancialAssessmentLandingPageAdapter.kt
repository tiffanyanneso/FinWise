package ph.edu.dlsu.finwise.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import ph.edu.dlsu.finwise.financialAssessmentModule.fragment.AssessmentLeaderboardFragment
import ph.edu.dlsu.finwise.financialAssessmentModule.fragment.AssessmentPerformanceFragment

class FinancialAssessmentLandingPageAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return AssessmentPerformanceFragment()
            1 -> return AssessmentLeaderboardFragment()
            else -> return AssessmentPerformanceFragment()
        }
    }
}