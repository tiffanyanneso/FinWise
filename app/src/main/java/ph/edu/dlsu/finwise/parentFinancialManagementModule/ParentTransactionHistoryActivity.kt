package ph.edu.dlsu.finwise.parentFinancialManagementModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityParentTransactionHistoryBinding



class ParentTransactionHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParentTransactionHistoryBinding
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentTransactionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        // Initializes the navbar
        Navbar(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance)
        loadBackButton()

    }


    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToPFM = Intent(applicationContext, ParentFinancialManagementActivity::class.java)
            startActivity(goToPFM)
        }
    }





}