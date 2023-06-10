package ph.edu.dlsu.finwise.parentDashboardModule

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.databinding.FragmentParentDashboardBinding
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*


class ParentDashboardFragment : Fragment() {

    private lateinit var binding: FragmentParentDashboardBinding
    private var firestore = Firebase.firestore

    private lateinit var childID:String

    private var age = 0
    private var personalFinancePerformance = 0.00F

    private var goalSettingPercentage = 0.00F
    private var savingPercentage = 0.00F
    private var budgetingPercentage = 0.00F
    private var nParent = 0
    private var purchasedBudgetItemCount = 0.00F
    private var totalBudgetAccuracy = 0.00F
    //number of budget items, including those from ongoing budgeting activities
    private var budgetItemCount = 0.00F
    private var spendingPercentage = 0.00F
    //number of budget items of completed budgting activities
    private var nBudgetItems = 0.00F
    private var overSpending = 0.00F
    private var overspendingPercentage = 0.00F
    private var nPlanned = 0.00F
    private var nTotalPurchased = 0.00F

    private var financialActivitiesPerformance = 0.00F

    private var financialAssessmentPerformance = 0.00F

    private var nGoalSettingCompleted = 0
    private var nSavingCompleted = 0
    private var nBudgetingCompleted = 0
    private var nSpendingCompleted = 0
    //if there are no assessments taken yet, do not include in fin health computation
    private var nAssessmentsTaken = 0

    private var overallFinancialHealth = 0.00F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = requireArguments()
        childID = bundle.getString("childID").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            getAge()
            getPersonalFinancePerformance()
            //finact performance
            if (age == 10 || age == 11)
                getGoalSettingPerformance()
            else
                binding.layoutGoalSettingInProgress.visibility = View.GONE

            getSavingPerformanceScore()
            getBudgetingPerformanceScore()
            getSpendingPerformance()

            if (age > 9 )
                purchasePlanning()
            else spendingPercentage = (1-overspendingPercentage)*100

