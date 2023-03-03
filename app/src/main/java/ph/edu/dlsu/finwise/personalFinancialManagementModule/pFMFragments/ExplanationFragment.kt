package ph.edu.dlsu.finwise.personalFinancialManagementModule.pFMFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentExplanationBinding

class ExplanationFragment : DialogFragment() {
    private lateinit var binding: FragmentExplanationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explanation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentExplanationBinding.bind(view)
        loadDoneButton()
    }

    private fun loadDoneButton() {
        binding.btnDone.setOnClickListener {
            dismiss()
        }
    }

}