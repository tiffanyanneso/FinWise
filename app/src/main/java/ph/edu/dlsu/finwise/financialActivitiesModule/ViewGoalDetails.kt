package ph.edu.dlsu.finwise.financialActivitiesModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalBinding
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalDetailsBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ViewGoalDetails : AppCompatActivity() {

    private lateinit var binding:ActivityViewGoalDetailsBinding
    private var firestore = Firebase.firestore

    private lateinit var goalID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewGoalDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        goalID = bundle.getString("goalID").toString()
        getGoal()
    }

    private fun getGoal() {
        if (goalID != null) {
            firestore.collection("FinancialGoals").document(goalID!!).get().addOnSuccessListener { document ->
                if (document != null) {
                    //TODO: compute remaining days
                    var goal = document.toObject(FinancialGoals::class.java)
                    //binding.tvMyGoals.text = goal?.goalName.toString()
                    binding.tvGoalAmount.text = "₱ " + DecimalFormat("#,##0.00").format(goal?.targetAmount)
                    binding.tvActivity.text = goal?.financialActivity.toString()

                    //convert timestamp to string date
                    val formatter = SimpleDateFormat("MM/dd/yyyy")
                    val dateCreated = formatter.format(goal?.dateCreated?.toDate())
                    binding.tvDateSet.text = dateCreated.toString()

                    val targetDate = formatter.format(goal?.targetDate?.toDate())
                    binding.tvTargetDate.text = targetDate.toString()
                    binding.tvStatus.text = goal?.status.toString()
                    binding.tvIsForChild.text = goal?.goalIsForSelf.toString()
                }
            }
        }
    }
}