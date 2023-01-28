package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.TransactionsAdapter
import ph.edu.dlsu.finwise.databinding.ActivityPfmtransactionHistoryBinding

class TransactionHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPfmtransactionHistoryBinding
    private lateinit var transactionAdapter: TransactionsAdapter
    private var firestore = Firebase.firestore
    private var transactionIDArrayList = ArrayList<String>()
    private lateinit var type: String
    private lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmtransactionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)

        getAllTransactions()
        sortTransactions()
    }

    private fun sortTransactions() {
        var sortSpinner = binding.spinnerSort
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                type = sortSpinner.selectedItem.toString()

                if (type == "--All--") {
                    transactionIDArrayList.clear()
                    getAllTransactions()
                }
                else {
                    type = type.lowercase()
                    transactionIDArrayList.clear()
                    firestore.collection("Transactions")
                        .whereEqualTo("transactionType", type).get().addOnSuccessListener { documents ->
                            for (transactionSnapshot in documents) {
                                //creating the object from list retrieved in db
                                val transactionID = transactionSnapshot.id
                                transactionIDArrayList.add(transactionID)
                            }
                            loadRecyclerView(transactionIDArrayList)
                        }
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }
    }

    private fun getAllTransactions() {

        //TODO:change to get transactions of current user
        //var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        //firestore.collection("Transactions").whereEqualTo("companyID", currentUser).get().addOnSuccessListener{ documents ->

        firestore.collection("Transactions").get().addOnSuccessListener { documents ->
            for (transactionSnapshot in documents) {
                //creating the object from list retrieved in db
                val transactionID = transactionSnapshot.id
                transactionIDArrayList.add(transactionID)
            }
            loadRecyclerView(transactionIDArrayList)
        }
    }

    private fun loadRecyclerView(transactionIDArrayList: ArrayList<String>) {
        transactionAdapter = TransactionsAdapter(this, transactionIDArrayList)
        binding.rvViewTransactions.adapter = transactionAdapter
        binding.rvViewTransactions.layoutManager = LinearLayoutManager(applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
        transactionAdapter.notifyDataSetChanged()
    }

}