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
import ph.edu.dlsu.finwise.databinding.ActivityParentSettingAgoalBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity
import ph.edu.dlsu.finwise.model.FinancialGoals
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

        binding.btnReviewGoal.setOnClickListener {
            var goToReviewGoal = Intent(this, ReviewGoalActivity::class.java)
            var bundle = Bundle()

            bundle.putString("financialGoalID", financialGoalID)
            bundle.putString("childID", childID)

            goToReviewGoal.putExtras(bundle)
            goToReviewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToReviewGoal)
        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {

            val goToParentGoalActivity = Intent(applicationContext, ParentGoalActivity::class.java)
            var bundle = Bundle()
            bundle.putString("childID", childID)
            this.startActivity(goToParentGoalActivity)
        }
    }

    private fun setInfo() {
        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var goal = it.toObject<FinancialGoals>()
            binding.tvGoalName.text = goal?.goalName
            binding.tvActivity.text = goal?.financialActivity
            binding.tvAmount.text = DecimalFormat("#,###.00").format(goal?.targetAmount)
            binding.tvTargetDate.text = SimpleDateFormat("MM/dd/yyyy").format(goal?.targetDate!!.toDate())
            if (goal.goalIsForSelf ==  true)
                binding.tvIsForChild.text = "Yes"
            else
                binding.tvIsForChild.text = "No"

        }
    }

    private fun checkUser() {
        firestore.collection("ParentUser").document(currentUser).get().addOnSuccessListener {
            //user is parent
            if (it.exists())
                binding.btnReviewGoal.visibility = View.VISIBLE
            else
                binding.btnReviewGoal.visibility = View.GONE
        }
    }

}