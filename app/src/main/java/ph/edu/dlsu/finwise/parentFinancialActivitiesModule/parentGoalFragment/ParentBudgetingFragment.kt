package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentGoalFragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactBudgetingAdapter
import ph.edu.dlsu.finwise.adapter.FinactSavingAdapter
import ph.edu.dlsu.finwise.databinding.DialogBudgetingReviewBinding
import ph.edu.dlsu.finwise.databinding.FragmentParentBudgetingBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.BudgetingPerformanceActivity
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.util.*
import kotlin.math.roundToInt

class ParentBudgetingFragment : Fragment() {

    private lateinit var binding: FragmentParentBudgetingBinding
    private var firestore = Firebase.firestore
    private lateinit var budgetingAdapter: FinactBudgetingAdapter

    var goalIDArrayList = ArrayList<String>()
    var budgetingArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<GoalFilter>()

    //number of times the item was modified by the parent
    private var nParent = 0
    //number of budget items in total
    private var budgetItemCount = 0.00F
    //budget variance
    private var totalBudgetVariance = 0.00F

    var nUpdates = 0.00F
    var nItems = 0.00F

    private lateinit var childID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        childID = bundle?.getString("childID").toString()

        goalIDArrayList.clear()
        budgetingArrayList.clear()
        getBudgeting()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentBudgetingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvPerformancePercentage.text = "0.00%"
        binding.title.text = "Overall Budgeting Performance"
        getBudgeting()

        binding.btnSeeMore.setOnClickListener {
            var goToPerformance = Intent(requireContext().applicationContext,BudgetingPerformanceActivity::class.java)
            this.startActivity(goToPerformance)
        }
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }

    private fun getBudgeting() {
        goalIDArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            binding.tvTitleInProgress.text = "Budgeting Activities (" + results.size().toString() + ")"
            for (budgeting in results) {
                var budgetingActivity = budgeting.toObject<FinancialActivities>()
                goalIDArrayList.add(budgetingActivity.financialGoalID.toString())
            }
            getOverallBudgeting()
            loadRecyclerView(goalIDArrayList)
        }
    }

    private fun getOverallBudgeting() {
        firestore.collection("FinancialActivities").whereEqualTo("childID", childID).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "Completed").get().addOnSuccessListener { results ->
            for (activity in results) {
                //for parent involvement
                firestore.collection("BudgetItems").whereEqualTo("financialActivityID", activity.id).get().addOnSuccessListener { budgetItems ->
                    for (budgetItem in budgetItems) {
                        budgetItemCount++
                        var budgetItemObject = budgetItem.toObject<BudgetItem>()

                        if (budgetItemObject.status == "Edited")
                            nUpdates++
                        binding.tvAverageUpdates.text = (nUpdates/budgetItemCount).roundToInt().toString()


                        //budget variance - lower budget variance means better budgeting performance
                        firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItem.id).get().addOnSuccessListener { transactions ->
                            var spent = 0.00F
                            for (transaction in transactions)
                                spent += transaction.toObject<Transactions>()!!.amount!!

                            var budgetVariance = budgetItemObject.amount
                        }

                        //parental involvement
                        firestore.collection("Users").document(budgetItemObject.createdBy.toString()).get().addOnSuccessListener { user ->
                            //parent is the one who added the budget item
                            if (user.toObject<Users>()!!.userType == "Parent")
                                nParent++
                        }
                    }
                }
                setOverall()

            }
        }
    }

    private fun getAverageUpdates(budgetingActivityID:String){
        firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetingActivityID).whereEqualTo("status", "Completed").get().addOnSuccessListener { budgetItems ->
            for (budgetItem in budgetItems){
                var budgetItemObject = budgetItem.toObject<BudgetItem>()
                nItems++

            }
            var averageUpdates = 0
            if (nItems != 0.00F )
                averageUpdates = (nUpdates/nItems).roundToInt()

            binding.tvAverageUpdates.text = DecimalFormat("##0.##").format(averageUpdates)
            setOverall()
        }
    }

    private fun setOverall() {
        var overall = (1 - (nParent.toFloat()/budgetItemCount)) * 100

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

    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        budgetingAdapter = FinactBudgetingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = budgetingAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        budgetingAdapter.notifyDataSetChanged()
    }

    private fun showBudgetingReivewDialog() {

        var dialogBinding= DialogBudgetingReviewBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}