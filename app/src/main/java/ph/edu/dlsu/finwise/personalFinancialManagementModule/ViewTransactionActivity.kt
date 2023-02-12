package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityPfmviewTransactionBinding
import ph.edu.dlsu.finwise.model.Transactions
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ViewTransactionActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmviewTransactionBinding
    var bundle = Bundle()
    private var firestore = Firebase.firestore
    lateinit var transaction: Transactions


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmviewTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)

        //loadTransactionDetails()
        //goToBack()

    }
/*
    private fun goToBack() {
        binding.btnDone.setOnClickListener{

            val goBack= Intent(this, PersonalFinancialManagementActivity::class.java)
            startActivity(goBack)
        }
    }


    private fun loadTransactionDetails() {
        val bundle: Bundle = intent.extras!!
        val transactionID = bundle.getString("transactionID")

        if (transactionID != null) {
            firestore.collection("Transactions").document(transactionID).get().addOnSuccessListener { document ->
                if (document != null) {
                    transaction = document.toObject<Transactions>()!!
                    val name = transaction.transactionName.toString()
                    if (transaction.category.toString() == "Goal")
                        initializeText(name, false)
                    else if (transaction.transactionType.toString() == "Maya Expense")
                        initializeText(name, true)
                     else initializeText(name, false)

                }
            }
        }
    }

    private fun initializeText(name: String, isPayMayaExpense: Boolean) {
        val dec = DecimalFormat("#,###.00")
        val amount = dec.format(transaction.amount)
        binding.tvTransactionType.text = transaction.transactionType.toString()
        binding.tvAmount.text = "₱$amount"
        binding.tvName.text = name
        binding.tvCategory.text = transaction.category.toString()
        // convert timestamp to date string
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val date = formatter.format(transaction.date?.toDate()!!)
        binding.tvDate.text = date.toString()

        if (isPayMayaExpense)
            payMayaTransaction()
    }

    private fun payMayaTransaction() {
        binding.tvMerchant.visibility = View.VISIBLE
        binding.tvMerchantLabel.visibility = View.VISIBLE
        binding.tvMerchant.text = transaction.merchant.toString()
    }*/
}