package ph.edu.dlsu.finwise

import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentLandingPageActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import java.util.*
import java.util.concurrent.TimeUnit

class GoalNotificationServices : IntentService("GoalNotificationServices") {

    override fun onHandleIntent(intent: Intent?) {
        var notificationType = intent?.getStringExtra("notificationType")

        when (notificationType) {
            "nearDeadline" -> nearDeadlineNotif()
            "recordTransactions" -> recordTransactionsNotif()
            "checkForUpdatesParent" -> parentCheckUpdates()
        }
    }

    private fun nearDeadlineNotif() {
        var intent = Intent(this, FinancialActivities::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        var builder = NotificationCompat.Builder(this, "finwise")
            .setSmallIcon(R.drawable.peso_coin)
            .setContentTitle("Your goal is nearing its deadline")
            .setContentText("Come back and check up on your progress")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with (NotificationManagerCompat.from(this)) {
            notify(100, builder.build())
        }
    }

    private fun recordTransactionsNotif() {
        var intent = Intent(this, PersonalFinancialManagementActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        var builder = NotificationCompat.Builder(this, "finwise")
            .setSmallIcon(R.drawable.peso_coin)
            .setContentTitle("Come record your financial transactions")
            .setContentText("Don't forget to record your financial transactions for the day!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with (NotificationManagerCompat.from(this)) {
            notify(100, builder.build())
        }
    }

    private fun parentCheckUpdates() {
        var intent = Intent(this, ParentLandingPageActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        var builder = NotificationCompat.Builder(this, "finwise")
            .setSmallIcon(R.drawable.peso_coin)
            .setContentTitle("Check for updates!")
            .setContentText("Don't forget to check your child's goals and earning activities")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with (NotificationManagerCompat.from(this)) {
            notify(100, builder.build())
        }
    }}