package ph.edu.dlsu.finwise.financialActivitiesModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ph.edu.dlsu.finwise.databinding.ActivityBudgetingPerformanceBinding

class BudgetingPerformanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetingPerformanceBinding

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}