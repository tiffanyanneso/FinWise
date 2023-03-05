package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.databinding.ActivityEarningMenuBinding

class EarningMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEarningMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        var childID = bundle.getString("childID").toString()
        var savingActivityID = bundle.getString("savingActivityID").toString()


        var sendBundle = Bundle()
        sendBundle.putString("childID", childID)

        if (bundle.containsKey("savingActivityID"))
            sendBundle.putString("savingActivityID", savingActivityID)
        binding.btnHomeRewards.setOnClickListener {
            var goToHomeRewardsActivity = Intent(this, EarningActivity::class.java)
            goToHomeRewardsActivity.putExtras(sendBundle)
            startActivity(goToHomeRewardsActivity)
        }

        binding.btnSelling.setOnClickListener {
            var goToSellingActivity = Intent(this, EarningSellingActivity::class.java)
            goToSellingActivity.putExtras(sendBundle)
            startActivity(goToSellingActivity)
        }
    }
}