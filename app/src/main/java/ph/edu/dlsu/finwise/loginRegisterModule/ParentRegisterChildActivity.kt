package ph.edu.dlsu.finwise.loginRegisterModule

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentLandingPageActivity
import ph.edu.dlsu.finwise.databinding.ActivityParentRegisterChildBinding
import java.text.SimpleDateFormat
import java.util.*

class ParentRegisterChildActivity : AppCompatActivity() {

    private lateinit var binding : ActivityParentRegisterChildBinding
    private var firestore = Firebase.firestore

    lateinit var firstName : String
    lateinit var lastName : String
    lateinit var email : String
    lateinit var contactNumber : String
    lateinit var password : String
    lateinit var username : String
    lateinit var birthday : String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentRegisterChildBinding.inflate(layoutInflater)
        setContentView(binding.root)

        save()
        setCancel()
        loadBackButton()

        binding.etBirthday.setOnClickListener{
            showCalendar()
        }
    }

    private fun save() {
        binding.btnSave.setOnClickListener {

            if (validateAndSetUserInput()) {
                val parentuserID = FirebaseAuth.getInstance().currentUser!!.uid
                println("current/parent user   " + parentuserID)

                //current user is changed to child after creating user
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //registers the user
                        val user = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "username" to username,
                            "birthday" to SimpleDateFormat("MM/dd/yyyy").parse(birthday),
                            "parentID" to parentuserID,
                            "userType" to "Child",
                            "lastLogin" to Timestamp.now())

                        println("current/child user   " + FirebaseAuth.getInstance().currentUser!!.uid.toString())

                        val childID = FirebaseAuth.getInstance().currentUser!!.uid

                        firestore.collection("Users").document(childID).set(user).addOnSuccessListener { childUser ->
                            clearForm()
                            createChildWallet(childID)
                            Toast.makeText(this, "Child register successful", Toast.LENGTH_SHORT).show()
                            //TODO: change current user back to parent
                            println("current/parent user   " + FirebaseAuth.getInstance().currentUser!!.uid.toString())
                            val parentLandingPage = Intent (this, ParentLandingPageActivity::class.java)
                            startActivity (parentLandingPage)
                        }
                    } else {
                        //not successfully registered
                        Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                        // catches other errors
                        try {
                            throw task.getException()!!
                        } catch (e: FirebaseAuthUserCollisionException) {
                            Log.d(
                                "ERRrrrrrrrrrr",
                                "FirebaseAuthUserCollisionException $e"
                            )
                            Toast.makeText(this, "Failed to register! Email is already in use by another account!",
                                Toast.LENGTH_SHORT).show()

                            // show error toast ot user ,user already exist
                        } catch (e: FirebaseNetworkException) {
                            Log.d("ERRrrrrrrrrrr", "FirebaseNetworkException " + e.toString())
                            //show error tost network exception
                        } catch (e: Exception) {
                            Log.d("ERRrrrrrrrrrr", "Exception $e")
                        }
                    }
                }
            }
            else
                Toast.makeText(this, "Failed to register! Try again!", Toast.LENGTH_SHORT).show()

        }
    }

    private fun createChildWallet(childID:String) {

        val time = Calendar.getInstance().time
        //val formatter = SimpleDateFormat("MM/dd/yyyy")
        //val current = formatter.format(time)

        var cashWallet = hashMapOf(
            "childID" to childID,
            "currentBalance" to 0,
            "type" to "Cash",
            "lastUpdated" to time
        )

        var mayaWallet = hashMapOf(
        "childID" to childID,
        "currentBalance" to 0,
        "type" to "Maya",
        "lastUpdated" to time
        )

        firestore.collection("ChildWallet").add(cashWallet)
        firestore.collection("ChildWallet").add(mayaWallet)

    }

    private fun validateAndSetUserInput(): Boolean {
        var valid = true
        var setPW= true
        var rePW= true

        if (binding.etFirstName.text.toString().trim().isEmpty()) {
            binding.etFirstName.error = "Please enter your first name."
            binding.etFirstName.requestFocus()
            valid = false
        } else firstName = binding.etFirstName.text.toString().capitalize().trim()

        if (binding.etLastName.text.toString().trim().isEmpty()) {
            binding.etLastName.error = "Please enter your last name."
            binding.etLastName.requestFocus()
            valid = false
        } else lastName = binding.etLastName.text.toString().capitalize().trim()

        if (binding.etEmail.text.toString().trim().isEmpty()) {
            binding.etEmail.error = "Please enter your email."
            binding.etEmail.requestFocus()
            valid = false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text).matches()) {
            binding.etEmail.error = "Please enter your email."
            binding.etEmail.requestFocus()
            valid = false
        } else email = binding.etEmail.text.toString().trim()

        if (binding.etContactNumber.text.toString().trim().isEmpty()) {
            binding.etContactNumber.error = "Please enter your contact number."
            binding.etContactNumber.requestFocus()
            valid = false
        } else contactNumber = binding.etContactNumber.text.toString().trim()

