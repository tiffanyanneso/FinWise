package ph.edu.dlsu.finwise.loginRegisterModule

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentLandingPageActivity
import ph.edu.dlsu.finwise.databinding.ActivityLoginBinding
import ph.edu.dlsu.finwise.financialAssessmentModule.FinancialAssessmentActivity
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.FinancialAssessmentFinlitExpertActivity
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentDashboardModule.ParentDashboardActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import ph.edu.dlsu.finwise.services.FirestoreDataSyncService
import ph.edu.dlsu.finwise.services.FirestoreJobService
import ph.edu.dlsu.finwise.services.FirestoreSyncWorkManager
import ph.edu.dlsu.finwise.services.GoalNotificationServices
import java.util.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    lateinit var email: String
    lateinit var password: String
    private var firestore = Firebase.firestore
    private lateinit var currentUser:String

    private lateinit var alarmManager:AlarmManager

    private lateinit var workManager: WorkManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        forgotPassWord()
        register()
        login()
    }

    private fun login() {
        binding.btnLogin.setOnClickListener {
            if (validateAndSetUserInput()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
                        currentUser  = FirebaseAuth.getInstance().currentUser!!.uid
                        Log.d("xxcxcxcxc", "login: "+currentUser)

                        firestore.collection("Users").document(currentUser).get()
                            .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                // User information already exists in the database
                                var isFirstLogin = true
                                if (documentSnapshot.contains("lastLogin"))
                                    isFirstLogin = false
                                Log.d("zxcvzxcvvc", "login: "+isFirstLogin)
                                updateToken(currentUser)
                                initializeRedirect(documentSnapshot, isFirstLogin)

                            }
                        }
                    } else noAccountFound(task)
                }
            } else {
                Toast.makeText(
                    baseContext, "Please fill up all the fields.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateToken(userID:String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            firestore.collection("Users").document(userID).update("userToken", token)
        }
    }

    private fun noAccountFound(task: Task<AuthResult>) {
        Log.d("xzcxcxz", "noAccountFound: "+task.exception?.message)

        binding.etPassword.error = "Please enter your correct password."
        binding.etPassword.requestFocus()

        binding.etEmail.error = "Please enter your correct email address."
        binding.etEmail.requestFocus()
    }

    private fun initializeRedirect(documentSnapshot: DocumentSnapshot, isFirstLogin: Boolean) {
        val user = documentSnapshot.toObject<Users>()!!
        when (user.userType) {
            "Parent" -> {
                initializeDailyReminderParentNotif()
                goToParentDashboard()
            }
            "Child" -> {
                initializeDailyReminderChildNotif()
                initializeNearDeadlineNotif()
                saveData()
                //scheduleFirestoreSyncJob(this)
                //saveScores()
                goToChild(isFirstLogin)
            }
            "Financial Expert" -> goToFinLitExpert()
        }
    }

    private fun initializeNearDeadlineNotif() {
        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser!!).whereEqualTo("status", "Ongoing").get().addOnSuccessListener { goals ->
            for (goal in goals) {
                var goalObject = goal.toObject<FinancialGoals>()
                val dueDate = goalObject.targetDate!!.toDate()
                val currentDate = Date()
                val daysUntilDue = TimeUnit.DAYS.convert(dueDate.time - currentDate.time, TimeUnit.MILLISECONDS).toInt()
                if ((goalObject.goalLength == "Short" && daysUntilDue <= 2) ||
                    (goalObject.goalLength == "Medium" && daysUntilDue <= 5) ||
                    (goalObject.goalLength == "Long" && daysUntilDue <= 7)){

                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 12)
                        set(Calendar.MINUTE, 0)
                    }
                    var  notificationIntent= Intent(this, GoalNotificationServices::class.java)
                    notificationIntent.putExtra("notificationType", "nearDeadline")
                    var pendingIntent = PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            }
        }
    }

    private fun initializeDailyReminderChildNotif() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
        }

        var  notificationIntent= Intent(this, GoalNotificationServices::class.java)
        notificationIntent.putExtra("notificationType", "recordTransactions")
        var pendingIntent = PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun initializeDailyReminderParentNotif() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
        }

        var  notificationIntent= Intent(this, GoalNotificationServices::class.java)
        notificationIntent.putExtra("notificationType", "checkForUpdatesParent")
        var pendingIntent = PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun saveData() {
        // Initialize WorkManager
        workManager = WorkManager.getInstance(applicationContext)

        // Create a Constraints object to specify requirements for running the worker
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Create a OneTimeWorkRequest for the worker
        val workRequest = OneTimeWorkRequestBuilder<FirestoreSyncWorkManager>()
            .setConstraints(constraints)
            .setInitialDelay(calculateDelay(), TimeUnit.MILLISECONDS)
            .build()

        // Enqueue the work request with WorkManager
        workManager.enqueue(workRequest)
    }

    private fun calculateDelay(): Long {
        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentTime
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 22)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val scheduledTime = calendar.timeInMillis
        return scheduledTime - currentTime
    }

