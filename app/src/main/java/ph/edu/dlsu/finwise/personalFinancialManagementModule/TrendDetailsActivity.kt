package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.PFMAdapter
import ph.edu.dlsu.finwise.databinding.ActivityTrendDetailsBinding
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.ExpenseFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.IncomeFragment
import java.util.*

class TrendDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrendDetailsBinding
    private var setBundle = Bundle()
    private var selectedDateRange = "week"

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
        setBundle.putString("date", selectedDateRange)
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
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)
        loadBackButton()
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToPFM = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goToPFM)
        }
    }

}