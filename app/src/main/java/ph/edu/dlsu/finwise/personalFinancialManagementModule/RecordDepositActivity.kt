package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordDepositBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.SimpleDateFormat
import java.util.*

class RecordDepositActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmrecordDepositBinding
    var bundle = Bundle()
    private var firestore = Firebase.firestore

    private var goals = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmrecordDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hides actionbar,
        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(ph.edu.dlsu.finwise.R.id.bottom_nav), this, ph.edu.dlsu.finwise.R.id.nav_finance)

        getGoals()
        goToConfirmation()
        cancel()
    }

    private fun getGoals() {
        //TODO: UPDATE LATER WITH CHILD ID
        firestore.collection("FinancialGoals").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            for (goal in results) {
                var goalObject = goal.toObject<FinancialGoals>()
                goals.add(goalObject.goalName.toString())
            }
            val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, goals)
            binding.spinnerGoal.adapter = adapter
        }
    }

    private fun cancel() {
        binding.btnCancel.setOnClickListener {
            val goBack = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goBack)
        }
    }

    private fun goToConfirmation() {
        binding.btnConfirm.setOnClickListener {
            setBundle()
            var goToConfirmDeposit = Intent(this, ConfirmDepositActivity::class.java)
            goToConfirmDeposit.putExtras(bundle)
            goToConfirmDeposit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(goToConfirmDeposit)
        }
    }

    private fun setBundle() {
        val amount = binding.etAmount.text.toString().toFloat()
        val goal = binding.spinnerGoal.selectedItem.toString()
        /*Get logged in user's balance'
        val userBalance = */

        bundle.putString("transactionType", "goal")
        bundle.putFloat("amount", amount)
        bundle.putString("goal", goal)
        bundle.putString("source", "PFMDepositToGoal")

        //TODO: reset spinner and date to default value
        /* binding.etName.text.clear()
         binding.etAmount.text.clear()
         binding.spinnerCategory.clear()
         binding.spinnerGoal.adapter(null)*/
    }
}