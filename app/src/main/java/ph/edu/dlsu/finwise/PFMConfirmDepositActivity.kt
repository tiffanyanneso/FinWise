package ph.edu.dlsu.finwise

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityPfmconfirmDepositBinding
import java.text.SimpleDateFormat
import java.util.*

class PFMConfirmDepositActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmconfirmDepositBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmconfirmDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context=this

        var bundle: Bundle = intent.extras!!
        binding.tvGoal.text = bundle.getString("goalName")
        binding.tvAmount.text = bundle.getFloat("amount").toString()
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val current = formatter.format(time)
        binding.tvDate.text = current

        binding.btnConfirm.setOnClickListener {
            
        }
    }
}