package ph.edu.dlsu.finwise.financialActivitiesModule.budgetExpenseFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ph.edu.dlsu.finwise.databinding.FragmentBudgetShoppingListBinding


class BudgetShoppingListFragment : Fragment() {

    private lateinit var binding:FragmentBudgetShoppingListBinding
    private lateinit var budgetActivityID:String
    private lateinit var budgetItemID:String
    private lateinit var spendingActivityID:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            var bundle = arguments
            budgetActivityID = bundle?.getString("budgetActivityID").toString()
            budgetItemID = bundle?.getString("budgetItemID").toString()
            spendingActivityID = bundle?.getString("spendingActivityID").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}