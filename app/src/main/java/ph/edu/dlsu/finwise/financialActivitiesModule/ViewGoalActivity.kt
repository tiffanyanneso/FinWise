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

    private var totalDecisionActivities =0
    private var completedDecisionActivities =0


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
                    //binding.tvGoal.text = "₱ " + goal?.currentAmount.toString() + " / ₱ " + goal?.targetAmount.toString()
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
            var index =0
            for (decisionActivitySnapshot in documents) {

                //check how many decision making activities has already been completed
                var decisionActivityObject = decisionActivitySnapshot.toObject<DecisionMakingActivities>()
                if (decisionActivityObject.status == "In Progress" || decisionActivityObject.status == "Completed")
                    completedDecisionActivities++

                //check the priority of the decision making activity
                if (index == 0)
                    decisionMakingActivitiesArray.add(decisionActivitySnapshot.id)
                if (index == 1) {
                    if (decisionActivityObject.priority == 1)
                        decisionMakingActivitiesArray.add(0, decisionActivitySnapshot.id)
                    else if (decisionActivityObject.priority == 2 || decisionActivityObject.priority == 2)
                        decisionMakingActivitiesArray.add(1, decisionActivitySnapshot.id)
                }
                if (index == 2) {
                    if (decisionActivityObject.priority == 1)
                        decisionMakingActivitiesArray.add(0, decisionActivitySnapshot.id)
                    else if (decisionActivityObject.priority == 2)
                        decisionMakingActivitiesArray.add(1, decisionActivitySnapshot.id)
                    else if (decisionActivityObject.priority == 3)
                        decisionMakingActivitiesArray.add(2, decisionActivitySnapshot.id)
                }
                index++
            }

            totalDecisionActivities = decisionMakingActivitiesArray.size
            binding.tvGoal.text = completedDecisionActivities.toString() + " / " + totalDecisionActivities.toString()

            binding.rvViewDecisionMakingActivities.setLayoutManager(LinearLayoutManager(applicationContext))
            decisionMakingActivitiesAdapater = GoalDecisionMakingActivitiesAdapter(applicationContext, decisionMakingActivitiesArray, goalID)
            binding.rvViewDecisionMakingActivities.setAdapter(decisionMakingActivitiesAdapater)
        }
    }

}