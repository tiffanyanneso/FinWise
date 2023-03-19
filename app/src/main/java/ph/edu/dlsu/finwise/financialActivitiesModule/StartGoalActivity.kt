package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityStartGoalBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

class StartGoalActivity : AppCompatActivity() {

    private lateinit var binding : ActivityStartGoalBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context

    @RequiresApi(Build.VERSION_CODES.O)
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
        var financialGoalID = bundle.getString("financialGoalID")
        if (financialGoalID != null) {
            firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener { document ->
                if (document != null) {
                    var goal = document.toObject(FinancialGoals::class.java)
                    binding.tvGoalName.text = goal?.goalName.toString()
                    binding.tvActivity.text = goal?.financialActivity.toString()
                    binding.tvAmount.text = DecimalFormat("#,##0.00").format(goal?.targetAmount)

                    //Convert timestasmp to date string
                    val formatter = SimpleDateFormat("MM/dd/yyyy")
                    val date = formatter.format(goal?.targetDate?.toDate())
                    binding.tvTargetDate.text = date.toString()
                    computeDays(goal?.targetDate?.toDate()!!)
                } else {
                    //Toast.makeText(this, "No data", Toast.LENGTH_SHORT)
                }
            }
        }

        binding.btnGetStarted.setOnClickListener {
            var goToViewGoal = Intent(context, ViewGoalActivity::class.java)
            goToViewGoal.putExtra("financialGoalID", financialGoalID)
            context.startActivity(goToViewGoal)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun computeDays(date: Date) {
        val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val from = LocalDate.now()
        val date =  SimpleDateFormat("MM/dd/yyyy").format(date)
        val to = LocalDate.parse(date.toString(), dateFormatter)

        var difference = Period.between(from, to)
        var differenceDays = ((difference.years * 365) + (difference.months * 30) + difference.days)
        binding.tvNDays.text = differenceDays.toString()
        binding.tvDaysString.text = "You have ${differenceDays} days left to\n complete your goal on time!"
    }
}