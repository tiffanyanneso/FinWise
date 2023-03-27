package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentGoalFragment

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentPerformance.ParentSpendingPerformanceActivity
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactSpendingAdapter
import ph.edu.dlsu.finwise.databinding.DialogParentSpendingTipsBinding
import ph.edu.dlsu.finwise.databinding.FragmentParentSpendingBinding
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ParentSpendingFragment : Fragment() {

    private lateinit var binding: FragmentParentSpendingBinding
    private var firestore = Firebase.firestore
    private lateinit var spendingAdapter: FinactSpendingAdapter

    var spendingActivityIDArrayList = ArrayList<String>()
    var goalIDArrayList = ArrayList<String>()
    var budgetingArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<GoalFilter>()

    private var overSpending = 0.00F
    private var nBudgetItems = 0.00F

    var nPlanned = 0.00F
    var nTotalPurchased = 0.00F

    var overspendingPercentage = 0.00F
    var purchasePlanningPercentage = 0.00F
    var overallSpending = 0.00F

    private lateinit var childID:String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        childID = bundle?.getString("childID").toString()
        goalIDArrayList.clear()
        budgetingArrayList.clear()
        spendingActivityIDArrayList.clear()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentSpendingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textPerformance.text = "0.00%"
        binding.title.text= "Overall Spending Performance"
        getSpending()
        //need to get the budgeting activities to be able to get the budget items
        getBudgeting()

        binding.btnSpendingTips.setOnClickListener {
            showSpendingReivewDialog()
        }

        binding.btnSeeMore.setOnClickListener {
            var goToPerformance = Intent(requireContext().applicationContext, ParentSpendingPerformanceActivity::class.java)

            var bundle = Bundle()

            bundle.putString("childID", childID)
            goToPerformance.putExtras(bundle)
            goToPerformance.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            this.startActivity(goToPerformance)
        }

        binding.btnSeeMore2.setOnClickListener {
            var goToPerformance = Intent(requireContext().applicationContext, ParentSpendingPerformanceActivity::class.java)

            var bundle = Bundle()

            bundle.putString("childID", childID)
            goToPerformance.putExtras(bundle)
            goToPerformance.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            this.startActivity(goToPerformance)
        }
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }


    private fun getSpending() {
        spendingActivityIDArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            for (activity in results) {
                var activityObject = activity.toObject<FinancialActivities>()
                spendingActivityIDArrayList.add(activityObject?.financialGoalID.toString())
            }
            loadRecyclerView(spendingActivityIDArrayList)
        }.continueWith { binding.tvTitleInProgress.text = "Spending Activities (" + spendingActivityIDArrayList.size.toString() + ")" }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getBudgeting() {
        //get completed spending activities
        firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
            for (spending in results) {
                var spendingActivity = spending.toObject<FinancialActivities>()
                println("print " + spendingActivity.financialGoalID )
                firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", spendingActivity.financialGoalID).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Completed").get().addOnSuccessListener { budgeting ->
                    var budgetingID = budgeting.documents[0].id
                    firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetingID).whereEqualTo("status", "Active").get().addOnSuccessListener { results ->
                        nBudgetItems += results.size()
                        for (budgetItem in results) {
                            var budgetItemObject = budgetItem.toObject<BudgetItem>()
                            checkOverSpending(budgetItem.id, budgetItemObject.amount!!)
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
//            overspendingPercentage = (overSpending/nBudgetItems)

            firestore.collection("Users").document(childID).get().addOnSuccessListener {
                var child = it.toObject<Users>()
                //compute age
                val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
                val from = LocalDate.now()
                val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
                val to = LocalDate.parse(date.toString(), dateFormatter)
                var difference = Period.between(to, from)

                var age = difference.years
                if (age > 9 ) {
                    purchasePlanning()
                }
                else {
                    overallSpending = (1-overspendingPercentage)*100
                    setOverall()
                }
            }
        }
    }



    private fun purchasePlanning() {
        //items planned / all the items they bought * 100
        firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "Completed").get().addOnSuccessListener { allSpendingActivities ->
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
                        overallSpending = (((1-overspendingPercentage)*100) + ((nPlanned/nTotalPurchased)*100)) /2
                        setOverall()
                    }
                }
            }
        }
    }

    private fun setOverall() {
        binding.textPerformance.text ="${DecimalFormat("##0.00").format(overallSpending)}%"

        if (overallSpending >= 96) {
            binding.imgFace.setImageResource(R.drawable.excellent)
            binding.textStatus.text = "Excellent"
            binding.textStatus.setTextColor(getResources().getColor(R.color.dark_green))
            binding.tvPerformanceText.text = "Your child excels at spending wisely. Encourage them to keep it up!"
            showSeeMoreButton()
        } else if (overallSpending < 96 && overallSpending >= 86) {
            binding.imgFace.setImageResource(R.drawable.amazing)
            binding.textStatus.text = "Amazing"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "Your child is amazing at spending! Encourage them to keep it up!"
            showSeeMoreButton()
        } else if (overallSpending < 86 && overallSpending >= 76) {
            binding.imgFace.setImageResource(R.drawable.great)
            binding.textStatus.text = "Great"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "Your child is doing a great job of spending wisely!"
            showSeeMoreButton()
        } else if (overallSpending < 76 && overallSpending >= 66) {
            binding.imgFace.setImageResource(R.drawable.good)
            binding.textStatus.text = "Good"
            binding.textStatus.setTextColor(getResources().getColor(R.color.light_green))
            binding.tvPerformanceText.text = "Your child is doing a good job of spending wisely!"
            showSeeMoreButton()
        } else if (overallSpending < 66 && overallSpending >= 56) {
            binding.imgFace.setImageResource(R.drawable.average)
            binding.textStatus.text = "Average"
            binding.textStatus.setTextColor(getResources().getColor(R.color.yellow))
            binding.tvPerformanceText.text = "Your child is doing a nice job of spending! Encourage them to plan ahead."
            showSeeMoreButton()
        } else if (overallSpending < 56 && overallSpending >= 46) {
            binding.imgFace.setImageResource(R.drawable.nearly_there)
            binding.textStatus.text = "Nearly There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Your child is nearly there! Click the tips button to learn how to help them get there!"
            showSeeMoreButton()
        }  else if (overallSpending < 46 && overallSpending >= 36) {
            binding.imgFace.setImageResource(R.drawable.almost_there)
            binding.textStatus.text = "Almost There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Your child is almost there! Click the tips button to learn how to help them get there!"
            showSeeMoreButton()
        } else if (overallSpending < 36 && overallSpending >= 26) {
            binding.imgFace.setImageResource(R.drawable.getting_there)
            binding.textStatus.text = "Getting There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Your child is getting there! Click the tips button to learn how to help them get there!"
            showSeeMoreButton()
        } else if (overallSpending < 26 && overallSpending >= 16) {
            binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
            binding.textStatus.text = "Not Quite\nThere"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Your child is not quite there yet! Click the tips button to learn how to help them get there!"
            showSeeMoreButton()
        } else if (overallSpending < 15) {
            binding.imgFace.setImageResource(R.drawable.bad)
            binding.textStatus.text = "Needs\nImprovement"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Uh oh! Click the tips button to learn how to help them improve their spending!"
            showSeeMoreButton()
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
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext, LinearLayoutManager.VERTICAL, false)
        spendingAdapter.notifyDataSetChanged()
    }

    private fun showSpendingReivewDialog() {

        var dialogBinding= DialogParentSpendingTipsBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}