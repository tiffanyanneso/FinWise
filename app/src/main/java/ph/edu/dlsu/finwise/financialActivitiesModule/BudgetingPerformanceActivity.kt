package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityBudgetingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.DialogBudgetAccuracyAmountReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogBudgetAccuracyItemsReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogBudgetingReviewBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.BudgetingFragment
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import java.text.DecimalFormat
import kotlin.math.roundToInt

class BudgetingPerformanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetingPerformanceBinding
    private var firestore = Firebase.firestore

    //contains only going budgeting activities for the recycler view
    var goalIDArrayList = ArrayList<String>()
    //used to get all budgeting activities to count parent involvement
    private var parentalInvolvementArrayList = ArrayList<String>()
    var budgetingArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<BudgetingFragment.GoalFilter>()
    //arraylist that holds all user IDs for createdBy fields in BudgetItem, for parental involvement
    private var createdByUserIDArrayList = ArrayList<String>()

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    //number of times the item was modified by the parent
    private var nParent = 0
    //number of budget items in total
    private var budgetItemCount = 0

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
        goalIDArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
//            binding.tvInProgress.text = results.size().toString()
            for (activity in results) {
                //add id to arraylit to load in recycler view
                var activityObject = activity.toObject<FinancialActivities>()
                goalIDArrayList.add(activityObject?.financialGoalID.toString())
            }
            getParentalInvolvement()
//            loadRecyclerView(goalIDArrayList)
        }
    }

        private fun getParentalInvolvement() {
        parentalInvolvementArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Budgeting").get().addOnSuccessListener { results ->
            for (activity in results) {
                //for parent involvement
                firestore.collection("BudgetItems").whereEqualTo("financialActivityID", activity.id).get().addOnSuccessListener { budgetItems ->
                    for (budgetItem in budgetItems) {
                        budgetItemCount++
                        var budgetItemObject = budgetItem.toObject<BudgetItem>()

                        firestore.collection("ParentUser").document(budgetItemObject?.createdBy.toString()).get().addOnSuccessListener { user ->
                            //parent is the one who added the budget item
                            if (user.exists())
                                nParent++

                        }.continueWith {
                            binding.textViewProgressParentalInvolvement.text = DecimalFormat("##0.##").format((nParent.toFloat()/budgetItemCount.toFloat())*100)+ "%"
                            binding.progressBarParentalInvolvement.progress = ((nParent.toFloat()/budgetItemCount.toFloat())*100).roundToInt()

                            // TODO add PI if else statements here
                        }
                    }
                }
//                getAverageUpdates(activity.id)
            }
        }
    }

//    private fun getAverageUpdates(budgetingActivityID:String){
//        firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetingActivityID).get().addOnSuccessListener { budgetItems ->
//            for (budgetItem in budgetItems){
//                var budgetItemObject = budgetItem.toObject<BudgetItem>()
//                nItems++
//                if (budgetItemObject.status == "Edited")
//                    nUpdates++
//            }
//            binding.tvAverageUpdates.text = DecimalFormat("##0.##").format((nUpdates/nItems.roundToInt()))
//        }
//    }

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