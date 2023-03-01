package ph.edu.dlsu.finwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.edu.dlsu.finwise.databinding.ActivityFinancialBinding
import ph.edu.dlsu.finwise.databinding.ActivitySpendingTransactionsBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.*

class SpendingTransactionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpendingTransactionsBinding

    private val tabIcons = intArrayOf(
        ph.edu.dlsu.finwise.R.drawable.baseline_directions_run_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_shopping_cart_checkout_24
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpendingTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                val adapter = ViewPagerAdapter(supportFragmentManager)
                adapter.addFragment(ShoppingListFragment(),"Shopping List")
                adapter.addFragment(SpendingExpensesFragment(),"Expenses")

                binding.viewPager.adapter = adapter
                binding.tabLayout.setupWithViewPager(binding.viewPager)
                setupTabIcons()
            }
        }
    }

    private fun setupTabIcons() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
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