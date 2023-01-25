package ph.edu.dlsu.finwise.loginRegisterModule

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    lateinit var email: String
    lateinit var password: String
    private var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        forgotPassWord()
        register()
        login()
    }

    private fun login() {
        binding.btnLogin.setOnClickListener {
            if (validateAndSetUserInput()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val currentUser: String = FirebaseAuth.getInstance().currentUser!!.uid
                            //Todo: Check if child or parent
                            /*firestore.collection("Companies").document(currentUser).get().addOnSuccessListener {
                            if (it.exists()) {
                                val intent = Intent(this, CompanyMenu::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val currentUser: String = FirebaseAuth.getInstance().currentUser!!.uid
                                firestore.collection("Interns").document(currentUser).get().addOnSuccessListener { documentSnapshot->
                                    if (documentSnapshot.exists()) {
                                        val intent = Intent(this, InternMenu::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else
                                        Toast.makeText(
                                            this,
                                            "User does not exist",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                }
                            }
                        }*/
                        }

                    }
            } else {
                Toast.makeText(
                    baseContext, "Wrong information given. Sign up if you don't have an account",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun validateAndSetUserInput(): Boolean {
        var valid = true
        // Check if edit text is empty and valid
        if (binding.etEmail.text.toString().trim().isEmpty()) {
            binding.etEmail.error = "Please enter your email address."
            binding.etEmail.requestFocus()
            valid = false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text).matches()) {
            binding.etEmail.error = "Please enter a valid email address."
            binding.etEmail.requestFocus()
            valid = false
        } else email = binding.etEmail.text.toString().trim()

        // Check if edit text is empty and valid
        if (binding.etPassword.text.toString().trim().isEmpty()) {
            binding.etPassword.error = "Please enter your correct password."
            binding.etPassword.requestFocus()
            valid = false
        } else password = binding.etPassword.text.toString().trim()

        return valid
    }


    private fun forgotPassWord() {
        binding.txtForgotPassword.setOnClickListener {
            val goToForgetPassword = Intent(this, ResetPasswordActivity::class.java)
            startActivity(goToForgetPassword)
        }
    }

    private fun register() {
        binding.txtRegister.setOnClickListener {
            val goToRegister = Intent(this, ParentRegisterActivity::class.java)
            startActivity(goToRegister)
        }
    }
}

