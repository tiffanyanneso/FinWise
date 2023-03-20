package ph.edu.dlsu.finwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.databinding.ActivityParentSavingPerformanceBinding

class ParentSavingPerformanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentSavingPerformanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentSavingPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}