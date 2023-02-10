package ph.edu.dlsu.finwise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.databinding.ActivityMayaPaymentBinding



class MayaPayment : AppCompatActivity() {

    private lateinit var binding: ActivityMayaPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMayaPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializes the navbar
        Navbar(findViewById(ph.edu.dlsu.finwise.R.id.bottom_nav), this, ph.edu.dlsu.finwise.R.id.nav_finance)

        goToMayaQRConfirmPayment()
    }

    private fun goToMayaQRConfirmPayment(){
        binding.btnConfirm.setOnClickListener(){
            val goToMayaQRConfirmPayment = Intent(applicationContext, MayaQRConfirmPayment::class.java)
            var sendBundle = Bundle()
            goToMayaQRConfirmPayment.putExtras(sendBundle)
            startActivity(goToMayaQRConfirmPayment)
        }
    }

}