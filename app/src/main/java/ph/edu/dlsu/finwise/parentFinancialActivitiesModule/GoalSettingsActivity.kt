package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityGoalSettingsBinding
import ph.edu.dlsu.finwise.model.GoalSettings

class GoalSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalSettingsBinding
    private var firestore = Firebase.firestore

    private lateinit var bundle:Bundle

    private lateinit var parentID:String
    private lateinit var childID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bundle = intent.extras!!
        parentID = FirebaseAuth.getInstance().currentUser!!.uid
        childID = bundle.getString("childID").toString()

        loadSettings()

        var switchSetOwnGoal = binding.switchSetOwnGoal
        var switchAutoApprove = binding.switchAutoApprove
        var switchUnaccomplishedGoal = binding.switchUnaccomplishedGoal

        switchSetOwnGoal?.setOnCheckedChangeListener { _, isChecked ->
            updateSettings()
//            val message = if (isChecked) "Switch Set Own Goal:ON" else "Switch Set Own Goal:OFF"
            Toast.makeText(this, "Settings updated", Toast.LENGTH_SHORT).show()
        }

        switchAutoApprove?.setOnCheckedChangeListener { _, isChecked ->
            updateSettings()
//            val message = if (isChecked) "Switch Auto Approve:ON" else "Switch Auto Approve:OFF"
            Toast.makeText(this, "Settings updated", Toast.LENGTH_SHORT).show()
        }

        switchUnaccomplishedGoal?.setOnCheckedChangeListener { _, isChecked ->
            updateSettings()
//            val message = if (isChecked) "Switch Unaccomplished:ON" else "Switch Unaccomplished:OFF"
            Toast.makeText(this, "Settings updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSettings() {
        firestore.collection("GoalSettings").whereEqualTo("parentID", parentID).whereEqualTo("childID", childID).get().addOnSuccessListener {
            var goalSetting = it.documents[0].toObject<GoalSettings>()
            if (goalSetting?.setOwnGoal == true)
                binding.switchSetOwnGoal.isChecked = true
            if (goalSetting?.autoApproved == true)
                binding.switchAutoApprove.isChecked = true
            if (goalSetting?.unaccomplishedGoal == true)
                binding.switchUnaccomplishedGoal.isChecked = true

        }
    }

    private fun updateSettings() {
        var setOwnGoal = binding.switchSetOwnGoal.isChecked
        var autoApproved = binding.switchAutoApprove.isChecked
        var unaccomplishedGoal = binding.switchUnaccomplishedGoal.isChecked

        firestore.collection("GoalSettings").whereEqualTo("parentID", parentID).whereEqualTo("childID", childID).get().addOnSuccessListener { result ->
            var settingsID = result.documents[0].id
            firestore.collection("GoalSettings").document(settingsID).update("setOwnGoal", setOwnGoal,
                "autoApproved", autoApproved, "unaccomplishedGoal", unaccomplishedGoal)
        }
    }
}