package ph.edu.dlsu.finwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.databinding.ActivitySavingPerformanceBinding
import ph.edu.dlsu.finwise.databinding.ActivityStartGoalBinding

class SavingPerformanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavingPerformanceBinding
            
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}