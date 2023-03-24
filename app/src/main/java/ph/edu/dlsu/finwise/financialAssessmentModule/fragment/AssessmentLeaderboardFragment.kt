package ph.edu.dlsu.finwise.financialAssessmentModule.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.adapter.LeaderboardRankingAdapter
import ph.edu.dlsu.finwise.databinding.FragmentAssessmentLeaderboardBinding
import ph.edu.dlsu.finwise.model.*


class AssessmentLeaderboardFragment : Fragment() {
    private lateinit var binding: FragmentAssessmentLeaderboardBinding
    private var firestore = Firebase.firestore
    private lateinit var childID: String
    private var childrenIDArrayList = ArrayList<String>()
    private val userScoresArray = ArrayList<ChildUsersWithID>()
    //private lateinit var user: String
    private val assessmentsTaken = ArrayList<FinancialAssessmentAttempts>()
    private var financialGoalsPercentage = 0.00F
    private var financialGoalsScores = ArrayList<Float>()
    private var savingPercentage = 0.00F
    private var savingScores = ArrayList<Float>()
    private var budgetingPercentage = 0.00F
    private var budgetingScores = ArrayList<Float>()
    private var spendingPercentage = 0.00F
    private var spendingScores = ArrayList<Float>()
    private var childObject: Users? = null
    private lateinit var assessmentChildID: String


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
        childrenIDArrayList.add(childID)
        firestore.collection("Friends").whereEqualTo("senderID", childID)
            .get().addOnSuccessListener { documents ->
                for (friend in documents) {
                    val friendVar = friend.toObject<Friends>()
                    if (friendVar.status == "Accepted")
                        friendVar.receiverID?.let { childrenIDArrayList.add(it) }
                }
            }.continueWith { loadData() }
    }

    private fun loadData() {
        CoroutineScope(Dispatchers.Main).launch {
            initializeLeaderboardScores()
        }
    }


    private suspend fun initializeLeaderboardScores() {
        for (friendID in childrenIDArrayList) {
            assessmentsTaken.clear()
            val docSnapshot = getAssessmentAtemps(friendID)
            addScoreToArray(docSnapshot)
            assessmentChildID = friendID
            childObject = getUserObject(friendID)
            getScores()
            computeForPercentages()
        }
        loadProgressBarOfCurrentUser()
        sortLeaderboard()
    }

    private suspend fun getAssessmentAtemps(friendID: String): QuerySnapshot? {
        val docRef = firestore.collection("AssessmentAttempts")
            .whereEqualTo("childID", friendID)
        return docRef.get().await()
    }

    private fun addScoreToArray(docSnapshot: QuerySnapshot?) {
        if (!docSnapshot?.isEmpty!!) {
            for (document in docSnapshot) {
                val assessmentObject = document.toObject<FinancialAssessmentAttempts>()
                assessmentsTaken.add(assessmentObject)
            }
        }
    }

    private suspend fun getUserObject(id: String): Users? {
        val docRef = firestore.collection("Users").document(id)
        val docSnapshot = docRef.get().await()
        return docSnapshot.toObject<Users>()
    }


    private suspend fun getScores() {
        if (assessmentsTaken.isNotEmpty()) {
            financialGoalsScores.clear()
            savingScores.clear()
            budgetingScores.clear()
            spendingScores.clear()
            for (assessment in assessmentsTaken) {
                val documentRef = firestore.collection("Assessments")
                    .document(assessment.assessmentID!!)
                val assessmentDocument = documentRef.get().await()
                val assessmentObject = assessmentDocument.toObject<FinancialAssessmentDetails>()
                val percentage = getPercentage(assessment)
                when (assessmentObject?.assessmentCategory) {
                    "Goal Setting" -> financialGoalsScores.add(percentage)
                    "Saving" -> savingScores.add(percentage)
                    "Budgeting" -> budgetingScores.add(percentage)
                    "Spending" -> spendingScores.add(percentage)
                }
            }
        }

    }

    private fun computeForPercentages() {
        val maxScore = 100
        savingPercentage = (savingScores.sum() / (maxScore * savingScores.size)) * 100
        spendingPercentage = (spendingScores.sum() / (maxScore * spendingScores.size)) * 100
        budgetingPercentage = (budgetingScores.sum() / (maxScore * budgetingScores.size)) * 100
        financialGoalsPercentage = (financialGoalsScores.sum() / (maxScore * financialGoalsScores.size)) * 100
        checkIfNaN()
        computePerformance()
        //setRanking()
    }

    private fun checkIfNaN() {
        val percentages = mutableListOf(savingPercentage, spendingPercentage, budgetingPercentage,
            financialGoalsPercentage)

        for (i in percentages.indices) {
            if (percentages[i].isNaN()) {
                when (i) {
                    0 -> savingPercentage = 0.00f
                    1 -> spendingPercentage = 0.00f
                    2 -> budgetingPercentage = 0.00f
                    3 -> financialGoalsPercentage = 0.00f
                }
            }
        }
    }


    private fun computePerformance() {
        val totalSum = spendingPercentage + savingPercentage + financialGoalsPercentage + budgetingPercentage
        val maxPossibleSum = 4 * 100  // assuming the maximum possible value for each variable is 100

        val percentage = (totalSum.toDouble() / maxPossibleSum) * 100



        userScoresArray.add(ChildUsersWithID(childObject, assessmentChildID, percentage))
    }

    private fun getPercentage(assessment: FinancialAssessmentAttempts): Float {
        val percentage = if (assessment.nAnsweredCorrectly!! > 0) {
            (assessment.nAnsweredCorrectly!!.toFloat() / assessment.nQuestions!!.toFloat()) * 100
        } else {
            0.0
        }
        return percentage.toFloat()
    }

    private fun loadProgressBarOfCurrentUser() {
        if (userScoresArray.isNotEmpty()) {
            binding.progressBar.progress = userScoresArray[0].assessmentPercentage?.toInt()!!
            binding.textViewProgress.text =
                String.format("%.1f%%", userScoresArray[0].assessmentPercentage)
        }
    }

    private fun sortLeaderboard() {
        val sortedChildScoresObject = userScoresArray
            .sortedByDescending { it.assessmentPercentage}

        val rankedFriends = sortedChildScoresObject.mapIndexed { index, value ->
            FriendRanking(index + 1, value)
        }

        setRankOfChildUser(rankedFriends)

        loadRecyclerView(rankedFriends)
    }

    private fun setRankOfChildUser(rankedFriends: List<FriendRanking>) {
        lateinit var message: String
        if (rankedFriends.size == 1)
            message = ""
        else {
            val result = rankedFriends.find { it.childUsers.id == childID }
            message = "You are rank ${result?.rank}!"
        }
        binding.tvRank.text = message
    }

    private fun loadRecyclerView(rankedFriends: List<FriendRanking>) {
        val leaderboardAdapter = LeaderboardRankingAdapter(rankedFriends)
        binding.rvRanking.adapter = leaderboardAdapter
        binding.rvRanking.layoutManager = LinearLayoutManager(requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false)
    }

    private fun getBundles() {
        childID = arguments?.getString("childID").toString()
        //user = arguments?.getString("user").toString()
    }

    data class ChildUsersWithID(val childUsersFilter: Users? = null, val id: String? = null,
                                val assessmentPercentage: Double? = null)
    data class FriendRanking(val rank: Int, val childUsers: ChildUsersWithID)


}

