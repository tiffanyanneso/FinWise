package ph.edu.dlsu.finwise

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.WorkManager
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
import ph.edu.dlsu.finwise.parentDashboardModule.ParentDashboardActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import ph.edu.dlsu.finwise.services.FirestoreDataSyncService
import ph.edu.dlsu.finwise.services.GoalNotificationServices
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private var firestore = Firebase.firestore

    private lateinit var alarmManager:AlarmManager

    //private lateinit var workManager: WorkManager

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
                }
                else if (userObject?.userType == "Parent") {
                    startActivity(Intent(this, ParentDashboardActivity::class.java))
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
}