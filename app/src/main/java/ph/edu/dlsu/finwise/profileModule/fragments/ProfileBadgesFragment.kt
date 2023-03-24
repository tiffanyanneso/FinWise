package ph.edu.dlsu.finwise.profileModule.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.BadgesAdapter
import ph.edu.dlsu.finwise.databinding.FragmentProfileBadgesBinding
import ph.edu.dlsu.finwise.model.GoalRating
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.UserBadges


class ProfileBadgesFragment : Fragment() {
    private lateinit var binding: FragmentProfileBadgesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_badges, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBadgesBinding.bind(view)
        getBadges()
    }

    private fun getBadges() {
        val badgesArrayList = ArrayList<UserBadges>()
        val childID = arguments?.getString("childID").toString()
        val firestore = Firebase.firestore

        firestore.collection("Badges").whereEqualTo("childID", childID)
            .get().addOnSuccessListener { documents ->
                for (badges in documents) {
                    val badge = badges.toObject<UserBadges>()
                    badgesArrayList.add(badge)

                }
                Log.d("xcxccxccccc", "getBadges: "+badgesArrayList)

                badgesArrayList.sortByDescending { it.dateEarned }
                loadRecyclerView(badgesArrayList)
            }
    }

    private fun loadRecyclerView(badgesArrayList: ArrayList<UserBadges>) {
        if (isAdded) {
            val badgeAdapter = BadgesAdapter(badgesArrayList)
            binding.rvBadges.adapter = badgeAdapter
            binding.rvBadges.layoutManager = LinearLayoutManager(requireContext().applicationContext,
                LinearLayoutManager.VERTICAL,
                false)
        }
    }


}