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
import ph.edu.dlsu.finwise.ParentBudgetingPerformanceActivity
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.FinactBudgetingAdapter
import ph.edu.dlsu.finwise.adapter.FinactSavingAdapter
import ph.edu.dlsu.finwise.databinding.DialogBudgetingReviewBinding
import ph.edu.dlsu.finwise.databinding.DialogParentBudgetingTipsBinding
import ph.edu.dlsu.finwise.databinding.FragmentParentBudgetingBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.BudgetingPerformanceActivity
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.util.*
import kotlin.math.abs
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

    //this is to count the number of budget items that have been already purchased in spending for budget accuracy
    private var purchasedBudgetItemCount  = 0.00F
    //budget variance
    private var totalBudgetAccuracy = 0.00F

    var nUpdates = 0.00F

    private lateinit var childID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        childID = bundle?.getString("childID").toString()

        goalIDArrayList.clear()
        budgetingArrayList.clear()
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
            var goToPerformance = Intent(requireContext().applicationContext, ParentBudgetingPerformanceActivity::class.java)

            var bundle = Bundle()

            bundle.putString("childID", childID)
            goToPerformance.putExtras(bundle)
            goToPerformance.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            this.startActivity(goToPerformance)
        }

        binding.btnSeeMore2.setOnClickListener {
            var goToPerformance = Intent(requireContext().applicationContext, ParentBudgetingPerformanceActivity::class.java)

            var bundle = Bundle()

            bundle.putString("childID", childID)
            goToPerformance.putExtras(bundle)
            goToPerformance.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            this.startActivity(goToPerformance)
        }

        binding.btnBudgetingTips.setOnClickListener{
            showBudgetingReivewDialog()
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
                firestore.collection("BudgetItems").whereEqualTo("financialActivityID", activity.id).get().addOnSuccessListener { budgetItems ->
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
        var overall = ((totalBudgetAccuracy/purchasedBudgetItemCount) + ((1 - (nParent.toFloat()/budgetItemCount)) * 100)) /2

        binding.tvPerformancePercentage.text = "${DecimalFormat("##0.0").format(overall)}%"

        if (overall >= 96) {
            binding.imgFace.setImageResource(R.drawable.excellent)
            binding.textStatus.text = "Excellent"
            binding.textStatus.setTextColor(getResources().getColor(R.color.dark_green))
            binding.tvPerformanceText.text = "Your child excels at budgeting. Encourage them to keep up the excellent work."
            showSeeMoreButton()
        } else if (overall < 96 && overall >= 86) {
            binding.imgFace.setImageResource(R.drawable.amazing)
            binding.textStatus.text = "Amazing"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "Your child is amazing at budgeting!  Encourage them to keep up the excellent work."
        } else if (overall < 90 && overall >= 80) {
            binding.imgFace.setImageResource(R.drawable.great)
            binding.textStatus.text = "Great"
            binding.textStatus.setTextColor(getResources().getColor(R.color.green))
            binding.tvPerformanceText.text = "Your child is doing a great job of budgeting!"
            showSeeMoreButton()
        } else if (overall < 80 && overall >= 70) {
            binding.imgFace.setImageResource(R.drawable.good)
            binding.textStatus.text = "Good"
            binding.textStatus.setTextColor(getResources().getColor(R.color.light_green))
            binding.tvPerformanceText.text = "Your child is doing a good job of budgeting! Encourage them to review their budget."
            showSeeMoreButton()
        } else if (overall < 70 && overall >= 60) {
            binding.imgFace.setImageResource(R.drawable.average)
            binding.textStatus.text = "Average"
            binding.textStatus.setTextColor(getResources().getColor(R.color.yellow))
            binding.tvPerformanceText.text = "Your child is doing a nice job of budgeting! Encourage them to always doublecheck their budget."
            showSeeMoreButton()
        } else if (overall < 56 && overall >= 46) {
            binding.imgFace.setImageResource(R.drawable.nearly_there)
            binding.textStatus.text = "Nearly There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Your child is nearly there! Click the tips button to learn how to get there!"
            showReviewButton()
        }  else if (overall < 46 && overall >= 36) {
            binding.imgFace.setImageResource(R.drawable.almost_there)
            binding.textStatus.text = "Almost There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Your child is almost there! Click the tips button to learn how to help them get there!"
            showReviewButton()
        } else if (overall < 36 && overall >= 26) {
            binding.imgFace.setImageResource(R.drawable.getting_there)
            binding.textStatus.text = "Getting There"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Your child is getting there! Click the tips button to learn how to help them get there!"
            showReviewButton()
        } else if (overall < 26 && overall >= 16) {
            binding.imgFace.setImageResource(R.drawable.not_quite_there_yet)
            binding.textStatus.text = "Not Quite There Yet"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Your child is not quite there yet! Click the tips button to learn how to help them get there!"
            showReviewButton()
        } else if (overall < 15) {
            binding.imgFace.setImageResource(R.drawable.bad)
            binding.textStatus.text = "Needs Improvement"
            binding.textStatus.setTextColor(getResources().getColor(R.color.red))
            binding.tvPerformanceText.text = "Uh oh! Click the tips button to learn how to help them get there!"
            showReviewButton()
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
        budgetingAdapter = FinactBudgetingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = budgetingAdapter
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        budgetingAdapter.notifyDataSetChanged()
    }

    private fun showBudgetingReivewDialog() {

        var dialogBinding= DialogParentBudgetingTipsBinding.inflate(getLayoutInflater())
        var dialog= Dialog(requireContext().applicationContext);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}