package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.databinding.ActivityEarningSendMoneyPfmBinding
import ph.edu.dlsu.finwise.databinding.ActivityViewGoalBinding

class EarningSendMoneyActivityPFM : AppCompatActivity() {
    private lateinit var binding: ActivityEarningSendMoneyPfmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningSendMoneyPfmBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}