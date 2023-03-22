package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityParentSettingAgoalBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.financialActivitiesModule.UpdateGoalActivity
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ParentSettingAGoalActivity : AppCompatActivity() {

    private lateinit var binding : ActivityParentSettingAgoalBinding

    private var firestore = Firebase.firestore

    private lateinit var financialGoalID:String
    private lateinit var childID:String

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentSettingAgoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        childID = bundle.getString("childID").toString()

        checkUser()
        setInfo()
        setNavigationBar()

        binding.btnReviewGoal.setOnClickListener {
            var goToReviewGoal = Intent(this, ReviewGoalActivity::class.java)
            var bundle = Bundle()

            bundle.putString("financialGoalID", financialGoalID)
            bundle.putString("childID", childID)

            goToReviewGoal.putExtras(bundle)
            goToReviewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToReviewGoal)
        }

        binding.btnEditGoal.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("financialGoalID", financialGoalID)

            var updatedGoal = Intent(this, UpdateGoalActivity::class.java)
            updatedGoal.putExtras(bundle)
            startActivity(updatedGoal)
        }

        binding.topAppBar.setNavigationOnClickListener {

            // Determine the source of the user
            val source = intent.getStringExtra("source")

            // Create a bundle with any data you want to pass to the next activity
            val bundle = Bundle()
            bundle.putString("childID", childID)

            // Depending on the source, navigate to the appropriate activity
            when (source) {
                "Child" -> {
                    val intent = Intent(this, FinancialActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
                "Parent" -> {
                    val intent = Intent(this, ParentGoalActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
                // Add additional cases for other sources
                else -> {
                    // If the source is unknown or not specified, just finish the current activity
                    finish()
                }
            }
        }
    }

    private fun setInfo() {
        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var goal = it.toObject<FinancialGoals>()
            binding.tvGoalName.text = goal?.goalName
            binding.tvActivity.text = goal?.financialActivity
            binding.tvAmount.text =  "₱ "  + DecimalFormat("#,##0.00").format(goal?.targetAmount)
            binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(goal?.targetDate!!.toDate())
            if (goal.goalIsForSelf ==  true)
                binding.tvIsForChild.text = "Yes"
            else
                binding.tvIsForChild.text = "No"
            binding.tvStatus.text = goal?.status

        }
    }

    private fun checkUser() {
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            //user is parent
            if (it.toObject<Users>()!!.userType == "Parent") {
                binding.btnReviewGoal.visibility = View.VISIBLE
                binding.btnEditGoal.visibility = View.GONE
            }
            else if (it.toObject<Users>()!!.userType == "Child") {
                binding.btnReviewGoal.visibility = View.GONE
                binding.btnEditGoal.visibility = View.VISIBLE
            }
        }
    }

    private fun setNavigationBar() {
        var navUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(navUser).get().addOnSuccessListener {

            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent

            if (it.toObject<Users>()!!.userType == "Parent") {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal)
            } else if (it.toObject<Users>()!!.userType == "Child") {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
            }
        }
    }

}