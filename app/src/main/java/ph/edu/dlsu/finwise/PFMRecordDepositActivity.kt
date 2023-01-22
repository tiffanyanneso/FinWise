package ph.edu.dlsu.finwise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordDepositBinding
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordIncomeBinding
import java.text.SimpleDateFormat
import java.util.*

class PFMRecordDepositActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmrecordDepositBinding
    var bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmrecordDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goToConfirmation()
    }

    private fun goToConfirmation() {
        binding.btnConfirm.setOnClickListener {
            setBundle()
            var goToConfirmDeposit = Intent(this, PFMConfirmDepositActivity::class.java)
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

        //Time
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val time = Calendar.getInstance().time
        val current = formatter.format(time)
        var date = current
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