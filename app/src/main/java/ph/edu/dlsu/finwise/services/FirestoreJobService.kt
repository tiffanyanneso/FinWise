package ph.edu.dlsu.finwise.services

import android.app.job.JobParameters
import android.app.job.JobService
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirestoreJobService : JobService() {

    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onStartJob(params: JobParameters): Boolean {
        // Perform your data saving task to Firestore here
        CoroutineScope(Dispatchers.Main).launch {
            addDataToFirestore()
        }
        // Return false if the task is completed synchronously in onStartJob(),
        // or true if there is an asynchronous task that needs to be completed in the background.
        return false
    }

    private suspend fun addDataToFirestore() {
        println("print in add data to firestore job service")
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

    override fun onStopJob(params: JobParameters): Boolean {
        // Called when the job is interrupted or needs to be stopped.
        // Return true if you want to reschedule the job or false to drop it.
        return false
    }
}