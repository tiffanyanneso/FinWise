package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.edu.dlsu.finwise.databinding.ActivityBudgetingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.DialogBudgetAccuracyAmountReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogBudgetAccuracyItemsReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogBudgetingReviewBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.BudgetingFragment
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import kotlin.math.roundToInt

class BudgetingPerformanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetingPerformanceBinding
    private var firestore = Firebase.firestore

    //arraylist that holds all user IDs for createdBy fields in BudgetItem, for parental involvement
    private var createdByUserIDArrayList = ArrayList<String>()

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    //number of times the item was modified by the parent
    private var nParent = 0
    //number of budget items in total
    private var budgetItemCount = 0

    private var budgetingActivityIDArrayList = ArrayList<String>()
    private var budgetItemIDArrayList = ArrayList<String>()

    private var parentalInvolvementPercentage = 0.00F
    private var budgetAmountAccuracyPercentage = 0.00F
    private var budgetItemAccuracyPercentage = 0.00F

    var nUpdates = 0.00F
    var nItems = 0.00F

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBudgeting()

        binding.btnReview.setOnClickListener {
            showBudgetingReivewDialog()
        }

        binding.btnBudgetAccuracyReview.setOnClickListener{
            showBudgetAccuracyAmountReivewDialog()
        }

        binding.btnBudgetItemsAccuracyReview.setOnClickListener{
            showBudgetAccuracyItemsReivewDialog()
        }

        binding.btnParentalInvolvementReview.setOnClickListener{
            showBudgetParentalInvolvementReivewDialog()
        }
    }

    private fun getBudgeting() {
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
//            binding.tvInProgress.text = results.size().toString()
            for (activity in results)
                budgetingActivityIDArrayList.add(activity.id)
        }.continueWith {
            getParentalInvolvement()
            getBudgetAmountAccuracy()
            getBudgetItemsAccuracy()
        }
    }

    private fun getParentalInvolvement() {
        nParent = 0
        budgetItemCount = 0
        for (budgetID in budgetingActivityIDArrayList) {
            firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetID).get().addOnSuccessListener { results ->
                for (budgetItemID in results) {
                    firestore.collection("BudgetItems").document(budgetItemID.id).get().addOnSuccessListener {
                        var budgetItemObject = it.toObject<BudgetItem>()
                        budgetItemCount++

                        firestore.collection("ParentUser").document(budgetItemObject?.createdBy.toString()).get().addOnSuccessListener { user ->
                            //parent is the one who added the budget item
                            if (user.exists())
                                nParent++

                        }.continueWith {
                            parentalInvolvementPercentage = nParent.toFloat()/budgetItemCount.toFloat()*100
                            binding.textViewProgressParentalInvolvement.text = DecimalFormat("##0.##").format((nParent.toFloat()/budgetItemCount.toFloat())*100)+ "%"
                            binding.progressBarParentalInvolvement.progress = ((nParent.toFloat()/budgetItemCount.toFloat())*100).roundToInt()

                            // TODO add PI if else statements here
                        }
                    }
                }
            }
        }
    }


    private fun getBudgetAmountAccuracy() {

        var budgetAmountScore = 0.00F
        for (budgetID in budgetingActivityIDArrayList) {
            firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetID).get().addOnSuccessListener { results ->
                for (budgetItemID in results) {
                    var nBudgetItems = results.size()
                    firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID.id).get().addOnSuccessListener { transactionsResult ->
                        var spentForBudgetItem = 0.00F
                        for (transaction in transactionsResult)
                            spentForBudgetItem += transaction.toObject<Transactions>().amount!!

                        firestore.collection("BudgetItems").document(budgetItemID.id).get().addOnSuccessListener {
                            budgetAmountScore = spentForBudgetItem / it.toObject<BudgetItem>()?.amount!!
                        }.continueWith {
                            budgetAmountAccuracyPercentage = (budgetAmountScore/nBudgetItems.toFloat())*100
                            binding.textViewBudgetAccuracyProgress.text = DecimalFormat("##0.00").format(budgetAmountAccuracyPercentage) + "%"
                        }
                    }
                }
            }
        }
    }

    private fun getBudgetItemsAccuracy() {
        var budgetItemsScore = 0.00F
        var originalCount = 0.00F
        var endCount = 0.00F
        println("print budget amount accuracy activity id list size " +  budgetingActivityIDArrayList.size)
        for (budgetActivityID in budgetingActivityIDArrayList) {
            firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetActivityID).whereEqualTo("whenAdded", "Before").get().addOnSuccessListener { beforeResults ->
                originalCount = beforeResults.size().toFloat()
            }.continueWith {
                var query1 = firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetActivityID).whereEqualTo("whenAdded", "Before").whereEqualTo("status", "Active").get()
                var query2 = firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetActivityID).whereEqualTo("whenAdded", "After").whereEqualTo("status", "Active").get()
                val combinedResult = Tasks.whenAllSuccess<QuerySnapshot>(query1, query2).result
                endCount = combinedResult.sumOf { it.size() }.toFloat()
                budgetItemsScore += originalCount/endCount
            }

        }

        budgetItemsScore /= budgetItemIDArrayList.size.toFloat()
        binding.textViewBudgetItemsAccuracyProgress.text = DecimalFormat("##0.00").format(budgetItemsScore) + "%"
    }

    private fun showBudgetingReivewDialog() {

        var dialogBinding= DialogBudgetingReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showBudgetAccuracyItemsReivewDialog() {

        var dialogBinding= DialogBudgetAccuracyItemsReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showBudgetAccuracyAmountReivewDialog() {

        var dialogBinding= DialogBudgetAccuracyAmountReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun showBudgetParentalInvolvementReivewDialog() {

        var dialogBinding= DialogBudgetAccuracyItemsReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}