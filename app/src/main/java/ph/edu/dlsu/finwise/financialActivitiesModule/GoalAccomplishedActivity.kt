package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityGoalAccomplishedBinding
import ph.edu.dlsu.finwise.databinding.DialogProceedNextActivityBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class GoalAccomplishedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalAccomplishedBinding
    private var firestore = Firebase.firestore

    private lateinit var  financialGoalID:String
    private lateinit var savingActivityID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalAccomplishedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var bundle: Bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()

        setText()

        var sendBundle = Bundle()
        sendBundle.putString("financialGoalID", financialGoalID)
        sendBundle.putString("savingActivityID", savingActivityID)

        /*binding.btnProceedNextActivity.setOnClickListener {
            firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", financialGoalID).get().addOnSuccessListener { results ->
                var budgetActivityID= ""
                for (activity in results) {
                    var activityObject = activity.toObject<FinancialActivities>()
                    if (activityObject.financialActivityName == "Budgeting")
                        budgetActivityID = activity.id
                }
                sendBundle.putString("budgetActivityID", budgetActivityID)
                var budgetActivity = Intent(this, BudgetActivity::class.java)
                budgetActivity.putExtras(sendBundle)
                this.startActivity(budgetActivity)
            }
        }*/

        binding.btnFinish.setOnClickListener {
            var dialogBinding = DialogProceedNextActivityBinding.inflate(getLayoutInflater())
            var dialog = Dialog(this);
            dialog.setContentView(dialogBinding.getRoot())
            dialog.window!!.setLayout(900, 800)

            dialog.show()
            dialogBinding.btnProceed.setOnClickListener {
                firestore.collection("FinancialActivities")
                    .whereEqualTo("financialGoalID", financialGoalID).get()
                    .addOnSuccessListener { results ->
                        var budgetActivityID = ""
                        for (activity in results) {
                            var activityObject = activity.toObject<FinancialActivities>()
                            if (activityObject.financialActivityName == "Budgeting")
                                budgetActivityID = activity.id
                        }
                        firestore.collection("FinancialActivities").document(budgetActivityID).update("status", "In Progress")
                        sendBundle.putString("budgetActivityID", budgetActivityID)
                        var budgetActivity = Intent(this, BudgetActivity::class.java)
                        budgetActivity.putExtras(sendBundle)
                        this.startActivity(budgetActivity)
                    }
            }

            dialogBinding.btnNo.setOnClickListener {
                //TODO: UPDATE BALANCE OF CHILD
                var pfm = Intent(this, PersonalFinancialManagementActivity::class.java)
                pfm.putExtras(sendBundle)
                this.startActivity(pfm)
            }
        }
    }

    private fun setText () {
        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var financialGoal = it.toObject<FinancialGoals>()
            binding.tvGoal.text = financialGoal?.goalName
            binding.tvActivity.text = financialGoal?.financialActivity
            binding.tvAmount.text = "₱ " + DecimalFormat("#,##0.00").format(financialGoal?.targetAmount)

            // convert timestamp to date string
            val formatter = SimpleDateFormat("MM/dd/yyyy")
            val date = formatter.format(financialGoal?.targetDate?.toDate())
            binding.tvTargetDate.text = date.toString()
        }
    }
}