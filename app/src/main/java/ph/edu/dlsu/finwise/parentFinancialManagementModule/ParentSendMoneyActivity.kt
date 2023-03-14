package ph.edu.dlsu.finwise.parentFinancialManagementModule

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.Navbar
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityParentSendMoneyBinding
import ph.edu.dlsu.finwise.model.Users
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ParentSendMoneyActivity :AppCompatActivity () {
    private lateinit var binding: ActivityParentSendMoneyBinding
    private lateinit var context: Context
    private var firestore = Firebase.firestore
    private var bundle = Bundle()
    private lateinit var name: String
    private lateinit var childID: String
    private lateinit var amount: String
    private lateinit var note: String
    private lateinit var phone: String
    private lateinit var date: Date
    private var childrenArray = ArrayList<String>()
    private var childrenIDArray = ArrayList<String>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentSendMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        // Initializes the navbar
        Navbar(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_finance)
        initializeDropdown()
        goToMayaQRConfirmPayment()
        cancel()
        loadBackButton()
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun goToMayaQRConfirmPayment(){
        binding.btnConfirm.setOnClickListener {
            if (validateAndSetUserInput()) {
                setBundle()

                val goToMayaConfirmPayment = Intent(applicationContext, ParentMayaConfirmPayment::class.java)
                goToMayaConfirmPayment.putExtras(bundle)
                goToMayaConfirmPayment.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(goToMayaConfirmPayment)
            } else {
                Toast.makeText(
                    baseContext, "Please fill up the form correctly.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setBundle() {
        //getCurrentTime()
//        val goal = binding.spinnerGoal.selectedItem.toString()
        bundle.putString("name", name)
        bundle.putFloat("amount", amount.toFloat())
        bundle.putString("phone", phone)
        bundle.putString("note", note)
        bundle.putString("childID", childID)
        //bundle.putFloat("balance", balance)
//        bundle.putString("goal", goal)
        bundle.putSerializable("date", date)

        //TODO: reset spinner and date to default value
        /* binding.etName.text.clear()
         binding.etAmount.text.clear()
         binding.spinnerCategory.clear()
         binding.spinnerGoal.adapter(null)*/
    }


    /*private fun validAmount(): Boolean {
        val bundle2 = intent.extras!!
        balance = bundle2.getFloat("balance")
        Toast.makeText(this, "balance"+balance, Toast.LENGTH_SHORT).show()

        //trying to deposit more than their current balance
        if (binding.etAmount.text.toString().toFloat() > balance) {
            binding.etAmount.error =
                "You cannot deposit more than your current balance of ₱$balance"
            binding.etAmount.requestFocus()
            return false
        }
        else
            return true
    }*/


    @RequiresApi(Build.VERSION_CODES.O)
    private fun validateAndSetUserInput(): Boolean {
        var valid = true
        // Check if edit text is empty and valid
        if (binding.etChildName.text.toString().trim().isEmpty()) {
            binding.etChildName.error = "Please enter the name of the child."
            binding.etChildName.requestFocus()
            valid = false
        } else {
            binding.etChildName.error = null
            name = binding.etChildName.text.toString().trim()
        }

        getChildID()

        note = binding.etNote.text.toString().trim()

        if (binding.etAmount.text.toString().trim().isEmpty()) {
            binding.etAmount.error = "Please enter the amount."
            binding.etAmount.requestFocus()
            valid = false
        } else amount = binding.etAmount.text.toString().trim()


        if (binding.etPhone.text.toString().trim().isEmpty() || binding.etPhone.text?.length!! < 11) {
            binding.etPhone.error = "Please enter the right 11 digit Phone Number."
            binding.etPhone.requestFocus()
            valid = false
        } else phone = binding.etPhone.text.toString().trim()

        getCurrentTime()

        return valid
    }

    private fun getChildID() {
        val index = childrenArray.indexOf(name)
        childID = childrenIDArray[index]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentTime() {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy")
        val currentDate = Date()
        date = dateFormat.parse(dateFormat.format(currentDate)) as Date
    }



    private fun initializeDropdown() {
        // TODO: set parent user id curent session
        val parentID = "HeiVgm55YmgBq2DjjRh1OT9bFnP2"
        firestore.collection("Users").whereEqualTo("parentID", parentID).get()
            .addOnSuccessListener {documents ->
                for (d in documents) {
                    val child = d.toObject<Users>()
                    childrenArray.add(child.firstName+" "+child.lastName)
                    childrenIDArray.add(d.id)
                }

                val adapter = ArrayAdapter (this, R.layout.list_item, childrenArray)
                binding.etChildName.setAdapter(adapter)
            }

        /* val items = resources.getStringArray(R.array.pfm_income_category)
         val adapter = ArrayAdapter (this, R.layout.list_item, items)
         binding.dropdownName.setAdapter(adapter)*/


    }




    private fun cancel() {
        binding.btnCancel.setOnClickListener {
            onBackPressed()
        }
    }
}


