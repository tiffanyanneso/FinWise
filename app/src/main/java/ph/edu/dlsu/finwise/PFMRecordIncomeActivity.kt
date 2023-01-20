package ph.edu.dlsu.finwise

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityChildNewGoalBinding
import ph.edu.dlsu.finwise.databinding.ActivityPersonalFinancialManagementBinding
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordIncomeBinding

class PFMRecordIncomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPfmrecordIncomeBinding
    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPfmrecordIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        
    }
}