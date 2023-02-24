package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlinx.coroutines.*
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.databinding.ActivityPfmtransactionHistoryBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.*
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.TransactionHistoryExpenseFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.TransactionHistoryIncomeFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.TransactionSortFragment
import java.util.*


class TransactionHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPfmtransactionHistoryBinding
    private lateinit var context: Context
    private var getBundle: Bundle? = null
    private var setBundle: Bundle? = null
    private var checkedBoxes = "default"
    private var minAmount: String? = null
    private var maxAmount: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private val tabIcons = intArrayOf(
        ph.edu.dlsu.finwise.R.drawable.baseline_wallet_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_shopping_cart_checkout_24
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmtransactionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        // Initializes the navbar
        Navbar(findViewById(ph.edu.dlsu.finwise.R.id.bottom_nav), this, ph.edu.dlsu.finwise.R.id.nav_finance)
        checkIfSort()
        initializeIncomeExpense()
        loadBackButton()
        initializeSort()
    }

    private fun checkIfSort() {
        getBundle = intent.extras
        setBundle = intent.extras

        if (getBundle != null) {
            getBundle()
            setBundle()
        }
    }

    private fun setBundle() {
        setBundle!!.putString("minAmount", minAmount)
        setBundle!!.putString("maxAmount", maxAmount)
        setBundle!!.putSerializable("startDate", startDate)
        setBundle!!.putSerializable("endDate", endDate)
        setBundle!!.putString("checkedBoxes", checkedBoxes)
    }

    private fun getBundle() {
        minAmount = getBundle!!.getFloat("minAmount").toString()
        maxAmount = getBundle!!.getFloat("maxAmount").toString()
        startDate = getBundle!!.getSerializable("startDate").toString()
        endDate = getBundle!!.getSerializable("endDate").toString()
        checkedBoxes = getBundle!!.getString("checkedBoxes").toString()
    }


    private fun initializeSort() {
        binding.ivSort.setOnClickListener {
            val activity = context as FragmentActivity
            val fm: FragmentManager = activity.supportFragmentManager
            val dialogFragment = TransactionSortFragment()
            dialogFragment.show(fm, "fragment_alert")
        }
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun initializeIncomeExpense() {
        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                val adapter = ViewPagerAdapter(supportFragmentManager)

                val incomeFragment = TransactionHistoryIncomeFragment()
                val expenseFragment = TransactionHistoryExpenseFragment()
                incomeFragment.arguments = setBundle
                expenseFragment.arguments = setBundle
                adapter.addFragment(incomeFragment, "Income")
                adapter.addFragment(expenseFragment, "Expense")

                binding.viewPager.adapter = adapter
                binding.tabLayout.setupWithViewPager(binding.viewPager)
                setupTabIcons()
                redirectToFilteredTab()
            }
        }
    }

    private fun redirectToFilteredTab() {
        if (checkedBoxes == "expense")
            binding.viewPager.currentItem = 1
    }

    private fun setupTabIcons() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[1])

    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToPFM = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goToPFM)
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


