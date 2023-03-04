package ph.edu.dlsu.finwise.financialActivitiesModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ph.edu.dlsu.finwise.databinding.ActivitySpendingPerformanceBinding

class SpendingPerformanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpendingPerformanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpendingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}