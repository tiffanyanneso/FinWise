package ph.edu.dlsu.finwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import ph.edu.dlsu.finwise.databinding.ActivityGoalSettingsBinding

class GoalSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var switchSetOwnGoal = binding.switchSetOwnGoal
        var switchAutoApprove = binding.switchAutoApprove
        var switchUnaccomplishedGoal = binding.switchUnaccomplishedGoal

        switchSetOwnGoal?.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Switch Set Own Goal:ON" else "Switch Set Own Goal:OFF"
            Toast.makeText(
                this, message,
                Toast.LENGTH_SHORT
            ).show()
        }

        switchAutoApprove?.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Switch Auto Approve:ON" else "Switch Auto Approve:OFF"
            Toast.makeText(
                this, message,
                Toast.LENGTH_SHORT
            ).show()
        }

        switchUnaccomplishedGoal?.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Switch Unaccomplished:ON" else "Switch Unaccomplished:OFF"
            Toast.makeText(
                this, message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}