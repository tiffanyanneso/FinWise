package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.ChildGoalAdapter
import ph.edu.dlsu.finwise.adapter.GoalDecisionMakingActivitiesAdapter
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalBinding
import ph.edu.dlsu.finwise.model.DecisionMakingActivities
import ph.edu.dlsu.finwise.model.FinancialGoals

class ViewGoalActivity : AppCompatActivity() {

    private lateinit var binding : ActivityViewGoalBinding
    private var firestore = Firebase.firestore

    private lateinit var decisionMakingActivitiesAdapater: GoalDecisionMakingActivitiesAdapter

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle: Bundle = intent.extras!!
        var goalID = bundle.getString("goalID")

        if (goalID != null) {
            firestore.collection("FinancialGoals").document(goalID!!).get().addOnSuccessListener { document ->
                if (document != null) {
                    //TODO: compute remaining days
                    var goal = document.toObject(FinancialGoals::class.java)
                    binding.tvMyGoals.text = goal?.goalName.toString()
                    binding.tvGoal.text = goal?.currentAmount.toString() + "/" + goal?.targetAmount.toString()
                    binding.tvActivity.text = goal?.financialActivity.toString()
                    binding.tvDateSet.text = goal?.dateCreated.toString()
                    binding.tvTargetDate.text = goal?.targetDate.toString()
                    binding.tvStatus.text = goal?.status.toString()
                    getDecisionMakingActivities(goalID)
                }
            }
        }

        binding.btnEditGoal.setOnClickListener {
            var goToEditGoal = Intent(context, ChildEditGoal::class.java)
            goToEditGoal.putExtra("goalID", bundle)
            context.startActivity(goToEditGoal)
        }
    }

    private fun getDecisionMakingActivities(goalID:String) {
        firestore.collection("DecisionMakingActivities").whereEqualTo("financialGoalID", goalID).get().addOnSuccessListener { documents ->
            var decisionMakingActivitiesArray = ArrayList<String>()
            for (decisionActivitySnapshot in documents) {
                var decisionActivity = decisionActivitySnapshot.id
                decisionMakingActivitiesArray.add(decisionActivity)
            }

            binding.rvViewDecisionMakingActivities.setLayoutManager(LinearLayoutManager(applicationContext))
            decisionMakingActivitiesAdapater = GoalDecisionMakingActivitiesAdapter(applicationContext, decisionMakingActivitiesArray, goalID)
            binding.rvViewDecisionMakingActivities.setAdapter(decisionMakingActivitiesAdapater)
        }
    }

}