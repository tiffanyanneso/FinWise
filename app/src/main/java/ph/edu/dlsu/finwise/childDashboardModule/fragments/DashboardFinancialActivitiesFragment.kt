package ph.edu.dlsu.finwise.childDashboardModule.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentDashboardFinancialActivitiesBinding
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class DashboardFinancialActivitiesFragment : Fragment() {
    private lateinit var binding: FragmentDashboardFinancialActivitiesBinding
    private var firestore = Firebase.firestore

    private lateinit var userID: String
    private var age = 0

    private var budgetItemCount = 0.00F
    private var nBudgetItems = 0.00F
    private var purchasedBudgetItemCount = 0.00F

    private var overSpending = 0.00F
    private var nPlanned = 0.00F
    private var nTotalPurchased = 0.00F


    private var nParent = 0

    private var savingPercentage = 0.00F
    private var spendingPercentage = 0.00F
    private var goalSettingPercentage = 0.00F
    private var budgetingPercentage = 0.00F
    private var overspendingPercentage = 0.00F

    private var nGoalSettingCompleted = 0
    private var nSpendingCompleted = 0
    private var nBudgetingCompleted = 0
    private var nSavingCompleted = 0

    private var totalBudgetAccuracy = 0.00F

    private var financialActivitiesPerformance = 0.00F






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_financial_activities, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDashboardFinancialActivitiesBinding.bind(view)

        getArgumentsBundle()
        getFinancialActivitiesPerformance()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getFinancialActivitiesPerformance() {
        CoroutineScope(Dispatchers.Main).launch {
            getChildAge()
            if (age == 10 || age == 11)
                getGoalSettingPerformance()
            else
                getSavingPerformanceScore()

            getBudgetingPerformanceScore()
            getSpendingPerformance()
        }
    }

    private suspend fun getSpendingPerformance() {
        //get budgeting items to see if they overspent for a specific budget item
        //get completed spending activities
        val financialActivitiesDocuments = firestore.collection("FinancialActivities")
            .whereEqualTo("childID", userID)
            .whereEqualTo("financialActivityName", "Spending")
            .whereEqualTo("status", "Completed").get().await()
            if (financialActivitiesDocuments.size() != 0) {
                nSpendingCompleted = financialActivitiesDocuments.size()
                for (spending in financialActivitiesDocuments) {
                    val spendingActivity = spending.toObject<FinancialActivities>()
                    println("print " + spendingActivity.financialGoalID)
                    val financialActivityDocuments = firestore.collection("FinancialActivities")
                        .whereEqualTo("financialGoalID", spendingActivity.financialGoalID)
                        .whereEqualTo("financialActivityName", "Budgeting")
                        .whereEqualTo("status", "Completed").get().await()
                        val budgetingID = financialActivityDocuments.documents[0].id
                        val budgetItemsDocuments = firestore.collection("BudgetItems")
                            .whereEqualTo("financialActivityID", budgetingID)
                            .whereEqualTo("status", "Active").get().await()
                            nBudgetItems += budgetItemsDocuments.size()
                            for (budgetItem in budgetItemsDocuments) {

                                val budgetItemObject = budgetItem.toObject<BudgetItem>()
                                checkOverSpending(budgetItem.id, budgetItemObject.amount!!)
                            }
                }
            }
    }

    private suspend fun checkOverSpending(budgetItemID:String, budgetItemAmount:Float){
        val transactionsDocuments = firestore.collection("Transactions")
            .whereEqualTo("budgetItemID", budgetItemID)
            .whereEqualTo("transactionType", "Expense").get().await()
            var amountSpent = 0.00F
            for (expense in transactionsDocuments) {
                val expenseObject = expense.toObject<Transactions>()
                amountSpent+= expenseObject.amount!!
            }
            //they spent more than their allocated budget
            if (amountSpent > budgetItemAmount)
                overSpending++

            overspendingPercentage = (overSpending/nBudgetItems)
            if (age > 9 ) {
                CoroutineScope(Dispatchers.Main).launch {
                    purchasePlanning()
                    calculateFinancialAssessmentScore()
                }
            }
            else {
                spendingPercentage = (1-overspendingPercentage)*100
//                println("print goal  setting " + goalSettingPercentage)
//                println("print savings " +  savingPercentage)
//                println("print budgeting " +  budgetingPercentage)
//                println("print spending " + spendingPercentage)
                financialActivitiesPerformance = ((savingPercentage + budgetingPercentage + spendingPercentage) / 3)
                binding.tvFinancialActivitiesPercentage.text = DecimalFormat("##0.00").format(financialActivitiesPerformance) + "%"
                Log.d("financialActivitiesPerformance", "calculateFinancialAssessmentScore: "+financialActivitiesPerformance)
            }

    }

    private fun calculateFinancialAssessmentScore() {
        if (age == 9 || age == 12)
            financialActivitiesPerformance = ((savingPercentage + budgetingPercentage + spendingPercentage) / 3)
        else if (age == 10 || age == 11)
            financialActivitiesPerformance = ((goalSettingPercentage + savingPercentage + budgetingPercentage + spendingPercentage) / 4)

        binding.progressBarFinancialActivities.progress = financialActivitiesPerformance.toInt()
        binding.tvFinancialActivitiesPercentage.text = DecimalFormat("##0.00").format(financialActivitiesPerformance) + "%"
        Log.d("financialActivitiesPerformance", "calculateFinancialAssessmentScore: "+financialActivitiesPerformance)
    }

    private suspend fun purchasePlanning() {
        //items planned / all the items they bought * 100
        val financialActivitiesDocuments = firestore.collection("FinancialActivities")
            .whereEqualTo("childID", userID)
            .whereEqualTo("financialActivityName", "Spending")
            .whereEqualTo("status", "Completed").get().await()
            for (spendingActivityID in financialActivitiesDocuments) {
                val shoppingListItemsDocuments = firestore.collection("ShoppingListItems")
                    .whereEqualTo("spendingActivityID", spendingActivityID.id).get().await()
                    for (shoppingListItem in shoppingListItemsDocuments) {
                        val shoppingListItemObject = shoppingListItem.toObject<ShoppingListItem>()
                        if (shoppingListItemObject.status == "Purchased")
                            nPlanned++
                    }
                    val transactionsDocuments = firestore.collection("Transactions")
                        .whereEqualTo("financialActivityID", spendingActivityID.id)
                        .whereEqualTo("transactionType", "Expense").get().await()
                        nTotalPurchased += transactionsDocuments.size().toFloat()
                        spendingPercentage = ((1-overspendingPercentage)*100 + ((nPlanned/nTotalPurchased)*100)) /2

            }

    }

    private suspend fun getBudgetingPerformanceScore() {
        val financialActivitiesDocuments = firestore.collection("FinancialActivities")
            .whereEqualTo("childID", userID)
            .whereEqualTo("financialActivityName", "Budgeting")
            .whereEqualTo("status", "Completed").get().await()
                nBudgetingCompleted = financialActivitiesDocuments.size()
                if (financialActivitiesDocuments.size() != 0) {
                    for (activity in financialActivitiesDocuments) {
                        processBudgetItems(activity)
                    }
                }

    }


    private suspend fun processBudgetItems(activity: QueryDocumentSnapshot?) {
        val budgetItemsDocuments = firestore.collection("BudgetItems")
            .whereEqualTo("financialActivityID", activity?.id)
            .whereEqualTo("status", "Active").get().await()
        for (budgetItem in budgetItemsDocuments) {
            budgetItemCount++
            val budgetItemObject = budgetItem.toObject<BudgetItem>()
            //parental involvement
            getParentalInvolvementBudget(budgetItemObject)
            if (activity != null) {
                getBudgetAccuracy(activity.id, budgetItem.id, budgetItemObject)
            }

        }
    }

    private suspend fun getBudgetAccuracy(budgetingActivityID:String, budgetItemID:String,
                                          budgetItemObject:BudgetItem) {
        val financialActivitiesDocument = firestore.collection("FinancialActivities")
            .document(budgetingActivityID).get().await()
            val financialActivitiesDocuemnts = firestore.collection("FinancialActivities")
                .whereEqualTo("financialGoalID",
                    financialActivitiesDocument.toObject<FinancialActivities>()!!.financialGoalID!!)
                .whereEqualTo("financialActivityName", "Spending").get().await()
                val spendingActivity = financialActivitiesDocuemnts.documents[0].toObject<FinancialActivities>()
                if (spendingActivity?.status == "Completed") {
                    //budget accuracy
                    purchasedBudgetItemCount++
                val transactionsDocuments = firestore.collection("Transactions")
                    .whereEqualTo("budgetItemID", budgetItemID).get().await()
                    var spent = 0.00F
                    for (transaction in transactionsDocuments)
                        spent += transaction.toObject<Transactions>().amount!!
                    totalBudgetAccuracy +=
                        (100 - (kotlin.math.abs(budgetItemObject.amount!! - spent)
                                / budgetItemObject.amount!!) * 100)

                    budgetingPercentage = if (purchasedBudgetItemCount != 0.00F)
                        ((totalBudgetAccuracy/purchasedBudgetItemCount)
                                + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2
                    else
                        ((1 - (nParent.toFloat()/budgetItemCount)) * 100)

                } else {
                    budgetingPercentage = if (purchasedBudgetItemCount != 0.00F)
                        ((totalBudgetAccuracy/purchasedBudgetItemCount) + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2
                    else
                        ((1 - (nParent.toFloat()/budgetItemCount)) * 100)
            }
                //println("print budgeting " + budgetingPercentage )


    }


    private suspend fun getParentalInvolvementBudget(budgetItemObject: BudgetItem) {
        val userDocument = firestore.collection("Users")
            .document(budgetItemObject.createdBy.toString()).get().await()
            //parent is the one who added the budget item
            if (userDocument.toObject<Users>()!!.userType == "Parent")
                nParent++

    }

    private suspend fun getGoalSettingPerformance() {
        var overallRating = 0.00F
        val nRatings: Int
        val goalRatingDocuments = firestore.collection("GoalRating")
            .whereEqualTo("childID", userID).get().await()
            nGoalSettingCompleted = goalRatingDocuments.size()
            nRatings = goalRatingDocuments.size()
            for (rating in goalRatingDocuments) {
                val ratingObject = rating.toObject<GoalRating>()
                overallRating += ratingObject.overallRating!!
            }
            goalSettingPercentage = 0.00F

            if (nRatings != 0)
                goalSettingPercentage = ((overallRating / nRatings)/5)* 100

            getSavingPerformanceScore()
    }

    private suspend fun getSavingPerformanceScore() {
        var nGoals = 0.00F
        var nOnTime = 0.00F
        val financialGoalsDocuments = firestore.collection("FinancialGoals")
            .whereEqualTo("childID", userID)
            .whereEqualTo("status", "Completed").get().await()
            nSavingCompleted = financialGoalsDocuments.size()
        Log.d("lklk", "look: "+nSavingCompleted)

        if (financialGoalsDocuments.size() != 0) {
                for (goal in financialGoalsDocuments) {
                    nGoals++
                    val goalObject = goal.toObject<FinancialGoals>()
                    if (goalObject.dateCompleted != null) {
                        val targetDate = goalObject.targetDate!!.toDate()
                        val completedDate = goalObject.dateCompleted!!.toDate()

                        //goal was completed before the target date, meaning it was completed on time
                        if (completedDate.before(targetDate) || completedDate == targetDate)
                            nOnTime++
                    }
                }
                savingPercentage = (nOnTime/nGoals)*100
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getChildAge() {
        val ageDocument = firestore.collection("Users").document(userID).get().await()
        val child = ageDocument.toObject<Users>()
        //compute age
        Log.d("fsdfxcvx", "getChildAge: "+child?.birthday?.toDate())
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val from = LocalDate.now()
        val date = SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
        val to = LocalDate.parse(date.toString(), dateFormatter)
        val difference = Period.between(to, from)

        age = difference.years
    }

    private fun getArgumentsBundle() {
        val args = arguments
        userID = args?.getString("userID").toString()
        Log.d("citizen", "getArgumentsBundle: "+userID)
    }


}