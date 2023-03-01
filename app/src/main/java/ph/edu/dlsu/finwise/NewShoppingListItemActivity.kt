package ph.edu.dlsu.finwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.databinding.ActivityNewShoppingListItemBinding
import ph.edu.dlsu.finwise.databinding.ActivityPfmrecordExpenseBinding

class NewShoppingListItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewShoppingListItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewShoppingListItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}