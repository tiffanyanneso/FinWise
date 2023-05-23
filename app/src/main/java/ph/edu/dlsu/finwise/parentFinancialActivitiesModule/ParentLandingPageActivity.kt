package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import ph.edu.dlsu.finwise.adapter.EarningReviewNotifAdapter
import ph.edu.dlsu.finwise.adapter.EarningToDoAdapter
import ph.edu.dlsu.finwise.adapter.GoalToReviewNotificationAdapter
import ph.edu.dlsu.finwise.adapter.ParentChildrenAdapter
import ph.edu.dlsu.finwise.databinding.ActivityParentLandingPageBinding
import ph.edu.dlsu.finwise.databinding.DialogNotifEarningToReviewParentBinding
import ph.edu.dlsu.finwise.databinding.DialogNotifNewEarningActivitiesBinding
import ph.edu.dlsu.finwise.databinding.DialogNotifNewGoalToRateParentBinding
import ph.edu.dlsu.finwise.loginRegisterModule.ParentRegisterChildActivity
import ph.edu.dlsu.finwise.model.EarningActivityModel
import ph.edu.dlsu.finwise.model.SettingsModel
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users

class ParentLandingPageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityParentLandingPageBinding
    private lateinit var context: Context

    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var lastLogin:Timestamp

    private lateinit var childAdapter:ParentChildrenAdapter

    private var childIDArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this

        CoroutineScope(Dispatchers.Main).launch {
            loadChildren()
            checkNotification()
            updateLastLogin()
        }

        binding.btnAddChild.setOnClickListener {
            val goToChildRegister = Intent(this, ParentRegisterChildActivity::class.java)
            startActivity(goToChildRegister)
        }

        binding.topAppBar.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent (this, MainActivity::class.java)
                    startActivity (intent)
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
        }
    }

    private suspend fun newGoals() {
        var newGoals = ArrayList<String>()

        for (childID in childIDArrayList) {
            var goals = firestore.collection("FinancialGoals").whereEqualTo("childID", childID).whereEqualTo("status", "For Review").get().await()
            for (goal in goals)
                newGoals.add(goal.id)
        }

        if (!newGoals.isEmpty()) {
            var dialogBinding = DialogNotifNewGoalToRateParentBinding.inflate(layoutInflater)
            var dialog = Dialog(this)
            dialog.setContentView(dialogBinding.root)
            dialog.window!!.setLayout(1100, 2000)

            var goalsToReviewAdapter = GoalToReviewNotificationAdapter(this, newGoals)
            dialogBinding.rvGoals.adapter = goalsToReviewAdapter
            dialogBinding.rvGoals.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            goalsToReviewAdapter.notifyDataSetChanged()

            dialogBinding.btnFinact.setOnClickListener {
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

    private suspend fun earningActivities() {
        var earningActivitiesArrayList = ArrayList<String>()
        for (childID in childIDArrayList) {
            var earnings = firestore.collection("EarningActivities").whereEqualTo("childID", childID).whereEqualTo("status", "Pending").get().await()
            for (earning in earnings) {
                var dateCompleted = earning.toObject<EarningActivityModel>().dateCompleted!!.toDate()
                if(dateCompleted.after(lastLogin.toDate()))
                    earningActivitiesArrayList.add(earning.id)
            }
        }

        if (!earningActivitiesArrayList.isEmpty()) {
            var dialogBinding = DialogNotifEarningToReviewParentBinding.inflate(layoutInflater)
            var dialog = Dialog(this)
            dialog.setContentView(dialogBinding.root)
            dialog.window!!.setLayout(1100, 2000)

            var earningReviewAdapter = EarningReviewNotifAdapter(this, earningActivitiesArrayList)
            dialogBinding.rvEarning.adapter = earningReviewAdapter
            dialogBinding.rvEarning.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            earningReviewAdapter.notifyDataSetChanged()

            dialogBinding.btnEarning.setOnClickListener {
                var intent = Intent(this, ParentPendingForReviewActivity::class.java)
                var bundle = Bundle()
                bundle.putString("view", "earning")
                intent.putExtras(bundle)
                startActivity(intent)
                dialog.dismiss()
            }

            dialogBinding.btnClose.setOnClickListener { dialog.dismiss() }

            dialog.show()
        }

    }

    private suspend fun overThreshold() {
        var exceedArrayList = ArrayList<String>()
        for (childID in childIDArrayList) {
            var settings = firestore.collection("Settings").whereEqualTo("childID", childID).whereEqualTo("parentID", currentUser).get().await()
            if (!settings.isEmpty) {
                var settingsObject = settings.documents[0].toObject<SettingsModel>()
                firestore.collection("Transactions").whereEqualTo("userID", childID).whereEqualTo("transactionType", "Expense").get().addOnSuccessListener { transactions ->
                    if (!transactions.isEmpty) {
                        for (transaction in transactions) {
                            var transactionObject = transaction.toObject<Transactions>()
                            if (settingsObject?.alertAmount != 0.00F && (transactionObject.amount!! >= settingsObject?.alertAmount!!))
                                exceedArrayList.add(transaction.id)
                        }
                    }
                }
            }
        }

        println("print exceed " + exceedArrayList.size)
        for (transaction in exceedArrayList)
            println("print id " + transaction)
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