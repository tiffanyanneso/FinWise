package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityPfmtransactionDetailsBinding

class TransactionDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPfmtransactionDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmtransactionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)

        goToBack()

    }

    private fun goToBack() {
        binding.btnBack.setOnClickListener{

            val goBack= Intent(this, PersonalFinancialManagementActivity::class.java)
            startActivity(goBack)
        }
    }

}