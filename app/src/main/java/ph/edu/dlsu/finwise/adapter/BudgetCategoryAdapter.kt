package ph.edu.dlsu.finwise.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ItemBudgetCategoryBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.BudgetActivity
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import kotlin.math.absoluteValue

class BudgetCategoryAdapter : RecyclerView.Adapter<BudgetCategoryAdapter.BudgetCategoryViewHolder>{

    private var budgetCategoryIDArrayList = ArrayList<String>()
    private var context: Context
    private var menuClick:MenuClick
    private var itemClick:ItemClick
    private var spendingActivityID:String
    private var firestore = Firebase.firestore

    constructor(context: Context, budgetCategoryIDArrayList:ArrayList<String>, spendingActivityID:String , menuClick:MenuClick, itemClick:ItemClick) {
        this.context = context
        this.budgetCategoryIDArrayList = budgetCategoryIDArrayList
        this.spendingActivityID = spendingActivityID
        this.menuClick = menuClick
        this.itemClick = itemClick
    }

    override fun getItemCount(): Int {
        return budgetCategoryIDArrayList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BudgetCategoryAdapter.BudgetCategoryViewHolder {
        val itemBinding = ItemBudgetCategoryBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        return BudgetCategoryViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BudgetCategoryAdapter.BudgetCategoryViewHolder,
                                  position: Int) {
        holder.bindCategory(budgetCategoryIDArrayList[position])
    }

    inner class BudgetCategoryViewHolder(private val itemBinding: ItemBudgetCategoryBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        @SuppressLint("ResourceAsColor")
        fun bindCategory(budgetItemID: String){

            itemBinding.btnSettings.setOnClickListener {
                val popup = PopupMenu(context, it)
                popup.inflate(R.menu.menu_budget_category)
                popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

                    var budgetActivity = BudgetActivity()
                    when (item!!.itemId) {
                        R.id.tv_edit_budget -> {
                            popup.dismiss()
                            menuClick.clickMenuItem(position, "Edit",
                                itemBinding.tvBudgetItemId.text.toString()
                            )
                        }
                        R.id.tv_delete_budget_item -> {
                            popup.dismiss()
                            menuClick.clickMenuItem(position,"Delete",
                                itemBinding.tvBudgetItemId.text.toString()
                            )
                        }
                    }
                    true
                })
                popup.show()
            }

            firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
                var budgetCategory = it.toObject<BudgetItem>()
                itemBinding.tvBudgetItemId.text = it.id
                itemBinding.tvBudgetingActivityId.text = budgetCategory?.financialActivityID

                if (budgetCategory?.budgetItemName != "Others")
                    itemBinding.tvBudgetItemName.text = budgetCategory?.budgetItemName
                else
                    itemBinding.tvBudgetItemName.text = budgetCategory?.budgetItemNameOther

                var categoryAmount = budgetCategory?.amount
                var spent = 0.00F
                firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID).whereEqualTo("transactionType", "Expense").get().addOnSuccessListener { results  ->
                    for (expense in results) {
                        var expenseObject = expense.toObject<Transactions>()
                        spent += expenseObject.amount!!.toFloat()
                    }
                }.continueWith {
                    var available = categoryAmount?.minus(spent)
                    if (available!! >= 0.00F ) {
                        itemBinding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(available) + " left available"
                        itemBinding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.very_light_green))
                    } else {
                        itemBinding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(available.absoluteValue) + " over the budget"
                        itemBinding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_red))
                    }
                    itemBinding.progressBar.progress = (spent/ categoryAmount!! *100).toInt()

                    var activitiesDone = true
                    firestore.collection("FinancialActivities").document(budgetCategory?.financialActivityID!!).get().addOnSuccessListener { budget ->
                        if (budget.toObject<FinancialActivities>()!!.status != "Completed")
                            activitiesDone = false
                    }.continueWith {
                        firestore.collection("FinancialActivities").document(spendingActivityID).get().addOnSuccessListener { spend ->
                            if (spend.toObject<FinancialActivities>()!!.status != "Completed")
                                activitiesDone = false
                        }.continueWith {
                            if (activitiesDone)
                                itemBinding.btnSettings.visibility = View.GONE
                        }
                    }
                }
            }
        }

        override fun onClick(p0: View?) {
            itemClick.clickItem(itemBinding.tvBudgetItemId.text.toString(), itemBinding.tvBudgetingActivityId.text.toString())
        }
    }

    interface MenuClick{
        fun clickMenuItem(position: Int, menuOption:String, budgetItemID:String)
    }

    interface ItemClick{
        fun clickItem (budgetItemID:String, budgetingActivityID:String)
    }
}