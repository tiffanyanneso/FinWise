package ph.edu.dlsu.finwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityPersonalFinancialManagementBinding
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordExpenseBinding
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordIncomeBinding

class PFMRecordExpenseActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmrecordExpenseBinding
    private var firestore = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPfmrecordExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}