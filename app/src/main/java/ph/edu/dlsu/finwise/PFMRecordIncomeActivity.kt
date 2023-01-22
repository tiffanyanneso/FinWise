package ph.edu.dlsu.finwise

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordIncomeBinding
import java.text.SimpleDateFormat
import java.util.*

class PFMRecordIncomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmrecordIncomeBinding
    var bundle = Bundle()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmrecordIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goToConfirmation()
    }

    private fun goToConfirmation() {
        binding.btnConfirm.setOnClickListener {
            setBundle()
            var goToConfirmTransaction = Intent(this, PFMConfirmTransactionActivity::class.java)
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
        bundle.putString("transactionType", "income")
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