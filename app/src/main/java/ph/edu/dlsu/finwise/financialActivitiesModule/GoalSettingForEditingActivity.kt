package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityGoalSettingForEditingBinding
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalDetailsBinding
import ph.edu.dlsu.finwise.databinding.DialogDeleteGoalWarningBinding
import ph.edu.dlsu.finwise.databinding.DialogDeleteGoalWarningGoalSettingBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.GoalRating
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class GoalSettingForEditingActivity : AppCompatActivity() {

    private lateinit var binding:ActivityGoalSettingForEditingBinding
    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private lateinit var financialGoalID:String
    private lateinit var savingActivityID:String

    private var cashBalance = 0.00F
    private var mayaBalance = 0.00F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalSettingForEditingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNavigationBar()

        var bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()
        getGoal()
        getParentFeedback()
        checkUser()

        binding.topAppBar.setNavigationOnClickListener {
            val goToGoal = Intent(applicationContext, FinancialActivity::class.java)
            this.startActivity(goToGoal)
        }

        binding.btnDelete.setOnClickListener {
            var dialogBinding= DialogDeleteGoalWarningGoalSettingBinding.inflate(getLayoutInflater())
            var dialog= Dialog(this);
            dialog.setContentView(dialogBinding.getRoot())
            dialog.window!!.setLayout(800, 800)

            dialogBinding.btnOk.setOnClickListener {
                dialog.dismiss()
                deleteGoal()
            }

            dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }

        binding.btnEditGoal.setOnClickListener {
            var editGoal = Intent(this, GoalSettingEditGoalActivity::class.java)
            var sendBundle  = Bundle()
            sendBundle.putString("financialGoalID", financialGoalID)
            sendBundle.putString("savingActivityID", savingActivityID)
            editGoal.putExtras(sendBundle)
            startActivity(editGoal)
        }
    }

    private fun deleteGoal() {
        //mark financial goal as deleted
        firestore.collection("FinancialGoals").document(financialGoalID).update("status", "Deleted", "currentSavings", 0).addOnSuccessListener {
            //mark related activities as deleted
            firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", financialGoalID).get().addOnSuccessListener { results ->
                for (activity in results ) {
                    firestore.collection("FinancialActivities").document(activity.id)
                        .update("status", "Deleted")
                }
                var goalList = Intent(this, FinancialActivity::class.java)
                startActivity(goalList)
            }
        }
    }


    private fun getGoal() {
        firestore.collection("FinancialGoals").document(financialGoalID!!).get().addOnSuccessListener { document ->
            if (document != null) {
                var goal = document.toObject(FinancialGoals::class.java)
                //binding.tvMyGoals.text = goal?.goalName.toString()
                binding.tvGoalName.text = goal?.goalName.toString()
                binding.tvGoalAmount.text = "₱ " + DecimalFormat("#,##0.00").format(goal?.targetAmount?.toFloat())
                binding.tvActivity.text = goal?.financialActivity.toString()

                //convert timestamp to string date
                binding.tvDateSet.text = SimpleDateFormat("MM/dd/yyyy").format(goal?.dateCreated?.toDate()).toString()
                binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(goal?.targetDate?.toDate()).toString()
                binding.tvStatus.text = goal?.status.toString()
                if (goal?.goalIsForSelf == true)
                    binding.tvIsForChild.text = "Yes"
                else
                    binding.tvIsForChild.text = "No"


                if (goal?.status == "For Review")
                    binding.layoutParentFeedback.visibility = View.GONE
                else if (goal?.status == "For Editing")
                    binding.layoutParentFeedback.visibility = View.VISIBLE
            }
        }
    }

    private fun getParentFeedback() {
        firestore.collection("GoalRating").whereEqualTo("financialGoalID", financialGoalID).get().addOnSuccessListener { result ->
            var goalReviewObjects= result.toObjects<GoalRating>()
            goalReviewObjects = goalReviewObjects.sortedByDescending { it.lastUpdated }
            var goalReview = goalReviewObjects[0]
            binding.tvOverallRating.text = "${goalReview?.overallRating} / 5.0"
            binding.ratingBarSpecific.rating = goalReview?.specific!!
            binding.ratingBarMeasurable.rating = goalReview.measurable!!
            binding.ratingBarAchievable.rating = goalReview.achievable!!
            binding.ratingBarRelevant.rating = goalReview.relevant!!
            binding.ratingBarTimeBound.rating = goalReview.timeBound!!
            binding.tvComment.text = goalReview.comment
        }
    }

    private fun checkUser() {
        var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            if (it.toObject<Users>()!!.userType == "Parent")
                binding.btnDelete.visibility = View.GONE
            else if (it.toObject<Users>()!!.userType == "Child")
                binding.btnDelete.visibility = View.VISIBLE
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
                //sends the ChildID to the parent navbar
                val bundle = intent.extras!!
                val childID = bundle.getString("childID").toString()
                val bundleNavBar = Bundle()
                bundleNavBar.putString("childID", childID)
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)
            } else if (it.toObject<Users>()!!.userType == "Child") {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
            }
        }
    }
}