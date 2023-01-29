package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordDepositBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.DecimalFormat

class RecordDepositActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmrecordDepositBinding
    var bundle = Bundle()
    private var firestore = Firebase.firestore

    private var hi = "hi"
    private var goals = ArrayList<String>()
    private var goalArrayID = ArrayList<String>()
    private var goal = FinancialGoals()

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

    private fun getGoalProgress() {
        val sortSpinner = binding.spinnerGoal
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                val goalID = goalArrayID[position]
                //check if may laman sa baba yung goalarrayID
                /*Toast.makeText(applicationContext, position,
                    Toast.LENGTH_LONG).show()*/
                firestore.collection("FinancialGoals").document(goalID).get().addOnSuccessListener { document ->
                        goal = document.toObject<FinancialGoals>()!!
                        setProgressBar()
                    }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }
    }

    private fun setProgressBar() {
        binding.progressGoal.max = goal.targetAmount!!.toInt()
        binding.progressGoal.progress = goal.currentAmount!!.toInt()
        val dec = DecimalFormat("#,###.00")
        var targetAmount = dec.format(goal.targetAmount)
        var currentAmount = dec.format(goal.currentAmount)
        binding.tvBalance.text = "₱$currentAmount / $targetAmount"
    }


    private fun getGoals() {
        //TODO: UPDATE LATER WITH CHILD ID
        firestore.collection("FinancialGoals").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            for (goal in results) {
                val goalObject = goal.toObject<FinancialGoals>()
                goals.add(goalObject.goalName.toString())
                goalArrayID.add(goal.id)
            }

            val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, goals)
            binding.spinnerGoal.adapter = adapter

            getGoalProgress()
        }

    }

    private fun setGoalArrayID(goalArrayIDTemp: ArrayList<String>) {
        goalArrayID = goalArrayIDTemp
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
            val goToConfirmDeposit = Intent(this, ConfirmDepositActivity::class.java)
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