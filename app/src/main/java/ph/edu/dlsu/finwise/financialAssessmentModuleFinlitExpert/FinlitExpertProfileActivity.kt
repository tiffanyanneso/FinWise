package ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import ph.edu.dlsu.finwise.MainActivity
import ph.edu.dlsu.finwise.NavbarFinlitExpert
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityFinancialAssessmentFinlitExpertProfileBinding
import ph.edu.dlsu.finwise.databinding.ActivityProfileBinding

class FinlitExpertProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinancialAssessmentFinlitExpertProfileBinding
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityFinancialAssessmentFinlitExpertProfileBinding.inflate(layoutInflater)
            setContentView(binding.root)
//            super.onCreate(savedInstanceState)
//            setContentView(R.layout.activity_financial_assessment_finlit_expert_profile)

            binding.topAppBar.setOnMenuItemClickListener{ menuItem ->
                when (menuItem.itemId) {
                    R.id.btn_logout -> {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent (this, MainActivity::class.java)
                        startActivity (intent)
                        finish()
                        true
                    }
                    else -> false
                }
            }

            // Hides actionbar,
            // and initializes the navbar
            supportActionBar?.hide()
            //NavbarFinlitExpert(findViewById(R.id.bottom_nav_finlit_expert), this, R.id.nav_finlit_profile)
        }
}