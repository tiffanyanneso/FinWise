package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordExpenseBinding
import ph.edu.dlsu.finwise.model.BudgetCategory
import ph.edu.dlsu.finwise.model.FinancialGoals
import java.text.SimpleDateFormat
import java.util.*

class RecordExpenseActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmrecordExpenseBinding
    var bundle = Bundle()
    private var firestore = Firebase.firestore
    private var goals = ArrayList<String>()

    lateinit var name: String
    lateinit var amount: String
    lateinit var category: String
    lateinit var goal: String
    lateinit var date: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmrecordExpenseBinding.inflate(layoutInflater)
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
        goals.add("None")
        firestore.collection("FinancialGoals").whereEqualTo("status", "In Progress").get().addOnSuccessListener { results ->
            for (goal in results) {
                var goalObject = goal.toObject<FinancialGoals>()
                goals.add(goalObject.goalName.toString())
            }
            val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, goals)
            binding.spinnerGoal.adapter = adapter
        }
    }

    private fun validateAndSetUserInput(): Boolean {
        var valid = true
        // Check if edit text is empty and valid
        if (binding.etName.text.toString().trim().isEmpty()) {
            binding.etName.error = "Please enter the name of the transaction."
            binding.etName.requestFocus()
            valid = false
        } else name = binding.etName.text.toString().trim()

        if (binding.spinnerCategory.selectedItem.toString() == "--Select Category--") {
            binding.tvErrorSpinner.visibility = View.VISIBLE
            valid = false
        } else {
            binding.tvErrorSpinner.visibility = View.GONE
            category = binding.spinnerCategory.selectedItem.toString()
        }


        if (binding.etAmount.text.toString().trim().isEmpty()) {
            binding.etAmount.error = "Please enter the amount."
            binding.etAmount.requestFocus()
            valid = false
        } else amount = binding.etAmount.text.toString().trim()

        return valid
    }


    private fun cancel() {
        binding.btnCancel.setOnClickListener {
            val goBack = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goBack)
        }
    }

    private fun goToConfirmation() {
        binding.btnConfirm.setOnClickListener {
            if (validateAndSetUserInput()) {
                setBundle()
                val goToConfirmTransaction = Intent(this, ConfirmTransactionActivity::class.java)
                goToConfirmTransaction.putExtras(bundle)
                goToConfirmTransaction.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(goToConfirmTransaction)
            } else {
                Toast.makeText(
                    baseContext, "Please fill up correctly the form.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setBundle() {
        getCurrentTime()
        val goal = binding.spinnerGoal.selectedItem.toString()

        bundle.putString("transactionType", "expense")
        bundle.putString("transactionName", name)
        bundle.putString("category", category)
        bundle.putFloat("amount", amount.toFloat())
        bundle.putString("goal", goal)
        bundle.putString("date", date)

        //TODO: reset spinner and date to default value
        /* binding.etName.text.clear()
         binding.etAmount.text.clear()
         binding.spinnerCategory.clear()
         binding.spinnerGoal.adapter(null)*/
    }

    private fun getCurrentTime() {
        //Time
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val time = Calendar.getInstance().time
        date = formatter.format(time)
    }
}