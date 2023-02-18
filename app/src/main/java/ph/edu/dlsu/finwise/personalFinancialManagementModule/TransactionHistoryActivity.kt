package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlinx.coroutines.*
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.databinding.ActivityPfmtransactionHistoryBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childGoalFragment.*
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.TransactionHistoryExpenseFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.TransactionHistoryIncomeFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.TransactionSortFragment
import java.util.*
import kotlin.collections.ArrayList


class TransactionHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPfmtransactionHistoryBinding
    private lateinit var context: Context
    var getBundle: Bundle? = null
    var setBundle: Bundle? = null
    private var checkedBoxes: String? = null
    private var minAmount: String? = null
    private var maxAmount: String? = null
    private var startDate: String? = null
    private var endDate: String? = null


    /*  private lateinit var transactionAdapter: TransactionsAdapter
      private var firestore = Firebase.firestore
      private var transactionIDArrayList = ArrayList<String>()
      private lateinit var type: String

      var x2 = 0.0f
      var x1 = 0.0f
      var y2 = 0.0f
      var y1 = 0.0f*/

   /* companion object {
        const val MIN_DISTANCE = 150
    }*/

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmtransactionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        // Initializes the navbar
        Navbar(findViewById(ph.edu.dlsu.finwise.R.id.bottom_nav), this, ph.edu.dlsu.finwise.R.id.nav_finance)
       // gestureDetector = GestureDetector(this, this)
        checkIfSort()
        initializeIncomeExpense()
        loadBackButton()
        initializeSort()
        /*transactionIDArrayList.clear()
        getAllTransactions()
        sortTransactions()*/
    }

    private fun checkIfSort() {
        getBundle = intent.extras
        setBundle = intent.extras

        //TODO: receive data then craete functions to sort
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
            }
        }
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




   /* override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)

        when (event?.action) {
            // When start to swipe
            0 -> {
                x1 = event.x
                y1 = event.y
            }

            // When end swipe
            1 -> {
                x2 = event.x
                y2 = event.y

                val valueX: Float = x2-x1
                val valueY: Float = y2-y1

                if (kotlin.math.abs(valueX) > MIN_DISTANCE) {
                    if (x1 > x2) {
                        Toast.makeText(this, "Right Swipe", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this, "Left Swipe", Toast.LENGTH_SHORT).show()

                    }
                }
            }

        }

        return super.onTouchEvent(event)
    }*/



//    private fun sortTransactions() {
//        val sortSpinner = binding.spinnerSort
//        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parentView: AdapterView<*>?,
//                selectedItemView: View?,
//                position: Int,
//                id: Long
//            ) {
//                type = sortSpinner.selectedItem.toString()
//
//                if (type == "All") {
//                    getAllTransactions()
//                }
//                else {
//                    transactionIDArrayList.clear()
//                    var transactionType = "transactionType"
//                    if (type == "Goal")
//                        transactionType = "category"
//                    Toast.makeText(applicationContext, transactionType+" "+ type, Toast.LENGTH_SHORT).show()
//                    firestore.collection("Transactions").whereEqualTo(transactionType, type)
//                        .orderBy("date", Query.Direction.DESCENDING)
//                        .get().addOnSuccessListener { documents ->
//                            for (transactionSnapshot in documents) {
//                                //creating the object from list retrieved in db
//                                val transactionID = transactionSnapshot.id
//                                transactionIDArrayList.add(transactionID)
//                            }
//                            loadRecyclerView(transactionIDArrayList)
//                        }
//                }
//
//            }
//
//            override fun onNothingSelected(parentView: AdapterView<*>?) {
//                // your code here
//            }
//        }
//    }

//    private fun getAllTransactions() {
//        var allTransactions = ArrayList<String>()
//        //TODO:change to get transactions of current user
//        //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
//        //firestore.collection("Transactions").whereEqualTo("companyID", currentUser).get().addOnSuccessListener{ documents ->
//        firestore.collection("Transactions").orderBy("date", Query.Direction.DESCENDING)
//            .get().addOnSuccessListener { documents ->
//            for (transactionSnapshot in documents) {
//                //creating the object from list retrieved in db
//                val transactionID = transactionSnapshot.id
//                allTransactions.add(transactionID)
//            }
//            loadRecyclerView(allTransactions)
//        }
//    }

//    private fun loadRecyclerView(transactionIDArrayList: ArrayList<String>) {
//        transactionAdapter = TransactionsAdapter(this, transactionIDArrayList)
//        binding.rvViewTransactions.adapter = transactionAdapter
//        binding.rvViewTransactions.layoutManager = LinearLayoutManager(applicationContext,
//            LinearLayoutManager.VERTICAL,
//            false)
//        transactionAdapter.notifyDataSetChanged()
//    }



}


