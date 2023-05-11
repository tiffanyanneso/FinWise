package ph.edu.dlsu.finwise.loginRegisterModule

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.parentFinancialActivitiesModule.ParentLandingPageActivity
import ph.edu.dlsu.finwise.databinding.ActivityLoginBinding
import ph.edu.dlsu.finwise.financialAssessmentModule.FinancialAssessmentActivity
import ph.edu.dlsu.finwise.financialAssessmentModuleFinlitExpert.FinancialAssessmentFinlitExpertActivity
import ph.edu.dlsu.finwise.model.Users
import ph.edu.dlsu.finwise.personalFinancialManagementModule.PersonalFinancialManagementActivity

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
                        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
                        val currentUser: String = FirebaseAuth.getInstance().currentUser!!.uid
                        Log.d("xxcxcxcxc", "login: "+currentUser)

                        firestore.collection("Users").document(currentUser).get()
                            .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                // User information already exists in the database
                                var isFirstLogin = true
                                firestore.collection("Users").document(currentUser)
                                    .update("lastLogin", Timestamp.now())
                                if (documentSnapshot.contains("lastLogin"))
                                    isFirstLogin = false
                                Log.d("zxcvzxcvvc", "login: "+isFirstLogin)
                                initializeRedirect(documentSnapshot, isFirstLogin)

                            }
                        }
                    } else noAccountFound(task)
                }
            } else {
                Toast.makeText(
                    baseContext, "Please fill up all the fields.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun noAccountFound(task: Task<AuthResult>) {
        Log.d("xzcxcxz", "noAccountFound: "+task.exception?.message)

        binding.etPassword.error = "Please enter your correct password."
        binding.etPassword.requestFocus()

        binding.etEmail.error = "Please enter your correct email address."
        binding.etEmail.requestFocus()
    }

    private fun initializeRedirect(documentSnapshot: DocumentSnapshot, isFirstLogin: Boolean) {
        val user = documentSnapshot.toObject<Users>()!!
        when (user.userType) {
            "Parent" -> {
                goToParentLandingPage()
            }
            "Child" -> {
                goToChild(isFirstLogin)
            }
            "Financial Expert" -> goToFinLitExpert()
        }
    }

    private fun goToChild(isFirstLogin: Boolean) {
        if (isFirstLogin) {
            val intent = Intent(this, FinancialAssessmentActivity::class.java)
            val bundle = Bundle()
            bundle.putString("assessmentType", "Preliminary")
            intent.putExtras(bundle)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, PersonalFinancialManagementActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun goToParentLandingPage() {
        val intent = Intent(this, ParentLandingPageActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToFinLitExpert() {
        val intent = Intent (this, FinancialAssessmentFinlitExpertActivity::class.java)
        startActivity(intent)
        finish()
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

