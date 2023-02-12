package ph.edu.dlsu.finwise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ph.edu.dlsu.finwise.databinding.ActivityMayaQrSuccessfulPaymentBinding

class MayaSuccessfulPayment :AppCompatActivity() {

    private lateinit var binding: ActivityMayaQrSuccessfulPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMayaQrSuccessfulPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializes the navbar
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)

    }
}