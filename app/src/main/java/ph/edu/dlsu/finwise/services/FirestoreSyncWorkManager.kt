package ph.edu.dlsu.finwise.services

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.model.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class FirestoreSyncWorkManager (context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private var finactScore = 0.00F
    var goalSettingPercentage = 0.00F
    var savingPercentage = 0.00F
    var budgetingPercentage = 0.00F
    var nParent = 0
    var purchasedBudgetItemCount = 0.00F
    var totalBudgetAccuracy = 0.00F
    //number of budget items, including those from ongoing budgeting activities
    var budgetItemCount = 0.00F
    var spendingPercentage = 0.00F
    //number of budget items of completed budgting activities
    var nBudgetItems = 0.00F
    var overSpending = 0.00F
    var overspendingPercentage = 0.00F
    var nPlanned = 0.00F
    var nTotalPurchased = 0.00F

    private var budgetItemsIDArrayList = ArrayList<String>()

     var nGoalSettingCompleted = 0
     var nSavingCompleted = 0
     var nBudgetingCompleted = 0
     var nSpendingCompleted = 0

    private var age = 0
    override fun doWork(): Result {
        // TODO: Implement your data-saving logic here (e.g., saving data to Firestore)
        CoroutineScope(Dispatchers.Main).launch {
            addDataToFirestore()
        }
        return Result.success()
    }

    private suspend fun addDataToFirestore() {
        CoroutineScope(Dispatchers.Main).launch {
            savePfmScore()
            computeFinactScore()
            saveAssessmentScore()
        }
    }

    private suspend fun savePfmScore() {
        var income = 0.00F
        var expense = 0.00F
        var transactions = firestore.collection("Transactions").whereEqualTo("userID", currentUser).get().await().toObjects<Transactions>()

        for (transaction in transactions) {
            if (transaction.transactionType == "Income")
                income += transaction.amount!!
            else if (transaction.transactionType == "Expense")
                expense += transaction.amount!!
        }

        var pfmScore = (income / expense * 100)
        if (pfmScore > 200)
            pfmScore = 200F
        pfmScore /= 2
        if (pfmScore.isNaN())
            pfmScore = 0F

        var scores = firestore.collection("Scores").whereEqualTo("childID", currentUser).whereEqualTo("type", "pfm")
                .orderBy("dateRecorded", Query.Direction.DESCENDING).get().await()

        if (!scores.isEmpty) {
            var latestScore = scores.documents[0]
            //compute if same day or not
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =
                SimpleDateFormat("MM/dd/yyyy").format(latestScore.toObject<ScoreModel>()!!.dateRecorded!!.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            Log.d("kulog", "savePfmScore: "+pfmScore)

            var difference = Period.between(to, from)

            var differenceDays = difference.days

            if (differenceDays > 0) {
                var score = hashMapOf(
                    "childID" to currentUser,
                    "score" to pfmScore,
                    "type" to "pfm",
                    "dateRecorded" to Timestamp.now()
                )
                firestore.collection("Scores").add(score)
            } else
                firestore.collection("Scores").document(latestScore.id)
                    .update("score", pfmScore, "dateRecorded", Timestamp.now())
        } else {
            var score = hashMapOf(
                "childID" to currentUser,
                "score" to pfmScore,
                "type" to "pfm",
                "dateRecorded" to Timestamp.now()
            )
            firestore.collection("Scores").add(score)
        }
    }

    private suspend fun computeFinactScore() {

        var child = firestore.collection("Users").document(currentUser).get().await().toObject<Users>()
        //compute age
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val from = LocalDate.now()
        val date = SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
        val to = LocalDate.parse(date.toString(), dateFormatter)
        val difference = Period.between(to, from)

        age = difference.years

        CoroutineScope(Dispatchers.Main).launch {
            if (age == 10 || age == 11)
                getGoalSettingPerformance()

            getSavingPerformanceScore()
            getBudgetingPerformanceScore()
            getSpendingPerformance()

            if (age > 9 )
                purchasePlanningPerformance()
            else {
                if (nSpendingCompleted > 0)
                    spendingPercentage = (1 - overspendingPercentage) * 100
            }

            println("print finact goal setting " + goalSettingPercentage)
            println("print finact saving"  + savingPercentage)
            println("print saving finsihed " + nSavingCompleted)
            println("print finact budgeting " + budgetingPercentage)
            println("print budgeting finished" + nBudgetingCompleted)
            println("print finact spending " + spendingPercentage)
            println("print spending finished" + nSpendingCompleted)

            if (age == 9 || age == 12) {
                //no saving completed yet, score will be personal finance and financial assessment (prelim)
                if (nBudgetingCompleted == 0)
                    finactScore =  savingPercentage
                //no spending completed yet, score will be personal finance, saving performance, budgeting performance,  financial assessment (prelim)
                else if (nSpendingCompleted == 0)
                    finactScore =  (savingPercentage + budgetingPercentage) / 2
                else if (nSpendingCompleted > 0)
                    finactScore = (savingPercentage + budgetingPercentage + spendingPercentage) / 3

            } else if (age == 10 || age == 11) {
                //no goal setting completed yet, score will be personal finance, and  financial assessment (prelim)
               if(nSavingCompleted == 0)
                   finactScore = goalSettingPercentage

                //no budgeting completed yet, score will be personal finance, saving performance,  financial assessment (prelim)
                else if (nBudgetingCompleted == 0)
                   finactScore =  (goalSettingPercentage + savingPercentage)/2

                //no spending completed yet, score will be personal finance, saving performance, budgeting performance,  financial assessment (prelim)
                else if (nSpendingCompleted == 0)
                   finactScore =  (goalSettingPercentage + savingPercentage + budgetingPercentage) / 3

                else if (nSpendingCompleted > 0)
                   finactScore = (goalSettingPercentage + savingPercentage + budgetingPercentage + spendingPercentage) / 4
            }

            checkIfNanFinancialActivities()
            saveFinactScore()

        }
    }

    private fun checkIfNanFinancialActivities() {
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

    private suspend fun saveFinactScore() {
        var scores = firestore.collection("Scores").whereEqualTo("childID", currentUser).whereEqualTo("type", "finact")
            .orderBy("dateRecorded", Query.Direction.DESCENDING).get().await()

        if(!scores.isEmpty) {
            var latestScore = scores.documents[0]
            //compute if same day or not
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =
                SimpleDateFormat("MM/dd/yyyy").format(latestScore.toObject<ScoreModel>()!!.dateRecorded!!.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var differenceDays = difference.days

            if (differenceDays > 0) {
                var score = hashMapOf(
                    "childID" to currentUser,
                    "score" to finactScore,
                    "type" to "finact",
                    "dateRecorded" to Timestamp.now()
                )
                firestore.collection("Scores").add(score)
                saveSeparateFinActScores()
            } else {
                firestore.collection("Scores").document(latestScore.id)
                    .update("score", finactScore, "dateRecorded", Timestamp.now())
                CoroutineScope(Dispatchers.Main).launch {
                    updateFinActScore()
                }
            }
        } else {
            var score = hashMapOf(
                "childID" to currentUser,
                "score" to finactScore,
                "type" to "finact",
                "dateRecorded" to Timestamp.now()
            )
            firestore.collection("Scores").add(score)
            saveSeparateFinActScores()
        }
    }

    private suspend fun updateFinActScore() {
        if (age == 10 || age == 11) {
            var goalSettingScoreID = firestore.collection("Scores").whereEqualTo("childID", currentUser)
                .whereEqualTo("type", "goal setting")
                .orderBy("dateRecorded", Query.Direction.DESCENDING).get().await()
            if (!goalSettingScoreID.isEmpty) {
                var latestScore = goalSettingScoreID.documents[0]
                //compute if same day or not
                val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                val from = LocalDate.now()
                val date =
                    SimpleDateFormat("MM/dd/yyyy").format(latestScore.toObject<ScoreModel>()!!.dateRecorded!!.toDate())
                val to = LocalDate.parse(date.toString(), dateFormatter)
                var difference = Period.between(to, from)

                var differenceDays = difference.days

                if (differenceDays > 0) {
                    var goalSetting = hashMapOf(
                        "childID" to currentUser,
                        "score" to goalSettingPercentage,
                        "type" to "goal setting",
                        "dateRecorded" to Timestamp.now()
                    )
                    firestore.collection("Scores").add(goalSetting)
                } else
                    firestore.collection("Scores").document(goalSettingScoreID.documents[0].id)
                        .update("score", goalSettingPercentage, "dateRecorded", Timestamp.now())
            } else {
                var goalSetting = hashMapOf(
                    "childID" to currentUser,
                    "score" to goalSettingPercentage,
                    "type" to "goal setting",
                    "dateRecorded" to Timestamp.now()
                )
                firestore.collection("Scores").add(goalSetting)
            }
        }

        var savingScore = firestore.collection("Scores").whereEqualTo("childID", currentUser)
            .whereEqualTo("type", "saving")
            .orderBy("dateRecorded", Query.Direction.DESCENDING).get().await()
        if (!savingScore.isEmpty) {
            var latestScore = savingScore.documents[0]
            //compute if same day or not
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =
                SimpleDateFormat("MM/dd/yyyy").format(latestScore.toObject<ScoreModel>()!!.dateRecorded!!.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var differenceDays = difference.days

            if (differenceDays > 0) {
                var saving = hashMapOf(
                    "childID" to currentUser,
                    "score" to savingPercentage,
                    "type" to "saving",
                    "dateRecorded" to Timestamp.now()
                )
                firestore.collection("Scores").add(saving)
            } else
                firestore.collection("Scores").document(savingScore.documents[0].id).update("score", savingPercentage, "dateRecorded", Timestamp.now())

        } else {
            var saving = hashMapOf(
                "childID" to currentUser,
                "score" to savingPercentage,
                "type" to "saving",
                "dateRecorded" to Timestamp.now()
            )
            firestore.collection("Scores").add(saving)
        }

        var budgetingScore = firestore.collection("Scores").whereEqualTo("childID", currentUser)
            .whereEqualTo("type", "budgeting")
            .orderBy("dateRecorded", Query.Direction.DESCENDING).get().await()
        if (!budgetingScore.isEmpty) {
            var latestScore = budgetingScore.documents[0]
            //compute if same day or not
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =
                SimpleDateFormat("MM/dd/yyyy").format(latestScore.toObject<ScoreModel>()!!.dateRecorded!!.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var differenceDays = difference.days

            if (differenceDays > 0) {
                var budgeting = hashMapOf(
                    "childID" to currentUser,
                    "score" to budgetingPercentage,
                    "type" to "budgeting",
                    "dateRecorded" to Timestamp.now()
                )
                firestore.collection("Scores").add(budgeting)
            } else
                firestore.collection("Scores").document(budgetingScore.documents[0].id).update("score", budgetingPercentage, "dateRecorded", Timestamp.now())

        } else {
            var budgeting = hashMapOf(
                "childID" to currentUser,
                "score" to budgetingPercentage,
                "type" to "budgeting",
                "dateRecorded" to Timestamp.now()
            )
            firestore.collection("Scores").add(budgeting)
        }

        var spendingScore = firestore.collection("Scores").whereEqualTo("childID", currentUser)
            .whereEqualTo("type", "spending")
            .orderBy("dateRecorded", Query.Direction.DESCENDING).get().await()
        if (!spendingScore.isEmpty) {
            var latestScore = spendingScore.documents[0]
            //compute if same day or not
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =
                SimpleDateFormat("MM/dd/yyyy").format(latestScore.toObject<ScoreModel>()!!.dateRecorded!!.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var differenceDays = difference.days

            if (differenceDays > 0) {
                var spending = hashMapOf(
                    "childID" to currentUser,
                    "score" to spendingPercentage,
                    "type" to "spending",
                    "dateRecorded" to Timestamp.now()
                )
                firestore.collection("Scores").add(spending)
            } else
            firestore.collection("Scores").document(spendingScore.documents[0].id)
                .update("score", spendingPercentage, "dateRecorded", Timestamp.now())
        } else {
            var spending = hashMapOf(
                "childID" to currentUser,
                "score" to spendingPercentage,
                "type" to "spending",
                "dateRecorded" to Timestamp.now()
            )
            firestore.collection("Scores").add(spending)
        }
    }

    private fun saveSeparateFinActScores() {
        if (age == 10 || age == 11) {
            var goalSetting = hashMapOf(
                "childID" to currentUser,
                "score" to goalSettingPercentage,
                "type" to "goal setting",
                "dateRecorded" to Timestamp.now()
            )
            firestore.collection("Scores").add(goalSetting)
        }

        var saving = hashMapOf(
            "childID" to currentUser,
            "score" to savingPercentage,
            "type" to "saving",
            "dateRecorded" to Timestamp.now()
        )
        firestore.collection("Scores").add(saving)

        var budgeting = hashMapOf(
            "childID" to currentUser,
            "score" to budgetingPercentage,
            "type" to "budgeting",
            "dateRecorded" to Timestamp.now()
        )
        firestore.collection("Scores").add(budgeting)

        var spending = hashMapOf(
            "childID" to currentUser,
            "score" to spendingPercentage,
            "type" to "spending",
            "dateRecorded" to Timestamp.now()
        )
        firestore.collection("Scores").add(spending)
    }

    private suspend fun getGoalSettingPerformance() {
        var overallRating = 0.00F
        val nRatings: Int
        val goalRatingDocuments = firestore.collection("GoalRating")
            .whereEqualTo("childID", currentUser).get().await()
        nGoalSettingCompleted = goalRatingDocuments.size()
        nRatings = goalRatingDocuments.size()
        for (rating in goalRatingDocuments) {
            val ratingObject = rating.toObject<GoalRating>()
            overallRating += ratingObject.overallRating!!
        }
        goalSettingPercentage = 0.00F

        if (nRatings != 0)
            goalSettingPercentage = ((overallRating / nRatings)/5)* 100

    }

    private suspend fun getSavingPerformanceScore() {
        var nGoals = 0.00F
        var nOnTime = 0.00F
        val financialGoalsDocuments = firestore.collection("FinancialGoals")
            .whereEqualTo("childID", currentUser)
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
    }

    private suspend fun getBudgetingPerformanceScore() {
        val financialActivitiesDocuments = firestore.collection("FinancialActivities")
            .whereEqualTo("childID", currentUser)
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
            val userDocument = firestore.collection("Users").document(budgetItemObject.createdBy.toString()).get().await()
            //parent is the one who added the budget item
            if (userDocument.toObject<Users>()?.userType == "Parent")
                nParent++

            if (activity != null)
                getBudgetAccuracy(activity.id, budgetItem.id, budgetItemObject)

        }
    }

    private suspend fun getBudgetAccuracy(budgetingActivityID:String, budgetItemID:String,
                                          budgetItemObject:BudgetItem) {
        val budgetingActivity = firestore.collection("FinancialActivities").document(budgetingActivityID).get().await()

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

            if (budgetItemObject.amount!! != 0.00F)
                totalBudgetAccuracy += (100 - (kotlin.math.abs(budgetItemObject.amount!! - spent) / budgetItemObject.amount!!) * 100)

            if (purchasedBudgetItemCount != 0.00F)
               budgetingPercentage = ((totalBudgetAccuracy/purchasedBudgetItemCount) + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2
            else
               budgetingPercentage =((1 - (nParent.toFloat()/budgetItemCount)) * 100)

        } else {
            if (purchasedBudgetItemCount != 0.00F)
                budgetingPercentage = ((totalBudgetAccuracy/purchasedBudgetItemCount) + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2
            else
                budgetingPercentage = ((1 - (nParent.toFloat()/budgetItemCount)) * 100)
        }
        //println("print budgeting   budgeting percentage in budget function" + budgetingPercentage )
    }

    private suspend fun getSpendingPerformance() {
        //get budgeting items to see if they overspent for a specific budget item
        //get completed spending activities
        val financialActivitiesDocuments = firestore.collection("FinancialActivities")
            .whereEqualTo("childID", currentUser)
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

                for (budgetItem in budgetItemsDocuments)
                    budgetItemsIDArrayList.add(budgetItem.id)
            }

            checkOverSpending()
        }
    }

    private suspend fun checkOverSpending(){
        for (budgetItemID in budgetItemsIDArrayList) {
            var spendingTransactions = firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID).whereEqualTo("transactionType", "Expense").get().await().toObjects<Transactions>()
            var amountSpent = 0.00F
            for (expense in spendingTransactions) {
                amountSpent+= expense.amount!!
            }
            //they spent more than their allocated budget
            var budgetItemAmount = firestore.collection("BudgetItems").document(budgetItemID).get().await().toObject<BudgetItem>()?.amount!!
            if (amountSpent > budgetItemAmount)
                overSpending++
        }

        overspendingPercentage = (overSpending/nBudgetItems)
    }


    private suspend fun purchasePlanningPerformance() {
        //items planned / all the items they bought * 100
        //items planned / all the items they bought * 100
        var nPlanned = 0.00F
        var nTotalPurchased = 0.00F
        var spendingActivities = firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Spending").whereEqualTo("status", "Completed").get().await()

        for (spendingActivityID in spendingActivities) {
            var shoppingListItems = firestore.collection("ShoppingListItems").whereEqualTo("spendingActivityID", spendingActivityID.id).get().await().toObjects<ShoppingListItem>()
            for (shoppingListItem in shoppingListItems) {
                if (shoppingListItem.status == "Purchased")
                    nPlanned++
            }

            nTotalPurchased += firestore.collection("Transactions").whereEqualTo("financialActivityID", spendingActivityID.id).whereEqualTo("transactionType", "Expense").get().await().size().toFloat()
        }

        val purchasePlanningPercentage = (nPlanned/nTotalPurchased)*100
        spendingPercentage = (((1-overspendingPercentage)*100) + purchasePlanningPercentage) /2
    }

    private suspend fun saveAssessmentScore() {
         var financialGoalsPercentage = 0.00F
         var financialGoalsScores = ArrayList<Double?>()
         var savingPercentage = 0.00F
         var savingScores = ArrayList<Double?>()
         var budgetingPercentage = 0.00F
         var budgetingScores = ArrayList<Double?>()
         var spendingPercentage = 0.00F
         var spendingScores = ArrayList<Double?>()

        var assessmentAttempts = firestore.collection("AssessmentAttempts").whereEqualTo("childID", currentUser)
            .get().await().toObjects<FinancialAssessmentAttempts>()

        for (attempt in assessmentAttempts) {
            val assessmentAttemptObject = firestore.collection("Assessments").document(attempt.assessmentID!!).get().await().toObject<FinancialAssessmentDetails>()
            val percentage = getPercentage(attempt)
            when (assessmentAttemptObject?.assessmentCategory) {
                "Goal Setting" -> financialGoalsScores.add(percentage)
                "Saving" -> savingScores.add(percentage)
                "Budgeting" -> budgetingScores.add(percentage)
                "Spending" -> spendingScores.add(percentage)
            }
        }

        val maxScore = 100
        val savingPercentageSum = savingScores.sumOf { it ?: 0.0 }
        savingPercentage = ((savingPercentageSum / (maxScore * savingScores.size)) * 100).toFloat()
        val spendingPercentageSum = spendingScores.sumOf { it ?: 0.0 }
        spendingPercentage = ((spendingPercentageSum / (maxScore * spendingScores.size)) * 100).toFloat()
        val budgetingPercentageSum = budgetingScores.sumOf { it ?: 0.0 }
        budgetingPercentage = ((budgetingPercentageSum / (maxScore * budgetingScores.size)) * 100).toFloat()
        val financialGoalsPercentageSum = financialGoalsScores.sumOf { it ?: 0.0 }
        financialGoalsPercentage = ((financialGoalsPercentageSum / (maxScore * financialGoalsScores.size)) * 100).toFloat()

        val totalSum = spendingPercentage + savingPercentage + financialGoalsPercentage + budgetingPercentage
        val maxPossibleSum = 4 * 100  // assuming the maximum possible value for each variable is 100

        var financialAssessmentScore = (totalSum.toDouble() / maxPossibleSum) * 100
        if (financialAssessmentScore.isNaN())
            financialAssessmentScore = 0.0

        var scores = firestore.collection("Scores").whereEqualTo("childID", currentUser).whereEqualTo("type", "assessments")
            .orderBy("dateRecorded", Query.Direction.DESCENDING).get().await()

        if (!scores.isEmpty) {
            var latestScore = scores.documents[0]
            //compute if same day or not
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =
                SimpleDateFormat("MM/dd/yyyy").format(latestScore.toObject<ScoreModel>()!!.dateRecorded!!.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var differenceDays = difference.days

            if (differenceDays > 0) {
                var score = hashMapOf(
                    "childID" to currentUser,
                    "score" to financialAssessmentScore,
                    "type" to "assessments",
                    "dateRecorded" to Timestamp.now()
                )
                firestore.collection("Scores").add(score)
            } else
                firestore.collection("Scores").document(latestScore.id)
                    .update("score", financialAssessmentScore, "dateRecorded", Timestamp.now())

        } else {
            var score = hashMapOf(
                "childID" to currentUser,
                "score" to financialAssessmentScore,
                "type" to "assessments",
                "dateRecorded" to Timestamp.now()
            )
            firestore.collection("Scores").add(score)
        }
    }

    private fun getPercentage(assessment: FinancialAssessmentAttempts): Double {
        val correctAnswers: Int? = assessment.nAnsweredCorrectly
        val totalAnswers: Int? = assessment.nQuestions

        val percentage = if(totalAnswers != null && correctAnswers != null && totalAnswers != 0) {
            (correctAnswers.toDouble() / totalAnswers.toDouble()) * 100
        } else {
            0.0
        }

        return percentage.coerceAtMost(100.0)
    }
}