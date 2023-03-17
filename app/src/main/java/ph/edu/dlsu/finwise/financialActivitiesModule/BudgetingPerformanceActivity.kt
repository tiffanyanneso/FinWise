package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityBudgetingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.DialogBudgetAccuracyAmountReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogBudgetAccuracyItemsReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogBudgetingReviewBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.BudgetingFragment
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users
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

            binding.btnReview.setOnClickListener{
                showBudgetingReivewDialog()
            }

//        binding.btnBudgetAccuracyReview.setOnClickListener{
//            showBudgetAccuracyAmountReivewDialog()
//        }
//
//        binding.btnBudgetItemsAccuracyReview.setOnClickListener{
//            showBudgetAccuracyItemsReivewDialog()
//        }
    }

    private fun getBudgeting() {
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
//            binding.tvInProgress.text = results.size().toString()
            for (activity in results)
                budgetingActivityIDArrayList.add(activity.id)
        }.continueWith {
            getParentalInvolvement()
//            getBudgetAmountAccuracy()
//            getBudgetItemsAccuracy()
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

                        firestore.collection("Users").document(budgetItemObject?.createdBy.toString()).get().addOnSuccessListener { user ->
                            //parent is the one who added the budget item
                            if (it.toObject<Users>()!!.userType == "Parent")
                                nParent++

                        }.continueWith {
                            parentalInvolvementPercentage = nParent.toFloat()/budgetItemCount.toFloat()*100
                            binding.textViewProgressParentalInvolvement.text = DecimalFormat("##0.##").format((nParent.toFloat()/budgetItemCount.toFloat())*100)+ "%"
                            binding.progressBarParentalInvolvement.progress = ((nParent.toFloat()/budgetItemCount.toFloat())*100).roundToInt()

                            var parentalPercentage = nParent.toFloat()/budgetItemCount.toFloat()*100

                            if (parentalPercentage < 5) {
                                binding.imgFace.setImageResource(R.drawable.excellent)
                                binding.textStatus.text = "Excellent"
                                binding.textStatus.setTextColor(getResources().getColor(R.color.dark_green))
                                binding.tvPerformanceText.text = "Keep up the excellent work! You are able to budget independently."
                            } else if (parentalPercentage < 15 && parentalPercentage >= 5) {
                                binding.imgFace.setImageResource(R.drawable.amazing)
                                binding.textStatus.text = "Amazing"
                                binding.textStatus.setTextColor(getResources().getColor(R.color.green))
                                binding.tvPerformanceText.text = "Amazing job! Keep up making those budget independently."
                            } else if (parentalPercentage < 25 && parentalPercentage >= 15) {
                                binding.imgFace.setImageResource(R.drawable.great)
                                binding.textStatus.text = "Great"
                                binding.textStatus.setTextColor(getResources().getColor(R.color.green))
                                binding.tvPerformanceText.text = "You are performing well! Keep up making those budget independently."
                            } else if (parentalPercentage < 35 && parentalPercentage >= 25) {
                                binding.imgFace.setImageResource(R.drawable.good)
                                binding.textStatus.text = "Good"
                                binding.textStatus.setTextColor(getResources().getColor(R.color.light_green))
                                binding.tvPerformanceText.text = "Good job! With a bit more attention to detail and independence, you’ll surely up your performance!"
                            } else if (parentalPercentage < 45 && parentalPercentage >= 35) {
                                binding.imgFace.setImageResource(R.drawable.average)
                                binding.textStatus.text = "Average"
                                binding.textStatus.setTextColor(getResources().getColor(R.color.yellow))
                                binding.tvPerformanceText.text = "Nice work! Work on improving your budget by always doublechecking. You’ll get there soon!"
                            } else if (parentalPercentage < 55 && parentalPercentage >= 45) {
                                binding.imgFace.setImageResource(R.drawable.nearly_there)
                                binding.textStatus.text = "Nearly There"
                                binding.textStatus.setTextColor(getResources().getColor(R.color.red))
                                binding.tvPerformanceText.text = "You're nearly there! Try budgeting more independently."
                            }  else if (parentalPercentage < 65 && parentalPercentage >= 55) {
                                binding.imgFace.setImageResource(R.drawable.almost_there)
                                binding.textStatus.text = "Almost There"
                                binding.textStatus.setTextColor(getResources().getColor(R.color.red))
                                binding.tvPerformanceText.text = "Almost there! You need to work on your budgeting. Try budgeting more independently."
                            } else if (parentalPercentage < 75 && parentalPercentage >= 65) {
                                binding.imgFace.setImageResource(R.drawable.getting_there)
                                binding.textStatus.text = "Getting There"
                                binding.textStatus.setTextColor(getResources().getColor(R.color.red))
                                binding.tvPerformanceText.text = "Getting there! You need to work on your budgeting. Try budgeting more independently."
                            } else if (parentalPercentage < 85 && parentalPercentage >= 75) {
                                binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
                                binding.textStatus.text = "Not Quite There Yet"
                                binding.textStatus.setTextColor(getResources().getColor(R.color.red))
                                binding.tvPerformanceText.text = "Not quite there yet! Don't give up. Try budgeting more independently."
                            } else if (parentalPercentage > 84) {
                                binding.imgFace.setImageResource(R.drawable.bad)
                                binding.textStatus.text = "Needs Improvement"
                                binding.textStatus.setTextColor(getResources().getColor(R.color.red))
                                binding.tvPerformanceText.text = "Your budgeting performance needs a lot of improvement. Try budgeting more independently."
                            }
                        }
                    }
                }
            }
        }
    }


