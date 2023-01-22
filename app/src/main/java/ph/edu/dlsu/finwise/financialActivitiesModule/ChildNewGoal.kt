package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.PersonalFinancialManagementActivity
import ph.edu.dlsu.finwise.databinding.ActivityChildNewGoalBinding
import ph.edu.dlsu.finwise.model.DecisionMakingActivities


class ChildNewGoal : AppCompatActivity() {

    private lateinit var binding : ActivityChildNewGoalBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChildNewGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        //TODO: how to bring back values to edit text fields
        /*var bundle: Bundle = intent.extras!!
        if (bundle!=null) {
            binding.etGoal.text = bundle.getString("goalName")
            binding.tvActivity.text = bundle.getString("activity")
            binding.etAmount.text = bundle.getString("amount")
            binding.etTargetDate.text = bundle.getString("targetDate")
        }*/


        /*if (currentUserType == "Child") {
            binding.tvFinancialDecisionMakingActivity.visibility = View.GONE
            binding.checkBoxes.visibility = View.GONE
        } else if (currentUserType == "Parent") {
            binding.tvFinancialDecisionMakingActivity.visibility = View.VISIBLE
            binding.checkBoxes.visibility = View.VISIBLE
        }*/




        binding.btnNext.setOnClickListener {
            var goToGoalConfirmation = Intent(context, GoalConfirmationActivity::class.java)
            var bundle = Bundle()

            var goalName =  binding.etGoal.text.toString()
            var activity = binding.spinnerActivity.selectedItem.toString()
            var amount = binding.etAmount.text.toString().toFloat()
            var targetDate =(binding.etTargetDate.month + 1).toString() + "/" +
                    (binding.etTargetDate.dayOfMonth).toString() + "/" + (binding.etTargetDate.year).toString()

            //see which decision making activities were selected
            var decisionMakingActivities = ArrayList<String>()

            if (binding.cbBudgeting.isChecked)
                decisionMakingActivities.add("Setting a Budget")
            if (binding.cbSaving.isChecked)
                decisionMakingActivities.add("Deciding to Save")
            if (binding.cbSpending.isChecked)
                decisionMakingActivities.add("Deciding to Spend")

            println("new goal " + decisionMakingActivities)

            bundle.putString("goalName", goalName)
            bundle.putString("activity", activity)
            bundle.putFloat("amount", amount)
            bundle.putString("targetDate", targetDate)
            bundle.putStringArrayList("decisionActivities", decisionMakingActivities)

            //TODO: reset spinner and date to default value
            binding.etGoal.text.clear()
            binding.etAmount.text.clear()

            goToGoalConfirmation.putExtras(bundle)
            goToGoalConfirmation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(goToGoalConfirmation)
        }

        binding.btnCancel.setOnClickListener {
            val goToPFM = Intent(this, PersonalFinancialManagementActivity::class.java)
            startActivity(goToPFM)
        }
    }
}