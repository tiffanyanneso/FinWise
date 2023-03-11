package ph.edu.dlsu.finwise.financialAssessmentModule.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.FragmentAssessmentLeaderboardBinding
import ph.edu.dlsu.finwise.databinding.FragmentBalanceChartBinding


class AssessmentLeaderboardFragment : Fragment() {
    private lateinit var binding: FragmentAssessmentLeaderboardBinding
    private var bundle = Bundle()
    private var firestore = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assessment_leaderboard, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAssessmentLeaderboardBinding.bind(view)

    }

}