package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityEarningMenuBinding
import ph.edu.dlsu.finwise.model.Users

class EarningMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEarningMenuBinding
    private var firestore = Firebase.firestore
    lateinit var module: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadButtons()
        setNavigationBar()

        val bundle = intent.extras!!
        val childID = bundle.getString("childID").toString()
        module = bundle.getString("module").toString()


        val sendBundle = Bundle()
        sendBundle.putString("childID", childID)
        sendBundle.putString("module", module)
        binding.btnHomeRewards.setOnClickListener {
            val goToHomeRewardsActivity = Intent(this, EarningActivity::class.java)
            goToHomeRewardsActivity.putExtras(sendBundle)
            startActivity(goToHomeRewardsActivity)
        }

        binding.btnSelling.setOnClickListener {
            val goToSellingActivity = Intent(this, EarningSellingActivity::class.java)
            goToSellingActivity.putExtras(sendBundle)
            startActivity(goToSellingActivity)
        }
    }

    private fun loadButtons() {
        loadBackButton()
    }

    private fun setNavigationBar() {
        var navUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(navUser).get().addOnSuccessListener {

            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent

            if (it.toObject<Users>()!!.userType == "Parent") {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal)
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