package ph.edu.dlsu.finwise

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.finwise.databinding.ActivityParentEditProfileBinding
import ph.edu.dlsu.finwise.model.Users
import java.text.SimpleDateFormat

class ParentEditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentEditProfileBinding
    private var firestore = Firebase.firestore
    private var currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context= this

        getProfileData()

        NavbarParentFirst(findViewById(R.id.bottom_nav_parent), this, R.id.nav_parent_first_profile)

        binding.btnSave.setOnClickListener{
            updateProfile()
            var goToProfile = Intent(this, ParentProfileActivity::class.java)
            context.startActivity(goToProfile)
        }

        binding.btnCancel.setOnClickListener {
            var goToProfile = Intent(this, ParentProfileActivity::class.java)
            context.startActivity(goToProfile)
        }

        binding.topAppBar.navigationIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.baseline_arrow_back_24, null)
        binding.topAppBar.setNavigationOnClickListener {
            val goToProfile = Intent(applicationContext, ParentProfileActivity::class.java)
            this.startActivity(goToProfile)
        }
    }

    fun getProfileData() {
        firestore.collection("Users").document(currentUser).get().addOnSuccessListener { documentSnapshot ->
            var parent = documentSnapshot.toObject<Users>()
            if (parent?.firstName != null)
                binding.etFirstName.setText(parent?.firstName.toString())
            if (parent?.lastName != null)
                binding.etLastName.setText(parent?.lastName.toString())
            if (parent?.number!= null)
                binding.etContactNumber.setText(parent.number.toString())
        }
    }



    private fun updateProfile() {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val number = binding.etContactNumber.text.toString()

        firestore.collection("Users").document(currentUser).update("firstName", firstName, "lastName", lastName, "number", number).addOnSuccessListener {
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT)
            finish()
            val intent = Intent (this, ParentProfileActivity::class.java)
            startActivity (intent)
        }.addOnFailureListener{
            Toast.makeText(this, "Profile Update Failed", Toast.LENGTH_SHORT)
        }
    }
}