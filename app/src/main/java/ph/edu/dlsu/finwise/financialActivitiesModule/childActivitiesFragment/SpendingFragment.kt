package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactSpendingAdapter
import ph.edu.dlsu.finwise.databinding.FragmentFinactSpendingBinding
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.ShoppingListItem
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sign

class SpendingFragment : Fragment(){

    class BudgetItemAmount(var budgetItemID:String, var amount:Float)

    private lateinit var binding: FragmentFinactSpendingBinding
    private var firestore = Firebase.firestore
    private lateinit var spendingAdapter: FinactSpendingAdapter

    var spendingActivityIDArrayList = ArrayList<String>()
    var budgetItemsIDArrayList = ArrayList<BudgetItemAmount>()
    var goalFilterArrayList = ArrayList<GoalFilter>()

    private var overSpending = 0.00F
    private var nBudgetItems = 0.00F

    var overspendingPercentage = 0.00F
    var purchasePlanningPercentage = 0.00F
    var overallSpending = 0.00F


    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        spendingActivityIDArrayList.clear()
        budgetItemsIDArrayList.clear()
        getSpending()
        //need to get the budgeting activities to be able to get the budget items
        getBudgeting()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFinactSpendingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
     /*   binding.progressBarOverspending.progress = 0
        binding.textPerformance.text = "0.00%"*/
//        binding.textStatus.text = "Good"
//        binding.textPerformance.setTextColor(getResources().getColor(R.color.dark_green))
//        binding.textStatus.setTextColor(getResources().getColor(R.color.dark_green))

        binding.progressBarOverspending.progress = 0
        binding.tvOverspendingPercentage.text = "0.00%"
//        binding.tvOverspendingStatus.text = "Bad"
//        binding.tvOverspendingPercentage.setTextColor(getResources().getColor(R.color.red))
//        binding.tvOverspendingStatus.setTextColor(getResources().getColor(R.color.red))

        binding.titleOverspendingFrequency.text = "Overspending\nFrequency"
      //  binding.titleOverallSpendingPerformance.text = "Overall Spending\nPerformance"
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }

    private fun getSpending() {
        spendingActivityIDArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            for (activity in results) {
                var activityObject = activity.toObject<FinancialActivities>()
                spendingActivityIDArrayList.add(activityObject?.financialGoalID.toString())
            }
            loadRecyclerView(spendingActivityIDArrayList)
        }.continueWith { binding.tvInProgress.text = spendingActivityIDArrayList.size.toString() }
    }

    private fun getBudgeting() {
        var budgetingActivityIDArrayList = ArrayList<String>()
        //get only completed budgeting activities because they should complete budgeting first before they are able to spend
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
            for (activity in results)
                budgetingActivityIDArrayList.add(activity.id)
            println("print number of budgeting activity " + budgetingActivityIDArrayList.size)

            for (budgetingID in budgetingActivityIDArrayList) {
                firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetingID).whereEqualTo("status", "Active").get().addOnSuccessListener { results ->
                    nBudgetItems += results.size()
                    for (budgetItem in results) {

                        var budgetItemObject = budgetItem.toObject<BudgetItem>()
                        checkOverSpending(budgetItem.id, budgetItemObject.amount!!)
//                        budgetItemsIDArrayList.add(BudgetItemAmount(budgetItem.id, budgetItemObject.amount!!))
//                        println("print add item in budgetItems array list")
                    }
                }
            }
        }
    }

    private fun checkOverSpending(budgetItemID:String, budgetItemAmount:Float){
        firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID).whereEqualTo("transactionType", "Expense").get().addOnSuccessListener { spendingTransactions ->
            var amountSpent = 0.00F
            for (expense in spendingTransactions) {
                var expenseObject = expense.toObject<Transactions>()
                amountSpent+= expenseObject.amount!!
            }
            //they spent more than their allocated budget
            if (amountSpent > budgetItemAmount)
                overSpending++

        }.continueWith {
            overspendingPercentage = (overSpending/nBudgetItems)*100
            binding.progressBarOverspending.progress = overspendingPercentage.toInt()
            binding.tvOverspendingPercentage.text  = DecimalFormat("##0.00").format(overspendingPercentage) + "%"

            purchasePlanning()

//            if (overSpending )
//            binding.tvOverspendingPercentage.setTextColor(getResources().getColor(R.color.red))
//            binding.tvOverspendingStatus.setTextColor(getResources().getColor(R.color.red))
        }
    }

    private fun purchasePlanning() {
        //items planned / all the items they bought * 100
        var nPlanned = 0.00F
        var nTotalPurchased = 0.00F
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Spending").get().addOnSuccessListener { allSpendingActivities ->
            for (spendingActivityID in allSpendingActivities) {
                firestore.collection("ShoppingListItems").whereEqualTo("spendingActivityID", spendingActivityID.id).get().addOnSuccessListener { shoppingListItems ->
                    for (shoppingListItem in shoppingListItems) {
                        var shoppingListItemObject = shoppingListItem.toObject<ShoppingListItem>()
                        if (shoppingListItemObject.status == "Purchased")
                            nPlanned++
                    }
                }.continueWith {
                    firestore.collection("Transactions").whereEqualTo("financialActivityID", spendingActivityID.id).whereEqualTo("transactionType", "Expense").get().addOnSuccessListener { expenseTransactions ->
                        nTotalPurchased += expenseTransactions.size().toFloat()
                    }.continueWith {
                        //TODO: ELIANA OVERALL
                        overallSpending = (overspendingPercentage + ((nPlanned/nTotalPurchased)*100)) /2
                    }
                }
            }
        }
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        spendingAdapter = FinactSpendingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = spendingAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        spendingAdapter.notifyDataSetChanged()
    }
}