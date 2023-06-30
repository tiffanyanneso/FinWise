package ph.edu.dlsu.finwise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.databinding.ActivityChildSettingsBinding
import ph.edu.dlsu.finwise.model.SettingsModel
import ph.edu.dlsu.finwise.profileModule.ProfileActivity


class ChildSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChildSettingsBinding
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private var firestore = Firebase.firestore

    private lateinit var settingsID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChildSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_profile)
        loadBackButton()
        cancel()

        CoroutineScope(Dispatchers.Main).launch {
            loadSettings()
        }

        binding.btnSave.setOnClickListener {
            saveSettings()
        }
    }

    private fun saveSettings() {
        //get the text of the radio button that is checked
//        var pfmScore = binding.radioButtonsPfm.findViewById<RadioButton>(binding.radioButtonsPfm.checkedRadioButtonId).text.toString()
//        var finactScore = binding.radioButtonsFinact.findViewById<RadioButton>(binding.radioButtonsFinact.checkedRadioButtonId).text.toString()
//        var assessmentScore = binding.radioButtonsAssessments.findViewById<RadioButton>(binding.radioButtonsAssessments.checkedRadioButtonId).text.toString()
        var literacyScore = binding.radioButtonsOverall.findViewById<RadioButton>(binding.radioButtonsOverall.checkedRadioButtonId).tag.toString()

        firestore.collection("Settings").document(settingsID).update("literacyGoal", literacyScore).addOnSuccessListener {
            Toast.makeText(this, "Settings successfully updated", Toast.LENGTH_SHORT).show()
            //"pfmScore", pfmScore, "finactScore", finactScore, "assessmentScore", assessmentScore
        }
    }

    private suspend fun loadSettings() {
        var settings = firestore.collection("Settings").whereEqualTo("childID", currentUser).get().await()
        if (!settings.isEmpty) {
            settingsID = settings.documents[0].id
            var settingsObject = settings.documents[0].toObject<SettingsModel>()
            when (settingsObject?.literacyGoal) {
                "Excellent" -> binding.rbOverallExcellent.isChecked = true
                "Amazing" -> binding.rbOverallAmazing.isChecked = true
                "Great" -> binding.rbOverallGreat.isChecked = true
                "Good" -> binding.rbOverallGood.isChecked = true
            }
//            when (settingsObject?.pfmScore) {
//                "Excellent" -> binding.rbPfmExcellent.isChecked = true
//                "Amazing" -> binding.rbPfmAmazing.isChecked = true
//                "Great" -> binding.rbPfmGreat.isChecked = true
//                "Good" -> binding.rbPfmGood.isChecked = true
//            }
//
//            when (settingsObject?.finactScore) {
//                "Excellent" -> binding.rbFinactExcellent.isChecked = true
//                "Amazing" -> binding.rbFinactAmazing.isChecked = true
//                "Great" -> binding.rbFinactGreat.isChecked = true
//                "Good" -> binding.rbFinactGood.isChecked = true
//            }
//
//            when (settingsObject?.assessmentScore) {
//                "Excellent" -> binding.rbAssessmentsExcellent.isChecked = true
//                "Amazing" -> binding.rbAssessmentsAmazing.isChecked = true
//                "Great" -> binding.rbAssessmentsGreat.isChecked = true
//                "Good" -> binding.rbAssessmentsGood.isChecked = true
//            }
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun cancel() {
        binding.btnCancel.setOnClickListener {
            var profile = Intent(this, ProfileActivity::class.java)
            startActivity(profile)
        }
    }
}