package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactBudgetingAdapter
import ph.edu.dlsu.finwise.databinding.DialogBudgetingReviewBinding
import ph.edu.dlsu.finwise.databinding.FragmentFinactBudgetingBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.performance.BudgetingPerformanceActivity
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.roundToInt

class BudgetingFragment : Fragment() {

    private lateinit var binding: FragmentFinactBudgetingBinding
    private var firestore = Firebase.firestore
    private lateinit var bugdetingAdapater: FinactBudgetingAdapter

    //contains only going budgeting activities for the recycler view
    var goalIDArrayList = ArrayList<String>()
    //used to get all budgeting activities to count parent involvement
    var budgetingArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<GoalFilter>()

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    //number of times the item was modified by the parent
    private var nParent = 0
    //number of budget items in total
    private var budgetItemCount = 0.00F
    //this is to count the number of budget items that have been already purchased in spending for budget accuracy
    private var purchasedBudgetItemCount  = 0.00F
    //budget variance
    private var totalBudgetAccuracy = 0.00F

    var nUpdates = 0.00F
    var nItems = 0.00F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goalIDArrayList.clear()
        budgetingArrayList.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFinactBudgetingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = "Overall Budgeting Performance"
        getBudgeting()

        binding.btnSeeMore.setOnClickListener {
            var goToPerformance = Intent(requireContext().applicationContext, BudgetingPerformanceActivity::class.java)
            this.startActivity(goToPerformance)
        }

        binding.btnSeeMore2.setOnClickListener {
            var goToPerformance = Intent(requireContext().applicationContext, BudgetingPerformanceActivity::class.java)
            this.startActivity(goToPerformance)
        }

        binding.btnBudgetingReview.setOnClickListener{
            showBudgetingReivewDialog()
        }
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }

    private fun getBudgeting() {
        goalIDArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            binding.tvTitleInProgress.text = "Budgeting Activities (" + results.size().toString() + ")"
            for (activity in results) {
                //add id to arraylit to load in recycler view
                var activityObject = activity.toObject<FinancialActivities>()
                goalIDArrayList.add(activityObject?.financialGoalID.toString())
            }
            getOverallBudgeting()
            loadRecyclerView(goalIDArrayList)
        }
    }

    private fun getOverallBudgeting() {
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
            for (activity in results) {
                firestore.collection("BudgetItems").whereEqualTo("financialActivityID", activity.id).whereEqualTo("status", "Active").get().addOnSuccessListener { budgetItems ->
                    println("print number of budget items" + budgetItems.size())
                    for (budgetItem in budgetItems) {
                        budgetItemCount++
                        var budgetItemObject = budgetItem.toObject<BudgetItem>()
                        if (budgetItemObject.status == "Edited")
                            nUpdates++
                        binding.tvAverageUpdates.text = (nUpdates / budgetItemCount).roundToInt().toString()


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
                var spendingActivity = spending.documents[0].toObject<FinancialActivities>()
                if (spendingActivity?.status == "Completed") {
                    //budget accuracy
                    purchasedBudgetItemCount++
                    firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID).get().addOnSuccessListener { transactions ->
                        var spent = 0.00F
                        for (transaction in transactions)
                            spent += transaction.toObject<Transactions>()!!.amount!!
                        println("print budget accuracy " +  (100 - (abs(budgetItemObject.amount!! - spent) / budgetItemObject.amount!!) * 100))
                        if (budgetItemObject.amount!! !=0.00F)
                            totalBudgetAccuracy += (100 - (abs(budgetItemObject.amount!! - spent) / budgetItemObject.amount!!) * 100)
                    }.continueWith {
                        setOverall()
                    }
                } else
                    setOverall()
            }
        }
    }

    private fun setOverall() {
        var overall = (  (totalBudgetAccuracy/purchasedBudgetItemCount) + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2

        binding.tvPerformancePercentage.text = "${DecimalFormat("##0.0").format(overall)}%"

        if (overall >= 96) {
            binding.imgFace.setImageResource(R.drawable.excellent)
            binding.textStatus.text = "Excellent"
            binding.textStatus.setTextColor(getResources().getColor(R.color.dark_green))
            binding.tvPerformanceText.text = "Keep up the excellent work! Budgeting is your strong point. Keep making those budgets!"
            showSeeMoreButton()
        } else if (overall < 96 && overall >= 86) {
            binding.imgFace.setImageResource(R.drawable.amazing)
            binding.textStatus.text = "Amazing"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "Amazing job! You are performing well. Budgeting is your strong point. Keep making those budgets!"
        } else if (overall < 86 && overall >= 76) {
            binding.imgFace.setImageResource(R.drawable.great)
            binding.textStatus.text = "Great"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "You are performing well. Keep making those budgets!"
            showSeeMoreButton()
        } else if (overall < 76 && overall >= 66) {
            binding.imgFace.setImageResource(R.drawable.good)
            binding.textStatus.text = "Good"
            binding.textStatus.setTextColor(getResources().getColor(R.color.light_green))
            binding.tvPerformanceText.text = "Good job! With a bit more attention to detail, you’ll surely up your performance!"
            showSeeMoreButton()
        } else if (overall < 66 && overall >= 56) {
            binding.imgFace.setImageResource(R.drawable.average)
            binding.textStatus.text = "Average"
            binding.textStatus.setTextColor(getResources().getColor(R.color.yellow))
            binding.tvPerformanceText.text = "Nice work! Work on improving your budget by always doublechecking. You’ll get there soon!"
            showSeeMoreButton()
        } else if (overall < 56 && overall >= 46) {
            binding.imgFace.setImageResource(R.drawable.nearly_there)
            binding.textStatus.text = "Nearly There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "You're nearly there! Click review to learn how to get there!"
            showSeeMoreButton()
        }  else if (overall < 46 && overall >= 36) {
            binding.imgFace.setImageResource(R.drawable.almost_there)
            binding.textStatus.text = "Almost There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Almost there! You need to work on your budgeting. Click review to learn how!"
            showSeeMoreButton()
        } else if (overall < 36 && overall >= 26) {
            binding.imgFace.setImageResource(R.drawable.getting_there)
            binding.textStatus.text = "Getting There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Getting there! You need to work on your budgeting. Click review to learn how!"
            showSeeMoreButton()
        } else if (overall < 26 && overall >= 16) {
            binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
            binding.textStatus.text = "Not Quite\nThere"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Not quite there yet! Don't give up. Click review to learn how to get there!"
            showSeeMoreButton()
        } else if (overall < 15) {
            binding.imgFace.setImageResource(R.drawable.bad)
            binding.textStatus.text = "Needs\nImprovement"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Your budgeting performance needs a lot of improvement. Click review to learn how!"
            showSeeMoreButton()
        }
    }

    private fun showSeeMoreButton() {
        binding.btnSeeMore.visibility = View.VISIBLE
        binding.layoutButtons.visibility = View.GONE
    }

    private fun showReviewButton() {
        binding.btnSeeMore.visibility = View.GONE
        binding.layoutButtons.visibility = View.VISIBLE
    }

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        bugdetingAdapater = FinactBudgetingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = bugdetingAdapater
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext, LinearLayoutManager.VERTICAL, false)
        bugdetingAdapater.notifyDataSetChanged()
    }

    private fun showBudgetingReivewDialog() {

        var dialogBinding= DialogBudgetingReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1400)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}