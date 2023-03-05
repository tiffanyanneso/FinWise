package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityEarningSellingBinding

class EarningSellingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEarningSellingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningSellingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializes the navbar
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal)
        loadBackButton()
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}