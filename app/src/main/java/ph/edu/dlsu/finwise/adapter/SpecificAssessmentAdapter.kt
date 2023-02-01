package ph.edu.dlsu.finwise.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.fragment.DetailsFragment
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.fragment.QuestionsFragment

class SpecificAssessmentAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter (fragmentManager, lifecycle)
{
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return DetailsFragment()
            1 -> return QuestionsFragment()
            else -> return DetailsFragment()
        }
    }
}