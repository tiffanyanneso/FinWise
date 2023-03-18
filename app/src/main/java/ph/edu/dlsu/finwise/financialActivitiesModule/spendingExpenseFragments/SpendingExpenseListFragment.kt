package ph.edu.dlsu.finwise.financialActivitiesModule.spendingExpenseFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.SpendingExpenseAdapter
import ph.edu.dlsu.finwise.databinding.FragmentSpendingExpenseBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivityRecordExpense
import ph.edu.dlsu.finwise.financialActivitiesModule.SpendingTransactionsActivity
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users


class SpendingExpenseListFragment : Fragment() {

    private var firestore = Firebase.firestore

    private lateinit var binding: FragmentSpendingExpenseBinding
    private lateinit var budgetingActivityID:String
    private lateinit var budgetItemID:String
    private lateinit var savingActivityID:String
    private lateinit var spendingActivityID:String
    private var remainingBudget = 0.00F

    private lateinit var spendingExpensesAdapter:SpendingExpenseAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var bundle = arguments
            budgetingActivityID = bundle?.getString("budgetingActivityID").toString()
            budgetItemID = bundle?.getString("budgetItemID").toString()
            spendingActivityID = bundle?.getString("spendingActivityID").toString()
            remainingBudget = bundle?.getFloat("remainingBudget")!!
            savingActivityID = bundle.getString("savingActivityID").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpendingExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUser()
        loadExpense()
        firestore.collection("FinancialActivities").document(spendingActivityID).get().addOnSuccessListener {
            var activity = it.toObject<FinancialActivities>()
            if (activity?.status == "Completed")
                binding.btnRecordExpense.visibility = View.GONE
        }
        binding.btnRecordExpense.setOnClickListener {
            recordExpense()
        }
        binding.tvViewAll.setOnClickListener {
            var spendingTransactions = Intent(requireActivity().applicationContext, SpendingTransactionsActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("spendingActivityID", spendingActivityID)
            spendingTransactions.putExtras(sendBundle)
            startActivity(spendingTransactions)
        }
    }

    private fun loadExpense () {
        var expensesArrayList = ArrayList<Transactions>()
        firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID).whereEqualTo("transactionType", "Expense").get().addOnSuccessListener { results ->
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()
                expensesArrayList.add(transactionObject)
            }

            spendingExpensesAdapter = SpendingExpenseAdapter(requireActivity().applicationContext, expensesArrayList)
            binding.rvViewItems.adapter = spendingExpensesAdapter
            binding.rvViewItems.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
            spendingExpensesAdapter.notifyDataSetChanged()
        }
    }

    private fun recordExpense() {
        var recordExpense = Intent (requireActivity().applicationContext, FinancialActivityRecordExpense::class.java)
        var bundle = Bundle()
        bundle.putString("savingActivityID", savingActivityID)
        bundle.putString("spendingActivityID", spendingActivityID)
        bundle.putString("budgetItemID", budgetItemID)
        bundle.putFloat("remainingBudget", remainingBudget)
        recordExpense.putExtras(bundle)
        this.startActivity(recordExpense)
    }

    private fun checkUser() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            //current user is parent
            if (it.toObject<Users>()!!.userType == "Parent")
                binding.btnRecordExpense.visibility = View.GONE
            else if (it.toObject<Users>()!!.userType == "Child")
                binding.btnRecordExpense.visibility = View.VISIBLE
        }
    }
}