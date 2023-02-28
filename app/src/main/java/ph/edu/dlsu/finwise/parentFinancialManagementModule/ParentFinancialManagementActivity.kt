package ph.edu.dlsu.finwise.parentFinancialManagementModule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityParentFinancialManagementBinding


class ParentFinancialManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentFinancialManagementBinding
    private var firestore = Firebase.firestore
    private var bundle = Bundle()
    var balance = 0.00f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentFinancialManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initializes the navbar
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance)

        goToParentTransactions()
        goToSendMoney()
    }

    private fun goToParentTransactions() {
        binding.btnViewTransactions.setOnClickListener {
            val goToTransactions = Intent(applicationContext, ParentTransactionHistoryActivity::class.java)
            startActivity(goToTransactions)
        }
    }

    private fun goToSendMoney() {
        binding.btnSendMoney.setOnClickListener {
            val goToSendMoney = Intent(applicationContext, ParentSendMoneyActivity::class.java)
            startActivity(goToSendMoney)
        }
    }
}