package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityRecordEarningSaleBinding
import ph.edu.dlsu.finwise.model.FinancialActivities
import ph.edu.dlsu.finwise.model.FinancialGoals
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.NewEarningActivity
import java.text.SimpleDateFormat

class RecordEarningSaleActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRecordEarningSaleBinding
    private var firestore = Firebase.firestore

    private var goalDropDownArrayList = ArrayList<NewEarningActivity.GoalDropDown>()
    private lateinit var goalAdapter: ArrayAdapter<String>

    private lateinit var savingActivityID:String

    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordEarningSaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadButtons()
        initializeDropDowns()
        setNavigationBar()

        var bundle = intent.extras!!
        var childID = bundle.getString("childID").toString()

        binding.etDate.setText(SimpleDateFormat("MM/dd/yyyy").format(Timestamp.now().toDate()))
        binding.etDate.setOnClickListener { showCalendar() }

        binding.dropdownDestination.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if (binding.dropdownDestination.text.toString() == "Personal Finance")
                binding.containerGoal.visibility = View.GONE
            else if (binding.dropdownDestination.text.toString() == "Financial Goal")
                binding.containerGoal.visibility = View.VISIBLE
        }

        binding.dropdownGoal.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            savingActivityID = goalDropDownArrayList[position].savingActivityID
        }

        binding.btnConfirm.setOnClickListener {
            var confirmSale = Intent(this, RecordEarningSaleConfirmationActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("childID", childID)
            sendBundle.putString("saleName", binding.etName.text.toString())
            sendBundle.putSerializable("saleDate", SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString()))
            sendBundle.putFloat("saleAmount", binding.etAmount.text.toString().toFloat())
            sendBundle.putString("depositTo", binding.dropdownDestination.text.toString())
            if (binding.dropdownDestination.text.toString() == "Financial Goal")
                sendBundle.putString("savingActivityID", savingActivityID)
            confirmSale.putExtras(sendBundle)
            startActivity(confirmSale)
        }
    }


    private fun initializeDropDowns() {
        val incomeDestinationAdapter = ArrayAdapter(this, R.layout.list_item, resources.getStringArray(R.array.income_destination))
        binding.dropdownDestination.setAdapter(incomeDestinationAdapter)

        firestore.collection("FinancialActivities").whereEqualTo("childID", currentUser).whereEqualTo("financialActivityName", "Saving").whereEqualTo("status", "In Progress").get().addOnSuccessListener { activityResults ->
            for (saving in activityResults) {
                firestore.collection("FinancialGoals").document(saving.toObject<FinancialActivities>().financialGoalID!!).get().addOnSuccessListener { goal ->
                    goalDropDownArrayList.add(
                        NewEarningActivity.GoalDropDown(
                            saving.id,
                            goal.toObject<FinancialGoals>()?.goalName!!
                        )
                    )
                }.continueWith {
                    goalAdapter = ArrayAdapter(this, R.layout.list_item, goalDropDownArrayList.map { it.goalName })
                    binding.dropdownGoal.setAdapter(goalAdapter)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(R.id.et_date)
        calendar.maxDate = System.currentTimeMillis()

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun loadButtons() {
        loadBackButton()
    }

    private fun setNavigationBar() {

        var navUser = FirebaseAuth.getInstance().currentUser!!.uid
        firestore.collection("ParentUser").document(navUser).get().addOnSuccessListener {

            val bottomNavigationViewChild = binding.bottomNav
            val bottomNavigationViewParent = binding.bottomNavParent

            if (it.exists()) {
                bottomNavigationViewChild.visibility = View.GONE
                bottomNavigationViewParent.visibility = View.VISIBLE
                NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal)
            } else  {
                bottomNavigationViewChild.visibility = View.VISIBLE
                bottomNavigationViewParent.visibility = View.GONE
                Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
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