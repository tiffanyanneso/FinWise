package ph.edu.dlsu.finwise.parentFinancialManagementModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityParentMayaConfirmPaymentBinding

class ParentMayaConfirmPayment : AppCompatActivity() {
    private lateinit var binding: ActivityParentMayaConfirmPaymentBinding
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentMayaConfirmPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        Navbar(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance)

    }
}