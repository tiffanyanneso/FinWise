package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalBinding
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalDetailsBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ViewGoalDetails : AppCompatActivity() {

    private lateinit var binding:ActivityViewGoalDetailsBinding
    private var firestore = Firebase.firestore

    private lateinit var financialGoalID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewGoalDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializes the navbar
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        var bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        getGoal()

        binding.topAppBar.setNavigationOnClickListener {
            val goToGoal = Intent(applicationContext, ViewGoalActivity::class.java)
            goToGoal.putExtras(bundle)
            goToGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToGoal)
        }
    }

    private fun getGoal() {
        firestore.collection("FinancialGoals").document(financialGoalID!!).get().addOnSuccessListener { document ->
            if (document != null) {
                var goal = document.toObject(FinancialGoals::class.java)
                //binding.tvMyGoals.text = goal?.goalName.toString()
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

            }
        }
    }
}