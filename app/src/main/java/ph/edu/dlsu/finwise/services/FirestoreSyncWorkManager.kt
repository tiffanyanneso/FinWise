package ph.edu.dlsu.finwise.services

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirestoreSyncWorkManager (context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun doWork(): Result {
        // TODO: Implement your data-saving logic here (e.g., saving data to Firestore)
        CoroutineScope(Dispatchers.Main).launch {
            println("print in firestore work manager")
            addDataToFirestore()
        }
        return Result.success()
    }

    private suspend fun addDataToFirestore() {
        println("print in add adata to firestore function")
        CoroutineScope(Dispatchers.Main).launch {
            savePfmScore()
            saveFinactScore()
            saveAssessmentScore()
        }
    }

    private suspend fun savePfmScore() {
        var score = hashMapOf(
            "score" to 60,
            "type" to "pfm",
            "dateRecorded" to Timestamp.now()
        )
        firestore.collection("Scores").add(score)
    }

    private suspend fun saveFinactScore() {
        var score = hashMapOf(
            "score" to 60,
            "type" to "finact",
            "dateRecorded" to Timestamp.now()
        )
        firestore.collection("Scores").add(score)
    }

    private suspend fun saveAssessmentScore() {
        var score = hashMapOf(
            "score" to 60,
            "type" to "assessments",
            "dateRecorded" to Timestamp.now()
        )
        firestore.collection("Scores").add(score)
    }
}