package ph.edu.dlsu.finwise.personalFinancialManagementModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.PFMAdapter
import ph.edu.dlsu.finwise.databinding.ActivityTrendDetailsBinding
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.ExpenseFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.IncomeFragment

class TrendDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrendDetailsBinding
    private var setBundle = Bundle()
    private var selectedDateRange = "week"
    private var user = "child"
    private var childID = "child"

    private val tabIcons1 = intArrayOf(
        R.drawable.baseline_wallet_24,
        R.drawable.baseline_shopping_cart_checkout_24
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrendDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAndSetBundleFromBalanceFragment()
        setUpBreakdownTabs()
        loadButtons()
    }

    private fun getAndSetBundleFromBalanceFragment() {
        val getBundle = intent.extras
        selectedDateRange = getBundle?.getString("date").toString()
        user = getBundle?.getString("user").toString()
        childID = getBundle?.getString("childID").toString()
        setBundle.putString("date", selectedDateRange)
        setBundle.putString("user", user)
        setBundle.putString("childID", childID)
    }

    private fun setupTabIcons1() {
        binding.tabs.getTabAt(0)?.setIcon(tabIcons1[0])
        binding.tabs.getTabAt(1)?.setIcon(tabIcons1[1])
    }

     private fun setUpBreakdownTabs() {
         val adapter = PFMAdapter(supportFragmentManager)
         val incomeFragment = IncomeFragment()
         val expenseFragment = ExpenseFragment()
         incomeFragment.arguments = setBundle
         expenseFragment.arguments = setBundle
         adapter.addFragment(incomeFragment, "Income")
         //TODO: Update expense + ui
         adapter.addFragment(expenseFragment, "Expense")
         binding.viewPager.adapter = adapter
         binding.tabs.setupWithViewPager(binding.viewPager)

         binding.tabs.getTabAt(0)?.text = "Income"
         binding.tabs.getTabAt(1)?.text = "Expense"
         setupTabIcons1()
     }

    private fun loadButtons() {
        supportActionBar?.hide()
        setNavigationBar()
        loadBackButton()
    }

    private fun setNavigationBar() {
        val bottomNavigationViewChild = binding.bottomNav
        val bottomNavigationViewParent = binding.bottomNavParent

        if (user == "child") {
            bottomNavigationViewChild.visibility = View.VISIBLE
            bottomNavigationViewParent.visibility = View.GONE
            Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)
        } else {
            bottomNavigationViewChild.visibility = View.GONE
            bottomNavigationViewParent.visibility = View.VISIBLE
            NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance)
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
            /*val goToPFM = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goToPFM)*/
        }
    }

}