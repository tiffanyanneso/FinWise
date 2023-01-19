package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.child.ChildEditGoal
import ph.edu.dlsu.finwise.databinding.ActivityStartGoalBinding
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalBinding
import ph.edu.dlsu.finwise.model.FinancialGoals

class ViewGoalActivity : AppCompatActivity() {

    private lateinit var binding : ActivityViewGoalBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this

        var bundle: Bundle = intent.extras!!
        var goalID = bundle.getString("goalID")
        println (goalID)


        if (goalID != null) {
            firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener { document ->
                if (document != null) {
                    //TODO: compute remaining days
                    Toast.makeText(this, goalID, Toast.LENGTH_SHORT).show()
                    var goal = document.toObject(FinancialGoals::class.java)
                    binding.tvMyGoals.text = goal?.goalName.toString()
                    binding.tvGoal.text = goal?.currentAmount.toString() + "/" + goal?.targetAmount.toString()
                    binding.tvActivity.text = goal?.financialActivity.toString()
                    binding.tvDateSet.text = goal?.dateCreated.toString()
                    binding.tvTargetDate.text = goal?.targetDate.toString()
                    binding.tvStatus.text = goal?.status.toString()
                }
            }
        }

        binding.btnEditGoal.setOnClickListener {
            var goToEditGoal = Intent(context, ChildEditGoal::class.java)
            goToEditGoal.putExtra("goalID", bundle)
            context.startActivity(goToEditGoal)
        }
    }
}