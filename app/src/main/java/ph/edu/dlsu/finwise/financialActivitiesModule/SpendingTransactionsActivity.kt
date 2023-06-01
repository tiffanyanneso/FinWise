package ph.edu.dlsu.finwise.financialActivitiesModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.SpendingExpenseAdapter
import ph.edu.dlsu.finwise.databinding.ActivitySpendingTransactionsBinding
import ph.edu.dlsu.finwise.model.Transactions

class SpendingTransactionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpendingTransactionsBinding

    private lateinit var spendingActivityID:String
    private lateinit var expenseAdapter:SpendingExpenseAdapter


    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpendingTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializes the navbar
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_profile)

        var bundle = intent.extras!!
        spendingActivityID = bundle.getString("spendingActivityID").toString()

        var expenses = ArrayList<Transactions>()

        firestore.collection("Transactions").whereEqualTo("financialActivityID", spendingActivityID).get().addOnSuccessListener {

            expenses.addAll(it.toObjects<Transactions>())

            expenseAdapter = SpendingExpenseAdapter(this, expenses)
            binding.rvExpenses.adapter = expenseAdapter
            binding.rvExpenses.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            expenseAdapter.notifyDataSetChanged()
            binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources,
                R.drawable.baseline_arrow_back_24, null)
        }

    }

//    private val tabIcons = intArrayOf(
//        R.drawable.baseline_directions_run_24,
//        R.drawable.baseline_shopping_cart_checkout_24
//    )
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySpendingTransactionsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        GlobalScope.launch(Dispatchers.IO) {
//            withContext(Dispatchers.Main) {
//                val adapter = ViewPagerAdapter(supportFragmentManager)
//                //adapter.addFragment(ShoppingListFragment(),"Shopping List")
//                adapter.addFragment(SpendingExpensesFragment(),"Expenses")
//
//                binding.viewPager.adapter = adapter
//                binding.tabLayout.setupWithViewPager(binding.viewPager)
//                setupTabIcons()
//            }
//        }
//    }
//
//    private fun setupTabIcons() {
//        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
//        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
//    }
//    class ViewPagerAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm){
//
//        private val mFrgmentList = ArrayList<Fragment>()
//        private val mFrgmentTitleList = ArrayList<String>()
//        override fun getCount() = mFrgmentList.size
//        override fun getItem(position: Int) = mFrgmentList[position]
//        override fun getPageTitle(position: Int) = mFrgmentTitleList[position]
//
//        fun addFragment(fragment: Fragment, title:String){
//            mFrgmentList.add(fragment)
//            mFrgmentTitleList.add(title)
//        }
//    }
}