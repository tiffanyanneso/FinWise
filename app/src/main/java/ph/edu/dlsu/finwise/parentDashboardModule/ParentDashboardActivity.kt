package ph.edu.dlsu.finwise.parentDashboardModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.FrameMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.MainActivity
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.EarningReviewNotifAdapter
import ph.edu.dlsu.finwise.adapter.GoalToReviewNotificationAdapter
import ph.edu.dlsu.finwise.adapter.ParentChildrenAdapter
import ph.edu.dlsu.finwise.adapter.TransactionNotifAdapter
import ph.edu.dlsu.finwise.databinding.ActivityParentDashboardBinding
import ph.edu.dlsu.finwise.databinding.ActivityParentLandingPageBinding
import ph.edu.dlsu.finwise.databinding.DialogNotifSummaryParentBinding
import ph.edu.dlsu.finwise.loginRegisterModule.ParentRegisterChildActivity
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.OverThresholdExpenseModel
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentPendingForReviewActivity
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.parentPendingFragments.ParentPendingGoalFragment


class ParentDashboardActivity : AppCompatActivity(){
    private lateinit var binding: ActivityParentDashboardBinding
    private var firestore = Firebase.firestore

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var lastLogin:Timestamp

    private lateinit var childAdapter:ParentChildrenAdapter

    private var childIDArrayList = ArrayList<String>()

    private var goalsArrayList = ArrayList<String>()
    private var earningArrayList = ArrayList<String>()
    private var transactionArrayList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.topAppBar.setOnMenuItemClickListener{ menuItem ->
//            when (menuItem.itemId) {
//                R.id.btn_logout -> {
//                    FirebaseAuth.getInstance().signOut()
//                    val intent = Intent (this, MainActivity::class.java)
//                    startActivity (intent)
//                    finish()
//                    true
//                }
//                else -> false
//            }
        }

        binding.btnAddChild.setOnClickListener {
            val goToChildRegister = Intent(this, ParentRegisterChildActivity::class.java)
            startActivity(goToChildRegister)
        }

        binding.layoutPendingEarning.setOnClickListener {
            var intent = Intent(this, ParentPendingForReviewActivity::class.java)
            var bundle = Bundle()
            bundle.putString("view", "earning")
            intent.putExtras(bundle)
            startActivity(intent)
        }

        binding.layoutPendingGoals.setOnClickListener {
            var intent = Intent(this, ParentPendingForReviewActivity::class.java)
            var bundle = Bundle()
            bundle.putString("view", "goal")
            intent.putExtras(bundle)
            startActivity(intent)
        }

        binding.layoutOverLimit.setOnClickListener {
            var intent = Intent(this, ParentPendingForReviewActivity::class.java)
            var bundle = Bundle()
            bundle.putString("view", "transaction")
            intent.putExtras(bundle)
            startActivity(intent)
        }

