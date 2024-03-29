package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import ph.edu.dlsu.finwise.model.Users
import java.util.*
import kotlin.collections.ArrayList

class EarningSellingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEarningSellingBinding

    private lateinit var childID:String
    private lateinit var module:String

    private var firestore = Firebase.firestore

    private lateinit var salesAdapter: EarningSalesAdapter
    private var salesArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningSellingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadButtons()
        setNavigationBar()

        val bundle = intent.extras!!
        childID = bundle.getString("childID").toString()
        module = bundle.getString("module").toString()


        getSales()

        binding.btnNewSale.setOnClickListener {
            val newSale = Intent(this, RecordEarningSaleActivity::class.java)
            val sendBundle = Bundle()
            sendBundle.putString("childID", childID)
            newSale.putExtras(sendBundle)
            startActivity(newSale)
        }

    }

    class SaleSort(var saleDate: Date, var saleID: String)

    private fun getSales() {
        var salesFilterArrayList = ArrayList<SaleSort>()
        salesArrayList.clear()
        firestore.collection("SellingItems").whereEqualTo("childID", childID).get().addOnSuccessListener { results ->
            for (sale in results) {
                var saleObject = sale.toObject<SellingItems>()
                salesFilterArrayList.add(SaleSort(saleObject.date!!.toDate(), sale.id))
            }
            salesFilterArrayList.sortedByDescending { it.saleDate }

            for (saleSort in salesFilterArrayList)
                salesArrayList.add(saleSort.saleID)

            if (!salesArrayList.isEmpty())
                loadRecyclerView()
            else
                emptyList()
            binding.rvViewTransactions.visibility = View.VISIBLE
            binding.loadingItems.stopShimmer()
            binding.loadingItems.visibility = View.GONE
        }
    }

    private fun loadRecyclerView() {
        salesAdapter = EarningSalesAdapter(this, salesArrayList)
        binding.rvViewTransactions.adapter = salesAdapter
        binding.rvViewTransactions.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        salesAdapter.notifyDataSetChanged()
    }

    private fun emptyList() {
        binding.rvViewTransactions.visibility = View.GONE
        binding.layoutEmptyActivity.visibility = View.VISIBLE
    }


    private fun loadButtons() {
        loadBackButton()
    }

    private fun setNavigationBar() {

        val navUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(navUser).get().addOnSuccessListener {

            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent

            if (it.toObject<Users>()!!.userType == "Parent") {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                //sends the ChildID to the parent navbar
                val bundle = Bundle()
                val childID = bundle.getString("childID").toString()
                val bundleNavBar = Bundle()
                bundleNavBar.putString("childID", childID)
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)
                binding.btnNewSale.visibility = View.GONE
            } else if (it.toObject<Users>()!!.userType == "Child") {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                if (module == "finact")
                    Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
                else  Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)
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