package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityGoalAccomplishedBinding
import ph.edu.dlsu.finwise.databinding.DialogBadgeBinding
import ph.edu.dlsu.finwise.databinding.DialogProceedNextActivityBinding
import ph.edu.dlsu.finwise.databinding.DialogTakeAssessmentBinding
import ph.edu.dlsu.finwise.financialAssessmentModule.FinancialAssessmentActivity
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

class GoalAccomplishedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalAccomplishedBinding
    private var firestore = Firebase.firestore

    private lateinit var  financialGoalID:String
    private lateinit var savingActivityID:String
    private lateinit var budgetingActivityID:String
    private lateinit var spendingActivityID:String
    private lateinit var sendBundle: Bundle
    private var nFinishedActivities = 0


    private lateinit var goalName:String

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalAccomplishedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        getBundles()
        sendBundles()
        badge()
        setText()
        initializeFinishButton()
    }

    private fun badge() {
        CoroutineScope(Dispatchers.Main).launch {
            val badgesQuerySnapshot = firestore.collection("Badges").whereEqualTo("childID", currentUser)
                .whereEqualTo("badgeType", "Financial Activity Badge").get().await()
            nFinishedActivities = badgesQuerySnapshot.size()
            if (badgesQuerySnapshot?.isEmpty!!) {
                if (nFinishedActivities == 1)
                addBadge()
                showBadgeDialog()
            } else {
                checkIfUpdateBadge(badgesQuerySnapshot)
            }
        }
    }

    private suspend fun addBadge() {
        val badgeName = getBadgeName()
        val badge = hashMapOf(
            "badgeName" to badgeName,
            "badgeType" to "Financial Activity Badge",
            "badgeDescription" to "Finished $nFinishedActivities Activities",
            "badgeScore" to nFinishedActivities,
            "childID"   to currentUser,
            "dateEarned" to SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate())
        )
        firestore.collection("Badges").add(badge).await()
    }

    private fun getBadgeName(): String {
        lateinit var badgeLevel: String
        when (nFinishedActivities) {
            1 -> badgeLevel = "\"Starter\""
            10 -> badgeLevel = "\"Apprentice\""
            20 -> badgeLevel = "\"Guru\""
            50 -> badgeLevel = "\"Genius\""
            100 -> badgeLevel = "\"Prodigy\""
        }
        return "$badgeLevel Financial Achiever"
    }

    private suspend fun checkIfUpdateBadge(querySnapshot: QuerySnapshot?) {
        val badge = querySnapshot?.documents?.get(0)?.toObject<UserBadges>()
        if (nFinishedActivities > badge?.badgeScore!!) {
            if (isUpdateBadge()) {
                updateBadge(querySnapshot)
                showBadgeDialog()
            }
        }
    }

    private suspend fun updateBadge(querySnapshot: QuerySnapshot) {
        val badgeName = getBadgeName()
        val updatedBadge = hashMapOf(
            "badgeName" to badgeName,
            "badgeScore" to nFinishedActivities,
            "badgeDescription" to "Finished $nFinishedActivities Activities",
            "dateEarned" to SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate())
        )
        val badgeID = querySnapshot.documents[0].id
        firestore.collection("Badges").document(badgeID).update(updatedBadge as Map<String, Any>)
            .await()
    }

    private fun isUpdateBadge(): Boolean {
        var updateBadge = false
        when (nFinishedActivities) {
            10 -> updateBadge = true
            20 -> updateBadge = true
            50 -> updateBadge = true
            100 -> updateBadge = true
        }
        return updateBadge
    }

    private fun showBadgeDialog() {
        val dialogBinding= DialogBadgeBinding.inflate(layoutInflater)
        val dialog= Dialog(this);
        dialog.setContentView(dialogBinding.root)
        // Initialize dialog

        dialog.window!!.setLayout(1050, 1300)

        setViewBindings(dialogBinding)
        setBtn(dialogBinding, dialog)

        dialog.show()
    }

    private fun setViewBindings(dialogBinding: DialogBadgeBinding) {
        val imageView = dialogBinding.ivBadge
        var badgeImage = R.drawable.excellent
        lateinit var badgeMessage: String

        when (nFinishedActivities) {
            1 -> {
                badgeImage = R.drawable.badge_achiever_1
                badgeMessage = "Congratulations on finishing your financial goals! You're on your way to becoming financially savvy."
            }
            10 -> {
                badgeImage = R.drawable.badge_achiever_10
                badgeMessage = "Great job on finishing your financial goals! You're making progress towards financial success."
            }
            20 -> {
                badgeImage = R.drawable.badge_achiever_20
                badgeMessage = "Congratulations on reaching this level! Your financial knowledge is growing, and you're making smart choices with your money."
            }
            50 -> {
                badgeImage = R.drawable.badge_achiever_50
                badgeMessage = "Congratulations! You've reached the Advanced level of financial goal achievement. You're doing great and your hard work is paying off. Keep it up and see how much more you can accomplish!."
            }
            100 -> {
                badgeImage = R.drawable.badge_achiever_100
                badgeMessage = "Wow, you're a financial superstar! Your dedication to financial literacy is impressive, and you're setting yourself up for a bright financial future."
            }
        }
        val bitmap = BitmapFactory.decodeResource(resources, badgeImage)
        imageView.setImageBitmap(bitmap)

        val badgeName = getBadgeName()
        val badgeTitle = "$badgeName Badge Unlocked!"
        dialogBinding.tvBadgeTitle.text = badgeTitle
        dialogBinding.tvMessage.text = badgeMessage
    }

    private fun setBtn(dialogBinding: DialogBadgeBinding, dialog: Dialog) {
        dialogBinding.btnOk.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun initializeFinishButton() {
        binding.btnFinish.setOnClickListener {
            val dialogBinding = DialogProceedNextActivityBinding.inflate(layoutInflater)
            val dialog = Dialog(this);
            dialog.setContentView(dialogBinding.root)
            dialog.window!!.setLayout(900, 800)
            dialog.show()

            initializeProceedButton(dialogBinding)
            initializeNoButton(dialogBinding)
        }
    }

    private fun initializeNoButton(dialogBinding: DialogProceedNextActivityBinding) {
        dialogBinding.btnNo.setOnClickListener {
            //withdrawal transaction from goal to wallet
            adjustWalletBalances()
        }
    }

    private fun initializeProceedButton(dialogBinding: DialogProceedNextActivityBinding) {
        dialogBinding.btnProceed.setOnClickListener {
            firestore.collection("FinancialActivities").document(budgetingActivityID).update("status", "In Progress")
            val budgetActivity = Intent(this, BudgetActivity::class.java)
            budgetActivity.putExtras(sendBundle)
            startActivity(budgetActivity)
        }

    }

    private fun sendBundles() {
        val sendBundle = Bundle()
        sendBundle.putString("financialGoalID", financialGoalID)
        sendBundle.putString("savingActivityID", savingActivityID)
        sendBundle.putString("budgetingActivityID", budgetingActivityID)
        sendBundle.putString("spendingActivityID", spendingActivityID)
    }

    private fun getBundles() {
        val bundle: Bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()
        budgetingActivityID = bundle.getString("budgetingActivityID").toString()
        spendingActivityID = bundle.getString("spendingActivityID").toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun goToFinancialAssessmentActivity() {
        firestore.collection("Assessments").whereEqualTo("assessmentType", "Pre-Activity").whereEqualTo("assessmentCategory", "Spending").get().addOnSuccessListener {
            if (it.size()!= 0) {
                val assessmentID = it.documents[0].id
                firestore.collection("AssessmentAttempts").whereEqualTo("assessmentID", assessmentID).whereEqualTo("childID", currentUser).get().addOnSuccessListener { results ->
                    if (results.size() != 0) {
                        val assessmentAttemptsObjects = results.toObjects<FinancialAssessmentAttempts>()
                        assessmentAttemptsObjects.sortedByDescending { it.dateTaken }
                        val latestAssessmentAttempt = assessmentAttemptsObjects[0].dateTaken
                        val dateFormatter: DateTimeFormatter =
                            DateTimeFormatter.ofPattern("MM/dd/yyyy")
                        val lastTakenFormat =
                            SimpleDateFormat("MM/dd/yyyy").format(latestAssessmentAttempt!!.toDate())
                        val from = LocalDate.parse(lastTakenFormat.toString(), dateFormatter)
                        val today = SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate())
                        val to = LocalDate.parse(today.toString(), dateFormatter)
                        val difference = Period.between(from, to)

                        if (difference.days >= 7)
                            buildAssessmentDialog()
                    } else
                        buildAssessmentDialog()
                }
            }
        }
    }

    private fun buildAssessmentDialog() {
        val dialogBinding= DialogTakeAssessmentBinding.inflate(layoutInflater)
        val dialog= Dialog(this);
        dialog.setContentView(dialogBinding.root)
        dialog.window!!.setLayout(950, 900)
        dialog.setCancelable(false)
        dialogBinding.btnOk.setOnClickListener { goToAssessment() }
        dialog.show()
    }

    private fun goToAssessment() {
        val bundle = Bundle()
        val assessmentType = "Post-Activity" // Change: Pre-Activity, Post-Activity
        val assessmentCategory = "Saving" // Change: Budgeting, Saving, Spending, Goal Setting
        bundle.putString("assessmentType", assessmentType)
        bundle.putString("assessmentCategory", assessmentCategory)

        val assessmentQuiz = Intent(this, FinancialAssessmentActivity::class.java)
        assessmentQuiz.putExtras(bundle)
        startActivity(assessmentQuiz)
    }

    private fun setText () {
        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            val financialGoal = it.toObject<FinancialGoals>()
            goalName = financialGoal?.goalName.toString()
            binding.tvGoal.text = goalName
            binding.tvActivity.text = financialGoal?.financialActivity
            binding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(financialGoal?.targetAmount)
            // convert timestamp to date string
            binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(financialGoal?.targetDate?.toDate())
        }
    }

    private fun adjustWalletBalances() {
        var cashBalance = 0.00F
        var mayaBalance = 0.00F
        firestore.collection("Transactions").whereEqualTo("financialActivityID", savingActivityID).whereIn("transactionType", Arrays.asList("Deposit", "Withdrawal")).get().addOnSuccessListener { results ->
            for (transaction in results) {
                val transactionObject = transaction.toObject<Transactions>()
                if (transactionObject.paymentType == "Cash") {
                    if (transactionObject.transactionType == "Deposit")
                        cashBalance += transactionObject.amount!!
                    else if (transactionObject.transactionType == "Withdrawal")
                        cashBalance -= transactionObject.amount!!
                }

                else if (transactionObject.paymentType == "Maya") {
                    if (transactionObject.transactionType == "Deposit")
                        mayaBalance += transactionObject.amount!!
                    else if (transactionObject.transactionType == "Withdrawal")
                        mayaBalance -= transactionObject.amount!!
                }
            }
            adjustCashBalance(cashBalance)
            adjustMayaBalance(mayaBalance)
            goToFinancialActivities()
        }
    }

    private fun goToFinancialActivities() {
        val finact = Intent(this, FinancialActivities::class.java)
        startActivity(finact)
    }

    private fun adjustCashBalance(cashBalance:Float) {
        //withdraw money from savings to wallet
        val withdrawal = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Withdrawal",
            "transactionName" to "$goalName Withdrawal",
            "amount" to cashBalance,
            "category" to "Goal",
            "financialActivityID" to savingActivityID,
            "date" to Timestamp.now(),
            "paymentType" to "Cash"
        )
        firestore.collection("Transactions").add(withdrawal)

        firestore.collection("ChildWallet").whereEqualTo("childID", currentUser).whereEqualTo("type", "Cash").get().addOnSuccessListener { result ->
            val walletID = result.documents[0].id
            firestore.collection("ChildWallet").document(walletID).update("currentBalance", FieldValue.increment(cashBalance.toDouble()))
        }
    }

    private fun adjustMayaBalance(mayaBalance:Float) {
        //withdraw money from savings to wallet
        val withdrawal = hashMapOf(
            "userID" to currentUser,
            "transactionType" to "Withdrawal",
            "transactionName" to "$goalName Withdrawal",
            "amount" to mayaBalance,
            "category" to "Goal",
            "financialActivityID" to savingActivityID,
            "date" to Timestamp.now(),
            "paymentType" to "Maya"
        )
        firestore.collection("Transactions").add(withdrawal)

        firestore.collection("ChildWallet").whereEqualTo("childID", currentUser).whereEqualTo("type", "Maya").get().addOnSuccessListener { result ->
            val walletID = result.documents[0].id
            firestore.collection("ChildWallet").document(walletID).update("currentBalance", FieldValue.increment(mayaBalance.toDouble()))
        }
    }
}

