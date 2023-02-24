package ph.edu.dlsu.finwise.profileModule.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ph.edu.dlsu.finwise.databinding.FragmentProfileBadgesBinding
import ph.edu.dlsu.finwise.databinding.FragmentProfileCurrentGoalsBinding


class ProfileBadgesFragment : Fragment() {
    private lateinit var binding: FragmentProfileBadgesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBadgesBinding.inflate(inflater, container, false)
        return binding.root
    }}