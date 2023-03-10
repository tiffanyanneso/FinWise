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
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityEarningMenuBinding

class EarningMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEarningMenuBinding
    private var firestore = Firebase.firestore
    lateinit var module: String
    lateinit var savingActivityID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarningMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadButtons()
        setNavigationBar()

        val bundle = intent.extras!!
        val childID = bundle.getString("childID").toString()
        module = bundle.getString("module").toString()

        savingActivityID = if (bundle.containsKey("savingActivityID")) {
            bundle.getString("savingActivityID").toString()
            // do something with the value
        } else {
            "null"
        }
        Log.d("asdas", "onCreate: +"+savingActivityID)


        val sendBundle = Bundle()
        sendBundle.putString("childID", childID)
        sendBundle.putString("module", module)
        sendBundle.putString("savingActivityID", savingActivityID)
        binding.btnHomeRewards.setOnClickListener {
            val goToHomeRewardsActivity = Intent(this, EarningActivity::class.java)
            goToHomeRewardsActivity.putExtras(sendBundle)
            startActivity(goToHomeRewardsActivity)
        }

        binding.btnSelling.setOnClickListener {
            val goToSellingActivity = Intent(this, EarningSellingActivity::class.java)
            sendBundle.putString("savingActivityID", savingActivityID)
            goToSellingActivity.putExtras(sendBundle)
            startActivity(goToSellingActivity)
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