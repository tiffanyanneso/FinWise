package ph.edu.dlsu.finwise.parentFinancialManagementModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.EarningSalesAdapter
import ph.edu.dlsu.finwise.databinding.ActivityEarningSellingBinding
import ph.edu.dlsu.finwise.financialActivitiesModule.RecordEarningSaleActivity
import ph.edu.dlsu.finwise.model.SellingItems
import ph.edu.dlsu.finwise.personalFinancialManagementModule.RecordEarningSaleActivityPFM

class EarningSellingPFMActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEarningSellingBinding

    private lateinit var childID:String
    private lateinit var savingActivityID:String

    private var firestore = Firebase.firestore

    private lateinit var salesAdapter: EarningSalesAdapter
    private var salesArrayList = ArrayList<SellingItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningSellingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
//        supportActionBar?.hide()
//        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)

        val bundle = intent.extras!!
        val childID = bundle.getString("childID").toString()

        getSales()

        binding.btnNewSale.setOnClickListener {
            var newSale = Intent(this, RecordEarningSaleActivityPFM::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("childID", childID)
            newSale.putExtras(sendBundle)
            startActivity(newSale)
        }
        loadBackButton()
    }
    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun getSales() {
        salesArrayList.clear()
        firestore.collection("SellingItems").whereEqualTo("source", "PFM").get().addOnSuccessListener { results ->
            for (sale in results)
                salesArrayList.add(sale.toObject<SellingItems>())

            salesAdapter = EarningSalesAdapter(this, salesArrayList)
            binding.rvViewTransactions.adapter = salesAdapter
            binding.rvViewTransactions.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            salesAdapter.notifyDataSetChanged()
        }
    }
}