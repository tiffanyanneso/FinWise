package ph.edu.dlsu.finwise.financialActivitiesModule

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivitySpendingBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.spendingExpenseFragments.SpendingExpenseListFragment
import ph.edu.dlsu.finwise.financialActivitiesModule.spendingExpenseFragments.SpendingShoppingListFragment
import ph.edu.dlsu.finwise.model.BudgetItem
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

class SpendingActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySpendingBinding
    private var firestore = Firebase.firestore

    //private lateinit var budgetCategoryID: String
    private lateinit var bundle:Bundle

    private var spent = 0.00F
    private var totalBudgetCategory = 0.00F
    private var remainingBudget = 0.00F

    private lateinit var budgetingActivityID:String
    private lateinit var budgetItemID:String

    private lateinit var savingActivityID:String
    private lateinit var spendingActivityID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpendingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        bundle = intent.extras!!
        savingActivityID = bundle.getString("savingActivityID").toString()
        budgetItemID = bundle.getString("budgetItemID").toString()
        budgetingActivityID = bundle.getString("budgetingActivityID").toString()
        spendingActivityID = bundle.getString("spendingActivityID").toString()

        //checkUser()
        bundle.putString("source", "viewGoal")
        getInfo()
        initializeFragments()


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
            var budgetItem = it.toObject<BudgetItem>()
            binding.tvBudgetItemName.text = budgetItem?.budgetItemName
            //binding.tvCategoryAmount.text = "??? " + DecimalFormat("#,##0.00").format(budgetItem?.amount)
        }.continueWith { getAvailableBudget() }
    }


    @SuppressLint("ResourceAsColor")
    private fun getAvailableBudget() {
        firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
            var budgetCategory = it.toObject<BudgetItem>()
            var categoryAmount = budgetCategory?.amount
            remainingBudget = categoryAmount!!
            binding.tvCategoryAmount.text = "??? " + DecimalFormat("###0.00").format(categoryAmount).toString()

            firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID).get().addOnSuccessListener { results ->
                for (expense in results) {
                    var expenseObject = expense.toObject<Transactions>()
                    spent += expenseObject.amount!!.toFloat()
                }
            }.continueWith {
                remainingBudget = categoryAmount?.minus(spent)!!
                if (remainingBudget!! >= 0.00F ) {
                    binding.tvTitle.text = "Remaining Budget"
                    binding.tvTitle.setTextColor(getResources().getColor(R.color.white))
                    binding.tvCategoryAmount.text = "??? " + DecimalFormat("###0.00").format(remainingBudget).toString()
                    binding.tvCategoryAmount.setTextColor(getResources().getColor(R.color.white))
                    binding.layoutAmount.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_green))
                } else {
                    binding.tvTitle.text = "Amount spent over the budget"
                    binding.tvTitle.setTextColor(getResources().getColor(R.color.black))
                    binding.tvCategoryAmount.text = "??? " + DecimalFormat("###0.00").format(remainingBudget.absoluteValue).toString()
                    binding.tvCategoryAmount.setTextColor(getResources().getColor(R.color.black))
                    binding.layoutAmount.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_red))
                }
            }
        }.continueWith { initializeFragments() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeFragments() {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        var fragmentBundle = Bundle()
        fragmentBundle.putString("savingActivityID", savingActivityID)
        fragmentBundle.putString("budgetingActivityID", budgetingActivityID)
        fragmentBundle.putString("budgetItemID", budgetItemID)
        fragmentBundle.putString("spendingActivityID", spendingActivityID)
        fragmentBundle.putFloat("remainingBudget", remainingBudget)


        var childID = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(childID).get().addOnSuccessListener {
            var child = it.toObject<Users>()
            //compute age
            val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var age = difference.years
            if (age  > 9 ) {
                var spendingShoppingListFragment = SpendingShoppingListFragment()
                spendingShoppingListFragment.arguments = fragmentBundle
                adapter.addFragment(spendingShoppingListFragment,"Shopping List")
            }

            var spendingExpenseFragment = SpendingExpenseListFragment()
            spendingExpenseFragment.arguments = fragmentBundle
            adapter.addFragment(spendingExpenseFragment,"Expenses")
            binding.viewPager.adapter = adapter
            binding.tabLayout.setupWithViewPager(binding.viewPager)
            adapter.notifyDataSetChanged()

        }
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