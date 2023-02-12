package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.TransactionHistoryExpenseFragment
import ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments.TransactionHistoryIncomeFragment
import ph.edu.dlsu.finwise.adapter.TransactionsAdapter
import ph.edu.dlsu.finwise.databinding.ActivityPfmtransactionHistoryBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childGoalFragment.*


class TransactionHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPfmtransactionHistoryBinding
    private lateinit var context: Context
    private lateinit var transactionAdapter: TransactionsAdapter
    private var firestore = Firebase.firestore
    private var transactionIDArrayList = ArrayList<String>()
    private lateinit var type: String


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmtransactionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        loadBackButton()

        // Initializes the navbar
        Navbar(findViewById(ph.edu.dlsu.finwise.R.id.bottom_nav), this, ph.edu.dlsu.finwise.R.id.nav_finance)

       // transactionIDArrayList.clear()

        //        getAllTransactions()
//        sortTransactions()

        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                val adapter = ViewPagerAdapter(supportFragmentManager)

                adapter.addFragment(TransactionHistoryIncomeFragment(), "Income")
                adapter.addFragment(TransactionHistoryExpenseFragment(), "Expense")

                binding.viewPager.adapter = adapter
                binding.tabLayout.setupWithViewPager(binding.viewPager)
            }
        }
    }

    private fun loadBackButton() {
       /* binding.topA33333ppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topA33333ppBar.setNavigationOnClickListener {
            payWithPayMayaClient.startSinglePaymentActivityForResult(this, buildSinglePaymentRequest())
            *//*val goToPFM = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goToPFM)*//*
        }*/
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