//    fun scheduleFirestoreSyncJob(context: Context) {
//        val jobId = 1 // Unique job ID
//
//        val componentName = ComponentName(context, FirestoreJobService::class.java)
//        val jobInfo = JobInfo.Builder(jobId, componentName)
//            .setRequiresCharging(false) // Set any additional constraints if needed
//            .setPersisted(true) // Allow the job to survive device reboots
//            .setMinimumLatency(getTimeUntilNextMidnight()) // Set the initial delay until 11:59 PM
//            .setPeriodic(AlarmManager.INTERVAL_DAY) // Repeat the job daily
//            .build()
//
//        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
//        jobScheduler.schedule(jobInfo)
//    }
//
//    fun getTimeUntilNextMidnight(): Long {
//        val currentTime = System.currentTimeMillis()
//        val midnight = (currentTime / 86400000L + 1) * 86400000L // Next midnight timestamp
//        return midnight - currentTime
//    }
//
//    private fun saveScores() {
//        println("print in save score llogin")
//        val intent = Intent(applicationContext, FirestoreDataSyncService::class.java)
//        val pendingIntent = PendingIntent.getService(
//            this,
//            0,
//            intent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val calendar = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, 12)
//            set(Calendar.MINUTE, 25)
//        }
//
//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY,
//            pendingIntent
//        )
//    }


    private fun goToChild(isFirstLogin: Boolean) {
        if (isFirstLogin) {
            val intent = Intent(this, FinancialAssessmentActivity::class.java)
            val bundle = Bundle()
            bundle.putString("assessmentType", "Preliminary")
            intent.putExtras(bundle)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, PersonalFinancialManagementActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun goToParentDashboard() {
        val intent = Intent(this, ParentDashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToFinLitExpert() {
        val intent = Intent (this, FinancialAssessmentFinlitExpertActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validateAndSetUserInput(): Boolean {
        var valid = true
        // Check if edit text is empty and valid
        if (binding.etEmail.text.toString().trim().isEmpty()) {
            binding.etEmail.error = "Please enter your email address."
            binding.etEmail.requestFocus()
            valid = false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text).matches()) {
            binding.etEmail.error = "Please enter a valid email address."
            binding.etEmail.requestFocus()
            valid = false
        } else email = binding.etEmail.text.toString().trim()

        // Check if edit text is empty and valid
        if (binding.etPassword.text.toString().trim().isEmpty()) {
            binding.etPassword.error = "Please enter your correct password."
            binding.etPassword.requestFocus()
            valid = false
        } else password = binding.etPassword.text.toString().trim()

        return valid
    }


    private fun forgotPassWord() {
        binding.txtForgotPassword.setOnClickListener {
            val goToForgetPassword = Intent(this, ResetPasswordActivity::class.java)
            startActivity(goToForgetPassword)
        }
    }

    private fun register() {
        binding.txtRegister.setOnClickListener {
            val goToRegister = Intent(this, ParentRegisterActivity::class.java)
            startActivity(goToRegister)
        }
    }
}

