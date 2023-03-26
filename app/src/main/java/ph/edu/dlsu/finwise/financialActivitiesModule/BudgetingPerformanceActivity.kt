package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
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
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
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
import kotlin.math.abs
import kotlin.math.roundToInt

class BudgetingPerformanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetingPerformanceBinding
    private var firestore = Firebase.firestore

    //contains only going budgeting activities for the recycler view
    var goalIDArrayList = ArrayList<String>()
    //used to get all budgeting activities to count parent involvement
    var budgetingArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<BudgetingFragment.GoalFilter>()

    //arraylist that holds all user IDs for createdBy fields in BudgetItem, for parental involvement
    private var createdByUserIDArrayList = ArrayList<String>()

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    //number of times the item was modified by the parent
    private var nParent = 0
    //number of budget items in total
    private var budgetItemCount = 0.00F
    //this is to count the number of budget items that have been already purchased in spending for budget accuracy
    private var purchasedBudgetItemCount  = 0.00F
    //budget variance
    private var totalBudgetAccuracy = 0.00F

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
        setNavigationBar()
        loadBackButton()

            binding.btnReview.setOnClickListener{
                showBudgetingReivewDialog()
            }

            binding.btnBudgetAccuracyReview.setOnClickListener{
                showBudgetAccuracyAmountReivewDialog()
            }
    }

    private fun getBudgeting() {
        goalIDArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            for (activity in results) {
                //add id to arraylit to load in recycler view
                var activityObject = activity.toObject<FinancialActivities>()
                goalIDArrayList.add(activityObject?.financialGoalID.toString())
            }
            getOverallBudgeting()
            getParentalInvolvement()
        }
    }

    private fun getParentalInvolvement() {
        nParent = 0
        budgetItemCount = 0.00F
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
                                binding.textViewPerformanceTextParentalInvolvement.text = "Excellent"
                                binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.dark_green))
                                binding.tvParentalInvolvementText.text = "Keep up the excellent work! You are able to budget independently."
                            } else if (parentalPercentage < 15 && parentalPercentage >= 5) {
                                binding.textViewPerformanceTextParentalInvolvement.text = "Amazing"
                                binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.green))
                                binding.tvParentalInvolvementText.text = "Amazing job! Keep up making those budget independently."
                            } else if (parentalPercentage < 25 && parentalPercentage >= 15) {
                                binding.textViewPerformanceTextParentalInvolvement.text = "Great"
                                binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.green))
                                binding.tvParentalInvolvementText.text = "You are performing well! Keep up making those budget independently."
                            } else if (parentalPercentage < 35 && parentalPercentage >= 25) {
                                binding.textViewPerformanceTextParentalInvolvement.text = "Good"
                                binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.light_green))
                                binding.tvParentalInvolvementText.text = "Good job! With a bit more attention to detail and independence, you’ll surely up your performance!"
                            } else if (parentalPercentage < 45 && parentalPercentage >= 35) {
                                binding.textViewPerformanceTextParentalInvolvement.text = "Average"
                                binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.yellow))
                                binding.tvParentalInvolvementText.text = "Nice work! Work on improving your budget by always doublechecking. You’ll get there soon!"
                            } else if (parentalPercentage < 55 && parentalPercentage >= 45) {
                                binding.textViewPerformanceTextParentalInvolvement.text = "Nearly There"
                                binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.red))
                                binding.tvParentalInvolvementText.text = "You're nearly there! Try budgeting more independently."
                            }  else if (parentalPercentage < 65 && parentalPercentage >= 55) {
                                binding.textViewPerformanceTextParentalInvolvement.text = "Almost There"
                                binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.red))
                                binding.tvParentalInvolvementText.text = "Almost there! You need to work on your budgeting. Try budgeting more independently."
                            } else if (parentalPercentage < 75 && parentalPercentage >= 65) {
                                binding.textViewPerformanceTextParentalInvolvement.text = "Getting There"
                                binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.red))
                                binding.tvParentalInvolvementText.text = "Getting there! You need to work on your budgeting. Try budgeting more independently."
                            } else if (parentalPercentage < 85 && parentalPercentage >= 75) {
                                binding.textViewPerformanceTextParentalInvolvement.text = "Not Quite\nThere"
                                binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.red))
                                binding.tvParentalInvolvementText.text = "Not quite there yet! Don't give up. Try budgeting more independently."
                            } else if (parentalPercentage > 84) {
                                binding.textViewPerformanceTextParentalInvolvement.text = "Needs\nImprovement"
                                binding.textViewPerformanceTextParentalInvolvement.setTextColor(getResources().getColor(R.color.red))
                                binding.tvParentalInvolvementText.text = "Your budgeting performance needs a lot of improvement. Try budgeting more independently."
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getOverallBudgeting() {
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
            for (activity in results) {
                firestore.collection("BudgetItems").whereEqualTo("financialActivityID", activity.id).get().addOnSuccessListener { budgetItems ->
                    for (budgetItem in budgetItems) {
                        budgetItemCount++
                        var budgetItemObject = budgetItem.toObject<BudgetItem>()
                        if (budgetItemObject.status == "Edited")
                            nUpdates++
//                        binding.tvAverageUpdates.text = (nUpdates / budgetItemCount).roundToInt().toString()


                        //parental involvement
                        firestore.collection("Users").document(budgetItemObject.createdBy.toString()).get().addOnSuccessListener { user ->
                            //parent is the one who added the budget item
                            if (user.toObject<Users>()!!.userType == "Parent")
                                nParent++
                        }.continueWith {
                            getBudgetAccuracy(activity.id, budgetItem.id, budgetItemObject)
                        }
                    }
                }
            }
        }
    }

    private fun getBudgetAccuracy(budgetingActivityID:String, budgetItemID:String, budgetItemObject:BudgetItem) {
        firestore.collection("FinancialActivities").document(budgetingActivityID).get().addOnSuccessListener {
            firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", it.toObject<FinancialActivities>()!!.financialGoalID!!).whereEqualTo("financialActivityName", "Spending").get().addOnSuccessListener { spending ->
                val spendingActivity = spending.documents[0].toObject<FinancialActivities>()
                if (spendingActivity?.status == "Completed") {
                    //budget accuracy
                    purchasedBudgetItemCount++
                    firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID).get().addOnSuccessListener { transactions ->
                        var spent = 0.00F
                        for (transaction in transactions)
                            spent += transaction.toObject<Transactions>()!!.amount!!
                        println("print budget accuracy " +  (100 - (abs(budgetItemObject.amount!! - spent) / budgetItemObject.amount!!) * 100))
                        totalBudgetAccuracy += (100 - (abs(budgetItemObject.amount!! - spent) / budgetItemObject.amount!!) * 100)
                    }.continueWith {
                        setOverall()
                        setBudgetAccuracy()
                    }
                } else {
                    setOverall()
                    setBudgetAccuracy()
                }
            }
        }
    }

    private fun setBudgetAccuracy() {
        var budgetAccuracy = totalBudgetAccuracy.roundToInt()
        binding.textViewBudgetAccuracyProgress.text =  DecimalFormat("##0.##").format(totalBudgetAccuracy) + "%"
        binding.progressBarBudgetAccuracy.progress = totalBudgetAccuracy.roundToInt()

        if (budgetAccuracy >= 96) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Excellent"
            binding.textStatus.setTextColor(getResources().getColor(R.color.dark_green))
            binding.tvPerformanceText.text = "Keep up the excellent work! Your budget is often accurate."
        } else if (budgetAccuracy < 96 && budgetAccuracy >= 86) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Amazing"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "Amazing job! You are performing well. You are amazing at creating accurate budgets."
        } else if (budgetAccuracy < 90 && budgetAccuracy >= 80) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Great"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "You are performing well. Keep making those accurate budgets!"
        } else if (budgetAccuracy < 80 && budgetAccuracy >= 70) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Good"
            binding.textStatus.setTextColor(getResources().getColor(R.color.light_green))
            binding.tvPerformanceText.text = "Good job! With a bit more attention to detail, you’ll surely up your performance!"
        } else if (budgetAccuracy < 70 && budgetAccuracy >= 60) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Average"
            binding.textStatus.setTextColor(getResources().getColor(R.color.yellow))
            binding.tvPerformanceText.text = "Nice work! Work on improving your budget by always doublechecking. You’ll get there soon!"
        } else if (budgetAccuracy < 56 && budgetAccuracy >= 46) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Nearly There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "You're nearly there! Click review to learn how to get there!"
        }  else if (budgetAccuracy < 46 && budgetAccuracy >= 36) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Almost There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Almost there! You need to work on your budget accuracy. Click review to learn how!"
        } else if (budgetAccuracy < 36 && budgetAccuracy >= 26) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Getting There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Getting there! You need to work on your budget accuracy. Click review to learn how!"
        } else if (budgetAccuracy < 26 && budgetAccuracy >= 16) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Not Quite\nThere"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Not quite there yet! Don't give up. Click review to learn how to get there!"
        } else if (budgetAccuracy < 15) {
            binding.textViewBudgetAccuracyPerformanceText.text = "Needs\nImprovement"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Your budget accuracy needs a lot of improvement. Click review to learn how!"
        }
    }

    private fun setOverall() {
        var overall = ((totalBudgetAccuracy/purchasedBudgetItemCount) + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2

        binding.tvPerformancePercentage.text = "${DecimalFormat("##0.0").format(overall)}%"

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
            binding.tvPerformanceText.text = "Nice work! Work on improving your budget by always doublechecking. You’ll get there soon!"
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
            binding.textStatus.text = "Not Quite\nThere"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Not quite there yet! Don't give up. Click review to learn how to get there!"
        } else if (overall < 15) {
            binding.imgFace.setImageResource(R.drawable.bad)
            binding.textStatus.text = "Needs\nImprovement"
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