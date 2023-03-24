package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityReviewUpdateGoalBinding
import ph.edu.dlsu.finwise.databinding.DialogDeleteGoalWarningBinding
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.model.Users
import java.text.SimpleDateFormat
import java.util.*

class UpdateGoalActivity: AppCompatActivity() {
    private lateinit var binding : ActivityReviewUpdateGoalBinding
    private var firestore = Firebase.firestore

    private lateinit var financialGoalID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewUpdateGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkUser()
        loadBackButton()

        var bundle = intent.extras!!
        financialGoalID = bundle.getString("financialGoalID").toString()
        setFields()

        binding.etTargetDate.setOnClickListener {
            showCalendar()
        }

        binding.btnSave.setOnClickListener {
            firestore.collection("FinancialGoals").document(financialGoalID).update("goalName", binding.etGoal.text.toString(),
            "targetAmount", binding.etAmount.text.toString().toFloat(), "targetDate", SimpleDateFormat("MM/dd/yyyy").parse(binding.etTargetDate.text.toString()),
            "status", "For Review").addOnSuccessListener {
                var goalDetails = Intent(this, ViewGoalDetailsTabbedActivity::class.java)
                var sendBundle = Bundle()
                sendBundle.putString("financialGoalID", financialGoalID)
                goalDetails.putExtras(sendBundle)
                startActivity(goalDetails)
            }
        }
    }

    private fun setFields() {
        firestore.collection("FinancialGoals").document(financialGoalID).get().addOnSuccessListener {
            var goal = it.toObject<FinancialGoals>()
            binding.etGoal.setText(goal?.goalName.toString())
            binding.dropdownActivity.setText(goal?.financialActivity)
            binding.etAmount.setText(goal?.targetAmount!!.toInt().toString())
            binding.etTargetDate.setText(SimpleDateFormat("MM/dd/yyyy").format(goal?.targetDate!!.toDate()))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(R.id.et_date)
        calendar.minDate = System.currentTimeMillis()

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etTargetDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
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

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}