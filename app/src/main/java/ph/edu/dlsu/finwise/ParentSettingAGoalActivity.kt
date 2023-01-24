package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.databinding.ActivityParentRegisterBinding
import ph.edu.dlsu.finwise.databinding.ActivityParentSettingAgoalBinding
import ph.edu.dlsu.finwise.databinding.ActivityReviewGoalBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.FinancialActivity

class ParentSettingAGoalActivity : AppCompatActivity() {

    private lateinit var binding : ActivityParentSettingAgoalBinding
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentSettingAgoalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this

        binding.btnReviewGoal.setOnClickListener {
            var goToReviewGoal = Intent(context, FinancialActivity::class.java)
            var bundle = Bundle()

            bundle.putString("goal", binding.tvGoalName.text.toString())
            bundle.putString("actvity", binding.tvActivity.text.toString())
            bundle.putString("amount", binding.tvAmount.text.toString())
            bundle.putString("target_date", binding.tvTargetDate.text.toString())

            goToReviewGoal.putExtras(bundle)
            goToReviewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(goToReviewGoal)
        }
    }
}