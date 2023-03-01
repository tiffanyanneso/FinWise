package ph.edu.dlsu.finwise.parentFinancialActivitiesModule.earningActivitiesFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ph.edu.dlsu.finwise.databinding.FragmentEarningCompletedBinding
import ph.edu.dlsu.finwise.databinding.FragmentEarningToDoBinding

class EarningToDoFragment : Fragment() {

    private lateinit var binding:FragmentEarningToDoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEarningToDoBinding.inflate(inflater, container, false)
        return binding.root
    }
}