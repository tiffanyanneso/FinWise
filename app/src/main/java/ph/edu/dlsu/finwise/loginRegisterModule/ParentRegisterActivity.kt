package ph.edu.dlsu.finwise.loginRegisterModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityLoginBinding
import ph.edu.dlsu.finwise.databinding.ActivityParentRegisterBinding

class ParentRegisterActivity : AppCompatActivity() {

    private lateinit var binding :ActivityParentRegisterBinding
    private var firestore = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnNext.setOnClickListener {
            var firstName = binding.etFirstName.text.toString()
            var lastName = binding.etLastName.text.toString()
            var email = binding.etEmail.text.toString()
            var contactNumber = binding.etContactNumber.text.toString()
            var password = binding.etPassword.text.toString()
            var confirmPassword = binding.etConfirmPassword.text.toString()

            // TODO: VALIDATION
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //registers the user

                    var user = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "number" to contactNumber,
                    )

                    var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
                    firestore.collection("ParentUser").document(currentUser).set(user)
                        .addOnSuccessListener {
                            binding.etFirstName.text.clear()
                            binding.etLastName.text.clear()
                            binding.etEmail.text.clear()
                            binding.etContactNumber.text.clear()
                            binding.etPassword.text.clear()
                            binding.etConfirmPassword.text.clear()


                            val childRegister = Intent (this, ParentRegisterChildActivity::class.java)
                            var bundle = Bundle()
                            bundle.putString("parentUserID", currentUser)
                            childRegister.putExtras(bundle)
                            startActivity (childRegister)
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Failed to Register", Toast.LENGTH_SHORT).show()
                            //Log.w(TAG, "Error adding document $exception")
                        }
                } else {
                    //not successfully registered
                    Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            val goToLogin = Intent(this, LoginActivity::class.java)
            startActivity(goToLogin)
        }
    }
}