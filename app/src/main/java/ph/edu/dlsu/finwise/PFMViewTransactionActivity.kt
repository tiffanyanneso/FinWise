package ph.edu.dlsu.finwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordIncomeBinding
import ph.edu.dlsu.finwise.databinding.ActivityPfmviewTransactionBinding

class PFMViewTransactionActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmviewTransactionBinding
    var bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmviewTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadTransactionDetails()
    }

    private fun loadTransactionDetails() {

    }
}