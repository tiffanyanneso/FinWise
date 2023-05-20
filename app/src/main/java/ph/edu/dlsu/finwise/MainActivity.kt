package ph.edu.dlsu.finwise

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityMainBinding
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.FinancialAssessmentFinlitExpertActivity
import ph.edu.dlsu.finwise.loginRegisterModule.LoginActivity
import ph.edu.dlsu.finwise.loginRegisterModule.ParentRegisterActivity
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentLandingPageActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import ph.edu.dlsu.finwise.services.FirestoreDataSyncService
import ph.edu.dlsu.finwise.services.FirestoreJobService
import ph.edu.dlsu.finwise.services.GoalNotificationServices
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private var firestore = Firebase.firestore

    private lateinit var alarmManager:AlarmManager

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        if (currentUser != null) {
            firestore.collection("Users").document(currentUser!!.uid).get().addOnSuccessListener {
                var userObject = it.toObject<Users>()
                if (userObject?.userType == "Child") {
                    startActivity(Intent(this, PersonalFinancialManagementActivity::class.java))
                    initializeDailyReminderChildNotif()
                    initializeNearDeadlineNotif()
                    scheduleFirestoreSyncJob(this)
                    //saveScores()
                }
                else if (userObject?.userType == "Parent") {
                    startActivity(Intent(this, ParentLandingPageActivity::class.java))
                    firestore.collection("Users").whereEqualTo("parentID", currentUser!!.uid).get().addOnSuccessListener { result ->
                        //there is a child under the parent, send daily reminder for them to check the app
                        if (!result.isEmpty)
                            initializeDailyReminderParentNotif()

                    }
                }
                else if (userObject?.userType == "Financial Expert")
                     startActivity(Intent(this, FinancialAssessmentFinlitExpertActivity::class.java))
            }
        }

        binding.btnLogin.setOnClickListener {
            var login = Intent(this, LoginActivity::class.java)
            startActivity(login)
        }

        binding.btnRegister.setOnClickListener {
            var register = Intent(this, ParentRegisterActivity::class.java)
            startActivity(register)
        }
    }

    private fun initializeNearDeadlineNotif() {
        firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser!!.uid).whereEqualTo("status", "Ongoing").get().addOnSuccessListener { goals ->
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

    fun scheduleFirestoreSyncJob(context: Context) {
        val jobId = 1 // Unique job ID

        val componentName = ComponentName(context, FirestoreJobService::class.java)
        val jobInfo = JobInfo.Builder(jobId, componentName)
            .setRequiresCharging(false) // Set any additional constraints if needed
            .setPersisted(true) // Allow the job to survive device reboots
            .setMinimumLatency(getTimeUntilNextMidnight()) // Set the initial delay until 11:59 PM
            .setPeriodic(AlarmManager.INTERVAL_DAY) // Repeat the job daily
            .build()

        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(jobInfo)
    }

    fun getTimeUntilNextMidnight(): Long {
        val currentTime = System.currentTimeMillis()
        val midnight = (currentTime / 86400000L + 1) * 86400000L // Next midnight timestamp
        return midnight - currentTime
    }

    private fun saveScores() {
        println("print in save main ")
        val intent = Intent(applicationContext, FirestoreDataSyncService::class.java)
        val pendingIntent = PendingIntent.getService(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 50)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}