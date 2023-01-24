package ph.edu.dlsu.finwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.databinding.ActivityLoginBinding
import ph.edu.dlsu.finwise.databinding.ActivityReviewGoalBinding

class ReviewGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewGoalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}