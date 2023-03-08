package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.financialActivitiesModule.RecordEarningSaleActivity
import ph.edu.dlsu.finwise.adapter.EarningSalesAdapter
import ph.edu.dlsu.finwise.databinding.ActivityEarningSellingBinding
import ph.edu.dlsu.finwise.model.SellingItems

class EarningSellingActivity : AppCompatActivity() {
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

        loadButtons()
        setNavigationBar()

        var bundle = intent.extras!!
        childID = bundle.getString("childID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()

        getSales()

        binding.btnNewSale.setOnClickListener {
            var newSale = Intent(this, RecordEarningSaleActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("childID", childID)
            sendBundle.putString("savingActivityID", savingActivityID)
            newSale.putExtras(sendBundle)
            startActivity(newSale)
        }

    }

    private fun getSales() {
        salesArrayList.clear()
        firestore.collection("SellingItems").whereEqualTo("savingActivityID", savingActivityID).whereEqualTo("source", "Financial Activity").get().addOnSuccessListener { results ->
            for (sale in results)
                salesArrayList.add(sale.toObject<SellingItems>())

            salesAdapter = EarningSalesAdapter(this, salesArrayList)
            binding.rvViewTransactions.adapter = salesAdapter
            binding.rvViewTransactions.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            salesAdapter.notifyDataSetChanged()
        }
    }

    private fun loadButtons() {
        loadBackButton()
    }

    private fun setNavigationBar() {

        var navUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("ParentUser").document(navUser).get().addOnSuccessListener {

            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent

            if (it.exists()) {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal)
            } else  {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
            }
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}