package ph.edu.dlsu.finwise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.TransactionsAdapter
import ph.edu.dlsu.finwise.databinding.ActivityPersonalFinancialManagementBinding
import ph.edu.dlsu.finwise.databinding.ActivityPfmtransactionHistoryBinding
import ph.edu.dlsu.finwise.model.Transactions

class PFMTransactionHistoryActivity : AppCompatActivity(), TransactionsAdapter.OnItemClickListener {
    private lateinit var binding: ActivityPfmtransactionHistoryBinding
    private lateinit var transactionAdapter: TransactionsAdapter
    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmtransactionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)*/

        getTransactions()
    }

    private fun getTransactions() {
        var transactionArrayList = ArrayList<Transactions>()
        //TODO:change to get transactions of current user
        //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        //firestore.collection("Transactions").whereEqualTo("companyID", currentUser).get().addOnSuccessListener{ documents ->

        firestore.collection("Transactions").get().addOnSuccessListener { documents ->
            for (transactionSnapshot in documents) {
                //creating the object from list retrieved in db
                val transaction = transactionSnapshot.toObject<Transactions>()
                transactionArrayList.add(transaction)
            }

            transactionAdapter = TransactionsAdapter(transactionArrayList, this)
            binding.rvViewTransactions.adapter = transactionAdapter
            binding.rvViewTransactions.layoutManager = LinearLayoutManager(applicationContext,
                LinearLayoutManager.VERTICAL,
                false)

        }
    }

    override fun onLoadClick(position: Int) {
        var gotoBurstWorkoutExercisesActivity = Intent(applicationContext, PFMViewTransactionActivity::class.java)
        gotoBurstWorkoutExercisesActivity.putExtra("position", position)
        startActivity(gotoBurstWorkoutExercisesActivity)
    }
}