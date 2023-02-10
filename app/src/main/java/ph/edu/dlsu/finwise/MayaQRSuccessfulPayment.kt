package ph.edu.dlsu.finwise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ph.edu.dlsu.finwise.databinding.ActivityMayaQrSuccessfulPaymentBinding

class MayaQRSuccessfulPayment :AppCompatActivity() {

    private lateinit var binding: ActivityMayaQrSuccessfulPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMayaQrSuccessfulPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializes the navbar
        Navbar(findViewById(ph.edu.dlsu.finwise.R.id.bottom_nav), this, ph.edu.dlsu.finwise.R.id.nav_finance)

    }
}