package ph.edu.dlsu.finwise.financialActivitiesModule.budgetExpenseFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ph.edu.dlsu.finwise.databinding.FragmentBudgetShoppingListBinding


class BudgetShoppingListFragment : Fragment() {

    private lateinit var binding:FragmentBudgetShoppingListBinding
    private lateinit var budgetActivityID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        budgetActivityID = bundle?.getString("budgetActivityID").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }
}