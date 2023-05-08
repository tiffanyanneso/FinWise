package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityEditGoalBinding
import ph.edu.dlsu.finwise.databinding.ActivityGoalSettingEditGoalBinding
import ph.edu.dlsu.finwise.databinding.DialogDeleteGoalWarningBinding
import ph.edu.dlsu.finwise.model.ChildWallet
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Transactions
import ph.edu.dlsu.finwise.model.Users
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

class GoalSettingEditGoalActivity : AppCompatActivity() {
    private lateinit var binding : ActivityGoalSettingEditGoalBinding
    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private lateinit var financialGoalID:String
    private lateinit var savingActivityID:String
    private lateinit var targetDate: Date

    private var maxAmount = 0.00F

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalSettingEditGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        savingActivityID = bundle.getString("savingActivityID").toString()
        checkAge()
        getFinancialGoal()
        checkUser()


        // for the dropdown
        val items = resources.getStringArray(R.array.financial_activity)
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        binding.dropdownActivity.setAdapter(adapter)

        binding.etTargetDate.setOnClickListener{
            showCalendar()
        }

        binding.btnSave.setOnClickListener {
            updateGoal()
        }

        binding.btnDelete.setOnClickListener {
            confirmDeleteGoal()
        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToGoal = Intent(applicationContext, ViewGoalActivity::class.java)
            goToGoal.putExtras(bundle)
            goToGoal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(goToGoal)
        }

    }

    private fun getFinancialGoal() {
        firestore.collection("FinancialGoals").document(financialGoalID).get()
            .addOnSuccessListener {
                var goalObject = it.toObject<FinancialGoals>()
                binding.etGoal.setText(goalObject?.goalName.toString())
                binding.etAmount.setText(goalObject?.targetAmount?.toInt().toString())
                binding.containerActivity.hint = goalObject?.financialActivity.toString()
                targetDate = goalObject?.targetDate?.toDate()!!
                binding.etTargetDate.setText(SimpleDateFormat("MM/dd/yyyy").format(goalObject?.targetDate?.toDate()).toString())
            }
    }

    private fun updateGoal() {
        var targetDate = SimpleDateFormat("MM/dd/yyyy").parse(binding.etTargetDate.text.toString())
        firestore.collection("FinancialGoals").document(financialGoalID).update("goalName", binding.etGoal.text.toString(),
            "targetAmount", binding.etAmount.text.toString().toFloat(), "targetDate", targetDate, "status", "For Review").addOnSuccessListener {
            Toast.makeText(this, "Goal has been updated", Toast.LENGTH_LONG).show()
            var goalList = Intent(this, WaitGoalReview::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("financialGoalID", financialGoalID)
            goalList.putExtras(sendBundle)
            startActivity(goalList)
            finish()
        }

    }

    private fun confirmDeleteGoal() {
        var dialogBinding= DialogDeleteGoalWarningBinding.inflate(getLayoutInflater())
        var dialog= Dialog(this);
        dialog.setContentView(dialogBinding.getRoot())

        dialog.window!!.setLayout(800, 1000)

        dialogBinding.btnOk.setOnClickListener {
            deleteGoal()
            dialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun deleteGoal() {
        //mark financial goal as deleted
        firestore.collection("FinancialGoals").document(financialGoalID).update("status", "Deleted", "currentSavings", 0).addOnSuccessListener {
            //mark related activities as deleted
            firestore.collection("FinancialActivities").whereEqualTo("financialGoalID", financialGoalID).get().addOnSuccessListener { results ->
                for (activity in results )
                    firestore.collection("FinancialActivities").document(activity.id).update("status", "Deleted")
                var goalList = Intent(this, FinancialActivity::class.java)
                startActivity(goalList)
                finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAge() {
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            var child = it.toObject<Users>()

            //compute age
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date = SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)

            var age = difference.years
            if (age == 9)
                maxAmount = 3000F
            else if (age == 10 || age == 11)
                maxAmount = 5000F
            else
                maxAmount = 10000F

            binding.tvMaxAmount.text = "The max amount that can be set is ₱${DecimalFormat("#,##0.00").format(maxAmount)}"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(R.id.et_date)
        calendar.minDate = System.currentTimeMillis()

        dialog.show()
        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etTargetDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
    }

    private fun checkUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener {
            var user  = it.toObject<Users>()!!
            //current user is a child
            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent
            if (user.userType == "Child") {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
            } else  if (user.userType == "Parent"){
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                //sends the ChildID to the parent navbar
                val bundle = intent.extras!!
                val childID = bundle.getString("childID").toString()
                val bundleNavBar = Bundle()
                bundleNavBar.putString("childID", childID)
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal, bundleNavBar)
            }

        }
    }
}