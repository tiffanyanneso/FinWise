package ph.edu.dlsu.finwise.financialAssessmentModule.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.LeaderboardRankingAdapter
import ph.edu.dlsu.finwise.databinding.FragmentAssessmentLeaderboardBinding
import ph.edu.dlsu.finwise.model.*


class AssessmentLeaderboardFragment : Fragment() {
    private lateinit var binding: FragmentAssessmentLeaderboardBinding
    private var firestore = Firebase.firestore
    private lateinit var childID: String
    private var friendsIDArrayList = ArrayList<String>()
    private val childFriendFilterArray = ArrayList<ChildUsersWithID?>()
    //private lateinit var user: String

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

        getBundles()
        loadLeaderboard()
    }

    private fun loadLeaderboard() {

        firestore.collection("Friends").whereEqualTo("senderID", childID)
            .get().addOnSuccessListener { documents ->
                for (friend in documents) {
                    val friendVar = friend.toObject<Friends>()
                    if (friendVar.status == "Accepted")
                        friendVar.receiverID?.let { friendsIDArrayList.add(it) }
                }
            }.continueWith { loadCoroutine() }
    }

    private fun loadCoroutine() {
        CoroutineScope(Dispatchers.Main).launch {
            initializeFriendScores()
        }
    }

    private suspend fun initializeFriendScores() {
        for (friendID in friendsIDArrayList) {
            val docRef = firestore.collection("ChildUser").document(friendID)
            val docSnapshot = docRef.get().await()

            if (docSnapshot.exists()) {
                val child = docSnapshot.toObject<ChildUser>()
                childFriendFilterArray.add(ChildUsersWithID(child, docSnapshot.id))
            }
        }
        addCurrentChildScore()


    }


    private fun addCurrentChildScore() {
        firestore.collection("Users").document(childID)
            .get().addOnSuccessListener { document ->
                val child = document.toObject<Users>()
                childFriendFilterArray.add(ChildUsersWithID(child, document.id))
                loadProgressBar(child)
            }.continueWith { sortLeaderboard() }
    }

    private fun loadProgressBar(child: Users?) {
        binding.progressBar.progress = child?.assessmentPerformance!!.toInt()
        binding.textViewProgress.text =
            String.format("%.1f%%", child.assessmentPerformance)
    }

    private fun sortLeaderboard() {
        val sortedChildScoresObject = childFriendFilterArray
            .sortedByDescending { it?.childUsersFilter?.assessmentPerformance}
        Log.d("aassss", "initializeFriendScores: "+sortedChildScoresObject[1]?.childUsersFilter?.assessmentPerformance)

        val rankedFriends = sortedChildScoresObject.mapIndexed { index, value ->
            FriendRanking(index + 1, value!!)
        }

        loadRecyclerView(rankedFriends)
    }

    private fun loadRecyclerView(rankedFriends: List<FriendRanking>) {
        val leaderboardAdapter = LeaderboardRankingAdapter(requireActivity(), rankedFriends)
        binding.rvRanking.adapter = leaderboardAdapter
        binding.rvRanking.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
    }

    private fun getBundles() {
        childID = arguments?.getString("childID").toString()
        //user = arguments?.getString("user").toString()
    }

    data class ChildUsersWithID(val childUsersFilter: Users? = null, val id: String)
    data class FriendRanking(val rank: Int, val childUsers: ChildUsersWithID)


}
