package ph.edu.dlsu.finwise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.databinding.ActivityEarningBinding
import ph.edu.dlsu.finwise.databinding.ActivityEarningMenuBinding
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.NewEarningActivity

class EarningMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEarningMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnHomeRewards.setOnClickListener {
            var goToHomeRewardsActivity = Intent(this, ph.edu.dlsu.finwise.parentFinancialActivitiesModule.EarningActivity::class.java)
            startActivity(goToHomeRewardsActivity)
        }

        binding.btnSelling.setOnClickListener {
            var goToSellingActivity = Intent(this, EarningSellingActivity::class.java)
            startActivity(goToSellingActivity)
        }
    }
}