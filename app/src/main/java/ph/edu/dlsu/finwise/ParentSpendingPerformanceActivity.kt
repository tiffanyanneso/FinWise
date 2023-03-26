package ph.edu.dlsu.finwise

import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityParentSpendingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.ActivitySpendingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.DialogParentOverspendingTipsBinding
import ph.edu.dlsu.finwise.databinding.DialogParentPurchasePlanningTipsBinding
import ph.edu.dlsu.finwise.databinding.DialogParentSpendingTipsBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.SpendingFragment
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.ShoppingListItem
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class ParentSpendingPerformanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParentSpendingPerformanceBinding
    private var firestore = Firebase.firestore

    var budgetItemsIDArrayList = ArrayList<SpendingFragment.BudgetItemAmount>()
    var goalFilterArrayList = ArrayList<SpendingFragment.GoalFilter>()

    private var overSpending = 0.00F
    private var nBudgetItems = 0.00F

    var overspendingPercentage = 0.00F
    var purchasePlanningPercentage = 0.00F
    var overallSpending = 0.00F

    private lateinit var childID: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentSpendingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = Bundle()
        childID = bundle.getString("childID").toString()

        budgetItemsIDArrayList.clear()
        getBudgeting()

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav_parent), this, R.id.nav_goal)

        binding.btnTips.setOnClickListener {
            showSpendingReivewDialog()
        }

        binding.btnOverspendingTips.setOnClickListener {
            showOverspendingReivewDialog()
        }

        binding.btnPurchasePlanningTips.setOnClickListener {
            showSpendingPurchasePlanningReviewDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getBudgeting() {
        val budgetingActivityIDArrayList = ArrayList<String>()
        //get only completed budgeting activities because they should complete budgeting first before they are able to spend
        firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
            for (activity in results)
                budgetingActivityIDArrayList.add(activity.id)
            println("print number of budgeting activity " + budgetingActivityIDArrayList.size)

            for (budgetingID in budgetingActivityIDArrayList) {
                firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetingID).whereEqualTo("status", "Active").get().addOnSuccessListener { results ->
                    nBudgetItems += results.size()
                    for (budgetItem in results) {

                        val budgetItemObject = budgetItem.toObject<BudgetItem>()
                        checkOverSpending(budgetItem.id, budgetItemObject.amount!!)
//                        budgetItemsIDArrayList.add(BudgetItemAmount(budgetItem.id, budgetItemObject.amount!!))
//                        println("print add item in budgetItems array list")
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
                    binding.linearLayoutOverspending.visibility = View.VISIBLE
                    binding.linearLayoutPurchasePlanning.visibility = View.VISIBLE
                    purchasePlanning()
                }
                else {
                    overallSpending = overspendingPercentage
                    overallPercentage()
                    binding.linearLayoutOverspending.visibility = View.GONE
                    binding.linearLayoutPurchasePlanning.visibility = View.GONE
                }
            }

            overspendingPercentage = (overSpending/nBudgetItems)*100
            binding.progressBarOverspending.progress = overspendingPercentage.toInt()
            binding.textOverspendingProgress.text  = DecimalFormat("##0.00").format(overspendingPercentage) + "%"

            if (overspendingPercentage >= 96) {
                binding.textOverspendingText.text = "Excellent"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.dark_green))
                binding.tvOverspendingText.text = "Your child is excellent at spending within their budget!"
            } else if (overspendingPercentage < 96 && overspendingPercentage >= 86) {
                binding.imgFace.setImageResource(R.drawable.amazing)
                binding.textOverspendingText.text = "Amazing"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.green))
                binding.tvPerformanceText.text = "Your child is amazing at spending within their budget!"
            } else if (overspendingPercentage < 90 && overspendingPercentage >= 80) {
                binding.textOverspendingText.text = "Great"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.green))
                binding.tvOverspendingText.text = "Great! Your child often spends within their budget."
            } else if (overspendingPercentage < 90 && overspendingPercentage >= 80) {
                binding.textOverspendingText.text = "Good"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.light_green))
                binding.tvOverspendingText.text = "Good! Remind your child to keep their budget in mind when spending."
            } else if (overspendingPercentage < 70 && overspendingPercentage >= 60) {
                binding.textOverspendingText.text = "Average"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.yellow))
                binding.tvOverspendingText.text = "Nice work! Remind your child to keep their budget in mind when spending."
            } else if (overspendingPercentage < 56 && overspendingPercentage >= 46) {
                binding.imgFace.setImageResource(R.drawable.nearly_there)
                binding.textOverspendingText.text = "Nearly There"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Your child is nearly there! Click the tips button to learn how to help them there!"
            }  else if (overspendingPercentage < 46 && overspendingPercentage >= 36) {
                binding.imgFace.setImageResource(R.drawable.almost_there)
                binding.textOverspendingText.text = "Almost There"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Your child is almost there! Click the tips button to learn how to help them there!"
            } else if (overspendingPercentage < 36 && overspendingPercentage >= 26) {
                binding.imgFace.setImageResource(R.drawable.getting_there)
                binding.textOverspendingText.text = "Getting There"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Your child is getting there! Encourage them to always refer to their budget. Click the tips button to learn how to help them get there!"
            } else if (overspendingPercentage < 26 && overspendingPercentage >= 16) {
                binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                binding.textOverspendingText.text  = "Not Quite\nThere"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.red))
                binding.tvPerformanceText.text = "Your child is not quite there yet! Click the tips button to learn how to help them get there!"
            } else if (overspendingPercentage < 15) {
                binding.textOverspendingText.text = "Needs\nImprovement"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.red))
                binding.tvOverspendingText.text = "Uh oh! Click the tips button to learn how to help them get there!"
            }
        }
    }

    private fun purchasePlanning() {
        //items planned / all the items they bought * 100
        var nPlanned = 0.00F
        var nTotalPurchased = 0.00F
        firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Spending").get().addOnSuccessListener { allSpendingActivities ->
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
                        val purchasePlanningPercentage = (nPlanned/nTotalPurchased)*100

                        binding.progressBarPurchasePlanning.progress = purchasePlanningPercentage.toInt()
                        binding.textPurchasePlanning.text  = DecimalFormat("##0.00").format(overspendingPercentage) + "%"

                        if (purchasePlanningPercentage >= 90) {
                            binding.textPurchasePlanningText.text = "Excellent"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.dark_green))
                            binding.tvPurchasePlanningText.text = "Excellent! Your child always plans for their expenses."
                        } else if (purchasePlanningPercentage < 96 && purchasePlanningPercentage >= 86) {
                            binding.imgFace.setImageResource(R.drawable.amazing)
                            binding.textOverspendingText.text = "Amazing"
                            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.green))
                            binding.tvPerformanceText.text = "Your child is doing an amazing job! They always plan for your expenses."
                        } else if (purchasePlanningPercentage < 90 && purchasePlanningPercentage >= 80) {
                            binding.textPurchasePlanningText.text = "Great"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.green))
                            binding.tvPurchasePlanningText.text = "Your child is doing a great job planning their purchases."
                        } else if (purchasePlanningPercentage < 80 && purchasePlanningPercentage >= 70) {
                            binding.textPurchasePlanningText.text = "Good"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.light_green))
                            binding.tvPurchasePlanningText.text = "Your child is doing a good job! Encourage them to list down the items they wanna buy in their shopping list."
                        } else if (purchasePlanningPercentage < 70 && purchasePlanningPercentage >= 60) {
                            binding.textPurchasePlanningText.text = "Average"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.yellow))
                            binding.tvPurchasePlanningText.text = "Nice Work! To help your child improve, encourage them to plan thei expenses more via the shopping list."
                        } else if (purchasePlanningPercentage < 56 && purchasePlanningPercentage >= 46) {
                            binding.imgFace.setImageResource(R.drawable.nearly_there)
                            binding.textOverspendingText.text = "Nearly There"
                            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.red))
                            binding.tvPerformanceText.text = "Your child is nearly there! Click the tips button to learn how to help them get there!"
                        }  else if (purchasePlanningPercentage < 46 && purchasePlanningPercentage >= 36) {
                            binding.imgFace.setImageResource(R.drawable.almost_there)
                            binding.textOverspendingText.text = "Almost There"
                            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.red))
                            binding.tvPerformanceText.text = "Almost there! You need to work on your spending. Click review to learn how!"
                        } else if (purchasePlanningPercentage < 36 && purchasePlanningPercentage >= 26) {
                            binding.imgFace.setImageResource(R.drawable.getting_there)
                            binding.textOverspendingText.text = "Getting\nThere"
                            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.red))
                            binding.tvPerformanceText.text = "Your child is getting there! Click the tips button to learn how to help them get there!"
                        } else if (purchasePlanningPercentage < 26 && purchasePlanningPercentage >= 16) {
                            binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                            binding.textOverspendingText.text  = "Not Quite\nThere"
                            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.red))
                            binding.tvPerformanceText.text = "Your child is not quite there yet! Click the tips button to learn how to help them get there!"
                        } else if (purchasePlanningPercentage < 60) {
                            binding.textPurchasePlanningText.text = "Needs\nImprovement"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.red))
                            binding.tvPurchasePlanningText.text = "Uh oh! Click the tips button to learn how to help them improve their expense planning!"
                        }

                        overallSpending = (overspendingPercentage + ((nPlanned/nTotalPurchased)*100)) /2
                        binding.tvPerformancePercentage.text ="${DecimalFormat("0.0").format(overallSpending)}%"

                        overallPercentage()
                    }
                }
            }
        }
    }

    private fun overallPercentage() {
        if (overallSpending >= 96) {
            binding.imgFace.setImageResource(R.drawable.excellent)
            binding.tvPerformanceStatus.text = "Excellent"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.dark_green))
            binding.tvPerformanceText.text = "Your child excels at spending wisely. Encourage them to keep it up!"
            binding.imgFace.setImageResource(R.drawable.amazing)
            binding.tvPerformanceStatus.text = "Amazing"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.green))
            binding.tvPerformanceText.text = "Your child is doing an amazing job at spending wisely. Encourage them to keep it up!"
        } else if (overallSpending < 90 && overallSpending >= 80) {
            binding.imgFace.setImageResource(R.drawable.great)
            binding.tvPerformanceStatus.text = "Great"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.green))
            binding.tvPerformanceText.text = "Your child is doing a great job of spending!"
        } else if (overallSpending < 80 && overallSpending >= 70) {
            binding.imgFace.setImageResource(R.drawable.good)
            binding.tvPerformanceStatus.text = "Good"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.light_green))
            binding.tvPerformanceText.text = "Your child is doing a good job of spending wisely!"
        } else if (overallSpending < 70 && overallSpending >= 60) {
            binding.imgFace.setImageResource(R.drawable.average)
            binding.tvPerformanceStatus.text = "Average"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.yellow))
            binding.tvPerformanceText.text = "Your child is doing a nice job of spending wisely! Encourage them to always planning ahead."
        } else if (overallSpending < 56 && overallSpending >= 46) {
            binding.imgFace.setImageResource(R.drawable.nearly_there)
            binding.tvPerformanceStatus.text = "Nearly There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.red))
            binding.tvPerformanceText.text = "Your child is nearly there! Click the tips button to learn how to help them get there!"
        }  else if (overallSpending < 46 && overallSpending >= 36) {
            binding.imgFace.setImageResource(R.drawable.almost_there)
            binding.tvPerformanceStatus.text = "Almost There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.red))
            binding.tvPerformanceText.text = "Your child is almost there! Click the tips button to learn how to help them get there!"
        } else if (overallSpending < 36 && overallSpending >= 26) {
            binding.imgFace.setImageResource(R.drawable.getting_there)
            binding.tvPerformanceStatus.text = "Getting There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.red))
            binding.tvPerformanceText.text = "Your child is getting there! Click the tips button to learn how to help them get there!"
        } else if (overallSpending < 26 && overallSpending >= 16) {
            binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
            binding.tvPerformanceStatus.text = "Not Quite\nThere"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.red))
            binding.tvPerformanceText.text = "Your child is not quite there yet! Click the tips button to learn how to help them get there!"
        } else if (overallSpending < 15) {
            binding.imgFace.setImageResource(R.drawable.bad)
            binding.tvPerformanceStatus.text = "Needs\nImprovement"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.red))
            binding.tvPerformanceText.text = "Uh oh! Click the tips button to learn how to help them improve their goal setting!"
        }
    }
    private fun showSpendingReivewDialog() {

        var dialogBinding= DialogParentSpendingTipsBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1400)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showOverspendingReivewDialog() {

        var dialogBinding= DialogParentOverspendingTipsBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1400)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showSpendingPurchasePlanningReviewDialog() {

        var dialogBinding = DialogParentPurchasePlanningTipsBinding.inflate(getLayoutInflater())
        var dialog = Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1400)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}