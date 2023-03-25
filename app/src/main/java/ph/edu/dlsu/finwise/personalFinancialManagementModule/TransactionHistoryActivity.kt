package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityPfmtransactionHistoryBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.*
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.TransactionHistoryAllFragment
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
    lateinit var isExpense: String
    private var childID = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var user: String
    private var minAmount: String? = null
    private var maxAmount: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private val tabIcons = intArrayOf(
        R.drawable.baseline_receipt_long_24,
        R.drawable.baseline_wallet_24,
        R.drawable.baseline_shopping_cart_checkout_24
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmtransactionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        // Initializes the navbar
        checkIfSort()
        initializeIncomeExpense()
        loadBackButton()
        initializeSort()
    }

    private fun setNavigationBar() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val firestore = Firebase.firestore
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            val user  = it.toObject<Users>()!!
            //current user is a child
            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent
            if (user.userType == "Child") {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)
            } else  if (user.userType == "Parent"){
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                //sends the ChildID to the parent navbar
                val bundle = Bundle()
                val childID = bundle.getString("childID").toString()
                val bundleNavBar = Bundle()
                bundleNavBar.putString("childID", childID)
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance, bundleNavBar)
            }
        }
    }

    private fun checkIfSort() {
        getBundle = intent.extras
        setBundle = Bundle()

        if (getBundle != null) {
            getBundle()
            setBundle()
            setNavigationBar()
        }
    }

    private fun setBundle() {
        setBundle!!.putString("minAmount", minAmount)
        setBundle!!.putString("maxAmount", maxAmount)
        setBundle!!.putSerializable("startDate", startDate)
        setBundle!!.putSerializable("endDate", endDate)
        setBundle!!.putString("checkedBoxes", checkedBoxes)
        setBundle!!.putString("childID", childID)
    }

    private fun getBundle() {
        minAmount = getBundle?.getFloat("minAmount").toString()
        maxAmount = getBundle?.getFloat("maxAmount").toString()
        startDate = getBundle?.getSerializable("startDate").toString()
        endDate = getBundle?.getSerializable("endDate").toString()
        checkedBoxes = getBundle?.getString("checkedBoxes").toString()
        isExpense = getBundle?.getString("isExpense").toString()
        user = getBundle?.getString("user").toString()
        //Toast.makeText(this, ""+user, Toast.LENGTH_SHORT).show()
        if (user == "parent") {
            childID = getBundle?.getString("childID").toString()
        }
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

                val allFragment = TransactionHistoryAllFragment()
                val incomeFragment = TransactionHistoryIncomeFragment()
                val expenseFragment = TransactionHistoryExpenseFragment()
                allFragment.arguments = setBundle
                incomeFragment.arguments = setBundle
                expenseFragment.arguments = setBundle
                adapter.addFragment(allFragment, "All")
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
        if (isExpense == "yes")
            binding.viewPager.currentItem = 2

        when (checkedBoxes) {
            "both" -> binding.viewPager.currentItem = 0
            "income" -> binding.viewPager.currentItem = 1
            "expense" -> binding.viewPager.currentItem = 2
        }


    }

    private fun setupTabIcons() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
        binding.tabLayout.getTabAt(2)?.setIcon(tabIcons[2])

    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
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


