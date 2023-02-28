package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.BudgetExpenseAdapter
import ph.edu.dlsu.finwise.databinding.ActivityBudgetExpenseBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.budgetExpenseFragments.BudgetExpenseFragment
import ph.edu.dlsu.finwise.financialActivitiesModule.budgetExpenseFragments.BudgetShoppingListFragment
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.GoalSettingFragment
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.SavingFragment
import ph.edu.dlsu.finwise.model.BudgetExpense
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.FinancialActivities
import java.text.DecimalFormat
import kotlin.collections.ArrayList

class BudgetExpenseActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBudgetExpenseBinding
    private var firestore = Firebase.firestore
    private lateinit var budgetExpenseAdapter: BudgetExpenseAdapter

    //private lateinit var budgetCategoryID: String
    private lateinit var bundle:Bundle

    private var spent = 0.00F
    private var totalBudgetCategory = 0.00F
    private var remainingBudget = 0.00F

    private lateinit var budgetActivityID:String
    private lateinit var budgetItemID:String

    private lateinit var spendingActivityID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        bundle = intent.extras!!
        budgetActivityID = bundle.getString("budgetActivityID").toString()
        budgetItemID = bundle.getString("budgetItemID").toString()
        bundle.putString("source", "viewGoal")
        spendingActivityID = bundle.getString("spendingActivityID").toString()
        getInfo()


        //checkUser()

//        binding.btnRecordExpense.setOnClickListener {
//            var recordExpense = Intent (this, FinancialActivityRecordExpense::class.java)
//            var bundle = Bundle()
//            bundle.putString("budgetActivityID", budgetActivityID)
//            bundle.putString("budgetItemID", budgetItemID)
//            bundle.putFloat("remainingBudget", remainingBudget)
//            recordExpense.putExtras(bundle)
//            this.startActivity(recordExpense)
//        }
//
//        binding.tvViewAll.setOnClickListener {
//            var goToGoalTransactions = Intent(this, GoalTransactionsActivity::class.java)
//            goToGoalTransactions.putExtras(bundle)
//            this.startActivity(goToGoalTransactions)
//        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToBudgetActivity = Intent(applicationContext, BudgetActivity::class.java)

//            var bundle = Bundle()
//            bundle.putString("financialGoalID", binding.tv)
//            bundle.putString("budgetItemID", budgetItemID)
//            bundle.putFloat("remainingBudget", remainingBudget)

//            goToBudgetActivity.putExtras(bundle)
//            goToBudgetActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToBudgetActivity)
        }
    }

    private fun getInfo() {
        firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
            var budgetItem  = it.toObject<BudgetItem>()
            binding.tvBudgetItemName.text = budgetItem?.budgetItemName
            //binding.tvCategoryAmount.text = "₱ " + DecimalFormat("#,##0.00").format(budgetItem?.amount)
        }.continueWith { getAvailableBudget() }
    }


    private fun getAvailableBudget() {
        firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
            var budgetCategory = it.toObject<BudgetItem>()
            var categoryAmount = budgetCategory?.amount
            remainingBudget = categoryAmount!!
            binding.tvCategoryAmount.text = "₱ " + DecimalFormat("###0.00").format(categoryAmount).toString()

            firestore.collection("BudgetExpenses").whereEqualTo("budgetCategoryID", budgetItemID)
                .get().addOnSuccessListener { results ->
                for (expense in results) {
                    var expenseObject = expense.toObject<BudgetExpense>()
                    spent += expenseObject.amount!!.toFloat()
                }
            }.continueWith {
                    remainingBudget = categoryAmount?.minus(spent)!!
                    binding.tvCategoryAmount.text = "₱ " + DecimalFormat("###0.00").format(remainingBudget).toString()
            }
        }.continueWith { initializeFragments() }
    }

    private fun initializeFragments() {
        var fragmentBundle = Bundle()
        fragmentBundle.putString("budgetActivityID", budgetActivityID)
        fragmentBundle.putString("budgetItemID", budgetItemID)
        //fragmentBundle.putString("budgetCategoryID", budgetCategoryID)
        fragmentBundle.putString("spendingActivityID", spendingActivityID)
        fragmentBundle.putFloat("remainingBudget", remainingBudget)
        val adapter = FinancialActivity.ViewPagerAdapter(supportFragmentManager)

        var shoppingListFragment = BudgetShoppingListFragment()
        shoppingListFragment.arguments = fragmentBundle

        var budgetExpenseFragment = BudgetExpenseFragment()
        budgetExpenseFragment.arguments = fragmentBundle

        adapter.addFragment(shoppingListFragment,"Shopping List")
        adapter.addFragment(budgetExpenseFragment,"Expenses")

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
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