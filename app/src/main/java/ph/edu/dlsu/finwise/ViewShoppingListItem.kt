package ph.edu.dlsu.finwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.finwise.databinding.ActivityNewShoppingListItemBinding
import ph.edu.dlsu.finwise.databinding.ActivityViewShoppingListItemBinding

class ViewShoppingListItem : AppCompatActivity() {

    private lateinit var binding: ActivityViewShoppingListItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewShoppingListItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}