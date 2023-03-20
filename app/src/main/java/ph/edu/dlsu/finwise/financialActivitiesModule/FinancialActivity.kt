package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
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
import ph.edu.dlsu.finwise.*
import ph.edu.dlsu.finwise.adapter.FinactSavingAdapter
import ph.edu.dlsu.finwise.adapter.NearingDeadlineAdapter
import ph.edu.dlsu.finwise.databinding.ActivityFinancialBinding
import ph.edu.dlsu.finwise.databinding.DialogNearingDeadlineBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.childActivitiesFragment.*
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class FinancialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialBinding
    private var firestore = Firebase.firestore


    private var childID  = FirebaseAuth.getInstance().currentUser!!.uid

    //used to check if goal setting fragment should be included
    private var setOwnGoals = false
    private var autoApprove = false

    private lateinit var nearingDeadlineAdapter: NearingDeadlineAdapter

    private val tabIcons = intArrayOf(
        ph.edu.dlsu.finwise.R.drawable.baseline_star_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_wallet_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_pie_chart_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_shopping_cart_checkout_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_check_24,
        ph.edu.dlsu.finwise.R.drawable.baseline_do_not_disturb_24,
    )


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore.collection("Users").document(childID).get().addOnSuccessListener {
            var lastLogin = it.toObject<Users>()!!.lastLogin!!.toDate()
            var lastShown = it.toObject<Users>()!!.lastShown!!.toDate()
            val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val lastShownFormat =  SimpleDateFormat("MM/dd/yyyy").format(lastShown)
            val from = LocalDate.parse(lastShownFormat.toString(), dateFormatter)
            val lastLoginFormat =  SimpleDateFormat("MM/dd/yyyy").format(lastLogin)
            val to = LocalDate.parse(lastLoginFormat.toString(), dateFormatter)
            var difference = Period.between(from, to)

            if(difference.days >= 1)
                nearDeadlineDialog()

        }

        checkAge()

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun nearDeadlineDialog() {
        firestore.collection("Users").document(childID).update("lastShown", Timestamp.now())
        var nearingDeadlineGoalIDArrayList = ArrayList<String>()

        firestore.collection("FinancialGoals").whereEqualTo("childID", childID).whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            for (goal in results) {
                var goalObject = goal.toObject<FinancialGoals>()
                val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
                val from = LocalDate.now()
                val date =  SimpleDateFormat("MM/dd/yyyy").format(goalObject.targetDate?.toDate())
                val to = LocalDate.parse(date.toString(), dateFormatter)
                var difference = Period.between(from, to)

                if (goalObject.goalLength == "Short") {
                    if (difference.days <= 2)
                        nearingDeadlineGoalIDArrayList.add(goal.id)
                } else if (goalObject.goalLength == "Medium") {
                    if (difference.days <= 5)
                        nearingDeadlineGoalIDArrayList.add(goal.id)
                } else if (goalObject.goalLength == "Long") {
                    if (difference.days <= 5)
                        nearingDeadlineGoalIDArrayList.add(goal.id)
                }
            }

            if (nearingDeadlineGoalIDArrayList.size > 0) {
                var dialogBinding = DialogNearingDeadlineBinding.inflate(layoutInflater)
                var dialog = Dialog(this)
                dialog.setContentView(dialogBinding.root)
                dialog.window!!.setLayout(900, 1400)
                dialogBinding.btnOk.setOnClickListener { dialog.dismiss() }
                nearingDeadlineAdapter = NearingDeadlineAdapter(this, nearingDeadlineGoalIDArrayList)
                dialogBinding.rvNearDeadline.adapter = nearingDeadlineAdapter
                dialogBinding.rvNearDeadline.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                nearingDeadlineAdapter.notifyDataSetChanged()
                dialog.show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAge() {
        firestore.collection("Users").document(childID).get().addOnSuccessListener {
            var child = it.toObject<Users>()
            //compute age
            val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var age = difference.years
            if (age == 10 || age == 11)
                setOwnGoals = true
            if (age == 12) {
                setOwnGoals = true
                autoApprove = true
                updateForReviewGoalStatus()
            }

        }.continueWith { initializeFragments() }
    }


    private fun initializeFragments() {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        if (setOwnGoals && !autoApprove)
            adapter.addFragment(GoalSettingFragment(),"Goal Setting")

        adapter.addFragment(SavingFragment(),"Saving")
        adapter.addFragment(BudgetingFragment(),"Budgeting")
        adapter.addFragment(SpendingFragment(),"Spending")
        adapter.addFragment(AchievedFragment(),"Achieved")
        adapter.addFragment(DisapprovedFragment(),"Disapproved")

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        setupTabIcons()
    }

    private fun updateForReviewGoalStatus() {
        firestore.collection("FinancialGoals").whereEqualTo("childID", childID).whereEqualTo("status", "For Review").get().addOnSuccessListener { results ->
            for (goal in results) {
                firestore.collection("FinancialGoals").document(goal.id).update("status", "In Progress").continueWith {
                    firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", goal.id).whereEqualTo("financialActivityName", "Saving").get().addOnSuccessListener { financialActivity ->
                        var savingID = financialActivity.documents[0].id
                        firestore.collection("FinancialActivities").document(savingID).update("status", "In Progress")
                    }
                }
            }
        }
    }

    private fun setupTabIcons() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcons[0])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcons[1])
        binding.tabLayout.getTabAt(2)?.setIcon(tabIcons[2])
        binding.tabLayout.getTabAt(3)?.setIcon(tabIcons[3])
        binding.tabLayout.getTabAt(4)?.setIcon(tabIcons[4])
        binding.tabLayout.getTabAt(5)?.setIcon(tabIcons[5])
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