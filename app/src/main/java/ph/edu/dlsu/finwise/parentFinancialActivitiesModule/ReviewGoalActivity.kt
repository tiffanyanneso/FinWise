package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityReviewGoalBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
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
        financialGoalID = bundle.getString("goalID").toString()
        childID = bundle.getString("childID").toString()

        getGoalDetails()

        binding.btnSubmit.setOnClickListener {
            submitGoalRating()
        }
    }

    private fun getGoalDetails() {
        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var financialGoal = it.toObject<FinancialGoals>()
            binding.tvGoal.text = financialGoal?.goalName
            binding.tvActivity.text = financialGoal?.financialActivity
            binding.tvAmount.text = financialGoal?.targetAmount.toString()
            binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(financialGoal?.targetDate?.toDate())
        }
    }

    private fun submitGoalRating() {
        var comment = "None"
        if (binding.etComment.text.toString() != "")
            comment = binding.etComment.text.toString()

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
            "overallRating" to binding.tvOverallRating.text.toString(),
            "lastUpdated" to Timestamp.now()
        )

        firestore.collection("GoalRating").add(rating).addOnSuccessListener {
            var status = binding.spinnerGoalStatus.selectedItem
            if (status == "Approved")
                status = "In Progress"
            firestore.collection("FinancialGoals").document(financialGoalID).update("status", status).addOnSuccessListener {
                Toast.makeText(this, "Rating saved", Toast.LENGTH_SHORT).show()
                var viewGoal = Intent (this, ParentGoalActivity::class.java)
                var bundle = Bundle()
                bundle.putString("childID", childID)
                viewGoal.putExtras(bundle)
                this.startActivity(viewGoal)
            }

        }
    }
}