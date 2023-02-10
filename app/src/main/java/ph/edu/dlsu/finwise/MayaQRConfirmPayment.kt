package ph.edu.dlsu.finwise

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ph.edu.dlsu.finwise.databinding.ActivityMayaQrConfirmPaymentBinding

class MayaQRConfirmPayment : AppCompatActivity() {

    private lateinit var binding: ActivityMayaQrConfirmPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMayaQrConfirmPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializes the navbar
        Navbar(findViewById(ph.edu.dlsu.finwise.R.id.bottom_nav), this, ph.edu.dlsu.finwise.R.id.nav_finance)


        goToMayaQRSuccessfulPayment()
    }

    private fun goToMayaQRSuccessfulPayment(){
        binding.btnConfirm.setOnClickListener(){
            val goToMayaQRSuccessfulPayment = Intent(applicationContext, MayaQRSuccessfulPayment::class.java)
            var sendBundle = Bundle()
            goToMayaQRSuccessfulPayment.putExtras(sendBundle)
            startActivity(goToMayaQRSuccessfulPayment)
        }
    }
}