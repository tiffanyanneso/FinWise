package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.*
import ph.edu.dlsu.finwise.adapter.BudgetCategoryAdapter
import ph.edu.dlsu.finwise.databinding.ActivityBudgetBinding
import ph.edu.dlsu.finwise.databinding.ActivityProfileBinding
import ph.edu.dlsu.finwise.databinding.ActivitySpendingBinding
import ph.edu.dlsu.finwise.model.BudgetCategory
import ph.edu.dlsu.finwise.model.DecisionMakingActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions

class SpendingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpendingBinding
    private var firestore = Firebase.firestore
    //private lateinit var spendingCategoryAdapter: SpendingCategoryAdapter
    //private lateinit var spendingHistoryAdaper: SpendingHistoryAdapter

    private lateinit var decisionMakingActivityID:String
    private lateinit var financialGoalID:String
    private var spentAmount = 0.00F

    private lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpendingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        var bundle: Bundle = intent.extras!!
        decisionMakingActivityID = bundle.getString("decisionMakingActivityID").toString()
        financialGoalID = bundle.getString("financialGoalID").toString()


        //checks status of goal. if goal is achieved, do not show "Done Spending" button
        showButton()
        getSpent()
        firestore.collection("DecisionMakingActivities").document(decisionMakingActivityID).get().addOnSuccessListener {
            if (it!=null) {
                var decisionActivity = it.toObject<DecisionMakingActivities>()
                binding.tvSpendingProgress.text = "₱ " + spentAmount + " / ₱ " + decisionActivity?.targetAmount
                binding.progressBar.max = decisionActivity?.targetAmount!!.toInt()
                binding.progressBar.progress = spentAmount.toInt()
            }
        }

        binding.btnAddExpense.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("decisionMakingActivityID", decisionMakingActivityID)
            bundle.putString("financialGoalID", financialGoalID)
            var recordExpense = Intent(this, FinancialActivityRecordExpense::class.java)
            recordExpense.putExtras(bundle)
            context.startActivity(recordExpense)
        }

        binding.btnDoneSpending.setOnClickListener {
            showConfirmDialog()
        }

        val adapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(SpendingCategoriesFragment(),"Categories")
        adapter.addFragment(SpendingHistoryFragment(),"History")

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun showButton(){
        firestore.collection("DecisionMakingActivities").document(decisionMakingActivityID).get().addOnSuccessListener {
            var decisionMakingActivity = it.toObject<DecisionMakingActivities>()
            if (decisionMakingActivity?.status == "In Progress") {
                binding.btnAddExpense.visibility = View.VISIBLE
                binding.btnDoneSpending.visibility = View.VISIBLE
            } else {
                binding.btnAddExpense.visibility = View.GONE
                binding.btnDoneSpending.visibility = View.GONE
            }
        }
    }

    private fun getSpent() {
        firestore.collection("Transactions").whereEqualTo("decisionMakingActivityID", decisionMakingActivityID).get().addOnSuccessListener { transactions ->
            for (document in transactions) {
                var transaction = document.toObject<Transactions>()
                spentAmount += transaction.amount!!.toFloat()
            }
            binding.tvSpending.text = "₱ " + spentAmount
        }
    }

    private fun showConfirmDialog() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_done_spending)
        dialog.window!!.setLayout(900, 700)

        val btnOk = dialog.findViewById<Button>(R.id.btn_ok)
        var btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)


        btnOk.setOnClickListener {
            firestore.collection("DecisionMakingActivities").document(decisionMakingActivityID).update("status", "Completed")
            firestore.collection("FinancialGoals").document(financialGoalID).update("status", "Achieved")
            var goalAchieved = Intent(this, GoalAccomplishedActivity::class.java)
            var bundle = Bundle()
            bundle.putString("financialGoalID", financialGoalID)
            goalAchieved.putExtras(bundle)
            this.startActivity(goalAchieved)
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    class ViewPagerAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm){

        private val mFrgmentList = ArrayList<Fragment>()
        private val mFrgmentTitleList = ArrayList<String>()
        override fun getCount() = mFrgmentList.size
        override fun getItem(position: Int) = mFrgmentList[position]
        override fun getPageTitle(position: Int) = mFrgmentTitleList[position]

        fun addFragment(fragment: Fragment, title:String){
            mFrgmentList.add(fragment)
            mFrgmentTitleList.add(title)
        }
    }
}