package ph.edu.dlsu.finwise.loginRegisterModule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private var firestore = Firebase.firestore

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            var email = binding.etUsername.text.toString()
            var password = binding.etPassword.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.txtForgotPassword.setOnClickListener {
            val goToForgetPassword = Intent(this, ResetPasswordActivity::class.java)
            startActivity(goToForgetPassword)
        }

        binding.txtRegister.setOnClickListener {
            val goToRegister = Intent(this, ParentRegisterActivity::class.java)
            startActivity(goToRegister)
        }
    }
}