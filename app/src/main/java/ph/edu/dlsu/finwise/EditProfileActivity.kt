package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityEditProfileBinding
import ph.edu.dlsu.finwise.databinding.ActivityProfileBinding
import ph.edu.dlsu.finwise.model.ChildUser

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var firestore = Firebase.firestore
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this

        getProfileData()

        binding.btnSave.setOnClickListener{
            updateInternProfile()
            var goToProfile = Intent(this, ProfileActivity::class.java)
            context.startActivity(goToProfile)
        }

        binding.btnCancel.setOnClickListener {
            var goToProfile = Intent(this, ProfileActivity::class.java)
            context.startActivity(goToProfile)
        }

    }

    fun getProfileData() {
        //val currentUser:String = FirebaseAuth.getInstance().currentUser!!.uid
        val currentUser = "JoCGIUSVMWTQ2IB7Rf41ropAv3S2"

        firestore.collection("ChildUser").document(currentUser).get().addOnSuccessListener { documentSnapshot ->
            var child = documentSnapshot.toObject<ChildUser>()
            if (child?.firstName != null)
                binding.etFirstName.setText(child?.firstName.toString())
            if (child?.lastName != null && child?.lastName != null)
                binding.etLastName.setText(child?.lastName.toString() + " " + child?.lastName.toString())
            if (child?.birthday != null && child?.birthday != null) {
                //TODO set dates
                //binding.etBirthday.set
                //setText(child?.lastName.toString() + " " + child?.lastName.toString())
            }
        }
    }

    private fun updateInternProfile() {
        //TODO update profile picture
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val birthday = binding.etBirthday.toString()
        //val currentUser:String = FirebaseAuth.getInstance().currentUser!!.uid
        val currentUser:String = "JoCGIUSVMWTQ2IB7Rf41ropAv3S2"

        val child = mapOf<String, String>(
            "firstName" to firstName,
            "lastName" to lastName,
            "birthday" to birthday
        )

        firestore.collection("ChildUser").document(currentUser).set(child, SetOptions.merge()).addOnSuccessListener {
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT)
            val intent = Intent (this, ProfileActivity::class.java)
            startActivity (intent)
            finish()
        }.addOnFailureListener{
            Toast.makeText(this, "Profile Update Failed", Toast.LENGTH_SHORT)
        }
    }
}