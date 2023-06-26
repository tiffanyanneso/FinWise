package ph.edu.dlsu.finwise.financialActivitiesModule

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivitySpendingBinding
import ph.edu.dlsu.finwise.databinding.DialogTakeAssessmentBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.spendingExpenseFragments.SpendingExpenseListFragment
import ph.edu.dlsu.finwise.financialActivitiesModule.spendingExpenseFragments.SpendingShoppingListFragment
import ph.edu.dlsu.finwise.financialAssessmentModule.FinancialAssessmentActivity
import ph.edu.dlsu.finwise.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

class SpendingActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySpendingBinding
    private var firestore = Firebase.firestore

    //private lateinit var budgetCategoryID: String
    private lateinit var bundle:Bundle

    private var spent = 0.00F
    private var totalBudgetCategory = 0.00F
    private var remainingBudget = 0.00F

    private lateinit var budgetingActivityID:String
    private lateinit var budgetItemID:String

    private lateinit var savingActivityID:String
    private lateinit var spendingActivityID:String

    private lateinit var childID:String

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpendingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        bundle = intent.extras!!
        savingActivityID = bundle.getString("savingActivityID").toString()
        budgetItemID = bundle.getString("budgetItemID").toString()
        budgetingActivityID = bundle.getString("budgetingActivityID").toString()
        spendingActivityID = bundle.getString("spendingActivityID").toString()
        childID = bundle.getString("childID").toString()

        checkUser()
        bundle.putString("source", "viewGoal")
        getInfo()


        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToBudgetActivity = Intent(applicationContext, BudgetActivity::class.java)

            var bundle = Bundle()
            bundle.putString("savingActivityID", savingActivityID)
            bundle.putString("budgetingActivityID", budgetingActivityID)
            bundle.putString("spendingActivityID", spendingActivityID)
            bundle.putString("childID", childID)

            goToBudgetActivity.putExtras(bundle)
            goToBudgetActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToBudgetActivity)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getInfo() {
        firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
            var budgetItem = it.toObject<BudgetItem>()
            if (budgetItem?.budgetItemName == "Others")
                binding.tvBudgetItemName.text = budgetItem?.budgetItemNameOther
            else
                binding.tvBudgetItemName.text = budgetItem?.budgetItemName
            //binding.tvCategoryAmount.text = "₱ " + DecimalFormat("#,##0.00").format(budgetItem?.amount)
        }.continueWith { getAvailableBudget() }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    private fun getAvailableBudget() {
        firestore.collection("BudgetItems").document(budgetItemID).get().addOnSuccessListener {
            var budgetCategory = it.toObject<BudgetItem>()
            var categoryAmount = budgetCategory?.amount
            remainingBudget = categoryAmount!!
            binding.tvCategoryAmount.text = "₱ " + DecimalFormat("###0.00").format(categoryAmount).toString()

            firestore.collection("Transactions").whereEqualTo("budgetItemID", budgetItemID).get().addOnSuccessListener { results ->
                for (expense in results) {
                    var expenseObject = expense.toObject<Transactions>()
                    spent += expenseObject.amount!!.toFloat()
                }
            }.continueWith {
                remainingBudget = categoryAmount?.minus(spent)!!
                if (remainingBudget!! >= 0.00F ) {
                    binding.tvTitle.text = "Remaining Budget"
                    binding.tvTitle.setTextColor(getResources().getColor(R.color.white))
                    binding.tvCategoryAmount.text = "₱ " + DecimalFormat("###0.00").format(remainingBudget).toString()
                    binding.tvCategoryAmount.setTextColor(getResources().getColor(R.color.white))
                    binding.layoutAmount.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_green))
                } else {
                    binding.tvTitle.text = "Amount spent over the budget"
                    binding.tvTitle.setTextColor(getResources().getColor(R.color.black))
                    binding.tvCategoryAmount.text = "₱ " + DecimalFormat("###0.00").format(remainingBudget.absoluteValue).toString()
                    binding.tvCategoryAmount.setTextColor(getResources().getColor(R.color.black))
                    binding.layoutAmount.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_red))
                }

                initializeFragments()
            }
        }
    }

    private fun checkUser() {
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            if (it.toObject<Users>()!!.userType == "Child") {
                childID = currentUser
                firestore.collection("FinancialActivities").document(spendingActivityID).get().addOnSuccessListener {
                    if (it.toObject<FinancialActivities>()!!.status == "In Progress") {
                        binding.layoutAmount.visibility = View.VISIBLE
                        goToFinancialAssessmentActivity()
                    } else
                        binding.layoutAmount.visibility = View.GONE
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun goToFinancialAssessmentActivity() {
        firestore.collection("Assessments").whereEqualTo("assessmentType", "Pre-Activity").whereEqualTo("assessmentCategory", "Spending").get().addOnSuccessListener {
            if (it.size()!= 0) {
                var assessmentID = it.documents[0].id
                firestore.collection("AssessmentAttempts").whereEqualTo("assessmentID", assessmentID).whereEqualTo("childID", currentUser).orderBy("dateTaken", Query.Direction.DESCENDING).get().addOnSuccessListener { results ->
                    if (results.size() != 0) {
                        var assessmentAttemptsObjects = results.toObjects<FinancialAssessmentAttempts>()
                        var latestAssessmentAttempt = assessmentAttemptsObjects.get(0).dateTaken
                        val dateFormatter: DateTimeFormatter =
                            DateTimeFormatter.ofPattern("MM/dd/yyyy")
                        val lastTakenFormat =
                            SimpleDateFormat("MM/dd/yyyy").format(latestAssessmentAttempt!!.toDate())
                        val from = LocalDate.parse(lastTakenFormat.toString(), dateFormatter)
                        val today = SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate())
                        val to = LocalDate.parse(today.toString(), dateFormatter)
                        var difference = Period.between(from, to)

                        if (difference.days >= 7)
                            buildAssessmentDialog()
                    } else
                        buildAssessmentDialog()
                }
            }
        }
    }

    private fun buildAssessmentDialog() {
        var dialogBinding= DialogTakeAssessmentBinding.inflate(layoutInflater)
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.root)
        dialog.window!!.setLayout(950, 900)
        dialog.setCancelable(false)
        dialogBinding.btnOk.setOnClickListener { goToAssessment() }
        dialog.show()
    }

    private fun goToAssessment() {
        val bundle = Bundle()
        val assessmentType = "Pre-Activity" // Change: Pre-Activity, Post-Activity
        val assessmentCategory = "Spending" // Change: Budgeting, Saving, Spending, Goal Setting
        bundle.putString("assessmentType", assessmentType)
        bundle.putString("assessmentCategory", assessmentCategory)

        val assessmentQuiz = Intent(this, FinancialAssessmentActivity::class.java)
        assessmentQuiz.putExtras(bundle)
        startActivity(assessmentQuiz)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeFragments() {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        var fragmentBundle = Bundle()
        fragmentBundle.putString("savingActivityID", savingActivityID)
        fragmentBundle.putString("budgetingActivityID", budgetingActivityID)
        fragmentBundle.putString("spendingActivityID", spendingActivityID)
        fragmentBundle.putString("budgetItemID", budgetItemID)
        fragmentBundle.putFloat("remainingBudget", remainingBudget)

        firestore.collection("Users").document(childID).get().addOnSuccessListener {
            var user = it.toObject<Users>()
            if (user?.userType == "Child") {
                //compute age
                val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
                val from = LocalDate.now()
                val date =  SimpleDateFormat("MM/dd/yyyy").format(user?.birthday?.toDate())
                val to = LocalDate.parse(date.toString(), dateFormatter)
                var difference = Period.between(to, from)

                var age = difference.years
                if (age  > 9 ) {
                    var spendingShoppingListFragment = SpendingShoppingListFragment()
                    spendingShoppingListFragment.arguments = fragmentBundle
                    adapter.addFragment(spendingShoppingListFragment,"Shopping List")
                }

                var spendingExpenseFragment = SpendingExpenseListFragment()
                spendingExpenseFragment.arguments = fragmentBundle
                adapter.addFragment(spendingExpenseFragment,"Expenses")
                binding.viewPager.adapter = adapter
                binding.tabLayout.setupWithViewPager(binding.viewPager)
                adapter.notifyDataSetChanged()

                binding.mainLayout.visibility = View.VISIBLE
                binding.layoutLoading.visibility = View.GONE
            }
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