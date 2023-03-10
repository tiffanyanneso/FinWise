package ph.edu.dlsu.finwise.financialActivitiesModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactSpendingAdapter
import ph.edu.dlsu.finwise.databinding.ActivitySpendingPerformanceBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.SpendingFragment
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.ShoppingListItem
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat

class SpendingPerformanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpendingPerformanceBinding
    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    var budgetItemsIDArrayList = ArrayList<SpendingFragment.BudgetItemAmount>()
    var goalFilterArrayList = ArrayList<SpendingFragment.GoalFilter>()

    private var overSpending = 0.00F
    private var nBudgetItems = 0.00F

    var overspendingPercentage = 0.00F
    var purchasePlanningPercentage = 0.00F
    var overallSpending = 0.00F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpendingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        budgetItemsIDArrayList.clear()

        getBudgeting()
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
            binding.textOverspendingProgress.text  = DecimalFormat("##0.00").format(overspendingPercentage) + "%"

            // TODO add if else statements for customized text based on percentage

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
                        overallSpending = (overspendingPercentage + ((nPlanned/nTotalPurchased)*100)) /2

                        binding.tvPerformancePercentage.text ="${overallSpending}%"

                        if (overallSpending >= 90) {
                            binding.imgFace.setImageResource(R.drawable.excellent)
                            binding.tvPerformanceStatus.text = "Excellent"
                            binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.dark_green))
                            binding.tvPerformanceText.text = "Keep up the excellent work! Spending wisely is your strong point. Keep it up!"
                        } else if (overallSpending < 90 && overallSpending >= 80) {
                            binding.imgFace.setImageResource(R.drawable.great)
                            binding.tvPerformanceStatus.text = "Great"
                            binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.green))
                            binding.tvPerformanceText.text = " Great job! You are performing well. Keep spending wisely!"
                        } else if (overallSpending < 80 && overallSpending >= 70) {
                            binding.imgFace.setImageResource(R.drawable.good)
                            binding.tvPerformanceStatus.text = "Good"
                            binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.light_green))
                            binding.tvPerformanceText.text = "Good job! With a bit more planning to detail, you’ll surely up your performance!"
                        } else if (overallSpending < 70 && overallSpending >= 60) {
                            binding.imgFace.setImageResource(R.drawable.average)
                            binding.tvPerformanceStatus.text = "Average"
                            binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.yellow))
                            binding.tvPerformanceText.text = "Nice work! Work on improving your spending performance by always planning ahead. You’ll get there soon!"
                        } else if (overallSpending < 60) {
                            binding.imgFace.setImageResource(R.drawable.bad)
                            binding.tvPerformanceStatus.text = "Bad"
                            binding.tvPerformanceStatus.setTextColor(getResources().getColor(R.color.red))
                            binding.tvPerformanceText.text = "Your spending performance needs a lot of improvement. Click review to learn how!"
                        }
                    }
                }
            }
        }
    }
}