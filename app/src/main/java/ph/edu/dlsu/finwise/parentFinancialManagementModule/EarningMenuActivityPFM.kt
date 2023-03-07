package ph.edu.dlsu.finwise.parentFinancialManagementModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.databinding.ActivityEarningMenuBinding

class EarningMenuActivityPFM : AppCompatActivity() {
    private lateinit var binding: ActivityEarningMenuBinding

    private lateinit var savingActivityID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        var childID = bundle.getString("childID").toString()


        var sendBundle = Bundle()
        sendBundle.putString("childID", childID)

        binding.btnHomeRewards.setOnClickListener {
            var goToHomeRewardsActivity = Intent(this, EarningActivityPFM::class.java)
            goToHomeRewardsActivity.putExtras(sendBundle)
            startActivity(goToHomeRewardsActivity)
        }

        binding.btnSelling.setOnClickListener {
            var goToSellingActivity = Intent(this, EarningSellingPFMActivity::class.java)
            goToSellingActivity.putExtras(sendBundle)
            startActivity(goToSellingActivity)
        }
    }
}