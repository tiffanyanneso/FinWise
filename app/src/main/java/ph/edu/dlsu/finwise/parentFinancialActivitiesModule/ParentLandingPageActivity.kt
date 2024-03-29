package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.MainActivity
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.*
import ph.edu.dlsu.finwise.databinding.ActivityParentLandingPageBinding
import ph.edu.dlsu.finwise.databinding.DialogNotifEarningToReviewParentBinding
import ph.edu.dlsu.finwise.databinding.DialogNotifNewEarningActivitiesBinding
import ph.edu.dlsu.finwise.databinding.DialogNotifNewGoalToRateParentBinding
import ph.edu.dlsu.finwise.databinding.DialogNotifSummaryParentBinding
import ph.edu.dlsu.finwise.loginRegisterModule.ParentRegisterChildActivity
import ph.edu.dlsu.finwise.model.*

class ParentLandingPageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityParentLandingPageBinding
    private lateinit var context: Context

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
        binding = ActivityParentLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        CoroutineScope(Dispatchers.Main).launch {
            loadChildren()
            checkNotification()
            updateLastLogin()
        }

        binding.btnAddChild.setOnClickListener {
            val goToChildRegister = Intent(this, ParentRegisterChildActivity::class.java)
            startActivity(goToChildRegister)
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
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
    }

    private suspend fun loadChildren() {
        lastLogin = firestore.collection("Users").document(currentUser).get().await().toObject<Users>()!!.lastLogin!!
        var children =  firestore.collection("Users").whereEqualTo("userType", "Child").whereEqualTo("parentID", currentUser).get().await()
        if (children.size()!=0) {
            for (child in children)
                childIDArrayList.add(child.id)
            childAdapter = ParentChildrenAdapter(this, childIDArrayList)
            binding.rvViewChildren.adapter = childAdapter
            binding.rvViewChildren.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun updateLastLogin() {
        firestore.collection("Users").document(currentUser).update("lastLogin", Timestamp.now())
    }
}