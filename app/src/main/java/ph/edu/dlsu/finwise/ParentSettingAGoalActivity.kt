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

    private lateinit var financialGoalID:String
    private lateinit var childUserID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentSettingAgoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        childUserID = bundle.getString("childUserID").toString()



        binding.btnReviewGoal.setOnClickListener {
            var goToReviewGoal = Intent(context, ReviewGoalActivity::class.java)
            var bundle = Bundle()

            bundle.putString("financialGoalID", financialGoalID)
            bundle.putString("childUserID", childUserID)

            goToReviewGoal.putExtras(bundle)
            goToReviewGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToReviewGoal)
        }
    }
}