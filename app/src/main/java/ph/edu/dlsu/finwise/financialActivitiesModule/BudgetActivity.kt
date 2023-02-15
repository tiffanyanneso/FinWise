package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.BudgetCategoryAdapter
import ph.edu.dlsu.finwise.databinding.ActivityBudgetBinding
import ph.edu.dlsu.finwise.databinding.DialogNewBudgetCategoryBinding
import ph.edu.dlsu.finwise.model.BudgetExpense
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat

class BudgetActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBudgetBinding
    private var firestore = Firebase.firestore
    private lateinit var budgetCategoryAdapter: BudgetCategoryAdapter
    lateinit var context:Context


    private lateinit var budgetActivityID:String
    private lateinit var savingActivityID:String
    private lateinit var financialGoalID:String
    private var budgetCategoryIDArrayList = ArrayList<String>()

    private var allocated = 0.00F
    private var totalBudget = 0.00F

    private var nextPriority = 0

    private var balance = 0.00F


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context =this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        var bundle: Bundle = intent.extras!!
        financialGoalID = bundle.getString("goalID").toString()
        budgetActivityID = bundle.getString("budgetActivityID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()

        //checkUser()
        getBalance()


        binding.btnNewCategory.setOnClickListener { showNewBudgetItemDialog() }
        binding.btnDoneSettingBudget.setOnClickListener {}
    }

    private fun getBalance() {
        firestore.collection("Transactions").whereEqualTo("financialActivityID", savingActivityID).get().addOnSuccessListener { results ->
            for (transaction in results) {
                var transactionObject = transaction.toObject<Transactions>()
                if (transactionObject.transactionType == "Deposit")
                    balance += transactionObject.amount!!
                else if (transactionObject.transactionType == "Withdrawal")
                    balance-= transactionObject.amount!!
            }
        }.continueWith { getBudgetItems() }
    }

    private fun getBudgetItems() {
        firestore.collection("BudgetItems").whereEqualTo("financialActivityID", budgetActivityID).get().addOnSuccessListener { budgetItems ->
            for (item in budgetItems)
                budgetCategoryIDArrayList.add(item.id)

            //set on click listener for menu item
            budgetCategoryAdapter = BudgetCategoryAdapter(this, budgetCategoryIDArrayList, object:BudgetCategoryAdapter.MenuClick{
                override fun clickMenuItem(position: Int, menuOption: String, budgetItemID: String) {
                    if (menuOption == "Edit")
                        editBudgetItem(budgetItemID)
                    else
                        deleteBudgetItem(position, budgetItemID)
                }
            })
            binding.rvViewCategories.adapter = budgetCategoryAdapter
            binding.rvViewCategories.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }.continueWith { deductExpenses() }
    }

    private fun deductExpenses() {
        if (budgetCategoryIDArrayList.size == 0)
            binding.tvSavingsAvailable.text = "₱ " +  DecimalFormat("#,###.00").format(balance)
        else {
            for (budgetCategoryID in budgetCategoryIDArrayList) {
                firestore.collection("BudgetExpenses").whereEqualTo("budgetCategoryID", budgetCategoryID).get().addOnSuccessListener { budgetExpenses ->
                    for (expense in budgetExpenses) {
                        var expenseObject = expense.toObject<BudgetExpense>()
                        balance -= expenseObject.amount!!
                    }
                }.continueWith { binding.tvSavingsAvailable.text = "₱ " +  DecimalFormat("#,###.00").format(balance) }
            }
        }
    }

    fun editBudgetItem(budgetItemID:String) {
        var dialogBinding= DialogNewBudgetCategoryBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(900, 1000)

        firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
            var budgetItem = it.toObject<BudgetItem>()
            dialogBinding.dialogEtCategoryName.setText(budgetItem?.budgetItemName.toString())
            dialogBinding.dialogEtCategoryAmount.setText(budgetItem?.amount!!.toInt().toString())

        }

        dialogBinding.btnSave.setOnClickListener {
            var itemName = dialogBinding.dialogEtCategoryName.text.toString()
            var itemAmount = dialogBinding.dialogEtCategoryAmount.text.toString().toFloat()

            firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
                var budgetItem = it.toObject<BudgetItem>()
                var nUpdate = budgetItem?.nUpdate!!.toInt() + 1

                firestore.collection("BudgetItems").document(budgetItemID).update("budgetItemName", itemName,
                    "amount", itemAmount, "nupdate", nUpdate).addOnSuccessListener {
                    budgetCategoryAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
            }
            //TODO: VALIDATION
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun deleteBudgetItem(position:Int, budgetItemID:String) {
        budgetCategoryIDArrayList.removeAt(position)
        budgetCategoryAdapter.notifyDataSetChanged()

        firestore.collection("BudgetItems").document(budgetItemID).delete()

    }

    private fun showNewBudgetItemDialog() {

        var dialogBinding= DialogNewBudgetCategoryBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(900, 1000)

        dialogBinding.btnSave.setOnClickListener {
            var itemName = dialogBinding.dialogEtCategoryName.text.toString()
            var itemAmount = dialogBinding.dialogEtCategoryAmount.text.toString().toFloat()
            var budgetCategory = BudgetItem(itemName, budgetActivityID, itemAmount, Timestamp.now(), 1)

            firestore.collection("BudgetItems").add(budgetCategory).addOnSuccessListener {
                budgetCategoryIDArrayList.add(it.id)
                budgetCategoryAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }


            //TODO: VALIDATION
            /*if (itemAmount.toFloat()+allocated > totalBudget)
                dialog.findViewById<TextView>(R.id.tv_error).visibility = View.VISIBLE
            else {
                firestore.collection("BudgetCategories").add(budgetCategory).addOnSuccessListener {
                    dialog.findViewById<TextView>(R.id.tv_error).visibility = View.GONE
                    dialog.dismiss()
                    budgetCategoryIDArrayList.add(it.id)
                    budgetCategoryAdapter.notifyDataSetChanged()
                }
            }*/
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun checkUser() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("ParentUser").document(currentUser).get().addOnSuccessListener {
            //current user is parent
            if (it.exists()) {
                binding.btnNewCategory.visibility = View.GONE
                binding.btnDoneSettingBudget.visibility = View.GONE
            }
        }
    }
}