//    private fun getBudgetAmountAccuracy() {
//
//        var budgetAmountScore = 0.00F
//        for (budgetID in budgetingActivityIDArrayList) {
//            firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetID).get().addOnSuccessListener { results ->
//                for (budgetItemID in results) {
//                    var nBudgetItems = results.size()
//                    firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID.id).get().addOnSuccessListener { transactionsResult ->
//                        var spentForBudgetItem = 0.00F
//                        for (transaction in transactionsResult)
//                            spentForBudgetItem += transaction.toObject<Transactions>().amount!!
//
//                        firestore.collection("BudgetItems").document(budgetItemID.id).get().addOnSuccessListener {
//                            budgetAmountScore = spentForBudgetItem / it.toObject<BudgetItem>()?.amount!!
//                        }.continueWith {
//                            budgetAmountAccuracyPercentage = (budgetAmountScore/nBudgetItems.toFloat())*100
//                            binding.textViewBudgetAccuracyProgress.text = DecimalFormat("##0.00").format(budgetAmountAccuracyPercentage) + "%"
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun getBudgetItemsAccuracy() {
//        var budgetItemsScore = 0.00F
//        var originalCount = 0.00F
//        var endCount = 0.00F
//        println("print budget amount accuracy activity id list size " +  budgetingActivityIDArrayList.size)
//        for (budgetActivityID in budgetingActivityIDArrayList) {
//            firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetActivityID).whereEqualTo("whenAdded", "Before").get().addOnSuccessListener { beforeResults ->
//                originalCount = beforeResults.size().toFloat()
//            }.continueWith {
//                var query1 = firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetActivityID).whereEqualTo("whenAdded", "Before").whereEqualTo("status", "Active").get()
//                var query2 = firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetActivityID).whereEqualTo("whenAdded", "After").whereEqualTo("status", "Active").get()
//                val combinedResult = Tasks.whenAllSuccess<QuerySnapshot>(query1, query2).result
//                endCount = combinedResult.sumOf { it.size() }.toFloat()
//                budgetItemsScore += originalCount/endCount
//            }
//
//        }
//
//        budgetItemsScore /= budgetItemIDArrayList.size.toFloat()
//        binding.textViewBudgetItemsAccuracyProgress.text = DecimalFormat("##0.00").format(budgetItemsScore) + "%"
//    }

    private fun setOverall(overall: Int) {
        // TODO set percentage

        if (overall >= 96) {
            binding.imgFace.setImageResource(R.drawable.excellent)
            binding.textStatus.text = "Excellent"
            binding.textStatus.setTextColor(getResources().getColor(R.color.dark_green))
            binding.tvPerformanceText.text = "Keep up the excellent work! Budgeting is your strong point. Keep making those budgets!"
        } else if (overall < 96 && overall >= 86) {
            binding.imgFace.setImageResource(R.drawable.amazing)
            binding.textStatus.text = "Amazing"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "Amazing job! You are performing well. Budgeting is your strong point. Keep making those budgets!"
        } else if (overall < 90 && overall >= 80) {
            binding.imgFace.setImageResource(R.drawable.great)
            binding.textStatus.text = "Great"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "You are performing well. Keep making those budgets!"
        } else if (overall < 80 && overall >= 70) {
            binding.imgFace.setImageResource(R.drawable.good)
            binding.textStatus.text = "Good"
            binding.textStatus.setTextColor(getResources().getColor(R.color.light_green))
            binding.tvPerformanceText.text = "Good job! With a bit more attention to detail, you’ll surely up your performance!"
        } else if (overall < 70 && overall >= 60) {
            binding.imgFace.setImageResource(R.drawable.average)
            binding.textStatus.text = "Average"
            binding.textStatus.setTextColor(getResources().getColor(R.color.yellow))
            binding.tvPerformanceText.text = "Nice work! Work on improving your budge always doublechecking. You’ll get there soon!"
        } else if (overall < 56 && overall >= 46) {
            binding.imgFace.setImageResource(R.drawable.nearly_there)
            binding.textStatus.text = "Nearly There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "You're nearly there! Click review to learn how to get there!"
        }  else if (overall < 46 && overall >= 36) {
            binding.imgFace.setImageResource(R.drawable.almost_there)
            binding.textStatus.text = "Almost There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Almost there! You need to work on your budgeting. Click review to learn how!"
        } else if (overall < 36 && overall >= 26) {
            binding.imgFace.setImageResource(R.drawable.getting_there)
            binding.textStatus.text = "Getting There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Getting there! You need to work on your budgeting. Click review to learn how!"
        } else if (overall < 26 && overall >= 16) {
            binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
            binding.textStatus.text = "Not Quite There Yet"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Not quite there yet! Don't give up. Click review to learn how to get there!"
        } else if (overall < 15) {
            binding.imgFace.setImageResource(R.drawable.bad)
            binding.textStatus.text = "Needs Improvement"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Your budgeting performance needs a lot of improvement. Click review to learn how!"
        }
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
}