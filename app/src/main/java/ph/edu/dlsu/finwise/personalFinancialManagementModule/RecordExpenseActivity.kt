package ph.edu.dlsu.finwise.personalFinancialManagementModule

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmrecordExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun cancel() {
        binding.btnCancel.setOnClickListener {
            val goBack = Intent(applicationContext, PersonalFinancialManagementActivity::class.java)
            startActivity(goBack)
        }
    }

    private fun goToConfirmation() {
        binding.btnConfirm.setOnClickListener {
            setBundle()
            var goToConfirmTransaction = Intent(this, ConfirmTransactionActivity::class.java)
            goToConfirmTransaction.putExtras(bundle)
            goToConfirmTransaction.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(goToConfirmTransaction)
        }
    }

    private fun setBundle() {
        val name =  binding.etName.text.toString()
        val category = binding.spinnerCategory.selectedItem.toString()
        val amount = binding.etAmount.text.toString().toFloat()
        val goal = binding.spinnerGoal.selectedItem.toString()

        //Time
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val time = Calendar.getInstance().time
        val current = formatter.format(time)
        var date = current
        bundle.putString("transactionType", "expense")
        bundle.putString("transactionName", name)
        bundle.putString("category", category)
        bundle.putFloat("amount", amount)
        bundle.putString("goal", goal)
        bundle.putString("date", date)

        //TODO: reset spinner and date to default value
        /* binding.etName.text.clear()
         binding.etAmount.text.clear()
         binding.spinnerCategory.clear()
         binding.spinnerGoal.adapter(null)*/
    }
}