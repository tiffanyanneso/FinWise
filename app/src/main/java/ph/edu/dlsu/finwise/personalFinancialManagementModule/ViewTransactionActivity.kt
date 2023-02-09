package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityPfmviewTransactionBinding
import ph.edu.dlsu.finwise.model.Transactions
import java.text.SimpleDateFormat
import java.util.*

class ViewTransactionActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmviewTransactionBinding
    var bundle = Bundle()
    private var firestore = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmviewTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)

        loadTransactionDetails()

        goToBack()




    }

    private fun goToBack() {
        binding.btnBack.setOnClickListener{

            val goBack= Intent(this, PersonalFinancialManagementActivity::class.java)
            startActivity(goBack)
        }
    }


    private fun loadTransactionDetails() {
        var bundle: Bundle = intent.extras!!
        var transactionID = bundle.getString("transactionID")

        if (transactionID != null) {
            firestore.collection("Transactions").document(transactionID).get().addOnSuccessListener { document ->
                if (document != null) {
                    Toast.makeText(this, transactionID, Toast.LENGTH_SHORT).show()
                    val transaction = document.toObject(Transactions::class.java)
                    if (transaction?.category.toString() == "Goal")
                        binding.tvTransactionType.text = "Deposit to Goal"
                    else {
                        binding.tvTransactionType.text = transaction?.transactionType.toString()
                    }
                    binding.tvAmount.text = "₱" + transaction?.amount.toString()
                    binding.tvName.text = transaction?.transactionName.toString()
                    binding.tvCategory.text = transaction?.category.toString()
                    // convert timestamp to date string
                    val formatter = SimpleDateFormat("MM/dd/yyyy")
                    val date = formatter.format(transaction?.date?.toDate())
                    binding.tvDate.text = date.toString()
                    //binding.tvGoal.text = transaction?.goal.toString()
                }
            }
        }
    }
}