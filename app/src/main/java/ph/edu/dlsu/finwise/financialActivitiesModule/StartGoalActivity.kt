package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityStartGoalBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class StartGoalActivity : AppCompatActivity() {

    private lateinit var binding : ActivityStartGoalBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        var bundle: Bundle = intent.extras!!
        var goalID = bundle.getString("goalID")
        if (goalID != null) {
            firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener { document ->
                if (document != null) {
                    var goal = document.toObject(FinancialGoals::class.java)
                    binding.tvGoalName.text = goal?.goalName.toString()
                    binding.tvActivity.text = goal?.financialActivity.toString()
                    binding.tvAmount.text = DecimalFormat("#,##0.00").format(goal?.targetAmount)

                    //Convert timestasmp to date string
                    val formatter = SimpleDateFormat("MM/dd/yyyy")
                    val date = formatter.format(goal?.targetDate?.toDate())
                    binding.tvTargetDate.text = date.toString()
                } else {
                    Toast.makeText(this, "No data", Toast.LENGTH_SHORT)
                }
            }
        }


        //computeDays(bundle.getString("targetDate")!!)

        binding.btnGetStarted.setOnClickListener {
            var goToViewGoal = Intent(context, ViewGoalActivity::class.java)
            goToViewGoal.putExtra("goalID", goalID)
            context.startActivity(goToViewGoal)
            finish()
        }
    }

    private fun computeDays(date:String) {
        //TODO: difference of current date and target date
    }
}