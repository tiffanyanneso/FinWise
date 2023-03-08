package ph.edu.dlsu.finwise.parentFinancialManagementModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
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
        var user = bundle.getString("user").toString()
        Toast.makeText(this, ""+user, Toast.LENGTH_SHORT).show()


        var sendBundle = Bundle()
        sendBundle.putString("childID", childID)
        sendBundle.putString("user", user)

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
        loadBackButton()
    }
        private fun loadBackButton() {
            binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
            binding.topAppBar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
}