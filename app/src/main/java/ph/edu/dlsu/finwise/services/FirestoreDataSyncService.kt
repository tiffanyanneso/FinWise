package ph.edu.dlsu.finwise.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirestoreDataSyncService : Service() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate() {
        super.onCreate()
        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.Main).launch {
            println("print in firestore service")
            addDataToFirestore()
        }
        // Return START_NOT_STICKY to indicate that the service should not be restarted if it's killed
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Return null as there is no binding required for this service
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

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