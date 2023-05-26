package ph.edu.dlsu.finwise.childDashboardModule.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
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
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.model.*
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentFinancialActivity
import ph.edu.dlsu.finwise.parentFinancialManagementModule.ParentFinancialManagementActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class DashboardFinancialActivitiesFragment : Fragment() {
    private lateinit var binding: FragmentDashboardFinancialActivitiesBinding
    private var firestore = Firebase.firestore
    private var userType = "child"

    var mediaPlayer: MediaPlayer? = MediaPlayer()

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
        binding.title.text = "Financial Activities Score"
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
            Log.d("kfc", "getFinancialActivitiesPerformance: "+goalSettingPercentage)
            Log.d("kfc", "savingPercentage: "+savingPercentage)
            Log.d("kfc", "spendingPercentage: "+spendingPercentage)
            Log.d("kfc", "budgetingPercentage: "+budgetingPercentage)
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
                    calculateFinancialActivitiesScore()
                }
            }
            else {
                spendingPercentage = (1-overspendingPercentage)*100
//                println("print goal  setting " + goalSettingPercentage)
//                println("print savings " +  savingPercentage)
//                println("print budgeting " +  budgetingPercentage)
//                println("print spending " + spendingPercentage)
                financialActivitiesPerformance = ((savingPercentage + budgetingPercentage + spendingPercentage) / 3)
                setPerformanceView()
            }

    }

    private fun calculateFinancialActivitiesScore() {
        checkIfNaN()

        if (age == 9 || age == 12)
            financialActivitiesPerformance = ((savingPercentage + budgetingPercentage + spendingPercentage) / 3)
        else if (age == 10 || age == 11)
            financialActivitiesPerformance = ((goalSettingPercentage + savingPercentage + budgetingPercentage + spendingPercentage) / 4)

        setPerformanceView()
    }

    private fun setPerformanceView() {
        Log.d("hatdog", "getFinancialAssessmentScore: "+financialActivitiesPerformance)

        if (financialActivitiesPerformance.isNaN())
            financialActivitiesPerformance = 0.0F

        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.UP
        val roundedValue = df.format(financialActivitiesPerformance)

        val imageView = binding.ivScore
        val message: String
        val performance: String
        val bitmap: Bitmap

        //TODO: Change audio
        var audio = 0

        if (financialActivitiesPerformance == 100.00F) {
            audio = R.raw.sample
            performance = "Excellent!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.dark_green))
            message = if (userType == "Parent")
                "They have become a financial superstar! Their dedication and skills in saving, spending, budgeting, and goal setting are commendable!"
            else "Keep inspiring others with your financial knowledge and success. You're on the path to greatness!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.excellent)
        } else if (financialActivitiesPerformance > 90) {
            audio = R.raw.sample
            performance = "Amazing!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.amazing_green))
            message = if (userType == "Parent")
                "Your child's financial skills are exceptional. Encourage them further to dive deeper into long-term goals!"
            else "Keep refining your strategies, exploring new ways to save and invest, and setting ambitious goals!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.amazing)
        } else if (financialActivitiesPerformance > 80) {
            audio = R.raw.sample
            performance = "Great!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.green))
            message = if (userType == "Parent")
                "Your child's financial activities showcase their strong understanding of finance. Encourage them to set ambitious goals and plan for the future!"
            else "Keep mastering the art of managing money. Your financial future looks bright!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.great)
        } else if (financialActivitiesPerformance > 70) {
            audio = R.raw.sample
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.light_green))
            performance = "Good!"
            message = if (userType == "Parent")
                "Your child is becoming financially responsible. Help them and encourage smart financial decision-making!"
            else "Continue building your knowledge of saving, goal setting, budgeting, and spending!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.good)
        } else if (financialActivitiesPerformance > 60) {
            audio = R.raw.sample
            performance = "Average"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.yellow))
            message = if (userType == "Parent")
                "Your child is demonstrating solid financial skills. Encourage them to take on more challenging tasks and explore ways to maximize their savings!"
            else "Keep setting challenging goals, tracking your progress, and making smart financial choices!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.average)
        } else if (financialActivitiesPerformance > 50) {
            audio = R.raw.sample
            performance = "Nearly There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.nearly_there_yellow))
            message = if (userType == "Parent")
                "Your child's commitment to financial activities is paying off. They are developing valuable skills in saving, spending, budgeting, and goal setting!"
            else "Keep honing your financial skills. By setting goals and making informed spending decisions, you'll achieve success!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.nearly_there)
        } else if (financialActivitiesPerformance > 40) {
            audio = R.raw.sample
            performance = "Almost There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.almost_there_yellow))
            message = if (userType == "Parent")
                "Your child is gaining a better understanding of financial activities. Encourage them to set achievable goals and make smart spending decisions!"
            else "Keep practicing saving, setting goals, and budgeting. Your dedication will pay off!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.almost_there)
        } else if (financialActivitiesPerformance > 30) {
            audio = R.raw.sample
            performance = "Getting There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.getting_there_orange))
            message = if (userType == "Parent")
                "Your child is taking steps towards financial literacy. Encourage them to practice budgeting and explore different saving strategies!"
            else "Keep exploring and learning new ways to save, set goals, and make smart spending choices. Your financial journey is exciting!."
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.getting_there)
        } else if (financialActivitiesPerformance > 20) {
            audio = R.raw.sample
            performance = "Not Quite There"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.not_quite_there_red))
            message = if (userType == "Parent")
                "Your child is beginning to grasp the importance of financial activities. Encourage them to continue exploring and learning about managing money effectively!"
            else "Practice saving, setting goals, and budgeting wisely. You're building a strong foundation for your financial future!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.not_quite_there_yet)
        } else if (financialActivitiesPerformance > 10) {
            audio = R.raw.sample
            performance = "Need Improvement"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.red))
            message = if (userType == "Parent")
                "Explore the app on  saving, spending, budgeting, and setting financial goals as it can make a positive impact on your child's financial journey!"
            else "Keep learning about saving, goal setting, budgeting, and spending. You're on your way to financial success!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.bad)
        }
        else {
            audio = R.raw.sample
            performance = "Get Started!"
            binding.tvPerformanceStatus.setTextColor(resources.getColor(R.color.yellow))
            /*var date = "month"
            if (selectedDatesSort == "quarterly")
                date = "quarter"*/
            message = if (userType == "Parent")
                "Your child hasn't done a financial activity. Go to the financial activities button to start.!"
            else "You haven't done a financial activity. Go to the financial activities button to start!"
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.nearly_there)
        }

        imageView.setImageBitmap(bitmap)
        binding.tvPerformanceText.text = message
        binding.tvPerformanceStatus.text = performance
        binding.tvPerformancePercentage.text = "${roundedValue}%"
        
        mediaPlayer = MediaPlayer.create(context, audio)
        loadOverallAudio()
        loadButton()
    }

    private fun loadOverallAudio() {
        binding.btnAudioFinancialActivitiesScore.setOnClickListener {
            try {
                mediaPlayer.let { player ->
                    if (player?.isPlaying == true) {
                        player.pause()
                        player.seekTo(0)
                        return@setOnClickListener
                    }

                    player?.start()
                }
            } catch (e: IllegalStateException) {
                // Handle the exception, e.g., log an error or display a message
            }
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
        } catch (e: IllegalStateException) {
            // Handle the exception gracefully
            e.printStackTrace()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        releaseMediaPlayer()
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun initializeParentFinancialActivityBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString("childID", userID)
        return bundle
    }

    private fun loadButton() {
        binding.btnSeeMore.setOnClickListener {
            releaseMediaPlayer()
            val goToActivity = if (userType == "Parent") {
                val bundle = initializeParentFinancialActivityBundle()
                val intent = Intent(context, ParentFinancialManagementActivity::class.java)
                intent.putExtras(bundle)
                intent
            } else {
                Intent(context, PersonalFinancialManagementActivity::class.java)
            }
            startActivity(goToActivity)
        }
    }


    private fun checkIfNaN() {
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
        val financialActivitiesDocuments = firestore.collection("FinancialActivities")
            .whereEqualTo("financialGoalID",
                financialActivitiesDocument.toObject<FinancialActivities>()!!.financialGoalID!!)
            .whereEqualTo("financialActivityName", "Spending").get().await()
            val spendingActivity = financialActivitiesDocuments.documents[0].toObject<FinancialActivities>()
            Log.d("ufc", "spendingActivity: "+spendingActivity?.status)

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
        if (userDocument.toObject<Users>()?.userType == "Parent")
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

        val date = args?.getString("date")
        val childIDBundle = args?.getString("childID")
        val currUser = args?.getString("user")
        if (currUser != null) {
            userType = currUser
        }

        if (childIDBundle != null)
            userID = childIDBundle

        /*if (date != null) {
            selectedDatesSort = date
            transactionsArrayList.clear()
        }*/
    }


}