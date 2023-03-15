package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityCashMayaBalanceBreakdownBinding
import ph.edu.dlsu.finwise.model.Users

class CashMayaBalanceBreakdownActivity : AppCompatActivity() {

    private lateinit var binding:ActivityCashMayaBalanceBreakdownBinding
    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCashMayaBalanceBreakdownBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNavigationBar()
        loadBackButton()

    }



    private fun setNavigationBar() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val firestore = Firebase.firestore
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            var user  = it.toObject<Users>()!!
            //current user is a child
            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent
            if (user.userType == "Child") {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)
            } else  if (user.userType == "Parent"){
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance)
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