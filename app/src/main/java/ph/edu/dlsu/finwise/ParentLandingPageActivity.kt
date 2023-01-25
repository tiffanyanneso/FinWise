package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.databinding.ActivityParentLandingPageBinding
import ph.edu.dlsu.finwise.databinding.ActivityParentSettingAgoalBinding
import ph.edu.dlsu.finwise.loginRegisterModule.ParentRegisterChildActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity

class ParentLandingPageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityParentLandingPageBinding
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this

        binding.btnAddChild.setOnClickListener {
            val goToChildRegister = Intent(this, ParentRegisterChildActivity::class.java)
            startActivity(goToChildRegister)
        }
    }
}