package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityChildNewGoalBinding
import ph.edu.dlsu.finwise.databinding.ActivityPersonalFinancialManagementBinding
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordIncomeBinding

class PFMRecordIncomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmrecordIncomeBinding
    private var firestore = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmrecordIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //next()
    }

    private fun next() {
        var goToConfirmTransaction = Intent(this, PFMConfirmTransactionActivity::class.java)
        var bundle = Bundle()

        var name =  binding.etName.text.toString()
        var category = binding.spinnerCategory.selectedItem.toString()
        var amount = binding.etAmount.text.toString().toFloat()
        var goal = binding.spinnerGoal.selectedItem.toString()
        bundle.putString("transactionName", name)
        bundle.putString("category", category)
        bundle.putFloat("amount", amount)
        bundle.putString("goal", goal)

        //TODO: reset spinner and date to default value
        binding.etName.text.clear()
        binding.etAmount.text.clear()
        /*binding.spinnerCategory.clear()
        binding.spinnerGoal.adapter(null)*/

        goToConfirmTransaction.putExtras(bundle)
        goToConfirmTransaction.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.startActivity(goToConfirmTransaction)
    }


}