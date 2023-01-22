package ph.edu.dlsu.finwise.loginRegisterModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.R
import ph.edu.dlsu.finwise.databinding.ActivityParentRegisterBinding
import ph.edu.dlsu.finwise.databinding.ActivityParentRegisterChildBinding

class ParentRegisterChildActivity : AppCompatActivity() {

    private lateinit var binding : ActivityParentRegisterChildBinding
    private var firestore = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentRegisterChildBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            var firstName = binding.etFirstName.text.toString()
            var lastName = binding.etLastName.text.toString()
            var email = binding.etEmail.text.toString()
            var username = binding.etUsername.text.toString()
            var birthday =(binding.etBirthday.month + 1).toString() + "/" +
                (binding.etBirthday.dayOfMonth).toString() + "/" + (binding.etBirthday.year).toString()
            var password = binding.etPassword.text.toString()
            var confirmPassword = binding.etConfirmPassword.text.toString()

            // TODO: VALIDATION
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //registers the user

                    var user = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "username" to username,
                        "birthday" to birthday
                    )

                    var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
                    firestore.collection("ChildUser").document(currentUser).set(user)
                        .addOnSuccessListener {
                            binding.etFirstName.text.clear()
                            binding.etLastName.text.clear()
                            binding.etEmail.text.clear()
                            binding.etUsername.text.clear()
                            binding.etPassword.text.clear()
                            binding.etConfirmPassword.text.clear()

                            var bundle: Bundle = intent.extras!!
                            firestore.collection("ParentUser").document(bundle.getString("parentUserID").toString()).update("childUserID", currentUser)
                            //TODO: CHANGE REDIRECTION TO DASHBOARD
                            Toast.makeText(this, "Child retgister successfull", Toast.LENGTH_SHORT).show()

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


        binding.btnBack.setOnClickListener {
            val goToParentRegiser = Intent(this, ParentRegisterActivity::class.java)
            startActivity(goToParentRegiser)
        }
    }
}