package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.BudgetCategoryTemplateAdapter
import ph.edu.dlsu.finwise.databinding.ActivityAddFriendsBinding.inflate
import ph.edu.dlsu.finwise.databinding.ActivityCompletedEarningBinding
import ph.edu.dlsu.finwise.databinding.ActivitySelectBudgetItemTemplateBinding
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningActivity
import java.text.DecimalFormat

class ActivitySelectBudgetItemTemplate : AppCompatActivity() {

    private lateinit var binding: ActivitySelectBudgetItemTemplateBinding

    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid


    private lateinit var savingActivityID:String
    private lateinit var budgetingActivityID:String
    private lateinit var spendingActivityID:String
    private lateinit var financialGoalID:String
    private var goalAmount = 0.00F

    private var budgetItemsList = ArrayList<String>()


    private lateinit var budgetCategorySelectAdapter:BudgetCategoryTemplateAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBudgetItemTemplateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        budgetingActivityID = bundle.getString("budgetingActivityID").toString()

        CoroutineScope(Dispatchers.Main).launch {
            createBudgetTemplate()
            getCommonBudget()
            getActivityIDs()
        }

        binding.btnSaveBudgetItems.setOnClickListener {
            saveBudgetItems()
        }
    }

    private suspend fun createBudgetTemplate() {
       var budgetingActivity = firestore.collection("FinancialActivities").document(budgetingActivityID).get().await()
       var goalSnapshot = firestore.collection("FinancialGoals").document(budgetingActivity.toObject<FinancialActivities>()?.financialGoalID!!).get().await()
       financialGoalID = goalSnapshot.id

       var goal = goalSnapshot.toObject<FinancialGoals>()
        goalAmount = goal?.targetAmount!!
        binding.tvBudgetAmount.text =  "₱ ${DecimalFormat("#,##0.00").format(goalAmount)}"
       if (goal?.financialActivity == "Buying Items") {
           budgetItemsList.add("Food & Drinks")
           budgetItemsList.add("Toys & Games")
           budgetItemsList.add("Gift")
       } else if (goal?.financialActivity == "Planning An Event") {
           budgetItemsList.add("Food & Drinks")
           budgetItemsList.add("Gift")
           budgetItemsList.add("Decorations")
           budgetItemsList.add("Rental")
           budgetItemsList.add("Transportation")
           budgetItemsList.add("Party Favors")
       } else if (goal?.financialActivity == "Situational Shopping") {
           budgetItemsList.add("Food & Drinks")
           budgetItemsList.add("Clothing")
       }

       budgetItemsList.add("Others")

       budgetCategorySelectAdapter = BudgetCategoryTemplateAdapter(this, budgetItemsList, binding.rvBudgetItemTemplate)
       binding.rvBudgetItemTemplate.adapter = budgetCategorySelectAdapter
       binding.rvBudgetItemTemplate.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
       budgetCategorySelectAdapter.notifyDataSetChanged()
    }

    private fun saveBudgetItems() {
        if (budgetCategorySelectAdapter.valid()) {
            var checkedBudgetItems = budgetCategorySelectAdapter.returnCheckedBudgetItems()
            var otherBudgetItem = budgetCategorySelectAdapter.otherBudgetItem()
            if (!checkedBudgetItems.isEmpty() || otherBudgetItem != null) {
                binding.tvError.visibility = View.GONE
                if (!checkedBudgetItems.isEmpty()) {
                    for (budgetItem in checkedBudgetItems) {
                        var budgetItem = BudgetItem(budgetItem.itemName, null, budgetingActivityID,
                            budgetItem.amount, "Active", "Before", currentUser)
                        firestore.collection("BudgetItems").add(budgetItem)
                    }
                }

                if (otherBudgetItem != null) {
                    var budgetItem = BudgetItem(otherBudgetItem.itemName, otherBudgetItem.otherBudgetItemName, budgetingActivityID,
                        otherBudgetItem.amount, "Active", "Before", currentUser)
                    firestore.collection("BudgetItems").add(budgetItem)
                }

                //go to budgeting
                var budgeting = Intent(this, BudgetActivity::class.java)
                var bundle = Bundle()
                bundle.putString("savingActivityID", savingActivityID)
                bundle.putString("budgetingActivityID", budgetingActivityID)
                bundle.putString("spendingActivityID", spendingActivityID)
                bundle.putString("childID", currentUser)
                budgeting.putExtras(bundle)
                startActivity(budgeting)
                finish()
            } else
                binding.tvError.visibility = View.VISIBLE
        }
    }

    private suspend fun getCommonBudget() {
        var commonText = ""
        for (budgetItem in budgetItemsList) {
            var budgetAmount = 0.00F
            var budgetItems = firestore.collection("BudgetItems").whereEqualTo("createdBy", currentUser)
                .whereEqualTo("budgetItemName", budgetItem).whereLessThan("amount", goalAmount).get().await()

            //adding the amount spent for a certain budget item
            for (item in budgetItems) {
                budgetAmount += item.toObject<BudgetItem>().amount!!
            }

            if (!budgetItems.isEmpty && budgetItem!= "Others")
                commonText += "${budgetItem} : ₱ ${DecimalFormat("#,##0.00").format(budgetAmount/budgetItems.size())} \n"
        }

        if (commonText.isEmpty())
            binding.layoutCommonBudgetItems.visibility = View.GONE

        binding.tvCommonBudgetItemsKids.text = commonText
        binding.mainLayout.visibility = View.VISIBLE
        binding.layoutLoading.visibility = View.GONE


    }



    private fun getActivityIDs() {
        firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", financialGoalID)
            .get().addOnSuccessListener { results ->
            for (finActivity in results) {
                val activityObject = finActivity.toObject<FinancialActivities>()
                if (activityObject.financialActivityName == "Saving")
                    savingActivityID = finActivity.id
                if (activityObject.financialActivityName == "Budgeting")
                    budgetingActivityID = finActivity.id
                if (activityObject.financialActivityName == "Spending")
                    spendingActivityID = finActivity.id
            }
        }
    }
}