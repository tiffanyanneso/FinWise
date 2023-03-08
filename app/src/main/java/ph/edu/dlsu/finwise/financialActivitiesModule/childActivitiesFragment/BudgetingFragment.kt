package ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.edu.dlsu.finwise.adapter.FinactBudgetingAdapter
import ph.edu.dlsu.finwise.databinding.FragmentBudgetingBinding
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class BudgetingFragment : Fragment() {

    private lateinit var binding: FragmentBudgetingBinding
    private var firestore = Firebase.firestore
    private lateinit var bugdetingAdapater: FinactBudgetingAdapter

    //contains only going budgeting activities for the recycler view
    var goalIDArrayList = ArrayList<String>()
    //used to get all budgeting activities to count parent involvement
    private var parentalInvolvementArrayList = ArrayList<String>()
    var budgetingArrayList = ArrayList<FinancialActivities>()
    var goalFilterArrayList = ArrayList<GoalFilter>()
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
        goalIDArrayList.clear()
        budgetingArrayList.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.titleOverallBudegtingPerformance.text = "Overall Budgeting\nPerformance"
        binding.tvPerformancePercentage.text = "0.00%"
        getBudgeting()
    }

    class GoalFilter(var financialGoalID: String?=null, var goalTargetDate: Date?=null){
    }

    private fun getBudgeting() {
        goalIDArrayList.clear()
        //saving activities that are in progress means that there the goal is also in progress because they are connected
        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Budgeting").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            binding.tvInProgress.text = results.size().toString()
            for (activity in results) {
                //add id to arraylit to load in recycler view
                var activityObject = activity.toObject<FinancialActivities>()
                goalIDArrayList.add(activityObject?.financialGoalID.toString())
            }
            getParentalInvolvement()
            loadRecyclerView(goalIDArrayList)
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
                            binding.tvParentalInvolvementPercent.text = DecimalFormat("##0.##").format((nParent.toFloat()/budgetItemCount.toFloat())*100)+ "%"
                            binding.progressBarParentalInvolvement.progress = ((nParent.toFloat()/budgetItemCount.toFloat())*100).roundToInt()
                        }
                    }
                }
                getAverageUpdates(activity.id)
            }
        }
    }

    private fun getAverageUpdates(budgetingActivityID:String){
        firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetingActivityID).get().addOnSuccessListener { budgetItems ->
            for (budgetItem in budgetItems){
                var budgetItemObject = budgetItem.toObject<BudgetItem>()
                nItems++
                if (budgetItemObject.status == "Edited")
                    nUpdates++
            }
            binding.tvAverageUpdates.text = DecimalFormat("##0.##").format((nUpdates/nItems.roundToInt()))
        }
    }


    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
        bugdetingAdapater = FinactBudgetingAdapter(requireContext().applicationContext, goalIDArrayList)
        binding.rvViewGoals.adapter = bugdetingAdapater
        binding.rvViewGoals.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        bugdetingAdapater.notifyDataSetChanged()
    }

}