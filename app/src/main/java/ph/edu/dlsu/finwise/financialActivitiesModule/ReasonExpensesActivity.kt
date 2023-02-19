package ph.edu.dlsu.finwise.financialActivitiesModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityBudgetExpenseBinding
import ph.edu.dlsu.finwise.databinding.ActivityReasonExpensesBinding

class ReasonExpensesActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReasonExpensesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReasonExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
    }
}