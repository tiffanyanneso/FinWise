package ph.edu.dlsu.finwise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialBinding
import ph.edu.dlsu.finwise.databinding.ActivityGoalTransactionsBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.BudgetExpenseActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ChildNewGoal
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.ViewGoalActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.childGoalFragment.*

class GoalTransactionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalTransactionsBinding
    private lateinit var financialGoalID:String
    private lateinit var source:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        //checkSettings()

        var bundle: Bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        financialGoalID = bundle.getString("source").toString()

        adapter.addFragment(GoalDepositFragment(),"Deposit")
        adapter.addFragment(GoalExpenseFragment(),"Expense")

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        // todo get previous page, add setting a budget page
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            if (source == "viewGoal") {
                val goToGoal = Intent(applicationContext, ViewGoalActivity::class.java)
                goToGoal.putExtras(bundle)
                goToGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(goToGoal)
            }
            else if (source == "budgetItem") {
                val goToBudget = Intent(applicationContext, BudgetExpenseActivity::class.java) 
                goToBudget.putExtras(bundle)
                goToBudget.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(goToBudget)
            }
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