        CoroutineScope(Dispatchers.Main).launch {
            loadChildren()
            checkNotification()
            updateLastLogin()
        }
    }

    private suspend fun checkNotification() {
        CoroutineScope(Dispatchers.Main).launch {
            newGoals()
            earningActivities()
            overThreshold()
            loadDialogAndRecyclerView()
        }
    }

    private suspend fun newGoals() {
        for (childID in childIDArrayList) {
            var goals = firestore.collection("FinancialGoals").whereEqualTo("childID", childID)
                .whereEqualTo("status", "For Review").get().await()
            for (goal in goals)
                goalsArrayList.add(goal.id)
        }
        binding.tvNumberPendingGoals.text = goalsArrayList.size.toString()
    }

    private suspend fun earningActivities() {
        for (childID in childIDArrayList) {
            var earnings = firestore.collection("EarningActivities").whereEqualTo("childID", childID)
                .whereEqualTo("status", "Pending").get().await()
            for (earning in earnings) {
                var dateCompleted = earning.toObject<EarningActivityModel>().dateCompleted!!.toDate()
                if (dateCompleted.after(lastLogin.toDate()))
                    earningArrayList.add(earning.id)
            }
        }
        binding.tvNumberPendingEarning.text = earningArrayList.size.toString()
    }


    private suspend fun overThreshold() {
        for (childID in childIDArrayList) {
            var transactions = firestore.collection("OverTransactions").whereEqualTo("childID", childID).get().await()
            for (transaction in transactions) {
                var transactionObject = transaction.toObject<OverThresholdExpenseModel>()
                if (transactionObject.dateRecorded!!.toDate().after(lastLogin.toDate()))
                    transactionArrayList.add(transactionObject.transactionID!!)
            }
        }
        binding.tvNumberOverLimit.text = transactionArrayList.size.toString()
    }

    private fun loadDialogAndRecyclerView() {
        if (!goalsArrayList.isEmpty() || !earningArrayList.isEmpty() || !transactionArrayList.isEmpty()) {
            var dialogBinding = DialogNotifSummaryParentBinding.inflate(layoutInflater)
            var dialog = Dialog(this)
            dialog.setContentView(dialogBinding.root)
            dialog.window!!.setLayout(1100, 2000)

            if (!goalsArrayList.isEmpty()) {
                var goalsToReviewAdapter = GoalToReviewNotificationAdapter(this, goalsArrayList)
                dialogBinding.rvGoals.adapter = goalsToReviewAdapter
                dialogBinding.rvGoals.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                goalsToReviewAdapter.notifyDataSetChanged()
            } else
                dialogBinding.layoutGoal.visibility = View.GONE

            if (!earningArrayList.isEmpty()) {
                var earningReviewAdapter = EarningReviewNotifAdapter(this, earningArrayList)
                dialogBinding.rvEarning.adapter = earningReviewAdapter
                dialogBinding.rvEarning.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                earningReviewAdapter.notifyDataSetChanged()
            } else
                dialogBinding.layoutEarning.visibility = View.GONE

            if (!transactionArrayList.isEmpty()) {
                var transactionsAdapter = TransactionNotifAdapter(this, transactionArrayList)
                dialogBinding.rvTransactions.adapter = transactionsAdapter
                dialogBinding.rvTransactions.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                transactionsAdapter.notifyDataSetChanged()
            } else
                dialogBinding.layoutTransactions.visibility = View.GONE

            dialogBinding.btnViewAll.setOnClickListener {
                var intent = Intent(this, ParentPendingForReviewActivity::class.java)
                var bundle = Bundle()
                bundle.putString("view", "goal")
                intent.putExtras(bundle)
                startActivity(intent)
                dialog.dismiss()
            }

            dialogBinding.btnClose.setOnClickListener { dialog.dismiss() }

            dialog.show()
        }

        binding.layoutLoading.visibility = View.GONE
        binding.layoutDashboard.visibility = View.VISIBLE
    }

    class ChildFilter (var childID:String, var childFirstName:String)
    private suspend fun loadChildren() {
        var childFilterArrayList = ArrayList<ChildFilter>()
        val adapter = ViewPagerAdapter(supportFragmentManager)


        lastLogin = firestore.collection("Users").document(currentUser).get().await().toObject<Users>()!!.lastLogin!!
        var children =  firestore.collection("Users").whereEqualTo("userType", "Child").whereEqualTo("parentID", currentUser).get().await()
        if (children.size()!=0) {
            for (child in children)
                childFilterArrayList.add(ChildFilter(child.id, child.toObject<Users>().firstName!!))

            //setting fragment to tabs
            binding.viewPager.adapter = adapter
            binding.tabLayout.setupWithViewPager(binding.viewPager)
            adapter.notifyDataSetChanged()

             var filtered = childFilterArrayList.sortedBy { it.childFirstName }
            for (child in filtered) {
                childIDArrayList.add(child.childID)
                //initializing each fragment
                var fragmentBundle = Bundle()
                fragmentBundle.putString("childID", child.childID)
                var childFragment = ParentDashboardFragment()
                childFragment.arguments = fragmentBundle
                adapter.addFragment(childFragment, child.childFirstName)
                adapter.notifyDataSetChanged()
            }

            childAdapter = ParentChildrenAdapter(this, childIDArrayList)
            binding.rvViewChildren.adapter = childAdapter
            binding.rvViewChildren.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    class ViewPagerAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm){

        private val mFrgmentList = java.util.ArrayList<Fragment>()
        private val mFrgmentTitleList = java.util.ArrayList<String>()
        override fun getCount() = mFrgmentList.size
        override fun getItem(position: Int) = mFrgmentList[position]
        override fun getPageTitle(position: Int) = mFrgmentTitleList[position]

        fun addFragment(fragment: Fragment, title:String){
            mFrgmentList.add(fragment)
            mFrgmentTitleList.add(title)
        }
    }

    private fun updateLastLogin() {
        firestore.collection("Users").document(currentUser).update("lastLogin", Timestamp.now())
    }

}