package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

import android.app.Dialog
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
import ph.edu.dlsu.finwise.databinding.DialogBudgetingReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogSpendingReviewBinding
import ph.edu.dlsu.finwise.databinding.FragmentFinactSpendingBinding
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
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
        binding.textPerformance.text = "0.00%"
        binding.title.text= "Overall Spending Performance"

        binding.btnSpendingReview.setOnClickListener {
            showSpendingReivewDialog()
        }
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
//            binding.progressBarOverspending.progress = overspendingPercentage.toInt()
//            binding.tvOverspendingPercentage.text  = DecimalFormat("##0.00").format(overspendingPercentage) + "%"


            firestore.collection("ChildUser").document(currentUser).get().addOnSuccessListener {
                var child = it.toObject<ChildUser>()
                //compute age
                val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
                val from = LocalDate.now()
                val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
                val to = LocalDate.parse(date.toString(), dateFormatter)
                var difference = Period.between(to, from)

                var age = difference.years
                if (age > 9 )
                    purchasePlanning()
                else {
                    overallSpending = overspendingPercentage
                    setOverall()
                }

            }

//            if (overSpending )
//            binding.tvOverspendingPercentage.setTextColor(getResources().getColor(R.color.red))
//            binding.tvOverspendingStatus.setTextColor(getResources().getColor(R.color.red))
        }
    }



    private fun purchasePlanning() {
        //items planned / all the items they bought * 100
        var nPlanned = 0.00F
        var nTotalPurchased = 0.00F
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "Completed").get().addOnSuccessListener { allSpendingActivities ->
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
                        overallSpending = (overspendingPercentage + ((nPlanned/nTotalPurchased)*100)) /2

                        binding.tvPerformanceText.text ="${overallSpending}%"
                        setOverall()
                    }
                }
            }
        }
    }

    private fun setOverall() {
        if (overallSpending >= 90) {
            binding.imgFace.setImageResource(R.drawable.excellent)
            binding.textStatus.text = "Excellent"
            binding.textStatus.setTextColor(getResources().getColor(R.color.dark_green))
            binding.tvPerformanceText.text = "Keep up the excellent work! Spending wisely is your strong point. Keep it up!"
            showSeeMoreButton()
        } else if (overallSpending < 90 && overallSpending >= 80) {
            binding.imgFace.setImageResource(R.drawable.great)
            binding.textStatus.text = "Great"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = " Great job! You are performing well. Keep spending wisely!"
            showSeeMoreButton()
        } else if (overallSpending < 80 && overallSpending >= 70) {
            binding.imgFace.setImageResource(R.drawable.good)
            binding.textStatus.text = "Good"
            binding.textStatus.setTextColor(getResources().getColor(R.color.light_green))
            binding.tvPerformanceText.text = "Good job! With a bit more planning to detail, you’ll surely up your performance!"
            showSeeMoreButton()
        } else if (overallSpending < 70 && overallSpending >= 60) {
            binding.imgFace.setImageResource(R.drawable.average)
            binding.textStatus.text = "Average"
            binding.textStatus.setTextColor(getResources().getColor(R.color.yellow))
            binding.tvPerformanceText.text = "Nice work! Work on improving your spending performance by always planning ahead. You’ll get there soon!"
            showReviewButton()
        } else if (overallSpending < 60) {
            binding.imgFace.setImageResource(R.drawable.bad)
            binding.textStatus.text = "Bad"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Your spending performance needs a lot of improvement. Click review to learn how!"
            showReviewButton()
        }
    }

    private fun showSeeMoreButton() {
        binding.btnSeeMore.visibility = View.VISIBLE
        binding.layoutButtons.visibility = View.GONE
    }

    private fun showReviewButton() {
        binding.btnSeeMore.visibility = View.GONE
        binding.layoutButtons.visibility = View.VISIBLE
    }


    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        spendingAdapter = FinactSpendingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = spendingAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        spendingAdapter.notifyDataSetChanged()
    }

    private fun showSpendingReivewDialog() {

        var dialogBinding= DialogSpendingReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}