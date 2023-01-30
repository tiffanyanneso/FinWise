package ph.edu.dlsu.finwise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.FragmentSpendingCategoriesBinding


class SpendingCategoriesFragment : Fragment() {

    private lateinit var binding: FragmentSpendingCategoriesBinding
    private var firestore = Firebase.firestore

    private lateinit var decisionMakingActivityID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSpendingCategories()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpendingCategoriesBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private fun getSpendingCategories() {

    }
}