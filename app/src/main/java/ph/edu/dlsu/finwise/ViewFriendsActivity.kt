package ph.edu.dlsu.finwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.databinding.ActivityEditProfileBinding
import ph.edu.dlsu.finwise.databinding.ActivityViewFriendsBinding

class ViewFriendsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewFriendsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}