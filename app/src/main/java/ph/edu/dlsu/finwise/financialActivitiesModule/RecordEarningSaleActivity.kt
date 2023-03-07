package ph.edu.dlsu.finwise.financialActivitiesModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.NavbarParent
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityRecordEarningSaleBinding
import java.text.SimpleDateFormat

class RecordEarningSaleActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRecordEarningSaleBinding

    private var user = "child"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordEarningSaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadButtons()

        var bundle = intent.extras!!
        var childID = bundle.getString("childID").toString()
        var savingActivityID = bundle.getString("savingActivityID").toString()

        binding.etDate.setOnClickListener {
            showCalendar()
        }

        binding.btnConfirm.setOnClickListener {
            var confirmSale = Intent(this, RecordEarningSaleConfirmationActivity::class.java)
            var sendBundle = Bundle()
            sendBundle.putString("childID", childID)
            sendBundle.putString("savingActivityID", savingActivityID)
            sendBundle.putString("saleName", binding.etName.text.toString())
            sendBundle.putSerializable("saleDate", SimpleDateFormat("MM/dd/yyyy").parse(binding.etDate.text.toString()))
            sendBundle.putFloat("saleAmount", binding.etAmount.text.toString().toFloat())
            confirmSale.putExtras(sendBundle)
            startActivity(confirmSale)
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
        setNavigationBar()
        loadBackButton()
    }

    private fun setNavigationBar() {
        val bottomNavigationViewChild = binding.bottomNav
        val bottomNavigationViewParent = binding.bottomNavParent

        if (user == "child") {
            bottomNavigationViewChild.visibility = View.VISIBLE
            bottomNavigationViewParent.visibility = View.GONE
            Navbar(findViewById(R.id.bottom_nav), this, R.id.nav_goal)
        }
        else {
            bottomNavigationViewChild.visibility = View.GONE
            bottomNavigationViewParent.visibility = View.VISIBLE
            NavbarParent(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_goal)
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}