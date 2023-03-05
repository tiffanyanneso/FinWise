package ph.edu.dlsu.finwise.parentFinancialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityNewEarningBinding
import ph.edu.dlsu.finwise.model.ChildUser
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class NewEarningActivity : AppCompatActivity() {

    private lateinit var binding:ActivityNewEarningBinding

    private lateinit var savingActivityID:String
    private lateinit var childID:String

    private var firestore = Firebase.firestore

    private var maxAmount = 0.00F


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewEarningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras!!
        savingActivityID = bundle.getString("savingActivityID").toString()
        childID = bundle.getString("childID").toString()

        initializeDropDownForChores()
        loadBackButton()
        cancel()

        // Initializes the navbar
        NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal)

        binding.etDate.setOnClickListener{
            showCalendar()
        }

        binding.dropdownChore.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
           changeDuration()
        }

        binding.btnConfirm.setOnClickListener {
            var earningActivity = hashMapOf(
                 "activityName" to binding.dropdownChore.text.toString(),
                 "targetDate" to SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString()),
                 "requiredTime" to binding.etDuration.text.toString().toInt(),
                 "amount" to binding.etAmount.text.toString().toFloat(),
                 "childID" to childID,
                "savingActivityID" to savingActivityID,
                "status" to "Ongoing",
                "source" to "Financial Activity"
            )
            firestore.collection("EarningActivities").add(earningActivity).addOnSuccessListener {
                var earning = Intent(this, EarningActivity::class.java)
                var sendBundle = Bundle()
                sendBundle.putString("childID", childID)
                sendBundle.putString("savingActivityID", savingActivityID)
                earning.putExtras(sendBundle)
                startActivity(earning)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeDropDownForChores() {
        var dropdownContent = ArrayList<String>()

        firestore.collection("ChildUser").document(childID).get().addOnSuccessListener {
            var child = it.toObject<ChildUser>()

            //compute age
            val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val from = LocalDate.now()
            val date =  SimpleDateFormat("MM/dd/yyyy").format(child?.birthday?.toDate())
            val to = LocalDate.parse(date.toString(), dateFormatter)
            var difference = Period.between(to, from)


            var age = difference.years
            if (age == 9) {
                maxAmount = 100F
                binding.tvMaxAmount.text = "The max amount that can be given is ₱${DecimalFormat("#0.00").format(maxAmount)}"
            }
            //chores for age 9-12
            dropdownContent.add("Put Away Groceries")
            dropdownContent.add("Put Away Laundry")
            dropdownContent.add("Clean Floor")
            dropdownContent.add("Set Table")

            if (age == 10 || age == 11 || age == 12) {
                maxAmount = 300F
                binding.tvMaxAmount.text = "The max amount that can be given is ₱${DecimalFormat("#0.00").format(maxAmount)}"

                dropdownContent.add("Fold Laundry")
                dropdownContent.add("Help Parent Prepare Meal")
                dropdownContent.add("Prepare Snack")
                dropdownContent.add("Change Bed Sheets")
                dropdownContent.add("Feed Pets")

                if (age == 12) {
                    maxAmount = 500F
                    binding.tvMaxAmount.text = "The max amount that can be given is ₱${DecimalFormat("#0.00").format(maxAmount)}"

                    dropdownContent.add("Mop Floor")
                    dropdownContent.add("Clean Bathroom")
                    dropdownContent.add("Wash Dishes")
                    dropdownContent.add("Wash Car")
                    dropdownContent.add("Prepare Meal")
                    dropdownContent.add("Take Care Of Younger Sibling")}
            }
        }

        // for the dropdown
        val adapter = ArrayAdapter(this, R.layout.list_item, dropdownContent)
        binding.dropdownChore.setAdapter(adapter)
    }

    private fun changeDuration() {
        var chore = binding.dropdownChore.text.toString()

        if (chore == "Put Away Laundry" || chore == "Set Table")
            binding.etDuration.setText("5")
        else if (chore == "Put Away Groceries" || chore == "Clean Floor" || chore == "Fold Laundry" || chore == "Change Bed Sheets" || chore == "Feed Pets" || chore =="Mop Floor")
            binding.etDuration.setText("10")
        else if (chore == "Help Parent Prepare Meal" || chore == "Prepare Snack" || chore == "Wash Dishes")
            binding.etDuration.setText("15")
        else if (chore == "Clean Bathroom" || chore == "Wash Car" || chore == "Prepare Meal" || chore == "Take Care Of Younger Sibling")
            binding.etDuration.setText("30")

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(ph.edu.dlsu.finwise.R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(ph.edu.dlsu.finwise.R.id.et_date)
        calendar.minDate = System.currentTimeMillis()

        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etDate.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun cancel() {
        binding.btnCancel.setOnClickListener {
            val goBack = Intent(applicationContext, EarningActivity::class.java)
            startActivity(goBack)
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}