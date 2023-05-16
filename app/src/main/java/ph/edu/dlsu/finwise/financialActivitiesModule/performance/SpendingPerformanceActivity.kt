package ph.edu.dlsu.finwise.financialActivitiesModule.performance

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivitySpendingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.DialogOverspendingReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogPurchasePlanningReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogSpendingReviewBinding
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpendingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        budgetItemsIDArrayList.clear()

        getBudgeting()
        setNavigationBar()
        loadBackButton()

        binding.btnReview.setOnClickListener {
            showSpendingReivewDialog()
        }

        binding.btnOverspendingReview.setOnClickListener {
            showOverspendingReivewDialog()
        }

        binding.btnPurchasePlanningReview.setOnClickListener {
            showSpendingPurchasePlanningReviewDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getBudgeting() {
        val budgetingActivityIDArrayList = ArrayList<String>()
        //get only completed budgeting activities because they should complete budgeting first before they are able to spend
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
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
            overspendingPercentage = (overSpending/nBudgetItems)

            firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
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
                    overallSpending = (1-overspendingPercentage)*100
                    binding.tvPerformancePercentage.text ="${DecimalFormat("##0.0").format(overallSpending)}%"
                    overallPercentage()
                    binding.linearLayoutOverspending.visibility = View.VISIBLE
                    binding.linearLayoutPurchasePlanning.visibility = View.GONE
                }
            }

            binding.progressBarOverspending.progress = overspendingPercentage.toInt()
            binding.textOverspendingProgress.text  = DecimalFormat("##0.00").format(overspendingPercentage) + "%"


            if (overspendingPercentage < 5) {
                binding.textOverspendingText.text = "Excellent"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.dark_green))
                binding.tvOverspendingText.text = "Excellent work! You always spend within your budget. Keep it up"
            } else if (overspendingPercentage < 15 && overspendingPercentage >= 5) {
                binding.imgFace.setImageResource(R.drawable.amazing)
                binding.textOverspendingText.text = "Amazing"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.amazing_green))
                binding.tvOverspendingText.text = "Amazing job! You always spend within your budget. Keep it up!"
            } else if (overspendingPercentage < 25 && overspendingPercentage >= 15) {
                binding.textOverspendingText.text = "Great"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.green))
                binding.tvOverspendingText.text = "Great job!  You often spend within your budget. Keep it up!"
            } else if (overspendingPercentage < 35 && overspendingPercentage >= 25) {
                binding.textOverspendingText.text = "Good"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.light_green))
                binding.tvOverspendingText.text = "Good job! Remember to keep your budget in mind when spending!"
            } else if (overspendingPercentage < 45 && overspendingPercentage >= 35) {
                binding.textOverspendingText.text = "Average"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.yellow))
                binding.tvOverspendingText.text = "Nice work! Work on improving your performance by using your budget as a guide and thinking before your buy."
            } else if (overspendingPercentage < 55 && overspendingPercentage >= 45) {
                binding.imgFace.setImageResource(R.drawable.nearly_there)
                binding.textOverspendingText.text = "Nearly There"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
                binding.tvOverspendingText.text = "You're nearly there! Work on improving your performance by using your budget as a guide and thinking before your buy."
            }  else if (overspendingPercentage < 65 && overspendingPercentage >= 55) {
                binding.imgFace.setImageResource(R.drawable.almost_there)
                binding.textOverspendingText.text = "Almost There"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.almost_there_yellow))
                binding.tvOverspendingText.text = "Almost there! Work on improving your performance by using your budget as a guide and thinking before your buy."
            } else if (overspendingPercentage < 75 && overspendingPercentage >= 65) {
                binding.imgFace.setImageResource(R.drawable.getting_there)
                binding.textOverspendingText.text = "Getting There"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.getting_there_orange))
                binding.tvOverspendingText.text = "Getting there! Work on improving your performance by using your budget as a guide and thinking before your buy."
            } else if (overspendingPercentage < 85 && overspendingPercentage >= 75) {
                binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                binding.textOverspendingText.text  = "Not Quite\nThere"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.not_quite_there_red))
                binding.tvOverspendingText.text = "Not quite there yet! Don't give up. Work on improving your performance by using your budget as a guide and thinking before your buy."
            } else if (overspendingPercentage > 84) {
                binding.textOverspendingText.text = "Needs\nImprovement"
                binding.textOverspendingText.setTextColor(getResources().getColor(R.color.red))
                binding.tvOverspendingText.text = "Don't give up! Work on improving your performance by using your budget as a guide and thinking before your buy."
            }
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
                        val purchasePlanningPercentage = (nPlanned/nTotalPurchased)*100
                        binding.progressBarPurchasePlanning.progress = purchasePlanningPercentage.toInt()
                        binding.textPurchasePlanning.text  = DecimalFormat("##0.00").format(purchasePlanningPercentage) + "%"

                        if (purchasePlanningPercentage >= 96) {
                            binding.textPurchasePlanningText.text = "Excellent"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.dark_green))
                            binding.tvPurchasePlanningText.text = "Excellent job! You always plan for your expenses by putting them in your shopping list. Keep this up!"
                        } else if (purchasePlanningPercentage < 96 && purchasePlanningPercentage >= 86) {
                            binding.textOverspendingText.text = "Amazing"
                            binding.textOverspendingText.setTextColor(getResources().getColor(R.color.amazing_green))
                            binding.tvPurchasePlanningText.text = "Amazing job! You always plan for your expenses. Keep it up!"
                        } else if (purchasePlanningPercentage < 86 && purchasePlanningPercentage >= 76) {
                            binding.textPurchasePlanningText.text = "Great"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.green))
                            binding.tvPurchasePlanningText.text = "Great job planning your purchases in your shopping list. Keep this up!"
                        } else if (purchasePlanningPercentage < 76 && purchasePlanningPercentage >= 66) {
                            binding.textPurchasePlanningText.text = "Good"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.light_green))
                            binding.tvPurchasePlanningText.text = "Good job! Up your performance by listing down the items you wanna buy in the shopping list."
                        } else if (purchasePlanningPercentage < 66 && purchasePlanningPercentage >= 56) {
                            binding.textPurchasePlanningText.text = "Average"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.yellow))
                            binding.tvPurchasePlanningText.text = "Nice Work! To improve, you may want to plan your expenses more via the shopping list."
                        } else if (purchasePlanningPercentage < 56 && purchasePlanningPercentage >= 46) {
                            binding.textPurchasePlanningText.text = "Nearly There"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.nearly_there_yellow))
                            binding.tvPurchasePlanningText.text = "You're nearly there! Click review to learn how to get there!"
                        }  else if (purchasePlanningPercentage < 46 && purchasePlanningPercentage >= 36) {
                            binding.textPurchasePlanningText.text = "Almost There"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.almost_there_yellow))
                            binding.tvPurchasePlanningText.text = "Almost there! You need to work on your spending. Click review to learn how!"
                        } else if (purchasePlanningPercentage < 36 && purchasePlanningPercentage >= 26) {
                            binding.textPurchasePlanningText.text = "Getting There"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.getting_there_orange))
                            binding.tvPurchasePlanningText.text = "Getting there! You need to work on your spending. Click review to learn how!"
                        } else if (purchasePlanningPercentage < 26 && purchasePlanningPercentage >= 16) {
                            binding.textPurchasePlanningText.text  = "Not Quite\nThere"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.not_quite_there_red))
                            binding.tvPurchasePlanningText.text = "Not quite there yet! Don't give up. Click review to learn how to get there!"
                        } else if (purchasePlanningPercentage < 15) {
                            binding.textPurchasePlanningText.text = "Needs\nImprovement"
                            binding.textPurchasePlanningText.setTextColor(getResources().getColor(R.color.red))
                            binding.tvPurchasePlanningText.text = "Uh oh! Seems like you haven’t really been planning your expenses by putting them in your shopping list. Try this out next time!"
                        }


                        overallSpending = (((1-overspendingPercentage)*100) + purchasePlanningPercentage) /2
                        binding.tvPerformancePercentage.text ="${DecimalFormat("##0.0").format(overallSpending)}%"


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
            binding.tvPerformanceText.text = "Keep up the excellent work! Spending wisely is your strong point. Keep it up!"
        } else if (overallSpending < 96 && overallSpending >= 86) {
            binding.imgFace.setImageResource(R.drawable.amazing)
            binding.tvPerformanceStatus.text = "Amazing"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.amazing_green))
            binding.tvPerformanceText.text = "Amazing job! You are performing well. Spending wisely is your strong point. Keep completing those goals!"
        } else if (overallSpending < 86 && overallSpending >= 76) {
            binding.imgFace.setImageResource(R.drawable.great)
            binding.tvPerformanceStatus.text = "Great"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.green))
            binding.tvPerformanceText.text = " Great job! You are performing well. Keep spending wisely!"
        } else if (overallSpending < 76 && overallSpending >= 66) {
            binding.imgFace.setImageResource(R.drawable.good)
            binding.tvPerformanceStatus.text = "Good"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.light_green))
            binding.tvPerformanceText.text = "Good job! With a bit more planning to detail, you’ll surely up your performance!"
        } else if (overallSpending < 66 && overallSpending >= 56) {
            binding.imgFace.setImageResource(R.drawable.average)
            binding.tvPerformanceStatus.text = "Average"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.yellow))
            binding.tvPerformanceText.text = "Nice work! Work on improving your spending performance by always planning ahead. You’ll get there soon!"
        } else if (overallSpending < 56 && overallSpending >= 46) {
            binding.imgFace.setImageResource(R.drawable.nearly_there)
            binding.tvPerformanceStatus.text = "Nearly There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            binding.tvPerformanceText.text = "You're nearly there! Click review to learn how to get there!"
        }  else if (overallSpending < 46 && overallSpending >= 36) {
            binding.imgFace.setImageResource(R.drawable.almost_there)
            binding.tvPerformanceStatus.text = "Almost There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.almost_there_yellow))
            binding.tvPerformanceText.text = "Almost there! You need to work on your spending. Click review to learn how!"
        } else if (overallSpending < 36 && overallSpending >= 26) {
            binding.imgFace.setImageResource(R.drawable.getting_there)
            binding.tvPerformanceStatus.text = "Getting There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.getting_there_orange))
            binding.tvPerformanceText.text = "Getting there! You need to work on your spending. Click review to learn how!"
        } else if (overallSpending < 26 && overallSpending >= 16) {
            binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
            binding.tvPerformanceStatus.text = "Not Quite\nThere"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            binding.tvPerformanceText.text = "Not quite there yet! Don't give up. Click review to learn how to get there!"
        } else if (overallSpending < 15) {
            binding.imgFace.setImageResource(R.drawable.bad)
            binding.tvPerformanceStatus.text = "Needs\nImprovement"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.red))
            binding.tvPerformanceText.text = "Your spending performance needs a lot of improvement. Click review to learn how!"
        }  else {
        binding.imgFace.setImageResource(R.drawable.good)
        binding.tvPerformanceStatus.text = ""
        binding.tvPerformanceText.text = "Finish spending to see your performance"
    }
    }
    private fun showSpendingReivewDialog() {

        var dialogBinding= DialogSpendingReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showOverspendingReivewDialog() {

        var dialogBinding= DialogOverspendingReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showSpendingPurchasePlanningReviewDialog() {

        var dialogBinding= DialogPurchasePlanningReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setNavigationBar() {
        var navUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(navUser).get().addOnSuccessListener {

            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent

            if (it.toObject<Users>()!!.userType == "Parent") {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                //sends the ChildID to the parent navbar
                val bundle = intent.extras!!
                val childID = bundle.getString("childID").toString()
                val bundleNavBar = Bundle()
                bundleNavBar.putString("childID", childID)
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)
            } else if (it.toObject<Users>()!!.userType == "Child") {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

            }
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}