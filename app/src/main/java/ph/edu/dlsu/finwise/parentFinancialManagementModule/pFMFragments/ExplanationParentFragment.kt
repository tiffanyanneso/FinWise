package ph.edu.dlsu.finwise.parentFinancialManagementModule.pFMFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentExplanationBinding
import ph.edu.dlsu.finwise.databinding.FragmentExplanationParentBinding

class ExplanationParentFragment : DialogFragment() {
    private lateinit var binding: FragmentExplanationParentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explanation_parent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentExplanationParentBinding.bind(view)
       loadDoneButton()
        setDialogSize()
    }

    private fun setDialogSize() {
        val width = resources.getDimensionPixelSize(R.dimen.explanation_popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.explanation_popup_height)
        dialog!!.window!!.setLayout(width, height)
    }

   private fun loadDoneButton() {
        binding.btnDone.setOnClickListener {
            dismiss()
        }
    }

}