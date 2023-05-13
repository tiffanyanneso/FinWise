package ph.edu.dlsu.finwise

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityAddFriendsBinding
import ph.edu.dlsu.finwise.databinding.ActivityMainBinding
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.FinancialAssessmentFinlitExpertActivity
import ph.edu.dlsu.finwise.loginRegisterModule.LoginActivity
import ph.edu.dlsu.finwise.loginRegisterModule.ParentRegisterActivity
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.parentFinancialManagementModule.ParentFinancialManagementActivity
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, GoalNotificationServices::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 31)
        }


        if (currentUser != null) {
            firestore.collection("FinancialGoals").whereEqualTo("childID", currentUser!!.uid).whereEqualTo("status", "Ongoing").get().addOnSuccessListener { goals ->
                for (goal in goals) {
                    var goalObject = goal.toObject<FinancialGoals>()
                    val dueDate = goalObject.targetDate!!.toDate()
                    val currentDate = Date()
                    val daysUntilDue = TimeUnit.DAYS.convert(dueDate.time - currentDate.time, TimeUnit.MILLISECONDS).toInt()
                    println("print until due " + daysUntilDue)
                    if ((goalObject.goalLength == "Short" && daysUntilDue == 2) ||
                        (goalObject.goalLength == "Medium" && daysUntilDue == 5) ||
                        (goalObject.goalLength == "Long" && daysUntilDue == 7)){
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    }
                }
            }
        }

//        if (currentUser != null) {
//            firestore.collection("Users").document(currentUser!!.uid).get().addOnSuccessListener {
//                var userObject = it.toObject<Users>()
//                if (userObject?.userType == "Child")
//                    startActivity(Intent(this, PersonalFinancialManagementActivity::class.java))
//                else if (userObject?.userType == "Parent")
//                    startActivity(Intent(this, ParentFinancialManagementActivity::class.java))
//                else if (userObject.userType == "Financial Expert")
//                     startActivity(Intent(this, FinancialAssessmentFinlitExpertActivity::class.java))
//            }
//        }

        binding.btnLogin.setOnClickListener {
            var login = Intent(this, LoginActivity::class.java)
            startActivity(login)
        }

        binding.btnRegister.setOnClickListener {
            var register = Intent(this, ParentRegisterActivity::class.java)
            startActivity(register)
        }
    }
}