//        birthday =(binding.etBirthday.month + 1).toString() + "/" +
//                (binding.etBirthday.dayOfMonth).toString() + "/" + (binding.etBirthday.year).toString()

        if (binding.etBirthday.text.toString().trim().isEmpty()) {
            binding.dateContainer.helperText = "Select target date."
            valid = false
        } else {
            binding.dateContainer.helperText = ""
            birthday = binding.etBirthday.text.toString()
        }

        if (binding.etUsername.text.toString().trim().isEmpty()) {
            binding.etUsername.error = "Please enter your contact number."
            binding.etContactNumber.requestFocus()
            valid = false
        } else username = binding.etUsername.text.toString().trim()


        if (binding.etPassword.text.toString().trim().isEmpty()) {
            binding.etPassword.error = "Please enter your password."
            binding.etPassword.requestFocus()
            setPW = false
            valid = false
        }

        if (binding.etPassword.text?.length!! < 6) {
            binding.etPassword.error = "Minimum password length should be six (6) characters."
            binding.etPassword.requestFocus()
            setPW = false
            valid = false
        }

        if (binding.etConfirmPassword.text.toString().trim().isEmpty()) {
            binding.etConfirmPassword.error = "Please enter your password."
            binding.etConfirmPassword.requestFocus()
            rePW = false
            valid = false
        }

        if (binding.etConfirmPassword.text.toString() != binding.etConfirmPassword.text.toString() && setPW && rePW) {
            binding.etConfirmPassword.error = "Please enter the same password."
            binding.etConfirmPassword.error = "Please enter the same password."
            valid = false
        } else password = binding.etConfirmPassword.text.toString().trim()

        return valid
    }

    private fun clearForm() {
        binding.etFirstName.text?.clear()
        binding.etLastName.text?.clear()
        binding.etEmail.text?.clear()
        binding.etUsername.text?.clear()
        binding.etPassword.text?.clear()
        binding.etConfirmPassword.text?.clear()
    }

    private fun setCancel() {
        binding.btnCancel.setOnClickListener {
            val goToParentLandingPage = Intent(this, ParentLandingPageActivity::class.java)
            startActivity(goToParentLandingPage)
        }
    }

    private fun loadBackButton() {
        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources, ph.edu.dlsu.finwise.R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToParentLandingPage = Intent(applicationContext, ParentLandingPageActivity::class.java)
            startActivity(goToParentLandingPage)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window!!.setLayout(1000, 1200)

        var calendar = dialog.findViewById<DatePicker>(R.id.et_date)
        calendar.minDate = getMillisFromYearMonthDay(2003, 0, 1)
        calendar.maxDate = getMillisFromYearMonthDay(2017, 0, 1)



        calendar.setOnDateChangedListener { datePicker: DatePicker, mYear, mMonth, mDay ->
            binding.etBirthday.setText((mMonth + 1).toString() + "/" + mDay.toString() + "/" + mYear.toString())
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getMillisFromYearMonthDay(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        return calendar.timeInMillis
    }
}