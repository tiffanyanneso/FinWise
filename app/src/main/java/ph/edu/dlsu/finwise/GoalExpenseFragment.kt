package ph.edu.dlsu.finwise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ph.edu.dlsu.finwise.databinding.FragmentGoalDepositBinding
import ph.edu.dlsu.finwise.databinding.FragmentGoalExpenseBinding

class GoalExpenseFragment : Fragment() {

    private lateinit var binding: FragmentGoalExpenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            getGoalExpenses()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalExpenseBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private fun getGoalExpenses() {
    }
}