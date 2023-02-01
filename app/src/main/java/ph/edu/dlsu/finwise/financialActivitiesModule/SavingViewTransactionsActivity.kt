package ph.edu.dlsu.finwise.financialActivitiesModule

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.GoalViewDepositAdapater
import ph.edu.dlsu.finwise.databinding.ActivitySavingViewTransactionsBinding
import ph.edu.dlsu.finwise.model.Transactions

class SavingViewTransactionsActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySavingViewTransactionsBinding
    private var firestore = Firebase.firestore
    private lateinit  var goalViewDepositAdapater:GoalViewDepositAdapater

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavingViewTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        var bundle: Bundle = intent.extras!!
        var decisionActivityID = bundle.getString("decisionMakingActivityID")

        firestore.collection("Transactions").whereEqualTo("decisionMakingActivityID", decisionActivityID).get().addOnSuccessListener { transactionsSnapshot ->
            var transactionsArrayList = ArrayList<Transactions>()
            for (document in transactionsSnapshot) {
                var transaction = document.toObject<Transactions>()
                transactionsArrayList.add(transaction)
            }

            goalViewDepositAdapater = GoalViewDepositAdapater(this, transactionsArrayList)
            binding.rvViewDepositHistory.adapter = goalViewDepositAdapater
            binding.rvViewDepositHistory.layoutManager = LinearLayoutManager(applicationContext,
                LinearLayoutManager.VERTICAL,
                false)


        }
    }
}