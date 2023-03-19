package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityReviewGoalBinding
import ph.edu.dlsu.finwise.databinding.DialogSmartGoalCriteriaParentBinding
import ph.edu.dlsu.finwise.databinding.DialogSmartGoalInfoBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ReviewGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewGoalBinding
    private var firestore = Firebase.firestore

    private lateinit var financialGoalID:String

    private lateinit var childID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        childID = bundle.getString("childID").toString()

        getGoalDetails()
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal)

        // for the dropdown
        val items = resources.getStringArray(R.array.goal_status_list)
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        binding.dropdownStatus.setAdapter(adapter)

        binding.btnSubmit.setOnClickListener {
            submitGoalRating()
        }

        binding.ratingBarSpecific.setOnRatingBarChangeListener { ratingBar, rating, fromUser -> updateOverallRating() }
        binding.ratingBarMeasurable.setOnRatingBarChangeListener { ratingBar, rating, fromUser -> updateOverallRating() }
        binding.ratingBarAchievable.setOnRatingBarChangeListener { ratingBar, rating, fromUser -> updateOverallRating() }
        binding.ratingBarRelevant.setOnRatingBarChangeListener { ratingBar, rating, fromUser -> updateOverallRating() }
        binding.ratingBarTimeBound.setOnRatingBarChangeListener { ratingBar, rating, fromUser -> updateOverallRating() }

            binding.imgInfo.setOnClickListener{
                showGoalDialog()
            }
    }

    private fun getGoalDetails() {
        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var financialGoal = it.toObject<FinancialGoals>()
            binding.tvGoal.text = financialGoal?.goalName
            binding.tvActivity.text = financialGoal?.financialActivity
            binding.tvAmount.text =  "₱ "  + DecimalFormat("#,##0.00").format(financialGoal?.targetAmount)
            binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(financialGoal?.targetDate?.toDate())
            if (financialGoal?.goalIsForSelf ==  true)
                binding.tvIsForChild.text = "Yes"
            else
                binding.tvIsForChild.text = "No"
        }
    }

    private fun updateOverallRating() {
        var overall = 0.00F
        var specific = binding.ratingBarSpecific.rating
        var measurable = binding.ratingBarMeasurable.rating
        var achievable = binding.ratingBarAchievable.rating
        var relevant = binding.ratingBarRelevant.rating
        var timeBound = binding.ratingBarTimeBound.rating

        overall = (specific + measurable + achievable + relevant + timeBound) /5
        binding.tvOverallRating.text = "$overall / 5.0"
    }

    private fun submitGoalRating() {
        var comment = "None"
        if (binding.etComment.text.toString() != "")
            comment = binding.etComment.text.toString()

        var overall = 0.00F
        var specific = binding.ratingBarSpecific.rating
        var measurable = binding.ratingBarMeasurable.rating
        var achievable = binding.ratingBarAchievable.rating
        var relevant = binding.ratingBarRelevant.rating
        var timeBound = binding.ratingBarTimeBound.rating

        overall = (specific + measurable + achievable + relevant + timeBound) /5

        var rating = hashMapOf(
            "parentID" to FirebaseAuth.getInstance().currentUser!!.uid,
            "childID" to childID,
            "financialGoalID" to financialGoalID,
            "specific" to binding.ratingBarSpecific.rating,
            "measurable" to binding.ratingBarMeasurable.rating,
            "achievable" to binding.ratingBarAchievable.rating,
            "relevant" to binding.ratingBarRelevant.rating ,
            "timeBound" to binding.ratingBarTimeBound.rating,
            "comment" to comment,
            "overallRating" to overall,
            "lastUpdated" to Timestamp.now()
        )

        firestore.collection("GoalRating").add(rating).addOnSuccessListener {
            var status = binding.dropdownStatus.text.toString()
            if (status == "Approved")
                status = "In Progress"

            var activityStatus = "In Progress"
            if (status == "For Editing" || status == "Disapproved")
                activityStatus = "Locked"



            firestore.collection("FinancialGoals").document(financialGoalID).update("status", status).addOnSuccessListener {
                firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", financialGoalID).whereEqualTo("financialActivityName", "Saving").get().addOnSuccessListener { result ->
                    var finactID = result.documents[0].id
                    firestore.collection("FinancialActivities").document(finactID).update("status", activityStatus)
                }
                Toast.makeText(this, "Rating saved", Toast.LENGTH_SHORT).show()
                var viewGoal = Intent(this, ParentGoalActivity::class.java)
                var bundle = Bundle()
                bundle.putString("childID", childID)
                viewGoal.putExtras(bundle)
                this.startActivity(viewGoal)
            }

        }
    }

    private fun showGoalDialog() {

        var dialogBinding= DialogSmartGoalCriteriaParentBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(1000, 1700)

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}