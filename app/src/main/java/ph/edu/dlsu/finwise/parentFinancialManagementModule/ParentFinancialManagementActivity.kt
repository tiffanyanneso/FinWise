package ph.edu.dlsu.finwise.parentFinancialManagementModule

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityParentFinancialManagementBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import java.text.DecimalFormat


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
        loadBalance()
        goToParentTransactions()
    }

    private fun loadBalance() {
        /*val currentUser = FirebaseAuth.getInstance().currentUser!!.uid*/
        /*firestore.collection("ChildWallet").whereEqualTo("childID", "eWZNOIb9qEf8kVNdvdRzKt4AYrA2")
            .get().addOnSuccessListener { document ->
                val childWallet = document.documents[0].toObject<ChildWallet>()
                val dec = DecimalFormat("#,###.00")
                balance = childWallet?.currentBalance!!
                val amount = dec.format(balance)
                binding.tvBalance.text = "₱$amount"
                initializeButtons()
            }*/
        val dec = DecimalFormat("#,###.00")
        //TODO: change to real wallet?
        balance = 150.00f
        val amount = dec.format(balance)
        binding.tvCurrentBalanceOfChild.text = "₱$amount"
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
            Toast.makeText(this, "balance"+balance, Toast.LENGTH_SHORT).show()
            bundle.putFloat("balance", balance)
            goToSendMoney.putExtras(bundle)
            startActivity(goToSendMoney)
        }
    }
}