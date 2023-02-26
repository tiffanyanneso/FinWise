package ph.edu.dlsu.finwise.personalFinancialManagementModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.databinding.ActivityGoalSavingDetailsBinding
import ph.edu.dlsu.finwise.databinding.ActivityTrendDetailsBinding

class GoalSavingDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoalSavingDetailsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalSavingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}