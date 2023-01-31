package ph.edu.dlsu.finwise.loginRegisterModule

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentRegisterChildBinding.inflate(layoutInflater)
        setContentView(binding.root)

        save()
        setCancel()
    }

    private fun save() {
        binding.btnSave.setOnClickListener {

            if (validateAndSetUserInput())
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //registers the user
                        val user = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "username" to username,
                            "birthday" to birthday
                        )

                        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
                        firestore.collection("ChildUser").document(currentUser).set(user)
                            .addOnSuccessListener {
                                clearForm()
                                createChildWallet(currentUser)

                                var bundle: Bundle = intent.extras!!
                                firestore.collection("ParentUser").document(bundle.getString("parentUserID").toString()).update("childUserID", currentUser)
                                //TODO: CHANGE REDIRECTION TO LOGIN
                                Toast.makeText(this, "Child register successful", Toast.LENGTH_SHORT).show()
                                /*val childRegister = Intent (this, ParentRegisterChildActivity::class.java)
                                var bundle = Bundle()
                                bundle.putString("parentUserID", currentUser)
                                childRegister.putExtras(bundle)
                                startActivity (childRegister)*/
                            }
                        /*.addOnFailureListener { exception ->
                            Toast.makeText(this, "Failed to Register", Toast.LENGTH_SHORT).show()
                            //Log.w(TAG, "Error adding document $exception")
                        }*/
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
            else
                Toast.makeText(this, "Failed to register! Try again!", Toast.LENGTH_SHORT).show()

        }
    }

    private fun createChildWallet(childUserID:String) {

        val time = Calendar.getInstance().time
        //val formatter = SimpleDateFormat("MM/dd/yyyy")
        //val current = formatter.format(time)

        var wallet = hashMapOf(
            "childID" to childUserID,
            "currentBalance" to 0,
            "lastUpdated" to time
        )

        firestore.collection("ChildWallet").add(wallet).addOnSuccessListener {

        }
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

        birthday =(binding.etBirthday.month + 1).toString() + "/" +
                (binding.etBirthday.dayOfMonth).toString() + "/" + (binding.etBirthday.year).toString()

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
            val goToParentRegiser = Intent(this, ParentRegisterActivity::class.java)
            startActivity(goToParentRegiser)
        }
    }
}