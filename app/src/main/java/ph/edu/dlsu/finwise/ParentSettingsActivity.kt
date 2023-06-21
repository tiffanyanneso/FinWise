package ph.edu.dlsu.finwise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
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
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

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
        var maxAmount = binding.etMaxAmount.text.toString()
        var alertAmount =  binding.etAlertAmount.text.toString()

        var pfmScore = binding.radioButtonsPfm.findViewById<RadioButton>(binding.radioButtonsPfm.checkedRadioButtonId).text.toString()
        var finactScore = binding.radioButtonsFinact.findViewById<RadioButton>(binding.radioButtonsFinact.checkedRadioButtonId).text.toString()
        var assessmentScore = binding.radioButtonsAssessments.findViewById<RadioButton>(binding.radioButtonsAssessments.checkedRadioButtonId).text.toString()

        if (maxAmount.isNotEmpty() && alertAmount.isNotEmpty()) {
            binding.containerMaxAmount.helperText = ""
            binding.containerAlertAmount.helperText = ""
            firestore.collection("Settings").document(settingsID).update(
                "maxAmountActivities", maxAmount.toFloat(),
                "alertAmount", alertAmount.toFloat(),
                "buyingItem", binding.checkBuyingItems.isChecked,
                "planingEvent", binding.checkPlanningEvent.isChecked,
                "emergencyFund", binding.checkEmergency.isChecked,
                "donatingCharity", binding.checkDonating.isChecked,
                "situationalShopping", binding.checkSituational.isChecked,
                "pfmScore", pfmScore,
                "finactScore", finactScore,
                "assessmentScore", assessmentScore
            ).addOnSuccessListener {
                Toast.makeText(this, "Settings have been updated", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (maxAmount.isEmpty())
                binding.containerMaxAmount.helperText = "Input a max amount for activities"
            else
                binding.containerMaxAmount.helperText = ""

            if (alertAmount.isEmpty())
                binding.containerAlertAmount.helperText = "Input a max amount for transactions"
            else
                binding.containerAlertAmount.helperText = ""
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

            when (settings?.pfmScore) {
                "Excellent" -> binding.rbPfmExcellent.isChecked = true
                "Amazing" -> binding.rbPfmAmazing.isChecked = true
                "Great" -> binding.rbPfmGreat.isChecked = true
                "Good" -> binding.rbPfmGood.isChecked = true
            }

            when (settings?.finactScore) {
                "Excellent" -> binding.rbFinactExcellent.isChecked = true
                "Amazing" -> binding.rbFinactAmazing.isChecked = true
                "Great" -> binding.rbFinactGreat.isChecked = true
                "Good" -> binding.rbFinactGood.isChecked = true
            }

            when (settings?.assessmentScore) {
                "Excellent" -> binding.rbAssessmentsExcellent.isChecked = true
                "Amazing" -> binding.rbAssessmentsAmazing.isChecked = true
                "Great" -> binding.rbAssessmentsGreat.isChecked = true
                "Good" -> binding.rbAssessmentsGood.isChecked = true
            }
        }

        var child = firestore.collection("Users").document(childID).get().await().toObject<Users>()

        //compute age
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val from = LocalDate.now()
        val date = SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
        val to = LocalDate.parse(date.toString(), dateFormatter)
        var difference = Period.between(to, from)

        var age = difference.years
        var maxAmount = 3000F

        if (age == 9)
            maxAmount = 3000F
        else if (age == 10 || age == 11)
            maxAmount = 5000F
        else
            maxAmount = 10000F

        binding.tvRecommendedMax.text = "Recommended maximum for age is ₱${DecimalFormat("#,##0.00").format(maxAmount)}."
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