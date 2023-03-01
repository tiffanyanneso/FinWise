package ph.edu.dlsu.finwise.financialActivitiesModule.budgetExpenseFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.FragmentBudgetExpenseBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivityRecordExpense
import ph.edu.dlsu.finwise.model.FinancialActivities


class BudgetExpenseListFragment : Fragment() {

    private var firestore = Firebase.firestore

    private lateinit var binding:FragmentBudgetExpenseBinding
    private lateinit var budgetActivityID:String
    private lateinit var budgetItemID:String
    private lateinit var spendingActivityID:String
    private var remainingBudget = 0.00F


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var bundle = arguments
            budgetActivityID = bundle?.getString("budgetActivityID").toString()
            budgetItemID = bundle?.getString("budgetItemID").toString()
            spendingActivityID = bundle?.getString("spendingActivityID").toString()
            remainingBudget = bundle?.getFloat("remainingBudget")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore.collection("FinancialActivities").document(spendingActivityID).get().addOnSuccessListener {
            var activity = it.toObject<FinancialActivities>()
            if (activity?.status == "Completed")
                binding.btnRecordExpense.visibility = View.GONE
        }
        binding.btnRecordExpense.setOnClickListener {
            recordExpense()
        }

//        binding.tvViewAll.setOnClickListener {
//            var goToGoalTransactions = Intent(requireContext(), GoalTransactionsActivity::class.java)
//            goToGoalTransactions.putExtras(bundle)
//            this.startActivity(goToGoalTransactions)
//        }

    }

    private fun recordExpense() {
        var recordExpense = Intent (requireContext().applicationContext, FinancialActivityRecordExpense::class.java)
        var bundle = Bundle()
        bundle.putString("budgetActivityID", budgetActivityID)
        bundle.putString("budgetItemID", budgetItemID)
        bundle.putFloat("remainingBudget", remainingBudget)
        recordExpense.putExtras(bundle)
        this.startActivity(recordExpense)
    }
}