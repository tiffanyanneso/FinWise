package ph.edu.dlsu.finwise.child

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.FinancialActivity
import ph.edu.dlsu.finwise.GoalConfirmationActivity
import ph.edu.dlsu.finwise.databinding.ActivityChildNewGoalBinding
import java.text.SimpleDateFormat
import java.util.*


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


        binding.btnNext.setOnClickListener {
            var goToGoalConfirmation = Intent(context, GoalConfirmationActivity::class.java)
            var bundle = Bundle()

            var goalName =  binding.etGoal.text.toString()
            var activity = binding.spinnerActivity.selectedItem.toString()
            var amount = binding.etAmount.text.toString().toFloat()
            var targetDate =(binding.etTargetDate.month + 1).toString() + "/" +
                    (binding.etTargetDate.dayOfMonth).toString() + "/" + (binding.etTargetDate.year).toString()
            bundle.putString("goalName", goalName)
            bundle.putString("activity", activity)
            bundle.putFloat("amount", amount)
            bundle.putString("targetDate", targetDate)

            //TODO: reset spinner and date to default value
            binding.etGoal.text.clear()
            binding.etAmount.text.clear()

            goToGoalConfirmation.putExtras(bundle)
            goToGoalConfirmation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(goToGoalConfirmation)
        }

        binding.btnCancel.setOnClickListener {
            var goToGoalList = Intent(context, FinancialActivity::class.java)
            context.startActivity(goToGoalList)
        }
    }
}