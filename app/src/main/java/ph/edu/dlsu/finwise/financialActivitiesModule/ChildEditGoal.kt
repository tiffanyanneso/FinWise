package ph.edu.dlsu.finwise.financialActivitiesModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityChildNewGoalBinding

class ChildEditGoal : AppCompatActivity() {
    private lateinit var binding : ActivityChildNewGoalBinding
    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChildNewGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getFinancialGoal()

        // for the dropdown
        val items = resources.getStringArray(R.array.financial_activity)
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        binding.dropdownActivity.setAdapter(adapter)

        /*if (currentUserType == "Child") {
            binding.tvFinancialDecisionMakingActivity.visibility = View.GONE
            binding.checkBoxes.visibility = View.GONE
        } else if (currentUserType == "Parent") {
            binding.tvFinancialDecisionMakingActivity.visibility = View.VISIBLE
            binding.checkBoxes.visibility = View.VISIBLE
        }*/


        /*binding.btnSave.setOnClickListener {

        }*/

    }

    private fun getFinancialGoal() {
        //TODO: update when goal list is done
        /*val goal:String = id of goal
        firestore.collection("FinancialGoals").document(goal).get().addOnSuccessListener {
            var goal = it.toObject<Company>()
            if (goal?.goalName != null)
                binding.etGoal.setText(goal?.goalName)
            if (goal?.amount != null)
                binding.etAmount.setText(goal?.amount)
            if (goal?.targetDate != null)
                //change how to set date
                binding.etTargetDate.setText(goal?.about)

            if (goal?.financialActivity != null) {
                var financialActivityArray = getResources().getStringArray(R.array.financial_activity)
                var finActivityIndex: Int = 0

                for (i in financialActivityArray.indices)
                    if (financialActivityArray[i].toString() == goal?.financialActivity.toString())
                        finActivityIndex = i

                binding.spinnerActivity.setSelection(finActivityIndex)
            }
        }*/
    }
}