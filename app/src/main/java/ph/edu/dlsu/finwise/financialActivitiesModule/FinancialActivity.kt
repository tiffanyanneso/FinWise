package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.AchievedFragment
import ph.edu.dlsu.finwise.InProgressFragment
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialBinding
import ph.edu.dlsu.finwise.adapter.ChildGoalAdapter
import ph.edu.dlsu.finwise.databinding.ActivityTestFinancialBinding
import ph.edu.dlsu.finwise.model.GoalSettings

class FinancialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialBinding
//    private lateinit var goalAdapter: ChildGoalAdapter
//    private var goalIDArrayList = ArrayList<String>()
//    private lateinit var status: String
    private var firestore = Firebase.firestore

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        val adapter = ViewPagerAdapter(supportFragmentManager)
        //checkSettings()

        // TODO: change the fragments added based on parent approval
        adapter.addFragment(InProgressFragment(),"In Progress")
//        adapter.addFragment(GoalForReviewFragment(),"For Review")
//        adapter.addFragment(GoalForEditingFragment(),"For Editing")
//        adapter.addFragment(GoalDisapprovedFragment(),"Disapproved")
        adapter.addFragment(AchievedFragment(),"Achieved")

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.btnNewGoal.setOnClickListener {
            context=this
            var goToNewGoal = Intent(context, ChildNewGoal::class.java)
            context.startActivity(goToNewGoal)
        }

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
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

    private fun checkSettings() {
        var currentChildUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("GoalSettings").whereEqualTo("childID", currentChildUser).get().addOnSuccessListener {
            var goalSettings = it.documents[0].toObject<GoalSettings>()
            //hide button
            if (goalSettings?.setOwnGoal == false)
                binding.btnNewGoal.visibility = View.GONE
            else if (goalSettings?.setOwnGoal == true)
                binding.btnNewGoal.visibility = View.VISIBLE
        }
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityFinancialBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        context = this
//
//        // Hides actionbar,
//        // and initializes the navbar
//        supportActionBar?.hide()
//        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
//        goToNewGoal()
//        getAllGoals()
//        sortTransactions()
//    }
//    private fun sortTransactions() {
//        val sortSpinner = binding.spinnerStatus
//        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parentView: AdapterView<*>?,
//                selectedItemView: View?,
//                position: Int,
//                id: Long
//            ) {
//                status = sortSpinner.selectedItem.toString()
//
//                if (status == "--Status--") {
//                    goalIDArrayList.clear()
//                    getAllGoals()
//                }
//                else {
//                    goalIDArrayList.clear()
//                    firestore.collection("FinancialGoals")
//                        .whereEqualTo("status", status).get().addOnSuccessListener { documents ->
//                            for (goalSnapshot in documents) {
//                                //creating the object from list retrieved in db
//                                val goalID = goalSnapshot.id
//                                goalIDArrayList.add(goalID)
//                            }
//                            loadRecyclerView(goalIDArrayList)
//                        }
//                }
//            }
//
//            override fun onNothingSelected(parentView: AdapterView<*>?) {
//                // your code here
//            }
//        }
//    }
//
//    private fun getAllGoals() {
//
//        //TODO:change to get transactions of current user
//        //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
//        //firestore.collection("Transactions").whereEqualTo("companyID", currentUser).get().addOnSuccessListener{ documents ->
//
//        firestore.collection("FinancialGoals").get().addOnSuccessListener { documents ->
//            for (goalSnapshot in documents) {
//                //creating the object from list retrieved in db
//                val goalID = goalSnapshot.id
//                goalIDArrayList.add(goalID)
//            }
//            loadRecyclerView(goalIDArrayList)
//        }
//    }
//
//    private fun loadRecyclerView(goalIDArrayList: ArrayList<String>) {
//        goalAdapter = ChildGoalAdapter(this, goalIDArrayList)
//        binding.rvViewGoals.adapter = goalAdapter
//        binding.rvViewGoals.layoutManager = LinearLayoutManager(applicationContext,
//            LinearLayoutManager.VERTICAL,
//            false)
//        goalAdapter.notifyDataSetChanged()
//    }
//
//    private fun goToNewGoal() {
//        binding.btnNewGoal.setOnClickListener {
//            val goToNewGoal = Intent(this, ChildNewGoal::class.java)
//            context.startActivity(goToNewGoal)
//        }
//    }

}