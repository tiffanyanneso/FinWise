package ph.edu.dlsu.finwise

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.adapter.LeaderboardRankingAdapter
import ph.edu.dlsu.finwise.databinding.ActivityLeaderboardBinding
import ph.edu.dlsu.finwise.model.FinancialAssessmentAttempts
import ph.edu.dlsu.finwise.model.Friends

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLeaderboardBinding
    private var friendsUserIDArrayList = ArrayList<String>()
    private var friendRankingArrayList = ArrayList<Ranking>()

    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // and initializes the navbar
        supportActionBar?.hide()
        Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_finance)

        getFriends()
    }

    private fun getFriends() {
        val currentUser  = FirebaseAuth.getInstance().currentUser!!.uid

        firestore.collection("Friends").whereEqualTo("senderID", currentUser).whereEqualTo("status", "Accepted").get().addOnSuccessListener { results ->
            for (friend in results) {
                var request = friend.toObject<Friends>()
                friendsUserIDArrayList.add(request.receiverID.toString())
            }
        }.continueWith {
            firestore.collection("Friends").whereEqualTo("receiverID", currentUser).whereEqualTo("status", "Accepted").get().addOnSuccessListener { results ->
                for (friend in results) {
                    var request = friend.toObject<Friends>()
                    friendsUserIDArrayList.add(request.senderID.toString())
                }
            }
        }.continueWith { getScores() }
    }

    private fun getScores() {
        for (friendUserID in friendsUserIDArrayList) {
            var totalCorrect = 0
            var totalQuestions = 0
            firestore.collection("AssessmentAttempts").whereEqualTo("childID", friendUserID).get().addOnSuccessListener { results ->
                for (assessmentAttempt in results) {
                    var assessmentAttemptObject = assessmentAttempt.toObject<FinancialAssessmentAttempts>()
                    totalCorrect += assessmentAttemptObject.nAnsweredCorrectly!!
                    totalQuestions += assessmentAttemptObject.nQuestions!!
                }
            }.continueWith {
                friendRankingArrayList.add(Ranking(0, friendUserID, totalCorrect.toFloat().div(totalQuestions.toFloat())))
                friendRankingArrayList.sortBy { it.score }
                println("print " + friendRankingArrayList)
            }
        }
    }
    class Ranking (var rank:Int, var childID:String, var score:Float)
}