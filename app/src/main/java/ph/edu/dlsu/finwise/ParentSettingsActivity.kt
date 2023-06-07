package ph.edu.dlsu.finwise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.databinding.ActivityMainBinding
import ph.edu.dlsu.finwise.databinding.ActivityParentSettingsBinding
import ph.edu.dlsu.finwise.model.SettingsModel
import java.text.DecimalFormat

class ParentSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentSettingsBinding
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private var firestore = Firebase.firestore

    private lateinit var bundle:Bundle
    private lateinit var childID:String
    private lateinit var parentID:String

    private lateinit var settingsID:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bundle = intent.extras!!

        loadBackButton()
        cancel()

        bundle = intent.extras!!
        NavbarParentFirst(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_first_profile)

        CoroutineScope(Dispatchers.Main).launch {
            loadSettings()
        }

        binding.btnSave.setOnClickListener {
            saveSettings()
        }
    }

    private fun saveSettings() {
        firestore.collection("Settings").document(settingsID).update(
            "maxAmountActivities",  binding.etMaxAmount.text.toString().toFloat(),
            "alertAmount",  binding.etAlertAmount.text.toString().toFloat(),
            "buyingItem", binding.checkBuyingItems.isChecked,
            "planingEvent",  binding.checkPlanningEvent.isChecked,
            "emergencyFund", binding.checkEmergency.isChecked,
            "donatingCharity", binding.checkDonating.isChecked,
            "situationalShopping",  binding.checkSituational.isChecked
        ).addOnSuccessListener {
            Toast.makeText(this, "Settings have been updated", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun loadSettings() {
        childID = bundle.getString("childID").toString()
        parentID = bundle.getString("parentID").toString()

        var settingsSnapshot = firestore.collection("Settings").whereEqualTo("childID", childID).whereEqualTo("parentID", parentID).get().await()
        if (!settingsSnapshot.isEmpty) {
            settingsID = settingsSnapshot.documents[0].id
            var settings = settingsSnapshot.documents[0].toObject<SettingsModel>()
            binding.etMaxAmount.setText(DecimalFormat("###0").format(settings?.maxAmountActivities))
            binding.etAlertAmount.setText(DecimalFormat("###0").format(settings?.alertAmount))
            binding.checkBuyingItems.isChecked = settings?.buyingItem!!
            binding.checkPlanningEvent.isChecked = settings?.planingEvent!!
            binding.checkEmergency.isChecked = settings?.emergencyFund!!
            binding.checkDonating.isChecked = settings?.donatingCharity!!
            binding.checkSituational.isChecked = settings?.situationalShopping!!
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun cancel() {
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}