            getFinancialAssessmentScore()
            getOverallFinancialHealth()
        }


       //TODO: Set views
    //setPerformanceView()
    }

    private suspend fun getAge() {
        //get the age of the child
        var child = firestore.collection("Users").document(childID).get().await().toObject<Users>()
            //compute age
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val from = LocalDate.now()
        val date = SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
        val to = LocalDate.parse(date.toString(), dateFormatter)
        val difference = Period.between(to, from)

        age = difference.years
    }


    private suspend fun getPersonalFinancePerformance() {
        var income = 0.00F
        var expense = 0.00F
        var transactions = firestore.collection("Transactions").whereEqualTo("userID", childID).whereIn("transactionType", listOf("Income", "Expense")).get().await()
        for (transaction in transactions) {
            var transactionObject = transaction.toObject<Transactions>()
            if (transactionObject.transactionType == "Income")
                income += transactionObject.amount!!
            else if (transactionObject.transactionType == "Expense")
                expense += transactionObject.amount!!
        }

        var personalFinancePerformancePercent = income/expense * 100
        if (personalFinancePerformancePercent > 200)
            personalFinancePerformancePercent = 200F
        personalFinancePerformance = personalFinancePerformancePercent / 2

        binding.progressBarPersonalFinance.progress = personalFinancePerformance.toInt()
        binding.tvPersonalFinancePercentage.text = DecimalFormat("##0.0").format(personalFinancePerformance) + "%"

    }

    private suspend fun getGoalSettingPerformance() {
        var overallRating = 0.00F
        val nRatings: Int
        val goalRatingDocuments = firestore.collection("GoalRating").whereEqualTo("childID", childID).get().await()
        nGoalSettingCompleted = goalRatingDocuments.size()
        nRatings = goalRatingDocuments.size()
        for (rating in goalRatingDocuments) {
            val ratingObject = rating.toObject<GoalRating>()
            overallRating += ratingObject.overallRating!!
        }
        goalSettingPercentage = 0.00F

        if (nRatings != 0)
            goalSettingPercentage = ((overallRating / nRatings)/5)* 100

        var goalSettingOngoing = firestore.collection("FinancialGoals").whereEqualTo("childID", childID).whereIn("status", Arrays.asList("For Review", "For Editing")).get().await()
        binding.tvGoalSetting.text = goalSettingOngoing.size().toString()
    }

    private suspend fun getSavingPerformanceScore() {
        var nGoals = 0.00F
        var nOnTime = 0.00F
        val financialGoalsDocuments = firestore.collection("FinancialGoals")
            .whereEqualTo("childID", childID)
            .whereEqualTo("status", "Completed").get().await()
        nSavingCompleted = financialGoalsDocuments.size()

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

        var ongoingSaving = firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Saving").whereEqualTo("status", "Ongoing").get().await()
        binding.tvSaving.text = ongoingSaving.size().toString()
    }

    private suspend fun getBudgetingPerformanceScore() {
        val completedBudgetingActivities = firestore.collection("FinancialActivities")
            .whereEqualTo("childID", childID)
            .whereEqualTo("financialActivityName", "Budgeting")
            .whereEqualTo("status", "Completed").get().await()
        nBudgetingCompleted = completedBudgetingActivities.size()

        if (completedBudgetingActivities.size() != 0) {
            for (budgetActivity in completedBudgetingActivities) {
                processBudgetItems(budgetActivity)
            }
        }

        var ongoingSaving = firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Ongoing").get().await()
        binding.tvBudgeting.text = ongoingSaving.size().toString()
    }

    private suspend fun processBudgetItems(budgetActivity: QueryDocumentSnapshot?) {
        val budgetItemsDocuments = firestore.collection("BudgetItems")
            .whereEqualTo("financialActivityID", budgetActivity?.id)
            .whereEqualTo("status", "Active").get().await()

        for (budgetItem in budgetItemsDocuments) {
            budgetItemCount++
            val budgetItemObject = budgetItem.toObject<BudgetItem>()
            //parental involvement
            getParentalInvolvementBudget(budgetItemObject)
            if (activity != null) {
                getBudgetAccuracy(budgetActivity?.id!!, budgetItem.id, budgetItemObject)
            }
        }
    }

    private suspend fun getBudgetAccuracy(budgetingActivityID:String, budgetItemID:String, budgetItemObject: BudgetItem) {
        val budgetingActivity = firestore.collection("FinancialActivities")
            .document(budgetingActivityID).get().await()

        val spendingActivity = firestore.collection("FinancialActivities")
            .whereEqualTo("financialGoalID", budgetingActivity.toObject<FinancialActivities>()!!.financialGoalID!!)
            .whereEqualTo("financialActivityName", "Spending").get().await().documents[0].toObject<FinancialActivities>()

        if (spendingActivity?.status == "Completed") {
            //budget accuracy
            purchasedBudgetItemCount++
            val transactionsDocuments = firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID).get().await()
            var spent = 0.00F
            for (transaction in transactionsDocuments)
                spent += transaction.toObject<Transactions>().amount!!

            totalBudgetAccuracy += (100 - (kotlin.math.abs(budgetItemObject.amount!! - spent) / budgetItemObject.amount!!) * 100)

            budgetingPercentage = if (purchasedBudgetItemCount != 0.00F)
                ((totalBudgetAccuracy/purchasedBudgetItemCount) + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2
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
        val userDocument = firestore.collection("Users").document(budgetItemObject.createdBy.toString()).get().await()
        //parent is the one who added the budget item
        if (userDocument.toObject<Users>()?.userType == "Parent")
            nParent++
    }



    private suspend fun getSpendingPerformance() {
        //get budgeting items to see if they overspent for a specific budget item
        //get completed spending activities
        val financialActivitiesDocuments = firestore.collection("FinancialActivities")
            .whereEqualTo("childID", childID)
            .whereEqualTo("financialActivityName", "Spending")
            .whereEqualTo("status", "Completed").get().await()
        if (financialActivitiesDocuments.size() != 0) {
            nSpendingCompleted = financialActivitiesDocuments.size()
            for (spending in financialActivitiesDocuments) {
                val spendingActivity = spending.toObject<FinancialActivities>()
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

        var ongoingSaving = firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "Ongoing").get().await()
        binding.tvSpending.text = ongoingSaving.size().toString()
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
    }


    private suspend fun purchasePlanning() {
        //items planned / all the items they bought * 100
        val spendingActivities = firestore.collection("FinancialActivities")
            .whereEqualTo("childID", childID)
            .whereEqualTo("financialActivityName", "Spending")
            .whereEqualTo("status", "Completed").get().await()

        for (spendingActivityID in spendingActivities) {
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

    private fun checkIfNaNFinancialActivitiesScores() {
        val percentages = mutableListOf(savingPercentage, spendingPercentage, budgetingPercentage,
            goalSettingPercentage)

        for (i in percentages.indices) {
            if (percentages[i].isNaN()) {
                when (i) {
                    0 -> savingPercentage = 0.00f
                    1 -> spendingPercentage = 0.00f
                    2 -> budgetingPercentage = 0.00f
                    3 -> goalSettingPercentage = 0.00f
                }
            }
        }
    }

    private suspend fun getFinancialAssessmentScore() {
        var nCorrect = 0
        var nQuestions = 0
        var results = firestore.collection("AssessmentAttempts").whereEqualTo("childID", childID).get().await()
        if (results.size() != 0) {
            for (attempt in results) {
                val assessmentAttempt = attempt.toObject<FinancialAssessmentAttempts>()
                if (assessmentAttempt.nAnsweredCorrectly != null && assessmentAttempt.nQuestions != null) {
                    nCorrect += assessmentAttempt.nAnsweredCorrectly!!
                    nQuestions += assessmentAttempt.nQuestions!!
                }
            }
//            println("print correct " + nCorrect)
//            println("print nquestions" + nQuestions)
            financialAssessmentPerformance = (nCorrect.toFloat() / nQuestions.toFloat()) * 100
            binding.progressBarFinancialAssessments.progress = financialAssessmentPerformance.toInt()
            binding.tvFinancialAssessmentsPercentage.text = DecimalFormat("##0.00").format(financialAssessmentPerformance) + "%"
        }
    }

    private fun checkIfNaN() {
        val percentages = mutableListOf(personalFinancePerformance, financialAssessmentPerformance,
            financialActivitiesPerformance, savingPercentage, spendingPercentage,
            budgetingPercentage, goalSettingPercentage)

        for (i in percentages.indices) {
            if (percentages[i].isNaN()) {
                when (i) {
                    0 -> personalFinancePerformance = 0.00f
                    1 -> financialAssessmentPerformance = 0.00f
                    2 -> financialActivitiesPerformance = 0.00f
                    3 -> savingPercentage = 0.00f
                    4 -> spendingPercentage = 0.00f
                    5 -> budgetingPercentage = 0.00f
                    6 -> goalSettingPercentage = 0.00f
                }
            }
        }
    }


    private fun getOverallFinancialHealth(){
        checkIfNaN()
        if (age == 9 || age == 12) {
            //no saving completed yet, score will be personal finance and financial assessment (prelim)
             if(nSavingCompleted == 0) {
                 overallFinancialHealth = (((personalFinancePerformance * .5) + financialAssessmentPerformance * .5) * 100).toFloat()
                 binding.tvFinancialActivitiesPercentage.text = "N/A"
            }

            //no budgeting completed yet, score will be personal finance, saving performance,  financial assessment (prelim)
            else if (nBudgetingCompleted == 0) {
                overallFinancialHealth = ((personalFinancePerformance * .35) + (savingPercentage  * .35) + (financialAssessmentPerformance * .30)).toFloat()
                 binding.tvFinancialActivitiesPercentage.text = DecimalFormat("##0.0").format(savingPercentage) + "%"
                 binding.progressBarFinancialActivities.progress = savingPercentage.toInt()
            }

            //no spending completed yet, score will be personal finance, saving performance, budgeting performance,  financial assessment (prelim)
            else if (nSpendingCompleted == 0) {
                 overallFinancialHealth =
                     ((personalFinancePerformance * .35) + (((savingPercentage + budgetingPercentage) / 2) * .35) + (financialAssessmentPerformance * .30)).toFloat()
                 binding.tvFinancialActivitiesPercentage.text = DecimalFormat("##0.0").format((savingPercentage+budgetingPercentage)/2) + "%"
                 binding.progressBarFinancialActivities.progress = ((savingPercentage+budgetingPercentage)/2).toInt()

             }
            else {
                 overallFinancialHealth =
                     ((personalFinancePerformance * .35) + (((savingPercentage + budgetingPercentage + spendingPercentage) / 3) * .35) + (financialAssessmentPerformance * .30)).toFloat()
                 binding.tvFinancialActivitiesPercentage.text = DecimalFormat("##0.0").format(((savingPercentage + budgetingPercentage + spendingPercentage) / 3)) + "%"
                 binding.progressBarFinancialActivities.progress = ((savingPercentage + budgetingPercentage + spendingPercentage) / 3).toInt()

             }

        } else if (age == 10 || age == 11) {
            //no goal setting completed yet, score will be personal finance, and  financial assessment (prelim)
            if (nGoalSettingCompleted == 0) {
                overallFinancialHealth = (((personalFinancePerformance * .5) + financialAssessmentPerformance * .5) * 100).toFloat()
                binding.tvFinancialActivitiesPercentage.text = "N/A"
            }

            //no saving completed yet, score will be personal finance, goal setting, and financial assessment (prelim)
            else if(nSavingCompleted == 0) {
                overallFinancialHealth = ((personalFinancePerformance * .35) + (goalSettingPercentage * .35) + (financialAssessmentPerformance * .30)).toFloat()
                binding.tvFinancialActivitiesPercentage.text = DecimalFormat("##0.0").format(goalSettingPercentage) + "%"
                binding.progressBarFinancialActivities.progress = goalSettingPercentage.toInt()

            }
            //no budgeting completed yet, score will be personal finance, saving performance,  financial assessment (prelim)
            else if (nBudgetingCompleted == 0) {
                overallFinancialHealth = ((personalFinancePerformance * .35) + (((goalSettingPercentage + savingPercentage) / 2) * .35) + (financialAssessmentPerformance * .30)).toFloat()
                binding.tvFinancialActivitiesPercentage.text = DecimalFormat("##0.0").format((goalSettingPercentage + savingPercentage) / 2) + "%"
                binding.progressBarFinancialActivities.progress = ((goalSettingPercentage + savingPercentage) / 2).toInt()


            }
            //no spending completed yet, score will be personal finance, saving performance, budgeting performance,  financial assessment (prelim)
            else if (nSpendingCompleted == 0) {
                overallFinancialHealth = ((personalFinancePerformance * .35) + (((goalSettingPercentage + savingPercentage + budgetingPercentage) / 3) * .35) + (financialAssessmentPerformance * .30)).toFloat()
                binding.tvFinancialActivitiesPercentage.text = DecimalFormat("##0.0").format((goalSettingPercentage + savingPercentage + budgetingPercentage) / 3) + "%"
                binding.progressBarFinancialActivities.progress = ((goalSettingPercentage + savingPercentage + budgetingPercentage) / 3).toInt()
            }
            else {
                overallFinancialHealth =
                    ((personalFinancePerformance * .35) + (((goalSettingPercentage + savingPercentage + budgetingPercentage + spendingPercentage) / 4) * .35) + (financialAssessmentPerformance * .30)).toFloat()
                binding.tvFinancialActivitiesPercentage.text = DecimalFormat("##0.0").format((goalSettingPercentage + savingPercentage + budgetingPercentage + spendingPercentage) / 4) + "%"
                binding.progressBarFinancialActivities.progress = ((goalSettingPercentage + savingPercentage + budgetingPercentage +spendingPercentage) / 4).toInt()

            }
        }
        println("print personal finance" +  personalFinancePerformance)
        println("print finact goal setting " + goalSettingPercentage)
        println("print finact saving"  + savingPercentage)
        println("print saving finsihed " + nSavingCompleted)
        println("print finact budgeting " + budgetingPercentage)
        println("print budgeting finished" + nBudgetingCompleted)
        println("print finact spending " + spendingPercentage)
        println("print spending finished" + nSpendingCompleted)
        println("print fin assessments" + financialAssessmentPerformance)
        binding.tvFinancialHealthScore.text = DecimalFormat("##0.00").format(overallFinancialHealth) + "%"
        binding.layoutLoading.visibility = View.GONE
        binding.mainLayout.visibility = View.VISIBLE
    }

}
